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
import geom.Checker;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PlaneByLineParallelCircleBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineParalCircle";

  public PlaneByLineParallelCircleBuilder() {
  }

  public PlaneByLineParallelCircleBuilder(String id, String name) {
    super(id, name);
  }


  public PlaneByLineParallelCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByLineParallelCircleBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
  }
  public PlaneByLineParallelCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_CIRCLE, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
     i_AnchorContainer anchors = edt.anchors();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      Circle3d circle = anchors.get(getValueAsString(BLDKEY_CIRCLE)).getDisk();
       if (!line.exists())
        throw new ExLostBody("прямая");
if( !Checker.isLineParallelPlane(line.line(), circle.plane()) )
          throw new ExDegeneration("Прямая не параллельна плоскости окружности!");
      Plane3d plane = Plane3d.planeByPointParalPlane(line.pnt(), circle.plane());
      PlaneBody result = new PlaneBody(_id, title(), plane);
      //result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch( ExNoBody | ExNoAnchor | ExDegeneration ex ){
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
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s и параллельная плоскости окружности %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CIRCLE)));
    } catch ( ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
