package builders;

import bodies.AngleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Angle by two points and value on plane.
 * @author Elena
 */
public class AngleByTwoPointsBuilder extends BodyBuilder {
  static public final String ALIAS = "AngleByTwoPoints";

  public AngleByTwoPointsBuilder() {
  }

  public AngleByTwoPointsBuilder(String name) {
    super(name);
  }

  public AngleByTwoPointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public AngleByTwoPointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addPointOnFirstSide(param.get(BLDKEY_A).asString());
    addVertex(param.get(BLDKEY_B).asString());
    addAngle(param.get(BLDKEY_ANGLE).asDouble());
  }

  public final void addPointOnFirstSide(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addVertex(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addAngle(double angle) {
    setValue(BLDKEY_ANGLE, angle);
  }

  public AngleByTwoPointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.POINT_ON_1_SIDE_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.ANGLE_VALUE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      double alpha = getValueAsDouble(BLDKEY_ANGLE);
      Angle3d angle = Angle3d.angleByTwoPoints2D(a, b, alpha);
      AngleBody result = new AngleBody(_id, title(), angle);
      result.addAnchor(AngleBody.BODY_KEY_A, getValueAsString(BLDKEY_A));
      result.addAnchor(AngleBody.BODY_KEY_B, getValueAsString(BLDKEY_B));
      edt.addAnchor(result.C(), result, AngleBody.BODY_KEY_C);

      result.addRibs(edt);

      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Угол не построен: отсутствует вершина", error.Error.WARNING);
        _exists = false;
      }
      return new AngleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Угол </strong><br> с вершиной %s величиной в %s\u00B0",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ANGLE)), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Угол</strong>";
    }
  }

}
