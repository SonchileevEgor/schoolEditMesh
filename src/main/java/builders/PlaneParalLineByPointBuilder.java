package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
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
 * @author Elena
 */
public class PlaneParalLineByPointBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneParalLineByPoint";
  
  public PlaneParalLineByPointBuilder() {
  }

  public PlaneParalLineByPointBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneParalLineByPointBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneParalLineByPointBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public PlaneParalLineByPointBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    double alpha = getValueAsDouble(BLDKEY_ANGLE);
    try {
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));

      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("Прямая");

      Plane3d plane = Plane3d.planeByPointParalLine(point, line.line(), alpha);
      i_Body result= new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    } catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
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
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s<br>"
              + "параллельно прямой %s под углом %s\u00B0<br>"
              + " к вектору из точки, перпендикулярному к прямой",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
              Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ANGLE)), precision));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
