package geom;

import static geom.Checker.pointOnOpenSegment;
import static geom.Line3d.line3dByTwoPoints;
import static geom.Vect3d.mul;
import static geom.Vect3d.sub;
import static geom.Vect3d.sum;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * В этом классе будут находиться функции(кроме проверок, они в checker),
 * которые необходимы для работы именно с отрезками(не с векторами).
 * Здесь пойдет речь только о трехмерных отрезках.
 * Для работы с одномерным отрезком есть класс Segment1D
 * Параметры a и b концы отрезка. Отрезок считается не направленным.
 * @author Пользователь
 */
public class Rib3d implements i_Geom {
  private Vect3d _a, _b;

  /**
   * Constructor of rib by vertices
   * @param a firs point
   * @param b second point
   * @throws geom.ExDegeneration
   */
  public Rib3d(Vect3d a, Vect3d b) throws ExDegeneration {
    if(!Vect3d.equals(a, b)) {
      _a = a; _b = b;
    } else {
      throw new ExDegeneration("концы отрезков совпадают");
    }
  }

   public Rib3d(ArrayList<Vect3d> points) throws ExDegeneration {
    if(!Vect3d.equals(points.get(0), points.get(1))) {
      _a = points.get(0);
      _b = points.get(1);
    } else {
      throw new ExDegeneration("концы отрезков совпадают");
    }
  }

  public Rib3d(Rib3d r) { _a = r.a(); _b = r.b(); }

  public Rib3d duplicate() {
    return new Rib3d(this);
  }

  /**
   * The first vertex.
   * @return
   */
  public Vect3d a() {
    return _a.duplicate();
  }

  /**
   * The second vertex.
   * @return
   */
  public Vect3d b() {
    return _b.duplicate();
  }

  /**
   * Center of the rib.
   * @return
   */
  public Vect3d center() {
    return Vect3d.conv_hull(_a, _b, 0.5);
  }

  /**
   * Lengh of the rib.
   * @return
   */
  public double length() {return sub(_a, _b).norm(); }

  public String toString(int precision) {
    return String.format(
        "(%." + precision + "f; %." + precision + "f; %." + precision + "f) ->"
            + "(%." + precision + "f; %." + precision + "f; %." + precision + "f)",
        _a.x(), _a.y(), _a.z(), _b.x(), _b.y(), _b.z());
  }

  /**
   * Find inner point by parameter.
   * @param t parameter from [0, 1]
   * @return point t * a + (1 - t) * b
   */
  public Vect3d innerPoint(double t){
    return sum(mul(_a, t), mul(_b, 1 - t));
  }

  /**
   * Check equivalence of two ribs.
   * @param rib first rib
   * @return true if ribs are equal false otherwise
   */
  @Override
  public boolean equals(Object rib) {
    if( rib == null )
      return false;
    
    if( getClass() != rib.getClass() )
      return false;
    
    Rib3d rib3d = (Rib3d)rib;
    return ((a().equals(rib3d.a()) && b().equals(rib3d.b())))
        || (a().equals(rib3d.b()) && b().equals(rib3d.a()));
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(_a);
    hash = 97 * hash + Objects.hashCode(_b);
    return hash;
  }

  /**
   * Create chain of segments.
   * @param ribs
   * @return sorted chain of segments.
   * @throws ExDegeneration
   */
  public static ArrayList<Rib3d> sort(List<Rib3d> ribs) throws ExDegeneration {
    ArrayList<Rib3d> chain = new ArrayList<>();
    if (ribs.isEmpty())
      return chain;
    // working copy
    List<Rib3d> copy = new ArrayList<>(ribs);
    // current rib in chain
    Rib3d current = copy.get(0);
    chain.add(current);
    // delete current element
    copy.remove(0);
    // construct chain
    for(int i = 0; i < ribs.size() - 1; i++) {
      for(int j = 0; j < copy.size(); j++) {
        Rib3d rib = copy.get(j);
        if (current.b().equals(rib.a()) ||
            current.b().equals(rib.b())) {
          if (current.b().equals(rib.b()))
            current = new Rib3d(rib.b(), rib.a());
          else
            current = new Rib3d(rib.a(), rib.b());
          copy.remove(j);
          chain.add(current);
          break;
        }
      }
    }
    return chain;
  }

  /**
   * Line by two vertices of rib.
   * @return
   * @throws ExDegeneration
   */
  public Line3d line() throws ExDegeneration {
    return line3dByTwoPoints(_a, _b);
  }

  /**
   * @param line
   * @return возвращает точку пересечения отрезка с прямой, если она есть
   * отрезок считается замкнутым
   * @throws ExDegeneration
   */
  public Vect3d intersectWithLine(Line3d line) throws ExDegeneration {
   if(!Checker.isLineAndSegmentIntersect(line, this))
     throw new ExDegeneration("отрезок и прямая не пересекаются");
   else return line.intersectionWithLine(this.line());
  }

