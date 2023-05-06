package builders;

import bodies.PrismBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Prism3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Right parallelepiped builder.
 * Choose one vertex, direction of length, angle of rotation and three dimensions.
 *
 * @author alexeev.
 */
public class RectangularParallelepipedBuilder extends BodyBuilder {
  static public final String ALIAS = "RectangularParallelepiped";
  public static final String BLDKEY_WIDTH = "width";

  public RectangularParallelepipedBuilder() {
  }
  
  public RectangularParallelepipedBuilder(String id, String name) {
    super(id, name);
  }

  public RectangularParallelepipedBuilder(String name) {
    super(name);
  }

  public RectangularParallelepipedBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RectangularParallelepipedBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addHeight(param.get(BLDKEY_HEIGHT).asDouble());
    addWidth(param.get(BLDKEY_WIDTH).asDouble());
    addAngle(param.get(BLDKEY_ANGLE).asDouble());
  }

  public RectangularParallelepipedBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_WIDTH, "Ширина", BuilderParamType.DOUBLE_POSITIVE, 31);
    addParam(BLDKEY_HEIGHT, "Высота", BuilderParamType.DOUBLE_POSITIVE, 30);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addWidth(double width) {
    setValue(BLDKEY_WIDTH, width);
  }

  public final void addHeight(double height) {
    setValue(BLDKEY_HEIGHT, height);
  }

  public final void addAngle(double angle) {
    setValue(BLDKEY_ANGLE, angle);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      double height = getValueAsDouble(BLDKEY_HEIGHT);
      double width = getValueAsDouble(BLDKEY_WIDTH);
      double alpha = getValueAsDouble(BLDKEY_ANGLE);
      Prism3d prism = Prism3d.recParallepBy2PntsAngle(a, b, height, width, alpha);
      PrismBody result = new PrismBody(_id, title(), prism);
      result.addAnchor("0", getValueAsString(BLDKEY_A));
      result.addAnchor("1", getValueAsString(BLDKEY_B));
      for( int i = 2; i < 8; i++ ){
        edt.addAnchor(prism.points().get(i), result, String.valueOf(i));
      }
      result.addRibs(edt);
      result.addPlanes(edt);
      result.setParallelepiped(true);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Параллелепипед не построен: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new PrismBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_HEIGHT, getValueAsDouble(BLDKEY_HEIGHT));
    result.add(BLDKEY_WIDTH, getValueAsDouble(BLDKEY_WIDTH));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямоугольный параллелепипед</strong><br> с вершинами"
              + "%s, %s <br>длиной шириной %s и высотой %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
        Util.valueOf(getValueAsDouble(BLDKEY_HEIGHT), precision),
        Util.valueOf(getValueAsDouble(BLDKEY_WIDTH), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Прямоугольный параллелепипед</strong>";
    }
  }

}
