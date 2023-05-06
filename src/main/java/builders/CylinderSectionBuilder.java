package builders;

import bodies.CylinderBody;
import bodies.CylinderSectionBody;
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
 *
 * @author rita
 */
public class CylinderSectionBuilder extends BodyBuilder {

  static public final String ALIAS = "CylinderSection";

  public CylinderSectionBuilder() {
  }

  public CylinderSectionBuilder(String id, String name) {
    super(id, name);
  }

  public CylinderSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CylinderSectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CYLINDER, param.get(BLDKEY_CYLINDER).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public CylinderSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CYLINDER, "Цилиндр", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.SECTION_PLANE_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody p = (PlaneBody) bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!p.exists()) {
        throw new ExLostBody("плоскость");
      }

      CylinderBody cylinder = (CylinderBody) bodies.get(getValueAsString(BLDKEY_CYLINDER));
      if (!cylinder.exists()) {
        throw new ExLostBody("цилиндр");
      }

      CylinderSectionBody result = cylinder.section(_id, title(), p);
      for (int i = 0; i < result.cylinderSection().pointsQuantity(); i++) {
        edt.addAnchor(result.cylinderSection().points().get(i), result, "p" + i, title() + String.valueOf(i + 1));
      }
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef | ExZeroDivision ex) {
      if (_exists) {
        eh.showMessage("Сечение цилиндра не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CylinderSectionBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CYLINDER, getValueAsString(BLDKEY_CYLINDER));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong> цилиндра %s плоскостью %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CYLINDER)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение цилиндра</strong>";
    }
  }
}