  /**
   * Возвращает точки пересечения окружности и отрезка.
   * @param circ
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Circle3d circ) throws ExDegeneration {
    ArrayList<Vect3d> points = circ.intersect(this.line());
    Iterator<Vect3d> iter = points.iterator();
    while (iter.hasNext()) {
       Vect3d s = iter.next();
        if(!Checker.pointOnOpenSegment(this, s))
        iter.remove();
    }
    return points;
  }

  /**
   * возвращает точки пересечения открытого отрезка и прямой
   * отрезок считается открытым
   * @param line
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    if(Checker.isLineAndOpenSegmentIntersect(line, this)){
      points.add(this.intersectWithLine(line));
    }
    return points;
  }


  /**
   * Возвращает единственную точку пересечения отрезков, если она принадлежит их внутренностям
   * @param rib
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExGeom{
    ArrayList<Vect3d> points = new ArrayList<>();
    //Проверяем пересекаются ли отрезки.
    if(!Checker.isSegmentsXSegment(rib, this)){
      return points;
    }
    //Если отрезки лежат на одной прямой, то возвращаем пустой массив, если нет,
    //то ище пересечений прямых, содержащих отрезки
    if(Checker.isTwoRibInOneLine(rib, this)){
      return points;
    } else {
      if(Checker.pointOnOpenSegment(rib, this.intersectWithRib(rib))
        && Checker.pointOnOpenSegment(this, this.intersectWithRib(rib))) {
        points.add(this.intersectWithRib(rib));
      }
      return points;
    }
  }

  /**
   * Возвращает точки пересечения двух отрезков, любые!
   * @param rib
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect3d(Rib3d rib) throws ExGeom {
    ArrayList<Vect3d> points = new ArrayList<>();
    //Проверяем пересекаются ли отрезки.
    if(!Checker.isSegmentsXSegment(rib, this)){
      return points;
    }
    // Проверяем лежат ли отрезки на одной линии
    if(Checker.isTwoRibInOneLine(rib, this)){
      if(Checker.isPointOnSegment(this, rib.a())) {
        Vect3d.addWithoutDuplicates(points, rib.a());
      }
      if(Checker.isPointOnSegment(this, rib.b())) {
        Vect3d.addWithoutDuplicates(points, rib.b());
      }
      if(Checker.isPointOnSegment(rib, this.a())) {
        Vect3d.addWithoutDuplicates(points, this.a());
      }
      if(Checker.isPointOnSegment(rib, this.b())) {
        Vect3d.addWithoutDuplicates(points, this.b());
      }
      return points;
    } else {
      // Лежат на разных линиях
      Vect3d intersectPoint = this.intersectWithRib(rib);
      if(Checker.isPointOnSegment(rib, intersectPoint)
          && Checker.isPointOnSegment(this, intersectPoint)) {
        points.add(this.intersectWithRib(rib));
      }
      return points;
    }
  }

  public ArrayList<Vect3d> intersect(Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = this.intersect(ray.line());
      if (!points.isEmpty()) {
        if(ray.containsPoint(points.get(0))) {
          result.add(points.get(0));
        }
      }
    } catch (ExDegeneration ex) {}
    return result;
  }

  /**
   * Ищет точки пересечения отрезка с границей многоугольника.
   * При этом отрезок считается открытым.
   * @param poly
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Polygon3d poly) throws ExDegeneration {
    ArrayList<Vect3d> points = poly.intersect(this.line());
    Iterator<Vect3d> iter = points.iterator();
    while (iter.hasNext()) {
      Vect3d s = iter.next();
      if(!Checker.pointOnOpenSegment(this, s))
      iter.remove();
    }
    return points;
  }

  /**
   *
   * @param rib
   * @return возвращает точку пересечения отрезков, если она есть
   * отрезок считается замкнутым
   * @throws ExDegeneration
   * @deprecated
  */
  public Vect3d intersectWithRib (Rib3d rib) throws ExGeom {
   if(!Checker.isTwoSegmentsIntersect(rib, this))
     throw new ExGeom("отрезки не пересекаются");
   else return intersectWithLine(rib.line());
  }

  /**
   * Find intersection points of rib and plane.
   * If plane contains rib, throws ExDegeneration.
   * @param plane
   * @return
   * @throws ExDegeneration
   */
  public Vect3d intersectWithPlane(Plane3d plane) throws ExGeom {
    Vect3d point = line().intersectionWithPlane(plane);
    if (!Checker.isPointOnSegment(this, point))
      throw new ExGeom("плоскость и отрезок не пересекаются");
    return point;
  }

  /**
   * @param plane
   * @return projection of rib if it is not orthogonal to plane
   * @throws ExDegeneration
   */
  public Rib3d projectionOnPlane (Plane3d plane) throws ExDegeneration {
      if (Checker.isCollinear ( plane.n(), line().l() ) ) {
          throw new ExDegeneration("отрезок ортогонален плоскости");
      }
       if (plane.containsPoint(_a)&& plane.containsPoint(_b) ) {
          throw new ExDegeneration("отрезок лежит в плоскости");
      }

      Vect3d proj1 = plane.projectionOfPoint(_a);
      Vect3d proj2 = plane.projectionOfPoint(_b);
      return new Rib3d(proj1, proj2);
  }

