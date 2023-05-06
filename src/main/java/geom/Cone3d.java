package geom;

import static geom.Checker.eps;
import static geom.Checker.isLineAndSegmentIntersect;
import static geom.Checker.isOrthogonal;
import static geom.Sphere3d.areTwoSpheresEqual;
import static geom.Vect3d.dist;
import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sum;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Cone math object.
 */
public class Cone3d implements i_Geom, i_OrientableGeom {
  private Vect3d _v, _c;//вершина, центр основания конуса
  private double _r;//радиус основания

  /**
   * Constructor of math cone
   *
   * @param v cone vertex
   * @param c center of cone bottom
   * @param r radius of cone bottom
   */
  public Cone3d(Vect3d v, Vect3d c, double r) {
    _v = v;
    _c = c;
    _r = r;
  }

  /**
   * Constructor of cone by points
   * @param points
   */
  public Cone3d(ArrayList<Vect3d> points) {
    Vect3d p_c = points.get(2).sub(points.get(1)); //вектор от центра к точке на окружности
    double r = p_c.norm();
    _v = points.get(0);
    _c = points.get(1);
    _r = r;
  }

  /**
   * @return cone vertex
   */
  public Vect3d v() {
    return _v.duplicate();
  }

  /**
   * @return center of cone bottom
   */
  public Vect3d c() {
    return _c.duplicate();
  }

  /**
   * @return radius of cone bottom
   */
  public double r() {
    return _r;
  }

  /**
   * @return height vector of cone; begin of vector is vertex, end of vector is center
   */
  public Vect3d h() {
    return Vect3d.sub(_c, _v);
  }

  /**
   * @return cone bottom plane
   */
  public Plane3d basePlane() {
    return new Plane3d(h(), c());
  }

  /**
   * Возвращает координату z точки на поверхности неограниченного вниз конуса по
   * известным x и y (в системе координат связанной с конусом).
   *
   * @param x абсцисса точки конуса
   * @param y ордината точки конуса
   * @return аппликата точки конуса
   */
  public double z(double x, double y) {
    return h().norm() - h().norm() * Math.sqrt(x * x + y * y) / _r;
  }

  /**
   * @return heightLength (length) of the cone
   */
  public double hLen() {
    return h().norm();
  }

  /**
   * @return cone lateral length
   */
  public double lat() {
    return Math.sqrt(_r * _r + hLen() * hLen());
  }

  /**
   * Площадь боковой поверхности конуса.
   * @return
   */
  public double sideSurfaceArea() {
    return Math.PI * _r * hLen();
  }

  /**
   * Площадь поверхности конуса.
   * @return
   */
  public double surfaceArea() {
    return Math.PI * _r * (hLen() + _r);
  }

  /**
   * @return circumscribed sphere of the cone
   */
  public Sphere3d outSphere() throws ExDegeneration {
    if (Checker.isTwoPointsEqual(_c, _v)) {
      throw new ExDegeneration("Вершина конуса лежит на основании: нет описанной сферы");
    } else {
      Vect3d center = new Vect3d();
      double radius = (_r * _r + hLen() * hLen()) / (2 * hLen());
      double _lambda = radius / hLen();
      Vect3d v1 = _c.duplicate();
      v1.inc_mul(_lambda);
      Vect3d v2 = _v.duplicate();
      v2.inc_mul(1 - _lambda);
      center.inc_add(v1);
      center.inc_add(v2);
      return new Sphere3d(center, radius);
    }
  }

  /**
   * Определение общей окружности у конуса и вписанной сферы
   *
   * @param sphere вписанная сфера
   * @return окружность
   * @throws geom.ExGeom
   */
  public Circle3d circleBySphereInCone(Sphere3d sphere) throws ExGeom {
    Sphere3d inSphere = inSphere();
    if (areTwoSpheresEqual(inSphere, sphere)) {
      double coef = (lat() - r()) * Math.sqrt(lat() * lat() - r() * r()) / lat();
      Vect3d center = sum(v(), rNormalizedVector(h(), coef));
      double radius = (r() * (lat() - r())) / lat();
      return new Circle3d(radius, h(), center);
    } else {
      throw new ExGeom("сфера не вписана в конус");
    }
  }

  /**
   * @return inscribed sphere of the cone
   */
  public Sphere3d inSphere() {
    double radius = 0;//_r*(lat()-_r)/hLen();
    Vect3d center = new Vect3d();
    if (!Checker.isTwoPointsEqual(_c, _v)) {
      radius = _r * (lat() - _r) / hLen();
      double _lambda = radius / hLen();
      Vect3d v1 = _c.duplicate();
      v1.inc_mul(1 - _lambda);
      Vect3d v2 = _v.duplicate();
      v2.inc_mul(_lambda);
      center.inc_add(v1);
      center.inc_add(v2);
    }
    return new Sphere3d(center, radius);
  }

