package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * constructs plane through a line parallel another line; given lines are skew
 * @author Elena
 */
public class PlaneBySkewLinesBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneBySkewLines";
  public static final String BLDKEY_LINE1 = "line1";
  public static final String BLDKEY_LINE2 = "line2";
   
  public PlaneBySkewLinesBuilder() {
  }

  public PlaneBySkewLinesBuilder(String id, String name) {
    super(id, name);
  }
  
  public PlaneBySkewLinesBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneBySkewLinesBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE1, param.get(BLDKEY_LINE1).asString());
    setValue(BLDKEY_LINE2, param.get(BLDKEY_LINE2).asString());
  }
  
  public PlaneBySkewLinesBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, 101);
    addParam(BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, 100);
  }

  @Override
  public String alias() { return ALIAS; }   
  
  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try{
      LineBody line1 = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE1));
      if (!line1.exists())
        throw new ExLostBody("Прямая1");
      LineBody line2 = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE2));
      if (!line2.exists())
        throw new ExLostBody("Прямая2");
      Plane3d plane = Plane3d.planeBySkewLines(line1.line(), line2.line());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;      
    }catch (ExNoBody | ExGeom ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE1, getValueAsString(BLDKEY_LINE1));
    result.add(BLDKEY_LINE2, getValueAsString(BLDKEY_LINE2));
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через прямую %s и параллельная "
              + "прямой %s .",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE1)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE2)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
