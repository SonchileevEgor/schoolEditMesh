package builders;

// Plane builder.
import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "Plane";

  public PlaneBuilder() {
  }

  public PlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_NORMAL, new Vect3d(param.get("X").asDouble(),
                                       param.get("Y").asDouble(),
                                       param.get("Z").asDouble()));
  }

  public PlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Точка плоскости", BuilderParamType.ANCHOR);
    addParam(BLDKEY_NORMAL, "Вектор нормали", BuilderParamType.VECT);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BLDKEY_A);
      Vect3d a = anchors.getVect(aID);
      Plane3d plane = new Plane3d((Vect3d)getValue(BLDKEY_NORMAL), a);
      PlaneBody result = new PlaneBody(_id, title(), plane);
      result.addAnchor(PlaneBody.BODY_KEY_A, aID);
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Плоскость не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add("X", getValueAsVect(BLDKEY_NORMAL).x());
    result.add("Y", getValueAsVect(BLDKEY_NORMAL).y());
    result.add("Z", getValueAsVect(BLDKEY_NORMAL).z());

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, построенная по точке"
          + " %s<br> и вектору нормали %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              getValueAsVect(BLDKEY_NORMAL).toString(precision, ctrl.getScene().is3d()));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
};
