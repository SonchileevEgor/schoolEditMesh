package builders;

import bodies.PlaneBody;
import bodies.RayBody;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author Kurgansky
 */
public class PlaneParalRayByPointBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneParalRayByPoint";

  public PlaneParalRayByPointBuilder() {
  }

  public PlaneParalRayByPointBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneParalRayByPointBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneParalRayByPointBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
    setValue(BLDKEY_RAY, param.get(BLDKEY_RAY).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public PlaneParalRayByPointBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_RAY, BuilderParam.RAY_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
     double alpha = (double)getValue(BLDKEY_ANGLE);
    try {
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));

      RayBody line = (RayBody)bodies.get(getValueAsString(BLDKEY_RAY));
      if (!line.exists())
        throw new ExLostBody("Луч");

      Plane3d plane = Plane3d.planeByPointParalLine(point, line.ray().line(), alpha);
      i_Body result= new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    }catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_RAY, getValueAsString(BLDKEY_RAY));
    result.add(BLDKEY_ANGLE, (double)getValue(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s<br>"
              + " параллельно лучу %s под углом %s\u00B0<br>"
              + "к вектору из точки, перпендикулярному к лучу",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_RAY)),
              Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ANGLE)), precision));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
