package builders;

import bodies.CircleBody;
import bodies.RibBody;
import static builders.BodyBuilder.BLDKEY_RADIUS;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author Elena
 */
public class TangentRib2dBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentRib2d";
  public static final String BLDKEY_RIB_LENGTH = "ribLength";

  public TangentRib2dBuilder() {
  }
  
  public TangentRib2dBuilder(String name) {
    super(name);
  }

  public TangentRib2dBuilder(String id, String name) {
    super(id, name);
  }

  public TangentRib2dBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentRib2dBuilder (String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_DISK, param.get(BLDKEY_DISK).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
    setValue(BLDKEY_COEFFICIENT, param.get(BLDKEY_COEFFICIENT).asDouble());
    setValue(BLDKEY_RIB_LENGTH, param.get(BLDKEY_RIB_LENGTH).asDouble());
  }

  public TangentRib2dBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DISK, BuilderParam.CIRCLE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_COEFFICIENT, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE_POSITIVE, 31);
    addParam(BLDKEY_RIB_LENGTH, "Длина отрезка", BuilderParamType.DOUBLE_POSITIVE, 30);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      CircleBody circ = (CircleBody)bodies.get(getValueAsString(BLDKEY_DISK));
      if (!circ.exists())
        throw new ExLostBody("окружность");

      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));
      double ribLength = getValueAsDouble(BLDKEY_RIB_LENGTH);
      double coef = getValueAsDouble(BLDKEY_COEFFICIENT);

      Rib3d rib = Rib3d.tangentRibByPointLengthAndRatio2d(point, coef, ribLength, circ.circle());
      RibBody result = new RibBody(_id, rib);
      edt.addAnchor(result.A(), result, "A");
      edt.addAnchor(result.B(), result, "B");

      result.addRibs(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Отрезок не построен: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_DISK, getValueAsString(BLDKEY_DISK));
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_RIB_LENGTH, getValueAsDouble(BLDKEY_RIB_LENGTH));
    result.add(BLDKEY_COEFFICIENT, getValueAsDouble(BLDKEY_COEFFICIENT));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong>, касательный к окружности %s<br>"
              + "в точке %s, заданный отношением %s"
              + "<br> и имеющий длину %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_DISK)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
        Util.valueOf(getValueAsDouble(BLDKEY_COEFFICIENT), precision),
        Util.valueOf(getValueAsDouble(BLDKEY_RIB_LENGTH), precision));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
