package geom;

import java.util.ArrayList;

/**
 * Hyperboloid of two sheet
 * @author Vitaliy
 */
public class HyperboloidOfTwoSheet3d implements i_Geom {

  // длина действительной полуоси
  private double _realAxle;
  private Vect3d _f1, _f2;
  // центр гиперболоида
  private Vect3d _center;
  // distance from center to top arcs
  private double _c;
  // length perpendicular to the x-axis, recovered from each vertex
  // to the intersection with the asymptote
  private double _b;
  private Vect3d _pOnBound;
  // плоскость гиперболоида
  private Plane3d _plane;

  /**
   * Новая система координат,
   * где центр системы соответсвует центру гиперболоида,
   * действительная ось ( проходящая через фокусы) соответсвует оси Ох,
   * а мнимая ось соответсвует оси Оу.
   */
  private NewSystemOfCoor _centerHyperboloid;

  /**
   * Constructor of hyperbole by focus and point on Bound of hyperboloid
   * Конструктор гиперболоида использует конструктор гиперболы
   * @param f1 first focus
   * @param f2 second focus
   * @param pointOnBound point owned hyperbole
   * @throws ExDegeneration
   * @throws geom.ExZeroDivision
   */
  public HyperboloidOfTwoSheet3d(Vect3d f1, Vect3d f2, Vect3d pointOnBound) throws ExDegeneration {
    _f1 = f1;
    _f2 = f2;
    _pOnBound = pointOnBound;
    if (_f1.equals(_f2)) {
      throw new ExDegeneration("Фокусы гиперболоида совпадают");
    }
    Vect3d n = Vect3d.vector_mul(Vect3d.sub(f1, f2), Vect3d.sub(pointOnBound, f1));
    if (n.norm() == 0) {
      throw new ExDegeneration("Points f1, f2, pointOnBound lies on the same line");
    }
    double distBetweenFocuses = Vect3d.sub(f1, f2).norm();
    _realAxle = Math.abs(Vect3d.sub(_pOnBound, _f1).norm() - Vect3d.sub(_pOnBound, f2).norm()) / 2;
    if(_realAxle * 2 < Checker.eps())
      throw new ExDegeneration("Указанная точка гиперболоида равноудалена от фокусов");
    if (distBetweenFocuses < _realAxle * 2 || Math.abs(distBetweenFocuses - _realAxle * 2) < Checker.eps()) {
      throw new ExDegeneration("Длина межфокусного расстояния меньше или равна сумме расстояний от точки до фокусов");
    }
    //Создаем новую координатную плоскость с началом координат в центре гиперболоида
    _center = Vect3d.conv_hull(f1, f2, 0.5);
    _c = Vect3d.sub(_center, _f2).norm();
    _b = Math.sqrt(_c * _c - _realAxle * _realAxle);
    _plane = new Plane3d(f1, f2, pointOnBound);
    //Линия, которая соответствует оси оУ в новой координатной плоскости
    //проходит через мнимую ось гиперболы
    Line3d oY = Vect3d.midLinesArePerpendicularToTheSegment(f1, f2, _plane);
    //Находим точку, которая нам определит ось Оу
    Vect3d pointOnOy;

    if (oY.pnt().equals(_center)) {
      pointOnOy = oY.pnt2().duplicate();
    } else {
      pointOnOy = oY.pnt().duplicate();
    }
    pointOnOy = Vect3d.sum(_center, Vect3d.sub(pointOnOy, _center).getNormalized());
    //Линия, которая соответствует оси оZ в новой координатной плоскости
    //перпендикулярна плоскости гиперболы и проходит через ее центр
    Line3d oZ = Line3d.linePerpendicularPlane(_plane, _center);
    Vect3d pointOnOZ;
    if (oZ.pnt().equals(_center)) {
      pointOnOZ = oZ.pnt2().duplicate();
    } else {
      pointOnOZ = oZ.pnt().duplicate();
    }

    //фокус определит ось оХ для новой координатной плоскости
    _centerHyperboloid = new NewSystemOfCoor(_center, f2, pointOnOy, pointOnOZ);
  }

  /**
   * Constructor of hyperboloid by points.
   */
  public HyperboloidOfTwoSheet3d(ArrayList<Vect3d> points) throws ExDegeneration, ExZeroDivision {
    this(points.get(0), points.get(1), points.get(2));
  }

  /**
   * The 1st focus of the hyperboloid.
   * @return
   */
  public Vect3d f1() {
    return _f1.duplicate();
  }

  /**
   * The 2nd focus of the hyperboloid.
   * @return
   */
  public Vect3d f2() {
    return _f2.duplicate();
  }

  public NewSystemOfCoor newSystemOfCoorHyperboloid() {
    return _centerHyperboloid;
  }

