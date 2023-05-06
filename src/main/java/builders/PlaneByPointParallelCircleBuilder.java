package builders;

import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Checker;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PlaneByPointParallelCircleBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointParalCircle";

  public PlaneByPointParallelCircleBuilder() {
  }

  public PlaneByPointParallelCircleBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointParallelCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointParallelCircleBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
  }
  public PlaneByPointParallelCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_CIRCLE, "Параллельный круг", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
     i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      Circle3d circle = anchors.get(getValueAsString(BLDKEY_CIRCLE)).getDisk();
  if( Checker.isPlaneContainPoint(circle.plane(), point) )
          throw new ExDegeneration("Точка лежит в плоскости окружности!");
      Plane3d plane = Plane3d.planeByPointParalPlane(point, circle.plane());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      //result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
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
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость, проходящая через точку</strong> %s и параллельная плоскости окружности %s",
         ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CIRCLE)));
    } catch (ExNoAnchor  ex) {
      return "<html><strong>Точка или окружность</strong>";
    }
  }
}