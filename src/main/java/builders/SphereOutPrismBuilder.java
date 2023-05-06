package builders;

import bodies.PrismBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.ExZeroDivision;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class SphereOutPrismBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOutPrism";

  public SphereOutPrismBuilder() {
  }

  public SphereOutPrismBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOutPrismBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOutPrismBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PRISM, param.get(BLDKEY_PRISM).asString());
  }

  public SphereOutPrismBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PRISM, BuilderParam.PRISM_ALIAS, BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      PrismBody pr = (PrismBody) bodies.get(getValueAsString(BLDKEY_PRISM));
      if (!pr.exists()) {
        throw new ExLostBody("призма");
      }

      SphereBody result = new SphereBody(_id, title(), pr.outSphere());
      edt.addAnchor(result.sphere().center(), result, "center");
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef | ExZeroDivision ex) {
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
    result.add(BLDKEY_PRISM, getValueAsString(BLDKEY_PRISM));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>описанная около призмы %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PRISM)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
