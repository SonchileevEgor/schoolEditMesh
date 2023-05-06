package builders;

import bodies.CircleBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Intersection of two circles.
 *
 * @author alexeev
 */
public class CircleXCircleBuilder extends BodyBuilder {
  static public final String ALIAS = "CircleXCircle";
  static public final String BLDKEY_CIRCLE_1 = "c1";
  static public final String BLDKEY_CIRCLE_2 = "c2";
  static public final String BLDKEY_DIRECTION = "direction";

  public CircleXCircleBuilder() {
  }

  public CircleXCircleBuilder(String name) {
    super(name);
  }

  public CircleXCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CircleXCircleBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addCircle1(param.get(BLDKEY_CIRCLE_1).asString());
    addCircle2(param.get(BLDKEY_CIRCLE_2).asString());
    addDirection(param.get(BLDKEY_DIRECTION).asBoolean());
  }

  public CircleXCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addCircle1(String id) {
    setValue(BLDKEY_CIRCLE_1, id);
  }

  public final void addCircle2(String id) {
    setValue(BLDKEY_CIRCLE_2, id);
  }

  public final void addDirection(boolean direction) {
    setValue(BLDKEY_DIRECTION, direction);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CIRCLE_1, "Первая окружность", BuilderParamType.BODY, 61);
    addParam(BLDKEY_CIRCLE_2, "Вторая окружность", BuilderParamType.BODY, 60);
    addParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      CircleBody c1 = (CircleBody) bodies.get(getValueAsString(BLDKEY_CIRCLE_1));
      CircleBody c2 = (CircleBody) bodies.get(getValueAsString(BLDKEY_CIRCLE_2));
      if (!c1.exists() || !c2.exists()) {
        throw new ExLostBody("окружность");
      }

      ArrayList<Vect3d> intersectionPoints = c1.circle().intersect(c2.circle());
      Vect3d intersectPoint;
      if (intersectionPoints.size() == 2 && (boolean)getValue(BLDKEY_DIRECTION) /* выбрана вторая точка пересечения */) {
        intersectPoint = intersectionPoints.get(1);
      } else if (!intersectionPoints.isEmpty()) {
        intersectPoint = intersectionPoints.get(0);
      } else {
        throw new ExDegeneration("отсутствуют точки пересечения окружностей");
      }
      PointBody result = new PointBody(_id, intersectPoint);
      edt.addAnchor(result.point(), result, "P", title());
      _exists = true;
      return result;
    } catch (ExBadRef | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Пересечение окружностей не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CIRCLE_1, getValueAsString(BLDKEY_CIRCLE_1));
    result.add(BLDKEY_CIRCLE_2, getValueAsString(BLDKEY_CIRCLE_2));
    result.add(BLDKEY_DIRECTION, (boolean) getValue(BLDKEY_DIRECTION));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong> двух окружностей %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CIRCLE_1)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CIRCLE_1)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
