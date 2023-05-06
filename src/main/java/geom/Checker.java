package geom;

import static geom.Line3d.getLineByTwoPoints;
import static geom.Vect3d.O;
import static geom.Vect3d.dist;
import static geom.Vect3d.getNormalizedVector;
import static geom.Vect3d.inner_mul;
import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sub;
import static geom.Vect3d.sum;
import static geom.Vect3d.vector_mul;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Checker {
  /**
   * Check if two numbers are the same with an &epsilon;-error
   * @param a Number one
   * @param b Number two
   * @return true if same
   */
  public static boolean isEqual(double a, double b){
    return Math.abs(a - b) <= _eps;
  }

  /**
   * Проверяет два вектора на ортогональность.
   * @param v1
   * @param v2
   * @return
   */
  public static boolean isOrthogonal(Vect3d v1, Vect3d v2){
    return Math.abs(inner_mul(v1, v2)) < _eps;
  }

  /**
   * Проверяет равенство норм двух векторов.
   * @param v1
   * @param v2
   * @return
   */
  public static boolean isEqualNorm(Vect3d v1, Vect3d v2){
    return Math.abs(v1.norm() - v2.norm()) < _eps;
  }

  /**
   * Проверка, лежат ли три точки на одной прямой.
   * Если есть совпадающие точки, возвращаем true.
   * @param t1 первая точка
   * @param t2 вторая точка
   * @param t3 третья точка
   * @return
   */
  public static boolean threePointsOnTheLine(Vect3d t1, Vect3d t2, Vect3d t3) {
    try {
      Rib3d rib1 = new Rib3d(t2, t3);
      Rib3d rib2 = new Rib3d(t1, t3);
      Rib3d rib3 = new Rib3d(t1, t2);

      return isPointOnSegment(rib1, t1) ||
             isPointOnSegment(rib2, t2) ||
             isPointOnSegment(rib3, t3);
    } catch (ExDegeneration ex) {
      // Две точки совпали - возвращаем true.
      return true;
    }
  }

  /**
   * Метод проверяет лежат ли точки, содержащиеся в массиве на одной прямой.
   * Если точек 1 или 2 или есть повторяющиеся(но не более 2-х типов), то возвращается true
   * @param pts массив точек
   * @return
   */
  public static boolean lieOnOneLine(ArrayList<Vect3d> pts) throws ExDegeneration {
    ArrayList<Vect3d> points = Vect3d.withoutDuplicates(pts);
    if (points.size() < 3)
      return true;
    Line3d line = Line3d.line3dByTwoPoints(points.get(0), points.get(1));
    for(Vect3d p : points)
      if(!line.contains(p))
        return false;
    return true;
  }
  /**
   * Метод проверяет лежат ли 4 точки на одной прямой.
   * Если точек 1 или 2 или есть повторяющиеся(но не более 2-х типов), то возвращается true
   * @return
   */
  public static boolean fourPointsOnOneLine(Vect3d p1, Vect3d p2, Vect3d p3, Vect3d p4) throws ExDegeneration {
    ArrayList<Vect3d> pts = new ArrayList<Vect3d>();
    pts.add(p1);
    pts.add(p2);
    pts.add(p3);
    pts.add(p4);
    return lieOnOneLine(pts);
  }

  /**
   * Проверяем лежат ли какие-то 3 точки из массива на одной прямой.
   * Подразумевается, что в массиве нет повторяющихся точек.
   * @param pts
   * @return true, если в массиве есть три точки лежащие на одной прямой.
   */
  public static boolean atLeast3PointsOneline(ArrayList<Vect3d> pts){
    for(Vect3d p1: pts)
      for(Vect3d p2: pts)
        for(Vect3d p3: pts)
          if(!Vect3d.equals(p1, p2) && !Vect3d.equals(p1, p3) && !Vect3d.equals(p2, p3)&&
              threePointsOnTheLine(p1,p2,p3))
            return true;
    return false;
  }

  /**
   * Проверка, лежат ли точки в одной плоскости.
   * @param pts
   * @return
   * @throws ExDegeneration 
   */
  public static boolean isCoplanar(ArrayList<Vect3d> pts) throws ExDegeneration {
    boolean inOnePlane = true;

    Vect3d t0 = pts.get(0); //выбираем из всех точек три, не лежащие на одной прямой
    Vect3d t1 = pts.get(1);
    Vect3d t2 = new Vect3d();
    for(int i=2; i< pts.size();i++) {
      t2 = pts.get(i);
      ArrayList<Vect3d> p = new ArrayList<Vect3d>();
      p.add(t0);
      p.add(t1);
      p.add(t2);
      if (!lieOnOneLine(p)) break;
    }

    for (int i=3; i<pts.size();i++){
      Vect3d t = pts.get(i);
      double det = ((t.x() - t0.x())*(t1.y()-t0.y())*(t2.z()-t0.z())+
              ((t1.x()-t0.x())*(t2.y()-t0.y())*(t.z()-t0.z()))+
              ((t.y()-t0.y())*(t1.z()-t0.z())*(t2.x()-t0.x()))-
              ((t2.x()- t0.x())*(t1.y()-t0.y())*(t.z()-t0.z()))-
              ((t.x()-t0.x())*(t2.y()-t0.y())*(t1.z()-t0.z()))-
              ((t1.x()-t0.x())*(t.y()-t0.y())*(t2.z()-t0.z())) );

      if(Math.abs(det-0)>_eps){inOnePlane=false; break;} // проверяемая точка не лежит на плоскости
    }
    return inOnePlane;
  }

  public static boolean isCodirectional(Vect3d a, Vect3d b) throws ExDegeneration{
    if(a.norm()<_eps || b.norm()< _eps)
      throw new ExDegeneration("один из вектров нулевой");
    Vect3d aNorm = Vect3d.getNormalizedVector(a);
    Vect3d bNorm = Vect3d.getNormalizedVector(b);
    return Vect3d.dist(aNorm, bNorm)                 < _eps; // сонаправлены
  }

  public static boolean isCollinear(Vect3d a, Vect3d b){
    Vect3d aNorm = Vect3d.getNormalizedVector(a);
    Vect3d bNorm = Vect3d.getNormalizedVector(b);
    return Vect3d.dist(aNorm, bNorm)                 < _eps || // сонаправлены
           Vect3d.dist(Vect3d.mul(aNorm, -1), bNorm) < _eps;   // противоположно направлены
  }

  /**
   * Метод проверяет пересекаются ли два отрезка.
   * Не обрабатывается случай, когда отрезки лежат на одной прямой.
   * @param rib1 Первый отрезок
   * @param rib2 Второй отрезок
   * @return
   * @throws ExDegeneration
   */
  public static boolean isTwoSegmentsIntersect(Rib3d rib1, Rib3d rib2) throws ExDegeneration {
    /*
      Проверяем пересекаются ли прямые, содержащие отрезок.
      Если да, то ищем точку пересечения прямых, содержащих данные отрезки,
      а потом проверяем принадлежит ли данная точка отрезкам.
    */
    Line3d l1 = getLineByTwoPoints(rib1.a(), rib1.b());
    Line3d l2 = getLineByTwoPoints(rib2.a(), rib2.b());
    if(!isTwoLinesIntersect(l1, l2))
      return false;
    Vect3d tochka = l1.intersectionWithLine(l2);
    return isPointOnSegment(rib1, tochka) && isPointOnSegment(rib2, tochka);
  }

  /**
   * Проверяет пересекаются, накладываются или имеют общую верщину отрезки.
   * Случаи, когда отрезки лежат на одной прямой учтены.
   * @param rib1
   * @param rib2
   * @return true, если отрезки имют хотя бы одну общую точку.
   * @throws ExDegeneration
   */
  public static boolean isSegmentsXSegment(Rib3d rib1, Rib3d rib2) throws ExDegeneration{
    /*
      Случай, когда отрезки не лежат на одной прямой.
      Проверяем пересекаются ли прямые, содержащие отрезок.
      Если да, то ищем точку пересечения прямых, содержащих данные отрезки,
      а потом проверяем принадлежит ли данная точка отрезкам.
    */
    Line3d l1 = getLineByTwoPoints(rib1.a(), rib1.b());
    Line3d l2 = getLineByTwoPoints(rib2.a(), rib2.b());
    if(isTwoLinesIntersect(l1, l2) && !Line3d.equals(l1, l2)){
       Vect3d tochka = l1.intersectionWithLine(l2);
       return isPointOnSegment(rib1, tochka) && isPointOnSegment(rib2, tochka);
    }
    return isTwoRibInOneLine(rib1, rib2) &&
            (isPointOnSegment(rib1, rib1.a()) ||
            isPointOnSegment(rib1, rib1.b())  ||
            isPointOnSegment(rib2, rib2.a())  ||
            isPointOnSegment(rib2, rib2.b()));
  }



  /**
   * Проверяем лежит ли первый отрезок внутри второго.
   * Допускается совпадение одной из вершин, но не допускается совпадение отрезков.
   * @param rib1
   * @param rib2
   * @return
   */
  public static boolean segmentInSegment (Rib3d rib1, Rib3d rib2){
    return ( pointOnOpenSegment(rib2, rib1.a() ) && ( Vect3d.equals(rib2.a(), rib1.b()) || Vect3d.equals(rib2.b(), rib1.b()) )) ||
           ( pointOnOpenSegment(rib2, rib1.b() ) && ( Vect3d.equals(rib2.a(), rib1.a()) || Vect3d.equals(rib2.b(), rib1.a()) ));
  }

  /**
   * Проверяет лежит ли один открытый отрезок строго внутри другого открытого отрезка
   * @param rib1
   * @param rib2
   * @return
   */
  public static boolean openSegmentInOpenSegment (Rib3d rib1, Rib3d rib2){
    return pointOnOpenSegment(rib2, rib1.a()) && pointOnOpenSegment(rib2, rib1.b());
  }

  /**
   * Проверяет пересекаются ли внутренности отрезка.
   * Включение не допускается.
   * @param rib1
   * @param rib2
   * @return
   */
  public static boolean twoOpenSegmentsOverlap (Rib3d rib1, Rib3d rib2){
    //Проверяем лежат ли вершины кадого из отрезка на другом отрезке. Например, если есть два отрезка AB и CD, то проверяем лежит ли A на CD и С на AB.(всего нужно проверить 4 различные комбинации)

    return ( pointOnOpenSegment(rib1, rib2.a()) && pointOnOpenSegment(rib2, rib1.a()) ) || ( pointOnOpenSegment(rib1, rib2.b()) && pointOnOpenSegment(rib2, rib1.a()) )  ||
           ( pointOnOpenSegment(rib1, rib2.a()) && pointOnOpenSegment(rib2, rib1.b()) ) || ( pointOnOpenSegment(rib1, rib2.b()) && pointOnOpenSegment(rib2, rib1.b()) );
   }

  /**
   * Метод проверяет пересекаются ли внутренности двух отрезков.
   * @param rib1 первый отрезок
   * @param rib2 второй отрезок
   * @return
   * @throws ExDegeneration
   */
  public static boolean whetherTwoOpenSegmentsIntersect(Rib3d rib1, Rib3d rib2) throws ExDegeneration {
    // Проверяем не пересекаются ли два отрезка,
    //а потом смотрим, чтобы концы не сопадали.
    return isTwoSegmentsIntersect(rib1, rib2) &&
            !Vect3d.equals(rib1.a(), rib2.a()) &&
            !Vect3d.equals(rib1.b(), rib2.a()) &&
            !Vect3d.equals(rib1.a(), rib2.b()) &&
            !Vect3d.equals(rib1.b(), rib2.b());
  }

  /**
   * На вход подается массив точек, соединяемых ребрами в порядке перечисления(по кругу).
   * Метод проверяет не пересекаются ли получившиеся отрезки.
   * @param pts Массив точек
   * @return
   */
  public static boolean doArrayDuplicateVectors(ArrayList<Vect3d> pts) {
    for (int i = 0; i < pts.size(); i++){
      for (int j = i + 1; j < pts.size(); j++)
        if (pts.get(i).equals(pts.get(j)))
          return true;
    }
    return false;
  }

  /**
   * Проверяет пересекается ли набор ребер,
   * построенный последовательным соединением по кругу указанных вершин.
   * Данная проверка производится при построении выпуклого многоугольника.
   * !!Не проверяются вырожденные случаи, когда 3 точки оказываются на одной прямой.
   * @param points Набор точек.
   * @return true, если самопересечения имеются, false иначе
   * @throws ExDegeneration
   */
  public static boolean hasPolygonSelfIntersect(ArrayList<Vect3d> points)
          throws ExDegeneration {
    ArrayList<Rib3d> ribs = Rib3d.getRibClosedChain(points);
    return haveSelfIntersect(ribs);
  }

  /**
   * Проверяет пересекается ли цепочка ребер (НЕЗАМКНУТАЯ),
   * полученная последовательным соединением точек
   * !!Не проверяются вырожденные случаи, когда 3 точки оказываются на одной прямой.
   * @param points набор точек
   * @return true, если самопересечения имеются, false иначе
   * @throws ExDegeneration
   */
  public static boolean hasChainSelfIntersect(ArrayList<Vect3d> points) throws ExDegeneration {
    ArrayList<Rib3d> ribs = Rib3d.getRibChain(points);
    return haveSelfIntersect(ribs);
  }

  // Проверяет пересекается ли набор ребер
  private static boolean haveSelfIntersect(ArrayList<Rib3d> arrayOfRib) throws ExDegeneration {
    for (int i = 0; i < arrayOfRib.size(); i++) {
      for (int j = i + 1; j < arrayOfRib.size(); j++) {
        if (whetherTwoOpenSegmentsIntersect(arrayOfRib.get(i), arrayOfRib.get(j)))
          return true;
      }
    }
    return false;
  }

  /**
   * Функция проверяет лежит ли точка c на отрезке rib.
   * Подразумевается, что точка может совпадать с одним из концов отрезка.
   * @param rib отрезок
   * @param c точка
   * @return
   */
  public static boolean isPointOnSegment(Rib3d rib, Vect3d c){
    /**
     * Пусть мы хотим проверить лежит ли точка C  на отрезке AB,
     * тогда досточно проверить справедливость равенства AC + CB = AB.
     */
    return Math.abs(sub(rib.a(),c).norm() + sub(rib.b(),c).norm() -
            sub(rib.a(),rib.b()).norm()) < _eps;
  }

  /**
   * Проверяется лежит ли точка на отрезке rib, подразумевается,
   * что она лежит строго внутри отрезка
   * @param rib отрезкок
   * @param c точка
   * @return
   */
  public static boolean pointOnOpenSegment(Rib3d rib, Vect3d c){
    /**
     * Пусть мы хотим проверить лежит ли точка C  на отрезке AB.
     * Тогда досточно проверить справедливость равенства AC+CB=AB и то,
     * что она не совпадает не с одним из концов.
     */
    return isPointOnSegment(rib, c) && !Vect3d.equals(rib.a(), c) && !Vect3d.equals(rib.b(), c);
  }

  /**
   * Проверка принадлежности точки списком.
   * 
   * @param points
   * @param p
   * @return
   * @deprecated
   *  В классе Vect3d переопределён метод equals.
   *  Используйте выражение points.contains(p)
   */
  public static boolean isArrayContainPoint(ArrayList<Vect3d> points, Vect3d p){
    return points.contains(p);
  }

  /**
   * Check four points lie in one plane
   * @param t1
   * @param t2
   * @param t3
   * @param t4
   * @return true if four points lie in one plane and false otherwise
   */
  public static boolean isFourPointsInOnePlane(Vect3d t1, Vect3d t2, Vect3d t3, Vect3d t4){
    // Вычитаем из одной точки 3 другие и считаем смешанное произведение трех векторов.
    Vect3d v1 = Vect3d.sub(t1, t2);
    Vect3d v2 = Vect3d.sub(t1, t3);
    Vect3d v3 = Vect3d.sub(t1, t4);
    return Math.abs(Vect3d.tripleProd(v1, v2, v3)) < _eps;
  }

  public static boolean isTwoRibInOneLine(Rib3d r1, Rib3d r2) throws ExDegeneration{
    return (pointLieOnTheLine(r1.line(),r2.a()) && pointLieOnTheLine(r1.line(),r2.b()));
  }

  /**
   * Check two points equal
   * @param t1
   * @param t2
   * @return true if this points are equal and false otherwise
   */
  public static boolean isTwoPointsEqual(Vect3d t1, Vect3d t2){
    Vect3d v1 = Vect3d.sub(t1, t2);
    return v1.norm() < _eps;
  }

  /**
   * Проверяет лежит ли точка на прямой.
   * @param line
   * @param pt
   * @return True, если точка лежит на прямой.
   */
  public static boolean pointLieOnTheLine(Line3d line, Vect3d pt){
    return line.distFromPoint(pt) < _eps;
  }

  /**
   * Проверяем параллельны ли две прямые, но при этом не совпадают.
   * @param line1
   * @param line2
   * @return
   */
  public static boolean isTwoLinesParallel(Line3d line1, Line3d line2){
    return isCollinear(line1.l(), line2.l()) && !pointLieOnTheLine(line1, line2.pnt());
  }

  /**
   * Проверяем пересекаются ли две прямые.
   * При этом они могут совпадать.
   * @param line1
   * @param line2
   * @return true if lines have intersection and false otherwise
   */
  public static boolean isTwoLinesIntersect(Line3d line1, Line3d line2){
    // Для проверки выбираем 4 точки на этих прямых и проверяем лежат ли они в одной плоскости.
    // Значит и прямые лежат в одной плоскости. Осталось проверить не параллельны ли они.
    return isFourPointsInOnePlane(line1.pnt(), line1.pnt2(),
                                  line2.pnt(), line2.pnt2()) &&
                                  !isTwoLinesParallel(line1, line2);
  }

  /**
   * Проверям пересекается ли прямая с замкнутым отрезком.
   * Если отрезок лежит на прямой, то считается, что они не пересекаются.
   * @param line
   * @param segment
   * @return
   * @throws ExDegeneration
   */
  public static boolean isLineAndSegmentIntersect(Line3d line, Rib3d segment) throws ExDegeneration{
    if (!isTwoLinesIntersect(segment.line(), line))
      return false;
    Vect3d point = line.intersectionWithLine(segment.line());
    return isPointOnSegment(segment, point);
  }

  /**
   * Проверям пересекается ли прямая с открытым отрезком.
   * Если отрезок лежит на прямой, то считается, что они не пересекаются.
   * @param line
   * @param segment
   * @return
   * @throws ExDegeneration
   */
  public static boolean isLineAndOpenSegmentIntersect(Line3d line, Rib3d segment)
          throws ExDegeneration {
    if(!isTwoLinesIntersect(segment.line(), line) || Line3d.equals(line, segment.line()))
      return false;
    Vect3d point = line.intersect(segment.line()).get(0);
    return  pointOnOpenSegment(segment, point);
  }

  public static boolean isSegmentOnLine(Line3d line, Rib3d segment){
    return pointLieOnTheLine(line, segment.a()) && pointLieOnTheLine(line, segment.b());
  }

  /**
   * Сначала мы проверяем, является ли точка вершиной.
   * Если нет, то проводим прямую через данную точку и одну из вершин.
   * Строим пересечение получившейся прямой и многоугольника.
   * Если точка пересечения одна, точка многоугольнику не принадлежит.
   * Если две, то проверяем принадлежит ли данная точка отрезку,
   * образованному этими двумя точками.
   * @param poly
   * @param point
   * @return
   * @throws ExDegeneration
   */
  public static boolean isPointOnClosePolygon(Polygon3d poly, Vect3d point) throws ExDegeneration {
    if( !poly.plane().containsPoint(point) )
      return false;
    if( poly.points().contains(point) )
      return true;
    else{
      Line3d line = Line3d.line3dByTwoPoints(point, poly.points().get(0));
      ArrayList<Vect3d> intersect = poly.intersectBoundaryWithLine(line);
      if(intersect.size() == 1)
        return false;
      else
        return(isPointOnSegment(new Rib3d(intersect.get(0), intersect.get(1)), point));
    }
  }

  /**
   * Проверяем лежит ли прямая в плоскости
   * @param line
   * @param plane
   * @return true, если прямая лежит в плоскости
   */
  public static boolean isLineLiesInPlane(Line3d line, Plane3d plane){
    return (plane.containsPoint(line.pnt()) && plane.containsPoint(line.pnt2()));
  }

  /**
   * Проверяем параллельна ли прямая и плоскость, принадлежность прямой плоскости не допускается
   * @param line
   * @param plane
   * @return true, если прямая и плоскость параллельны
   */
  public static boolean isLineParallelPlane(Line3d line, Plane3d plane){
    return (!plane.containsPoint(line.pnt()) && Math.abs( Vect3d.inner_mul(plane.n(),line.l()))<_eps);
  }

  /**
  * Проверяем лежат ли в одной плоскости окружность и точка
  * @param circle
  * @param point
  * @return true, если окружность и точка лежат в одной плоскости
  */
  public static boolean isCircleAndPointLieinOnePlane(Circle3d circle, Vect3d point){
    Plane3d plane = circle.plane();
    return plane.containsPoint(point);
  }

  /**
   * Проверяем лежит ли точка на окружности.
   * @param point
   * @param circ
   * @return true, если точка лежит на окружности
   */
  public static boolean isPointOnCircle(Vect3d point, Circle3d circ){
    return Math.abs(Vect3d.dist(point, circ.center())-circ.radiusLength())< _eps && isPlaneContainPoint(circ.plane(),point);
  }

  /**
   * Проверяет лежит ли точка внутри окружности
   * @param point
   * @param circ
   * @return
   */
  public static boolean isPointInCircle(Vect3d point, Circle3d circ){
    return Vect3d.dist(point, circ.center())-circ.radiusLength() < _eps;
  }

  /**
   * Проверяет лежит ли точка на круге
   * @param point
   * @param circ
   * @return
   */
  public static boolean isPointOnDisk(Vect3d point, Circle3d circ){
    return isPointOnCircle(point, circ) || isPointInCircle(point, circ);
  }

  /**
   * Проверяем лежат ли две окружности в одной плоскости.
   * @param circ1
   * @param circ2
   * @return true, если окружности лежат в одной плоскости
   */
  public static boolean isTwoCirclesLieInOnePlane(Circle3d circ1, Circle3d circ2){
    return Plane3d.equals(circ1.plane(), circ2.plane());
  }

  public static boolean isOneCircleInSecondCircle(Circle3d circ1, Circle3d circ2){
    return isTwoCirclesLieInOnePlane(circ1, circ2) && (Circle3d.equals(circ1, circ2)
            || Vect3d.sub(circ1.center(), circ2.center()).norm() < circ2.radiusLength());
  }

  public static boolean isTwoCirclesTouchExternally(Circle3d circ1, Circle3d circ2){
    return isTwoCirclesLieInOnePlane(circ1, circ2) && Math.abs(Vect3d.norm(Vect3d.sub(circ1.center(),
            circ2.center())) - circ1.radiusLength() - circ2.radiusLength())<_eps;
  }

  public static boolean isFirstCircleTouchSecondCircleInternally(Circle3d circ1, Circle3d circ2){
    return isTwoCirclesLieInOnePlane(circ1, circ2) && Math.abs(Vect3d.norm(Vect3d.sub(circ1.center(),
            circ2.center())) + circ1.radiusLength() - circ2.radiusLength())<_eps;
  }

 /**
  * Проверяем, пересекаются ли две окружности, лежащие в одной плоскости, в двух точках.
  * @param circ1
  * @param circ2
  * @return true, если две окружности лежащие в одной плоскости пересекаются по двум точкам.
  */
  public static boolean isTwoCircleHaveTwoPointIntersect(Circle3d circ1, Circle3d circ2){
    return (circ1.radius().norm()+circ2.radius().norm()) > Vect3d.dist(circ1.center(), circ2.center());
  }

  public static boolean isPointInSphere(Vect3d point, Sphere3d sphere) {
    return Vect3d.dist(point, sphere.center()) <= sphere.radius() + _eps;
  }

  public static boolean isPointOnSphere(Vect3d point, Sphere3d sphere) {
    Vect3d dist = new Vect3d(point.x()-sphere.center().x(), point.y()-sphere.center().y(), point.z()-sphere.center().z());
    return Math.abs(dist.mag() - sphere.radius()*sphere.radius()) <= _eps;
  }
  
  /**
   * point belongs paraboloid
   * @param point point of the paraboloid
   * @param paraboloid
   * @return 
   */
  public static boolean isPointOnEllipticParaboloid(Vect3d point, EllipticParaboloid3d paraboloid) {
    Vect3d pointNew = paraboloid.newSystemOfCoorParaboloid().newCoor(point);
    Vect3d f = paraboloid.newSystemOfCoorParaboloid().newCoor(paraboloid.f());
    return Math.abs(pointNew.x() * pointNew.x() + pointNew.y() * pointNew.y() - 4*f.x() * pointNew.z()) <= _eps;
  }

  /**
   * point belongs ellipsoid
   * @param point point of ellipsoid
   * @param ellipsoid
   * @return 
   */
  public static boolean isPointOnEllipsoid(Vect3d point, Ellipsoid3d ellipsoid) {
    Vect3d pointNew = ellipsoid.newSystemOfCoorEllipsoid().newCoor(point);
    return Math.abs((pointNew.x() * pointNew.x()) / (ellipsoid.bigAxle() * ellipsoid.bigAxle())
            + (pointNew.y() * pointNew.y()) / (ellipsoid.smallAxle() * ellipsoid.smallAxle())
            + (pointNew.z() * pointNew.z()) / (ellipsoid.smallAxle() * ellipsoid.smallAxle()) - 1) <= _eps;
  }
  
  /**
   * point belongs hyperboloidOfOneSheet
   *
   * @param point point of hyperboloidOfOneSheet
   * @param hyperboloidOfOneSheet
   * @return
   */
  public static boolean isPointOnHyperboloidOfOneSheet(Vect3d point, HyperboloidOfOneSheet3d hyperboloidOfOneSheet) {
    Vect3d pointNew = hyperboloidOfOneSheet.newSystemOfCoorHyperboloid().newCoor(point);
    return Math.abs((pointNew.x() * pointNew.x()) / (hyperboloidOfOneSheet.realAxle() * hyperboloidOfOneSheet.realAxle())
            - (pointNew.y() * pointNew.y()) / (hyperboloidOfOneSheet.b() * hyperboloidOfOneSheet.b())
            + (pointNew.z() * pointNew.z()) / (hyperboloidOfOneSheet.realAxle() * hyperboloidOfOneSheet.realAxle()) - 1) <= _eps;
  }

  /**
   * point belongs hyperboloidOfTwoSheet
   *
   * @param point point of hyperboloidOfTwoSheet
   * @param hyperboloidOfTwoSheet
   * @return
   */
  public static boolean isPointOnHyperboloidOfTwoSheet(Vect3d point, HyperboloidOfTwoSheet3d hyperboloidOfTwoSheet) {
    Vect3d pointNew = hyperboloidOfTwoSheet.newSystemOfCoorHyperboloid().newCoor(point);
    return Math.abs((pointNew.x() * pointNew.x()) / (hyperboloidOfTwoSheet.realAxle() * hyperboloidOfTwoSheet.realAxle())
            - (pointNew.y() * pointNew.y()) / (hyperboloidOfTwoSheet.b() * hyperboloidOfTwoSheet.b())
            - (pointNew.z() * pointNew.z()) / (hyperboloidOfTwoSheet.b() * hyperboloidOfTwoSheet.b()) - 1) <= _eps;
  }
  
  public static boolean isTriangleInSphere(Triang3d triangle, Sphere3d sphere) {
    return isPointInSphere(triangle.a(), sphere) &&
           isPointInSphere(triangle.b(), sphere) &&
           isPointInSphere(triangle.c(), sphere);
  }

  /**
   * Check point in scene cube
   * @param point
   * @param scene_cube
   * @return
   */
  public static boolean isPointInSceneCube(Vect3d point, Cube3d scene_cube) {
    return (point.x() <= scene_cube.D2().x()+_eps && point.y() <= scene_cube.D2().y()+_eps && point.z() <= scene_cube.D2().z()+_eps) &&
           (point.x() >= scene_cube.A1().x()-_eps && point.y() >= scene_cube.A1().y()-_eps && point.z() >= scene_cube.A1().z()-_eps);
  }

  /**
   * Check rib in scene cube
   * @param rib
   * @param cube
   * @return
   */
  public static boolean isRibInCube(Rib3d rib, Cube3d cube) {
    return isPointInSceneCube(rib.a(), cube) &&
           isPointInSceneCube(rib.b(), cube);
  }

  /**
   * Check 1st circle contains 2nd circle
   * Just for circles which lie in one plane!
   * @param c1 1st circle
   * @param c2 2nd circle
   * @return true if 1st circle contains 2nd circle and false otherwise
   */
  public static boolean isCircleContainCircle(Circle3d c1, Circle3d c2){
    double R = c1.radiusLength();
    double r = c2.radiusLength();
    double dist = Vect3d.dist(c1.center(), c2.center());
    return r - _eps <= R - dist;
  }

  public static boolean isPlaneContainPoint(Plane3d plane, Vect3d point) {
    return plane.containsPoint(point);
  }

  public static boolean isPlaneContainPoints(Plane3d plane, ArrayList<Vect3d> points) {
    for (int i = 0; i < points.size(); i++) {
        if (!plane.containsPoint(points.get(i)))
            return false;
    }
    return true;
  }

  /**
   * @author rita
   * Check given _ORDERED_PLANAR_ set of points is convex.
   * Supposition: polygon on this vertices _HAVE_NO_EDGE_INTERSECTION_.
   * @param points list of points which lie in one plane
   * @return true if set of points is convex and false otherwise
   */
  public static boolean isConvexPolygon(ArrayList<Vect3d> points) throws ExDegeneration {
    int n = points.size();// number of given points
    if (n < 3) {
      throw new RuntimeException("Convex polygon can't have less than 3 points!");
    }
    if (!isCoplanar(points)) {
      throw new ExDegeneration("Vertices of polygon must lie in one plane!");
    }
    if (hasPolygonSelfIntersect(points))
      return false;
    // We choose control (non-zero) orientation for 3 successive points.
    // Then check for every 3 successive points that orientation is the same.
    // If it isn't polygon isn't convex, return false.
    // If all orientations are the same or some orientation is zero polygon is convex, return zero.
    Vect3d controlVectMul = new Vect3d(0,0,0);// Control orientation (vector multiplication)
    Vect3d v1, v2, vectMul;// 2 vectors for current 3 points and vector multiplication of this points
    for (int i = 0; i < n; i++){
      v1 = sub(points.get((i + 1) % n), points.get(i % n));
      v2 = sub(points.get((i + 2) % n), points.get((i + 1) % n));
      vectMul = getNormalizedVector(vector_mul(v1, v2));
      // First non-zero vector multiplication take as control orientation.
      if (Vect3d.equals(controlVectMul, O) && !Vect3d.equals(vectMul, O)){
        controlVectMul = vectMul;
      // If some non-zero orientation differ from control orientation polygon is not convex.
      } else if (!Vect3d.equals(controlVectMul, O) &&
                 !Vect3d.equals(vectMul, O) &&
                 !Vect3d.equals(vectMul, controlVectMul))
        return false;
    }
    if (Vect3d.equals(controlVectMul, O))
      throw new ExDegeneration("All vertices of polygon lie on one line!");
    return true;
  }

  /**
   * @author rita
   * Check if given points are vertices of regular polygon
   * @param points list of given points, number of points more then 2
   * @return true if points are vertices of regular polygon and false otherwise
   * @throws geom.ExDegeneration
   */
  public static boolean isRegularPolygon(ArrayList<Vect3d> points)
          throws ExDegeneration {
    int n = points.size();// number of given points
    if (n < 3) {
      // Вставляю AssertionError,
      // поскольку данная ошибка может возникнуть только по вине разработчика.
      throw new AssertionError("Regular polygon can't have less than 3 points!");
    }
    // We construct regular n-gon with side equals distance between zero and first given points
    // and check every given point equals the next vertex of constructed polygon.
    // If all vertices are the same then given points are vertices of regular polygon and function returns true
    // else returns false.
    Vect3d side = sub(points.get(1), points.get(0)); // side of regular polygon
    double angle = 2*Math.PI/n;// angle of regular n-polygon
    // If 3 first given points lie on one line (check triangle inequality) then return false
    // else construct normal for plane of polygon as vector multiplication
    if(dist(points.get(0),points.get(1)) + dist(points.get(1),points.get(2)) - dist(points.get(0),points.get(1)) < _eps)
      return false;
    Vect3d normal = vector_mul(side, sub(points.get(2), points.get(1)));// normal for plane of constructed polygon
    Vect3d vertex;// vertex of regular polygon
    int i = 1;// index
    while(i < n-1){
      side = side.rotate(angle, normal);
      vertex = sum(points.get(i), side);
      if(!Vect3d.equals(points.get(i+1), vertex))
        return false;
      i++;
    }
    return true;
  }

  /**
   * Проверяем пересекаются ли луч и прямая
   * Если они накладываются, то считаем, что они не пересекаются
   * Вершина луча точкой пересечения не считается
   * @param ray
   * @param line
   * @return
   * @throws geom.ExDegeneration
   */
  public static boolean isRayAndLineIntersect(Ray3d ray, Line3d line)
          throws ExDegeneration {
    if(isTwoLinesParallel(line, ray.line()) || Line3d.equals(line, ray.line()))
      return false;
    Vect3d point = line.intersect(ray.line()).get(0);
    return ray.containsPoint(point) && !Vect3d.equals(ray.pnt(),point);
  }

  /**
   * Check array of points for containing equal points.
   * @param points array for checking
   * @return
   */
  public static boolean constainsEqualPoints(ArrayList<Vect3d> points) {
    if (!points.isEmpty()) {
      for (int i = 0; i < points.size() - 1; i++) {
        for (int j = i + 1; j < points.size(); j++) {
          if (Vect3d.equals(points.get(i), points.get(j)))
            return true;
        }
      }
    }
    return false;
  }

  /**
   * Проверяет параллельны ли плоскости
   * Совпадать им запрещается
   * @param plane1
   * @param plane2
   * @return
   */
  public static boolean isPlaneParallelPlane(Plane3d plane1, Plane3d plane2){
    return isCollinear(plane1.n(), plane2.n()) && !Plane3d.equals(plane1, plane2);
  }

  /**
   * Check three vectors are right base
   * @return true if right and false if left
   */
  public static boolean isThreeVectorsRight(Vect3d a, Vect3d b, Vect3d c) {
    /* Проверяет, является ли тройка векторов правой
    Для первых двух векторов строим векторное произведение.
    Если оно сонаправленно с третьим вектором, значит, исходная тройка правая.
    Если противоположнонаправленно, то тройка левая */
    Vect3d normalizedC = rNormalizedVector(c, 1);
    Vect3d normalizedRightC = rNormalizedVector(vector_mul(a, b), 1);
    return Vect3d.equals(normalizedC, normalizedRightC);
  }

  /**
     * Метод проверки пересечения ребром rib списка рёбер triangulation
     * допускается касание (конец одного лежит на другом или есть общая вершина)
     * не допускается совпадение или ситуация, когда один лежит на другом
     * @param triangulation - список рёбер, с которыми rib не должно пересекаться (касаться может)
     * @param rib - проверяемое ребро
     * @return true - rib пересекает ребро из набора triangulation (хотя бы 1 ребро)
     *         false - не пересекает
     */
    public static boolean checkIntersectRib(List<Rib3d> triangulation, Rib3d rib) {
        for (Rib3d current : triangulation)
            try {
                if (current.equals(rib)) return true;
                ArrayList<Vect3d> intersect3d = current.intersect3d(rib);
                if (!intersect3d.isEmpty()) {
                    if (intersect3d.size() > 1) return true;
                    if (intersect3d.get(0).equals(rib.a()) || intersect3d.get(0).equals(rib.b())
                            && intersect3d.get(0).equals(current.a()) || intersect3d.get(0).equals(current.b())) {
                    } else return true;
                }
            } catch (ExGeom ex) {
                Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }
        return false;
    }
  
  /**
   * Проверяем, можно ли описать окружность вокруг многоугольника
   * @param poly
   * @return
   */
  public static boolean isCircleOutPolygon(Polygon3d poly) throws ExDegeneration {
    Circle3d result = new Circle3d(poly.points().get(0), poly.points().get(1), poly.points().get(2));
    for (int i = 3; i < poly.vertNumber(); i++) {
      if (Math.abs(result.radius().norm() - Vect3d.dist(result.center(), poly.points().get(i))) > _eps)
        return false;
    }
    return true;
  }

  /**
   * Проверяем, выполняются ли неравенства треугольника для трёх расстояний.
   */
  public static boolean holdTriangleInequality(double a, double b, double c){
    return (a <= b + c + _eps) && (b <= a + c + _eps) && (c <= a + b + _eps);
  }

  /**
   * Точность вычислений, установленная конфигурацией редактора.
   * @return
   */
  public static double eps(){
    return _eps;
  }

  /**
   * Установить значение epsilon.
   * Это делается при инициализации конфига.
   * @param eps
   */
  public static void setEps(double eps){
    _eps = eps;
  }

  private static double _eps = 1e-5d;
}
