package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_POLYGON;
import builders.CircleOutPolygonBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Body;
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
public class CreateCircumscribeCircleMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _polyID;

  public CreateCircumscribeCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите многоугольник{MOUSELEFT}");
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
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCUMSCRIBE_CIRCLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.OUT_CIRCLE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.OUT_CIRCLE.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Описать окружность</strong><br>около многоугольника";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.getPolygonTypes(), 1);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.getPolygonTypes());
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
        if (!bd.type().isPolygon()) {
          return;
        }
        _polyID = bd.getAnchorID("facet");
        createWithNameValidation();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();

    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Многоугольник", BuilderParamType.ANCHOR, _polyID));
    CircleOutPolygonBuilder builder = new CircleOutPolygonBuilder(params);

    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
