package builders;

import bodies.LineBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Finds intersection of line and rib, if exists.
 *
 * @author alexeev
 */
public class LineXRibBuilder extends BodyBuilder {
  static public final String ALIAS = "LineXRib";

  public LineXRibBuilder() {
  }

  public LineXRibBuilder(String name) {
    super(name);
  }

  public LineXRibBuilder(String id, String name) {
    super(id, name);
  }

  public LineXRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineXRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addLine(param.get(BLDKEY_LINE).asString());
    addRib(param.get(BLDKEY_RIB).asString());
  }

  public final void addLine(String id) {
    setValue(BLDKEY_LINE, id);
  }

  public final void addRib(String id) {
    setValue(BLDKEY_RIB, id);
  }

  public LineXRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, 102);
    addParam(BLDKEY_RIB, "Вторая прямая", BuilderParamType.ANCHOR, 101);
  }

  @Override
  public void initParams(){
    super.initParams();
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      LineBody line = (LineBody) bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists()) {
        throw new ExLostBody("прямая");
      }

      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      Vect3d intersect = rib.getRib().intersectWithLine(line.line());
      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch (ExBadRef | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Пересечение прямой и отрезка не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong><br> прямой %s и отрезка %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExBadRef ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
