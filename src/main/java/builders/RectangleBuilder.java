package builders;

import bodies.PolygonBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Построение прямоугольника по двум точкам на стороне,
 * высоте и углу поворота в пространстве.
 * @author rita
 */
public class RectangleBuilder extends BodyBuilder {
  static public final String ALIAS = "Rectangle";
  
  public RectangleBuilder() {
  }

  public RectangleBuilder(String name) {
    super(name);
  }

  public RectangleBuilder(String id, String name) {
    super(id, name);
  }

  public RectangleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RectangleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addHeight(param.get(BLDKEY_HEIGHT).asDouble());
    addRotationAngle(param.get(BLDKEY_ANGLE).asDouble());
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addHeight(double height) {
    setValue(BLDKEY_HEIGHT, height);
  }

  public final void addRotationAngle(double angle) {
    setValue(BLDKEY_ANGLE, angle);
  }

  public RectangleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_HEIGHT, "Высота", BuilderParamType.DOUBLE_POSITIVE, 90);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.DOUBLE_POSITIVE, 80);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      double height = getValueAsDouble(BLDKEY_HEIGHT);
      double angle = getValueAsDouble(BLDKEY_ANGLE);
      Polygon3d poly = Polygon3d.rectangleBy2PntsAngle(a, b, height, angle);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("1", anchors.get(getValueAsString(BLDKEY_B)).id());
      edt.addAnchor(result.polygon().points().get(2), result, "2");
      edt.addAnchor(result.polygon().points().get(3), result, "3");
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Прямоугольник не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_HEIGHT, getValueAsDouble(BLDKEY_HEIGHT));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямоугольник</strong><br>"
              + "с вершинами %s, %s <br> и высотой %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              Util.valueOf(getValueAsDouble(BLDKEY_HEIGHT), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Прямоугольник</strong>";
    }
  }
}
