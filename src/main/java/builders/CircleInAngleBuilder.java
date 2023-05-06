package builders;

import bodies.CircleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.Checker;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Окружность, вписанная в угол.
 * В качестве параметра, определяющего положение центра на биссектрисе выступает
 * коэффициент в линейной комбинации вершины угла и проекции точки на первой стороне на биссектрису.
 * @author elena
 */
public class CircleInAngleBuilder extends BodyBuilder {
  public static final String ALIAS = "CircleInAngle";

  public CircleInAngleBuilder() {
  }

  public CircleInAngleBuilder(String id, String name) {
    super(id, name);
  }

  public CircleInAngleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleInAngleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public CircleInAngleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      i_Body angle = bodies.get(getValueAsString(BLDKEY_ANGLE));
      if (!angle.exists())
        throw new ExLostBody(angle.alias());
      Angle3d ang = (Angle3d)angle.getGeom();
      if (!ang.isLessThanPI())
        throw new ExDegeneration("Угол больше или равен 180 градусам");
      if (ang.value() < Checker.eps())
        throw new ExDegeneration("Угол равен нулю");
      Line3d bisectrix = ang.bisectrix();
      Vect3d point = Vect3d.conv_hull(ang.vertex(),
              bisectrix.projectOfPoint(ang.pointOnFirstSide()), getValueAsDouble(BLDKEY_ALPHA));
      Line3d side = Line3d.getLineByTwoPoints(ang.pointOnFirstSide(), ang.vertex());
      double radius = side.distFromPoint(point);
      Circle3d circ = new Circle3d(radius, ang.normal(), point);
      CircleBody result = new CircleBody(_id, title(), circ);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(circ, result.getAnchorID("C"), result, "disk");
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExBadRef | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_ANGLE, getValueAsString(BLDKEY_ANGLE));
    result.add(BLDKEY_ALPHA, getValueAsDouble(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность, вписанная</strong> в угол %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_ANGLE)));
    } catch (ExNoBody ex) {
           return "<html><strong>Окружность</strong>";
      }
  }
}
