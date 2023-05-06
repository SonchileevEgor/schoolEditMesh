package builders;

import bodies.ConeBody;
import bodies.LineBody;
import bodies.PointBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Builder of intersection cone and line
 *
 * @author Preobrazhenskaia
 */
public class LineXConeBuilder extends BodyBuilder {

  static public final String ALIAS = "LineXCone";

  public LineXConeBuilder() {
  }

  public LineXConeBuilder(String id, String name) {
    super(id, name);
  }

  public LineXConeBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineXConeBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_CONE, param.get(BLDKEY_CONE).asString());
  }

  public LineXConeBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, "Секущая прямая", BuilderParamType.BODY, 60);
    addParam(BLDKEY_CONE, "Конус", BuilderParamType.BODY, 61);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody l = (LineBody) bodies.get(getValueAsString(BLDKEY_LINE));
      if (!l.exists()) {
        throw new ExLostBody("прямая");
      }

      ConeBody c = (ConeBody) bodies.get(getValueAsString(BLDKEY_CONE));
      if (!c.exists()) {
        throw new ExLostBody("конус");
      }

      ArrayList<Vect3d> intersectPoints = c.cone().intersect(l.line());
      i_Body result;
      switch (intersectPoints.size()) {
        case 1:
          result = new PointBody(_id, intersectPoints.get(0));
          edt.addAnchor(intersectPoints.get(0), result, "A");
          break;
        case 2:
          Rib3d rib = new Rib3d(intersectPoints.get(0), intersectPoints.get(1));
          result = new RibBody(_id, rib);
          edt.addAnchor(intersectPoints.get(0), result, "A");
          edt.addAnchor(intersectPoints.get(1), result, "B");
          result.addRibs(edt);
          break;
        default:
          throw new ExDegeneration("");
      }
      _exists = true;
      return result;
    } catch (ExGeom | ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Пересечение прямой и конуса не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_CONE, getValueAsString(BLDKEY_CONE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение конуса %s</strong><br> прямой %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CONE)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение конуса прямой</strong>";
    }
  }
}
