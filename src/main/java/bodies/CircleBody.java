package bodies;

import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import opengl.Render;

/**
 * Meta body - circle.
 *
 */
public class CircleBody extends BodyAdapter implements i_PlainBody {
  public static final String BODY_KEY_CENTER = "C";
  public static final String BODY_KEY_DISK = "disk";

  private Circle3d _circle; // math object circle

  /**
   * Constructor of circle by math object circle
   * @param id
   * @param title
   * @param circle math object Circle3d
   */
  public CircleBody(String id, String title, Circle3d circle){
    super(id, title);
    _circle = circle;
    _alias = "круг";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public CircleBody(String id, String title) {
    super(id, title);
    _alias = "круг";
    _exists = false;
  }

  public Circle3d circle() { return _circle; }

  /**
   * @return center of the circle
   */
  public Vect3d center(){
    return _circle.center();
  }

  /**
   * @return radius of the circle
   */
  public Vect3d radius(){
    return _circle.radius();
  }

  /**
   * @return normal vector of the plane
   */
  public Vect3d normal(){
    return _circle.normal();
  }

  /**
   * @return plane which contains this circle
   */
  @Override
  public Plane3d plane(){
    return new Plane3d(_circle.normal(), _circle.center());
  }

  @Override
  public BodyType type() {
    return BodyType.CIRCLE;
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = _circle.faces();
    return faces;
  }

  @Override
  public i_Geom getGeom() {
    return _circle;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
  return new CircleBody (id, title, (Circle3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    CircleBody circ = (CircleBody) result;
    edt.addAnchor(circ.center(), result, BODY_KEY_CENTER);
    edt.addAnchor(circ.circle(), result.getAnchorID(BODY_KEY_CENTER), result, BODY_KEY_DISK);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return _circle.intersectWithRayTransversal(ray);
  }
};