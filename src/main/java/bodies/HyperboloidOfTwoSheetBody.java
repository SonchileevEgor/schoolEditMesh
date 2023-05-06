package bodies;

import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.ExDegeneration;
import geom.HyperboloidOfTwoSheet3d;
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
public class HyperboloidOfTwoSheetBody extends BodyAdapter {
  
  private HyperboloidOfTwoSheet3d _hyperboloid; // math object hyperboloid

  /**
   * Constructor of hyperboloid by math hyperboloid
   * @param id
   * @param title
   * @param hyperboloid math object HyperboloidOfTwoSheet3d
   */
  public HyperboloidOfTwoSheetBody(String id, String title, HyperboloidOfTwoSheet3d hyperboloid) {
    super(id, title);
    _alias = "гиперболоид двуполостный";
    _hyperboloid = hyperboloid;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public HyperboloidOfTwoSheetBody(String id, String title) {
    super(id, title);
    _alias = "гиперболоид двуполостный";
    _exists = false;
  }

  public HyperboloidOfTwoSheet3d hyperboloid() {
    return _hyperboloid;
  }

  /**
   * @return first focus of the hyperboloid
   */
  public Vect3d f1() {
    return _hyperboloid.f1();
  }

  /**
   * @return second focus of the hyperboloid
   */
  public Vect3d f2() {
    return _hyperboloid.f2();
  }

  /**
   * @return point on bound of hyperboloid
   */
   public Vect3d pOnBound() {
    return _hyperboloid.pOnBound();
  }
   
  /**
   * @return normal vector of hyperboloid
   * @throws geom.ExDegeneration
   */
  public Vect3d normal() throws ExDegeneration {
    return _hyperboloid.normal();
  }

  /**
   * @return new system of coordinate in center of hyperboloid
   */
  public NewSystemOfCoor centerhyperboloid() {
    return _hyperboloid.newSystemOfCoorHyperboloid();
  }

  @Override
  public BodyType type() {
    return BodyType.HYPERBOLOID_OF_TWO_SHEET;
  }

  @Override
  public i_Geom getGeom() {
    return _hyperboloid;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new HyperboloidOfTwoSheetBody(id, title, (HyperboloidOfTwoSheet3d) geom);
  }

  @Override
  public void glDrawCarcass(Render ren) {
    if (!exists()) {
      return;
    }
    if (getState().isVisible()) {
      Drawer.setObjectColor(ren, (ColorGL) getState().getParam(DisplayParam.CARCASS_COLOR));
      Drawer.setLineWidth(ren, (float) getState().getParam(DisplayParam.CARCASS_THICKNESS));
      Drawer.drawHyperboloidOfTwoSheet(ren, _hyperboloid, TypeFigure.CURVE);
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      Drawer.setObjectFacetColor(ren, _state);
      try {
        Drawer.drawHyperboloidOfTwoSheetFacets(ren, _hyperboloid) ;
      } catch (ExDegeneration ex) {}
    }
  }
  
  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    HyperboloidOfTwoSheetBody hyp = (HyperboloidOfTwoSheetBody) result;
    edt.addAnchor(hyp.f1(), result, "f1");
    edt.addAnchor(hyp.f2(), result, "f2");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    HyperboloidOfTwoSheetBody hyp = (HyperboloidOfTwoSheetBody) result;
    edt.addAnchor(hyp.f1(), result, "focus1");
    edt.addAnchor(hyp.f2(), result, "focus2");
    edt.addAnchor(hyp.pOnBound(), result, "pointOnBound");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: реализовать функцию
    return null;
  }
}
