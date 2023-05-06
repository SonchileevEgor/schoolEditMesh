package builders;

import bodies.CircleBody;
import bodies.PlaneBody;
import bodies.PointBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs section of the sphere
 *
 * @author alexeev
 */
public class SphereSectionBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereSection";

  public SphereSectionBuilder() {
  }

  public SphereSectionBuilder(String id, String name) {
    super(id, name);
  }

  public SphereSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereSectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
  }

  public SphereSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE, BuilderParam.SECTION_PLANE_ALIAS, BuilderParamType.BODY, 60);
    addParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY, 61);
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

      SphereBody s = (SphereBody) bodies.get(getValueAsString(BLDKEY_SPHERE));
      if (!s.exists()) {
        throw new ExLostBody("сфера");
      }

      Vect3d c1 = p.plane().projectionOfPoint(s.getCenter());
      if (Checker.isPointOnSphere(c1, s.sphere())) {
        PointBody result = new PointBody(_id, p.plane().projectionOfPoint(s.getCenter()));
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      } else if (!Checker.isPointInSphere(c1, s.sphere())) {
        throw new ExGeom("плоскость и сфера не пересекаются");
      }

      CircleBody result = s.section(_id, title(), p);
      edt.addAnchor(result.center(), result, "C");
      edt.addAnchor(result.circle(), result.getAnchorID("C"), result, "disk");
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Сечение сферы не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CircleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong> сферы %s плоскостью %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение сферы</strong>";
    }
  }
}
