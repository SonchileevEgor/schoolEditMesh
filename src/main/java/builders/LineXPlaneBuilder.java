package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Finds intersection of line and plane, if exists.
 *
 * @author Vladislav Alexeev
 */
public class LineXPlaneBuilder extends BodyBuilder {

  static public final String ALIAS = "LineXPlane";

  public LineXPlaneBuilder() {
  }
  
  public LineXPlaneBuilder(String name) {
    super(name);
  }

  public LineXPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public LineXPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineXPlaneBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public LineXPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, 60);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 61);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody line = (LineBody) bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists()) {
        throw new ExLostBody("прямая");
      }

      PlaneBody plane = (PlaneBody) bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!plane.exists()) {
        throw new ExLostBody("плоскость");
      }

      Vect3d intersect = line.line().intersectionWithPlane(plane.plane());
      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch (ExNoBody | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Пересечение прямой и плоскости не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PointBody(_id);
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
      return String.format("<html><strong>Точка пересечения</strong> прямой %s и плоскости %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
