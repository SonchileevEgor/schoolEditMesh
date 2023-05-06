package builders;

// 3d point builder using convex hull of three points.

import bodies.PointBody;
import static builders.BodyBuilder.BLDKEY_HEIGHT;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

public class PointBuilderHull3 extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "Hull3";
  private Vect3d _a;
  private Vect3d _b;
  private Vect3d _c;

  public PointBuilderHull3() {
  }
  
  public PointBuilderHull3(String id, String name) {
    super(id, name);
  }

  public PointBuilderHull3(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointBuilderHull3( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_C, param.get(BLDKEY_C).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
    setValue(BLDKEY_BETA, param.get(BLDKEY_BETA).asDouble());
  }

  public PointBuilderHull3(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.POINT_1_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, BuilderParam.POINT_2_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, BuilderParam.POINT_3_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_1_ALIAS, BuilderParamType.DOUBLE, 41);
    addParam(BLDKEY_BETA, BuilderParam.COEF_2_ALIAS, BuilderParamType.DOUBLE, 40);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      _a = anchors.getVect(getValueAsString(BLDKEY_A));
      _b = anchors.getVect(getValueAsString(BLDKEY_B));
      _c = anchors.getVect(getValueAsString(BLDKEY_C));
      if (Checker.threePointsOnTheLine(_a, _b, _c))
        throw new ExDegeneration("точки лежат на одной прямой");
      Vect3d p = Vect3d.getVectFromAffineCoords(_a, _b, _c,
              (double)getValue(BLDKEY_ALPHA), (double)getValue(BLDKEY_BETA));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      anchors.get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createPlaneBehavior(new Plane3d(_a, _b, _c)));
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExDegeneration ex){
      if (_exists)
        eh.showMessage("Точка не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    result.add(BLDKEY_BETA, (double)getValue(BLDKEY_BETA));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      String alpha = Util.valueOf(getValueAsDouble(BLDKEY_ALPHA), precision);
      String beta = Util.valueOf(getValueAsDouble(BLDKEY_BETA), precision);
      String gamma = Util.valueOf(1 - getValueAsDouble(BLDKEY_ALPHA) -
              getValueAsDouble(BLDKEY_BETA), precision);
      return String.format("<html><strong>Линейная комбинация</strong> "
              + "точек %s, %s и %s<br> с коэффициентами %s, %s, %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)),
        alpha, beta, gamma);
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
};
