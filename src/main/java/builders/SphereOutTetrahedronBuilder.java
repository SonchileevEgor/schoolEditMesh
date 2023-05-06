package builders;

import bodies.SphereBody;
import bodies.TetrahedronBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Sphere3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Circumscribes sphere around tetrahedron
 *
 * @author alexeev
 */
public class SphereOutTetrahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOutTetrahedron";

  public SphereOutTetrahedronBuilder() {
  }

  public SphereOutTetrahedronBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOutTetrahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOutTetrahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TETRAHEDRON, param.get(BLDKEY_TETRAHEDRON).asString());
  }

  public SphereOutTetrahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_TETRAHEDRON, BuilderParam.TETRAHEDRON_ALIAS, BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      TetrahedronBody th = (TetrahedronBody) bodies.get(getValueAsString(BLDKEY_TETRAHEDRON));
      if (!th.exists()) {
        throw new ExLostBody("тетраэдр");
      }

      Sphere3d sphere = th.outSphere();
      SphereBody result = new SphereBody(_id, title(), sphere);
      edt.addAnchor(result.sphere().center(), result, "center");
      _exists = true;
      return result;
    } catch (ExNoBody ex) {
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
    result.add(BLDKEY_TETRAHEDRON, getValueAsString(BLDKEY_TETRAHEDRON));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>описанная около тетраэдра %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_TETRAHEDRON)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
