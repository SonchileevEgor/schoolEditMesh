package geom;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Parabola
 * @author VitaliiZah
 */
public class Parabola3d implements i_Geom, i_OrientableGeom {

  private Vect3d _f;// Фокус параболы
  private Line3d _directrix;// Директриса параболы
  private NewSystemOfCoor _centerParabola;//Новая система координат,
  //где центр системы соответсвует вершине параболы,
  //ось, которая делит параболу на две ветки соответсвует оси Ох,
  //прямая проходящая через вершину параболы и параллельна директрисе соответсвует оси Оу

  /**
   * constructor of parabola by focus and directrix
   * @param f focus of parabola
   * @param direct directrix of parabola
   * @throws ExDegeneration
   */
  public Parabola3d(Line3d direct, Vect3d f) throws ExDegeneration {
    if (direct.contains(f)) {
      throw new ExDegeneration("Фокус не должен лежать на прямой");
    }
    Vect3d pointOnDirect = direct.projectOfPoint(f);
    if (Vect3d.dist(f, pointOnDirect) < 2 * Checker.eps()) {
      throw new ExDegeneration("Фокус не может находится на столь близком расстоянии от директрисы");
    }
    _f = f;
    _directrix = direct;
  }

  /**
   * constructor of parabola by focus and points form the directrix
   * @param p1OnLine first point of directrix
   * @param p2OnLine second point of directrix
   * @param f focus of parabola
   * @throws ExDegeneration
   */
  public Parabola3d(Vect3d p1OnLine, Vect3d p2OnLine, Vect3d f) throws ExDegeneration {
    Line3d direct = new Line3d(new Rib3d(p1OnLine, p2OnLine));
    if (direct.contains(f)) {
      throw new ExDegeneration("Фокус не должен лежать на прямой");
    }
    Vect3d pointOnDirect = direct.projectOfPoint(f);
    if (Vect3d.dist(f, pointOnDirect) < 2 * Checker.eps()) {
      throw new ExDegeneration("Фокус не может находится на столь близком расстоянии от директрисы");
    }
    //Создаем новую координатную плоскость с началом координат в вершине параболы
    //Находим вершину параболы
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
    _centerParabola = new NewSystemOfCoor(top, pointOnOx, f, pointOnOZ);
    _f = f;
    _directrix = direct;
  }

  public Parabola3d(ArrayList<Vect3d> points) {
    try {
      Parabola3d par = new Parabola3d(points.get(0), points.get(1), points.get(2));
      _centerParabola = par.newSystemOfCoorParabola();
      _f = par.f();
      _directrix = par.directrix();
    } catch (ExDegeneration ex) {}
  }

  public Vect3d f() {
    return _f.duplicate();
  }

  public Line3d directrix() {
    return _directrix;
  }

  public NewSystemOfCoor newSystemOfCoorParabola() {
    return _centerParabola;
  }

  /**
   * Check parabola contains given point
   * @param p
   * @return 
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
   * @param p
   * @return the projection of the point on the directrix
   */
  public Vect3d projectPointOnDirect(Vect3d p) {
    return _directrix.projectOfPoint(p);
  }

  /**
   * return top of the parabola
   * @return point
   */
  public Vect3d top() {
    return Vect3d.conv_hull(f(), projectFokusOnDirect(), 0.5);
  }

  /**
   * Distance from the focus point to its projection on the directrix
   * @return
   */
  public double p() {
    return Vect3d.sub(f(), projectFokusOnDirect()).norm();
  }

