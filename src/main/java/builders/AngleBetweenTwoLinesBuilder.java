package builders;

import bodies.AngleBody;
import bodies.i_LinearBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Angle between two linear bodies (lines, rays, or segments).
 * Combination of direction options determines choice of one of four available angles.
 * @author alexeev
 */
public class AngleBetweenTwoLinesBuilder extends BodyBuilder {
  public static final String ALIAS = "AngleBetweenTwoLines";
  public static final String BLDKEY_LINE1 = "l1";
  public static final String BLDKEY_LINE2 = "l2";
  public static final String BLDKEY_DIRECTION1 = "direction1";
  public static final String BLDKEY_DIRECTION2 = "direction2";

  public AngleBetweenTwoLinesBuilder() {
  }

  public AngleBetweenTwoLinesBuilder(String name) {
    super(name);
  }

  public AngleBetweenTwoLinesBuilder(String id, String name) {
    super(id, name);
  }

  public AngleBetweenTwoLinesBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public AngleBetweenTwoLinesBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public AngleBetweenTwoLinesBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addLine1(param.get(BLDKEY_LINE1).asString());
    addLine2(param.get(BLDKEY_LINE2).asString());
    addDirection1(param.get(BLDKEY_DIRECTION1).asBoolean());
    addDirection2(param.get(BLDKEY_DIRECTION2).asBoolean());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, 102);
    addParam(BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, 101);
    addParam(BLDKEY_DIRECTION1, "Направление 1-й прямой", BuilderParamType.BOOLEAN, 90);
    addParam(BLDKEY_DIRECTION2, "Направление 2-й прямой", BuilderParamType.BOOLEAN, 80);
  }

  public final void addLine1(String id) {
    setValue(BLDKEY_LINE1, id);
  }

  public final void addLine2(String id) {
    setValue(BLDKEY_LINE2, id);
  }

  public final void addDirection1(boolean direction) {
    setValue(BLDKEY_DIRECTION1, direction);
  }

  public final void addDirection2(boolean direction) {
    setValue(BLDKEY_DIRECTION2, direction);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bd = edt.bd();
    try {
      i_LinearBody l1 = (i_LinearBody)bd.get(getValueAsString(BLDKEY_LINE1));
      i_LinearBody l2 = (i_LinearBody)bd.get(getValueAsString(BLDKEY_LINE2));
      if( !(l1.exists() && l2.exists()) )
        throw new ExLostBody("Прямая");
      ArrayList<Vect3d> intersect = l1.line().intersect(l2.line());
      if (intersect.isEmpty()) {
        throw new ExGeom("прямые не пересекаются");
      }
      Vect3d center = intersect.get(0);

      // выбираем один из четырёх углов
      Vect3d pt1 = getValueAsBoolean(BLDKEY_DIRECTION1) ?
              Vect3d.sum(center, l1.line().l()) :
              Vect3d.sub(center, l1.line().l());
      Vect3d pt2 = getValueAsBoolean(BLDKEY_DIRECTION2) ?
              Vect3d.sum(center, l2.line().l()) :
              Vect3d.sub(center, l2.line().l());

      Angle3d angle = new Angle3d(pt1, center, pt2, true);
      AngleBody result = new AngleBody(_id, getValueAsString(BLDKEY_NAME), angle);
      _exists = true;
      return result;
    } catch( ExNoBody | ExGeom ex ){
      if (_exists) {
        eh.showMessage("Угол не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new AngleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE1, getValueAsString(BLDKEY_LINE1));
    result.add(BLDKEY_LINE2, getValueAsString(BLDKEY_LINE2));
    result.add(BLDKEY_DIRECTION1, getValueAsBoolean(BLDKEY_DIRECTION1));
    result.add(BLDKEY_DIRECTION2, getValueAsBoolean(BLDKEY_DIRECTION2));
    return result;
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Угол</strong><br> между %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE1)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Угол между прямыми</strong>";
    }
  }
}
