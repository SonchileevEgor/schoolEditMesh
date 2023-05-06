package bodies;

import static config.Config.LOG_LEVEL;
import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import util.Log;

/**
 * Meta body - polygon.
 *
 */
public class PolygonBody extends BodyAdapter implements i_PlainBody {
  public static final String BODY_KEY_POLYGON = "facet";
  private Polygon3d _polygon; // Math object polygon
  
  /**
   * Constructor of polygon by the geom object.
   * @param id
   * @param poly
   */
  public PolygonBody(String id, Polygon3d poly) {
    super(id, "");
    _polygon = poly;
    _alias = "многоугольник";
    _exists = true;
    _isRenamable = false;
  }

  /**
   * Constructor of polygon by list of points
   * @param id
   * @param points list of points
   * @throws geom.ExGeom
   */
  public PolygonBody(String id, ArrayList<Vect3d> points) throws ExGeom {
    super(id, "");
    _polygon = new Polygon3d(points);
    _alias = "многоугольник";
    _exists = true;
    _isRenamable = false;
  }

  /**
   * constructor for null-body
   * @param id
   */
  public PolygonBody(String id) {
    super(id, "");
    _alias = "многоугольник";
    _exists = false;
    _isRenamable = false;
  }

  /**
   * @return in circle
   * @throws geom.ExGeom
   */
  public Circle3d inCircle() throws ExGeom {
    return _polygon.inCircle();
  }

  /**
   * @return out circle
   * @throws geom.ExGeom
   */
  public Circle3d outCircle() throws ExGeom {
    return _polygon.outCircle();
  }

  /**
   * @return math object polygon
   */
  public Polygon3d polygon(){
    return _polygon;
  }

  @Override
  public Plane3d plane() {
    try {
      return _polygon.plane();
    } catch( ExDegeneration ex ){
      return Plane3d.oXY();
    }
  }

  @Override
  public BodyType type() {
    return BodyType.POLYGON;
  }

  @Override
  public void addRibs(Editor edt){
    try {
      ArrayList<Vect3d> points = _polygon.points();
      int num = _polygon.vertNumber();
      for(int i = 0; i < num; i++){
        String idA = getAnchorID(String.valueOf(i));
        String idB = getAnchorID(String.valueOf((i + 1) % num));

        Rib3d rib = new Rib3d(points.get(i),points.get ((i + 1) % num));
        edt.addAnchor(rib, idA, idB, this, "rib_" + String.valueOf(i) + String.valueOf((i + 1) % num));
      }
    } catch(ExDegeneration ex) {}
  }

  @Override
  public void addPlanes(Editor edt){
    int num = _polygon.vertNumber();
    ArrayList<String> pointIDs = new ArrayList<>();
    for(int i = 0; i < num; i++){
      String idA = getAnchorID(String.valueOf(i));
      pointIDs.add(idA);
    }
    edt.addAnchor(_polygon, pointIDs, this, BODY_KEY_POLYGON);
  }
  
  @Override
  public String getTitle() {
    return _exists ? "%" + getAnchorID(BODY_KEY_POLYGON) + "%" : "?";
  }

  @Override
  public void glDrawFacets(Render ren){
    // рисуются только якоря
  }

  @Override
  public void glDrawCarcass(Render ren){
    // рисуются только якоря
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    try {
      faces = _polygon.faces();
    } catch (ExGeom ex) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.println(ex.getMessage());
      }
    }
    return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    //TODO: add intersection with plane and return as abstract polygon.
    return super.getIntersectionWithPlane(plane);
  }

  @Override
  public i_Geom getGeom() {
    return _polygon;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new PolygonBody(id, (Polygon3d)geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    PolygonBody poly = (PolygonBody) result;
    for (int j = 0; j < poly.polygon().points().size(); j++) {
      edt.addAnchor(poly.polygon().points().get(j), result, String.valueOf(j));
    }
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return _polygon.intersectWithRayTransversal(ray);
  }
};
