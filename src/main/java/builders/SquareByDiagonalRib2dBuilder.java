package builders;

import bodies.PolygonBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
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
 * @author Elena
 */
public class SquareByDiagonalRib2dBuilder extends BodyBuilder {
  static public final String ALIAS = "SquareByDiagonal";
  public static final String BLDKEY_DIAGONAL = "diag";

  public SquareByDiagonalRib2dBuilder() {
  }

  public SquareByDiagonalRib2dBuilder(String id, String name) {
    super(id, name);
  }

  public SquareByDiagonalRib2dBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SquareByDiagonalRib2dBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_DIAGONAL, param.get(BLDKEY_DIAGONAL).asString());
  }

  public SquareByDiagonalRib2dBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DIAGONAL, "Диагональ", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor diag = anchors.get(getValueAsString(BLDKEY_DIAGONAL));
      Vect3d pA = diag.getRib().a();
      Vect3d pC = diag.getRib().b();
      Polygon3d poly = Polygon3d.square2dBy2PntsOfDiag(pA, pC);
      PolygonBody result = new PolygonBody(_id, poly.points());
      result.addAnchor("0", diag.arrayIDs().get(0));//!!!!
      result.addAnchor("2", diag.arrayIDs().get(1));
      edt.addAnchor(result.polygon().points().get(1), result, String.valueOf(1));
      edt.addAnchor(result.polygon().points().get(3), result, String.valueOf(3));

      result.addRibs(edt);
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
    result.add(BLDKEY_DIAGONAL, getValueAsString(BLDKEY_DIAGONAL));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Квадрат</strong> с диагональю %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_DIAGONAL)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Квадрат</strong>";
    }
  }
}
