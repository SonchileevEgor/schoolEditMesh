package geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 *
 * @author Knyazev Vladislav
 */
public class Polygon3d implements AbstractPolygon, i_Geom, i_OrientableGeom {
  protected ArrayList<Vect3d> _points;

  public Polygon3d(ArrayList<Vect3d> points) throws ExDegeneration {
    create_poly(points);
  }

  public Polygon3d(final Vect3d... args) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>(Arrays.asList(args));
    create_poly(points);
  }

  public Polygon3d(Polygon3d poly) { _points = poly.points(); }

  private void create_poly(ArrayList<Vect3d> points) throws ExDegeneration {
    _points = points;
    if(_points.size() > 2) {
      if(Checker.atLeast3PointsOneline(_points))
        throw new ExDegeneration("точки многоугольника расположены на одной прямой!");
    }
    if(_points.size() > 3){
      if(!Checker.isCoplanar(_points))
        throw new ExDegeneration("вершины многоугольника не лежат в одной плоскости!");
      if(Checker.hasPolygonSelfIntersect(_points))
        throw new ExDegeneration("имеются самопересечения сторон многоугольника!");
      if(!Checker.isConvexPolygon(_points))
        throw new ExDegeneration("многоугольник должен быть выпуклым!");
    }
  }

  /**
   * Construct rectangle by 2 points, width and angle
   * @param a 1st vertex
   * @param b 2nd vertex
   * @param width length other side (not ab)
   * @param angle angle between default plane and plane which contains rectangle
   * @return rectangle (Polygon3d)
   * @throws ExDegeneration
   */
  public static Polygon3d rectangleBy2PntsAngle(Vect3d a, Vect3d b, double width, double angle)
      throws ExGeom {
    Plane3d plane = Plane3d.planeByTwoPoints(a, b, angle);
    Vect3d ba = Vect3d.sub(a, b);
    Vect3d w = Vect3d.rNormalizedVector(ba.rotate(Math.PI / 2, plane.n()), width);
    return new Polygon3d(a, b, Vect3d.sub(b, w), Vect3d.sub(a, w));
  }

  /**
   * Construct regular polygon by 2 points, vertices number and angle
   * @param a 1st vertex
   * @param b 2nd vertex
   * @param vertNum vertices number
   * @param angle angle between default plane and plane which contains regular polygon
   * @return regular polygon
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public static Polygon3d regPolygonByTwoPoints(Vect3d a, Vect3d b, int vertNum, double angle) throws ExGeom {
    Plane3d plane = Plane3d.planeByTwoPoints(a, b, angle);
    return new Polygon3d(Vect3d.getRegularPolygonPoints(a, b, plane, vertNum));
  }


  /**
   * Construct square by 2 points of diagonal (2D)
   * @param a 1st point of diagonal
   * @param c 2nd point of diagonal
   * @return object of Polygon3d class
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public static Polygon3d square2dBy2PntsOfDiag(Vect3d a, Vect3d c) throws ExGeom {
    Rib3d ac = new Rib3d(a,c);
    Vect3d middle = ac.center();
    Vect2d mc = Vect3d.sub(c, middle).projectOnOXY();
    mc.rot(Math.PI / 2);
    Vect3d b = Vect3d.sum(mc.threeD(), middle);
    Polygon3d poly = Polygon3d.regPolygonByTwoPoints(a, b, 4, 0);
    return poly;
  }


  /**
   * Construct parallelogram by 3 points
   * @param a 1st point
   * @param b 2nd point
   * @param c 3d point
   * @return object of Polygon3d
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public static Polygon3d parallelogramBy3Pnts(Vect3d a, Vect3d b, Vect3d c) throws ExGeom {
    Vect3d d = Vect3d.sum(c, Vect3d.sub(a, b));
    return new Polygon3d(a, b, c, d);
  }

  /**
   * Construct trapeze by 3 points and base length.
   * The rib between 1st and 2nd point is 1st base.
   * 3rd point belongs 2nd base.
   * @param a 1st point of 1st base
   * @param b 2nd point of 1st base
   * @param c known point of 2nd base
   * @param baseLength length of 2nd base
   * @return object of Polygon3d
   * @throws ExGeom
   */
  public static Polygon3d trapezeBy3PntsBaseLength(Vect3d a, Vect3d b, Vect3d c, double baseLength) throws ExGeom {
    Vect3d base = Vect3d.sub(a, b);
    Vect3d d = Vect3d.sum(c, Vect3d.rNormalizedVector(base, baseLength));
    return new Polygon3d(a, b, c, d);
  }

   /**
   * Construct isosceles trapeze by 3 points.
   * The rib between 1st and 2nd point is 1st base.
   * 3rd point belongs 2nd base.
   * @param a 1st point of 1st base
   * @param b 2nd point of 1st base
   * @param c known point of 2nd base
   * @return object of Polygon3d
   * @throws ExGeom
   */
  public static Polygon3d isoscelesTrapezeBy3Pnts(Vect3d a, Vect3d b, Vect3d c) throws ExGeom {
    Plane3d p1 = new Plane3d(a, b, c);
    Line3d os = Vect3d.midLinesArePerpendicularToTheSegment(a, b, p1);
    Vect3d d = (Vect3d)SpaceTransformation.objectSymUnderLine(c, os);
    return new Polygon3d(a, b, c, d);
  }
  /**
   * Construct rhombus by 2 point of diagonal and length of other diagonal
   * @param a 1st point of 1st diagonal
   * @param c 2nd point of 1st diagonal
   * @param diagLength length of 2nd diagonal
   * @param angle angle between default plane and plane which contains rhombus
   * @return object of Polygon3d
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public static Polygon3d rhombusBy2PntsDiagLengthAng(Vect3d a, Vect3d c,
          double diagLength, double angle) throws ExGeom {
    Plane3d plane = Plane3d.planeByTwoPoints(a, c, angle);
    Vect3d diagAC = Vect3d.sub(c, a);
    Vect3d center = Vect3d.sum(a, Vect3d.mul(diagAC, 0.5));
    Vect3d directBD = Vect3d.vector_mul(diagAC, plane.n());
    Vect3d b = Vect3d.sum(center, Vect3d.rNormalizedVector(directBD, diagLength / 2));
    Vect3d d = Vect3d.sum(center, Vect3d.rNormalizedVector(directBD, -diagLength / 2));
    return new Polygon3d(a, b, c, d);
  }

  /**
   * Construct rhombus by 2 points of one side and 2 angles
   * @param a 1st point of 1st side
   * @param b 2nd point of 1st side
   * @param angBetweenSides angle between 2 sides
   * @param rotAng angle between default plane and plane which contains rhombus
   * @return object of Polygon3d class
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public static Polygon3d rhombusBy2PntsAngAng(Vect3d a, Vect3d b,
          double angBetweenSides, double rotAng) throws ExGeom {
    Plane3d plane = Plane3d.planeByTwoPoints(a, b, rotAng);
    Vect3d ab = Vect3d.sub(b, a);
    Vect3d ad = ab.rotate(angBetweenSides, plane.n());
    Vect3d d = Vect3d.sum(a, ad);
    return parallelogramBy3Pnts(b, a, d);
  }

  public Polygon3d duplicate() {
    return new Polygon3d(this);
  }

  public ArrayList<Vect3d> points() { return _points; }

  public ArrayList<Rib3d> ribs() throws ExDegeneration{
    ArrayList<Rib3d> ribs = new ArrayList<Rib3d>();
    for(int i = 0; i < _points.size(); i++){
      Rib3d temp = new Rib3d(_points.get(i % _points.size()),_points.get ((i + 1) % _points.size()));
      ribs.add(temp);
    }
    return ribs;
  }

  /**
   * Angle of i-th vertex
   * @param i vertex number
   * @return angle
   * @throws ExDegeneration
   */
  public Angle3d angle(int i) throws ExDegeneration {
    return new Angle3d(_points.get(i % _points.size()),
            _points.get ((i + 1) % _points.size()),
            _points.get ((i + 2) % _points.size()));
  }

  /**
   * returns [index, index + 1] face of the polygon
   * @param index
   * @return Line3d
   * @throws geom.ExDegeneration
   */
  public Line3d getFace(int index) throws ExDegeneration {
    return Line3d.getLineByTwoPoints(_points.get(index),
        _points.get((index == vertNumber() - 1) ? 0 : index + 1));
  }

  /**
   * checks if the polygon is regular
   * @return boolean
   * @throws geom.ExDegeneration
   */
  public boolean isRegular() throws ExDegeneration {
    return Checker.isRegularPolygon(_points);
  }

  /**
   * checks if the polygon is convex
   * @return boolean
   * @throws geom.ExDegeneration
   */
  public boolean isConvex() throws ExDegeneration {
    return Checker.isConvexPolygon(_points);
  }

  public boolean isTriangle(){
    return vertNumber() == 3;
  }

  /**
   * number of vertices of polygon
   * @return int
   */
  public int vertNumber() {
    return _points.size();
  }

  /**
   *
   * @return plane which contains this polygon
   * @throws geom.ExDegeneration
   */
  public Plane3d plane() throws ExDegeneration {
    return new Plane3d(_points.get(0), _points.get(1), _points.get(2));
  }

  /**
   * @author alexeev
   * constructs a circumscribed circle of the polygon if that's possible
   * otherwise throws an exception
   * @return Circle3d
   * @throws ExGeom
   */
  public Circle3d outCircle() throws ExGeom {
    Circle3d result = new Circle3d(_points.get(0), _points.get(1), _points.get(2));
    for (int i = 3; i < vertNumber(); i++) {
      if (Math.abs(result.radius().norm() - Vect3d.dist(result.center(), _points.get(i))) > Checker.eps())
        throw new ExGeom("невозможно описать окружность");
    }
    return result;
  }

  /**
   * @author alexeev
   * constructs an inscribed circle of the polygon if that's possible
   * otherwise throws an exception
   * @return Circle3d
   * @throws ExGeom
   */
  public Circle3d inCircle() throws ExGeom {
    Vect3d center;

    // the first bisector
    Vect3d direction1 = Vect3d.getNormalizedVector(Vect3d.sub(_points.get(2), _points.get(1)));
    direction1.inc_add(Vect3d.getNormalizedVector(Vect3d.sub(_points.get(0), _points.get(1))));
    Line3d bisector1 = new Line3d(_points.get(1), direction1);

    // the second bisector
    Vect3d direction2 = Vect3d.getNormalizedVector(Vect3d.sub(_points.get((vertNumber() > 3) ? 3 : 0), _points.get(2)));
    direction2.inc_add(Vect3d.getNormalizedVector(Vect3d.sub(_points.get(1), _points.get(2))));
    Line3d bisector2 = new Line3d(_points.get(2), direction2);

    // found center of incircle
    try {
      center = bisector1.intersectionWithLine(bisector2);
    } catch (ExDegeneration ex) {
      throw new ExGeom("невозможно вписать окружность");
    }

    // found radius
    double radius = getFace(0).distFromPoint(center);

    // check that center is equidistant from faces of polygon
    for(int i = 1; i < vertNumber(); i++) {
      if( Math.abs(getFace(i).distFromPoint(center) - radius) > Checker.eps() ) {
        throw new ExGeom("невозможно вписать окружность");
      }
    }

    return new Circle3d(radius, plane().n(), center);
  }

  /**
   *
   * @param line
   * @return возвращает точки пересечения сторон многоугольника с данной прямой
   * (подразумевается, что прямая и многоугольник лежат в одной плоскости).
   * Если таких точек нет, то возвращается пустой список
   * Если точка пересечения совпадает с одной из вершин, то она тоже возвращается
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersectBoundaryWithLine (Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    for( Vect3d p: _points ){
      if( line.contains(p) )
        points.add(p);
    }
    for( Rib3d r: this.ribs() ){
      Vect3d.addWithoutDuplicates(points, r.intersect(line));
    }
    return points;
  }

  /**
   * Ищет пересечение границы многоугольника с прямой.
   * Возвращаются точки не являющиеся вершинами.
   * @param line
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect (Line3d line) throws ExDegeneration{
    ArrayList<Vect3d> points = new ArrayList<>();
    for (Rib3d rib : ribs()){
      if (Checker.isLineAndOpenSegmentIntersect(line, rib))
        points.add(rib.intersect(line).get(0));
    }
    return points;
  }

  /**
   *
   * @param ray
   * @return
   */
  public ArrayList<Vect3d> intersect (Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExDegeneration ex) {}
    return result;
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
      return Checker.isPointOnClosePolygon(this, result) ? result : null;
    } catch( ExDegeneration ex ){
      return null;
    }
  }

  /**
   * Find intersection points of two polygons.
   * @param that
   * @return List of intersection points.
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect3d(Polygon3d that) throws ExGeom {
    ArrayList<Vect3d> result = new ArrayList<>();
    // Проверка равенства поверхностей
    //!! TODO: это действительно нужно? Здесь проверяется равенство объектов.
    if (this.equals(that)) {
      result.addAll(that._points);
      return result;
    }
    Plane3d thisPlane = this.plane();
    Plane3d thatPlane = that.plane();
    if(thisPlane.isIntersectsPlane(thatPlane)) {
      if(Plane3d.equals(thisPlane, thatPlane)) {
        // Лежат в одной плоскости
        for(Rib3d thisRib : this.ribs())
          for(Rib3d thatRib : that.ribs())
            try {
              ArrayList<Vect3d> point = thisRib.intersect3d(thatRib);
              Vect3d.addWithoutDuplicates(result, point);
            }
            catch (ExDegeneration e){}
      }
      // Лежат в разных плоскостях
      else {
        Line3d line = thisPlane.intersectionWithPlane(thatPlane);
        ArrayList<Vect3d> temp = new ArrayList<>();
        Vect3d.addWithoutDuplicates(temp, this.intersect3d(line));
        Vect3d.addWithoutDuplicates(temp, that.intersect3d(line));
        for( int i = 0; i < temp.size(); i++ ){
          Vect3d point = temp.get(i);
          if( Checker.isPointOnClosePolygon(this, point) &&
              Checker.isPointOnClosePolygon(that, point) ){
            result.add(point);
          }
        }
      }
    } else {
      throw new ExDegeneration("границы многоугольников не пересекаются");
    }

    if (result.isEmpty()) {
      throw new ExDegeneration("границы многоугольников не пересекаются");
    }

    //!! TODO: Зачем кидать исключение в случае непересекающихся многоугольников?
    // Это не является вырождением!

    return result;
  }

  public ArrayList<Vect3d> intersect( Polygon3d poly ) throws ExDegeneration {
      ArrayList<Vect3d> points = new ArrayList<Vect3d>();
      for (Rib3d rib : ribs()){
        Vect3d.addWithoutDuplicates(points, rib.intersect(poly));
      }
      return  points;
   }

  /**
  * Возвращает точку пересечения многоугольника с прямой.
  * Пересечение с границей многоугольника тоже учитывается.
  * @param line линия с которой ищим пересечение
  * @return массив точек
  * @throws ExDegeneration
  */
  public ArrayList<Vect3d> intersect3d( Line3d line ) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    if( Checker.isLineParallelPlane(line, this.plane()) ){
      return points;
    } else if( Checker.isLineLiesInPlane(line, this.plane()) ){
     points = this.intersectBoundaryWithLine(line);
     return points;
    } else {
      Vect3d p = line.intersectionWithPlane(this.plane());
      if( Checker.isPointOnClosePolygon(this, p) ){
        points.add(p);
        return points;
      }
    }
    return points;
  }

  /**
   * Возвращает точку пересечения многоугольника с прямой.
   * Считается, что прямая и многоугольник не лежат в одной плоскости.
   * В случае когда прямая лежит в плоскости многоугольника или параллельна ей
   * возвращается пустой список
   * Пересечение с границей многоугольника тоже учитывается.
   * @param line
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersectWithLine (Line3d line) throws ExDegeneration{
    ArrayList<Vect3d> points = new ArrayList<>();
    if (Checker.isLineParallelPlane(line, this.plane()) ||
        Checker.isLineLiesInPlane(line, this.plane())) {
      return points;
    } else {
      Vect3d p = line.intersectionWithPlane(this.plane());
      if (Checker.isPointOnClosePolygon(this, p)){
        points.add(p);
      }
    }
    return points;
  }

  private ArrayList<Triang3d> getTriangularDivisionOfConvexPolygon() {
    ArrayList<Triang3d> triangles = new ArrayList<>();
    try {
      if (isConvex()) {
        for (int i = 1; i < _points.size()-1; i++) {
          try {
            triangles.add(new Triang3d(_points.get(0), _points.get(i), _points.get(i+1)));
          }
          catch (ExDegeneration ex) { }
        }
      }
    } catch (ExDegeneration e) { }
    return triangles;
  }

  /**
   * Area of polygon.
   * @return
   */
  public double area() {
    try {
      if (isConvex()) {
        ArrayList<Triang3d> polygon_triangles = getTriangularDivisionOfConvexPolygon();
        double area = 0.0;
        for (int i = 0; i < polygon_triangles.size(); i++)
            area += polygon_triangles.get(i).area();
        return area;
      }
    } catch (ExDegeneration ex) { }
    return 0.0;
  }

  /**
   * Perimeter of polygon.
   * @return
   * @author alexeev
   */
  public double perimeter() {
    try {
      double result = 0;
      for (Rib3d side : sides()) {
        result += side.length();
      }
      return result;
    } catch (ExDegeneration ex) {
      return 0.0;
    }
  }

  public ArrayList<Polygon3d> faces() throws ExGeom {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      ArrayList<Vect3d> face = new ArrayList<>();
      for(int i = 0; i < _points.size(); i++)
          face.add(points().get(i));
      Vect3d a = new Vect3d(_points.get(0));
      Vect3d b = new Vect3d(_points.get(1));
      Vect3d c = new Vect3d(_points.get(_points.size()-1));
      Orientation bypass = Orientation.getBodyOrientation(new Vect3d(), a, b, c);
      if (bypass == Orientation.LEFT)
          Collections.reverse(face);
      faces.add(new Polygon3d(face));
      return faces;
  }

  public ArrayList<Rib3d> sides() throws ExDegeneration {
    ArrayList<Rib3d> ribs = new ArrayList<>();
    int vertAmount = vertNumber();
    for (int i = 0; i < vertAmount; i++) {
      ribs.add(new Rib3d(_points.get(i), _points.get((i + 1) % vertAmount)));
    }
    return ribs;
  }

  /**
   * Находит ближайшую точку многоугольника относительной заданной точки.
   * Предполагается, что точка и многоугольник лежат в одной плоскости.
   * Если точка лежит внутри многоугольника, возвращаем её;
   * Если нет, находим ближайшее ребро;
   * возвращаем ближайшую точку к этому ребру.
   * @param v
   * @return Vect3d
   * @throws geom.ExDegeneration
   */
  public Vect3d getClosestPointToPointInPlane(Vect3d v) throws ExDegeneration {
    if (Checker.isPointOnClosePolygon(this, v)) {
      return v.duplicate();
    } else {
      Vect3d nearestPoint = new Vect3d();
      double dist = Double.MAX_VALUE;
      for (Rib3d rib : sides()) {
        Vect3d newNearestPoint = rib.getClosestPointToPoint(v);
        double newDist = Vect3d.dist(newNearestPoint, v);
        if (newDist < dist) {
          dist = newDist;
          nearestPoint = newNearestPoint;
        }
      }
      return nearestPoint;
    }
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon() {
    ArrayList<Vect3d> polygon_points = points();
    return polygon_points;
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon(int numPoints) {
    return getAsAbstractPolygon();
  }

  public static Polygon3d getFaceInRightOrientation(Polygon3d poly) {
    ArrayList<Vect3d> poly_points = poly.points();
    //!! TODO: константы нужно вынести в статические поля с комментарием, что они означают.
    //!! Посмотрите в конфигах, возможно, есть константы со схожим смыслом.
    Vect3d o = new Vect3d(200000.0, 200000.0, 200000.0);
    Vect3d a = new Vect3d(poly_points.get(0));
    Vect3d b = new Vect3d(poly_points.get(1));
    Vect3d c = new Vect3d(poly_points.get(2));
    Orientation bypass = Orientation.getBodyOrientation(o, a, b, c);
    if (bypass == Orientation.LEFT) {
      Collections.reverse(poly_points);
      try {
        return new Polygon3d(poly_points);
      } catch (ExGeom ex) {}
    }
    return poly.duplicate();
  }

  public static boolean isFacesEquals(Polygon3d face1, Polygon3d face2) {
    ArrayList<Vect3d> face_points1 = face1.points();
    ArrayList<Vect3d> face_points2 = face2.points();
    int size = face_points1.size();
    if (size != face_points2.size())
      return false;
    else {
      int i=0;
      //!! TODO: Здесь по смыслу подходит цикл с условием.
      for (i=0; i<size; i++)
        if (!Vect3d.equals(face_points1.get(i), face_points2.get(i)))
          break;
      if (i == size)
        return true;
      else {
        Collections.reverse(face_points2);
        for (i=0; i<size; i++)
          if (!Vect3d.equals(face_points1.get(i), face_points2.get(i)))
            return false;
      }
    }
    return true;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return new ArrayList<Vect3d>(_points);
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Polygon3d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
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

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.POLYGON3D;
  }
}