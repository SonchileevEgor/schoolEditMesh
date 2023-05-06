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
 * @author Куганский
 */
public class IsoscelesTrapezeBuilder extends BodyBuilder {
  static public final String ALIAS = "IsoscelesTrapeze";

  public IsoscelesTrapezeBuilder() {
  }

  public IsoscelesTrapezeBuilder(String id, String name) {
    super(id, name);
  }
  
  public IsoscelesTrapezeBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public IsoscelesTrapezeBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public IsoscelesTrapezeBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BuilderParam.KEY_A).asString());
    addB(param.get(BuilderParam.KEY_B).asString());
    addC(param.get(BuilderParam.KEY_C).asString());
  }

  public final void addA(String id) {
    setValue(BuilderParam.KEY_A, id);
  }

  public final void addB(String id) {
    setValue(BuilderParam.KEY_B, id);
  }

  public final void addC(String id) {
    setValue(BuilderParam.KEY_C, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BuilderParam.KEY_A, BuilderParam.VERT_1_BASE_1_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BuilderParam.KEY_B, BuilderParam.VERT_2_BASE_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BuilderParam.KEY_C, BuilderParam.VERT_BASE_2_ALIAS, BuilderParamType.ANCHOR, 100);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BuilderParam.KEY_A);
      String bID = getValueAsString(BuilderParam.KEY_B);
      String cID = getValueAsString(BuilderParam.KEY_C);

      Vect3d a = anchors.getVect(aID);
      Vect3d b = anchors.getVect(bID);
      Vect3d c = anchors.getVect(cID);

      Polygon3d poly = Polygon3d.isoscelesTrapezeBy3Pnts(a, b, c);
      PolygonBody result = new PolygonBody(_id, poly.points());

      result.addAnchor("0", aID);
      result.addAnchor("1", bID);
      result.addAnchor("2", cID);

      edt.addAnchor(result.polygon().points().get(3), result, String.valueOf(3));

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Трапеция не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
        return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BuilderParam.KEY_A, getValueAsString(BuilderParam.KEY_A));
    result.add(BuilderParam.KEY_B, getValueAsString(BuilderParam.KEY_B));
    result.add(BuilderParam.KEY_C, getValueAsString(BuilderParam.KEY_C));
    return result;
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Равнобокая трапеция,</strong><br>построенная по вершинам %s, %s, %s",
        ctrl.getAnchorTitle(getValueAsString(BuilderParam.KEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BuilderParam.KEY_B)),
        ctrl.getAnchorTitle(getValueAsString(BuilderParam.KEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Трапеция</strong>";
    }
  }
}
