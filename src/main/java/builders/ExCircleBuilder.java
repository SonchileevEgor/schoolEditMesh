package builders;

import bodies.CircleBody;
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
import geom.ExDegeneration;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Escribed circle of a triangle.
 *
 * @author Preobrazhenskaia
 */
public class ExCircleBuilder extends BodyBuilder{
  static public final String ALIAS = "ExCircle";

  public ExCircleBuilder() {
  }

  public ExCircleBuilder(String id, String name) {
    super(id, name);
  }

  public ExCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ExCircleBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_TRIANGLE, param.get(BLDKEY_TRIANGLE).asString());
    setValue(BLDKEY_VERTEX, param.get(BLDKEY_VERTEX).asString());
  }

  public ExCircleBuilder(HashMap<String, BuilderParam> params){
    super(params);
  }

  @Override
  public String alias(){ return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      TriangleBody triang = (TriangleBody)bodies.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triang.exists())
        throw new ExLostBody("треугольник");

      CircleBody result;
      if(triang.getAnchorID("A").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new CircleBody(_id, title(), triang.triangle().exCircleA());
        result.addAnchor("A", triang.getAnchorID("A"));
      } else if(triang.getAnchorID("B").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new CircleBody(_id, title(), triang.triangle().exCircleB());
        result.addAnchor("B", triang.getAnchorID("B"));
      } else if(triang.getAnchorID("C").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new CircleBody(_id, title(), triang.triangle().exCircleC());
        result.addAnchor("C", triang.getAnchorID("C"));
      } else
        throw new ExNoAnchor("неверно указана вершина");
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(result.circle(), result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExDegeneration | ExNoBody ex){
      if (_exists) {
				eh.showMessage("Вневписанная окружность не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
			return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams(){
    JsonObject result = new JsonObject();
    result.add(BLDKEY_TRIANGLE, getValueAsString(BLDKEY_TRIANGLE));
    result.add(BLDKEY_VERTEX, getValueAsString(BLDKEY_VERTEX));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Вневписанная окружность</strong> треугольника %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Вневписанная окружность</strong>";
    }
  }
}
