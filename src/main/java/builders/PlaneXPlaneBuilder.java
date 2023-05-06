package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Finds intersection of two planes, if exists.
 * 
 * @author Vladislav Alexeev
 */
public class PlaneXPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneXPlane";
  
  public static final String BLDKEY_PLANE1 = "p1";
  public static final String BLDKEY_PLANE2 = "p2";
   
  public PlaneXPlaneBuilder() {
  }

  public PlaneXPlaneBuilder(String id, String name) {
    super(id, name);
  }
  
  public PlaneXPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneXPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_PLANE1, param.get(BLDKEY_PLANE1).asString());
    setValue(BLDKEY_PLANE2, param.get(BLDKEY_PLANE2).asString());
  }
  public PlaneXPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE1, "Первая плоскость", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE2, "Вторая плоскость", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody p1 = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE1));
      PlaneBody p2 = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE2));
      if (!p1.exists() || !p2.exists())
        throw new ExLostBody("плоскость");
      
      Line3d intersect = p1.plane().intersectionWithPlane(p2.plane());
      LineBody result = new LineBody(_id, title(), intersect);
      _exists = true;
      return result;
    } catch( ExNoBody | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Пересечение плоскостей не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PLANE1, getValueAsString(BLDKEY_PLANE1));
    result.add(BLDKEY_PLANE2, getValueAsString(BLDKEY_PLANE2));
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Пересечения</strong> плоскостей %s и %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE1)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Прямая</strong>";
    }
  }  
}
