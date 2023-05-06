package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_CENTER;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_POINT;
import builders.CircleByPlaneBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreateCircleByPlaneMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _planeID;

  public CreateCircleByPlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.PLANE);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
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
    if (_numOfChosenAnchors == 0) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.PLANE) {
          return;
        }
        _planeID = id;
        _numOfChosenAnchors++;
        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else {
      i_Anchor a = getPointAnchor(id);
      if (_numOfChosenAnchors == 2
              && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      chooseAnchor(a);
      if (_numOfChosenAnchors == 3) {
        createWithNameValidation();
      }
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_BY_PLANE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_BY_PLANE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.PLANE, 1) && _ctrl.containsBodies(BodyType.POINT, 2));
  }

  @Override
  public String description() {
    return "<html><strong>Окружность,</strong><br>построенная по плоскости, центру и точке";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CIRCLE_BY_PLANE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Плоскость", BuilderParamType.BODY, _planeID));
    params.put(BLDKEY_CENTER, new BuilderParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_POINT, new BuilderParam(BLDKEY_POINT, "Точка на окружности", BuilderParamType.ANCHOR, id(1)));
    i_BodyBuilder builder = new CircleByPlaneBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите плоскость окружности{MOUSELEFT}",
            "Выберите центр{MOUSELEFT}", "Выберите точку на окружности{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
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
