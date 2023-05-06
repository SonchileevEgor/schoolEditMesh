package builders;

import bodies.RibBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Rib3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import opengl.LineMark;

/**
 *
 * @author Preobrazhenskaia
 */
public class MedianBuilder extends BodyBuilder {
  static public final String ALIAS = "Mediane";

  public MedianBuilder() {
  }

  public MedianBuilder(String id, String name) {
    super(id, name);
  }

  public MedianBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public MedianBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TRIANGLE, param.get(BLDKEY_TRIANGLE).asString());
    setValue(BLDKEY_VERTEX, param.get(BLDKEY_VERTEX).asString());
  }

  public MedianBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_TRIANGLE, "Треугольник", BuilderParamType.BODY);
    addParam(BLDKEY_VERTEX, BuilderParam.VERT_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  //function adds anchors for second point of median and for middle ribs
  void func(Editor edt, Rib3d rib1, Rib3d rib2, TriangleBody triang, RibBody result, String p1, String p2, i_AnchorContainer anchors) throws ExNoAnchor {
    edt.addAnchor(result.B(), result, "B", title());

    edt.addAnchor(rib1, triang.getAnchorID(p1), result.getAnchorID("B"), result, "r1");
    String s1 = result.getAnchorID("r1");
    i_Anchor anch1 = edt.anchors().get(s1);
    anch1.getState().setLineMark(LineMark.SINGLE);

    edt.addAnchor(rib2, triang.getAnchorID(p2), result.getAnchorID("B"), result, "r2");
    String s2 = result.getAnchorID("r2");
    i_Anchor anch2 = edt.anchors().get(s2);
    anch2.getState().setLineMark(LineMark.SINGLE);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      TriangleBody triang = (TriangleBody) bodies.get(getValueAsString(BLDKEY_TRIANGLE));
      if (!triang.exists()) {
        throw new ExLostBody("треугольник");
      }

      RibBody result;
      Rib3d rib1, rib2;//middle ribs

      if (triang.getAnchorID("A").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new RibBody(_id, triang.triangle().medianeA());
        result.addAnchor("A", triang.getAnchorID("A"));

        rib1 = new Rib3d(triang.triangle().b(), result.B());
        rib2 = new Rib3d(triang.triangle().c(), result.B());
        func(edt, rib1, rib2, triang, result, "B", "C", anchors);
      } else if (triang.getAnchorID("B").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new RibBody(_id, triang.triangle().medianeB());
        result.addAnchor("A", triang.getAnchorID("B"));

        rib1 = new Rib3d(triang.triangle().a(), result.B());
        rib2 = new Rib3d(triang.triangle().c(), result.B());
        func(edt, rib1, rib2, triang, result, "A", "C", anchors);
      } else if (triang.getAnchorID("C").equals(getValueAsString(BLDKEY_VERTEX))) {
        result = new RibBody(_id, triang.triangle().medianeC());
        result.addAnchor("A", triang.getAnchorID("C"));

        rib1 = new Rib3d(triang.triangle().a(), result.B());
        rib2 = new Rib3d(triang.triangle().b(), result.B());
        func(edt, rib1, rib2, triang, result, "A", "B", anchors);
      } else {
        throw new ExNoAnchor("неверно указана вершина");
      }
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Медиана треугольника не построена: " + ex.getMessage(), error.Error.WARNING);
      }
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
      return String.format("<html><strong>Медиана</strong> треугольника %s,<br>проведённая из вершины %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_TRIANGLE)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_VERTEX)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Медиана</strong>";
    }
  }
}
