package builders;

import bodies.LineBody;
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
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * constructs plane through point and line 
 * @author Elena
 */
public class PlaneByPointAndLineBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointAndLine";
   
  public PlaneByPointAndLineBuilder() {
  }

  public PlaneByPointAndLineBuilder(String id, String name) {
    super(id, name);
  }
  
  public PlaneByPointAndLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneByPointAndLineBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
  }
  
  public PlaneByPointAndLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR);
    addParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }   
  
  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try{
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("Прямая");
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      Plane3d plane = new Plane3d(point, line.pnt(),line.pnt2());
      i_Body result= new PlaneBody(_id, title(), plane);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    }catch (ExNoAnchor|ExNoBody|ExDegeneration ex) {
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
     result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
     return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s и "
              + "прямую %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}