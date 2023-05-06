package builders;

import bodies.CircleBody;
import bodies.ConicBody;
import bodies.EllipseMainBody;
import bodies.HyperboleBody;
import bodies.LineBody;
import bodies.PairOfLinesBody;
import bodies.ParabolaBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Parabola3d;
import geom.ConicPivots;
import geom.EllipseMain3d;
import geom.ExZeroDivision;
import geom.Hyperbole3d;
import geom.Line3d;
import static geom.Line3d.line3dByTwoPoints;
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
public class ConicByFivePointsBuilder extends BodyBuilder {
  public static final String ALIAS = "Conic";
  public static final String BLDKEY_P1 = "p1";
  public static final String BLDKEY_P2 = "p2";
  public static final String BLDKEY_P3 = "p3";
  public static final String BLDKEY_P4 = "p4";
  public static final String BLDKEY_P5 = "p5";

  public ConicByFivePointsBuilder() {
    super();
  }

  public ConicByFivePointsBuilder(String id, String name) {
    super(id, name);
  }

  public ConicByFivePointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ConicByFivePointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public ConicByFivePointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P1, param.get(BLDKEY_P1).asString());
    setValue(BLDKEY_P2, param.get(BLDKEY_P2).asString());
    setValue(BLDKEY_P3, param.get(BLDKEY_P3).asString());
    setValue(BLDKEY_P4, param.get(BLDKEY_P4).asString());
    setValue(BLDKEY_P5, param.get(BLDKEY_P5).asString());
  }

  @Override
  public void initParams() {
    super.initParams();
    addParam(BLDKEY_P1, "Первая точка", BuilderParamType.ANCHOR, 104);
    addParam(BLDKEY_P2, "Вторая точка", BuilderParamType.ANCHOR, 103);
    addParam(BLDKEY_P3, "Третья точка", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_P4, "Четвертая точка", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_P5, "Пятая точка", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d t1 = anchors.getVect(getValueAsString(BLDKEY_P1));
      Vect3d t2 = anchors.getVect(getValueAsString(BLDKEY_P2));
      Vect3d t3 = anchors.getVect(getValueAsString(BLDKEY_P3));
      Vect3d t4 = anchors.getVect(getValueAsString(BLDKEY_P4));
      Vect3d t5 = anchors.getVect(getValueAsString(BLDKEY_P5));
      ConicPivots conicPivots = new ConicPivots(t1, t2, t3, t4, t5);
      i_Body result = null;
      switch (conicPivots.type()) {
        case PARABOLA3D:
          Parabola3d parabola = new Parabola3d(conicPivots.pivots().get(0),
                                               conicPivots.pivots().get(1),
                                               conicPivots.pivots().get(2));
          result = new ParabolaBody(_id, title(), parabola);
          break;
        case ELLIPSE3D:
          EllipseMain3d ellipse = new EllipseMain3d(conicPivots.pivots().get(0),
                                                    conicPivots.pivots().get(1),
                                                    conicPivots.pivots().get(2));
          result = new EllipseMainBody(_id, title(), ellipse);
          break;
        case HYPERBOLE3D:
          Hyperbole3d hyperbole = new Hyperbole3d(conicPivots.pivots().get(0),
                                                  conicPivots.pivots().get(1),
                                                  conicPivots.pivots().get(2));
          result = new HyperboleBody(_id, title(), hyperbole);
          break;
        case CIRCLE3D:
          Circle3d circle = new Circle3d(conicPivots.pivots().get(0),
                                         conicPivots.pivots().get(1),
                                         conicPivots.pivots().get(2));
          result = new CircleBody(_id, title(), circle);
          edt.addAnchor(circle.center(), result, CircleBody.BODY_KEY_CENTER);
          edt.addAnchor(circle, result.getAnchorID(CircleBody.BODY_KEY_CENTER),
                  result, CircleBody.BODY_KEY_DISK);
          break;
        case LINE3D:
          Line3d line = line3dByTwoPoints(conicPivots.pivots().get(0), conicPivots.pivots().get(1));
          result = new LineBody(_id, title(), line);
          break;
        case PAIROFLINES3D:
          PairOfLines pairOfLines = PairOfLinesByFourPoints(conicPivots.pivots().get(0),
                                                            conicPivots.pivots().get(1),
                                                            conicPivots.pivots().get(2),
                                                            conicPivots.pivots().get(3));
          result = new PairOfLinesBody(_id, title(), pairOfLines);
          break;
      }
      result.addAnchor(BLDKEY_P1, anchors.get(getValueAsString(BLDKEY_P1)).id());
      result.addAnchor(BLDKEY_P2, anchors.get(getValueAsString(BLDKEY_P2)).id());
      result.addAnchor(BLDKEY_P3, anchors.get(getValueAsString(BLDKEY_P3)).id());
      result.addAnchor(BLDKEY_P4, anchors.get(getValueAsString(BLDKEY_P4)).id());
      result.addAnchor(BLDKEY_P5, anchors.get(getValueAsString(BLDKEY_P5)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Коника не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new ConicBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject() {
    };
    result.add(BLDKEY_P1, getValueAsString(BLDKEY_P1));
    result.add(BLDKEY_P2, getValueAsString(BLDKEY_P2));
    result.add(BLDKEY_P3, getValueAsString(BLDKEY_P3));
    result.add(BLDKEY_P4, getValueAsString(BLDKEY_P4));
    result.add(BLDKEY_P5, getValueAsString(BLDKEY_P5));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Коника</strong> по точкам %s, %s, %s, %s, %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P2)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P3)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P4)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P5)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Коника</strong>";
    }
  }
}
