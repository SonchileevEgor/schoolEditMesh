package builders;

import bodies.PrismBody;
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
import geom.Prism3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Построение призмы по основанию, вершины на основании и сответствующей вершине
 * верхнего основания.
 *
 * @author alexeev
 */
public class PrismByBaseAndTopBuilder extends BodyBuilder {
  static public final String ALIAS = "PrismByBaseAndTop";

  public PrismByBaseAndTopBuilder() {
  }

  public PrismByBaseAndTopBuilder(String id, String name) {
    super(id, name);
  }

  public PrismByBaseAndTopBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PrismByBaseAndTopBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_BASE, param.get(BLDKEY_BASE).asString());
    setValue(BLDKEY_VERTEX, param.get(BLDKEY_VERTEX).asString());
    setValue(BLDKEY_TOP, param.get(BLDKEY_TOP).asString());
  }

  public PrismByBaseAndTopBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BASE, "Основание", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_VERTEX, "Вершина 1-го основания", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_TOP, "Вершина 2-го основания", BuilderParamType.ANCHOR, 100);
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
      String vertexID = getValueAsString(BLDKEY_VERTEX);

      int index = base.arrayIDs().indexOf(vertexID);
      if (index == -1) {
        throw new ExGeom("вершина не принадлежит основанию");
      }

      Prism3d p = new Prism3d(top, base.getPoly(), index);
      PrismBody result = new PrismBody(_id, title(), p);
      int baseSize = base.arrayIDs().size();
      for (int i = 0; i < baseSize; i++) {
        result.addAnchor(String.valueOf(i), base.arrayIDs().get((i + index) % baseSize));
      }
      result.addAnchor(String.valueOf(base.arrayIDs().size()), anchors.get(getValueAsString(BLDKEY_TOP)).id());
      for (int i = baseSize + 1; i < 2 * baseSize; i++) {
        edt.addAnchor(p.points().get(i), result, String.valueOf(i));
      }
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Призма не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PrismBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_TOP, getValueAsString(BLDKEY_TOP));
    result.add(BLDKEY_BASE, getValueAsString(BLDKEY_BASE));
    result.add(BLDKEY_VERTEX, getValueAsString(BLDKEY_VERTEX));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Призма</strong> с вершиной %s и основанием %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_TOP)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_BASE)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Призма</strong>";
    }
  }
}
