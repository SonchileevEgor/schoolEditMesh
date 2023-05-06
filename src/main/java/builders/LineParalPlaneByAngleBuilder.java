package builders;

import bodies.BodyType;
import bodies.LineBody;
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
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Polygon3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *Прямая, проходящая через точку параллельно плоскости по углу
 * между направляющим вектором прямой и вектором в плоскости (соединяющим первую и вторую точки плоскости)
 * @author Elena
 */
public class LineParalPlaneByAngleBuilder extends BodyBuilder {
  static public final String ALIAS = "LineParalPlaneByAngle";

  public LineParalPlaneByAngleBuilder() {
  }

  public LineParalPlaneByAngleBuilder(String id, String name) {
    super(id, name);
  }

  public LineParalPlaneByAngleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineParalPlaneByAngleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public LineParalPlaneByAngleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_PLANE, "Параллельная плоскость", BuilderParamType.BODY);
    addParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      Plane3d pl;
      i_Body facet = bodies.get(getValueAsString(BLDKEY_PLANE));
      if(!facet.exists())
        throw new ExLostBody("Плоскость");

      if (facet.type() == BodyType.CIRCLE) {
        Circle3d circ = (Circle3d) facet.getGeom();
        pl = circ.plane();
      } else
      if (facet.type().isPolygon()) {
        Polygon3d poly = (Polygon3d) facet.getGeom();
        pl = poly.plane();
      } else {
      pl = (Plane3d)facet.getGeom();
      }

      Line3d line = Line3d.lineParalPlaneByAngle(pl, anchors.getVect(getValueAsString(BLDKEY_P)), (double)getValue(BLDKEY_ANGLE));
      LineBody result = new LineBody(_id, title(), line);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Прямая не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
   result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    result.add(BLDKEY_ANGLE, (double)getValue(BLDKEY_ANGLE));
  return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямая</strong>, проходящая через точку %s<br>параллельная плоскости %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Прямая</strong>";
    }
  }


}
