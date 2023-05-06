package builders;

import bodies.CircleBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * @deprecated @author Preobrazhenskaia
 */
public class CircleInTriangleBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleInTriangle";

  public CircleInTriangleBuilder() {
  }

  public CircleInTriangleBuilder(String id, String name) {
    super(id, name);
  }

  public CircleInTriangleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleInTriangleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TRIANGLE, param.get(BLDKEY_TRIANGLE).asString());
  }

  public CircleInTriangleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_TRIANGLE, "Треугольник", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      TriangleBody triang = (TriangleBody) bodies.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triang.exists()) {
        throw new ExLostBody("треугольник");
      }
      Circle3d circ = triang.triangle().inCircle();
      CircleBody result = new CircleBody(_id, title(), circ);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(circ, result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch (ExDegeneration | ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_TRIANGLE, getValueAsString(BLDKEY_TRIANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Вписанная окружность</strong> треугольника %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