  /**
   * @return plane
   * @throws ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return new Plane3d(_directrix, _f);
  }

  /**
   * @return return point, which required to determine the new coord plane,
   * including the axis oZ
   * @throws ExDegeneration
   */
  public Vect3d getPointThridAxis() throws ExDegeneration {
    Line3d oZ = Line3d.linePerpendicularPlane(plane(), top());
    if (oZ.pnt().equals(top())) {
      return new Vect3d(oZ.pnt2());
    } else {
      return new Vect3d(oZ.pnt());
    }
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
   * return point of the parabola depending on the parameter
   * @param param параметр по которому строится точка [-infinity, +infinity]
   * @return точку параболы
   */
  public Vect3d getPoint(double param) {
    Vect3d p = new Vect3d(param, (param * param) / (2 * p()), 0);
    return _centerParabola.oldCoor(p);
  }
  
  public Vect3d getPointForDraw(double param) {
    Vect3d p = new Vect3d(param, (param * param) / (2 * p()), 0);
    return p;
  }

  @Override
  public Vect3d getUpVect() {
    try {
      return plane().n();
    } catch (ExDegeneration ex) {
      return new Vect3d(0, 0, 1);
    }
  }

  /**
   * Intersect parabola with ray
   * @param ray intersection
   * @return points intersection
   */
  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> points = new ArrayList<>();
    try {
      points.addAll(intersect(ray.line()));
      Iterator<Vect3d> iter = points.iterator();
      while (iter.hasNext()) {
        Vect3d s = iter.next();
        if(!ray.containsPoint(s))
          iter.remove();
      }
    } catch( ExDegeneration ex ){}
    return points;
  }

    /**
   * Intersect parabola with rib
   * @param rib intersection
   * @return points intersection
   */
  public ArrayList<Vect3d> intersect(Rib3d rib) {
    ArrayList<Vect3d> points = new ArrayList<>();
    try {
      points.addAll(intersect(rib.line()));
      Iterator<Vect3d> iter = points.iterator();
      while (iter.hasNext()) {
        Vect3d s = iter.next();
        if(!Checker.pointOnOpenSegment(rib, s))
          iter.remove();
      }
    } catch( ExDegeneration ex ){}
    return points;
  }

  /**
   * Intersect parabola with line
   * @param line intersection
   * @return points intersection
   */
  public ArrayList<Vect3d> intersect(Line3d line) {
    ArrayList<Vect3d> points = new ArrayList<>();
    try {
      // Проверяем входит ли луч в плоскость кривой
      if (plane().containsLine(line)) {
        //Если луч входит в плоскость, то рассматриваем параболу и луч в новой системе координат,
        //где центр вершины параболы совпадает с центром оси
        //Находим общие точки луча как прямой с параболой
        Vect3d pl1 = _centerParabola.newCoor(line.pnt());
        Vect3d pl2 = _centerParabola.newCoor(line.pnt2());
        double x1, x2, y1, y2;
        //Исключение, если прямая(луч) проходит параллельно директрисе
        if (Math.abs(pl1.y() - pl2.y()) < Checker.eps()) {
          y1 = pl1.y();
          y2 = y1;
          x1 = Math.sqrt(Math.abs(y1 * 2 * p()));
          x2 = -x1;
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
          x1 = p() * k + Math.sqrt(p() * p() * k * k + 2 * p() * t);
          x2 = p() * k - Math.sqrt(p() * p() * k * k + 2 * p() * t);
          y1 = x1 * k + t;
          y2 = x2 * k + t;
        }
        Vect3d pi1 = _centerParabola.oldCoor(new Vect3d(x1, y1, 0));
        Vect3d pi2 = _centerParabola.oldCoor(new Vect3d(x2, y2, 0));
        if (contains(pi1) && line.contains(pi1)) {
          points.add(pi1);
        }
        if (contains(pi2) && line.contains(pi2) && !pi2.equals(pi1)) {
          points.add(pi2);
        }
      } else {
        //если луч не входит, проверяем на пересечение луча с плоскостью параболы
        if (contains(line.intersectionWithPlane(plane()))) {
          points.add(line.intersectionWithPlane(plane()));
        }
      }
    } catch( ExDegeneration ex ){}
    return points;
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
    return new Parabola3d(points);
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.PARABOLA3D;
  }
}
