package builders;

import bodies.RibBody;
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
import geom.Line3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PolyXPolyBuilder extends BodyBuilder {
  static public final String ALIAS = "PolyXPoly";
  public static final String BLDKEY_POLYGON1 = "poly1";
  public static final String BLDKEY_POLYGON2 = "poly2";

  public PolyXPolyBuilder() {
  }

  public PolyXPolyBuilder(String id, String name) {
    super(id, name);
  }

  public PolyXPolyBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PolyXPolyBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POLYGON1, param.get(BLDKEY_POLYGON1).asString());
    setValue(BLDKEY_POLYGON2, param.get(BLDKEY_POLYGON2).asString());
  }

  public PolyXPolyBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POLYGON1, "Первый многоугольник", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_POLYGON2, "Второй многоугольник", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    // TODO: рассмотреть случай пересечения не по внутренности.

    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor poly1 = anchors.get(getValueAsString(BLDKEY_POLYGON1));
      i_Anchor poly2 = anchors.get(getValueAsString(BLDKEY_POLYGON2));

      Line3d planeIntersect = poly1.getPoly().plane().intersectionWithPlane(poly2.getPoly().plane());
      ArrayList<Vect3d> intersect1 = poly1.getPoly().intersectBoundaryWithLine(planeIntersect);
      if (intersect1.size() < 2) {
        throw new ExGeom("грани не пересекаются");
      }
      ArrayList<Vect3d> intersect2 = poly2.getPoly().intersectBoundaryWithLine(planeIntersect);
      if (intersect2.size() < 2) {
        throw new ExGeom("грани не пересекаются");
      }
      intersect1.addAll(intersect2);
      Rib3d rib1 = new Rib3d(intersect1.get(0), intersect1.get(1));
      Rib3d rib2 = new Rib3d(intersect2.get(0), intersect2.get(1));
      Rib3d resultRib = rib1.intersectByOverlappingRib(rib2);
      RibBody result = new RibBody(_id, resultRib);
      edt.addAnchor(result.A(), result, "A");
      edt.addAnchor(result.B(), result, "B");
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExBadRef | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение граней не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POLYGON1, getValueAsString(BLDKEY_POLYGON1));
    result.add(BLDKEY_POLYGON2, getValueAsString(BLDKEY_POLYGON2));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Пересечение</strong> многоугольников %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON2)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
