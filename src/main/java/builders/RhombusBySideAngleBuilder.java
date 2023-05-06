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
import geom.Angle3d;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author rita
 */
public class RhombusBySideAngleBuilder extends BodyBuilder {
  static public final String ALIAS = "RhombusBySideAngle";
  public static final String BLDKEY_ANGLE1 = "ang1";
  public static final String BLDKEY_ANGLE2 = "ang2";

  public RhombusBySideAngleBuilder() {
  }

  public RhombusBySideAngleBuilder(String id, String name) {
    super(id, name);
  }

  public RhombusBySideAngleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RhombusBySideAngleBuilder (String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_ANGLE1, param.get(BLDKEY_ANGLE1).asDouble());
    setValue(BLDKEY_ANGLE2, param.get(BLDKEY_ANGLE2).asDouble());
  }

  public RhombusBySideAngleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина ромба", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая вершина ромба", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE1, "Угол между сторонами", BuilderParamType.DOUBLE_POSITIVE, 31);
    addParam(BLDKEY_ANGLE2, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.DOUBLE_POSITIVE, 30);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d A = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d B = anchors.getVect(getValueAsString(BLDKEY_B));
      double ang1 = getValueAsDouble(BLDKEY_ANGLE1);
      double ang2 = getValueAsDouble(BLDKEY_ANGLE2);
      Polygon3d poly = Polygon3d.rhombusBy2PntsAngAng(A, B, ang1, ang2);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("1", anchors.get(getValueAsString(BLDKEY_B)).id());
      edt.addAnchor(result.polygon().points().get(2), result, String.valueOf(2));
      edt.addAnchor(result.polygon().points().get(3), result, String.valueOf(3));
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Ромб не построен: " + ex.getMessage(),error.Error.WARNING);
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
    result.add(BLDKEY_ANGLE1, getValueAsDouble(BLDKEY_ANGLE1));
    result.add(BLDKEY_ANGLE2, getValueAsDouble(BLDKEY_ANGLE2));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Ромб,</strong><br>построенный по вершинам %s, %s<br>"
              + "и углу %s рад.",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ANGLE1)), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Ромб</strong>";
    }
  }
}
