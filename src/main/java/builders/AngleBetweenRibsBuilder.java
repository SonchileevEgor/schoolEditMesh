package builders;

import bodies.AngleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExGeom;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Angle between two ribs builder.
 * Combination of direction options determines choice of one of four available angles.
 * @author alexeev
 */
public class AngleBetweenRibsBuilder extends BodyBuilder {
  public static final String ALIAS = "AngleBetweenRibs";
  public static final String BLDKEY_RIB1 = "r1";
  public static final String BLDKEY_RIB2 = "r2";
  public static final String BLDKEY_DIRECTION1 = "direction1";
  public static final String BLDKEY_DIRECTION2 = "direction2";

  public AngleBetweenRibsBuilder() {
  }

  public AngleBetweenRibsBuilder(String name) {
    super(name);
  }

  public AngleBetweenRibsBuilder(String id, String name) {
    super(id, name);
  }

  public AngleBetweenRibsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public AngleBetweenRibsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public AngleBetweenRibsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addRib1(param.get(BLDKEY_RIB1).asString());
    addRib2(param.get(BLDKEY_RIB2).asString());
    addDirection1(param.get(BLDKEY_DIRECTION1).asBoolean());
    addDirection2(param.get(BLDKEY_DIRECTION2).asBoolean());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB1, "Первый отрезок", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_RIB2, "Второй отрезок", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_DIRECTION1, "Направление первого отрезка", BuilderParamType.BOOLEAN, 90);
    addParam(BLDKEY_DIRECTION2, "Направление второго отрезка", BuilderParamType.BOOLEAN, 80);
  }

  public final void addRib1(String id) {
    setValue(BLDKEY_RIB1, id);
  }

  public final void addRib2(String id) {
    setValue(BLDKEY_RIB2, id);
  }

  public final void addDirection1(boolean direction) {
    setValue(BLDKEY_DIRECTION1, direction);
  }

  public final void addDirection2(boolean direction) {
    setValue(BLDKEY_DIRECTION2, direction);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor rib1 = anchors.get(getValueAsString(BLDKEY_RIB1));
      i_Anchor rib2 = anchors.get(getValueAsString(BLDKEY_RIB2));
      Rib3d rib3d_1 = rib1.getRib();
      Rib3d rib3d_2 = rib2.getRib();
      ArrayList<Vect3d> intersect = rib3d_1.line().intersect(rib3d_2.line());
      if (intersect.isEmpty()) {
        throw new ExGeom("отрезки лежат в разных плоскостях");
      }
      Vect3d center = intersect.get(0);

      // выбираем один из четырёх углов
      Vect3d pt1 = getValueAsBoolean(BLDKEY_DIRECTION1) ?
              Vect3d.sum(center, Vect3d.sub(rib3d_1.b(), rib3d_1.a())) :
              Vect3d.sum(center, Vect3d.sub(rib3d_1.a(), rib3d_1.b()));

      Vect3d pt2 = getValueAsBoolean(BLDKEY_DIRECTION2) ?
              Vect3d.sum(center, Vect3d.sub(rib3d_2.b(), rib3d_2.a())) :
              Vect3d.sum(center, Vect3d.sub(rib3d_2.a(), rib3d_2.b()));

      Angle3d angle = new Angle3d(pt1, center, pt2, true);
      AngleBody result = new AngleBody(_id, getValueAsString(BLDKEY_NAME), angle);

      // Якоря не добавляются, чтобы избежать повторного добавления точки пересечения
      // для разных углов (смежных или вертикальных).
      _exists = true;
      return result;
    } catch( ExNoAnchor | ExGeom ex ){
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
    result.add(BLDKEY_RIB1, getValueAsString(BLDKEY_RIB1));
    result.add(BLDKEY_RIB2, getValueAsString(BLDKEY_RIB2));
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
      return String.format("<html><strong>Угол</strong><br> между отрезками %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB2)));
    } catch ( ExNoAnchor ex ) {
      return "<html><strong>Угол между отрезками</strong>";
    }
  }
}
