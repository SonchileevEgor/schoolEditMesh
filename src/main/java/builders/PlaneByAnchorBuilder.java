package builders;

import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
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
 * Построение плоскости по якорю (кругу или полигону).
 * 
 * @author alexeev
 */
public class PlaneByAnchorBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByAnchor";
  public static final String BLDKEY_ANCHOR = "anchor";

  public PlaneByAnchorBuilder() {
  }

  public PlaneByAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByAnchorBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_ANCHOR, param.get(BLDKEY_ANCHOR).asString());
  }

  public PlaneByAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ANCHOR, "Якорь", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      String anchorID = getValueAsString(BLDKEY_ANCHOR);
      i_Anchor a = anchors.get(anchorID);
      Plane3d plane;
      PlaneBody result;
      if (a.getAnchorType() == AnchorType.ANC_POLY) {
        plane = a.getPoly().plane();
        result = new PlaneBody(_id, title(), plane);
        result.addAnchor(BLDKEY_ANCHOR, anchorID);
      } else if (a.getAnchorType() == AnchorType.ANC_DISK) {
        plane = a.getDisk().plane();
        result = new PlaneBody(_id, title(), plane);
        result.addAnchor(BLDKEY_ANCHOR, anchorID);
      } else {
        throw new ExNoAnchor("неверный тип якоря");
      }
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExDegeneration ex){
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_ANCHOR, getValueAsString(BLDKEY_ANCHOR));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    return "<html><strong>Плоскость</strong>";
  }

}
