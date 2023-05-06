package builders;

import bodies.PlaneBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Лютенков
 */
public class PlaneByTriangleBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByTriangle";

  public PlaneByTriangleBuilder() {
  }

  public PlaneByTriangleBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByTriangleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByTriangleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TRIANGLE, param.get(BLDKEY_TRIANGLE).asString());
  }

  public PlaneByTriangleBuilder(HashMap<String, BuilderParam> params) {
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
      Plane3d plane = triang.triangle().plane();
      PlaneBody result = new PlaneBody(_id, title(), plane);

      return result;
    } catch (ExDegeneration | ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Плоскость не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PlaneBody(_id, title());
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
      return String.format("<html><strong>Плоскость</strong> треугольника %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }

}
