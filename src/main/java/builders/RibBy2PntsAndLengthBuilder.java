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
public class RibBy2PntsAndLengthBuilder extends BodyBuilder{
  static public final String ALIAS = "RibBy2PntsAndLength";
  public static final String BLDKEY_RIB_LENGTH = "ribLength";

  public RibBy2PntsAndLengthBuilder() {
  }

  public RibBy2PntsAndLengthBuilder(String id, String name) {
    super(id, name);
  }

  public RibBy2PntsAndLengthBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibBy2PntsAndLengthBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_RIB_LENGTH, param.get(BLDKEY_RIB_LENGTH).asDouble());
  }

  public RibBy2PntsAndLengthBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.POINT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Bторая точка", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_RIB_LENGTH, "Длина отрезка", BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      double length = (double)getValue(BLDKEY_RIB_LENGTH);
      Rib3d r = Rib3d.RibBy2PntsAndLength(a, b, length);
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
    result.add(BLDKEY_RIB_LENGTH, getValueAsDouble(BLDKEY_RIB_LENGTH));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong> %s%s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }

}
