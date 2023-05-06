package builders;

import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a plane by three points.
 *
 * @author alexeev
 */
public class PlaneThreePointsBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByThreePoints";

  
  public PlaneThreePointsBuilder() {
  }

  public PlaneThreePointsBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneThreePointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneThreePointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_C, param.get(BLDKEY_C).asString());
  }

  public PlaneThreePointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая точка плоскости", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, "Вторая точка плоскости", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, "Третья точка плоскости", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Plane3d plane = new Plane3d(anchors.getVect(getValueAsString(BLDKEY_A)),
                                  anchors.getVect(getValueAsString(BLDKEY_B)),
                                  anchors.getVect(getValueAsString(BLDKEY_C)));
      PlaneBody result = new PlaneBody(_id, title(), plane);
      result.addAnchor(PlaneBody.BODY_KEY_A, anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor(PlaneBody.BODY_KEY_B, anchors.get(getValueAsString(BLDKEY_B)).id());
      result.addAnchor(PlaneBody.BODY_KEY_C, anchors.get(getValueAsString(BLDKEY_C)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Плоскость не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PlaneBody(_id, title());
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
      return String.format("<html><strong>Плоскость</strong>,<br> построенная по точкам %s, %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
