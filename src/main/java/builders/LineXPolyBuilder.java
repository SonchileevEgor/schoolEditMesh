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
import geom.Checker;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class LineXPolyBuilder extends BodyBuilder {

  static public final String ALIAS = "LineXPoly";

  public LineXPolyBuilder() {
  }

  public LineXPolyBuilder(String id, String name) {
    super(id, name);
  }

  public LineXPolyBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineXPolyBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
  }

  public LineXPolyBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      i_Body body = bodies.get(getValueAsString(BLDKEY_LINE));
      if (!body.exists()) {
        throw new ExLostBody("прямая");
      }
      LineBody line = (LineBody) body;
      i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));

      Vect3d intersect = line.line().intersectionWithPlane(poly.getPoly().plane());
      if (!Checker.isPointOnClosePolygon(poly.getPoly(), intersect)) {
        throw new ExGeom("прямая и многоугольник не пересекаются");
      }

      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch (ExBadRef | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение прямой и плоскости не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong><br> прямой %s<br> и многоугольника %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch (ExBadRef ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
