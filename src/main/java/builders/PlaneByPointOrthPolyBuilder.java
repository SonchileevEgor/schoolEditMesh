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
public class PlaneByPointOrthPolyBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointOrthPoly";

  public PlaneByPointOrthPolyBuilder() {
  }

  public PlaneByPointOrthPolyBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointOrthPolyBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointOrthPolyBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PlaneByPointOrthPolyBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POLYGON, "Ортогональный многоугольник", BuilderParamType.ANCHOR, 101);
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
      i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));
      Plane3d pl = poly.getPoly().plane();
      Plane3d plane = Plane3d.planeByPointOrthPlane(anchors.getVect(getValueAsString(BLDKEY_A)), pl, alpha);
      i_Body result= new PlaneBody(_id, title(), plane);
      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      _exists = true;
      return result;
    }catch (ExNoAnchor| ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s и перпендикулярная "
              + "плоскости многоугольника %s. Угол поворота %s\u00B0",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)),
        Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Многоугольник</strong>";
    }
  }
}
