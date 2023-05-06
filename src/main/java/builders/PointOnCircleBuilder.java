package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Point on circle builder.
 * @author alexeev
 */
public class PointOnCircleBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnCircle";
  private Circle3d _circle;

  public PointOnCircleBuilder() {
  }

  public PointOnCircleBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnCircleBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_DISK, param.get(BLDKEY_DISK).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public PointOnCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DISK, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.DOUBLE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      double phi = (double)getValue(BLDKEY_ANGLE);
      _circle = anchors.get(getValueAsString(BLDKEY_DISK)).getDisk();
      final Vect3d point = _circle.getPointByPolar(phi, _circle.radiusLength());

      PointBody result = new PointBody(_id, point);
      edt.addAnchor(result.point(), result, "P", title());

      /**
       * Определяем поведение при перемещении точки.
       */
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createCircleBehavior(_circle));
      _exists = true;
      return result;
    } catch(ExNoAnchor ex){
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_DISK, getValueAsString(BLDKEY_DISK));
    result.add(BLDKEY_ANGLE, (double)getValue(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong>, на окружности %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_DISK)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка на окружности</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double angle = _circle.getPolarAngleByPoint(newPosition);
      setValue(BLDKEY_ANGLE, angle);
    } catch (ExDegeneration ex) { }
  }
}
