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
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Лютенков
 */
public class PlaneByPolygonBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPolygon";
  
  public PlaneByPolygonBuilder() {
  }

  public PlaneByPolygonBuilder(String id, String name) {
    super(id, name);
  }
  
  public PlaneByPolygonBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneByPolygonBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
  }
  
  public PlaneByPolygonBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor p = anchors.get(getValueAsString(BLDKEY_POLYGON));
      Plane3d plane = p.getPoly().plane();
      PlaneBody result = new PlaneBody(_id, title(), plane);
      
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong> многоугольника %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
