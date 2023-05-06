package builders;

import bodies.PolygonBody;
import static builders.BodyBuilder.BLDKEY_RADIUS;
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
 * Trapeze builder.
 * By three points and length of base.
 *
 * @author rita
 */
public class TrapezeBuilder extends BodyBuilder {
  static public final String ALIAS = "Trapeze";
  static public final String BLDKEY_BASE_LENGTH = "baseLength";

  public TrapezeBuilder() {
  }

  public TrapezeBuilder(String id, String name) {
    super(id, name);
  }

  public TrapezeBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TrapezeBuilder (String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addC(param.get(BLDKEY_C).asString());
    addBaseLength(param.get(BLDKEY_BASE_LENGTH).asDouble());
  }

  public TrapezeBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addC(String id) {
    setValue(BLDKEY_C, id);
  }

  public final void addBaseLength(double value) {
    setValue(BLDKEY_BASE_LENGTH, value);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_BASE_1_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, BuilderParam.VERT_2_BASE_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, BuilderParam.VERT_BASE_2_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_BASE_LENGTH, BuilderParam.LENGTH_OF_BASE_ALIAS, BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d A = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d B = anchors.getVect(getValueAsString(BLDKEY_B));
      Vect3d C = anchors.getVect(getValueAsString(BLDKEY_C));
      double baseLength = getValueAsDouble(BLDKEY_BASE_LENGTH);
      Polygon3d poly = Polygon3d.trapezeBy3PntsBaseLength(A, B, C, baseLength);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("1", anchors.get(getValueAsString(BLDKEY_B)).id());
      result.addAnchor("2", anchors.get(getValueAsString(BLDKEY_C)).id());
      edt.addAnchor(result.polygon().points().get(3), result, String.valueOf(3));
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Трапеция не построена: " + ex.getMessage(), error.Error.WARNING);
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
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_BASE_LENGTH, getValueAsDouble(BLDKEY_BASE_LENGTH));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Трапеция,</strong><br>построенная по вершинам %s, %s, %s,"
              + "<br>с длиной основания %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)),
        Util.valueOf(getValueAsDouble(BLDKEY_BASE_LENGTH), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Трапеция</strong>";
    }
  }
}