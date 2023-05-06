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
 *
 * @author rita
 */
public class ParallelogramBuilder extends BodyBuilder {
  static public final String ALIAS = "Parallelogram";

  public ParallelogramBuilder() {
  }

  public ParallelogramBuilder(String id, String name) {
    super(id, name);
  }

  public ParallelogramBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ParallelogramBuilder (String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_C, param.get(BLDKEY_C).asString());
  }

  public ParallelogramBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина параллелограмма", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, "Вторая вершина параллелограмма", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, "Третья вершина параллелограмма", BuilderParamType.ANCHOR, 100);
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
      Polygon3d poly = Polygon3d.parallelogramBy3Pnts(A, B, C);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("1", anchors.get(getValueAsString(BLDKEY_B)).id());
      result.addAnchor("2", anchors.get(getValueAsString(BLDKEY_C)).id());
      edt.addAnchor(result.polygon().points().get(3), result, "3");
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Параллелограмм не построен: " + ex.getMessage(),error.Error.WARNING);
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
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Параллелограмм,</strong><br>построенный по вершинам %s, %s, %s.",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Параллелограмм</strong>";
    }
  }
}
