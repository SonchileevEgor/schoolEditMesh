package builders;

import bodies.CubeBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.Cube3d;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Cube builder. Constructs cube by two points and angle of rotation [0; 2pi)
 * 
 * @author alexeev
 */
public class CubeByTwoPointsAndAngleBuilder extends BodyBuilder {
  static public final String ALIAS = "CubeByTwoPointsAndAngle";
  
  public CubeByTwoPointsAndAngleBuilder() {
  }
  
  public CubeByTwoPointsAndAngleBuilder(String name) {
    super(name);
  }

  public CubeByTwoPointsAndAngleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CubeByTwoPointsAndAngleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addAngle(param.get(BLDKEY_ALPHA).asDouble());
  }

  public CubeByTwoPointsAndAngleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addAngle(double angle) {
    setValue(BLDKEY_ALPHA, angle);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина куба", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая вершина куба", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BLDKEY_A);
      String bID = getValueAsString(BLDKEY_B);
      Vect3d a = anchors.getVect(aID);
      Vect3d b = anchors.getVect(bID);
      double angle = getValueAsDouble(BLDKEY_ALPHA);
      Cube3d cube = Cube3d.cube3dByTwoPointsAndAngle(a, b, angle);
      CubeBody result = new CubeBody(_id, title(), cube);
      result.addAnchor("A1", aID);
      result.addAnchor("B1", bID);
      edt.addAnchor(result.C1(), result, "C1");
      edt.addAnchor(result.D1(), result, "D1");
      edt.addAnchor(result.A2(), result, "A2");
      edt.addAnchor(result.B2(), result, "B2");
      edt.addAnchor(result.C2(), result, "C2");
      edt.addAnchor(result.D2(), result, "D2");

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Куб не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CubeBody(_id, title());
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
      return String.format("<html><strong>Куб</strong>, построенный по вершинам"
              + " %s, %s<br> и углу поворота %s\u00B0",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Куб</strong>";
    }
  }

}
