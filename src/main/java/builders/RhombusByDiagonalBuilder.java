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
import util.Util;

/**
 *
 * @author rita
 */
public class RhombusByDiagonalBuilder extends BodyBuilder {
  static public final String ALIAS = "RhombusByDiagonal";
  
  public static final String BLDKEY_DIAG_LENGTH = "diagLength";

  public RhombusByDiagonalBuilder() {
  }

  public RhombusByDiagonalBuilder(String id, String name) {
    super(id, name);
  }

  public RhombusByDiagonalBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RhombusByDiagonalBuilder (String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_C, param.get(BLDKEY_C).asString());
    setValue(BLDKEY_DIAG_LENGTH, param.get(BLDKEY_DIAG_LENGTH).asDouble());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public RhombusByDiagonalBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_DIAG_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, BuilderParam.VERT_2_DIAG_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_DIAG_LENGTH, BuilderParam.LENGTH_OF_DIAG_ALIAS, BuilderParamType.DOUBLE_POSITIVE, 31);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.DOUBLE_POSITIVE, 30);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d A = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d C = anchors.getVect(getValueAsString(BLDKEY_C));
      double diagLength = (double)getValue(BLDKEY_DIAG_LENGTH);
      double angle = (double)getValue(BLDKEY_ANGLE);

      Polygon3d poly = Polygon3d.rhombusBy2PntsDiagLengthAng(A, C, diagLength, angle);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("2", anchors.get(getValueAsString(BLDKEY_C)).id());
      edt.addAnchor(result.polygon().points().get(1), result, String.valueOf(1));
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
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_DIAG_LENGTH, (double)getValue(BLDKEY_DIAG_LENGTH));
    result.add(BLDKEY_ANGLE, (double)getValue(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Ромб,</strong><br>построенный по вершинам %s, %s <br> и длине диагонали %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)),
        Util.valueOf(getValueAsDouble(BLDKEY_DIAG_LENGTH), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Ромб</strong>";
    }
  }
}
