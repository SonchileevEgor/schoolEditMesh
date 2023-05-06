package builders;

import bodies.CircleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author Лютенков
 */
public class CircleByDiameterAngleBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleByDiameterAngleBuilder";

  public CircleByDiameterAngleBuilder() {
  }

  public CircleByDiameterAngleBuilder(String name) {
    super(name);
  }

  public CircleByDiameterAngleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByDiameterAngleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addAngle(param.get(BLDKEY_ANGLE).asDouble());
  }

  public CircleByDiameterAngleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addAngle(double angle) {
    setValue(BLDKEY_ANGLE, angle);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_DIAM_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_2_DIAM_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
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
      double angle = (double) getValue(BLDKEY_ANGLE);

      Circle3d disk = Circle3d.CircleBy2PntsAngle(a, b, angle);
      CircleBody result = new CircleBody(_id, title(), disk);

      edt.addAnchor(result.center(), result, CircleBody.BODY_KEY_CENTER);
      edt.addAnchor(disk, result.getAnchorID(CircleBody.BODY_KEY_CENTER), result, CircleBody.BODY_KEY_DISK);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_ANGLE, (double) getValue(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность</strong> по точкам на концах диаметра %s, %s"
              + "<br> и углу %s рад",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              Util.valueOf(getValueAsDouble(BLDKEY_ANGLE), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}
