package builders;

import bodies.PolygonBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Triang3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Конструктор многоугольника по якорю-грани.
 *
 * @author alexeev-laptop
 */
public class PolygonFromAnchorBuilder extends BodyBuilder {
  static public final String ALIAS = "PolygonFromAnchor";

  public PolygonFromAnchorBuilder() {
  }

  public PolygonFromAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public PolygonFromAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PolygonFromAnchorBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_FACET, param.get(BLDKEY_FACET).asString());
  }

  public PolygonFromAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_FACET, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor a = anchors.get(getValueAsString(BLDKEY_FACET));
      ArrayList<String> pointIDs = a.arrayIDs();
      i_Body result;
      if (a.getPoly().isTriangle()) {
        result = new TriangleBody(_id, new Triang3d(a.getPoly().points().get(0),
                                                    a.getPoly().points().get(1),
                                                    a.getPoly().points().get(2)));
        result.addAnchor("A", pointIDs.get(0));
        result.addAnchor("B", pointIDs.get(1));
        result.addAnchor("C", pointIDs.get(2));
      } else {
        result = new PolygonBody(_id, a.getPoly());
        // Добавляем точки
        for (int i = 0; i < pointIDs.size(); i++) {
          result.addAnchor(String.valueOf(i), pointIDs.get(i));
        }
      }

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Многоугольник не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_FACET, getValueAsString(BLDKEY_FACET));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Многоугольник</strong><br> построенный по грани %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_FACET)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Многоугольник</strong>";
    }
  }
}
