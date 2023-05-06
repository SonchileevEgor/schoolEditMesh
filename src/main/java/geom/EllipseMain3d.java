package geom;

import static config.Config.LOG_LEVEL;
import java.util.ArrayList;
import java.util.Iterator;
import util.Log;

/**
 * Ellipse
 *
 * @author VitaliiZah
 */
public class EllipseMain3d implements i_Geom, i_OrientableGeom {

  // Фокусы эллипса
  private Vect3d _f1, _f2;
  // Точка на эллипсе
  private Vect3d _pOnBound;
  //Новая система координат, где центр системы соответсвует центру эллипса,
  //большая ось соответсвует оси Ох,
  //малая ось соответсвует оси Оу
  private NewSystemOfCoor _coorElipse;

  /**
   * constructor of elipse by focus f1, focus f2 and point on Bound of elipse
   *
   * @param f1 first focus
   * @param f2 second focus
   * @param pointOnBound point owned elipse
   * @throws geom.ExDegeneration
   */
  public EllipseMain3d(Vect3d f1, Vect3d f2, Vect3d pointOnBound) throws ExDegeneration {
    if (f1.equals(f2)) {
      throw new ExDegeneration("Фокусы эллипса совпадают");
    }
    Vect3d n = Vect3d.vector_mul(Vect3d.sub(f1, f2), Vect3d.sub(pointOnBound, f1));
    if (Checker.isEqual(n.norm(), 0)) {
      throw new ExDegeneration("Points f1, f2, pointOnBound lies on the same line");
    }
    double distFocus = Vect3d.norm(f1.sub(f2));
    double distFromPointTofocus = Vect3d.norm(pointOnBound.sub(f1)) + Vect3d.norm(pointOnBound.sub(f2));
    if (distFocus > distFromPointTofocus || Math.abs(distFocus - distFromPointTofocus) < Checker.eps()) {
      throw new ExDegeneration("длина межфокусного расстояния больше или равна сумме расстояний от точки до фокусов");
    }
    //Создаем новую координатную плоскость с началом координат в центре эллипса
    Vect3d center = Vect3d.conv_hull(f1, f2, 0.5);
    //Определяем плоскость, в которой лежит эллипс
    Plane3d plane = new Plane3d(f1, f2, pointOnBound);
    //Линия, которая соответствует оси оУ в новой координатной плоскости
    //проходит через малую ось эллипса
    Line3d oY = Vect3d.midLinesArePerpendicularToTheSegment(f1, f2, plane);
    //Находим точку, которая нам определит ось Оу
    Vect3d pointOnOy;
    if (oY.pnt().equals(center)) {
      pointOnOy = new Vect3d(oY.pnt2());
    } else {
      pointOnOy = new Vect3d(oY.pnt());
    }
    pointOnOy = new Vect3d(center).sum(Vect3d.rNormalizedVector(pointOnOy.sub(center), 1));
    //Линия, которая соответствует оси оZ в новой координатной плоскости
    //перпендикулярна плоскости эллипса и проходит через его центр
    Line3d oZ = Line3d.linePerpendicularPlane(plane, center);
    Vect3d pointOnOz;
    if (oZ.pnt().equals(center)) {
      pointOnOz = new Vect3d(oZ.pnt2());
    } else {
      pointOnOz = new Vect3d(oZ.pnt());
    }
    _f1 = f1;
    _f2 = f2;
    _pOnBound = pointOnBound;
    _coorElipse = new NewSystemOfCoor(center, _f2, pointOnOy, pointOnOz);
  }

