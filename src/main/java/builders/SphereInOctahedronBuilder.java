package builders;

import bodies.OctahedronBody;
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
public class SphereInOctahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereInOctahedron";

  public SphereInOctahedronBuilder() {
  }

  public SphereInOctahedronBuilder(String id, String name) {
    super(id, name);
  }

  public SphereInOctahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereInOctahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_OCTAHEDRON, param.get(BLDKEY_OCTAHEDRON).asString());
  }

  public SphereInOctahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_OCTAHEDRON, "Октаэдр", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      OctahedronBody okt = (OctahedronBody)bodies.get(getValueAsString(BLDKEY_OCTAHEDRON));
      if (!okt.exists())
        throw new ExLostBody("октаэдр");

      SphereBody result = new SphereBody(_id, title(), okt.octahedron().inSphere());
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
    result.add(BLDKEY_OCTAHEDRON, getValueAsString(BLDKEY_OCTAHEDRON));
    return result;
   }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>вписанная в октаэдр %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_OCTAHEDRON)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
