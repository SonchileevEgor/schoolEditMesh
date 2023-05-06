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
import geom.Checker;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class PlaneByLineParallelPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineParalPlane";

  public PlaneByLineParallelPlaneBuilder() {
  }

  public PlaneByLineParallelPlaneBuilder(String id, String name) {
    super(id, name);
  }
   
  
  public PlaneByLineParallelPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneByLineParallelPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }
  public PlaneByLineParallelPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      PlaneBody pl = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!pl.exists())
        throw new ExLostBody("плоскость");
      if (!line.exists())
        throw new ExLostBody("прямая");
      if( !Checker.isLineParallelPlane(line.line(), pl.plane()) )
        throw new ExDegeneration("Прямая не параллельна плоскости!");
      Plane3d plane = Plane3d.planeByPointParalPlane(line.pnt(), pl.plane());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      //result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch( ExNoBody | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s и параллельная плоскости %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch ( ExNoBody ex) {
      return "<html><strong>Прямая или плоскость</strong>";
    }
  }  
}
