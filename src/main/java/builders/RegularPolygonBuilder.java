package builders;

import bodies.PolygonBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
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

/**
 * Constructs a regular polygon by two consequent vertices and the plane
 *
 * @author alexeev
 */
public class RegularPolygonBuilder extends BodyBuilder {
  static public final String ALIAS = "RegularPolygon";

  public RegularPolygonBuilder() {
  }

  public RegularPolygonBuilder(String id, String name) {
    super(id, name);
  }

  public RegularPolygonBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RegularPolygonBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
    setValue(BLDKEY_POINT_NUM, param.get(BLDKEY_POINT_NUM).asInt());
  }

  public RegularPolygonBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
    addParam(BLDKEY_POINT_NUM, "Количество вершин", BuilderParamType.INT);
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
      double angle = (double) getValue(BLDKEY_ANGLE);

      Polygon3d poly = Polygon3d.regPolygonByTwoPoints(A, B, (int) getValue(BLDKEY_POINT_NUM), angle);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("1", anchors.get(getValueAsString(BLDKEY_B)).id());
      for (int j = 2; j < result.polygon().vertNumber(); j++) {
        edt.addAnchor(result.polygon().points().get(j), result, String.valueOf(j));
      }

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Многоугольник не построен: " + ex.getMessage(), error.Error.WARNING);
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
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));
    result.add(BLDKEY_POINT_NUM, getValueAsInt(BLDKEY_POINT_NUM));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Правильный %s-угольник</strong><br> со стороной основания %s%s",
              getValue(BLDKEY_POINT_NUM),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Правильный многоугольник</strong>";
    }
  }
}
