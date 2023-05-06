package builders;

import bodies.PyramidBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Pyramid3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a prism by the base polygon and the top vertex
 *
 * @author alexeev
 */
public class PyramidByBaseAndTopBuilder extends BodyBuilder {
  static public final String ALIAS = "PyramidByBaseAndTop";

  public PyramidByBaseAndTopBuilder() {
  }

  public PyramidByBaseAndTopBuilder(String id, String name) {
    super(id, name);
  }

  public PyramidByBaseAndTopBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PyramidByBaseAndTopBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_TOP, param.get(BLDKEY_TOP).asString());
    setValue(BLDKEY_BASE, param.get(BLDKEY_BASE).asString());
  }

  public PyramidByBaseAndTopBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_TOP, BuilderParam.VERT_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_BASE, "Основание", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor base = anchors.get(getValueAsString(BLDKEY_BASE));
      Vect3d top = anchors.getVect(getValueAsString(BLDKEY_TOP));
      Pyramid3d p = new Pyramid3d(top, base.getPoly());
      PyramidBody result = new PyramidBody(_id, title(), p);
      for (int i = 0; i < base.arrayIDs().size(); i++) {
        result.addAnchor(String.valueOf(i), base.arrayIDs().get(i));
      }
      result.addAnchor(BLDKEY_TOP, anchors.get(getValueAsString(BLDKEY_TOP)).id());
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Пирамида не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PyramidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_TOP, getValueAsString(BLDKEY_TOP));
    result.add(BLDKEY_BASE, getValueAsString(BLDKEY_BASE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Пирамида</strong> с вершиной %s<br> и основанием %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_TOP)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_BASE)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Пирамида</strong>";
    }
  }
}
