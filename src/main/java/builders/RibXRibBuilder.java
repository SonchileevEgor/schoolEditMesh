package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Finds intersection two ribs, if exists.
 *
 * @author alexeev
 */
public class RibXRibBuilder extends BodyBuilder {
  static public final String ALIAS = "RibXRib";
  
  public static final String BLDKEY_RIB1 = "r1";
  public static final String BLDKEY_RIB2 = "r2";

  public RibXRibBuilder() {
  }

  public RibXRibBuilder(String id, String name) {
    super(id, name);
  }

  public RibXRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibXRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB1, param.get(BLDKEY_RIB1).asString());
    setValue(BLDKEY_RIB2, param.get(BLDKEY_RIB2).asString());
  }

  public RibXRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB1, "Первый отрезок", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_RIB2, "Второй отрезок", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor r1 = anchors.get(getValueAsString(BLDKEY_RIB1));
      i_Anchor r2 = anchors.get(getValueAsString(BLDKEY_RIB2));

      Vect3d intersect = r1.getRib().intersectWithRib(r2.getRib());

      if (r1.getRib().a().equals(intersect) || r1.getRib().b().equals(intersect)) {
        throw new ExDegeneration("отрезки имеют общую вершину");
      }

      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P", title());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение отрезков не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB1, getValueAsString(BLDKEY_RIB1));
    result.add(BLDKEY_RIB2, getValueAsString(BLDKEY_RIB2));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong> отрезков %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB2)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
