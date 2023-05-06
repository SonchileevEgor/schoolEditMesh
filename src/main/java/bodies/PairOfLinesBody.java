package bodies;

import static config.Config.GUI_EPS;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.ExDegeneration;
import static geom.Line3d.line3dByTwoPoints;
import geom.PairOfLines;
import geom.Plane3d;
import geom.Ray3d;
import geom.Rib2d;
import geom.Vect2d;
import geom.Vect3d;
import geom.i_Geom;
import opengl.CalculatorGL;
import opengl.Drawer;
import opengl.Render;

/**
 *
 * @author Ivan
 */
public class PairOfLinesBody extends BodyAdapter implements i_PlainBody {

  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_C = "C";
  public static final String BODY_KEY_D = "D";

  private PairOfLines _pairOfLines;

  public PairOfLinesBody(String id, String title, PairOfLines pairOfLines) {
    super(id, title);
    _alias = "пара прямых";
    _pairOfLines = pairOfLines;
    _exists = true;
  }

  public PairOfLinesBody(String id, String title, Vect3d a, Vect3d b, Vect3d c, Vect3d d) throws ExDegeneration {
    this(id, title, PairOfLines.PairOfLinesByFourPoints(a, b, c, d));
  }

  public PairOfLinesBody(String id, String title) {
    super(id, title);
    _alias = "пара прямых";
    _exists = false;
  }

  public PairOfLines pairOfLines() {
    return _pairOfLines;
  }

  public Vect3d pnt1() {
    return _pairOfLines.pnt1();
  }

  public Vect3d pnt11() {
    return _pairOfLines.pnt11();
  }

  public Vect3d l1() {
    return _pairOfLines.l1();
  }

  public Vect3d pnt2() {
    return _pairOfLines.pnt2();
  }

  public Vect3d pnt21() {
    return _pairOfLines.pnt21();
  }

  public Vect3d l2() {
    return _pairOfLines.l2();
  }

  @Override
  public BodyType type() {
    return BodyType.PAIROFLINES;
  }

  @Override
  public void glDrawCarcass(Render ren) {
    if (!_exists || !_state.isVisible()) {
      return;
    }
    Drawer.setObjectCarcassColor(ren, _state);
    if (_state.isChosen()) {
      Drawer.setPairOfLinesWidth(ren, (float) _state.getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
    } else {
      Drawer.setPairOfLinesWidth(ren, (float) _state.getParam(DisplayParam.CARCASS_THICKNESS));
    }
    Drawer.drawPairOfLines(ren, pnt1(), pnt11(), pnt2(), pnt21());
  }

  @Override
  public i_Geom getGeom() {
    return _pairOfLines;
  }

  /**
   * @return normal vector of pair of lines
   * @throws ExDegeneration
   */
  public Vect3d n() throws ExDegeneration {
    return _pairOfLines.normal();
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new PairOfLinesBody(id, title, (PairOfLines) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    PairOfLinesBody pairOfLines = (PairOfLinesBody) result;
    edt.addAnchor(pairOfLines.pnt1(), result, BODY_KEY_A);
    edt.addAnchor(pairOfLines.pnt11(), result, BODY_KEY_B);
    edt.addAnchor(pairOfLines.pnt2(), result, BODY_KEY_C);
    edt.addAnchor(pairOfLines.pnt21(), result, BODY_KEY_D);
  }

  @Override
  public Plane3d plane() {
    try {
      return _pairOfLines.plane();
    } catch (ExDegeneration ex) {
      return Plane3d.oXY();
    }
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    double[] coord11 = new double[2];
    double[] coord12 = new double[2];
    double[] coord21 = new double[2];
    double[] coord22 = new double[2];
    // проецируем отрезок №1 на экран
    CalculatorGL.getDisplayCoord(ren, _pairOfLines.pnt1(), coord11);
    CalculatorGL.getDisplayCoord(ren, _pairOfLines.pnt11(), coord12);
    Rib2d ribOnScreen1 = new Rib2d(new Vect2d(coord11[0], coord11[1]),
            new Vect2d(coord12[0], coord12[1]));
    // вычисляем расстояние до прямой №1 на экране в пикселях
    // проецируем отрезок №2 на экран
    CalculatorGL.getDisplayCoord(ren, _pairOfLines.pnt2(), coord21);
    CalculatorGL.getDisplayCoord(ren, _pairOfLines.pnt21(), coord22);
    Rib2d ribOnScreen2 = new Rib2d(new Vect2d(coord21[0], coord21[1]),
            new Vect2d(coord22[0], coord22[1]));
    // вычисляем расстояние до прямой №2 на экране в пикселях
    if ((ribOnScreen1.line().distFromPoint(new Vect2d(x, ren.getHeight() - y))
            <= GUI_EPS.value()) || (ribOnScreen2.line().distFromPoint(new Vect2d(x, ren.getHeight() - y))
            <= GUI_EPS.value())) {
      // Ищем точку на паре прямых, ближайшую к лучу взгляда.
      if (ribOnScreen1.line().distFromPoint(new Vect2d(x, ren.getHeight() - y))
              <= ribOnScreen1.line().distFromPoint(new Vect2d(x, ren.getHeight() - y))) {
        try {
          Plane3d pl = new Plane3d(_pairOfLines.pnt1(), _pairOfLines.pnt11(), Vect3d.sum(_pairOfLines.pnt11(), ray.l()));
          return line3dByTwoPoints(_pairOfLines.pnt1(),_pairOfLines.pnt11()).intersectionWithLine(ray.line().projectOnPlane(pl));
        } catch (ExDegeneration ex) {
        }
      }else{
       try {
          Plane3d p2 = new Plane3d(_pairOfLines.pnt2(), _pairOfLines.pnt21(), Vect3d.sum(_pairOfLines.pnt21(), ray.l()));
          return line3dByTwoPoints(_pairOfLines.pnt2(),_pairOfLines.pnt21()).intersectionWithLine(ray.line().projectOnPlane(p2));
        } catch (ExDegeneration ex) {
        }
      }
    }
    return null;
  }
}
