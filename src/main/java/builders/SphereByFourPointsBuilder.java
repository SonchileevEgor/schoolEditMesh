package builders;

import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Sphere3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a sphere by its four points
 *
 * @author alexeev
 */
public class SphereByFourPointsBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereByFourPoints";

  public SphereByFourPointsBuilder() {
  }

  public SphereByFourPointsBuilder(String name) {
    super(name);
  }

  public SphereByFourPointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereByFourPointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addC(param.get(BLDKEY_C).asString());
    addD(param.get(BLDKEY_D).asString());
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

  public final void addD(String id) {
    setValue(BLDKEY_D, id);
  }

  public SphereByFourPointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая точка на сфере", BuilderParamType.ANCHOR, 103);
    addParam(BLDKEY_B, "Вторая точка на сфере", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_C, "Третья точка на сфере", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_D, "Четвёртая точка на сфере", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      Vect3d c = anchors.getVect(getValueAsString(BLDKEY_C));
      Vect3d d = anchors.getVect(getValueAsString(BLDKEY_D));
      Sphere3d sph = new Sphere3d(a, b, c, d);
      SphereBody result = new SphereBody(_id, title(), sph);
      edt.addAnchor(result.sphere().center(), result, SphereBody.BODY_KEY_CENTER);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Сфера не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new SphereBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_D, getValueAsString(BLDKEY_D));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong>,<br> построенная по точкам %s, %s, %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_D)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
