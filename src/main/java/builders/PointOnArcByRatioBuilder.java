package builders;

import bodies.ArcBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.behavior.BehaviorFactory;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Arc3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author rita
 */
public class PointOnArcByRatioBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnArcByRatio";
  public static final String BLDKEY_RATIO = "ratio";
  private Arc3d _arc;

  public PointOnArcByRatioBuilder() {
  }

  public PointOnArcByRatioBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnArcByRatioBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnArcByRatioBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_ARC, param.get(BLDKEY_ARC).asString());
    setValue(BLDKEY_RATIO, param.get(BLDKEY_RATIO).asDouble());
  }

  public PointOnArcByRatioBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ARC, BuilderParam.ARC_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_RATIO, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      ArcBody arcBody = (ArcBody)bodies.get(getValueAsString(BLDKEY_ARC));
      if (!arcBody.exists())
        throw new ExLostBody("Дуга");
      _arc = (Arc3d)arcBody.getGeom();
      double ratio = (double)getValue(BLDKEY_RATIO);
      Vect3d point = _arc.getPointByRatio(ratio);
      PointBody result = new PointBody(_id, point);
      edt.addAnchor(result.point(), result, "P", title());
      /**
       * Определяем поведение при перемещении точки.
       */
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createArcBehavior(_arc));
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExNoBody ex){
      if (_exists)
        eh.showMessage("Точка не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_ARC, getValueAsString(BLDKEY_ARC));
    result.add(BLDKEY_RATIO, (double)getValue(BLDKEY_RATIO));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong>, на дуге %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_ARC)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка на дуге</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double ratio = _arc.getRatioByPoint(newPosition);
      setValue(BLDKEY_RATIO, ratio);
    } catch (ExDegeneration ex) { }
  }
}
