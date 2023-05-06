package builders;

import bodies.RayBody;
import static builders.RayTwoPointsBuilder.ALIAS;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Ray3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Курганский
 */
public class RayByPointBuilder extends BodyBuilder {
   static public final String ALIAS = "RayByPoint";

  public RayByPointBuilder() {
  }

  public RayByPointBuilder(String id, String title) {
    super(id, title);
  }
  
  public RayByPointBuilder(String title) {
    super(title);
  }
  
  public RayByPointBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RayByPointBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public RayByPointBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Вершина луча", BuilderParamType.ANCHOR);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_VALUE);
  }
  
  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      double alpha = (double)getValue(BLDKEY_ALPHA);
      Ray3d ray = Ray3d.ray2dByPoint(a, alpha);
      RayBody result = new RayBody(_id, title(), ray);
      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      //edt.addAnchor(result.pnt2(), result, "B");
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Луч не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RayBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
     try {
      return String.format("<html><strong>Луч</strong> построенный по точке %s и углу %f",
                ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
                getValue(BLDKEY_ALPHA));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Луч</strong>";
    }
  }
  
}
