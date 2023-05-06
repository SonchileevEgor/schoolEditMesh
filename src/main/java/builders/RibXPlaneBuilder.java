package builders;

import bodies.PlaneBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Finds intersection of plane and rib, if exists.
 *
 * @author alexeev
 */
public class RibXPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "RibXPlane";

  public RibXPlaneBuilder() {
  }

  public RibXPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public RibXPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibXPlaneBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public RibXPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      PlaneBody plane = (PlaneBody) bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!plane.exists()) {
        throw new ExLostBody("плоскость");
      }

      Vect3d intersect = rib.getRib().intersectWithPlane(plane.plane());
      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch (ExBadRef | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение отрезка и плоскости не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong> отрезка %s и плоскости %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExBadRef ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
