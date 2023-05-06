package bodies;

import static config.Config.GUI_EPS;
import geom.AbstractPolygon;
import opengl.Drawer;
import opengl.Render;
import editor.state.DisplayParam;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;
import opengl.CalculatorGL;

/**
 * Line metabody.
 */
public class LineBody extends BodyAdapter implements i_LinearBody {
  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";

  private Line3d _line;

  // Опции, необходимые для отображения отрезков, построенных по двум точкам.
  /**
   * Нужно ли скрывать отрезок линии.
   */
  private boolean _hideSegment;

  /**
   * Начало отрезка, который нужно скрыть.
   */
  private Vect3d _segmentA;

  /**
   * Конец отрезка, который нужно скрыть.
   */
  private Vect3d _segmentB;

  public LineBody(String id, String title, Line3d line) {
    super(id, title);
    _line = line;
    _hideSegment = false;
    _alias = "прямая";
    _exists = true;
  }

  public LineBody (String id, String title, Vect3d a, Vect3d b) throws ExDegeneration{
    this(id, title, Line3d.getLineByTwoPoints(a, b));
  }

  public LineBody(String id, String title) {
    super(id, title);
    _alias = "прямая";
    _exists = false;
  }

  @Override
  public Line3d line() {
    return _line;
  }

  public Vect3d pnt(){return _line.pnt();}
  public Vect3d pnt2(){return _line.pnt2();}
  public Vect3d l(){return _line.l();}

  @Override
  public BodyType type() {
    return BodyType.LINE;
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!_exists || !_state.isVisible())
      return;
    Drawer.setObjectCarcassColor(ren, _state);
    if (_state.isChosen()) {
      Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
    } else {
      Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
    }
    if (_hideSegment) {
      Drawer.drawRay(ren, _segmentA, Vect3d.sub(_segmentA, _segmentB));
      Drawer.drawRay(ren, _segmentB, Vect3d.sub(_segmentB, _segmentA));
    } else {
      Drawer.drawLine(ren, pnt(), pnt2());
    }
  }

  @Override
  public void addRibs(Editor edt){
    String idA = getAnchorID(BODY_KEY_A);
    String idB = getAnchorID(BODY_KEY_B);

    try {
      i_Anchor a = edt.anchors().get(idA);
      i_Anchor b = edt.anchors().get(idB);

      // adding the ribs
      Rib3d rib = new Rib3d(a.getPoint(), b.getPoint());
      edt.addAnchor(rib, idA, idB, this, "rib");
    } catch (ExNoAnchor | ExDegeneration ex) {}
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return line().intersectionWithPlane(plane);
    }
    catch (ExDegeneration ex){
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _line;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new LineBody (id, title, (Line3d) geom);
  }

  /**
   * @param hideSegment the _hideSegment to set
   */
  public void setHideSegment(boolean hideSegment) {
    _hideSegment = hideSegment;
  }

  /**
   * @param segmentA the _segmentA to set
   */
  public void setSegmentA(Vect3d segmentA) {
    _segmentA = segmentA;
  }

  /**
   * @param segmentB the _segmentB to set
   */
  public void setSegmentB(Vect3d segmentB) {
    _segmentB = segmentB;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    LineBody line = (LineBody) result;
    edt.addAnchor(line.pnt(), result, BODY_KEY_A);
    edt.addAnchor(line.pnt2(), result, BODY_KEY_B);
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    double[] coord1 = new double[2];
    double[] coord2 = new double[2];
    // проецируем отрезок на экран
    CalculatorGL.getDisplayCoord(ren, _line.pnt(), coord1);
    CalculatorGL.getDisplayCoord(ren, _line.pnt2(), coord2);
    Rib2d ribOnScreen = new Rib2d(new Vect2d(coord1[0], coord1[1]),
                                      new Vect2d(coord2[0], coord2[1]));
    // вычисляем расстояние до прямой на экране в пикселях
    if( ribOnScreen.line().distFromPoint(new Vect2d(x, ren.getHeight() - y))
            <= GUI_EPS.value()) {
      // Ищем точку на прямой, ближайшую к лучу взгляда.
      try {
        Plane3d pl = new Plane3d(_line.pnt(), _line.pnt2(), Vect3d.sum(_line.pnt2(), ray.l()));
        return _line.intersectionWithLine(ray.line().projectOnPlane(pl));
      } catch (ExDegeneration ex) {}
    }
    return null;
  }
}