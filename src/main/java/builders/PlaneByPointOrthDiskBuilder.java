package builders;

import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author Elena
 */
public class PlaneByPointOrthDiskBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointOrthDisk";

  public PlaneByPointOrthDiskBuilder() {
  }

  public PlaneByPointOrthDiskBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointOrthDiskBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointOrthDiskBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PlaneByPointOrthDiskBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CIRCLE, "Ортогональный круг", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_A, "Точка плоскости", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
     double alpha = (double)getValue(BLDKEY_ALPHA);
    try{
      i_Anchor circle = anchors.get(getValueAsString(BLDKEY_CIRCLE));
      Plane3d pl = circle.getDisk().plane();
      Plane3d plane = Plane3d.planeByPointOrthPlane(anchors.getVect(getValueAsString(BLDKEY_A)), pl, alpha);
      i_Body result= new PlaneBody(_id, title(), plane);
      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      return result;
    }catch (ExNoAnchor| ExDegeneration ex) {
      util.Fatal.warning("Cannot create plane: ".concat(ex.getMessage()));
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s и перпендикулярная "
              + "плоскости окружности %s. Угол поворота %s\u00B0",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CIRCLE)),
        Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
