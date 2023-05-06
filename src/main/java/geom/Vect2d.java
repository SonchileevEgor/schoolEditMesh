package geom;

// Simple 2d-vector.

import java.util.ArrayList;


public class Vect2d {
  private double _x, _y;

  public Vect2d() {
    _x = _y = 0.0;
  }

  public Vect2d( double x, double y ){
    _x = x; _y = y;
  }
  public Vect2d( Vect2d v ){
    _x = v.x(); _y = v.y();
  }

  public Vect2d duplicate() {
    return new Vect2d(this);
  }

  public double x() { return _x; }
  public double y() { return _y; }

  public void set_x( double x ) { _x = x; }
  public void set_y( double y ) { _y = y; }

  public void set( double x, double y ) {
    _x = x; _y = y;
  }

  // норма (длина) вектора
  public double norm() {
    return Math.hypot(_x, _y);
  }

  // квадрат нормы вектора
  public double mag() {
    double n = norm();
    return n*n;
  }

  public double arg() {
    return Math.atan2(_y, _x);
  }

  public void rot( double angle ){
    double xx = _x;
    double yy = _y;
    double sn = Math.sin(angle);
    double cs = Math.cos(angle);
    _x = xx * cs - yy * sn;
    _y = xx * sn + yy * cs;
  }

  public void rot( SinCos sc ){
    double xx = _x;
    double yy = _y;
    _x = xx * sc.cos() - yy * sc.sin();
    _y = xx * sc.sin() + yy * sc.cos();
  }

  public void rot( double angle, Vect2d cnt ){
    double xx = _x - cnt.x();
    double yy = _y - cnt.y();
    double sn = Math.sin(angle);
    double cs = Math.cos(angle);
    _x = cnt.x() + xx * cs - yy * sn;
    _y = cnt.y() + xx * sn + yy * cs;
  }

  public void rot( SinCos sc, Vect2d cnt ){
    double xx = _x - cnt.x();
    double yy = _y - cnt.y();
    _x = cnt.x() + xx * sc.cos() - yy * sc.sin();
    _y = cnt.y() + xx * sc.sin() + yy * sc.cos();
  }

  public void inc_add( Vect2d v ) { _x += v.x();  _y += v.y(); }
  public void inc_sub( Vect2d v ) { _x -= v.x();  _y -= v.y(); }
  public void inc_mul( double k ) { _x *= k;  _y *= k; }
  public void inc_div( double k ) { _x /= k;  _y /= k; }

  public static double norm( Vect2d v ) { return v.norm(); }
  public static double mag( Vect2d v )  { return v.mag(); }
  public static double arg( Vect2d v )  { return v.arg(); }
  public static double dist( Vect2d v1, Vect2d v2 ) { return Math.hypot(v1.x() - v2.x(), v1.y() - v2.y()); }

  public static Vect2d polar( double r, double phi ) {
    return new Vect2d(r * Math.cos(phi), r * Math.sin(phi));
  }

  public static Vect2d polar( double r, SinCos sc ) {
    return new Vect2d(r * sc.cos(), r * sc.sin());
  }

  public static Vect2d rot( Vect2d pnt, double angle ){
    Vect2d res = new Vect2d(pnt);
    res.rot(angle);
    return res;
  }

  public static Vect2d rot( Vect2d pnt, SinCos sc ){
    Vect2d res = new Vect2d(pnt);
    res.rot(sc);
    return res;
  }

  public static Vect2d rot( Vect2d pnt, double angle, Vect2d cnt )
  {
    Vect2d res = new Vect2d(pnt);
    res.rot(angle, cnt);
    return res;
  }

  public static Vect2d rot( Vect2d pnt, SinCos sc, Vect2d cnt )
  {
    Vect2d res = new Vect2d(pnt);
    res.rot(sc, cnt);
    return res;
  }

  public static double inner_mul( Vect2d v1, Vect2d v2 ){
    return v1.x() * v2.x() + v1.y() * v2.y();
  }

  public static Vect2d conv_hull( Vect2d a, Vect2d b, double al ){
    Vect2d d = new Vect2d(b.x() - a.x(), b.y() - a.y());
    return new Vect2d(a.x() + al * d.x(), a.y() + al * d.y());
  }

  public static Vect2d sub( Vect2d a, Vect2d b ){
    return new Vect2d(a.x() - b.x(), a.y() - b.y());
  }

  public Vect3d threeD(){
    return new Vect3d(_x, _y, 0);
  }

  public ArrayList<Vect3d> deconstr() {
    ArrayList <Vect3d> result = new ArrayList();
    result.add(this.threeD());
    return result;
  }

  public Vect2d constr(ArrayList<Vect3d> points) {
    return new Vect2d (points.get(0).projectOnOXY());
  }
};

