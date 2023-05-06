package builders;

import bodies.PointBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Курганский
 */
public class CentroidBuilder extends BodyBuilder {
  static public final String ALIAS = "Centroid";

  public CentroidBuilder() {
  }

  public CentroidBuilder(String id, String name) {
    super(id, name);
  }

  public CentroidBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CentroidBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public CentroidBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TRIANGLE, param.get(BLDKEY_TRIANGLE).asString());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_TRIANGLE, "Треугольник", BuilderParamType.BODY);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      TriangleBody triangle = (TriangleBody)bodies.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triangle.exists())
         throw new ExLostBody("треугольник");

      Vect3d point = triangle.triangle().centroid();
      PointBody result = new PointBody(_id, point);
      edt.addAnchor(result.point(), result, "P");

      return result;
    } catch (ExNoBody ex) {
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
  public String alias() {
    return ALIAS;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> пересечения медиан<br>треугольника %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_TRIANGLE), TriangleBody.BODY_KEY_FACET));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }

}
