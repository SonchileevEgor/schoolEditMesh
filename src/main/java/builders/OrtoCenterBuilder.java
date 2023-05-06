package builders;

/**
 *
 * @author Лютенков
 */
import bodies.TriangleBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import error.i_ErrorHandler;
import editor.i_BodyContainer;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

public class OrtoCenterBuilder extends BodyBuilder {
  static public final String ALIAS = "OrtoCenter";

  public OrtoCenterBuilder() {
  }

  public OrtoCenterBuilder(String id, String name) {
    super(id, name);
  }

  public OrtoCenterBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public OrtoCenterBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TRIANGLE, param.get(BLDKEY_TRIANGLE).asString());
  }

  public OrtoCenterBuilder(HashMap<String, BuilderParam> params) {
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
    i_BodyContainer bd = edt.bd();
    try {
      TriangleBody triangle = (TriangleBody)bd.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triangle.exists()) {
        throw new ExLostBody("треугольник");
      }
      Vect3d point = triangle.triangle().ortoCenter();
      PointBody result = new PointBody(_id, point);
      edt.addAnchor(result.point(), result, "P");
      return result;
    } catch (ExNoBody | ExDegeneration ex) {
      return new PointBody(_id);
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
      return String.format("<html><strong>Точка</strong> пересечения высот треугольника %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }

};
