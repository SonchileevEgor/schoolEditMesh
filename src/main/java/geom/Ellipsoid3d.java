package geom;

import static config.Config.LOG_LEVEL;
import java.util.ArrayList;
import util.Log;

/**
 * Ellipsoid of rotation
 * @author Vitaliy
 */
public class Ellipsoid3d implements i_Geom, i_OrientableGeom{

  // Фокусы эллипсоида
  private Vect3d _f1, _f2;
  // Точка на эллипсоиде
  private Vect3d _pOnBound;
  //Эллипсоид строится как эллипс, образованный с помощью двух фокусов и точки
  //Новая система координат, где центр системы соответсвует центру эллипсоида,
  //большая ось соответсвует оси Ох,
  //малая ось соответсвует оси Оу
  private NewSystemOfCoor _coorEllipsoid;

  /**
   * constructor of ellipsoid by focus f1, focus f2 and point on Bound of ellipsoid
   * Конструктор эллипсоида вращения представляет собой конструктор эллипса
   * @param f1 first focus
   * @param f2 second focus
   * @param pointOnBound point owned ellipsoid
   * @throws geom.ExDegeneration
   */
  public Ellipsoid3d(Vect3d f1, Vect3d f2, Vect3d pointOnBound) throws ExDegeneration {
    if (f1.equals(f2)) {
      throw new ExDegeneration("Фокусы эллипсоида совпадают");
    }
    Vect3d n = Vect3d.vector_mul(Vect3d.sub(f1, f2), Vect3d.sub(pointOnBound, f1));
    if (Checker.isEqual(n.norm(), 0)) {
      throw new ExDegeneration("Points f1, f2, pointOnBound lies on the same line");
    }
    double distFocus = Vect3d.norm(f1.sub(f2));
    double distFromPointTofocus = Vect3d.norm(pointOnBound.sub(f1)) + Vect3d.norm(pointOnBound.sub(f2));
    if (distFocus > distFromPointTofocus || Math.abs(distFocus - distFromPointTofocus) < Checker.eps()) {
      throw new ExDegeneration("Длинна межфокусного расстояния больше или равна сумме расстояний от точки до фокусов");
    }
    //Создаем новую координатную плоскость с началом координат в центре эллипсоида
    Vect3d center = Vect3d.conv_hull(f1, f2, 0.5);
    //Строим плоскость, в которой лежит эллипс
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
    _coorEllipsoid = new NewSystemOfCoor(center, _f2, pointOnOy, pointOnOz);
  }

  /**
   * constructor of ellipsoid by ellipse
   *
   * @param el ellipse
   * @throws ExDegeneration 
   */
  public Ellipsoid3d(EllipseMain3d el)throws ExDegeneration {
    this(el.f1(), el.f2(), el.pOnBound());
  }
  
