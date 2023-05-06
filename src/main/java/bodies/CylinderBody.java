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
import util.Util;

/**
 * Metabody - cylinder.
 *
 * @author alexeev
 */
public class CylinderBody extends BodyAdapter {

  /**
   * Constructor of cylinder by id, title and math object cylinder
   * @param id
   * @param title
   * @param cylinder math object Cone3d
   */
  public CylinderBody(String id, String title, Cylinder3d cylinder){
    super(id, title);
    _cylinder = cylinder;
    _alias = "цилиндр";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public CylinderBody(String id, String title) {
    super(id, title);
    _alias = "цилиндр";
    _exists = false;
  }

  /**
   * @return height of cylinder
   * begin of vector is center of the first base
   * end of vector is center of the second base
   */
  public Vect3d height(){ return _cylinder.h(); }

  /**
   * @return center of cylinder's first base
   */
  public Vect3d center1(){ return _cylinder.c0(); }

  /**
   * @return center of cylinder's second base
   */
  public Vect3d center2(){ return _cylinder.c1(); }

  /**
   * @return r radius of cone bottom
   */
  public double radius(){ return _cylinder.r(); }

  public Cylinder3d cylinder() { return _cylinder; }

  /**
   * Construct cylinder section by plane
   * @param id id of cylinder section
   * @param title name of cylinder section
   * @param plane section plane
   * @return object cylinder section
   * @throws geom.ExGeom
   * @throws geom.ExZeroDivision
   */
  public CylinderSectionBody section(String id, String title, PlaneBody plane) throws ExGeom, ExZeroDivision{
    return new CylinderSectionBody(id, title, _cylinder, plane.plane());
  }

  /**
   * constructs a cylinder outsphere
   * @return math object sphere
   */
  public Sphere3d outSphere(){
    return _cylinder.outSphere();
  }

  /**
   * constructs a cylinder insphere
   * @return math object sphere
   * @throws geom.ExDegeneration
   */
  public Sphere3d inSphere() throws ExDegeneration {
    return _cylinder.inSphere();
  }

  @Override
  public BodyType type() {
    return BodyType.CYLINDER;
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!exists())
      return;
    if (_state.isVisible()) {
      Drawer.setObjectCarcassColor(ren, _state);
      Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
      Drawer.drawCylinder(ren, _cylinder.r(), _cylinder.c0(), _cylinder.c1(), TypeFigure.WIRE,
          (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      Drawer.setObjectFacetColor(ren, _state);
      Drawer.drawCylinder(ren, _cylinder.r(), _cylinder.c0(), _cylinder.c1(), TypeFigure.SOLID,
          (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
    }
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
      ArrayList<Polygon3d> faces = _cylinder.faces();
      return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return _cylinder.sectionByPlane(plane);
    } catch (ExGeom | ExZeroDivision ex) {
      return super.getIntersectionWithPlane(plane);
    }
  }

  private Cylinder3d _cylinder; // math object cone

  @Override
  public i_Geom getGeom() {
    return _cylinder;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new CylinderBody (id, title, (Cylinder3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    CylinderBody cyl = (CylinderBody) result;
    edt.addAnchor(cyl.center1(), result, "c1");
    edt.addAnchor(cyl.center2(), result, "c2");
    edt.addAnchor(cyl.cylinder().circ1(), result.getAnchorID("c1"), result, "disk1");
    edt.addAnchor(cyl.cylinder().circ2(), result.getAnchorID("c2"), result, "disk2");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_cylinder.intersect(ray), ren.getCameraPosition().eye());
  }
}
