package builders;

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
import geom.ExDegeneration;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Circumscribes sphere around tetrahedron
 *
 * @author alexeev
 */
public class SphereOutConeBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOutCone";

  public SphereOutConeBuilder() {
  }

  public SphereOutConeBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOutConeBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOutConeBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CONE, param.get(BLDKEY_CONE).asString());
  }

  public SphereOutConeBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CONE, "Конус", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      ConeBody cn = (ConeBody) bodies.get(getValueAsString(BLDKEY_CONE));
      if (!cn.exists()) {
        throw new ExLostBody("конус");
      }

      SphereBody result = new SphereBody(_id, title(), cn.outSphere());
      edt.addAnchor(result.sphere().center(), result, "center");
      _exists = true;
      return result;
    } catch (ExNoBody | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Сфера не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new SphereBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CONE, getValueAsString(BLDKEY_CONE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>описанная около конуса %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CONE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
