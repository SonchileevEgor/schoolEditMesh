package geom;

import static geom.Angle3d.arccos;
import static geom.Angle3d.arcsin;
import static geom.Checker.eps;
import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sub;
import static geom.Vect3d.sum;
import static geom.Vect3d.vector_mul;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import java.util.ArrayList;

/**
 * Математический объект эллипс
 * @author rita
 */
public class Ellipse3d {
  private double _a, _b;// ellipse params
  private NewSystemOfCoor _planeSysOfCoor;// Система координат на секущей плоскости, в которой эллипс описывается каноническим уравнением
  private ArrayList<Vect3d> _points; // Опорные точки
  private Vect3d _centerOfCurve;

  public Ellipse3d(Cone3d cone, Plane3d plane, ArrayList<Vect3d> points) throws ExDegeneration, ExZeroDivision {
    /*
     Выберем плоскость w перпендикулярную секущей плоскости. Спроецируем все на эту плоскость.
     Проекция конуса на эту плоскость - равнобедренный треугольник T,
     его боковые стороны - generatrix1, generatrix2 - образующими конуса в плоскости w.
     Проекция секущей плоскости - прямая l.
     */
    Vect3d l = vector_mul(plane.n(), cone.h());
    // Вершины при основании треугольника T (нужны ждя построения образующих в плоскости w):
    Vect3d pInBottom1 = rNormalizedVector(new Vect3d(plane.n().x(), plane.n().y(), 0), cone.r());
    Vect3d pInBottom2 = rNormalizedVector(new Vect3d(plane.n().x(), plane.n().y(), 0), -cone.r());
    // Образующие в плоскости w:
    Line3d generatrix1 = new Line3d(cone.v(), sub(pInBottom1, cone.v()));
    Line3d generatrix2 = new Line3d(cone.v(), sub(pInBottom2, cone.v()));
    // Вершины эллипса на оси Ox (находятся как пересечение образующих generatrix1, generatrix2 с прямой l):
    init(generatrix1, generatrix2, plane, l, points);
    double r = (cone.h().norm() - _centerOfCurve.z()) * cone.r() / cone.h().norm();//радиус конуса на высоте центра эллипса
    _b = Math.sqrt(r * r - _centerOfCurve.x() * _centerOfCurve.x() - _centerOfCurve.y() * _centerOfCurve.y());
  }

  public Ellipse3d(Cylinder3d cylinder, Plane3d plane, ArrayList<Vect3d> points) throws ExDegeneration, ExZeroDivision {
    /*
     Выберем плоскость w перпендикулярную секущей плоскости. Проецируем все на эту плоскость.
     Проекция цилиндра на эту плоскость - прямоугольник П,
     его боковые стороны - generatrix1, generatrix2 - образующими цилиндра в плоскости w.
     Проекция секущей плоскости - прямая l.
     */
    Vect3d l = vector_mul(plane.n(), cylinder.h());
    // Вершины прямоугольника П:
    Vect3d rectVert11 = rNormalizedVector(new Vect3d(plane.n().x(), plane.n().y(), 0), cylinder.r());
    Vect3d rectVert12 = rNormalizedVector(new Vect3d(plane.n().x(), plane.n().y(), 0), -cylinder.r());
    Vect3d rectVert21 = sum(rectVert11, new Vect3d(0, 0, cylinder.h().norm()));
    Vect3d rectVert22 = sum(rectVert12, new Vect3d(0, 0, cylinder.h().norm()));
    // Образующие в плоскости w:
    Line3d generatrix1 = new Line3d(rectVert11, sub(rectVert11, rectVert21));
    Line3d generatrix2 = new Line3d(rectVert12, sub(rectVert12, rectVert22));
    // Вершины эллипса на оси Ox (находятся как пересечение образующих generatrix1, generatrix2 с прямой l):
    init(generatrix1, generatrix2, plane, l, points);
    _b = Math.sqrt(cylinder.r() * cylinder.r() - _centerOfCurve.x() * _centerOfCurve.x() - _centerOfCurve.y() * _centerOfCurve.y());
  }

  public double a(){ return _a; }
  public double b(){ return _b; }

