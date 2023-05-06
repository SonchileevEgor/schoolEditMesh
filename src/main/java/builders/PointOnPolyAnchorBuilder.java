package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Построение точки по грани, на которой она находится
 * и двум коэффициентам выпуклой комбинации первых трёх вершин грани.
 * (По аналогии с PointBuilderHull3)
 *
 * @author alexeev
 */
public class PointOnPolyAnchorBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnPolyAnchor";
  private Vect3d _a;
  private Vect3d _b;
  private Vect3d _c;

  public PointOnPolyAnchorBuilder() {
  }

  public PointOnPolyAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnPolyAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnPolyAnchorBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_FACET, param.get(BLDKEY_FACET).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
    setValue(BLDKEY_BETA, param.get(BLDKEY_BETA).asDouble());
  }

  public PointOnPolyAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_FACET, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_1_ALIAS, BuilderParamType.DOUBLE, 41);
    addParam(BLDKEY_BETA, BuilderParam.COEF_2_ALIAS, BuilderParamType.DOUBLE, 40);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor anch = anchors.get(getValueAsString(BLDKEY_FACET));
      Polygon3d poly = anch.getPoly();
      _a = poly.points().get(0);
      _b = poly.points().get(1);
      _c = poly.points().get(2);
      Vect3d p = Vect3d.getVectFromAffineCoords(_a, _b, _c,
              (double)getValue(BLDKEY_ALPHA), (double)getValue(BLDKEY_BETA));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      anchors.get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createPolygonBehavior(poly));
      _exists = true;
      return result;
    } catch(ExNoAnchor ex){
      if (_exists)
        eh.showMessage("Точка на грани не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_FACET, getValueAsString(BLDKEY_FACET));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    result.add(BLDKEY_BETA, (double)getValue(BLDKEY_BETA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong>, на грани %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_FACET)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double[] coefs = Vect3d.getMultipliersForHull3(_a, _b, _c, newPosition);
      setValue(BLDKEY_ALPHA, coefs[0]);
      setValue(BLDKEY_BETA, coefs[1]);
    } catch (ExDegeneration ex) { }
  }
}
