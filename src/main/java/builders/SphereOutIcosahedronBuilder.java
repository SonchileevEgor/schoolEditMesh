package builders;

import bodies.IcosahedronBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author gayduk
 */
public class SphereOutIcosahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOutIcosahedron";
  
  public SphereOutIcosahedronBuilder() {
  }
  
  public SphereOutIcosahedronBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOutIcosahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOutIcosahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_ICOSAHEDRON, param.get(BLDKEY_ICOSAHEDRON).asString());
  }

  public SphereOutIcosahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ICOSAHEDRON, "Икосаэдр", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      IcosahedronBody ico = (IcosahedronBody)bodies.get(getValueAsString(BLDKEY_ICOSAHEDRON));
      if (!ico.exists())
        throw new ExLostBody("икосаэдр");

      SphereBody result = new SphereBody(_id, title(), ico.icosahedron().outSphere());
      edt.addAnchor(result.sphere().center(), result, "center");
      _exists = true;
      return result;
    } catch(ExNoBody | ExGeom ex){
      if (_exists)
        eh.showMessage("Сфера не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new SphereBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_ICOSAHEDRON, getValueAsString(BLDKEY_ICOSAHEDRON));
    return result;
   }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>описанная около икосаэдра %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_ICOSAHEDRON)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
