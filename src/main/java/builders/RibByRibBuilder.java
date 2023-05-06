  package builders;

import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * builder for a segment equal to the given
 * 
 * @author Лютенков
 */
public class RibByRibBuilder extends BodyBuilder {
  static public final String ALIAS = "RibByRib";


  public RibByRibBuilder() {
  }

  public RibByRibBuilder(String id, String name) {
    super(id, name);
  }

  public RibByRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibByRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
  }

  public RibByRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();

    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      Rib3d rib = anchors.get(getValueAsString(BLDKEY_RIB)).getRib();
      Rib3d r = Rib3d.RibByRibAndTwoPnts(rib, a, b);
      RibBody result = new RibBody(_id, r);
      edt.addAnchor(result.B(), result, RibBody.BODY_KEY_B);
      result.addAnchor(RibBody.BODY_KEY_A, anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch( ExNoAnchor | ExDegeneration ex ){
      if (_exists) {
        eh.showMessage("Отрезок не построен: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong> %s%s равный %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }


}
