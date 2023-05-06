package builders;

import bodies.BodyType;
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
import static geom.Checker.eps;
import geom.Circle3d;
import geom.ExGeom;
import geom.Line3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class TangentPointSphereFacetBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentPointSphereFacet";

  public TangentPointSphereFacetBuilder (String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
    setValue(BLDKEY_FACET, param.get(BLDKEY_FACET).asString());
  }

  public TangentPointSphereFacetBuilder() {
  }

  public TangentPointSphereFacetBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentPointSphereFacetBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY, 61);
    addParam(BLDKEY_FACET, BuilderParam.POLY_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      SphereBody sph = (SphereBody)bodies.get(getValueAsString(BLDKEY_SPHERE));
      i_Body facet = bodies.get(getValueAsString(BLDKEY_FACET));

      if(!sph.exists())
        throw new ExLostBody("Сфера");
      if(!facet.exists())
        throw new ExLostBody("Грань");

      Vect3d tangPoint = null;
      if (facet.type() == BodyType.CIRCLE) {
        Circle3d circ = (Circle3d)facet.getGeom();
        if (Math.abs(circ.plane().distFromPoint(sph.sphere().center()) - sph.sphere().radius()) > eps()) {
          throw new ExGeom("Плоскость, содержащая окружность, не касается сферы");
        }
        tangPoint = sph.sphere().tangentPointSphereCircle((Circle3d) facet.getGeom());
      }
      if (facet.type().isPolygon()) {
        Polygon3d poly = (Polygon3d)facet.getGeom();
        if (Math.abs(poly.plane().distFromPoint(sph.sphere().center()) - sph.sphere().radius()) > eps()) {
          throw new ExGeom ("Плоскость, содержащая многоугольник, не касается сферы");
        }
        tangPoint = sph.sphere().tangentPointSpherePoly((Polygon3d) facet.getGeom());
      }
      if (facet.type() == BodyType.RIB) {
        Rib3d rib = (Rib3d)facet.getGeom();
        if (Math.abs(rib.line().distFromPoint(sph.sphere().center()) - sph.sphere().radius()) > eps()) {
          throw new ExGeom("Прямая, содержащая отрезок, не касается сферы");
        }
        tangPoint = sph.sphere().tangentPointSphereRib((Rib3d) facet.getGeom());
      }
      if (facet.type() == BodyType.LINE) {
        tangPoint = sph.sphere().tangentPointSphereLine((Line3d) facet.getGeom());
      }
      if (facet.type() == BodyType.RAY) {
        Ray3d ray = (Ray3d)facet.getGeom();
        if (Math.abs(ray.line().distFromPoint(sph.sphere().center()) - sph.sphere().radius()) > eps()) {
          throw new ExGeom ("Прямая, содержащая луч, не касается сферы");
        }
        tangPoint = sph.sphere().tangentPointSphereRay((Ray3d) facet.getGeom());
      }
      PointBody result = new PointBody(_id, tangPoint);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch(ExBadRef | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Касательная не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    result.add(BLDKEY_FACET, getValueAsString(BLDKEY_FACET));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> пересечения сферы %s и грани (отрезка, луча) %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_FACET)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
