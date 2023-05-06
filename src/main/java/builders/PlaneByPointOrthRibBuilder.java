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
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class PlaneByPointOrthRibBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByPointOrthRib";

  public PlaneByPointOrthRibBuilder() {
  }

  public PlaneByPointOrthRibBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByPointOrthRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByPointOrthRibBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
  }

  public PlaneByPointOrthRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_RIB, "Ортогональный отрезок", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try{
      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      Plane3d plane = Plane3d.planeByPointOrthLine(point, rib.getRib().line());
      i_Body result= new PlaneBody(_id, title(), plane);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    }catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: ".concat(ex.getMessage()),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
     result.add("rib", getValueAsString("rib"));
     return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через точку %s и перпендиклярная "
              + "отрезку %s .",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