  /**
   * @return normal vector of the plane of the ellipse
   * @throws ExDegeneration
   */
  public Vect3d normal() throws ExDegeneration {
    return _plane.n();
  }

  /**
   * @return center of hyperbole
   */
  public Vect3d center() {
    return _center.duplicate();
  }

  /**
   * @return point on bound
   */
  public Vect3d pOnBound() {
    return _pOnBound.duplicate();
  }

  /**
   * @return длину действительной полуоси гиперболы
   */
  public double realAxle() {
    return _realAxle;
  }

  public double b() {
    return _b;
  }

  /**
   * @return eccentricity of hyperbole
   */
  public double e() {
    return _c / _realAxle;
  }

  /**
   * @return вершину правой дуги гиперболы
   */
  public Vect3d getTopRightHyb() {
    Vect3d realAxle = Vect3d.rNormalizedVector(Vect3d.sub(_f2, _center), _realAxle);
    return Vect3d.sum(_center, realAxle);
  }

  /**
   * @return вершину левой дуги гиперболы
   */
  public Vect3d getTopLeftHyb() {
    Vect3d realAxle = Vect3d.rNormalizedVector(Vect3d.sub(_f1, _center), _realAxle);
    return Vect3d.sum(_center, realAxle);
  }
  
  /**
   * @return точка, лежащая на мнимой оси гиперболы
   * @throws ExDegeneration
   */
  public Vect3d getPointImagynaryAxle() throws ExDegeneration {
    Line3d oY = Vect3d.midLinesArePerpendicularToTheSegment(_f1, _f2, _plane);
    Vect3d pointOnOy;
    if (oY.pnt().equals(_center)) {
      pointOnOy = new Vect3d(oY.pnt2());
    } else {
      pointOnOy = new Vect3d(oY.pnt());
    }

    return new Vect3d(_center).sum(Vect3d.rNormalizedVector(pointOnOy.sub(_center), _realAxle));
  }

  /**
   * @return point, which required to determine the new coord plane, including
   * the axis oZ
   * @throws ExDegeneration
   *
   */
  public Vect3d getPointThridAxle() throws ExDegeneration {
    Line3d oZ = Line3d.linePerpendicularPlane(_plane, _center);
    if (oZ.pnt().equals(_center)) {
      return new Vect3d(oZ.pnt2());
    } else {
      return new Vect3d(oZ.pnt());
    }
  }
  
  /**
   *
   * @param t параметр по которому строится точка [-infinity, +infinity]
   * @param first условный параметр определяющий точку какой дуги мы хотим
   * построить [true(правая), false(левая)]
   * @param ang угол поворота точки относительно дествительной оси образующей  
   * гиперболы
   * @return point of hyperboloid
   */
  public Vect3d getPoint(double t, boolean first, double ang) {
    Vect3d newPoint;
    if (first) {
      newPoint = new Vect3d(_realAxle * Math.cosh(t), _b * Math.sinh(t), 0);
    } else {
      newPoint = new Vect3d((-1) * _realAxle * Math.cosh(t), _b * Math.sinh(t), 0);
    }
    return rotatePointOfHyperboloid(newPoint, ang);
  }

  /**
   * @param point точка вращения относительно действительной 
   * оси образующей гиперболы
   * @param ang угол поворота
   * @return
   */
  public Vect3d rotatePointOfHyperboloid(Vect3d point, double ang) {
    return point.rotate(ang, Vect3d.UX);
  }

  /**
   * constructing tangency plane to an hyperboloidOfTwoSheet at a selected point
   * of the hyperboloidOfTwoSheet
   *
   * @param point point of the hyperboloidOfTwoSheet
   * @return the tangent plane at the point
   * @throws ExGeom
   */
  public Plane3d tangentPlaneInPoint(Vect3d point) throws ExGeom {
    Vect3d pointNew = _centerHyperboloid.newCoor(point);
    if (Checker.isPointOnHyperboloidOfTwoSheet(point, this)) {
      Vect3d normal = new Vect3d(2 * pointNew.x() / (realAxle() * realAxle()), -2 * pointNew.y()
              / (b() * b()), -2 * pointNew.z() / (b() * b()));
      Vect3d c = _centerHyperboloid.oldCoor(normal);
      Vect3d normalNew = new Vect3d(c.x() - center().x(), c.y() - center().y(), c.z() - center().z());
      Plane3d tangPlane = new Plane3d(normalNew, point);
      return tangPlane;
    } else {
      throw new ExGeom("Точка не принадлежит двуполостному гиперболоиду вращения");
    }
  }
  
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_f1);
    result.add(_f2);
    result.add(_pOnBound);
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new HyperboloidOfOneSheet3d(points);
    } catch (ExDegeneration | ExZeroDivision ex) {
    }
    return null;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.HYPERBOLOID_OF_TWO_SHEET3D;
  }
}
