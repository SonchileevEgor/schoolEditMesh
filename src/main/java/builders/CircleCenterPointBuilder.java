package builders;

import bodies.CircleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class CircleCenterPointBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleCenterPoint";
  
  public CircleCenterPointBuilder() {
  }
  
  public CircleCenterPointBuilder(String name) {
    super(name);
  }

  public CircleCenterPointBuilder(String id, String name) {
    super(id, name);
  }

  public CircleCenterPointBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleCenterPointBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public CircleCenterPointBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_NORMAL, new Vect3d(param.get("x").asDouble(),
                                       param.get("y").asDouble(),
                                       param.get("z").asDouble()));
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_NORMAL, "Вектор нормали", BuilderParamType.VECT);
    addParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_POINT, "Точка на окружности", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor c = anchors.get(getValueAsString(BLDKEY_CENTER));
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));
      Vect3d normal = getValueAsVect(BLDKEY_NORMAL);
      Circle3d circ = new Circle3d(Vect3d.dist(point, c.getPoint()), normal, c.getPoint());
      CircleBody result = new CircleBody(_id, title(), circ);
      result.addAnchor(CircleBody.BODY_KEY_CENTER, anchors.get(getValueAsString(BLDKEY_CENTER)).id());
      edt.addAnchor(circ, c.id(), result, CircleBody.BODY_KEY_DISK);
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CircleBody(_id, title());
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
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add("x", getValueAsVect(BLDKEY_NORMAL).x());
    result.add("y", getValueAsVect(BLDKEY_NORMAL).y());
    result.add("z", getValueAsVect(BLDKEY_NORMAL).z());

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность</strong> с центром %s и точкой %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Окружность</strong>";
    }
  }

}
