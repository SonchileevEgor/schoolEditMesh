package builders;

import bodies.CircleBody;
import bodies.LineBody;
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
public class CircleByCenterRadiusLineBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleByCenterRadiusLine";

  public CircleByCenterRadiusLineBuilder() {
  }

  public CircleByCenterRadiusLineBuilder(String id, String name) {
    super(id, name);
  }

  public CircleByCenterRadiusLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByCenterRadiusLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public CircleByCenterRadiusLineBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_RADIUS, param.get(BLDKEY_RADIUS).asDouble());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY);
    addParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR);
    addParam(BLDKEY_RADIUS, "Радиус", BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      i_Anchor c = anchors.get(getValueAsString(BLDKEY_CENTER));
      LineBody b = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      Vect3d normal = b.l();
      double radius = getValueAsDouble(BLDKEY_RADIUS);
      Circle3d circ = new Circle3d(radius, normal, c.getPoint());
      CircleBody result = new CircleBody(_id, title(), circ);
      result.addAnchor("center", anchors.get(getValueAsString(BLDKEY_CENTER)).id());
      edt.addAnchor(circ, c.id(), result, "disk");
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody ex) {
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
    result.add(BLDKEY_RADIUS, getValueAsDouble(BLDKEY_RADIUS));
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));

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
