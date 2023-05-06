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
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs circle by three points on it.
 *
 * @author alexeev
 */
public class CircleByThreePointsBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleByThreePoints";

  public CircleByThreePointsBuilder() {
  }

  public CircleByThreePointsBuilder(String name) {
    super(name);
  }

  public CircleByThreePointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByThreePointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addC(param.get(BLDKEY_C).asString());
  }

  public CircleByThreePointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addC(String id) {
    setValue(BLDKEY_C, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая точка окружности", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, "Вторая точка окружности", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, "Третья точка окружности", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BLDKEY_A);
      String bID = getValueAsString(BLDKEY_B);
      String cID = getValueAsString(BLDKEY_C);

      Vect3d a = anchors.getVect(aID);
      Vect3d b = anchors.getVect(bID);
      Vect3d c = anchors.getVect(cID);

      Circle3d circle = new Circle3d(a, b, c);
      CircleBody result = new CircleBody(_id, title(), circle);

      edt.addAnchor(result.center(), result, CircleBody.BODY_KEY_CENTER);
      edt.addAnchor(circle, result.getAnchorID(CircleBody.BODY_KEY_CENTER), result, CircleBody.BODY_KEY_DISK);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Окружность не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность</strong> по точкам %s, %s, %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Окружность</strong><br> по трём точкам";
    }
  }
};

