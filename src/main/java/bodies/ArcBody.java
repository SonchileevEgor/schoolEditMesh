package bodies;

import opengl.colorgl.ColorGL;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import editor.state.DisplayParam;
import geom.Arc3d;
import geom.ExDegeneration;
import geom.Ray3d;
import geom.Vect3d;
import java.util.ArrayList;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;

/**
 *
 * @author rita
 */
public class ArcBody extends BodyAdapter {
  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_C = "C";

  private Arc3d _arc;// math object arc

  /**
   * Constructor of arc by math arc
   * @param id
   * @param title
   * @param arc math object Rib3d
   */
  public ArcBody (String id, String title, Arc3d arc){
    super(id, title);
    _arc = arc;
    _alias = "дуга";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public ArcBody(String id, String title) {
    super(id, title);
    _alias = "дуга";
    _exists = false;
  }

  public Vect3d vert1() {
    return _arc.vert1();
  }

  public Vect3d vert2() {
    return _arc.vert2();
  }

  @Override
  public BodyType type() {
    return BodyType.ARC;
  }

  @Override
  public void addRibs(Editor edt){
    // no ribs
  }

  @Override
  public void glDrawFacets(Render ren){
    // no facets
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!exists())
      return;
    if (getState().isVisible()) {
      Drawer.setObjectColor(ren, (ColorGL)getState().getParam(DisplayParam.CARCASS_COLOR));
      Drawer.setLineWidth(ren, (float)getState().getParam(DisplayParam.CARCASS_THICKNESS));
      try {
        Drawer.drawArc(ren, _arc, TypeFigure.CURVE);
      } catch (ExDegeneration ex) { }
    }
  }

  @Override
  public i_Geom getGeom() {
    return _arc;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new ArcBody (id, title, (Arc3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    ArcBody arc = (ArcBody) result;
    edt.addAnchor(arc._arc.vert1(), result, BODY_KEY_A);
    edt.addAnchor(arc._arc.midVert(), result, BODY_KEY_B);
    edt.addAnchor(arc.vert2(), result, BODY_KEY_C);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: Заменить 0.1 на величину, зависящую от масштаба
    //!! вернуть точку на дуге, а не рядом с ней.
    ArrayList<Vect3d> intersectWithPlane = _arc.plane().intersect(ray);
    if (intersectWithPlane.size() == 1)
      if (_arc.dist(intersectWithPlane.get(0)) < 0.1)
        return intersectWithPlane.get(0);
    return null;
  }
}
