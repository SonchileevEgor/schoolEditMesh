package geom;

import static geom.Checker.isCollinear;
import java.util.ArrayList;
import static geom.Checker.isOrthogonal;
import static geom.Checker.isTwoLinesIntersect;
import static geom.Vect3d.*;
import util.Util;

/**
 * math object line
 * @author
 */
public class Line3d implements i_Geom {
  private Vect3d _pnt;
  private Vect3d _l;

  /**
   * Default line constructor
   * direction vector = (1, 1, 1), point from line = (2, 2, 2)
   */
  public Line3d(){
    _l = new Vect3d(1, 1, 1);
    _pnt = new Vect3d(2, 2, 2);
  }

  /**
   * Constructor of line by point T and direction vector L
   * @param point point from line
   * @param vect direction vector
   * @throws geom.ExDegeneration
   */
  public Line3d(Vect3d point, Vect3d vect) throws ExDegeneration {
    if (vect.norm() < Checker.eps())
      throw new ExDegeneration("направляющий вектор слишком мал");
    _pnt = point; _l = rNormalizedVector(vect, 1);
  }

  /**
   * Constructor of line by rib.
   * @param rib
   */
  public Line3d(Rib3d rib){
    _pnt = rib.a();
    _l = rNormalizedVector(sub(rib.b(), rib.a()), 1);
  }

