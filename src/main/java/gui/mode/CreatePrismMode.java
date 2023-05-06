package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_POINTS;
import builders.PrismBuilder;
import static config.Config.LOG_LEVEL;
import editor.AnchorType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
import util.Log;
import util.Util;

/**
 * Prism building scenario. User chooses lower base vertices, and top vertex, correspondent to first
 * vertex of the lower base.
 *
 * @author alexeev
 */
public class CreatePrismMode extends CreateBodyMode implements i_FocusChangeListener {

  private boolean _baseChosen; // флаг показывает, отмечено ли основание
  private String _firstPointID;
  private ArrayList<Vect3d> _anchors = new ArrayList<>();
  private ArrayList<String> _anchorID = new ArrayList<>();
  private Plane3d _plane; // плоскость основания

  public CreatePrismMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());
    _anchors.clear();
    _anchorID.clear();
    _firstPointID = null;
    _baseChosen = false;
    _ctrl.status().showMessage("Отметьте вершины основания{MOUSELEFT}");
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PRISM.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PRISM.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 4);
  }

  @Override
  public String description() {
    return "<html><strong>Призма</strong><br>по точкам основания и вершине другого основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PRISM;
  }

  @Override
  protected void create() {
    int anchorsNum = _anchor.size() - 1;
    String[] defaultPointTitles = new String[anchorsNum * 2];
    for (int i = 0; i < anchorsNum + 1; i++) {
      defaultPointTitles[i] = _anchor.get(i).getTitle();
    }
    for (int i = 0; i < anchorsNum - 1; i++) {
      defaultPointTitles[i + anchorsNum + 1] = BodyType.POINT.getName(_ctrl.getEditor(), i + 1);
    }
    String[] orderedPointTitles = new String[anchorsNum * 2];
    for (int i = 0; i < anchorsNum; i++) {
      orderedPointTitles[i] = _anchor.get(i).getTitle();
      orderedPointTitles[i + anchorsNum] = orderedPointTitles[i] + "_1";
    }
    boolean[] fixTitles = new boolean[anchorsNum * 2];
    for (int i = 0; i < anchorsNum; i++) {
      fixTitles[i] = true;
      fixTitles[i + anchorsNum] = false;
    }
    fixTitles[anchorsNum] = true;

    PrismBuilder builder = new PrismBuilder(_name);
    builder.setValue(BLDKEY_POINTS, new ArrayList<>(_anchorID));
    _ctrl.add(builder, checker, false);

    // Show dialog for vertices renaming.
    // Show dialog with three options:
    // Choose default alphabetical naming, like ABCDEFGHIJ;
    // Choose default indexed naming, like ABCDEA_1B_1C_1D_1E_1;
    // Choose point names by user.
    ChoosePointNamesDialog cpnd = new ChoosePointNamesDialog(_ctrl,
            new String[]{ Util.concat(defaultPointTitles),
              Util.concat(orderedPointTitles) }, defaultPointTitles, fixTitles);
    cpnd.setVisible(true);

    if (cpnd.accepted) {
      if (cpnd.getChoice() != 0) {
        String[] values;
        switch (cpnd.getChoice()) {
          case 1:
            values = orderedPointTitles; // use ordered naming
            break;
          default:
            values = cpnd.getValues(); // use user-defined naming
        }
        try {
          i_Body bd = _ctrl.getBody(builder.id());
          for (int i = 0; i < anchorsNum; i++) {
            _ctrl.renameAnchorPoint(bd.getAnchorID(String.valueOf(i + anchorsNum)),
                    values[i + anchorsNum]);
          }
          _ctrl.setUndo("Переименование точек призмы");
        } catch (ExNoBody ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.printf("Cannot rename prism points: %s", ex.getMessage());
          }
        }
        _ctrl.notifyEditorStateChange();
      }
    }

    exit();
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    i_Anchor a = getPointAnchor(id);
    if (a == null) {
      return;
    }
    if (_firstPointID == null) {
      _anchorID.add(a.id());
      _anchors.add(a.getPoint());
      _anchor.add(a);
      _firstPointID = a.id();
    } else if (!_baseChosen) {
      if (!checker.checkBasePointsDifferent(_anchorID, a.id())) {
        return;
      }
      if (a.id().equals(_firstPointID)) {
        // Base closed.
        if (_anchors.size() >= 3) {
          try {
            _plane = new Plane3d(_anchors.get(0), _anchors.get(1), _anchors.get(2));
          } catch (ExDegeneration ex) {
            checker.showMessage("Ошибка в построении!", error.Error.WARNING);
            reset();
            return;
          }
          _ctrl.status().showMessage("Выберите вершину параллельного основания");
          _baseChosen = true;
          _ctrl.redraw();
        } else {
          checker.showMessage("В основании должны быть хотя бы три вершины!", error.Error.WARNING);
        }
      } else {
        _anchorID.add(a.id());
        _anchors.add(a.getPoint());
        _anchor.add(a);

        if (!checker.checkPointsNotCollinear(_anchors)
                || !checker.checkPointsNotCoplanar(_anchors)
                || !checker.checkPolygonWithoutSelfIntersections(_anchors)
                || !checker.checkPolygonConvex(_anchors)) {
          // check for self-intersections, collinearity, and complanarity
          _anchors.remove(_anchors.size() - 1);
          _anchorID.remove(_anchorID.size() - 1);
          _anchor.remove(_anchor.size() - 1);
          return;
        }
        _ctrl.redraw();
      }
    } else {
      if (!checker.checkPlaneNotContainsPoint(_plane, a.getPoint())) {
        return;
      }
      // Выбираем вершину пирамиды
      _anchors.add(a.getPoint());
      _anchorID.add(a.id());
      _anchor.add(a);
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
  }

  @Override
  protected void setName() {
    _name = BodyType.PRISM.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchors.size(); i++) {
      Drawer.drawPoint(ren, _anchors.get(i));
    }
    for (int i = 0; i < _anchors.size() - 1; i++) {
      Drawer.drawSegment(ren, _anchors.get(i), _anchors.get(i + 1));
    }
    if (_baseChosen) {
      Drawer.drawSegment(ren, _anchors.get(0), _anchors.get(_anchors.size() - 1));
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
