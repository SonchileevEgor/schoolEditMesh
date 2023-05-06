package geom;

import java.util.ArrayList;

/**
 * Elliptic paraboloid rotation
 *
 * @author Vitaliy
 */
public class EllipticParaboloid3d implements i_Geom {

  private Vect3d _f;// Фокус параболоида
  private Line3d _directrix;// Директриса параболоида
  private NewSystemOfCoor _centerParaboloid;//Новая система координат,
  //где центр системы соответсвует вершине параболоида,
  //ось, которая делит параболоид на две половины соответсвует оси Ох,
  //прямая проходящая через вершину параболы и параллельна директрисе соответсвует оси Оу

  /**
   * конструктор параболоида реализуется как конструктор параболы constructor of
   * elliptic paraboloid by focus and directrix
   *
   * @param f focus of paraboloid
   * @param direct directrix of paraboloid
   * @throws ExDegeneration
   */
  public EllipticParaboloid3d(Line3d direct, Vect3d f) throws ExDegeneration {
    this(direct.pnt(), direct.pnt2(), f);
  }
  
  /**
   * конструктор параболоида реализуется как конструктор параболы constructor of
   * paraboloid by focus and points form the directrix
   *
   * @param p1OnLine first point of directrix
   * @param p2OnLine second point of directrix
   * @param f focus of paraboloid
   * @throws ExDegeneration
   */
  public EllipticParaboloid3d(Vect3d p1OnLine, Vect3d p2OnLine, Vect3d f) throws ExDegeneration {
    Line3d direct = new Line3d(new Rib3d(p1OnLine, p2OnLine));
    if (direct.contains(f)) {
      throw new ExDegeneration("Фокус не должен лежать на прямой");
    }
    Vect3d pointOnDirect = direct.projectOfPoint(f);
    if (Vect3d.dist(f, pointOnDirect) < 2 * Checker.eps()) {
      throw new ExDegeneration("Фокус не может находится на столь близком расстоянии от директрисы");
    }
    //Создаем новую координатную плоскость с началом координат в вершине параболоида
    //Находим вершину параболоида
    Vect3d projectFokusOnDirect = direct.projectOfPoint(f);
    Vect3d top = Vect3d.conv_hull(f, projectFokusOnDirect, 0.5);
    Vect3d pointOnOx;
    Vect3d pointOnOZ;
    //Линия, которая соответствует оси оХ в новой координатной плоскости
    //Линия проходит через вершину параболы и параллельна директрисе
    Line3d oX = Line3d.lineParallelLine(direct, top);
    //Определяем плоскость параболы
    Plane3d plane = new Plane3d(direct, f);
    if (oX.pnt().equals(top)) {
      pointOnOx = oX.pnt2();
    } else {
      pointOnOx = oX.pnt();
    }
    //Линия, которая соответствует оси оZ в новой координатной плоскости
    Line3d oZ = Line3d.linePerpendicularPlane(plane, top);
    if (oZ.pnt().equals(top)) {
      pointOnOZ = oZ.pnt2();
    } else {
      pointOnOZ = oZ.pnt();
    }
    //Фокус определит ось оУ для новой координатной плоскости
    _centerParaboloid = new NewSystemOfCoor(top, pointOnOx, f, pointOnOZ);
    _f = f;
    _directrix = direct;
  }

  /**
   * paraboloid by parabola
   *
   * @param parabola parabola with which the paraboloid construction
   * @throws ExDegeneration
   */
  public EllipticParaboloid3d(Parabola3d parabola) throws ExDegeneration {
    this(parabola.directrix().pnt(), parabola.directrix().pnt2(), parabola.f());
  }

    public EllipticParaboloid3d(ArrayList<Vect3d> points) {
    try {
      EllipticParaboloid3d el_par = new EllipticParaboloid3d(points.get(0), points.get(1), points.get(2));
      _centerParaboloid = el_par.newSystemOfCoorParaboloid();
      _f = el_par.f();
      _directrix = el_par.directrix();
    } catch (ExDegeneration ex) {
    }
  }

