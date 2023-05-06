package builders;

import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Rib3d;
import geom.Triang3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author rita
 */
public class RectTriangleBuilder extends BodyBuilder {
  static public final String ALIAS = "RectTriangle";

  public RectTriangleBuilder() {
  }

  public RectTriangleBuilder(String id, String name) {
    super(id, name);
  }

  public RectTriangleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RectTriangleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_HEIGHT, param.get(BLDKEY_HEIGHT).asDouble());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public RectTriangleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Вершина острого угла", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вершина прямого угла", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_HEIGHT, "Длина катета", BuilderParamType.DOUBLE_POSITIVE, 31);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.DOUBLE_POSITIVE, 30);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d A = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d B = anchors.getVect(getValueAsString(BLDKEY_B));
      double height = (double) getValue(BLDKEY_HEIGHT);
      double angle = (double) getValue(BLDKEY_ANGLE);

      Triang3d triang = Triang3d.rectTriangleBy2PntsAngle(A, B, height, angle);
      TriangleBody result = new TriangleBody(_id, triang);
      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());
      edt.addAnchor(result.C(), result, "C");
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Прямоугольный треугольник не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new TriangleBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_HEIGHT, (double) getValue(BLDKEY_HEIGHT));
    result.add(BLDKEY_ANGLE, (double) getValue(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямоугольный теругольник</strong><br> с вершинами %s, %s <br> и длиной катета %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              Util.valueOf(getValueAsDouble(BLDKEY_HEIGHT), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Прямоугольный треугольник</strong>";
    }
  }
}
