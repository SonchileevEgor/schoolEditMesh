package builders;

import bodies.PairOfLinesBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.PairOfLines;
import static geom.PairOfLines.PairOfLinesByFourPoints;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Ivan
 */
public class PairOfLinesBuilder extends BodyBuilder {
  static public final String ALIAS = "PairOfLines";
  public static final String BLDKEY_P1 = "p1";
  public static final String BLDKEY_P2 = "p2";
  public static final String BLDKEY_P3 = "p3";
  public static final String BLDKEY_P4 = "p4";

  public PairOfLinesBuilder() {
    super();
  }

  public PairOfLinesBuilder(String id, String name) {
    super(id, name);
  }

  public PairOfLinesBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PairOfLinesBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public PairOfLinesBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P1, param.get(BLDKEY_P1).asString());
    setValue(BLDKEY_P2, param.get(BLDKEY_P2).asString());
    setValue(BLDKEY_P3, param.get(BLDKEY_P3).asString());
    setValue(BLDKEY_P4, param.get(BLDKEY_P4).asString());
  }

  @Override
  public void initParams() {
    super.initParams();
    addParam(BLDKEY_P1, "Первая точка первой прямой", BuilderParamType.ANCHOR, 103);
    addParam(BLDKEY_P2, "Вторая точка первой прямой", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_P3, "Первая точка второй прямой", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_P4, "Вторая точка второй прямой", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d p1 = anchors.getVect(getValueAsString(BLDKEY_P1));
      Vect3d p2 = anchors.getVect(getValueAsString(BLDKEY_P2));
      Vect3d p3 = anchors.getVect(getValueAsString(BLDKEY_P3));
      Vect3d p4 = anchors.getVect(getValueAsString(BLDKEY_P4));
      PairOfLines pairOfLines = PairOfLinesByFourPoints(p1, p2, p3, p4);
      PairOfLinesBody result = new PairOfLinesBody(_id, title(), pairOfLines);
      result.addAnchor(BLDKEY_P1, anchors.get(getValueAsString(BLDKEY_P1)).id());
      result.addAnchor(BLDKEY_P2, anchors.get(getValueAsString(BLDKEY_P2)).id());
      result.addAnchor(BLDKEY_P3, anchors.get(getValueAsString(BLDKEY_P3)).id());
      result.addAnchor(BLDKEY_P4, anchors.get(getValueAsString(BLDKEY_P4)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Пара прямых не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PairOfLinesBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P1, getValueAsString(BLDKEY_P1));
    result.add(BLDKEY_P2, getValueAsString(BLDKEY_P2));
    result.add(BLDKEY_P3, getValueAsString(BLDKEY_P3));
    result.add(BLDKEY_P4, getValueAsString(BLDKEY_P4));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Пара прямых</strong> с точками %s, %s, %s, %s  ",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P2)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P3)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P4)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Пара прямых</strong>";
    }
  }
}