  /**
   *constructing tangency plane to an elliptic paraboloid rotation at a selected point of the paraboloid
   * @param point point of the paraboloid
   * @return the tangent plane at the point
   * @throws ExGeom
   */
  public Plane3d tangentPlaneInPoint(Vect3d point) throws ExGeom {
    Vect3d pointNew = _centerParaboloid.newCoor(point);
    Vect3d f = _centerParaboloid.newCoor(f());
    Vect3d center = _centerParaboloid.oldCoor(Vect3d.O);
    if (Checker.isPointOnEllipticParaboloid(point, this)) {
      Vect3d normal = new Vect3d(2 * pointNew.x(), 2 * pointNew.y(), -4 * f.x());
      Vect3d c = _centerParaboloid.oldCoor(normal);
      Vect3d normalNew = new Vect3d(c.x() - center.x(), c.y() - center.y(), c.z() - center.z());
      Plane3d tangPlane = new Plane3d(normalNew, point);
      return tangPlane;
    } else {
      throw new ExGeom("Точка не принадлежит эллиптическому параболоиду");
    }
  }
  
  public Vect3d f() {
    return _f.duplicate();
  }

  public Line3d directrix() {
    return _directrix;
  }

  public NewSystemOfCoor newSystemOfCoorParaboloid() {
    return _centerParaboloid;
  }

  /**
   * Check paraboloid contains given point
   *
   * @param p
   */
  public boolean contains(Vect3d p) {
    return Math.abs(Vect3d.norm(f().sub(p))
            - Vect3d.norm(p.sub(projectPointOnDirect(p))))
            < Checker.eps();
  }

  /**
   * @return the projection of the focus on the directrix
   */
  public Vect3d projectFokusOnDirect() {
    return _directrix.projectOfPoint(f());
  }

  /**
   * @return the projection of the point on the directrix
   */
  public Vect3d projectPointOnDirect(Vect3d p) {
    return _directrix.projectOfPoint(p);
  }

  /**
   * return top of the paraboloid
   *
   * @return point
   */
  public Vect3d top() {
    return Vect3d.conv_hull(f(), projectFokusOnDirect(), 0.5);
  }

  /**
   * Distance from the focus point to its projection on the directrix
   *
   * @return
   */
  public double p() {
    return Vect3d.sub(f(), projectFokusOnDirect()).norm();
  }
  
  /**
   * @return return point, which required to determine the new coord plane,
   * including the axis oX
   * @throws ExDegeneration
   */
  public Vect3d getPointAxisOx() throws ExDegeneration {
    Line3d oX = Line3d.lineParallelLine(_directrix, top());
    if (oX.pnt().equals(top())) {
      return oX.pnt2();
    } else {
      return oX.pnt();
    }
  }

  /**
   * return point of the paraboloid depending on the parameter
   * for drawing
   * @param param параметр по которому строится точка [-infinity, +infinity]
   * @param ang угол поворота точки относительно оси, проходящей через вершину и
   * фокус гиперболоида
   * @return точку параболы
   */
  public Vect3d getPointForDraw(double param, double ang) {
    Vect3d p = new Vect3d(param, (param * param) / (2 * p()), 0);
    return rotatePointofParaboloid(p, ang);
  }

  /**
   * for drawing
   * @param point точка вращения
   * @param ang угол вращения точки относительно оси , проходящей через вершину
   * и фокус параболоида
   * @return
   */
  public Vect3d rotatePointofParaboloid(Vect3d point, double ang) {
    return point.rotate(ang, Vect3d.UY);
  }
  
  /**
   * return point of the paraboloid depending on the parameter
   * @param param параметр по которому строится точка [-infinity, +infinity]
   * @param ang угол поворота точки относительно оси, проходящей через вершину и
   * фокус гиперболоида
   * @return точку параболы
   */
  public Vect3d getPoint(double param, double ang) {
    Vect3d p = new Vect3d(param, (param * param) / (2 * p()), 0);
    return rotatePointofParaboloid(_centerParaboloid.oldCoor(p), ang);
  } 

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_directrix.pnt());
    result.add(_directrix.pnt2());
    result.add(_f);
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new EllipticParaboloid3d(points);
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.ELLIPTIC_PARABOLOID3D;
  }
}
