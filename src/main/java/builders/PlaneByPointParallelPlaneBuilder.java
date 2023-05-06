package builders;

import bodies.PlaneBody;
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
import geom.Checker;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Plane through the point, parallel given plane
 * @author
 */
public class PlaneByPointParallelPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointParalPlane";

  public PlaneByPointParallelPlaneBuilder() {
  }

  public PlaneByPointParallelPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointParallelPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointParallelPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }
  public PlaneByPointParallelPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR);
    addParam(BLDKEY_PLANE, "Параллельная плоскость", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      PlaneBody pl = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!pl.exists())
        throw new ExLostBody("плоскость");
      if( Checker.isPlaneContainPoint(pl.plane(), point) )
        throw new ExDegeneration("Точка лежит в плоскости!");
      Plane3d plane = Plane3d.planeByPointParalPlane(point, pl.plane());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      //result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch( ExNoAnchor | ExNoBody | ExDegeneration ex ){
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
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость, проходящая через точку</strong> %s и параллельная плоскости %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Точка или плоскость</strong>";
    }
  }
}