  /**
   * Find intersection cone with line
   *
   * @param line section line
   * @return point if section is point, two points if section is segment
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Vect3d> result = new ArrayList<Vect3d>();
    // Вершина конуса не принадлежит прямой.
    if (!line.contains(_v)) {
      // Строим плоскость через вершмну и прямую.
      Plane3d plane = new Plane3d(line, v());
      // Сечение конуса плоскостью - треугольник. Тогда сечение конуса прямой - сечение треугольника прямой
      if (intersectWithPlane(plane).sectionType().equals("triangle")) {
        Triang3d triang = new Triang3d(intersectWithPlane(plane).points().get(0),
                intersectWithPlane(plane).points().get(1),
                intersectWithPlane(plane).points().get(2));
        result = triang.intersect(line);
        // Сечение конуса плоскостью - отрезок. Тогда сечение конуса прямой - пересечение отрезка и прямой.
      } else if (intersectWithPlane(plane).sectionType().equals("segment")) {
        Rib3d rib = new Rib3d(intersectWithPlane(plane).points().get(0),
                intersectWithPlane(plane).points().get(1));
        if (isLineAndSegmentIntersect(line, rib)) {
          result.add(rib.intersectWithLine(line));
        }
        // Если сечение конуса плоскостью - вершина, сечение прямой пусто.
      } else if (!intersectWithPlane(plane).sectionType().equals("vertex")) {
        throw new ExGeom("прямая не пересекает конус");
      }
      // Вершина конуса принадлежит прямой.
    } else {
      result.add(v());
      // Смотрим, пересекает ли прямая плоскость основания.
      // Не пересекает.
      if (isOrthogonal(line.l(), basePlane().n())) {
        result.add(v());
        // Пересекает.
      } else {
        Vect3d point = line.intersectionWithPlane(basePlane());
        if (dist(point, c()) < eps()) {
          result.add(point);
        }
      }
    }
    return result;
  }

  public ConeSection3d intersectWithPlane(Plane3d plane) throws ExGeom {
    try {
      ConeSection3d conSec = new ConeSection3d(this, plane);
      if ("empty".equals(conSec.sectionType()))
        throw new ExGeom("плоскость не пересекает конус");
      return conSec;
    } catch (ExZeroDivision ex) {
      throw new ExGeom("плоскость не пересекает конус");
    }
  }

  public Circle3d getBaseCircle() {
    return new Circle3d(_r, basePlane().n(), _c);
  }

  public ArrayList<Vect3d> getBaseCirclePoints() {
      ArrayList<Vect3d> base_points = getBaseCircle().getAsAbstractPolygon();
      return base_points;
  }

  public ArrayList<Polygon3d> faces() {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    ArrayList<Vect3d> base_points = getBaseCircle().getAsAbstractPolygon();
    Vect3d p1 = new Vect3d(base_points.get(0));
    Vect3d p2 = new Vect3d(base_points.get(1));
    Vect3d p3 = new Vect3d(base_points.get(base_points.size() - 1));
    Vect3d p4 = new Vect3d(_v);
    Orientation bypass = Orientation.getBodyOrientation(p1, p2, p3, p4);
    try {
      if (bypass == Orientation.LEFT) {
        Collections.reverse(base_points);
      }
      faces.add(new Polygon3d(new ArrayList<>(base_points)));
      int last = base_points.size();
      for (int i = 0; i < last; i++) {
        faces.add(new Polygon3d(base_points.get(i % last), _v, base_points.get((i + 1) % last)));
      }
    } catch (ExGeom ex) {
      util.Fatal.warning("Error @ Cone3d.faces");
    }
    return faces;
  }

  public Pyramid3d getAbstractPyramid() {
    ArrayList<Vect3d> base_points = getBaseCircle().getAsAbstractPolygon();
    try {
      Pyramid3d pyramid = new Pyramid3d(_v, new Polygon3d(base_points));
      return pyramid;
    } catch (ExGeom ex) {
      util.Fatal.warning("Error @ Cone3d.getAbstractPyramid");
    }
    return null;
  }
  
  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExGeom ex) {}
    return result;
  }

  /**
    * В качестве основного набора точек - вершина, центр основания,
    * точка на окружности-основании.
    * @return
    */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList();
    result.add(_v);
    result.add(_c);
    Circle3d circ = getBaseCircle();
    result.add(circ.getPointOnBoundary());
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new Cone3d(points);
  }

  @Override
  public Vect3d getUpVect() {
    return h().getNormalized();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.CONE3D;
  }
}