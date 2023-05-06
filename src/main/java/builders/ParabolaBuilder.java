package builders;

import bodies.ParabolaBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Parabola3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author VitaliiZah
 */
public class ParabolaBuilder extends BodyBuilder {
  static public final String ALIAS = "Parabola";
  public static final String BLDKEY_POINT_ON_LINE1 = "pOnLine1";
  public static final String BLDKEY_POINT_ON_LINE2 = "pOnLine2";

  public ParabolaBuilder() {
  }

  public ParabolaBuilder(String id, String name) {
    super(id, name);
  }

  public ParabolaBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ParabolaBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public ParabolaBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POINT_ON_LINE1, param.get(BLDKEY_POINT_ON_LINE1).asString());
    setValue(BLDKEY_POINT_ON_LINE2, param.get(BLDKEY_POINT_ON_LINE2).asString());
    setValue(BLDKEY_FOCUS, param.get(BLDKEY_FOCUS).asString());
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINT_ON_LINE1, "Точка на директрисе", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_POINT_ON_LINE2, "Вторая точка на директрисе", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_FOCUS, "Фокус", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d pOnLine1 = anchors.getVect(getValueAsString(BLDKEY_POINT_ON_LINE1));
      Vect3d pOnLine2 = anchors.getVect(getValueAsString(BLDKEY_POINT_ON_LINE2));
      Vect3d f = anchors.getVect(getValueAsString(BLDKEY_FOCUS));
      Parabola3d parabola = new Parabola3d(pOnLine1, pOnLine2, f);
      ParabolaBody result = new ParabolaBody(_id, title(), parabola);
      result.addAnchor(BLDKEY_POINT_ON_LINE1, anchors.get(getValueAsString(BLDKEY_POINT_ON_LINE1)).id());
      result.addAnchor(BLDKEY_POINT_ON_LINE2, anchors.get(getValueAsString(BLDKEY_POINT_ON_LINE2)).id());
      result.addAnchor(BLDKEY_FOCUS, anchors.get(getValueAsString(BLDKEY_FOCUS)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Парабола не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new ParabolaBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject() {
    };
    result.add(BLDKEY_POINT_ON_LINE1, getValueAsString(BLDKEY_POINT_ON_LINE1));
    result.add(BLDKEY_POINT_ON_LINE2, getValueAsString(BLDKEY_POINT_ON_LINE2));
    result.add(BLDKEY_FOCUS, getValueAsString(BLDKEY_FOCUS));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Парабола</strong> с точками %s, %s директрисы <br> и фокусом %s ",
              ctrl.getAnchor(getValueAsString(BLDKEY_POINT_ON_LINE1)).getTitle(),
              ctrl.getAnchor(getValueAsString(BLDKEY_POINT_ON_LINE2)).getTitle(),
              ctrl.getAnchor(getValueAsString(BLDKEY_FOCUS)).getTitle());
    } catch (ExNoAnchor ex) {
      return "<html><strong>Парабола</strong>";
    }
  }
}
