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
public class PointOnArcByAngleBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnArcByAngle";
  private Arc3d _arc;

  public PointOnArcByAngleBuilder() {
  }

  public PointOnArcByAngleBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnArcByAngleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnArcByAngleBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_ARC, param.get(BLDKEY_ARC).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public PointOnArcByAngleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ARC, BuilderParam.ARC_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.DOUBLE);
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
      double phi = (double)getValue(BLDKEY_ANGLE);
      Vect3d point = _arc.getPointByPolar(phi);
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
    result.add(BLDKEY_ANGLE, (double)getValue(BLDKEY_ANGLE));
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
      double angle = _arc.getPolarAngleByPoint(newPosition);
      setValue(BLDKEY_ANGLE, angle);
    } catch (ExDegeneration ex) { }
  }
}
