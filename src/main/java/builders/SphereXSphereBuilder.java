package builders;

import bodies.CircleBody;
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
 * Builder of two sphere intersection.
 *
 * @author alexeev
 */
public class SphereXSphereBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereXSphere";
  
  public static final String BLDKEY_SPHERE1 = "s1";
  public static final String BLDKEY_SPHERE2 = "s2";

  public SphereXSphereBuilder() {
  }
  
  public SphereXSphereBuilder(String name) {
    super(name);
  }
  
  public SphereXSphereBuilder(String id, String name) {
    super(id, name);
  }

  public SphereXSphereBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereXSphereBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_SPHERE1, param.get(BLDKEY_SPHERE1).asString());
    setValue(BLDKEY_SPHERE2, param.get(BLDKEY_SPHERE2).asString());
  }

  public SphereXSphereBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_SPHERE1, "Первая сфера", BuilderParamType.BODY, 61);
    addParam(BLDKEY_SPHERE2, "Вторая сфера", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      SphereBody s1 = (SphereBody) bodies.get(getValueAsString(BLDKEY_SPHERE1));
      SphereBody s2 = (SphereBody) bodies.get(getValueAsString(BLDKEY_SPHERE2));
      if (!s1.exists() || !s2.exists()) {
        throw new ExLostBody("сфера");
      }

      Circle3d intersect = s1.sphere().intersectionWithSphere(s2.sphere());
      CircleBody result = new CircleBody(_id, title(), intersect);
      edt.addAnchor(intersect.center(), result, "C");
      edt.addAnchor(intersect, result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch (ExNoBody | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение сфер не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_SPHERE1, getValueAsString(BLDKEY_SPHERE1));
    result.add(BLDKEY_SPHERE2, getValueAsString(BLDKEY_SPHERE2));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("Пересечение сфер %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE1)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
