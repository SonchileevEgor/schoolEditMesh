package builders;

import bodies.PlaneBody;
import bodies.PointBody;
import bodies.PolygonBody;
import bodies.RibBody;
import bodies.OctahedronBody;
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
import geom.Polygon3d;
import geom.Rib3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs section of the octahedron
 * @author Vladislav
 */
public class OctahedronSectionBuilder extends BodyBuilder {
  static public final String ALIAS = "OctahedronSection";

  public OctahedronSectionBuilder() {
  }

  public OctahedronSectionBuilder(String id, String name) {
    super(id, name);
  }

  public OctahedronSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public OctahedronSectionBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_OCTAHEDRON, param.get(BLDKEY_OCTAHEDRON).asString());
  }

  public OctahedronSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE, BuilderParam.SECTION_PLANE_ALIAS, BuilderParamType.BODY, 60);
    addParam(BLDKEY_OCTAHEDRON, "Октаэдр", BuilderParamType.BODY, 61);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody p = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!p.exists())
        throw new ExLostBody("плоскость");

      OctahedronBody o = (OctahedronBody)bodies.get(getValueAsString(BLDKEY_OCTAHEDRON));
      if (!o.exists())
        throw new ExLostBody("октаэдр");

      Polygon3d poly = o.octahedron().sectionByPlane(p.plane());
      if (poly.points().size() == 1) {
        PointBody result = new PointBody(_id, poly.points().get(0));
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      } else if (poly.points().size() == 2) {
        Rib3d rib = new Rib3d(poly.points().get(0), poly.points().get(1));
        RibBody result = new RibBody(_id, rib);
        edt.addAnchor(result.A() , result, "A");
        edt.addAnchor(result.B() , result, "B");
        result.addRibs(edt);
        _exists = true;
        return result;
      }
      PolygonBody result = o.section(_id, p);
      for (int j = 0; j < result.polygon().vertNumber(); j++){
        edt.addAnchor(result.polygon().points().get(j), result, String.valueOf(j), title() + "_" + String.valueOf(j + 1));
      }
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef | ExZeroDivision ex ){
      if (_exists)
				eh.showMessage("Сечение октаэдра не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_OCTAHEDRON, getValueAsString(BLDKEY_OCTAHEDRON));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong> октаэдра %s плоскостью %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_OCTAHEDRON)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение октаэдра</strong>";
    }
  }
}
