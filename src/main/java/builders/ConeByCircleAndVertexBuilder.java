package builders;

import bodies.ConeBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.Cone3d;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs cone by its vertex and base circle
 *
 * @author alexeev
 */
public class ConeByCircleAndVertexBuilder extends BodyBuilder {
  public static final String ALIAS = "ConeByCircleAndVertex";

  public ConeByCircleAndVertexBuilder() {
  }

  public ConeByCircleAndVertexBuilder(String id, String name) {
    super(id, name);
  }

  public ConeByCircleAndVertexBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ConeByCircleAndVertexBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_VERTEX, param.get(BLDKEY_VERTEX).asString());
    setValue(BLDKEY_DISK, param.get(BLDKEY_DISK).asString());
  }

  public ConeByCircleAndVertexBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_VERTEX, "Вершина конуса", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_DISK, "Основание", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor base = anchors.get(getValueAsString(BLDKEY_DISK));
      i_Anchor vertex = anchors.get(getValueAsString(BLDKEY_VERTEX));

      Circle3d disk = base.getDisk();

      Vect3d p = vertex.getPoint();
      Vect3d proj = disk.plane().projectionOfPoint(p);

      if (!proj.equals(disk.center())) {
        throw new ExGeom("проекция вершины не совпадает с центром основания");
      }

      Cone3d cone = new Cone3d(p, disk.center(), disk.radius().norm());
      ConeBody result = new ConeBody(_id, title(), cone);
      result.addAnchor(ConeBody.KEY_CENTER, base.arrayIDs().get(0));
      result.addAnchor(ConeBody.KEY_VERTEX, vertex.id());
      result.addAnchor(ConeBody.KEY_BASE, base.id());
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Конус не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new ConeBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_DISK, getValueAsString(BLDKEY_DISK));
    result.add(BLDKEY_VERTEX, getValueAsString(BLDKEY_VERTEX));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Конус</strong> с вершиной %s и основанием %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_VERTEX)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_DISK)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Конус</strong>";
    }
  }
}