  public Vect3d getPoint(double p) throws ExDegeneration {
    if(_points.isEmpty()) {
      return _planeSysOfCoor.oldCoor(new Vect3d(_a * Math.cos(2 * Math.PI * p), _b * Math.sin(2 * Math.PI * p), 0));
    }
    if(_points.size() == 2){
      return getPointOfTruncatedEllipse(p);
    }
    if(_points.size() == 4){
      // Кривая состоит из двух частей. Значениями р от 0 до 0.5 параметризуется один кусок, от 0.5 до 1 - другой.
      if(p < 0.5)// p in [0, 0.5)
        return getPointOfDubleTruncatedEllipse(2 * p, 0, 2);
      else// p in [0.5, 1]
        return getPointOfDubleTruncatedEllipse(2 * (p - 0.5), 1, 3);
      }
    return null;
  }

  private void init(Line3d generatrix1, Line3d generatrix2, Plane3d plane, Vect3d l, ArrayList<Vect3d> points)
          throws ExDegeneration {
    Vect3d xCurveVert1 = generatrix1.intersectionWithPlane(plane);
    Vect3d xCurveVert2 = generatrix2.intersectionWithPlane(plane);
    _a = sub(xCurveVert1, xCurveVert2).norm() / 2;
    _centerOfCurve = new Vect3d((xCurveVert1.x() + xCurveVert2.x()) / 2, (xCurveVert1.y() + xCurveVert2.y()) / 2, (xCurveVert1.z() + xCurveVert2.z()) / 2);// Центр эллипса
    Vect3d yCurveVertPlus = sum(l, _centerOfCurve);// Вектор направления оси Oy в системе координат planeSysOfCoor
    if(xCurveVert1.z() < xCurveVert2.z()){
      _planeSysOfCoor = new NewSystemOfCoor(_centerOfCurve, xCurveVert2, yCurveVertPlus, sum(_centerOfCurve, vector_mul(sub(xCurveVert2, _centerOfCurve), l)));
    } else {
      _planeSysOfCoor = new NewSystemOfCoor(_centerOfCurve, xCurveVert1, yCurveVertPlus, sum(_centerOfCurve, vector_mul(sub(xCurveVert1, _centerOfCurve), l)));
    }
    _points = points;
  }

  // Для цилиндра и конуса
  // Параметризация куска эллипса, высеченного ОДНИМ основанием
  // p из [0, 1]
  private Vect3d getPointOfTruncatedEllipse(double p) throws ExDegeneration {
    double a, p0, p1;
    double basePointY0 = _planeSysOfCoor.newCoor(_points.get(0)).y();
    double basePointY1 = _planeSysOfCoor.newCoor(_points.get(1)).y();
    if(_points.get(0).z() <= eps()){ // points from bottom base
      a = _a;
      if(_planeSysOfCoor.newCoor(_points.get(0)).x() > eps()){
        p0 = arcsin(basePointY0 / _b);
        p1 = arcsin(basePointY1 / _b);
      }else{
        p0 = PI - arcsin(basePointY0 / _b);
        p1 = -p0;
      }
    }else{// points from up base
      a = -_a;
      if(_planeSysOfCoor.newCoor(_points.get(0)).x() > eps()){
        p0 = PI - arcsin(basePointY0 / _b);
        p1 = -p0;
      }else{
        p0 = arcsin(basePointY0 / _b);
        p1 = arcsin(basePointY1 / _b);
      }
    }
    return _planeSysOfCoor.oldCoor(new Vect3d( a * Math.cos( p * p0 + (1 - p) * p1), _b * Math.sin( p * p0 + (1 - p) * p1), 0));
  }

  // Только для цилиндра!
  // Параметризация куска эллипса, высеченного из цилиндра ДВУМЯ основаниями
  // p из [0, 1]
  // i, j - номера точек, пара (0,2) или (1,3)
  private Vect3d getPointOfDubleTruncatedEllipse(double p, int i, int j) throws ExDegeneration {
    double p0, p1;
    double basePointX0 = _planeSysOfCoor.newCoor(_points.get(i)).x();
    double basePointX1 = _planeSysOfCoor.newCoor(_points.get(j)).x();
    p0 = pow(-1, i) * arccos(basePointX0 / _a);
    p1 = pow(-1, i) * arccos(basePointX1 / _a);
    return _planeSysOfCoor.oldCoor(new Vect3d( _a * Math.cos( p * p0 + (1 - p) * p1), _b * Math.sin( p * p0 + (1 - p) * p1), 0));
  }
}
