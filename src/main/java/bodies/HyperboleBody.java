package bodies;

import opengl.colorgl.ColorGL;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.ExDegeneration;
import geom.Hyperbole3d;
import geom.Line3d;
import geom.NewSystemOfCoor;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;

/**
 *
 * @author VitaliiZah
 */
public class HyperboleBody extends BodyAdapter {
  private Hyperbole3d _hyperbole; // math object hyperbole

  /**
   * Constructor of hyperbole by math hyperbole
   * @param id
   * @param title
   * @param hyperbole math object Hyperbole3d
   */
  public HyperboleBody(String id, String title, Hyperbole3d hyperbole) {
    super(id, title);
    _alias = "гипербола";
    _hyperbole = hyperbole;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public HyperboleBody(String id, String title) {
    super(id, title);
    _alias = "гипербола";
    _exists = false;
  }

  public Hyperbole3d hyperbole() {
    return _hyperbole;
  }

  /**
   * @return first focus of the hyperbole
   */
  public Vect3d f1() {
    return _hyperbole.f1();
  }

  /**
   * @return second focus of the hyperbole
   */
  public Vect3d f2() {
    return _hyperbole.f2();
  }

  /**
   * @return normal vector of hyperbole
   * @throws geom.ExDegeneration
   */
  public Vect3d normal() throws ExDegeneration {
    return _hyperbole.normal();
  }

  /**
   * @return new system of coordinate in center of hyperbole
   */
  public NewSystemOfCoor centerHyperbole() {
    return _hyperbole.newSystemOfCoorHyperb();
  }

  /**
   * @return plane of hyperbole
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return _hyperbole.plane();
  }

  @Override
  public BodyType type() {
    return BodyType.HYPERBOLE;
  }

  @Override
  public i_Geom getGeom() {
    return _hyperbole;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new HyperboleBody(id, title, (Hyperbole3d) geom);
  }

  @Override
  public void glDrawCarcass(Render ren) {
    if (!exists()) {
      return;
    }
    if (getState().isVisible()) {
      Drawer.setObjectColor(ren, (ColorGL) getState().getParam(DisplayParam.CARCASS_COLOR));
      if (_state.isChosen()) 
        Drawer.setLineWidth(ren, (float) getState().getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
      else
        Drawer.setLineWidth(ren, (float) getState().getParam(DisplayParam.CARCASS_THICKNESS));
      Drawer.drawHyperbole(ren, _hyperbole, TypeFigure.CURVE);
      
      if( (boolean)_state.getParam(DisplayParam.DRAW_ASYMPTOTES) ){
        Drawer.setObjectColor(ren, ColorGL.BLUE);
        Drawer.setLineWidth(ren, 0.5);
        try {
          ArrayList<Line3d> as = _hyperbole.asymptotes();
          Drawer.drawLine(ren, as.get(0).pnt(), as.get(0).pnt2());
          Drawer.drawLine(ren, as.get(1).pnt(), as.get(1).pnt2());
        } catch(ExDegeneration ex){}
      }
    }
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    HyperboleBody el = (HyperboleBody) result;
    edt.addAnchor(el.f1(), result, "f1");
    edt.addAnchor(el.f2(), result, "f2");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    HyperboleBody hyp = (HyperboleBody) result;
    edt.addAnchor(hyp.f1(), result, "focus1");
    edt.addAnchor(hyp.f2(), result, "focus2");
    edt.addAnchor(hyp.hyperbole().pOnBound(), result, "pointOnBound");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: Реализовать функцию.
    return null;
  }
}
