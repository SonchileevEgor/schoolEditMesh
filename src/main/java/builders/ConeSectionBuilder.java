package builders;

import bodies.ConeBody;
import bodies.ConeSectionBody;
import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.ExZeroDivision;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs conic section.
 * @author rita
 */
public class ConeSectionBuilder extends BodyBuilder {
  static public final String ALIAS = "ConeSection";

  public ConeSectionBuilder() {
  }

  public ConeSectionBuilder(String id, String name) {
    super(id, name);
  }

  public ConeSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ConeSectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CONE, param.get(BLDKEY_CONE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public ConeSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CONE, "Конус", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.SECTION_PLANE_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody p = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!p.exists())
        throw new ExLostBody("плоскость");

      ConeBody cone = (ConeBody)bodies.get(getValueAsString(BLDKEY_CONE));
      if (!cone.exists())
        throw new ExLostBody("конус");

      ConeSectionBody result = cone.section(_id, title(), p);

      for (int i = 0; i < result.coneSection().pointsQuantity(); i++) {
        edt.addAnchor(result.coneSection().points().get(i), result, "p" + i, title() + String.valueOf(i + 1));
      }
      _exists = true;
      return result;
    } catch(ExGeom | ExBadRef | ExZeroDivision ex){
      if (_exists)
        eh.showMessage("Сечение конуса не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new ConeSectionBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CONE, getValueAsString(BLDKEY_CONE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong> конуса %s плоскостью %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_CONE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение конуса</strong>";
    }
  }
}
