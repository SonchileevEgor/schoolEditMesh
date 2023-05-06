package builders;

import bodies.CircleBody;
import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Out tangent line for two circles
 *
 * @author rita
 */
public class OutTangentForTwoCirclesBuilder extends BodyBuilder {
  static public final String ALIAS = "OutTangentForTwoCircles";

  public OutTangentForTwoCirclesBuilder() {
  }

  public OutTangentForTwoCirclesBuilder(String id, String name) {
    super(id, name);
  }

  public OutTangentForTwoCirclesBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public OutTangentForTwoCirclesBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_C1, param.get(BLDKEY_C1).asString());
    setValue(BLDKEY_C2, param.get(BLDKEY_C2).asString());
    setValue(BLDKEY_DIRECTION, param.get(BLDKEY_DIRECTION).asBoolean());
  }

  public OutTangentForTwoCirclesBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_C1, "Первая окружность", BuilderParamType.BODY, 61);
    addParam(BLDKEY_C2, "Вторая окружность", BuilderParamType.BODY, 60);
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
      CircleBody c1 = (CircleBody) bodies.get(getValueAsString(BLDKEY_C1));
      CircleBody c2 = (CircleBody) bodies.get(getValueAsString(BLDKEY_C2));
      if (!c1.exists() || !c2.exists()) {
        throw new ExLostBody("окружность");
      }
      ArrayList<Vect3d> tangPoints = Circle3d.pointsOfOutTangentLines(c1.circle(), c2.circle());
      Vect3d tangPoint1, tangPoint2;
      if (tangPoints.size() == 4) {
        if ((boolean) getValue(BLDKEY_DIRECTION)/* выбрана вторая касательная */) {
          tangPoint1 = tangPoints.get(2);
          tangPoint2 = tangPoints.get(3);
        } else {
          tangPoint1 = tangPoints.get(0);
          tangPoint2 = tangPoints.get(1);
        }
      } else {
        throw new ExDegeneration("Отсутствуют касательные");
      }
      LineBody result = new LineBody(_id, title(), Line3d.line3dByTwoPoints(tangPoint1, tangPoint2));

      edt.addAnchor(tangPoint1, result, "A");
      edt.addAnchor(tangPoint2, result, "B");

      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExBadRef | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Внешняя касательная окружностей не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new LineBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_C1, getValueAsString(BLDKEY_C1));
    result.add(BLDKEY_C2, getValueAsString(BLDKEY_C2));
    result.add(BLDKEY_DIRECTION, getValueAsBoolean(BLDKEY_DIRECTION));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Внешняя общая касательная</strong><br>окружностей %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_C1)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_C2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
