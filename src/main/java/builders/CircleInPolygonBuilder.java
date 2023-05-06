package builders;

import bodies.CircleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Inscribes circle into polygon
 *
 * @author alexeev
 */
public class CircleInPolygonBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleInPolygon";

  public CircleInPolygonBuilder() {
  }

  public CircleInPolygonBuilder(String id, String name) {
    super(id, name);
  }

  public CircleInPolygonBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleInPolygonBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
  }

  public CircleInPolygonBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor p = anchors.get(getValueAsString(BLDKEY_POLYGON));

      Circle3d circ = p.getPoly().inCircle();
      CircleBody result = new CircleBody(_id, title(), circ);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(circ, result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Вписанная окружность</strong> многоугольника %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
