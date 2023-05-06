package builders;

import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *Plane through the point, parallel given plane
 * @author
 */
public class PlaneByPointParallelPolygonBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointParalPoly";
   
  public PlaneByPointParallelPolygonBuilder() {
  }

  public PlaneByPointParallelPolygonBuilder(String id, String name) {
    super(id, name);
  }
  
  public PlaneByPointParallelPolygonBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneByPointParallelPolygonBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
  }
  
  public PlaneByPointParallelPolygonBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR);
    addParam(BLDKEY_POLYGON, "Параллельный многоугольник", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));
      if( Checker.isPlaneContainPoint(poly.getPoly().plane(), point) )
        throw new ExDegeneration("Точка лежит в плоскости многоугольника!");
      Plane3d plane = Plane3d.planeByPointParalPlane(point, poly.getPoly().plane());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch( ExNoAnchor | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость, проходящая через точку</strong> %s и параллельная многоугольнику %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка или многоугольник</strong>";
    }
  }  
}
