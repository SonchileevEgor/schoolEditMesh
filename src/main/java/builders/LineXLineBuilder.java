package builders;

import bodies.LineBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Builder of intersection of two lines.
 *
 * @author alexeev
 */
public class LineXLineBuilder extends BodyBuilder {
  static public final String BLDKEY_LINE_1 = "l1";
  static public final String BLDKEY_LINE_2 = "l2";
  static public final String ALIAS = "LineXLine";

  public LineXLineBuilder() {
  }

  public LineXLineBuilder(String name) {
    super(name);
  }

  public LineXLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineXLineBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addLine1(param.get(BLDKEY_LINE_1).asString());
    addLine2(param.get(BLDKEY_LINE_2).asString());
  }

  public LineXLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addLine1(String id) {
    setValue(BLDKEY_LINE_1, id);
  }

  public final void addLine2(String id) {
    setValue(BLDKEY_LINE_2, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE_1, "Первая прямая", BuilderParamType.BODY, 61);
    addParam(BLDKEY_LINE_2, "Вторая прямая", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      String id1 = getValueAsString(BLDKEY_LINE_1);
      String id2 = getValueAsString(BLDKEY_LINE_2);
      LineBody l1 = (LineBody) bodies.get(id1);
      LineBody l2 = (LineBody) bodies.get(id2);
      if (!l1.exists() || !l2.exists()) {
        throw new ExLostBody("прямая");
      }

      Vect3d intersect = l1.line().intersectionWithLine(l2.line());
      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch (ExNoBody | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Пересечение прямых не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE_1, getValueAsString(BLDKEY_LINE_1));
    result.add(BLDKEY_LINE_2, getValueAsString(BLDKEY_LINE_2));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong> прямых %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE_1)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE_2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
