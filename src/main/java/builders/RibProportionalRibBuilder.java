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
 *
 * @author Лютенков
 */
public class RibProportionalRibBuilder extends BodyBuilder {
  static public final String ALIAS = "RibProportionalRib";


  public RibProportionalRibBuilder() {
  }

  public RibProportionalRibBuilder(String id, String name) {
    super(id, name);
  }

  public RibProportionalRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibProportionalRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_COEFFICIENT, param.get(BLDKEY_COEFFICIENT).asDouble());
  }

  public RibProportionalRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_A, "A", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "B", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_COEFFICIENT, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE);
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
      double coef = getValueAsDouble(BLDKEY_COEFFICIENT);
      Rib3d r = Rib3d.RibProportionalRibAndTwoPnts(rib, a, b, coef);
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
    result.add(BLDKEY_COEFFICIENT, getValueAsDouble(BLDKEY_COEFFICIENT));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong> %s%s пропорциональный %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }


}
