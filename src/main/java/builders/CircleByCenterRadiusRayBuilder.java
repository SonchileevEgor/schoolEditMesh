package builders;

import bodies.CircleBody;
import bodies.RayBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;
/**
 *
 * @author Лютенков
 */
public class CircleByCenterRadiusRayBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleByCenterRadiusRay";

  public CircleByCenterRadiusRayBuilder() {
  }

  public CircleByCenterRadiusRayBuilder(String id, String name) {
    super(id, name);
  }

  public CircleByCenterRadiusRayBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByCenterRadiusRayBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public CircleByCenterRadiusRayBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RAY, param.get(BLDKEY_RAY).asString());
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_RADIUS, param.get(BLDKEY_RADIUS).asDouble());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RAY, "Луч", BuilderParamType.BODY);
    addParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR);
    addParam(BLDKEY_RADIUS, "Радиус", BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      i_Anchor c = anchors.get(getValueAsString(BLDKEY_CENTER));
      RayBody b = (RayBody)bodies.get(getValueAsString(BLDKEY_RAY));
      Vect3d normal = b.l();
      double radius = getValueAsDouble(BLDKEY_RADIUS);
      Circle3d circ = new Circle3d(radius, normal, c.getPoint());
      CircleBody result = new CircleBody(_id, title(), circ);
      result.addAnchor(CircleBody.BODY_KEY_CENTER,
              anchors.get(getValueAsString(BLDKEY_CENTER)).id());
      edt.addAnchor(circ, c.id(), result, CircleBody.BODY_KEY_DISK);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
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
    result.add(BLDKEY_RADIUS, getValueAsDouble(BLDKEY_RADIUS));
    result.add(BLDKEY_RAY, getValueAsString(BLDKEY_RAY));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность</strong> с центром %s и радиусом %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
              Util.valueOf(getValueAsDouble(BLDKEY_RADIUS), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
    
    
}
