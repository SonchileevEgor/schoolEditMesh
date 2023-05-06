package builders;

import bodies.EllipsoidBody;
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
 *
 * @author Vitaliy
 */
public class SphereInEllipsoidBuilder extends BodyBuilder {

 static public final String ALIAS = "SphereInEllipsoid";

  public SphereInEllipsoidBuilder() {
  }

  public SphereInEllipsoidBuilder(String id, String name) {
    super(id, name);
  }

  public SphereInEllipsoidBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereInEllipsoidBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_ELLIPSOID, param.get(BLDKEY_ELLIPSOID).asString());
  }

  public SphereInEllipsoidBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ELLIPSOID, "эллипсоид", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      EllipsoidBody el = (EllipsoidBody) bodies.get(getValueAsString(BLDKEY_ELLIPSOID));
      if (!el.exists()) {
        throw new ExLostBody("эллипсоид");
      }

      SphereBody result = new SphereBody(_id, title(), el.inSphere());
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
    result.add(BLDKEY_ELLIPSOID, getValueAsString(BLDKEY_ELLIPSOID));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>вписанная в эллипсоид %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_ELLIPSOID)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
