package builders;

import bodies.RibBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Билдер биссектрисы треугольника.
 * BLDKEY_TRIANGLE - Треугольник
 * BLDKEY_VERTEX - Вершина треугольника
 *
 * @author rita
 */
public class BisectrixBuilder extends BodyBuilder {
  static public final String ALIAS = "Bisectrix";

  public BisectrixBuilder() {
  }

  public BisectrixBuilder(String name) {
    super(name);
  }

  public BisectrixBuilder(String id, String name) {
    super(id, name);
  }

  public BisectrixBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public BisectrixBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addTriangle(param.get(BLDKEY_TRIANGLE).asString());
    addVertex(param.get(BLDKEY_VERTEX).asString());
  }

  public BisectrixBuilder(HashMap<String, BuilderParam> params) {
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
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try{
      TriangleBody triang = (TriangleBody)bodies.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triang.exists())
        throw new ExLostBody("треугольник");

      String vertex = getValueAsString(BLDKEY_VERTEX);
      RibBody result;
      if (triang.getAnchorID(TriangleBody.BODY_KEY_A).equals(vertex)) {
        result = new RibBody(_id, triang.triangle().bisectrixA());
        result.addAnchor(RibBody.BODY_KEY_A, triang.getAnchorID(TriangleBody.BODY_KEY_A));
      } else if (triang.getAnchorID(TriangleBody.BODY_KEY_B).equals(vertex)) {
        result = new RibBody(_id, triang.triangle().bisectrixB());
        result.addAnchor(RibBody.BODY_KEY_A, triang.getAnchorID(TriangleBody.BODY_KEY_B));
      } else if (triang.getAnchorID(TriangleBody.BODY_KEY_C).equals(vertex)) {
        result = new RibBody(_id, triang.triangle().bisectrixC());
        result.addAnchor(RibBody.BODY_KEY_A, triang.getAnchorID(TriangleBody.BODY_KEY_C));
      } else {
        throw new ExNoAnchor("неверно указана вершина");
      }
      edt.addAnchor(result.B(), result, RibBody.BODY_KEY_B, title());
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch(ExNoBody | ExNoAnchor | ExDegeneration ex){
      if (_exists)
        eh.showMessage("Биссектриса не построена: " + ex.getMessage(), error.Error.WARNING);
      _exists = false;
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
      return String.format("<html><strong>Биссектриса</strong> треугольника %s из вершины %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_VERTEX)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Биссектриса</strong>";
    }
  }
}
