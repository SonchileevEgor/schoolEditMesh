package builders;

import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Plane builder.
 * Constructs plane by point, orthogonal plane and angle of rotation
 * @author Ivan
 */

 public class PlaneByPointOrthPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointOrthPlane";
  public static final String BLDKEY_P1 = "p1";

  public PlaneByPointOrthPlaneBuilder() {
  }

  public PlaneByPointOrthPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointOrthPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointOrthPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P1, param.get(BLDKEY_P1).asString());
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PlaneByPointOrthPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P1, "Ортогональная плоскость", BuilderParamType.BODY);
    addParam(BLDKEY_A, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    double alpha = (double)getValue(BLDKEY_ALPHA);
    try {
      i_Anchor a = anchors.get(getValueAsString(BLDKEY_A));
      PlaneBody p2 = (PlaneBody)bodies.get(getValueAsString(BLDKEY_P1));
      if (!p2.exists())
        throw new ExLostBody("Плоскость");
      Plane3d p3 = p2.plane();
      Plane3d plane = Plane3d.planeByPointOrthPlane(a.getPoint(), p3, alpha);
      i_Body result = new PlaneBody(_id, title(), plane);
      result.addAnchor("A", a.id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P1, getValueAsString(BLDKEY_P1));
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s "
              + "и перпендикулярная плоскости %s. Угол поворота %s\u00B0",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_P1)),
        Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
