package builders;

import bodies.HexagonalPrismBody;
import bodies.PlaneBody;
import bodies.PointBody;
import bodies.PolygonBody;
import bodies.RibBody;
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
 * Constructs section of the hexagonal prism.
 * @author Vladislav
 */
public class HexPrismSectionBuilder extends BodyBuilder {
  static public final String ALIAS = "HexagonalPrismSection";
  
  public HexPrismSectionBuilder() {
  }

  public HexPrismSectionBuilder(String id, String name) {
    super(id, name);
  }

  public HexPrismSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public HexPrismSectionBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_PRISM, param.get(BLDKEY_PRISM).asString());
  }

  public HexPrismSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PRISM, "шестиугольная призма", BuilderParamType.BODY, 61);
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

      HexagonalPrismBody o = (HexagonalPrismBody)bodies.get(getValueAsString(BLDKEY_PRISM));
      if (!o.exists())
        throw new ExLostBody("шестиугольная призма");

      Polygon3d poly = o.hexagonalprism().sectionByPlane(p.plane());
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
      if (_exists) {
        eh.showMessage("Сечение шестиугольной призмы не построено: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PRISM, getValueAsString(BLDKEY_PRISM));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong><br>"
              + "шестиугольной призмы %s<br> плоскостью %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PRISM)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение шестиугольной призмы</strong>";
    }
  }
}