  /**
   * constructor of elipse by points
   *
   * @throws ExDegeneration
   */
  public EllipseMain3d(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), points.get(1), points.get(2));
  }

  public Vect3d f1() {
    return _f1.duplicate();
  }

  public Vect3d f2() {
    return _f2.duplicate();
  }

  public Vect3d pOnBound() {
    return _pOnBound.duplicate();
  }

  public NewSystemOfCoor newSystemOfCoorElipse() {
    return _coorElipse;
  }

  /**
   * @return normal vector of the plane of the ellipse
   * @throws geom.ExDegeneration
   */
  public Vect3d normal() throws ExDegeneration {
    return plane().n();
  }

  /**
   * @return sum of the distances from its point to the foci
   */
  public double distance() {
    return Vect3d.norm(_f1.sub(_pOnBound)) + Vect3d.norm(_f2.sub(_pOnBound));
  }

  /**
   * @return point of the ellipse on angle parameter It uses a new coordinate
   * system
   * @param ang [0, 2 * PI]
   */
  public Vect3d getPoint(double ang) {
    Vect3d newPoint = new Vect3d(bigAxle() * Math.cos(ang), smallAxle() * Math.sin(ang), 0);
    return _coorElipse.oldCoor(newPoint);
  }
  
  public Vect3d getPointForDraw(double ang) {
    Vect3d newPoint = new Vect3d(bigAxle() * Math.cos(ang), smallAxle() * Math.sin(ang), 0);
    return newPoint;
  }

  /**
   * @return duplicate of ellipse
   */
  public EllipseMain3d duplicate() {
    try {
      return new EllipseMain3d(f1(), f2(), normal());
    } catch (ExDegeneration ex) {
      Log.out.println(ex.getMessage());
    }
    return null;
  }

  /**
   * @return distance from center to focus of ellipse
   */
  public double c() {
    return Vect3d.dist(this.center(), this.f2());
  }

  /**
   * Check bound of ellipse contains given point
   * @param p
   */
  public boolean containsBound(Vect3d p) {
    return Math.abs(Vect3d.norm(_f1.sub(p)) + Vect3d.norm(_f2.sub(p)) - distance()) < Checker.eps();
  }

  /**
   * Check ellipse contains given point
   * @param p
   */
  public boolean contains(Vect3d p) {
    return Math.abs(Vect3d.norm(_f1.sub(p)) + Vect3d.norm(_f2.sub(p))) <= distance() + Checker.eps();
  }

  /**
   * @return center of ellipse
   */
  public Vect3d center() {
    return Vect3d.conv_hull(this.f1(), this.f2(), 0.5);
  }

  /**
   * @return длину большой полуоси эллипса
   */
  public double bigAxle() {
    return distance() / 2;
  }

  /**
   * @return длину малой полуоси эллипса
   */
  public double smallAxle() {
    return Math.sqrt(bigAxle() * bigAxle() - c() * c());
  }

  /**
   * @return eccentricity of ellipse
   */
  public double e() {
    return c() / bigAxle();
  }

  /**
   * @return the plane of the ellipse
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return new Plane3d(_f1, _f2, _pOnBound);
  }

  /**
   * return point, which required to determine the new coord plane, including
   * the axis oX
   *
   * @return точку, которая принадлежит большой оси эллипса
   */
  public Vect3d getPointOnBoundBigAxis() {
    Vect3d bigAxle = Vect3d.rNormalizedVector(Vect3d.sub(this.f2(), this.center()), this.bigAxle());
    return Vect3d.sum(this.center(), bigAxle);
  }

  /**
   * @return return point, which required to determine the new coord plane,
   * including the axis oY
   * @throws ExDegeneration
   */
  public Vect3d getPointOnBoundSmallAxis() throws ExDegeneration {
    Line3d oY = Vect3d.midLinesArePerpendicularToTheSegment(f1(), f2(), plane());
    Vect3d pointOnOy;
    if (oY.pnt().equals(center())) {
      pointOnOy = new Vect3d(oY.pnt2());
    } else {
      pointOnOy = new Vect3d(oY.pnt());
    }
    return new Vect3d(center()).sum(Vect3d.rNormalizedVector(pointOnOy.sub(center()), smallAxle()));
  }

  /**
   * @return return point, which required to determine the new coord plane,
   * including the axis oZ
   * @throws ExDegeneration
   */
  public Vect3d getPointThridAxis() throws ExDegeneration {
    Line3d oZ = Line3d.linePerpendicularPlane(plane(), center());
    if (oZ.pnt().equals(center())) {
      return new Vect3d(oZ.pnt2());
    } else {
      return new Vect3d(oZ.pnt());
    }
  }

  /**
   * В качестве основного набора точек: точки фокусов, точка эллипса
   *
   * @return
   */
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
      return new EllipseMain3d(points);
    } catch (ExDegeneration ex) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.printf("%s: Ошибка восстановления эллипса%n", ex.getMessage());
      }
    }
    return null;
  }

  /**
   * Intersect ellipse with rib
   * @param rib intersection
   * @return points intersection
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExDegeneration, ExZeroDivision {
    ArrayList<Vect3d> points = intersect(rib.line());
    Iterator<Vect3d> iter = points.iterator();
    while (iter.hasNext()) {
      Vect3d s = iter.next();
      if(!Checker.pointOnOpenSegment(rib, s))
        iter.remove();
    }
    return points;
  }

  /**
   * Intersect ellipse with ray
   * @param ray intersection
   * @return points intersection
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  public ArrayList<Vect3d> intersect(Ray3d ray) throws ExDegeneration, ExZeroDivision {
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
   * Пересечение с лучом (трансверсальное).
   * Если луч и эллипс в одной плоскости, то пересечения нет.
   * @param ray
   * @return 
   */
  public Vect3d intersectWithRayTransversal(Ray3d ray) {
    // Проверяем точку пересечения плоскости эллипса с лучом
    // на принадлежность области эллипса.
    try {
      Vect3d result = ray.intersectionWithPlane(plane());
      return contains(result) ? result : null;
    } catch( ExDegeneration ex ){
      return null;
    }
  }

  /**
   * Intersect ellipse with line
   * @param line intersection
   * @return points intersection
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration, ExZeroDivision {
    ArrayList<Vect3d> points = new ArrayList<>();
    //если прямая принадлежит плоскости эллипса
    if (this.plane().containsLine(line)) {
      //составляем коэффицент сжатия
      double coef = smallAxle() / bigAxle();
      //центр эллипса(окружности) - как центр новой системы координат
      Vect3d center = new Vect3d(0,0,0);
      //точка на оси апликат в новой системе координат
      Vect3d pb = new Vect3d(0,0,1);
      //окружность - как результат сжатого эллипса
      //строится в центре системы координат и с радиусом,
      //равным длине малой полуоси эллипса
      Circle3d transCircle = new Circle3d(Vect3d.rNormalizedVector(pb.sub(center), smallAxle()), center);
      //прямая после сжатия
      Vect3d l1 = newSystemOfCoorElipse().newCoor(line.pnt());
      Vect3d l2 = newSystemOfCoorElipse().newCoor(line.pnt2());
      l1.mull_x(coef);
      l2.mull_x(coef);
      Line3d transLine = Line3d.getLineByTwoPoints(l1, l2);
      ArrayList<Vect3d> transPnt = transCircle.intersect(transLine);
      if(transPnt.get(0).equals(transPnt.get(1)))
        transPnt.remove(1);
      for (Vect3d p: transPnt) {
        //восстановление точек
        p.mull_x(1 / coef);
        points.add(newSystemOfCoorElipse().oldCoor(p));
      }
    //если прямая пересекает плоскость эллипса
    } else if (containsBound(line.intersectionWithPlane(plane()))) {
      points.add(line.intersectionWithPlane(plane()));
    }
    return points;
  }

  @Override
  public Vect3d getUpVect() {
    try {
      return normal();
    } catch (ExDegeneration ex) {
      return new Vect3d(0, 0, 1);
    }
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.ELLIPSE3D;
  }
}
