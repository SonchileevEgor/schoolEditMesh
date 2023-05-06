package opengl;

import geom.*;

import java.util.ArrayList;

/**
 * Плоскость для рисования.
 * <br>
 * <br>Плоскость - бесконечна, но при рисовании удобно изображать только ее часть.
 * Эта часть формируется по контрольным точкам, которые она обязательно должна содержать внутри себя.
 * <br><i>I believe that it may be writing more simple.</i>
 */
public class SmartPlane {

  private ArrayList<Vect3d> points;
  private Plane3d plane3d;
  /** First of two most distant points. */
  private Vect3d a = new Vect3d();
  /** Second of two most distant points. */
  private Vect3d b = new Vect3d();
  /** Distance between two most distant points. */
  private double maxDistance = 0;
  /**Indents from segment AB in right side */
  private double maxRightDistance = 0;
  /**Indents from segment AB in left side */
  private double maxLeftDistance = 0;

  private boolean isCorrect = false;

  /** Vector of longest side of resulting rectangle. */
  private Vect3d longSideVect;
  /** Vector to right half-plane. */
  private Vect3d rightSide;
  /** Normalized {@link #longSideVect} */
  private Vect3d frontVect;

  /**
   * The same as the {@link #SmartPlane(ArrayList, Plane3d, boolean)} with using predictable direction.
   * @param points Control points that must be contained in the result part of the plane.
   *               (Must be at least 2 points)
   * @param plane3d Plane on which generates SmartPlane.
   */
  public SmartPlane(ArrayList<Vect3d> points, Plane3d plane3d) {
    this(points, plane3d, true);
  }

  /**
   * Generate SmartPlane on control points.
   *
   * @param points Control points that must be contained in the result part of the plane.
   *               (Must be at least 2 points)
   * @param plane3d Plane on which generates SmartPlane.
   * @param usePredictableDirection Use fixed direction for construct smart plane
   *                                (more predictable direction of result plane)
   */
  public SmartPlane(ArrayList<Vect3d> points, Plane3d plane3d, boolean usePredictableDirection) {
    this.points = points;
    this.plane3d = plane3d;
    if (usePredictableDirection)
      makeSmartPlaneManualDirecton(new Vect3d(1, 0, 0), new Vect3d(0, 0, 1));
    else
      makeSmartPlaneAutoDirection();
  }

  /**
   * Generate smart plane (use most distant point as main direction)
   */
  private void makeSmartPlaneAutoDirection(){
    /**
     * Смысл алгоритма:
     * 1) Находим пересечения заданной плоскости со всеми объектами на сцене
     * (получаем набор точек, находящиеся на этой плоскости)
     * 2) Находим среди этих точек 2 самые удаленные друг от друга
     * 3) Эти две точки образуют отрезок, параллельно которому будут располагаться длинные стороны прямоугольника
     * (отрисованной части плоскости)
     * 4) Находим точки, которые находятся на максимальном расстоянии от этой прямой (в каждой полуплоскости отдельно)
     * 5) Формируем прямоугольник на основе отрезка (из п.3) и 2х расстояний из п.4
     * 6) Расширяем получившийся прямоугольник и рисуем его.
     */
    findTwoMostDistantPoints();
    isCorrect = checkOnCorrect();
    if (isCorrect) {
      calcVectors();
      findLeftRightDistances();
    }
  }

  /**
   * Generate smart plane (use setted vectors for calculating of main direction)
   * @param basicVector First vector for set predictable direction
   * @param additionalVector Second vector for set predictable direction
   */
  private void makeSmartPlaneManualDirecton(Vect3d basicVector, Vect3d additionalVector){
    Vect3d directionVector1 = plane3d.projectionOfVect(basicVector);
    Vect3d directionVector2 = plane3d.projectionOfVect(additionalVector);
    Vect3d directionVector = Vect3d.sum(
        directionVector1.mul(directionVector1.norm()),
        directionVector2.mul(directionVector2.norm())
    );
    Vect3d initPoint;
    if (points.isEmpty())
      initPoint = plane3d.pnt();
    else
      initPoint = points.get(0);
    Line3d directionLine;
    try {
      directionLine = new Line3d(initPoint, directionVector);
    }
    catch (ExDegeneration ex){
      isCorrect = false;
      return;
    }
    findTwoMostDistantPoints(directionLine);
    isCorrect = checkOnCorrect();
    if (isCorrect){
      calcVectors();
      findLeftRightDistances();
    }
  }

