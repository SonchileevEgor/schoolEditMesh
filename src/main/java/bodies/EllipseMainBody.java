package bodies;

import opengl.colorgl.ColorGL;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.EllipseMain3d;
import geom.ExDegeneration;
import geom.NewSystemOfCoor;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import util.Util;

/**
 *
 * @author VitaliiZah
 */
public class EllipseMainBody extends BodyAdapter implements i_PlainBody {
  private EllipseMain3d _ellipse; // math object elipse

  /**
   * Constructor of ellipse by math ellipse
   * @param id
   * @param title
   * @param elipse math object ElipseMain3d
   */
  public EllipseMainBody(String id, String title, EllipseMain3d elipse) {
    super(id, title);
    _alias = "эллипс";
    _ellipse = elipse;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public EllipseMainBody(String id, String title) {
    super(id, title);
    _alias = "эллипс";
    _exists = false;
  }

  public EllipseMain3d ellipse() {
    return _ellipse;
  }

  /**
   * @return first focus of the ellipse
   */
  public Vect3d f1() {
    return _ellipse.f1();
  }

  /**
   * @return second focus of the ellipse
   */
  public Vect3d f2() {
    return _ellipse.f2();
  }

  /**
   * @return point of ellipse
   */
  public Vect3d pOnBound() {
    return _ellipse.pOnBound();
  }

  /**
   * @return normal vector of ellipse
   * @throws ExDegeneration
   */
  public Vect3d n() throws ExDegeneration {
    return _ellipse.normal();
  }

  /**
   * @return new system of coordinate in center of ellipse
   */
  public NewSystemOfCoor centerEllipse() {
    return _ellipse.newSystemOfCoorElipse();
  }

  /**
   * @return module sum distance from bound to focus f1 and f2
   */
  public double distance() {
    return _ellipse.distance();
  }

  public Vect3d center() {
    return _ellipse.center();
  }

  @Override
  public Plane3d plane() {
    try {
      return _ellipse.plane();
    } catch( ExDegeneration ex ){
      return Plane3d.oXY();
    }
  }

  @Override
  public BodyType type() {
    return BodyType.ELLIPSE;
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
      Drawer.drawEllipse(ren, _ellipse, TypeFigure.SOLID);
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
      Drawer.drawEllipse(ren, _ellipse, TypeFigure.WIRE);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _ellipse;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new EllipseMainBody(id, title, (EllipseMain3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    EllipseMainBody el = (EllipseMainBody) result;
    edt.addAnchor(el.f1(), result, "f1");
    edt.addAnchor(el.f2(), result, "f2");
    edt.addAnchor(el.pOnBound(), result, "pointOnBound");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return _ellipse.intersectWithRayTransversal(ray);
  }
}
