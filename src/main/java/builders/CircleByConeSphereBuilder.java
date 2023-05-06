package builders;

import bodies.CircleBody;
import bodies.ConeBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs circle by tangent inscribed sphere of the cone.
 * @author VitaliiZah
 */
public class CircleByConeSphereBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleByConeSphere";
  
  public CircleByConeSphereBuilder() {
  }

  public CircleByConeSphereBuilder(String id, String name) {
    super(id, name);
  }

  public CircleByConeSphereBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByConeSphereBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_CONE, param.get(BLDKEY_CONE).asString());
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
  }

  public CircleByConeSphereBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CONE, "Конус", BuilderParamType.BODY, 61);
    addParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      SphereBody s = (SphereBody)bodies.get(getValueAsString(BLDKEY_SPHERE));
      if (!s.exists())
        throw new ExLostBody("сфера");

      ConeBody cone = (ConeBody)bodies.get(getValueAsString(BLDKEY_CONE));
      if (!cone.exists())
        throw new ExLostBody("конус");

      Circle3d disk = cone.cone().circleBySphereInCone(s.sphere());
      CircleBody result = new CircleBody(_id, title(), disk);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(disk, result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch(ExNoBody | ExGeom ex){
      if (_exists)
        eh.showMessage("Окружность не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
        return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CONE, getValueAsString(BLDKEY_CONE));
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность касания</strong> сферы %s и конуса %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_CONE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
