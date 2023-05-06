package geom;

import java.util.ArrayList;

public class Line2d {
  private Vect2d _t;
  private Vect2d _l;

  public Line2d( Vect2d t, Vect2d l ) {
    _t = t; _l = l;
  }
  public Line2d(ArrayList<Vect3d> points) {
      this(points.get(0).projectOnOXY(), Vect2d.sub(points.get(1).projectOnOXY(), points.get(0).projectOnOXY()));
  }

  /**
   * Конструктор прямой по двум точкам.
   * @param a точка на прямой
   * @param b точка на прямой
   * @return
   */
  public static Line2d lineByTwoPoints(Vect2d a, Vect2d b) {
    return new Line2d(a, Vect2d.sub(b, a));
  }

  /**
   * Точка на прямой.
   * @return
   */
  public Vect2d t() { return _t; }

  /**
   * Направляющий вектор прямой.
   * @return
   */
  public Vect2d l() { return _l; }

  public void set_t( Vect2d t ) { _t = t; }
  public void set_l( Vect2d l ) { _l = l; }

  public void set( Vect2d t, Vect2d l ) {
    _t = t; _l = l;
  }

  public boolean isContainsPoint(Vect2d p){
    return Math.abs(_l.y() * (p.x() - _t.x()) -
            _l.x() * (p.y() - _t.y())) < Checker.eps();
  }

  public double distFromPoint ( Vect2d c ){
    return Vect2d.sub(projectOfPoint(c), c).norm();
  }

  public Vect2d projectOfPoint( Vect2d c ) {
    Vect2d n = _l;
    n.rot(Math.PI/2);
    Plane3d pl = new Plane3d(n.threeD(),_t.threeD());
    Vect3d v = pl.projectionOfPoint(c.threeD());
    return v.projectOnOXY();
  }

 public ArrayList<Vect3d> deconstr() {
      ArrayList<Vect3d> result = new ArrayList();
      result.add(_t.threeD());
      Vect3d pnt2 = Vect3d.sum(_t.threeD(), _l.threeD());
      result.add(pnt2);
      return result;
  }

  public Line2d constr(ArrayList<Vect3d> points) {
    return new Line2d(points);
  }
}
