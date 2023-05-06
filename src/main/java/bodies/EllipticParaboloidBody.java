package bodies;

import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.EllipticParaboloid3d;
import geom.Line3d;
import geom.NewSystemOfCoor;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import util.Util;

/**
 *
 * @author Vitaliy
 */
public class EllipticParaboloidBody extends BodyAdapter {

  private EllipticParaboloid3d _paraboloid; //math object paraboloid

  /**
   * Constructor of paraboloid by math paraboloid
   * @param id
   * @param title
   * @param paraboloid math object elliptic paraboloid
   */
  public EllipticParaboloidBody(String id, String title, EllipticParaboloid3d paraboloid) {
    super(id, title);
    _paraboloid = paraboloid;
    _alias = "эллиптический параболоид";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public EllipticParaboloidBody(String id, String title) {
    super(id, title);
    _alias = "эллиптический параболоид";
    _exists = false;
  }

  public EllipticParaboloid3d paraboloid() {
    return _paraboloid;
  }

  /**
   * @return focus of the paraboloid
   */
  public Vect3d f() {
    return _paraboloid.f();
  }

  /**
   * @return point of directrix
   */
  public Vect3d getPointOnLine() {
    return _paraboloid.directrix().pnt();
  }

  /**
   * @return other point of directrix
   */
  public Vect3d getSecondPointOnLine() {
    return _paraboloid.directrix().pnt2();
  }

  /**
   * @return directrix of paraboloid
   */
  public Line3d directrix() {
    return _paraboloid.directrix();
  }

  /**
   * @return new system of coordinate in center of paraboloid
   */
  public NewSystemOfCoor centerParaboloid() {
    return _paraboloid.newSystemOfCoorParaboloid();
  }

  @Override
  public void glDrawFacets(Render ren) {
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      
      Drawer.setObjectFacetColor(ren, _state);
      Drawer.drawEllipticParaboloidFacets(ren, paraboloid());
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
      Drawer.drawEllipticParaboloid(ren, _paraboloid, TypeFigure.CURVE);
    }
  }

  @Override
  public BodyType type() {
    return BodyType.ELLIPTIC_PARABOLOID;
  }

  @Override
  public i_Geom getGeom() {
    return _paraboloid;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new EllipticParaboloidBody(id, title, (EllipticParaboloid3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    EllipticParaboloidBody el_par = (EllipticParaboloidBody) result;
    edt.addAnchor(el_par.f(), result, "focus");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    EllipticParaboloidBody el_par = (EllipticParaboloidBody) result;
    edt.addAnchor(el_par.getPointOnLine(), result, "pOnLine1");
    edt.addAnchor(el_par.getSecondPointOnLine(), result, "pOnLine2");
    edt.addAnchor(el_par.f(), result, "focus");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: реализовать функцию
    return null;
  }
}
