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
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class PlaneByLineOrthRibBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineOrthRib";

  public PlaneByLineOrthRibBuilder() {
  }

  public PlaneByLineOrthRibBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByLineOrthRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByLineOrthRibBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
  }

  public PlaneByLineOrthRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
     addParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY);
    addParam(BLDKEY_RIB, "Ортогональный отрезок", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try{
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("Прямая");
      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      Plane3d plane = Plane3d.planeByPointOrthLine(line.pnt(), rib.getRib().line());
      i_Body result = null;
      if (plane.containsLine(line.line())) {
      result = new PlaneBody(_id, title(), plane);
      }
      _exists = true;
      return result;
    }catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists)
        util.Fatal.warning("Cannot create plane: ".concat(ex.getMessage()));
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
     result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
     result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
     return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через прямую %s и перпендиклярная "
              + "отрезку %s .",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}