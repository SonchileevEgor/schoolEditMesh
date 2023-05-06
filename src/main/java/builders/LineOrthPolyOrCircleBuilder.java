package builders;

import bodies.BodyType;
import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Polygon3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class LineOrthPolyOrCircleBuilder extends BodyBuilder  {
  static public final String ALIAS = "LineOrthogonalPolyOrCirc";
   
  public LineOrthPolyOrCircleBuilder() {
  }

  public LineOrthPolyOrCircleBuilder(String id, String name) {
    super(id, name);
  }
  
  public LineOrthPolyOrCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public LineOrthPolyOrCircleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_FACET, param.get(BLDKEY_FACET).asString());
  }
  
  public LineOrthPolyOrCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_FACET, "Ортогональная фигура", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      i_Body facet = bodies.get(getValueAsString(BLDKEY_FACET));
      if(!facet.exists())
        throw new ExLostBody("Грань");
      Plane3d plane = null;
      if (facet.type() == BodyType.CIRCLE) {
        Circle3d circ = (Circle3d) facet.getGeom();
        plane = circ.plane();
      }
      if (facet.type().isPolygon()) {
        Polygon3d poly = (Polygon3d) facet.getGeom();
        plane = poly.plane();
      }
      Line3d line = Line3d.linePerpendicularPlane(plane, anchors.getVect(getValueAsString(BLDKEY_P)));
      LineBody result = new LineBody(_id, title(), line);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody | ExDegeneration ex) {
      if (_exists)
	eh.showMessage("Прямая не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
    result.add(BLDKEY_FACET, getValueAsString(BLDKEY_FACET));
  return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямая</strong>, проходящая через точку %s<br>,"
              + "перпендикулярная плоскости фигуры %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_FACET)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Прямая</strong>";
    }
  }
}
