package builders;

import bodies.PlaneBody;
import bodies.RayBody;
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
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class PlaneByPointOrthRayBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointOrthRay";

  public PlaneByPointOrthRayBuilder() {
  }

  public PlaneByPointOrthRayBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointOrthRayBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointOrthRayBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_RAY, param.get(BLDKEY_RAY).asString());
  }

  public PlaneByPointOrthRayBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR);
    addParam(BLDKEY_RAY, "Ортогональный луч", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try{
      RayBody ray = (RayBody)bodies.get(getValueAsString(BLDKEY_RAY));
      if (!ray.exists())
        throw new ExLostBody("Луч");
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      Plane3d plane = Plane3d.planeByPointOrthLine(point, ray.ray().line());
      i_Body result= new PlaneBody(_id, title(), plane);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      return result;
    }catch (ExNoAnchor|ExNoBody|ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
     result.add(BLDKEY_RAY, getValueAsString(BLDKEY_RAY));
     return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s и перпендиклярная "
              + "лучу %s .",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RAY)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}

