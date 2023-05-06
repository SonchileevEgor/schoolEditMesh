package builders;

import bodies.RayBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Ray3d;
import static geom.Ray3d.ray3dByTwoPoints;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Igor
 */
public class RayTwoPointsBuilder extends BodyBuilder {
  static public final String ALIAS = "RayTwoPoints";

  public RayTwoPointsBuilder() {
  }

  public RayTwoPointsBuilder(String name) {
    super(name);
  }

  public RayTwoPointsBuilder(String id, String name) {
    super(id, name);
  }

  public RayTwoPointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RayTwoPointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public RayTwoPointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 101);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Ray3d ray = ray3dByTwoPoints(anchors.getVect(getValueAsString(BLDKEY_A)),
              anchors.getVect(getValueAsString(BLDKEY_B)));
      RayBody result = new RayBody(_id, title(), ray);
      result.addAnchor(RayBody.KEY_A, anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor(RayBody.KEY_B, anchors.get(getValueAsString(BLDKEY_B)).id());
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Луч не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RayBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Луч</strong> построенный по точкам %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Луч</strong>";
    }
  }
}
