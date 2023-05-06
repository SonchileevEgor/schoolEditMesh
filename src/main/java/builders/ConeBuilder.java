package builders;

import bodies.ConeBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.Cone3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Builder of cone. Constructs cone by vertex, center and base radius.
 *
 * @author alexeev
 */
public class ConeBuilder extends BodyBuilder {
  public static final String ALIAS = "Cone";

  public ConeBuilder() {
  }

  public ConeBuilder(String id, String name) {
    super(id, name);
  }

  public ConeBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ConeBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_VERTEX, param.get(BLDKEY_VERTEX).asString());
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_RADIUS, param.get(BLDKEY_RADIUS).asDouble());
  }

  public ConeBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_VERTEX, "Вершина конуса", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_CENTER, "Центр основания", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_RADIUS, "Радиус основания", BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor v = anchors.get(getValueAsString(BLDKEY_VERTEX));
      i_Anchor c = anchors.get(getValueAsString(BLDKEY_CENTER));
      Cone3d cone = new Cone3d(v.getPoint(), c.getPoint(), getValueAsDouble(BLDKEY_RADIUS));
      Circle3d disk = cone.getBaseCircle();
      ConeBody result = new ConeBody(_id, title(), cone);
      edt.addAnchor(disk, c.id(), result, ConeBody.KEY_BASE);
      result.addAnchor(ConeBody.KEY_VERTEX, v.id());
      result.addAnchor(ConeBody.KEY_CENTER, c.id());
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Конус не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new ConeBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_VERTEX, getValueAsString(BLDKEY_VERTEX));
    result.add(BLDKEY_CENTER, getValueAsString(BLDKEY_CENTER));
    result.add(BLDKEY_RADIUS, getValueAsDouble(BLDKEY_RADIUS));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Конус</strong>, "
              + "построенный по вершине %s<br> центру основания %s<br>"
              + " с радиусом основания %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_VERTEX)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
              Util.valueOf(getValueAsDouble(BLDKEY_RADIUS), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Конус</strong>";
    }
  }
}
