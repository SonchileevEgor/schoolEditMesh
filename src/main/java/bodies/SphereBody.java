package bodies;

import editor.Editor;
import geom.EmptyPolygon;
import geom.AbstractPolygon;
import opengl.Drawer;
import opengl.Render;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.*;
import util.Util;

/**
 * Meta body - sphere.
 * @author ?
 */
public class SphereBody extends BodyAdapter {
  public static final String BODY_KEY_CENTER = "center";

  private Sphere3d _sph; // math object sphere

  /**
   * Constructor of sphere by math object sphere
   * @param id
   * @param title
   * @param sph math object sphere
   */
  public SphereBody (String id, String title, Sphere3d sph){
    super(id, title);
    _sph = sph;
    _alias = "сфера";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public SphereBody(String id, String title) {
    super(id, title);
    _alias = "сфера";
    _exists = false;
  }

  public Sphere3d sphere() {
    return _sph;
  }

  /**
   * @return volume of the sphere
   */
  public double volume(){
    return _sph.volume();
  }

  /**
   * Constructs section of this sphere by the plane
   * @param id id of a new circle
   * @param title
   * @param p section plane
   * @return section circle
   * @throws ExGeom
   */
  public CircleBody section(String id, String title, PlaneBody p)
    throws ExGeom {
    try {
      Circle3d c = _sph.sectionByPlane(new Plane3d(p.normal(), p.point()));
      return new CircleBody(id, title, c);
    } catch (NullPointerException ex) {
      throw new ExGeom("секущая плоскость не была построена");
    }
  }

  @Override
  public BodyType type() {
    return BodyType.SPHERE;
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!exists())
      return;
    if (_state.isVisible()) {
      Drawer.setObjectCarcassColor(ren, _state);
      Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
      Drawer.drawSphereCircles(ren, _sph.center(), _sph.radius());
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      Drawer.setObjectFacetColor(ren, _state);
      Drawer.drawSphereFacets(ren, _sph.center(), _sph.radius(),
          (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
    }
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return _sph.sectionByPlane(plane);
    } catch (ExGeom ex){
      return new EmptyPolygon();
    }
  }

  public Vect3d getCenter() {
    return _sph.center();
  }

  public double getRadius() {
    return _sph.radius();
  }

  @Override
  public i_Geom getGeom() {
    return _sph;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new SphereBody (id, title, (Sphere3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    SphereBody sph = (SphereBody) result;
    edt.addAnchor(sph.getCenter(), result, BODY_KEY_CENTER);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_sph.intersect(ray), ren.getCameraPosition().eye());
  }
};