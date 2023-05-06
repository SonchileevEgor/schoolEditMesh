package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_BASE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_TOP;
import static builders.BodyBuilder.BLDKEY_VERTEX;
import builders.PrismByBaseAndTopBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Prism building scenario. User chooses base polygon, vertex of base, and the correspondent vertex
 * of parallel base.
 *
 * @author alexeev
 */
public class CreatePrismByBaseAndTopMode extends CreateBodyMode implements i_FocusChangeListener {

  private Plane3d _plane;
  private ArrayList<String> _baseAnchorID = new ArrayList<>();

  public CreatePrismByBaseAndTopMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.POLYGON);
    setCursor(Cursor.getDefaultCursor());
    _baseAnchorID.clear();
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PRISM_BY_BASE_AND_TOP.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PRISM_BY_BASE_AND_TOP.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POLY, 1)
            && _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Призма</strong><br>по основанию и вершине";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PRISM_BY_BASE_AND_TOP;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_BASE, new BuilderParam(BLDKEY_BASE, "Основание", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_VERTEX, new BuilderParam(BLDKEY_VERTEX, "Вершина 1-го основания", BuilderParamType.ANCHOR, id(1)));
    params.put(BLDKEY_TOP, new BuilderParam(BLDKEY_TOP, "Вершина 2-го основания", BuilderParamType.ANCHOR, id(2)));
    PrismByBaseAndTopBuilder builder = new PrismByBaseAndTopBuilder(params);
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
      return; // handle only anchors
    }
    if (_numOfChosenAnchors == 0) {
      // choose base
      i_Anchor a = getPolygonAnchor(id);
      if (a == null) {
        return;
      }
      try {
        _plane = a.getPoly().plane();
        _anchorID.add(a.id());
        _baseAnchorID = a.arrayIDs();
        _numOfChosenAnchors++;
        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
        _ctrl.status().showMessage(_msg.current());
      } catch (ExDegeneration ex) {
      }
    } else {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      if (_numOfChosenAnchors == 1) {
        // choose vertex on lower base
        if (!_baseAnchorID.contains(a.id())) {
          checker.showMessage("Выберите вершину основания", error.Error.WARNING);
          return;
        }
        _anchorID.add(a.id());
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
      } else { // choose vertex on top base
        if (!checker.checkPointsDifferent(_anchorID, a.id())
                || !checker.checkPlaneNotContainsPoint(_plane, a.getPoint())) {
          return;
        }
        _anchorID.add(a.id());
        createWithNameValidation();
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте основание призмы{MOUSELEFT}",
            "Отметьте вершину на основании{MOUSELEFT}",
            "Отметьте вершину параллельного основания{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PRISM.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
