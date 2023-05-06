package builders;

import bodies.ArcBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Arc3d;
import geom.Circle3d;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author rita
 */
public class ArcOnCircleBuilder extends BodyBuilder {
  static public final String ALIAS = "ArcOnCircle";
  static public final String BLDKEY_CIRCLE = "circ";

  public ArcOnCircleBuilder() {
  }

  public ArcOnCircleBuilder(String name) {
    super(name);
  }

  public ArcOnCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ArcOnCircleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addCircle(param.get(BLDKEY_CIRCLE).asString());
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addCircle(String id) {
    setValue(BLDKEY_CIRCLE, id);
  }

  public ArcOnCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_DIAM_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, BuilderParam.VERT_2_DIAM_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_CIRCLE, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      Circle3d c = anchors.get(getValueAsString(BLDKEY_CIRCLE)).getDisk();
      Arc3d arc = new Arc3d(a, b, c);
      ArcBody result = new ArcBody(_id, title(), arc);
      result.addAnchor(ArcBody.BODY_KEY_A, getValueAsString(BLDKEY_A));
      result.addAnchor(ArcBody.BODY_KEY_B, getValueAsString(BLDKEY_B));
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Дуга не построена" ,error.Error.WARNING);
        _exists = false;
      }
      return new ArcBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Дуга </strong>%s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Дуга</strong>";
    }
  }
}