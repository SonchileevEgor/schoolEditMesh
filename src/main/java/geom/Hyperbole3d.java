package geom;

import static config.Config.LOG_LEVEL;
import java.util.ArrayList;
import java.util.Iterator;
import util.Log;

/**
 * Hyperbole
 * @author VitaliiZah
 */
public class Hyperbole3d implements i_Geom, i_OrientableGeom {

  // длина действительной полуоси
  private double _realAxle;
  private Vect3d _f1, _f2;
  // центр гиперболы
  private Vect3d _center;
  // distance from center to top arcs
  private double _c;
  // length perpendicular to the x-axis, recovered from each vertex
  // to the intersection with the asymptote
  private double _b;
  private Vect3d _pOnBound;
  // плоскость гиперболы
  private Plane3d _plane;

  /**
   * Новая система координат,
   * где центр системы соответсвует центру гиперболы,
   * действительная ось ( проходящая через фокусы) соответсвует оси Ох,
   * а мнимая ось соответсвует оси Оу.
   */
  private NewSystemOfCoor _centerHyperb;

  /**
   * Constructor of hyperbole by focus and point on Bound of hyperbole.
   * @param f1 first focus
   * @param f2 second focus
   * @param pointOnBound point owned hyperbole
   * @throws ExDegeneration
   */
  public Hyperbole3d(Vect3d f1, Vect3d f2, Vect3d pointOnBound) throws ExDegeneration {
    _f1 = f1;
    _f2 = f2;
    _pOnBound = pointOnBound;
    if (_f1.equals(_f2)) {
      throw new ExDegeneration("Фокусы гиперболы совпадают");
    }
    Vect3d n = Vect3d.vector_mul(Vect3d.sub(f1, f2), Vect3d.sub(pointOnBound, f1));
    if (n.norm() == 0) {
      throw new ExDegeneration("точка гиперболы лежит на фокальной оси");
    }
    double distBetweenFocuses = Vect3d.sub(f1, f2).norm();
    _realAxle = Math.abs(Vect3d.sub(_pOnBound, _f1).norm() - Vect3d.sub(_pOnBound, f2).norm()) / 2;
    if(_realAxle * 2 < Checker.eps())
      throw new ExDegeneration("Указанная точка гиперболы равноудалена от фокусов");
    if (distBetweenFocuses < _realAxle * 2 || Math.abs(distBetweenFocuses - _realAxle * 2) < Checker.eps()) {
      throw new ExDegeneration("Длина межфокусного расстояния меньше или равна сумме расстояний от точки до фокусов");
    }
    //Создаем новую координатную плоскость с началом координат в центре гиперболы
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
    _centerHyperb = new NewSystemOfCoor(_center, f2, pointOnOy, pointOnOZ);
  }

