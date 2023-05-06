package builders;

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
import geom.ExDegeneration;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a cylinder insphere
 *
 * @author Preobrazhenskaia
 */
public class SphereInCylinderBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereInCylinder";

  public SphereInCylinderBuilder() {
  }

  public SphereInCylinderBuilder(String id, String name) {
    super(id, name);
  }

  public SphereInCylinderBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereInCylinderBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CYLINDER, param.get(BLDKEY_CYLINDER).asString());
  }

  public SphereInCylinderBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CYLINDER, "Цилиндр", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      CylinderBody cyl = (CylinderBody) bodies.get(getValueAsString(BLDKEY_CYLINDER));
      if (!cyl.exists()) {
        throw new ExLostBody("цилиндр");
      }

      SphereBody result = new SphereBody(_id, title(), cyl.inSphere());
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
    result.add(BLDKEY_CYLINDER, getValueAsString(BLDKEY_CYLINDER));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>вписанная в цилиндр %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CYLINDER)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