  /**
   * Construct smart plane with predictable direction
   * @param points Control points that must be contained in the result part of the plane.
   *               (Must be at least 2 points)
   * @param plane3d Plane on which generates SmartPlane.
   * @param basicVector First vector for set predictable direction
   * @param additionalVector Second vector for set predictable direction
   */
  public SmartPlane(ArrayList<Vect3d> points, Plane3d plane3d, Vect3d basicVector, Vect3d additionalVector){
    this.points = points;
    this.plane3d = plane3d;
    makeSmartPlaneManualDirecton(basicVector, additionalVector);
  }

  /**
   * Calculates {@link #longSideVect}, {@link #rightSide} and {@link #frontVect}
   */
  private void calcVectors(){
    longSideVect = Vect3d.sub(b, a);
    rightSide = Vect3d.getNormalizedVector(Vect3d.vector_mul(longSideVect, plane3d.n()));
    frontVect = Vect3d.getNormalizedVector(longSideVect);
  }

  /**
   * Find the two points located at the largest distance from each other
   * and writes results to
   * {@link #maxDistance},
   * {@link #a} and
   * {@link #b}
   */
  private void findTwoMostDistantPoints(){
    maxDistance = findTwoMostDistantPoints(points, a, b);
  }

  /**
   * Find the two points located at the largest distance from each other on line
   */
  private void findTwoMostDistantPoints(Line3d line3d){
    maxDistance = makeTwoMostDistantPointsByDirectionLine(points, a, b, line3d);
  }

  /**
   * Делает проекцию всех точек на линию и находит среди них самые отдаленные.
   * @param points Vector of points for searching
   * @param a Point 1 for writing of result.
   * @param b Point 2 for writing of result.
   * @param directionLine Line on which the projections are made
   * @return Distance from a and b.
   */
  static public double makeTwoMostDistantPointsByDirectionLine(
          ArrayList<Vect3d> points, Vect3d a, Vect3d b, Line3d directionLine) {
    ArrayList<Vect3d> pointsProjection = new ArrayList<>();
    for (Vect3d p : points){
      pointsProjection.add(directionLine.projectOfPoint(p));
    }
    return findTwoMostDistantPoints(pointsProjection, a, b);
  }

  /**
   * Find two most distance point. Result writes on 'a' and 'b' variables.
   * @param points Vector of points for searching
   * @param a Point 1 for writing of result.
   * @param b Point 2 for writing of result.
   * @return Distance from a and b.
   */
  static public double findTwoMostDistantPoints(ArrayList<Vect3d> points, Vect3d a, Vect3d b){
    if (points.size() < 2) {
      return 0;
    }
    double maxDistance = 0;
    int curInd = 0;
    int newInd = 0;
    int savedInd = 0;
    boolean newPointWasFound = true;

    while (newPointWasFound) {
      curInd = newInd;
      newPointWasFound = false;
      for (int i = 0; i < points.size(); i++) {
        double distance = Vect3d.dist(points.get(i), points.get(curInd));
        if (distance > maxDistance) {
          maxDistance = distance;
          newInd = i;
          savedInd = curInd;
          newPointWasFound = true;
        }
      }
    }
    a.setAs(points.get(savedInd));
    b.setAs(points.get(newInd));
    return maxDistance;
  }

  /**
   * Find indents from segment AB in right end left sides
   * and writes results to
   * {@link #maxLeftDistance} and {@link #maxRightDistance}
   */
  private void findLeftRightDistances(){
    Line3d line3d;
    try {
      line3d = new Line3d(a, longSideVect);
    }
    catch (ExDegeneration ex){
      System.out.println("WTF? You are a wizard?");
      return;
    }
    for (int i = 0; i < points.size(); i++) {
      if (points.get(i) != a && points.get(i) != b){
        Vect3d side = Vect3d.sub(points.get(i), a);
        double dist = line3d.distFromPoint(points.get(i));
        if (Vect3d.getAngle(side, rightSide) < Math.PI/2 ){
          if (maxRightDistance < dist)
            maxRightDistance = dist;
        }
        else{
          if (maxLeftDistance < dist)
            maxLeftDistance = dist;
        }
      }
    }
  }
  private boolean checkOnCorrect(){
    return !(maxDistance < Checker.eps());
  }

  public boolean isCorrect() {
    return isCorrect;
  }

