package builders;

import bodies.EllipseMainBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.EllipseMain3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author VitaliiZah
 */
public class EllipseMainBuilder extends BodyBuilder {
  public static final String ALIAS = "Ellipse";
  
  public static final String BLDKEY_FOCUS1 = "focus1";
  public static final String BLDKEY_FOCUS2 = "focus2";
  public static final String BLDKEY_POINT_ON_BOUND = "pointOnBound";

  public EllipseMainBuilder() {
  }

  public EllipseMainBuilder(String id, String name) {
    super(id, name);
  }

  public EllipseMainBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public EllipseMainBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public EllipseMainBuilder(String id, String name, JsonObject param) {
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
    addParam(BLDKEY_POINT_ON_BOUND, "Точка на границе эллипса", BuilderParamType.ANCHOR, 100);
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
      EllipseMain3d elipse = new EllipseMain3d(f1, f2, pointOnBound);
      EllipseMainBody result = new EllipseMainBody(_id, title(), elipse);
      result.addAnchor(BLDKEY_FOCUS1, anchors.get(getValueAsString(BLDKEY_FOCUS1)).id());
      result.addAnchor(BLDKEY_FOCUS2, anchors.get(getValueAsString(BLDKEY_FOCUS2)).id());
      result.addAnchor(BLDKEY_POINT_ON_BOUND, anchors.get(getValueAsString(BLDKEY_POINT_ON_BOUND)).id());
      _exists = true;
      return result;

    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Эллипс не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new EllipseMainBody(_id, title());
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
      return String.format("<html><strong>Эллипс</strong> с фокусами %s, %s <br>и точкой %s на эллипсе ",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_FOCUS1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_FOCUS2)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT_ON_BOUND)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Эллипс</strong>";
    }
  }

}
