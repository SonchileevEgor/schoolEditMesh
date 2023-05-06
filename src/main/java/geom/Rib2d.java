package geom;

import java.util.ArrayList;

/**
 *
 * @author alexeev
 */
public class Rib2d {
  private Vect2d _a, _b;

  public Rib2d(Vect2d a, Vect2d b) {
    _a = a; _b = b;
  }

  public Rib2d(Rib2d r) {
    _a = r.a(); _b = r.b();
  }
  
  public Rib2d(ArrayList<Vect3d> points) {
    Rib2d rib = new Rib2d(points.get(0).projectOnOXY(),points.get(1).projectOnOXY());
    _a = rib.a(); _b = rib.b();
  }

  public Rib2d duplicate() {
    return new Rib2d(this);
  }

  public Vect2d a() { return _a.duplicate(); }
  public Vect2d b() { return _b.duplicate(); }

  /**
   * @return length of rib
   */
  public double length() {
    return Vect2d.sub(_a, _b).norm();
  }

  /**
   * Line through two vertices of rib.
   * @return
   */
  public Line2d line() {
    return Line2d.lineByTwoPoints(_a, _b);
  }

  public double distFromPoint(Vect2d v) {
    Vect2d proj = line().projectOfPoint(v);
    if (Checker.isEqual(Vect2d.dist(_a, _b),
                        Vect2d.dist(_a, proj) + Vect2d.dist(proj, _b))) {
      // если проекция находится на отрезке, то считаем как расстояние до прямой
      return Vect2d.dist(proj, v);
    } else {
      // иначе, берём расстояние до ближайшей вершины.
      return Math.min(Vect2d.dist(v, _a), Vect2d.dist(v, _b));
    }
  }

  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList();
    result.add(_a.threeD());
    result.add(_b.threeD());
    return result;
  }

  public Rib2d constr(ArrayList<Vect3d> points) {
    return new Rib2d(points);
  }
}
