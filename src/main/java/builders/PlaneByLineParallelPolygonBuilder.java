package builders;

import bodies.LineBody;
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
public class PlaneByLineParallelPolygonBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineParalPoly";

  public PlaneByLineParallelPolygonBuilder() {
  }

  public PlaneByLineParallelPolygonBuilder(String id, String name) {
    super(id, name);
  }
   
  
  public PlaneByLineParallelPolygonBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public PlaneByLineParallelPolygonBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
  }
  
  public PlaneByLineParallelPolygonBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));
      if( !Checker.isLineParallelPlane(line.line(), poly.getPoly().plane()) )
        throw new ExDegeneration("Прямая не параллельна плоскости многоугольника!");
      if (!line.exists())
        throw new ExLostBody("прямая");
      Plane3d plane = Plane3d.planeByPointParalPlane(line.pnt(), poly.getPoly().plane());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    } catch( ExNoBody | ExNoAnchor| ExDegeneration ex ){
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
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s и параллельная многоугольнику %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch ( ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Прямая или многоугольник</strong>";
    }
  }  
}

