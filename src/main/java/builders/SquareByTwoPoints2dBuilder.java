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

/**
 * Builds square by two points of diagonal
 * @author Elena
 */
public class SquareByTwoPoints2dBuilder extends BodyBuilder {
  static public final String ALIAS = "SquareByTwoPoints";

  public SquareByTwoPoints2dBuilder() {
  }
  
  public SquareByTwoPoints2dBuilder(String name) {
    super(name);
  }

  public SquareByTwoPoints2dBuilder(String id, String name) {
    super(id, name);
  }

  public SquareByTwoPoints2dBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SquareByTwoPoints2dBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_C, param.get(BLDKEY_C).asString());
  }

  public SquareByTwoPoints2dBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_DIAG_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, BuilderParam.VERT_2_DIAG_ALIAS, BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d pA = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d pC = anchors.getVect(getValueAsString(BLDKEY_C));
      Polygon3d poly = Polygon3d.square2dBy2PntsOfDiag(pA,pC);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("2", anchors.get(getValueAsString(BLDKEY_C)).id());
      edt.addAnchor(result.polygon().points().get(1), result, String.valueOf(1));
      edt.addAnchor(result.polygon().points().get(3), result, String.valueOf(3));
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExGeom ex){
      if (_exists) {
        eh.showMessage("Квадрат не построен: " + ex.getMessage(),error.Error.WARNING);
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

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Квадрат</strong> с диагональю, образованной точками %s и %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Квадрат</strong>";
    }
  }
}