  /**
   * Constructor of hyperbole by points.
   */
  public Hyperbole3d(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), points.get(1), points.get(2));
  }

  /**
   * The 1st focus of the hyperbole.
   * @return
   */
  public Vect3d f1() {
    return _f1.duplicate();
  }

  /**
   * The 2nd focus of the hyperbole.
   * @return
   */
  public Vect3d f2() {
    return _f2.duplicate();
  }

  public NewSystemOfCoor newSystemOfCoorHyperb() {
    return _centerHyperb;
  }

  /**
   * @return normal vector of the plane of the ellipse
   * @throws ExDegeneration
   */
  public Vect3d normal() throws ExDegeneration {
    return _plane.n();
  }

  /**
   * Check hyperbole contains given point
   * @param p
  */
  public boolean contains(Vect3d p) {
    double v = Math.abs(Vect3d.sub(_f1, p).norm() - Vect3d.sub(_f2, p).norm());
    return v < _realAxle * 2 + Checker.eps() &&
           v > _realAxle * 2 - Checker.eps();
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
   * @return the plane of the hyperbole
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return _plane.duplicate();
  }

  /**
   *
   * @param t параметр по которому строится точка [-infinity, +infinity]
   * @param first условный параметр определяющий точку какой дуги мы хотим
   * построить [true(правая), false(левая)]
   * @return point on hyperbole
   */
  public Vect3d getPoint(double t, boolean first) {
    Vect3d newPoint;
    if (first) {
      newPoint = new Vect3d(_realAxle * Math.cosh(t), _b * Math.sinh(t), 0);
    } else {
      newPoint = new Vect3d((-1) * _realAxle * Math.cosh(t), _b * Math.sinh(t), 0);
    }
    return _centerHyperb.oldCoor(newPoint);
  }
  
  public Vect3d getPointForDraw(double t, boolean first) {
    Vect3d newPoint;
    if (first) {
      newPoint = new Vect3d(_realAxle * Math.cosh(t), _b * Math.sinh(t), 0);
    } else {
      newPoint = new Vect3d((-1) * _realAxle * Math.cosh(t), _b * Math.sinh(t), 0);
    }
    return newPoint;
  }

  /**
   * @return asymptotes of hyperbola
   * @throws ExDegeneration
   */
  public ArrayList<Line3d> asymptotes() throws ExDegeneration {
    ArrayList<Line3d> asymp = new ArrayList<>();
    asymp.add(Line3d.line3dByTwoPoints(center(), _centerHyperb.oldCoor(new Vect3d(1, b() / realAxle(),0))));
    asymp.add(Line3d.line3dByTwoPoints(center(), _centerHyperb.oldCoor(new Vect3d(1, - b() / realAxle(),0))));
    asymp.add(new Line3d (_centerHyperb.oldCoor(new Vect3d(0,0,0)),
            _centerHyperb.oldCoor(new Vect3d(1, - b() / realAxle(),0))));
    return asymp;
  }

  /**
   * Intersect hyperbole with ray
   * @param ray intersection
   * @return points intersection
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Ray3d ray) throws ExDegeneration {
    ArrayList<Vect3d> points = intersect(ray.line());
    Iterator<Vect3d> iter = points.iterator();
    while (iter.hasNext()) {
      Vect3d s = iter.next();
      if(!ray.containsPoint(s))
        iter.remove();
    }
    return points;
  }

  /**
   * Intersect hyperbole with rib
   * @param rib intersection
   * @return points inntersection
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExDegeneration {
    ArrayList<Vect3d> points = intersect(rib.line());
    Iterator<Vect3d> iter = points.iterator();
    while (iter.hasNext()) {
      Vect3d s = iter.next();
      if(!Checker.pointOnOpenSegment(rib, s))
        iter.remove();
    }
    return points;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_f1);
    result.add(_f2);
    result.add(_pOnBound);
    return result;
  }

  /**
   * Intersect hyperbole with line
   * @param line intersection
   * @return points intersection
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    if (_plane.containsLine(line)) {
      //Если луч входит в плоскость, то рассматриваем гиперболу и луч в новой системе координат,
      //где центр гиперболы совпадает с центром оси
      //Находим общие точки луча, как прямой с гиперболой
      Vect3d pl1 = _centerHyperb.newCoor(line.pnt());
      Vect3d pl2 = _centerHyperb.newCoor(line.pnt2());
      double x1, x2, y1, y2;
      //Исключение, если прямая проходит параллельно оси ординат
      if (Math.abs(pl1.x() - pl2.x()) < Checker.eps()) {
        y1 = _b * Math.sqrt(pl1.x() * pl1.x() - _realAxle
                * _realAxle) / _realAxle;
        y2 = -_b * Math.sqrt(pl1.x() * pl1.x() - _realAxle
                * _realAxle) / _realAxle;
        x1 = pl1.x();
        x2 = pl1.x();
      } else {
        //Общий случай
        //Определяем коэффиценты A, B и C прямой Ax + By + C = 0
        double A = pl1.y() - pl2.y();
        double B = pl2.x() - pl1.x();
        double C = pl1.x() * pl2.y() - pl1.y() * pl2.x();
        //Приводим прямую к необходимому нам виду
        //y = k * x + t, где получаем k и t
        double k = -A / B;
        double t = -C / B;
        x1 = (-_realAxle * _realAxle * k * t + _realAxle * _b
                * Math.sqrt(t * t - _realAxle * _realAxle * k * k + _b * _b))
                / (_realAxle * _realAxle * k * k - _b * _b);
        x2 = (-_realAxle * _realAxle * k * t - _realAxle * _b
                * Math.sqrt(t * t - _realAxle * _realAxle * k * k + _b * _b))
                / (_realAxle * _realAxle * k * k - _b * _b);
        y1 = x1 * k + t;
        y2 = x2 * k + t;
      }
      Vect3d pi1 = _centerHyperb.oldCoor(new Vect3d(x1, y1, 0));
      Vect3d pi2 = _centerHyperb.oldCoor(new Vect3d(x2, y2, 0));
      if (contains(pi1) && line.contains(pi1)) {
        points.add(pi1);
      }
      if (contains(pi2) && line.contains(pi2)) {
        points.add(pi2);
      }
    }
    //если луч не входит, проверяем на пересечение луча с плоскостью гиперболы
    else if (contains(line.intersectionWithPlane(_plane))) {
        points.add(line.intersectionWithPlane(_plane));
    }
    return points;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Hyperbole3d(points);
    } catch (ExDegeneration ex) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.printf("%s: Ошибка восстановления гиперболы%n", ex.getMessage());
      }
    }
    return null;
  }

  @Override
  public Vect3d getUpVect() {
    return _plane.n();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.HYPERBOLE3D;
  }
}