  public Line3d(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), sub(points.get(1), points.get(0)));
  }

  /**
   * Construct line by two points
   * @param p1 1st point
   * @param p2 2nd point
   * @return Line passing through 2 points
   * @throws ExDegeneration
   */
  public static Line3d line3dByTwoPoints(Vect3d p1, Vect3d p2)
    throws ExDegeneration{
    return new Line3d(p1, rNormalizedVector(sub(p2, p1), 1));
  }

  /**
   * @return point on the line
   */
  public Vect3d pnt(){
    return _pnt.duplicate();
  }

  /**
   * @return direction vector of the line
   */
  public Vect3d l(){
    return _l;
  }

  /**
   * Point of line, differ from main point
   * @return second point of line
   */
  public Vect3d pnt2(){
    return Vect3d.sum(_pnt, _l);
  }

  /**
   * computes distance from point P to the line
   * @param p
   * @return double value
   */
  public double distFromPoint(Vect3d p){
    return sub(projectOfPoint(p), p).norm();
  }

  /**
   * Find projection of line on plane.
   * If they are orthogonal throw exception
   * @param plane
   * @return line in plane
   * @throws ExOrthogonalException
   */
  public Line3d projectOnPlane(Plane3d plane) throws ExOrthogonalException {
    try {
      Vect3d pntProj = plane.projectionOfPoint(_pnt); // Проекция точки прямой
      Vect3d vectProj = plane.projectionOfVect(_l); // Проекция направляющего вектора
      return new Line3d(pntProj, vectProj);
    } catch( ExDegeneration ex ){
      throw new ExOrthogonalException("прямая перпендикулярна плоскости");
    }
  }

  /**
   * Projection of point P on this rib (or its extension).
   * @param p
   * @return Vect3d
   */
  public Vect3d projectOfPoint(Vect3d p){
    if (contains(p))
      return p;
    else{
      Vect3d n = vector_mul(vector_mul(sub(_pnt,p), _l), _l);
      Plane3d plane = new Plane3d(n,_pnt);
      return plane.projectionOfPoint(p);
    }
  }

  /**
   * Find projection of point along vector on line
   * @param p point
   * @param v vector, direction of projection
   * @return point, projection on line
   * @throws ExDegeneration
   */
  public Vect3d projectOfPointAlongVect(Vect3d p, Vect3d v) throws ExDegeneration{
    Vect3d n = vector_mul(vector_mul(v, _l),_l);
    Plane3d plane = new Plane3d(n, _pnt);
    return plane.projectionOfPointAlongVect(p, v);
  }

  /**
   * Check line contains given point
   * @param p
   * @return <strong>true</strong> if point P lies on the line,<br/> otherwise <strong>false</strong>
   */
  public boolean contains(Vect3d p){
    if( Vect3d.equals(p, _pnt) ) {
      return true;
    } else {
      return isCollinear(sub(p, _pnt), _l);
    }
  }

  /**
   * По массиву точек лежащих на прямой возвращаются две крайние
   * @param points массив точек
   * @return возвращает две крайние точки из набора точек, лежащих на прямой
   * @throws ExDegeneration
   */
  public static ArrayList<Vect3d> extremePointsOnLine (ArrayList<Vect3d> points)
      throws ExDegeneration {
    if(!Checker.lieOnOneLine(points)) // проверяем действительно ли эти точки лежат на одной прямой
      throw new ExDegeneration("точки не лежат на одной прямой");

    ArrayList<Vect3d> pointsTemp = new ArrayList<Vect3d>();
    for (int i = 0; i < points.size(); i++)
      pointsTemp.add(points.get(i));

    for (int i = 0; i < pointsTemp.size()-2;)
      //сделующий for берет 3 первых точки массива  и удаляет ту, что лежит между двумя другими
      for(int j=0; j<3; j++){
         Rib3d segment = new Rib3d(pointsTemp.get(j), pointsTemp.get((j+1)%3));
         if(Checker.isPointOnSegment(segment, points.get((j+2)%3))){
          pointsTemp.remove((j+2)%3);
          break;
        }
    }
    return pointsTemp;
  }

  /**
   * Find intersection point of two lines
   * @param line line which intersects the given line
   * @return Vect3d
   * @throws ExDegeneration
   * @deprecated
   */
  public Vect3d intersectionWithLine(Line3d line) throws ExDegeneration {
    if( !isTwoLinesIntersect(this, line) )
      throw new ExDegeneration("прямые не пересекаются");
    
    if( equals(this, line))
      throw new ExDegeneration("прямые совпадают");
      
    // Construct plane which contains one of given lines
    // and is orthogonal to plane where these lines lie.
    // Intersection of lines is intersection of plane and second line.
    Vect3d normal = vector_mul(vector_mul(line.l(), _l), _l);
    Plane3d plane = new Plane3d(normal,_pnt);
    return line.intersectionWithPlane(plane);
  }

  /**
   *
   * Find intersection point of two lines
   * @param line line which intersects the given line
   * @return list of intersection points
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration{
    ArrayList<Vect3d> points = new ArrayList<>();
    points.add(this.intersectionWithLine(line));
    return points;
  }

  /**
   * Find intersection points of this line and ray.
   * @param ray
   * @return
   */
  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> point = intersect(ray.line());
      if (ray.containsPoint(point.get(0))) {
        result.add(point.get(0));
      }
    } catch (ExDegeneration ex) {}
    return result;
  }


  /**
   * @param plane
   * @return point of intersection given plane and line
   * @throws ExDegeneration
   */
  public Vect3d intersectionWithPlane(Plane3d plane) throws ExDegeneration {
    double coef;
    // Check line and plane have intersection
    if (!isOrthogonal(plane.n(), _l)){
      coef = (inner_mul(plane.n(), plane.pnt()) - inner_mul(plane.n(), _pnt)) / inner_mul(plane.n(), _l);
      return sum(_pnt, mul(_l, coef));
    }
    else
      throw new ExDegeneration("прямая и плоскость параллельны");
  }

  /**
   * constructs Line3d by two points on it
   * @param a first point
   * @param b second point
   * @return Line3d
   * @throws geom.ExDegeneration
   */
  public static Line3d getLineByTwoPoints(Vect3d a, Vect3d b)
    throws ExDegeneration {
    return new Line3d(a, Vect3d.sub(b, a));
  }

  /**
   * Check two lines are identical
   * @param l1
   * @param l2
   * @return
   */
  public static boolean equals(Line3d l1, Line3d l2){
    return Checker.pointLieOnTheLine(l2, l1.pnt()) &&
           Checker.pointLieOnTheLine(l2, Vect3d.sum(l1.pnt(),l1.l()));
  }

  /**
   * @param line
   * @param p
   * @return прямая, проходящая через данную точку, параллельно данной прямой
   * @throws geom.ExDegeneration
   */
  public static Line3d lineParallelLine(Line3d line, Vect3d p)
    throws ExDegeneration{
    return new Line3d(p, line.l());
  }

  /**
   * Прямая как пересечение плоскостей
   * @param pl1
   * @param pl2
   * @return
   * @throws ExDegeneration
   */
  public static Line3d lineByTwoPlanes (Plane3d pl1, Plane3d pl2) throws ExDegeneration {
    if (Checker.isPlaneParallelPlane(pl1, pl2) || Plane3d.equals(pl1, pl2))
      throw new ExDegeneration("плоскости не пересекаются");
    return pl1.intersectionWithPlane(pl2);
  }

  /**
   * Подразумевается, что точка не лежит на прямой
   * @param line
   * @param p
   * @return прямая, проходящая через данную точку и перпендикулярная данной прямой
   * @throws ExDegeneration
   */
  public static Line3d linePerpendicularLine(Line3d line, Vect3d p)
    throws ExDegeneration {
    if(line.contains(p))
      throw new ExDegeneration("точка лежит на прямой");
    return new Line3d(p, sub(p, line.projectOfPoint(p)));
 }

  /**
   * Точка может лежать на прямой
   * @param line
   * @param p
   * @return прямая, проходящая через данную точку и перпендикулярная данной прямой в 2d
   * @throws ExDegeneration
   */
  public static Line3d linePerpendicularLine2d(Line3d line, Vect3d p) throws ExDegeneration {
    if(line.contains(p)) {
      Plane3d plane = Plane3d.planeByPointOrthLine(p, line);
      Line3d result = Line3d.lineByTwoPlanes(plane, Plane3d.oXY());
      return result;
    }
    return new Line3d(p, sub(p, line.projectOfPoint(p)));
 }

  /**
   * Возвращается один из возможных вариантов ответа.
   * @param plane
   * @param p
   * @return прямая, проходящая через данную точку и параллельная данной плоскости
   * @throws geom.ExDegeneration
   */
  public static Line3d lineParallelPlane(Plane3d plane, Vect3d p)
    throws ExDegeneration{
   /*Vect3d direct = new Vect3d(plane.n()); //sic!
   direct.rot_x(Math.PI / 2);*/
   Vect3d direct = Vect3d.sub(plane.pnt(), plane.getSecondPoint(1));
   return new Line3d(p, direct);
 }

  /**
   * Возвращается прямая в параллельной плоскости под углом к вектору плоскости.
   * @param plane
   * @param p
   * @param angle
   * @return прямая, проходящая через данную точку и параллельная данной плоскости
   * @throws geom.ExDegeneration
   */
  public static Line3d lineParalPlaneByAngle(Plane3d plane, Vect3d p, double angle)
    throws ExDegeneration{
   if(plane.containsPoint(p))
      throw new ExDegeneration("точка лежит в плоскости");
   Vect3d direct = Vect3d.sub(plane.pnt(), plane.getSecondPoint(1));
   direct = direct.rotate(angle, plane.n());
   return new Line3d(p, direct);
 }

  /**
   * @param plane
   * @param p
   * @return прямая, проходящая через данную точку и перпендикулярную плоскости
   * @throws geom.ExDegeneration
   */
  public static Line3d linePerpendicularPlane(Plane3d plane, Vect3d p)
    throws ExDegeneration{
    return new Line3d(p, plane.n());
  }

  /**
   * Equation of the line.
   * Using LaTeX format.
   * @param precision
   * @return  String
   * @author alexeev
   */
  public String getCanonicalEquation(int precision) {
    StringBuilder result = new StringBuilder();
    result.append("\\dfrac{x");
    result.append((_pnt.x() >= 0) ? " - " : " + ");
    result.append(Util.valueOf(Math.abs(_pnt.x()), precision));
    result.append("}{");
    result.append(Util.valueOf(_l.x(), precision));
    result.append("} = ");
    result.append("\\dfrac{y");
    result.append((_pnt.y() >= 0) ? " - " : " + ");
    result.append(Util.valueOf(Math.abs(_pnt.y()), precision));
    result.append("}{");
    result.append(Util.valueOf(_l.y(), precision));
    result.append("} = ");
    result.append("\\dfrac{z");
    result.append((_pnt.z() >= 0) ? " - " : " + ");
    result.append(Util.valueOf(Math.abs(_pnt.z()), precision));
    result.append("}{");
    result.append(Util.valueOf(_l.z(), precision));
    result.append("}");

    return result.toString();
  }

 @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_pnt);
    result.add(pnt2());
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    Line3d line = null;
    try {
      line = new Line3d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return line;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.LINE3D;
  }
}