  /**
   * Get plane part as rectangle polygon.
   *
   * @param k Minimal distance from control points to edges of plane part.
   * @return Plane part as rectangle polygon.
   */
  public ArrayList<Vect3d> getAsRectangle(double k){
    // Generate points for drawing rectangle.
    ArrayList<Vect3d> planePoints = new ArrayList<>();
    planePoints.add(Vect3d.sum(Vect3d.sub(a, Vect3d.mul(frontVect, k)), Vect3d.mul(rightSide, maxRightDistance + k)));
    planePoints.add(Vect3d.sum(Vect3d.sum(b, Vect3d.mul(frontVect, k)), Vect3d.mul(rightSide, maxRightDistance + k)));
    planePoints.add(Vect3d.sub(Vect3d.sum(b, Vect3d.mul(frontVect, k)), Vect3d.mul(rightSide, maxLeftDistance + k)));
    planePoints.add(Vect3d.sub(Vect3d.sub(a, Vect3d.mul(frontVect, k)), Vect3d.mul(rightSide, maxLeftDistance + k)));
    return planePoints;
  }

  /**
   * Get plane part as rectangle polygon with 2 smoothed edges.
   *
   * @param k Minimal distance from control points to edges of plane part.
   * @return Plane part as rectangle polygon with 2 smoothed edges.
   */
  public ArrayList<Vect3d> getAsSmoothRectangle(double k){
    int smoothness = 20;
    ArrayList<Vect3d> rect = getAsRectangle(k);
    ArrayList<Vect3d> res = new ArrayList<>();
    res.add(rect.get(0));
    res.addAll(getBezierPoints(smoothness, rect.get(1), rect.get(2), frontVect, k));
    res.add(rect.get(2));
    res.addAll(getBezierPoints(smoothness, rect.get(3), rect.get(0), Vect3d.mul(frontVect, -1), k));
    return res;
  }

  /**
   * Get plane part as rectangle polygon with 4 smoothed edges.
   *
   * @param k Minimal distance from control points to edges of plane part.
   * @return Plane part as rectangle polygon with 4 smoothed edges.
   */
  public ArrayList<Vect3d> getAsSmoothRectangleExt(double k){
    int smoothness = 20;
    ArrayList<Vect3d> rect = getAsRectangle(k);
    ArrayList<Vect3d> res = new ArrayList<>();
    res.addAll(getBezierPoints(smoothness, rect.get(0), rect.get(1), rightSide, k));
    res.addAll(getBezierPoints(smoothness, rect.get(1), rect.get(2), frontVect, k));
    res.addAll(getBezierPoints(smoothness, rect.get(2), rect.get(3), Vect3d.mul(rightSide, -1), k));
    res.addAll(getBezierPoints(smoothness, rect.get(3), rect.get(0), Vect3d.mul(frontVect, -1), k));
    return res;
  }

  /**
   * Get curve from point pStart(include) to pEnd(not include).
   * @param numPoints Num points of curve.
   * @param pStart Start point of curve.
   * @param pEnd End point of curve.
   * @param indent Normalized vector, on which curve can be bent from line pStart-pEnd.
   * @param k Size of indent of curve from line pStart-pEnd.
   * @return Curve as array of points.
   */
  private ArrayList<Vect3d> getBezierPoints(int numPoints, Vect3d pStart, Vect3d pEnd, Vect3d indent, double k){
    ArrayList<Vect3d> resList = new ArrayList<>();
    for (double t = 0; t < 1; t += 1.0/numPoints){
      resList.add(
              calculateBezierFunction(
                      pStart,
                      Vect3d.sum(Vect3d.conv_hull(pStart, pEnd, 1.0 / 3), Vect3d.mul(indent, k)),
                      Vect3d.sub(Vect3d.conv_hull(pStart, pEnd, 2.0 / 3), Vect3d.mul(indent, k)),
                      pEnd,
                      t
              )
      );
    }
    return resList;
  }

  /**
   * Calculate a cubic Bezier curve
   * @param p1 First point
   * @param p2 Second point
   * @param p3 Third point
   * @param p4 Fourth point
   * @param t Point of curve. t = [0, 1]
   * @return Point of cubic Bezier curve.
   */
  private Vect3d calculateBezierFunction(Vect3d p1, Vect3d p2, Vect3d p3, Vect3d p4, double t) {
    return
        Vect3d.sum(
            Vect3d.sum(
                Vect3d.sum(
                    Vect3d.mul(p1, ( Math.pow(-t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1 )),
                    Vect3d.mul(p2, 3 * t * (Math.pow(t, 2) - 2 * t + 1))),
                Vect3d.mul(p3, 3 * Math.pow(t, 2) * (1 - t))),
            Vect3d.mul(p4, Math.pow(t, 3)));
  }
}