  /**
   * Находит ближайшую точку отрезка относительной заданной точки.
   * Опускает проекцию на прямую;
   * если точка попала на отрезок - возвращаем её;
   * иначе - ближайшую вершину отрезка.
   * @param v
   * @return
   */
  public Vect3d getClosestPointToPoint(Vect3d v) throws ExDegeneration {
    Vect3d proj = line().projectOfPoint(v);
    if (Checker.isPointOnSegment(this, proj)) {
      return proj;
    } else {
      if (Vect3d.dist(a(), proj) > Vect3d.dist(b(), proj)) {
        return b().duplicate();
      } else {
        return a().duplicate();
      }
    }
  }

  /**
   * Vertices of rib.
   * @return
   */
  public ArrayList<Vect3d> points() {
      ArrayList<Vect3d> points = new ArrayList<>();
      points.add(_a);
      points.add(_b);
      return points;
  }

  public static ArrayList<Rib3d> getRibChain(ArrayList<Vect3d> points) throws ExDegeneration {
    ArrayList<Rib3d> ribs = new ArrayList<>();
    for(int i = 0; i < points.size() - 1; i++)
      ribs.add(new Rib3d(points.get(i), points.get(i+1)));
    return ribs;
  }

  public static ArrayList<Rib3d> getRibClosedChain(ArrayList<Vect3d> points) throws ExDegeneration {
    ArrayList<Rib3d> ribs = new ArrayList<>();
    for(int i = 0; i < points.size() - 1; i++)
      ribs.add(new Rib3d(points.get(i), points.get(i+1)));
    ribs.add(new Rib3d(points.get(points.size()-1), points.get(0)));
    return ribs;
  }

  /**
   * Пересечение двух отрезков, расположенных на одной прямой.
   * @param rib
   * @return Rib3d
   * @throws geom.ExGeom
   */
  public Rib3d intersectByOverlappingRib(Rib3d rib) throws ExGeom {
    if (pointOnOpenSegment(this, rib.a())) {
      if (pointOnOpenSegment(this, rib.b()))
        return rib.duplicate();
      if (pointOnOpenSegment(rib, _a))
        return new Rib3d(_a, rib.a());
      if (pointOnOpenSegment(rib, _b))
        return new Rib3d(rib.a(), _b);
    }
    if (pointOnOpenSegment(this, rib.b())){
      if (pointOnOpenSegment(rib, _a))
        return new Rib3d(_a, rib.b());
      if (pointOnOpenSegment(rib, _b))
        return new Rib3d(rib.b(), _b);
    }
    if (pointOnOpenSegment(rib, _a))
      return duplicate();

    throw new ExGeom("отрезки не пересекаются");
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList <Vect3d> result = new ArrayList<>();
    result.add(_a);
    result.add(_b);
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Rib3d (points);
    } catch (ExDegeneration ex) { }
    return null;
  }

  /**
   * Строит касательный отрезок к окружности в 2д
   * @param point точка лежит на окружности
   * @param ratio отношение части отрезка, отделенной точкой, ко всей длине отрезка (0,1 - конец отрезка совпадает с данной точкой)
   * @param length длина отрезка
   * @param circ
   * @return
   * @throws ExDegeneration
   */
  public static Rib3d tangentRibByPointLengthAndRatio2d (Vect3d point, double ratio, double length, Circle3d circ) throws ExDegeneration {
    Vect3d radius = Vect3d.sub(circ.center(), point);
    radius = radius.rotate(Math.PI/2, new Vect3d (0,0, 1) );

    double x = ratio*length;
    double y = length - x;
    Vect3d a = radius.getNormalized().mul(x);
    a = a.add(point);
    radius = radius.rotate(Math.PI, new Vect3d (0, 0, 1) );
    Vect3d b = radius.getNormalized().mul(y).add(point);

    return new Rib3d (a, b);
  }
  
  public static Rib3d RibByRibAndTwoPnts (Rib3d rib, Vect3d a, Vect3d b) throws ExDegeneration {
    Vect3d c = Vect3d.sub(b, a);
    c = c.mul(1 / (c.norm() / (rib.length())));
    c = c.sum(a);
    Rib3d r = new Rib3d(a, c);
    return r;
  }

  public static Rib3d RibBy2PntsAndLength (Vect3d a, Vect3d b, double Length) throws ExDegeneration {
    Vect3d c = Vect3d.sub(b, a);
    c = c.mul(1 / (c.norm() / Length));
    c = c.sum(a);
    Rib3d r = new Rib3d(a, c);
    return r;
  }
    
  public static Rib3d RibProportionalRibAndTwoPnts (Rib3d rib, Vect3d a, Vect3d b, double coef) throws ExDegeneration {
    Vect3d c = Vect3d.sub(b, a);
    c = c.mul(1 / (c.norm() / (rib.length())));
    c = c.mul(coef);
    c = c.sum(a);
    Rib3d r = new Rib3d(a, c);  
    return r;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.RIB3D;
  }
}