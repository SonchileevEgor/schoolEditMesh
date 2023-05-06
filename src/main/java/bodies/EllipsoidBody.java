package bodies;

import opengl.colorgl.ColorGL;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.EllipseMain3d;
import geom.Ellipsoid3d;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.NewSystemOfCoor;
import geom.Plane3d;
import geom.Ray3d;
import geom.Sphere3d;
import geom.Vect3d;
import geom.i_Geom;
import util.Util;

/**
 *
 * @author VitaliiZah
 */
public class EllipsoidBody extends BodyAdapter {
  private Ellipsoid3d _ellipsoid; // math object ellipsoid

  /**
   * Constructor of ellipsoid by math ellipsoid
   * @param id
   * @param title
   * @param ellipsoid math object Ellipsoid3d
   */
  public EllipsoidBody(String id, String title, Ellipsoid3d ellipsoid) {
    super(id, title);
    _alias = "эллипсоид";
    _ellipsoid = ellipsoid;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public EllipsoidBody(String id, String title) {
    super(id, title);
    _alias = "эллипсоид";
    _exists = false;
  }

  public Ellipsoid3d ellipsoid() {
    return _ellipsoid;
  }

  /**
   * @return first focus of the ellipsoid
   */
  public Vect3d f1() {
    return _ellipsoid.f1();
  }

  /**
   * @return second focus of the ellipsoid
   */
  public Vect3d f2() {
    return _ellipsoid.f2();
  }

  /**
   * @return point of ellipsoid
   */
  public Vect3d pOnBound() {
    return _ellipsoid.pOnBound();
  }

  /**
   * @return new system of coordinate in center of ellipsoid
   */
  public NewSystemOfCoor centerEllipsoid() {
    return _ellipsoid.newSystemOfCoorEllipsoid();
  }
  
  public Vect3d center() {
    return _ellipsoid.center();
  }

  /**
   * @return modul sum distance from bound to focus f1 and f2
   */
  public double distance() {
    return _ellipsoid.distance();
  }

  /**
   * @return the circumscribed sphere
   * @throws geom.ExDegeneration
   */
  public Sphere3d outSphere() throws ExDegeneration {
    return _ellipsoid.outSphere();
  }

  /**
   * @return the inscribed sphere
   * @throws geom.ExDegeneration
   */
  public Sphere3d inSphere() throws ExDegeneration {
    return _ellipsoid.inSphere();
  }

  /**
   * transform ellipsoid in sphere
   * @return 
   */
  public Sphere3d convSphere() {
   return _ellipsoid.convEllipsoidInSphere();
  }
  
  /**
   * transform plane
   * @param plane
   * @return
   * @throws ExDegeneration 
   */
  public Plane3d convPlane(Plane3d plane) throws ExDegeneration {
    return _ellipsoid.convPlane(new Plane3d(plane.n(), plane.pnt()));
  }
  
  /**
   * inverse transform point
   * @param point
   * @return 
   */
  public Vect3d reconvPoint(Vect3d point) {
    return  _ellipsoid.reconvPoint(new Vect3d(point));
  }
  
  @Override
  public BodyType type() {
    return BodyType.ELLIPSOID;
  }

  public double angle() {
    return Vect3d.getAngle(Vect3d.UX, _ellipsoid.f1().sub(_ellipsoid.center()));
  }
  
  @Override
  public void addRibs(Editor edt) {
    // no ribs
  }

  @Override
  public void glDrawFacets(Render ren) {
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      
      Drawer.setObjectFacetColor(ren, _state);
      Drawer.drawEllipsoidFacets(ren, _ellipsoid.center(), _ellipsoid.smallAxle(), 
          _ellipsoid.bigAxle() / _ellipsoid.smallAxle(), _ellipsoid.center(), _ellipsoid.f1());
    }
  }

  @Override
  public void glDrawCarcass(Render ren) {
    if (!exists()) {
      return;
    }
    if (_state.isVisible()) {
      Drawer.setObjectColor(ren, (ColorGL) _state.getParam(DisplayParam.CARCASS_COLOR));
      Drawer.setLineWidth(ren, (float) _state.getParam(DisplayParam.CARCASS_THICKNESS));
      Drawer.drawEllipsoid(ren, _ellipsoid, TypeFigure.WIRE);
    }
  }

  /**
   * Constructs section of this ellipsoid by the plane
   * @param id id of a new ellipse
   * @param title
   * @param p section plane
   * @return section ellipse
   * @throws ExGeom
   */
  public EllipseMainBody section(String id, String title, PlaneBody p)
    throws ExGeom {
    try {
      EllipseMain3d el = _ellipsoid.sectionByPlane(new Plane3d(p.normal(), p.point()));
      return new EllipseMainBody(id, title, el);
    } catch (NullPointerException ex) {
      throw new ExGeom("секущая плоскость не была построена");
    }
  }
  
  @Override
  public i_Geom getGeom() {
    return _ellipsoid;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new EllipsoidBody(id, title, (Ellipsoid3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    EllipsoidBody eld = (EllipsoidBody) result;
    edt.addAnchor(eld.f1(), result, "f1");
    edt.addAnchor(eld.f2(), result, "f2");
    edt.addAnchor(eld.pOnBound(), result, "pointOnBound");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_ellipsoid.intersect(ray), ren.getCameraPosition().eye());
  }
}
