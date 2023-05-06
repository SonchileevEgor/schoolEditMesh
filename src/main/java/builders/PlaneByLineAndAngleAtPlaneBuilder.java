package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.Checker;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Plane through the given line at an angle to the given plane
 * @author Elena
 */
public class PlaneByLineAndAngleAtPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineAngleAtPlane";

  public PlaneByLineAndAngleAtPlaneBuilder() {
  }

  public PlaneByLineAndAngleAtPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByLineAndAngleAtPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByLineAndAngleAtPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PlaneByLineAndAngleAtPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, 60);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 61);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    double alpha = getValueAsDouble(BLDKEY_ALPHA);
    try {
      PlaneBody pl = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!pl.exists())
        throw new ExLostBody("Плоскость");
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("Прямая");
      if (!Checker.isLineLiesInPlane(line.line(), pl.plane()))
        throw new ExDegeneration("Прямая не лежит в плоскости!");
      Plane3d plane = Plane3d.planeByLineAndAngleBetweenPlanes(line.line(), pl.plane(), alpha);
      i_Body result = new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    } catch (ExNoBody | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(), error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    result.add(BLDKEY_ALPHA, getValueAsDouble(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>,<br>проходящая через прямую %s"
              + " под углом %s\u00B0 к плоскости %s ",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
        Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
