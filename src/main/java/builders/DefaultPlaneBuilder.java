package builders;

import bodies.PlaneBody;
import builders.param.BuilderParam;
import editor.Editor;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Builder of the default plane (Oxy).
 *
 * @author alexeev
 */
public class DefaultPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "DefaultPlane";

  public DefaultPlaneBuilder() {
  }

  public DefaultPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public DefaultPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public DefaultPlaneBuilder(String id, String name, JsonObject param){
    super(id, name);
  }

  public DefaultPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    PlaneBody result = new PlaneBody(_id, title(), Plane3d.oXY());
    return result;
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    return "<html><strong>Плоскость Oxy</strong>";
  }
}
