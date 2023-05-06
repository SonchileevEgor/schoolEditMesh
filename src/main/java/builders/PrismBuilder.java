package builders;

import bodies.PrismBody;
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
import geom.Prism3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a prism by the list of base vertices and the first vertex of
 * another base
 *
 * @author alexeev
 */
public class PrismBuilder extends BodyBuilder {
  static public final String ALIAS = "Prism";

  public PrismBuilder() {
  }
  
  public PrismBuilder(String name) {
    super(name);
  }

  public PrismBuilder(String id, String name) {
    super(id, name);
  }

  public PrismBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PrismBuilder(String id, String name, JsonObject param) {
    super(id, name);
    ArrayList<String> points = new ArrayList<>();
    for (int j = 0; j < param.size(); j++) {
      String a = param.get(String.valueOf(j + 1)).asString();
      points.add(a);
    }
    setValue(BLDKEY_POINTS, points);
  }

  public PrismBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINTS, "Точки призмы", BuilderParamType.ANCHOR_LIST);
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
      PrismBody result = new PrismBody(_id, title(), new Prism3d(points));

      ArrayList<String> pointID = (ArrayList<String>) getValue(BLDKEY_POINTS);
      // добавляем якоря из списка параметров построения
      for (int i = 0; i < pointID.size(); i++) {
        result.addAnchor(String.valueOf(i), pointID.get(i));
      }
      // добавляем новые якоря
      for (int i = pointID.size(); i < result.prism().points().size(); i++) {
        edt.addAnchor(result.prism().points().get(i), result, String.valueOf(i));
      }

      // ограничиваем передвижение всех точек со свободным перемещением.
      Plane3d base = result.prism().base1().plane();
      fixPointsOnPlane(edt, result.baseAnchorIDs(), base);

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
    for (int j = 0; j < ((ArrayList<String>) getValue(BLDKEY_POINTS)).size(); j++) {
      result.add(Integer.toString(j + 1), ((ArrayList<String>) getValue(BLDKEY_POINTS)).get(j));
    }
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      StringBuilder format = new StringBuilder("<html><strong>%s-угольная"
              + " призма</strong><br> с вершиной %s и основанием ");
      ArrayList<String> points = (ArrayList<String>) getValue(BLDKEY_POINTS);
      for (int i = 0; i < points.size() - 1; i++) {
        format.append("%s");
      }
      ArrayList<String> args = new ArrayList<>();
      args.add(String.valueOf(points.size()));
      args.add(ctrl.getAnchor(points.get(points.size() - 1)).getTitle());
      for (int j = 0; j < points.size() - 1; j++) {
        args.add(ctrl.getAnchor(points.get(j)).getTitle());
      }
      return String.format(format.toString(), args.toArray());
    } catch (ExNoAnchor ex) {
      return "<html><strong>Призма</strong>";
    }
  }
}
