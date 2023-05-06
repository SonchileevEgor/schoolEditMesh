package builders;

import bodies.PlaneBody;
import bodies.RibBody;
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
import geom.Line3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PolyXPlaneBuilder extends BodyBuilder {

  static public final String ALIAS = "PolyXPlane";

  public PolyXPlaneBuilder() {
  }

  public PolyXPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PolyXPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PolyXPlaneBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public PolyXPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
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
      PlaneBody plane = (PlaneBody) bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!plane.exists()) {
        throw new ExLostBody("плоскость");
      }
      i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));
      Line3d planeIntersect = poly.getPoly().plane().intersectionWithPlane(plane.plane());
      ArrayList<Vect3d> intersect1 = poly.getPoly().intersectBoundaryWithLine(planeIntersect);
      if (intersect1.size() < 2) {
        throw new ExGeom("грань и плоскость не пересекаются");
      }
      Rib3d resultRib = new Rib3d(intersect1.get(0), intersect1.get(1));
      RibBody result = new RibBody(_id, resultRib);
      edt.addAnchor(result.A(), result, "A");
      edt.addAnchor(result.B(), result, "B");
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExBadRef | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение грани и плоскости не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Пересечение</strong> грани %s и плоскости %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExBadRef ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
