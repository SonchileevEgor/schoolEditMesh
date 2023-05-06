package builders;

import bodies.LineBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Line3d;
import geom.Plane3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import geom.ExGeom;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class RibBetweenSkewLinesBuilder extends BodyBuilder {
  static public final String ALIAS = "RibBySkewLines";
  public static final String BLDKEY_LINE1 = "l1";
  public static final String BLDKEY_LINE2 = "l2";

  public RibBetweenSkewLinesBuilder() {
  }

  public RibBetweenSkewLinesBuilder(String id, String name) {
    super(id, name);
  }

  public RibBetweenSkewLinesBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibBetweenSkewLinesBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_LINE1, param.get(BLDKEY_LINE1).asString());
    setValue(BLDKEY_LINE2, param.get(BLDKEY_LINE2).asString());
  }

  public RibBetweenSkewLinesBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, 61);
    addParam(BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody l1 = (LineBody) bodies.get(getValueAsString(BLDKEY_LINE1));
      LineBody l2 = (LineBody) bodies.get(getValueAsString(BLDKEY_LINE2));
      if (!l1.exists() || !l2.exists()) {
        throw new ExLostBody("прямая");
      }
      if (Checker.isTwoLinesIntersect(l1.line(), l2.line()) || Checker.isTwoLinesParallel(l1.line(), l2.line()) || Line3d.equals(l1.line(), l2.line())) {
        throw new ExDegeneration("Прямые не скрещивающиеся");
      }
      Plane3d plane1 = Plane3d.planeBySkewLines(l1.line(), l2.line());
      Plane3d plane2 = Plane3d.planeByLineAndOrthPlane(l2.line(), plane1);
      Line3d intersect = plane1.intersectionWithPlane(plane2);
      Vect3d a = l1.line().intersectionWithLine(intersect);
      Vect3d b = l2.line().projectOfPoint(a);
      Rib3d r = new Rib3d(a, b);
      RibBody result = new RibBody(_id, r);
      edt.addAnchor(result.A(), result, "A");
      edt.addAnchor(result.B(), result, "B");
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoBody | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Отрезок не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE1, getValueAsString(BLDKEY_LINE1));
    result.add(BLDKEY_LINE2, getValueAsString(BLDKEY_LINE2));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong><br> между скрещивающимися прямыми %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE1)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
};
