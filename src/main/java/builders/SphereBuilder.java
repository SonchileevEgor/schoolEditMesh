package builders;

import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Sphere3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Sphere builder.
 * @author lt
 */
public class SphereBuilder extends BodyBuilder {
  static public final String ALIAS = "Sphere";

  public SphereBuilder() {
  }

  public SphereBuilder(String id, String name) {
    super(id, name);
  }

  public SphereBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public SphereBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_RADIUS, param.get(BLDKEY_RADIUS).asDouble());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR);
    addParam(BLDKEY_RADIUS, "Радиус", BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d c = anchors.getVect(getValueAsString(BLDKEY_CENTER));
      SphereBody result = new SphereBody(_id, title(), new Sphere3d(c, getValueAsDouble(BLDKEY_RADIUS)));
      result.addAnchor("center", anchors.get(getValueAsString("center")).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Сфера не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new SphereBody(_id, title());
    }
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CENTER, getValueAsString(BLDKEY_CENTER));
    result.add(BLDKEY_RADIUS, getValueAsDouble(BLDKEY_RADIUS));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong><br> с центром %s и радиусом %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
              Util.valueOf(getValueAsDouble(BLDKEY_RADIUS), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
};
