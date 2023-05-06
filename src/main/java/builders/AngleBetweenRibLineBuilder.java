package builders;

import bodies.AngleBody;
import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
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
 * Angle between rib and line builder.
 * Combination of direction options determines choice of one of four available angles.
 * @author alexeev.
 */
public class AngleBetweenRibLineBuilder extends BodyBuilder {
  public static final String ALIAS = "AngleBetweenRibLine";
  public static final String BLDKEY_DIRECTION1 = "direction1";
  public static final String BLDKEY_DIRECTION2 = "direction2";

  public AngleBetweenRibLineBuilder() {
  }

  public AngleBetweenRibLineBuilder(String name) {
    super(name);
  }

  public AngleBetweenRibLineBuilder(String id, String name) {
    super(id, name);
  }

  public AngleBetweenRibLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public AngleBetweenRibLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public AngleBetweenRibLineBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addLine(param.get(BLDKEY_LINE).asString());
    addRib(param.get(BLDKEY_RIB).asString());
    addDirection1(param.get(BLDKEY_DIRECTION1).asBoolean());
    addDirection2(param.get(BLDKEY_DIRECTION2).asBoolean());
  }

  public final void addLine(String id) {
    setValue(BLDKEY_LINE, id);
  }

  public final void addRib(String id) {
    setValue(BLDKEY_RIB, id);
  }

  public final void addDirection1(boolean direction) {
    setValue(BLDKEY_DIRECTION1, direction);
  }

  public final void addDirection2(boolean direction) {
    setValue(BLDKEY_DIRECTION2, direction);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, 102);
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_DIRECTION1, "Направление прямой", BuilderParamType.BOOLEAN, 90);
    addParam(BLDKEY_DIRECTION2, "Направление отрезка", BuilderParamType.BOOLEAN, 80);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bd = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      LineBody line = (LineBody)bd.get(getValueAsString(BLDKEY_LINE));
      if( !line.exists() )
        throw new ExLostBody("Прямая");
      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      ArrayList<Vect3d> intersect = rib.getRib().line().intersect(line.line());
      if (intersect.isEmpty()) {
        throw new ExGeom("отрезок и прямая лежат в разных плоскостях");
      }
      Vect3d center = intersect.get(0);

      // считаем выпуклую координату точки пересечения относительно ребра AB
      // если она < 0, то точка пересечениянаходится за точкой A
      // если > 1, — то за точкой B
      // иначе — на отрезке
      double center_koef = Vect3d.getMultiplierForHull2(rib.getRib().a(), rib.getRib().b(), center);

      // выбираем один из четырёх углов
      Vect3d pt1 = getValueAsBoolean(BLDKEY_DIRECTION1) ?
              Vect3d.sum(center, line.l()) :
              Vect3d.sub(center, line.l());

      Vect3d pt2 = getValueAsBoolean(BLDKEY_DIRECTION2) ?
              Vect3d.sum(center, Vect3d.sub(rib.getRib().b(), rib.getRib().a())) :
              Vect3d.sum(center, Vect3d.sub(rib.getRib().a(), rib.getRib().b()));

      Angle3d angle = new Angle3d(pt1, center, pt2, true);
      AngleBody result = new AngleBody(_id, getValueAsString(BLDKEY_NAME), angle);

      // Якоря не добавляются, чтобы избежать повторного добавления точки пересечения
      // для разных углов (смежных или вертикальных).
      
      _exists = true;
      return result;
    } catch( ExNoBody | ExNoAnchor | ExGeom ex ){
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
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
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
      return String.format("<html><strong>Угол</strong><br> между прямой %s и отрезком %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Угол между прямой и отрезком</strong>";
    }
  }
}
