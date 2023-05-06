package builders;

import bodies.EllipsoidBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Ellipsoid3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author VitaliiZah
 */
public class EllipsoidBuilder extends BodyBuilder {
  static public final String ALIAS = "Ellipsoid";

  public EllipsoidBuilder() {
  }

  public EllipsoidBuilder(String id, String name) {
    super(id, name);
  }

  public EllipsoidBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public EllipsoidBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public EllipsoidBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_FOCUS1, param.get(BLDKEY_FOCUS1).asString());
    setValue(BLDKEY_FOCUS2, param.get(BLDKEY_FOCUS2).asString());
    setValue(BLDKEY_POINT_ON_BOUND, param.get(BLDKEY_POINT_ON_BOUND).asString());
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_FOCUS1, "Первый фокус", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_FOCUS2, "Второй фокус", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_POINT_ON_BOUND, "Точка на границе эллипсоида", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d f1 = anchors.getVect(getValueAsString(BLDKEY_FOCUS1));
      Vect3d f2 = anchors.getVect(getValueAsString(BLDKEY_FOCUS2));
      Vect3d pointOnBound = anchors.getVect(getValueAsString(BLDKEY_POINT_ON_BOUND));
      Ellipsoid3d ellipsoid = new Ellipsoid3d(f1, f2, pointOnBound);
      EllipsoidBody result = new EllipsoidBody(_id, title(), ellipsoid);
      result.addAnchor(BLDKEY_FOCUS1, anchors.get(getValueAsString(BLDKEY_FOCUS1)).id());
      result.addAnchor(BLDKEY_FOCUS2, anchors.get(getValueAsString(BLDKEY_FOCUS2)).id());
      result.addAnchor(BLDKEY_POINT_ON_BOUND, anchors.get(getValueAsString(BLDKEY_POINT_ON_BOUND)).id());
      _exists = true;
      return result;

    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Эллипсоид не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new EllipsoidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_FOCUS1, getValueAsString(BLDKEY_FOCUS1));
    result.add(BLDKEY_FOCUS2, getValueAsString(BLDKEY_FOCUS2));
    result.add(BLDKEY_POINT_ON_BOUND, getValueAsString(BLDKEY_POINT_ON_BOUND));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Эллипсоид</strong> с фокусами %s, %s <br>и точкой %s на эллипсоиде ",
              ctrl.getAnchor(getValueAsString(BLDKEY_FOCUS1)).getTitle(),
              ctrl.getAnchor(getValueAsString(BLDKEY_FOCUS2)).getTitle(),
              ctrl.getAnchor(getValueAsString(BLDKEY_POINT_ON_BOUND)).getTitle());
    } catch (ExNoAnchor ex) {
      return "<html><strong>Эллипсоид</strong>";
    }
  }

}
