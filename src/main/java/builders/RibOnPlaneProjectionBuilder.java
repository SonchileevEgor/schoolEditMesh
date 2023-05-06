package builders;

import bodies.PlaneBody;
import bodies.PointBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class RibOnPlaneProjectionBuilder extends BodyBuilder {

  static public final String ALIAS = "RibOnPlaneProjection";

  public RibOnPlaneProjectionBuilder() {
  }

  public RibOnPlaneProjectionBuilder(String id, String name) {
    super(id, name);
  }

  public RibOnPlaneProjectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibOnPlaneProjectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public RibOnPlaneProjectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, "Проецируемый отрезок", BuilderParamType.ANCHOR);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      PlaneBody plane = (PlaneBody) bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!plane.exists()) {
        throw new ExLostBody("плоскость");
      }

      //rib is orthogonal to plane - projection is a point
      if (Checker.isCollinear(plane.plane().n(), rib.getRib().line().l())) {
        Vect3d intersect = rib.getRib().line().intersectionWithPlane(plane.plane());
        PointBody result = new PointBody(_id, intersect);
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      } else {
        RibBody result = new RibBody(_id, rib.getRib().projectionOnPlane(plane.plane()));
        edt.addAnchor(result.A(), result, "A");
        edt.addAnchor(result.B(), result, "B");
        result.addRibs(edt);
        _exists = true;
        return result;
      }
    } catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Проекция не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RibBody(_id);
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
      return String.format("<html><strong>Проекция</strong> отрезка %s на плоскость %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
