package builders;

import bodies.CircleBody;
import bodies.PlaneBody;
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
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Projection of a sphere onto a plane.
 *
 * @author alexeev
 */
public class SphereOnPlaneProjectionBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOnPlaneProjection";

  public SphereOnPlaneProjectionBuilder() {
  }
  
  public SphereOnPlaneProjectionBuilder(String name) {
    super(name);
  }

  public SphereOnPlaneProjectionBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOnPlaneProjectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOnPlaneProjectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public SphereOnPlaneProjectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_SPHERE, "Проецируемая сфера", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      i_Body body = bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!body.exists()) {
        throw new ExLostBody("плоскость");
      }
      PlaneBody p = (PlaneBody) body;

      body = bodies.get(getValueAsString(BLDKEY_SPHERE));
      if (!body.exists()) {
        throw new ExLostBody("сфера");
      }

      SphereBody sphere = (SphereBody) body;
      Circle3d proj = sphere.sphere().projectOnPlane(p.plane());
      CircleBody result = new CircleBody(_id, title(), proj);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(proj, result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch (ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Cannot create projection: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Проекция сферы</strong> %s на плоскость %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "Проекция сферы на плоскость";
    }
  }
}
