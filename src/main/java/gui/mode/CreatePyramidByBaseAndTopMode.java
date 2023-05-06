package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_BASE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_TOP;
import builders.PyramidByBaseAndTopBuilder;
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
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Pyramid building scenario. User chooses base polygon and top vertex.
 *
 * @author alexeev
 */
public class CreatePyramidByBaseAndTopMode extends CreateBodyMode implements i_FocusChangeListener {

  private Plane3d _plane; // плоскость основания

  public CreatePyramidByBaseAndTopMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.POLYGON);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PYRAMID_BY_BASE_AND_TOP.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PYRAMID_BY_BASE_AND_TOP.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POLY, 1)
            && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Пирамида</strong><br>по основанию и вершине";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PYRAMID_BY_BASE_AND_TOP;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_BASE, new BuilderParam(BLDKEY_BASE, "Основание", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_TOP, new BuilderParam(BLDKEY_TOP, "Вершина", BuilderParamType.ANCHOR, id(1)));
    PyramidByBaseAndTopBuilder builder = new PyramidByBaseAndTopBuilder(params);
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
      i_Anchor a = getPolygonAnchor(id);
      if (a == null) {
        return;
      }
      try {
        _plane = a.getPoly().plane();
        _anchorID.add(a.id());
        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
        _ctrl.status().showMessage(_msg.current());
        _numOfChosenAnchors++;
      } catch (ExDegeneration ex) {
      }
    } else {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      if (!checker.checkPlaneNotContainsPoint(_plane, a.getPoint())) {
        return;
      }
      _anchorID.add(a.id());
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите основание пирамиды{MOUSELEFT}",
            "Выберите вершину пирамиды{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PYRAMID.getName(_ctrl.getEditor());
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
