package builders;


import bodies.CubeBody;
import bodies.PlaneBody;
import bodies.PointBody;
import bodies.PolygonBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Rib3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * constructs section of the cube
 * @author Vladislav
 */
public class CubeSectionBuilder extends BodyBuilder {
  static public final String ALIAS = "CubeSection";

  public CubeSectionBuilder() {
  }

  public CubeSectionBuilder(String id, String name) {
    super(id, name);
  }

  public CubeSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CubeSectionBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_CUBE, param.get(BLDKEY_CUBE).asString());
  }

  public CubeSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CUBE, "Куб", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.SECTION_PLANE_ALIAS, BuilderParamType.BODY, 60);
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

      CubeBody c = (CubeBody)bodies.get(getValueAsString(BLDKEY_CUBE));
      if (!c.exists())
        throw new ExLostBody("куб");

      Polygon3d poly = c.cube().sectionByPlane(p.plane());
      if (poly.points().size() == 1) {
        PointBody result = new PointBody(_id, poly.points().get(0));
        edt.addAnchor(result.point(), result, "P", title());
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
      PolygonBody result = c.section(_id, p);
      for (int j = 0; j < result.polygon().vertNumber(); j++){
        edt.addAnchor(result.polygon().points().get(j), result, String.valueOf(j), title() + "_" + String.valueOf(j + 1));
      }

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExNoBody | ExGeom ex) {
      if (_exists)
				eh.showMessage("Сечение куба не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CUBE, getValueAsString(BLDKEY_CUBE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

 @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong> куба %s плоскостью %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_CUBE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение куба</strong>";
    }
  }
}
