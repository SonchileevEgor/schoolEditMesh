package builders;

import bodies.PyramidBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Pyramid3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Constructs a regular pyramid by the two points of base polygon, plane and the
 * top vertex
 */
public class PyramidRegularBuilder extends BodyBuilder {
  static public final String ALIAS = "RegularPyramid";

  public PyramidRegularBuilder() {
  }

  public PyramidRegularBuilder(String name) {
    super(name);
  }
  
  public PyramidRegularBuilder(String id, String name) {
    super(id, name);
  }

  public PyramidRegularBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PyramidRegularBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_HEIGHT, param.get(BLDKEY_HEIGHT).asDouble());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
    setValue(BLDKEY_POINT_NUM, param.get(BLDKEY_POINT_NUM).asInt());
  }

  public PyramidRegularBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина основания", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая вершина основания", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_HEIGHT, "Высота", BuilderParamType.DOUBLE_POSITIVE);
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
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      double height = (double) getValue(BLDKEY_HEIGHT);
      double angle = (double) getValue(BLDKEY_ANGLE);
      int vertNum = (int) getValue(BLDKEY_POINT_NUM);
      Pyramid3d pyr = Pyramid3d.regPyramidBy2PntsHeightAngle(a, b, vertNum, height, angle);
      PyramidBody result = new PyramidBody(_id, title(), pyr);
      result.addAnchor("0", getValueAsString(BLDKEY_A));
      result.addAnchor("1", getValueAsString(BLDKEY_B));
      for (int j = 2; j < result.pyramid().vertNumber() - 1; j++) {
        edt.addAnchor(result.pyramid().points().get(j), result, String.valueOf(j));
      }
      edt.addAnchor(result.pyramid().top(), result, BLDKEY_TOP);
//      i_Anchor anchor = edt.anchors().get(result.getAnchorID(BLDKEY_TOP));
      /**
       * Определяем поведение при перемещении вершины пирамиды.
       */
      /*final Vect3d point = anchor.getPoint();
       anchor.setMovable(true);
       anchor.setMoveBehavior(new i_AnchorMoveBehavior() {
       @Override
       public Vect3d getNextPosition(SceneGL sc, int canvasHeight, float newX, float newY) {

       }
       @Override
       public void move(SceneGL sc, int canvasHeight, float newX, float newY) {

       }
       });*/
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Пирамида не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PyramidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_HEIGHT, getValueAsDouble(BLDKEY_HEIGHT));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));
    result.add(BLDKEY_POINT_NUM, getValueAsInt(BLDKEY_POINT_NUM));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Правильная %s-угольная пирамида</strong><br>со стороной основания %s%s<br> и длиной высоты %s",
              getValue(BLDKEY_POINT_NUM),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              Util.valueOf(getValueAsDouble(BLDKEY_HEIGHT), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Правильная пирамида</strong>";
    }
  }
}
