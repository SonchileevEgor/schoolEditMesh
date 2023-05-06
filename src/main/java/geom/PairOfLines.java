package geom;

import static geom.Checker.isTwoLinesIntersect;
import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sub;
import static geom.Vect3d.vector_mul;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Ivan
 */
public class PairOfLines implements i_Geom, i_OrientableGeom {

  Vect3d _pnt1;
  Vect3d _pnt2;
  Vect3d _l1;
  Vect3d _l2;

  public PairOfLines() {
    _l1 = new Vect3d(1, 1, 1);
    _pnt1 = new Vect3d(2, 2, 2);
    _l2 = new Vect3d(1, 1, 1);
    _pnt2 = new Vect3d(2, 2, 2);
  }

  public PairOfLines(Vect3d point1, Vect3d vect1, Vect3d point2, Vect3d vect2) throws ExDegeneration {
    if ((vect1.norm() < Checker.eps()) || (vect2.norm() < Checker.eps())) {
      throw new ExDegeneration("направляющий вектор слишком мал");
    }
    _pnt1 = point1;
    _l1 = rNormalizedVector(vect1, 1);
    _pnt2 = point2;
    _l2 = rNormalizedVector(vect2, 1);
  }

  public PairOfLines(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), sub(points.get(1), points.get(0)), points.get(2), sub(points.get(2), points.get(3)));
  }

  public Vect3d pnt11() {
    return Vect3d.sum(_pnt1, _l1);
  }

  public Vect3d pnt21() {
    return Vect3d.sum(_pnt2, _l2);
  }

  public Vect3d pnt1() {
    return _pnt1.duplicate();
  }

  public Vect3d pnt2() {
    return _pnt2.duplicate();
  }

  public Vect3d l1() {
    return _l1;
  }

  public Vect3d l2() {
    return _l2;
  }

  public Line3d line1() throws ExDegeneration {
    return new Line3d(_pnt1, _l1);
  }

  public Line3d line2() throws ExDegeneration {
    return new Line3d(_pnt2, _l2);
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.PAIROFLINES3D;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_pnt1);
    result.add(pnt11());
    result.add(_pnt2);
    result.add(pnt21());
    return result;
  }

  /**
   * @param plane
   * @return point of intersection given plane and line
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersectionWithPlane(Plane3d plane) throws ExDegeneration {
    ArrayList<Vect3d> vects = new ArrayList<>();
    vects.add(new Line3d(_pnt1, _l1).intersectionWithPlane(plane));
    vects.add(new Line3d(_pnt2, _l2).intersectionWithPlane(plane));
    return vects;
  }

  public static PairOfLines PairOfLinesByFourPoints(Vect3d a, Vect3d b, Vect3d c, Vect3d d)
          throws ExDegeneration {
    return new PairOfLines(a, Vect3d.sub(b, a), c, Vect3d.sub(d, c));
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    PairOfLines pairOfLines = null;
    try {
      pairOfLines = new PairOfLines(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return pairOfLines;
  }

  public ArrayList<Vect3d> intersectWithRay(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> point = intersect(ray.line());
      if ((ray.containsPoint(point.get(0))) || (ray.containsPoint(point.get(1)))) {
        if (ray.containsPoint(point.get(0))) {
          result.add(point.get(0));
        }
        if (ray.containsPoint(point.get(1))) {
          result.add(point.get(1));
        }
      }
    } catch (ExDegeneration ex) {
    }
    return result;
  }

  /**
   * Intersect pair of lines with rib
   *
   * @param rib intersection
   * @return points inntersection
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExDegeneration, ExZeroDivision {
    ArrayList<Vect3d> points = intersect(rib.line());
    Iterator<Vect3d> iter = points.iterator();
    while (iter.hasNext()) {
      Vect3d s = iter.next();
      if (!Checker.pointOnOpenSegment(rib, s)) {
        iter.remove();
      }
    }
    return points;
  }

  /**
   * @return normal vector of the plane of the pair of lines
   * @throws geom.ExDegeneration
   */
  public Vect3d normal() throws ExDegeneration {
    return plane().n();
  }

  /**
   * @return the plane of the pair of lines
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return new Plane3d(_pnt1, _pnt2, pnt11());
  }

  /**
   * Intersect pair of lines with line
   *
   * @param line intersection
   * @return points intersection
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    points.addAll(this.intersectionWithLine(line));
    return points;
  }

  public ArrayList<Vect3d> intersectionWithLine(Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    if ((isTwoLinesIntersect(line1(), line)) || (isTwoLinesIntersect(line2(), line))) {
      if (isTwoLinesIntersect(line1(), line)) {
        Vect3d normal = vector_mul(vector_mul(line.l(), _l1), _l1);
        Plane3d plane = new Plane3d(normal, _pnt1);
        points.add(line.intersectionWithPlane(plane));
      }
      if (isTwoLinesIntersect(line2(), line)) {
        Vect3d normal = vector_mul(vector_mul(line.l(), _l2), _l2);
        Plane3d plane = new Plane3d(normal, _pnt2);
        points.add(line.intersectionWithPlane(plane));
      }
      return points;
    } else {
      throw new ExDegeneration("прямая и пара прямых не пересекаются");
    }
  }

  /**
   * Find intersection points of this pair of lines and ray.
   *
   * @param ray
   * @return
   */
  public ArrayList<Vect3d> intersect(Ray3d ray) {
    return intersectWithRay(ray);
  }

  @Override
  public Vect3d getUpVect() {
    try {
      return normal();
    } catch (ExDegeneration ex) {
      return new Vect3d(0, 0, 1);
    }
  }

}
