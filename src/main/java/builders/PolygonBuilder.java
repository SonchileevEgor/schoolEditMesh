package builders;

import bodies.PolygonBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a polygon by list of its vertices
 *
 * @author Vlad Knyazev
 */
public class PolygonBuilder extends BodyBuilder {
  static public final String ALIAS = "Polygon";

  public PolygonBuilder() {
  }

  public PolygonBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PolygonBuilder(String id, String name, JsonObject param) {
    super(id);
    ArrayList<String> points = new ArrayList<>();
    for (int j = 0; j < param.size(); j++) {
      String a = param.get(String.valueOf(j + 1)).asString();
      points.add(a);
    }
    addPoints(points);
  }

  public PolygonBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addPoints(ArrayList<String> ids){
    setValue(BLDKEY_POINTS, ids);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINTS, "Точки многоугольника", BuilderParamType.ANCHOR_LIST);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      ArrayList<Vect3d> points = new ArrayList<>();
      for (int j = 0; j < ((ArrayList<String>) getValue(BLDKEY_POINTS)).size(); j++) {
        Vect3d a = anchors.getVect(((ArrayList<String>) getValue(BLDKEY_POINTS)).get(j));
        points.add(a);
      }
      PolygonBody result = new PolygonBody(_id, points);
      for (int j = 0; j < result.polygon().vertNumber(); j++) {
        result.addAnchor(String.valueOf(j), anchors.get(((ArrayList<String>) getValue(BLDKEY_POINTS)).get(j)).id());
      }

      // ограничиваем передвижение всех точек со свободным перемещением.
      Plane3d polygonPlane = result.polygon().plane();
      fixPointsOnPlane(edt, result.getAnchors().values(), polygonPlane);

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Многоугольник не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PolygonBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    for (int j = 0; j < ((ArrayList<String>) getValue(BLDKEY_POINTS)).size(); j++) {
      result.add(Integer.toString(j + 1), ((ArrayList<String>) getValue(BLDKEY_POINTS)).get(j));
    }
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      StringBuilder format = new StringBuilder("<html><strong>%s-угольник </strong>");
      ArrayList<String> points = (ArrayList<String>) getValue(BLDKEY_POINTS);
      for (int i = 0; i < points.size(); i++) {
        format.append("%s");
      }
      ArrayList<String> args = new ArrayList<>();
      args.add(String.valueOf(points.size()));
      for (int j = 0; j < points.size(); j++) {
        args.add(ctrl.getAnchor(points.get(j)).getTitle());
      }
      return String.format(format.toString(), args.toArray());
    } catch (ExNoAnchor ex) {
      return "<html><strong>Многоугольник</strong>";
    }
  }
}
