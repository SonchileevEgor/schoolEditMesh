package bodies;

import editor.Editor;
import geom.AbstractPolygon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import util.Fatal;
import util.Util;

/**
 * Meta body - cone.
 *
 */
public class ConeBody extends BodyAdapter {
  private Cone3d _cone; // math object cone

  /**
   * Key for accessing vertex anchor ID field.
   */
  public static final String KEY_VERTEX = "V";

  /**
   * Key for accessing base center ID field.
   */
  public static final String KEY_CENTER = "C";

  /**
   * Key for accessing vertex base (disk) anchor ID field.
   */
  public static final String KEY_BASE = "disk";

  /**
   * Constructor of cone by id, title and math object cone
   * @param id
   * @param title
   * @param cone math object Cone3d
   */
  public ConeBody(String id, String title, Cone3d cone){
    super(id, title);
    _cone = cone;
    _alias = "конус";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public ConeBody(String id, String title) {
    super(id, title);
    _alias = "конус";
    _exists = false;
  }

  /**
   * @return heigh of cone; begin of vector is vertex, end of vector is center
   */
  public Vect3d height(){ return _cone.h(); }

  /**
   * @return cone vertex
   */
  public Vect3d vertex(){ return _cone.v(); }

  /**
   * @return center of cone bottom
   */
  public Vect3d center(){ return _cone.c(); }

  /**
   * @return r radius of cone bottom
   */
  public double radius(){ return _cone.r(); }

  public Cone3d cone() { return _cone; }

  /**
   * Construct cone section by plane
   * @param id id of cone section
   * @param title name of cone section
   * @param plane section plane
   * @return object cone section
   */
  public ConeSectionBody section(String id, String title, PlaneBody plane)
          throws ExGeom, ExZeroDivision {
    return new ConeSectionBody(id, title, _cone , plane.plane() );
  }

  @Override
  public BodyType type() {
    return BodyType.CONE;
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!exists())
      return;
    if (_state.isVisible()) {
      Drawer.setObjectCarcassColor(ren, _state);
      try {
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
        Drawer.drawConeCarcass(ren, _cone);
      } catch (ExDegeneration ex) {
        Fatal.warning("Не отрисованы образующие конуса");
      }
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      Drawer.setObjectFacetColor(ren, _state);
      Drawer.drawCone(ren, _cone.r(), _cone.c(), _cone.v(), TypeFigure.SOLID,
          (float)_state.getParam(DisplayParam.CARCASS_THICKNESS), false);
    }
  }

  /**
   * @return the circumscribed sphere
   * @throws geom.ExDegeneration
   */
  public Sphere3d outSphere() throws ExDegeneration{
    return _cone.outSphere();
  }

    /**
   * @return center of the circumscribed sphere
   * @throws geom.ExDegeneration
   */
  public Vect3d outCenter() throws ExDegeneration{
    return outSphere().center();
  }

  /**
   * @return radius of the circumscribed sphere
   * @throws geom.ExDegeneration
   */
  public double outRadius() throws ExDegeneration{
    return outSphere().radius();
  }

  /**
   * @return the inscribed sphere
   */
  public Sphere3d inSphere(){
    return _cone.inSphere();
  }

  /**
   * @return center of the inscribed sphere
   */
  public Vect3d inCenter(){
    return inSphere().center();
  }

  /**
   * @return radius of the inscribed sphere
   */
  public double inRadius(){
    return inSphere().radius();
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
      ArrayList<Polygon3d> faces = _cone.faces();
      return faces;
  }

  public ArrayList<Vect3d> getBasePoints() {
      ArrayList<Vect3d> base_points = _cone.getBaseCirclePoints();
      return base_points;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try{
      return _cone.intersectWithPlane(plane);
    } catch (ExGeom ex) {
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _cone;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new ConeBody (id, title, (Cone3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    ConeBody cone = (ConeBody) result;
    edt.addAnchor(cone.vertex(), result, KEY_VERTEX);
    edt.addAnchor(cone.center(), result, KEY_CENTER);
    Circle3d disk = cone._cone.getBaseCircle();
    edt.addAnchor(disk, result.getAnchorID(KEY_CENTER) , result, KEY_BASE);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_cone.intersect(ray), ren.getCameraPosition().eye());
  }
};