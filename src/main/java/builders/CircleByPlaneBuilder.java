package builders;

import bodies.CircleBody;
import bodies.PlaneBody;
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
import geom.Circle3d;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Строит окружность по центру,
 * точке на окружности и плоскости, в которой лежит окружность.
 *
 * @author
 */
public class CircleByPlaneBuilder extends BodyBuilder{
  static public final String ALIAS = "CircleByPlane";

  public CircleByPlaneBuilder() {
  }

  public CircleByPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public CircleByPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleByPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public CircleByPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_POINT, "Точка на окружности", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody plane =(PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!plane.exists())
        throw new ExLostBody("плоскость");

      Vect3d normal = plane.normal();
      i_Anchor c = anchors.get(getValueAsString(BLDKEY_CENTER));
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));
      if (!plane.containsPoint(c.getPoint())||!plane.containsPoint(point))
        throw new ExGeom("точка или центр не лежат в плоскости");
      Circle3d disk = new Circle3d(Vect3d.dist(point, c.getPoint()), normal, c.getPoint());
      CircleBody result = new CircleBody(_id, title(), disk);
      result.addAnchor(CircleBody.BODY_KEY_CENTER, anchors.get(getValueAsString(BLDKEY_CENTER)).id());
      edt.addAnchor(disk, c.id(), result, CircleBody.BODY_KEY_DISK);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody | ExGeom ex) {
      if (_exists)
        eh.showMessage("Окружность не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new CircleBody(_id, title());
    }
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    result.add(BLDKEY_CENTER, getValueAsString(BLDKEY_CENTER));
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Окружность</strong> с центром %s и точкой %s,<br>построенная в плоскости %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Окружность</strong>";
    }
  }
}