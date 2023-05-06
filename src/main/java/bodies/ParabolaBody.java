package bodies;

import opengl.colorgl.ColorGL;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.ExDegeneration;
import geom.Line3d;
import geom.NewSystemOfCoor;
import geom.Parabola3d;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;
import util.Util;

/**
 *
 * @author VitaliiZah
 */
public class ParabolaBody extends BodyAdapter {

  private Parabola3d _parabola; //math object parabola

  /**
   * Constructor of parabola by math parabola
   * @param id
   * @param title
   * @param parabola math object parabola
   */
  public ParabolaBody(String id, String title, Parabola3d parabola) {
    super(id, title);
    _parabola = parabola;
    _alias = "парабола";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public ParabolaBody(String id, String title) {
    super(id, title);
    _alias = "парабола";
    _exists = false;
  }

  public Parabola3d parabola() {
    return _parabola;
  }

  /**
   * @return focus of the parabola
   */
  public Vect3d f() {
    return _parabola.f();
  }

  /**
   * @return point of directrix
   */
  public Vect3d getPointOnLine() {
    return _parabola.directrix().pnt();
  }

  /**
   * @return other point of directrix
   */
  public Vect3d getSecondPointOnLine() {
    return _parabola.directrix().pnt2();
  }

  /**
   * @return directrix of parabola
   */
  public Line3d directrix() {
    return _parabola.directrix();
  }

  /**
   * @return new system of coordinate in center of parabola
   */
  public NewSystemOfCoor centerParabola() {
    return _parabola.newSystemOfCoorParabola();
  }

  /**
   * @return plane of parabola
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return _parabola.plane();
  }

  @Override
  public void glDrawCarcass(Render ren) {
    if (!exists()) {
      return;
    }
    if (_state.isVisible()) {
      Drawer.setObjectColor(ren, (ColorGL) _state.getParam(DisplayParam.CARCASS_COLOR));
      if (_state.isChosen()) {
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
      } else {
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
      }
      Drawer.drawParabola(ren, _parabola, TypeFigure.CURVE);
    }
  }

  @Override
  public BodyType type() {
    return BodyType.PARABOLA;
  }

  @Override
  public i_Geom getGeom() {
    return _parabola;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new ParabolaBody(id, title, (Parabola3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    ParabolaBody el = (ParabolaBody) result;

    edt.addAnchor(el.f(), result, "focus");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    ParabolaBody par = (ParabolaBody) result;
    edt.addAnchor(par.getPointOnLine(), result, "pOnLine1");
    edt.addAnchor(par.getSecondPointOnLine(), result, "pOnLine2");
    edt.addAnchor(par.f(), result, "focus");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: Реализовать функцию.
    return Util.getClosestPointToCamera(_parabola.intersect(ray), ren.getCameraPosition().eye());
  }
}
