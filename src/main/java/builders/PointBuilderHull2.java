package builders;

// 3d point builder using convex hull of two points.

import bodies.PointBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

public class PointBuilderHull2 extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "Hull2";
  private Vect3d _a;
  private Vect3d _b;

  public PointBuilderHull2() {
  }
  
  public PointBuilderHull2(String id, String name) {
    super(id, name);
  }

  public PointBuilderHull2(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointBuilderHull2( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PointBuilderHull2(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.POINT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.POINT_2_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      _a = anchors.getVect(getValueAsString(BLDKEY_A));
      _b = anchors.getVect(getValueAsString(BLDKEY_B));
      Line3d line = Line3d.line3dByTwoPoints(_a, _b);
      Vect3d p = Vect3d.conv_hull(_a, _b, getValueAsDouble(BLDKEY_ALPHA));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      anchors.get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createLineBehavior(line));
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
    result.add(BLDKEY_ALPHA, getValueAsDouble(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Линейная комбинация</strong> точек %s и %s<br> с коэффициентом %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        Util.valueOf(getValueAsDouble(BLDKEY_ALPHA), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double alpha = Vect3d.getMultiplierForHull2(_a, _b, newPosition);
      setValue(BLDKEY_ALPHA, alpha);
    } catch (ExDegeneration ex) { }
  }
};
