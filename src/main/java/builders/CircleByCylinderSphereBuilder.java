package builders;

import bodies.CircleBody;
import bodies.CylinderBody;
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
 * Constructs circle by tangent inscribed sphere of the cylinder.
 * @author VitaliiZah
 */
public class CircleByCylinderSphereBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleByCylinderSphere";

  public CircleByCylinderSphereBuilder() {
  }

  public CircleByCylinderSphereBuilder(String id, String name) {
    super(id, name);
  }

  public CircleByCylinderSphereBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByCylinderSphereBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_CYLINDER, param.get(BLDKEY_CYLINDER).asString());
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
  }

  public CircleByCylinderSphereBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CYLINDER, "Цилиндр", BuilderParamType.BODY, 61);
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
        throw new ExLostBody("Сфера");

      CylinderBody cylinder = (CylinderBody)bodies.get(getValueAsString(BLDKEY_CYLINDER));
      if (!cylinder.exists())
        throw new ExLostBody("Цилиндр");

      Circle3d disk = cylinder.cylinder().circleBySphereInCylinder(s.sphere());
      CircleBody result = new CircleBody(_id, title(), disk);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(disk, result.getAnchorID("C"), result, "disk");
      return result;
    } catch(ExNoBody | ExGeom ex){
      if (_exists) {
        eh.showMessage("Окружность не построена." + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CYLINDER, getValueAsString(BLDKEY_CYLINDER));
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность касания</strong> сферы %s и цилиндра %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_CYLINDER)));
    } catch (ExNoBody ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
