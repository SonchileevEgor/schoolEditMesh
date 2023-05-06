package builders;

import bodies.PyramidBody;
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
import geom.Pyramid3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a pyramid by the list of base vertices and the top vertex
 * @author Knyazev Vladislav
 */
public class PyramidBuilder extends BodyBuilder {
  static public final String ALIAS = "Pyramid";

  public PyramidBuilder() {
  }
  
  public PyramidBuilder(String name) {
    super(name);
  }

  public PyramidBuilder(String id, String name) {
    super(id, name);
  }

  public PyramidBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PyramidBuilder (String id, String name, JsonObject param) {
    super(id, name);
    ArrayList<String> points = new ArrayList<>();
    for (int j = 0; j < param.size(); j++) {
      points.add(param.get(String.valueOf(j+1)).asString());
    }
    setValue(BLDKEY_POINTS, points);
  }

  public PyramidBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINTS, "Точки пирамиды", BuilderParamType.ANCHOR_LIST);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      ArrayList<Vect3d> points = new ArrayList<>();
      for (int j = 0; j < ((ArrayList<String>)getValue(BLDKEY_POINTS)).size(); j++) {
        Vect3d a = anchors.getVect(((ArrayList<String>)getValue(BLDKEY_POINTS)).get(j));
        points.add(a);
      }
      PyramidBody result = new PyramidBody(_id, title(), new Pyramid3d(points));
      result.addAnchor(BLDKEY_TOP, anchors.get(((ArrayList<String>)getValue(BLDKEY_POINTS)).get(result.pyramid().points().size() - 1)).id());
      for (int j = 0; j < result.pyramid().points().size() - 1; j++){
        result.addAnchor(String.valueOf(j), anchors.get(((ArrayList<String>)getValue(BLDKEY_POINTS)).get(j)).id());
      }
      // ограничиваем передвижение всех точек со свободным перемещением.
      Plane3d base = result.pyramid().base().plane();
      fixPointsOnPlane(edt, result.baseAnchorIDs(), base);

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Пирамида не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new PyramidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    for (int j = 0; j < ((ArrayList<String>)getValue(BLDKEY_POINTS)).size(); j++) {
      result.add(Integer.toString(j + 1), ((ArrayList<String>)getValue(BLDKEY_POINTS)).get(j));
    }

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      StringBuilder format = new StringBuilder("<html><strong>%s-угольная"
              + " пирамида</strong><br>с вершиной %s<br>и основанием ");
      ArrayList<String> points = (ArrayList<String>)getValue(BLDKEY_POINTS);
      for (int i = 0; i < points.size() - 1; i++) {
        format.append("%s");
      }
      ArrayList<String> args = new ArrayList<>();
      args.add(String.valueOf(points.size() - 1));
      args.add(ctrl.getAnchor(points.get(points.size() - 1)).getTitle());
      for (int j = 0; j < points.size() - 1; j++) {
        args.add(ctrl.getAnchor(points.get(j)).getTitle());
      }
      return String.format(format.toString(), args.toArray());
    } catch (ExNoAnchor ex) {
      return "<html><strong>Пирамида</strong>";
    }
  }
}