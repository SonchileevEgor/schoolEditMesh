package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import bodies.RayBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
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
public class PlaneByLineOrthRayBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineOrthRay";

  public PlaneByLineOrthRayBuilder() {
  }

  public PlaneByLineOrthRayBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByLineOrthRayBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByLineOrthRayBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_RAY, param.get(BLDKEY_RAY).asString());
  }

  public PlaneByLineOrthRayBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
     addParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, 61);
     addParam(BLDKEY_RAY, "Ортогональный луч", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try{
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("Прямая");
      RayBody ray = (RayBody)bodies.get(getValueAsString(BLDKEY_RAY));
      Plane3d plane = Plane3d.planeByPointOrthLine(line.pnt(), ray.ray().line());
      i_Body result = null;
      if (plane.containsLine(line.line())) {
      result = new PlaneBody(_id, title(), plane);
      }
      _exists = true;
      return result;
    }catch (ExNoBody | ExDegeneration ex) {
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
     result.add(BLDKEY_RAY, getValueAsString(BLDKEY_RAY));
     return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через прямую %s и перпендиклярная "
              + "лучу %s .",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RAY)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}