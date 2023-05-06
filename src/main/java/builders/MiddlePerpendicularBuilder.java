package builders;

import bodies.RibBody;
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
 *
 * @author rita
 */
public class MiddlePerpendicularBuilder extends BodyBuilder {
  static public final String ALIAS = "MiddlePerpendicular";

  public MiddlePerpendicularBuilder() {
  }

  public MiddlePerpendicularBuilder(String name) {
    super(name);
  }

  public MiddlePerpendicularBuilder(String id, String name) {
    super(id, name);
  }

  public MiddlePerpendicularBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public MiddlePerpendicularBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addTriangle(param.get(BLDKEY_TRIANGLE).asString());
    addVertex(param.get(BLDKEY_VERTEX).asString());
  }

  public MiddlePerpendicularBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addTriangle(String id) {
    setValue(BLDKEY_TRIANGLE, id);
  }

  public final void addVertex(String id) {
    setValue(BLDKEY_VERTEX, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_TRIANGLE, "Треугольник", BuilderParamType.BODY, 102);
    addParam(BLDKEY_VERTEX, BuilderParam.VERT_ALIAS, BuilderParamType.ANCHOR, 101);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      TriangleBody triang = (TriangleBody)bodies.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triang.exists()) {
        throw new ExLostBody("треугольник");
      }

      RibBody result;
      if (triang.getAnchorID("A").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new RibBody(_id, triang.triangle().midPerpBCRib());
      } else if (triang.getAnchorID("B").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new RibBody(_id, triang.triangle().midPerpCARib());
      } else if (triang.getAnchorID("C").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new RibBody(_id, triang.triangle().midPerpABRib());
      } else {
        throw new ExNoAnchor("неверно указана вершина");
      }
      edt.addAnchor(result.A(), result, "A");
      edt.addAnchor(result.B(), result, "B");
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Срединный перпендикуляр треугольника не построен: " + ex.getMessage(),
                error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_TRIANGLE, getValueAsString(BLDKEY_TRIANGLE));
    result.add(BLDKEY_VERTEX, getValueAsString(BLDKEY_VERTEX));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Срединный перпендикуляр</strong> треугольника %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Срединный перпендикуляр</strong>";
    }
  }
}