  /**
   * constructor of elipsoid by points
   * @param points
   * @throws ExDegeneration
   */
  public Ellipsoid3d(ArrayList<Vect3d> points) throws ExDegeneration {
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

  public NewSystemOfCoor newSystemOfCoorEllipsoid() {
    return _coorEllipsoid;
  }

   /**
   * @return center of ellipsoid
   */
  public Vect3d center() {
    return Vect3d.conv_hull(this.f1(), this.f2(), 0.5);
  }

  /**
   *
   * @param ang угол определяет точку в плоскости эллипса
   * @param ang2 угол определяет вращение точки относительно большой оси
   * эллипсоида
   * @return
   */
  public Vect3d getPoint(double ang, double ang2) {
    Vect3d newPoint = new Vect3d(bigAxle() * Math.cos(ang), smallAxle() * Math.sin(ang), 0);
    return rotatePointOfEllipsoid(_coorEllipsoid.oldCoor(newPoint), ang2);
  }

   /**
   * @param point точка вращения вокруг большой оси эллипсоида
   * @param ang угол определяет плоскость эллипса в эллипсоиде
   * @return
   */
  public Vect3d rotatePointOfEllipsoid(Vect3d point, double ang) {
    return point.sub(f1()).rotate(ang, f2().sub(f1())).sum(f1());
  }

   /**
   * @return длину большой полуоси эллипсоида
   */
  public double bigAxle() {
    return distance() / 2;
  }

  /**
   * @return длину малой полуоси эллипсоида
   */
  public double smallAxle() {
    return Math.sqrt(bigAxle() * bigAxle() - c() * c());
  }

  /**
   * @return distance from center to focus of ellipsoid
   */
  public double c() {
    return Vect3d.dist(this.center(), this.f2());
  }

  /**
   * @return sum of the distances from its point to the foci
   */
  public double distance() {
    return Vect3d.norm(_f1.sub(_pOnBound)) + Vect3d.norm(_f2.sub(_pOnBound));
  }

  /**
   * @return the plane of the ellipsoid using focuses and point on bound
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return new Plane3d(_f1, _f2, _pOnBound);
  }

  /**
   * @return circumscribed sphere of the ellipsoid
   */
  public Sphere3d outSphere() throws ExDegeneration {
    return new Sphere3d(center(),bigAxle());
  }

  /**
   * @return inscribed sphere of the ellipsoid
   */
  public Sphere3d inSphere() throws ExDegeneration {
    return new Sphere3d(center(), smallAxle());
  }

  /**
   * conversion ellipsoid to sphere
   * @return
   */
  public Sphere3d convEllipsoidInSphere() {
   return new Sphere3d(new Vect3d(0,0,0), smallAxle());
  }

  /**
   * conversion plane
   * @param plane
   * @return
   * @throws ExDegeneration
   */
  public Plane3d convPlane(Plane3d plane) throws ExDegeneration {
    // разбиваем плоскость на образующие точки
    // точки преобразуем и по ним строим новую плоскость
    ArrayList<Vect3d> pOnPlane = plane.deconstr();
    for(Vect3d p: pOnPlane) {
      int i = pOnPlane.indexOf(p);
      p = newSystemOfCoorEllipsoid().newCoor(p);
      p.mull_x(smallAxle() / bigAxle());
      pOnPlane.set(i, p);
    }
    return new Plane3d(pOnPlane);
  }

  /**
   * conversion line
   * @param line
   * @return
   * @throws ExDegeneration
   */
  public Line3d convLine(Line3d line) throws ExDegeneration {
     ArrayList<Vect3d> pOnLine = line.deconstr();
     for(Vect3d p: pOnLine) {
       int i = pOnLine.indexOf(p);
       p = newSystemOfCoorEllipsoid().newCoor(p);
       p.mull_x(smallAxle() / bigAxle());
       pOnLine.set(i, p);
     }
     return new Line3d(pOnLine);
  }

  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> pOnEllipsoid = convEllipsoidInSphere().intersect(convLine(line));
    for(Vect3d p: pOnEllipsoid) {
      int i = pOnEllipsoid.indexOf(p);
      p = reconvPoint(p);
      pOnEllipsoid.set(i, p);
    }
    return pOnEllipsoid;
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) {
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
   * inverse conversion point
   * @param point
   * @return
   */
  public Vect3d reconvPoint(Vect3d point) {
      point.mull_x(bigAxle() / smallAxle());
      return newSystemOfCoorEllipsoid().oldCoor(point);
  }

  /**
   * Find section of ellipsoid by plane
   * @param plane section plane
   * @return ellipse section
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public EllipseMain3d sectionByPlane(Plane3d plane) throws ExDegeneration, ExGeom {
    Vect3d center = new Vect3d(0, 0, 0);
    // Находим пересечение сжатого эллипсоида (сферы) с преобразованной плоскостью
    Sphere3d sph = new Sphere3d(center, smallAxle());
    Plane3d transPlane = convPlane(plane);
    Circle3d transCircle = sph.sectionByPlane(transPlane);
    // проецируем вектор оХ на плоскость окружности для получения необходимых точек
    Vect3d projVectOxOnCircle = Line3d.getLineByTwoPoints(center, new Vect3d(1, 0, 0)).projectOnPlane(transCircle.plane()).l();
    ArrayList<Vect3d> pCircle = new ArrayList<>();
    // получение данных точек позволит определить параметры для построения эллипса
    pCircle.add(transCircle.center().sum(Vect3d.rNormalizedVector(projVectOxOnCircle, transCircle.radiusLength())));
    pCircle.add(transCircle.center());
    // обратное преобразование точек
    for (Vect3d p : pCircle) {
      int i = pCircle.indexOf(p);
      p = reconvPoint(p);
      pCircle.set(i, p);
    }
    // определение эллипса сечения
    // большая полуось
    double a = Vect3d.dist(pCircle.get(0), pCircle.get(1));
    // малая полуось
    double b = transCircle.radiusLength();
    // половина межфокусного расстояния
    double c = Math.sqrt(a * a - b * b);
    Vect3d f1 = pCircle.get(1).sum(Vect3d.rNormalizedVector(pCircle.get(1).sub(pCircle.get(0)), c));
    Vect3d f2 = pCircle.get(1).sum(Vect3d.rNormalizedVector(pCircle.get(1).sub(pCircle.get(0)), -c));
    // чтобы получить точку на эллипсе сечения
    // находим прямую, соответсвующая малой оси эллипса
    Line3d linePerpend = Line3d.linePerpendicularLine2d(Line3d.line3dByTwoPoints(f1, f2), pCircle.get(1)).projectOnPlane(plane);
    Vect3d pBound = pCircle.get(1).sum(Vect3d.rNormalizedVector(linePerpend.l(), b));
    return new EllipseMain3d(f1, f2, pBound);
  }

  /**
   * constructing tangency plane to an ellipsoid rotation at a selected point of the ellipsoid
   * @param point point of the ellipsoid
   * @return the tangent plane at the point
   * @throws ExGeom 
   */
  public Plane3d tangentPlaneInPoint(Vect3d point) throws ExGeom {
    Vect3d pointNew = _coorEllipsoid.newCoor(point);
    if (Checker.isPointOnEllipsoid(point, this)) {
      Vect3d normal = new Vect3d(-smallAxle()*smallAxle()*pointNew.x()/(bigAxle()*bigAxle()*pointNew.z()), -pointNew.y()/pointNew.z(), -1);
      Vect3d c = _coorEllipsoid.oldCoor(normal);
      Vect3d normalNew = new Vect3d(c.x() - center().x(), c.y() - center().y(), c.z() - center().z());
      Plane3d tangPlane = new Plane3d(normalNew, point);
      return tangPlane;
    } else {
      throw new ExGeom("Точка не принадлежит эллипсоиду");
    }
  }
  
  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.ELLIPSOID3D;
  }

   /**
   * В качестве основного набора точек: точки фокусов, точка эллипсоида
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
      return new Ellipsoid3d(points);
    } catch (ExDegeneration ex) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.printf("%s: Ошибка восстановления эллипсоида%n", ex.getMessage());
      }
    }
    return null;
  }

  @Override
  public Vect3d getUpVect() {
    try {
      return plane().n();
    } catch (ExDegeneration ex) {
      return new Vect3d(0, 0, 1);
    }
  }

  }
