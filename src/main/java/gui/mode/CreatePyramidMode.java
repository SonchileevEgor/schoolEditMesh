package gui.mode;

import bodies.BodyType;
import builders.PyramidBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.ArrayList;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Pyramid building scenario. User chooses base points and top vertex.
 *
 * @author alexeev
 */
public class CreatePyramidMode extends CreateBodyMode implements i_FocusChangeListener {

  private boolean _baseChosen; // флаг показывает, отмечено ли основание
  private String _firstPointID;
  private ArrayList<Vect3d> _anchors = new ArrayList<>();
  private ArrayList<String> _anchorID = new ArrayList<>();
  private Plane3d _plane; // плоскость основания

  public CreatePyramidMode(EdtController ctrl) {
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
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 4);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PYRAMID.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PYRAMID.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Пирамида</strong><br>по точкам основания и вершине";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PYRAMID;
  }

  @Override
  protected void create() {
    PyramidBuilder builder = new PyramidBuilder(_name);
    builder.setValue(PyramidBuilder.BLDKEY_POINTS, new ArrayList<>(_anchorID));

    _ctrl.add(builder, checker, false);
    reset();
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
          _ctrl.status().showMessage("Выберите вершину пирамиды");
          _baseChosen = true;
          _ctrl.redraw();
        } else {
          checker.showMessage("В основании должны быть хотя бы три вершины!", error.Error.WARNING);
        }
      } else {
        _anchorID.add(a.id());
        _anchors.add(a.getPoint());

        if (!checker.checkPointsNotCollinear(_anchors)
                || !checker.checkPointsNotCoplanar(_anchors)
                || !checker.checkPolygonWithoutSelfIntersections(_anchors)
                || !checker.checkPolygonConvex(_anchors)) {
          // check for self-intersections, collinearity, and complanarity
          _anchors.remove(_anchors.size() - 1);
          _anchorID.remove(_anchorID.size() - 1);
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
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
  }

  @Override
  protected void setName() {
    _name = BodyType.PYRAMID.getName(_ctrl.getEditor());
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
