package builders;

import bodies.DodecahedronBody;
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
public class SphereOutDodecahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOutDodecahedron";

  public SphereOutDodecahedronBuilder() {
  }

  public SphereOutDodecahedronBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOutDodecahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOutDodecahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_DODECAHEDRON, param.get(BLDKEY_DODECAHEDRON).asString());
  }

  public SphereOutDodecahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DODECAHEDRON, "Додекаэдр", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      DodecahedronBody dodec = (DodecahedronBody)bodies.get(getValueAsString(BLDKEY_DODECAHEDRON));
      if (!dodec.exists())
        throw new ExLostBody("додекаэдр");

      SphereBody result = new SphereBody(_id, title(), dodec.dodecahedron().outSphere());
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
    result.add(BLDKEY_DODECAHEDRON, getValueAsString(BLDKEY_DODECAHEDRON));
    return result;
   }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>описанная около додекаэдра %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_DODECAHEDRON)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
