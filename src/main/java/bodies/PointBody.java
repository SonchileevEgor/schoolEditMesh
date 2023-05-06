package bodies;

import static config.Config.GUI_EPS;
import editor.Editor;
import geom.AbstractPolygon;
import opengl.Render;
import editor.behavior.Behavior;
import editor.behavior.BehaviorFactory;
import geom.i_Geom;
import editor.behavior.i_PointMoveBehavior;
import editor.i_Body;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Vect3d;
import java.util.ArrayList;
import opengl.CalculatorGL;

/**
 * Meta body - 3d point.
 *
 */
public class PointBody extends BodyAdapter {
  public static final String BODY_KEY_POINT = "P";
  
  private Vect3d _point; // math object point
  private i_PointMoveBehavior _behavior;

  /**
   * Constructor of point by math object point
   * @param id
   * @param p
   */
  public PointBody(String id, Vect3d p){
    super(id, "");
    _point = new Vect3d(p.x(), p.y(), p.z());
    _behavior = BehaviorFactory.createFixedBehavior();
    _alias = "точка";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   */
  public PointBody(String id) {
    super(id, "");
    _alias = "точка";
    _exists = false;
  }

  /**
   * Getter of math object point
   * @return
   */
  public Vect3d point(){ return _point; }
  
  @Override
  public String getTitle() {
    return _exists ? "%" + getAnchorID(BODY_KEY_POINT) + "%" : "?";
  }

  @Override
  public BodyType type() {
    return BodyType.POINT;
  }

  @Override
  public void glDrawCarcass(Render ren){
    // Point has not carcass
  }

  @Override
  public void glDrawFacets(Render ren){
    // Point has not facets
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    if (plane.containsPoint(_point) ){
      return _point;
    } else {
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _point;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new PointBody(id, (Vect3d) geom);
  }

  public i_PointMoveBehavior getBehavior() {
    return _behavior;
  }

  public void setBehavior(i_PointMoveBehavior behavior) {
    _behavior = behavior;
  }

  public boolean isMovable() {
    return _behavior.type() != Behavior.FIXED;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    PointBody point = (PointBody) result;
    edt.addAnchor(point.point(), result, BODY_KEY_POINT);
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    double[] coord = new double[2];
    CalculatorGL.getDisplayCoord(ren, _point, coord);
    if (Math.abs(coord[0] - x) <= GUI_EPS.value() &&
                Math.abs(y - (ren.getHeight() - coord[1])) <= GUI_EPS.value()) {
      return _point.duplicate();
    } else {
      return null;
    }
  }
};