package builders;

import bodies.PyramidBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author gayduk
 */
public class SphereInPyramidBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereInPyramid";

  public SphereInPyramidBuilder() {
  }

  public SphereInPyramidBuilder(String id, String name) {
    super(id, name);
  }

  public SphereInPyramidBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereInPyramidBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PYRAMID, param.get(BLDKEY_PYRAMID).asString());
  }

  public SphereInPyramidBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PYRAMID, "Пирамида", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      PyramidBody pyramid = (PyramidBody)bodies.get(getValueAsString(BLDKEY_PYRAMID));
      if (!pyramid.exists())
        throw new ExLostBody("пирамида");

      SphereBody result = new SphereBody(_id, title(), pyramid.pyramid().inSphere());
      edt.addAnchor(result.sphere().center(), result, "center");
      _exists = true;
      return result;
    } catch(ExNoBody | ExGeom ex){
      if (_exists)
        eh.showMessage("Сфера не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new SphereBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PYRAMID, getValueAsString(BLDKEY_PYRAMID));
    return result;
   }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong> <br>вписанная в пирамиду %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PYRAMID)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
