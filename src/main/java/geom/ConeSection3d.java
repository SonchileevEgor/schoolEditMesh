package geom;

import java.util.ArrayList;
import java.util.Collections;
import static geom.Checker.eps;
import static geom.Vect3d.*;
import static geom.Vect3d.O;

/**
 * Коническое сечение как математический объект
 *
 * @author rita
 */
public class ConeSection3d implements AbstractPolygon, i_Geom, i_OrientableGeom {
  // Количество точек в представлении сечения как многоугольника.
  private static final int POINTS_NUM = 100;

  // Все объекты в системе координат, связанной с конусом,
  // в которой он описывается соотношениями
  // r^2*(z-h)^2 = h^2*(x^2+y^2), 0 <= z <= r,
  // где h - высота, r - радиус основания конуса

  private ArrayList<Vect3d> _points; // Опорные точки сечения
  //!! TODO: Сделать тип сечения перечислением.
  private String _sectType; // Тип сечения. Принимает одно из следующих значений: "ellipse", "part of ellipse", "parabola", "hyperbola", "point", "segment", "cone vertex", "triangle", "empty"
  private Cone3d _cone; // Конус
  private Plane3d _plane;// Секущая плоскость
  private NewSystemOfCoor _coneSysOfCoor; // Система координат, связанная с конусом

  /**
   * Constructor of cone section by cone and plane
   * @param cone math object cone
   * @param plane math object plane
   * @throws ExDegeneration
   * @throws geom.ExZeroDivision
   */
  public ConeSection3d(Cone3d cone, Plane3d plane) throws ExDegeneration, ExZeroDivision {
    _coneSysOfCoor = new NewSystemOfCoor(cone);
    _cone = _coneSysOfCoor.coneInNewCoor(cone);
    _plane = _coneSysOfCoor.planeInNewCoor(plane);
    _points = new ArrayList();
    _sectType = set_sectionType();
  }

   /**
   * Constructor of cone section by points
   */
  public ConeSection3d(ArrayList<Vect3d> points) {
    ArrayList<Vect3d> cone_pnts = new ArrayList();
    cone_pnts.add(points.get(0));
    cone_pnts.add(points.get(1));
    cone_pnts.add(points.get(2));
    Cone3d cone = new Cone3d(cone_pnts);
    Plane3d plane = null;
    try {
        plane = new Plane3d(points.get(3), points.get(4), points.get(5));
    } catch (ExDegeneration ex) {}
    try {
      _coneSysOfCoor = new NewSystemOfCoor(cone);
      _cone = _coneSysOfCoor.coneInNewCoor(cone);
      _plane = _coneSysOfCoor.planeInNewCoor(plane);
      _points = new ArrayList();
      _sectType = set_sectionType();
    } catch (ExZeroDivision ex) {
     GeomErrorHandler.errorMessage(ex);
    } catch (ExDegeneration ex) {
     GeomErrorHandler.errorMessage(ex);
    }
  }

  /**
   * @return Список опорных точек сечения
   * "triangle" => вершины треугольника;
   * "hyperbola", "parabola", "part of ellipse" => концы отрезка сечения, лежащего в основании конуса;
   * "segment" => концы отрезка - вершина конуса и точка на окружности основания конуса;
   * "cone vertex" => вершина конуса;
   * "point" => точка на окружности основания конуса;
   * "circle" => центр окружности;
   * "ellipse", "empty" => пустой список.
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> oldPoints = new ArrayList();
    for (int i = 0; i < _points.size(); i++) {
      oldPoints.add(_coneSysOfCoor.oldCoor(_points.get(i)));
    }
    return oldPoints;
  }

  /**
   * @return количество опорных точек сечения
   * "triangle" => 3;
   * "hyperbola", "parabola", "part of ellipse", "segment" => 2;
   * "cone vertex", "point", "circle" => 1;
   * "ellipse", "empty" => 0.
   */
  public int pointsQuantity() {
    return _points.size();
  }

  /**
   * @return тип сечения, один из следующих:
   * "triangle",
   * "hyperbola",
   * "parabola",
   * "part of ellipse",
   * "segment",
   * "cone vertex",
   * "point",
   * "ellipse",
   * "empty",
   * "circle"
   */
  public String sectionType() {
    return _sectType;
  }

  public Cone3d cone() {
    return _cone;
  }

  public Plane3d plane() {
    return _plane;
  }
  
  public NewSystemOfCoor getConeSysOfCoor(){
    return _coneSysOfCoor;
  }
  
  /**
   * Возвращает точку эллипса, параметризованного отрезком [0,1]
   * @param p параметр из отрезка [0,1]
   * @return точка эллипса, соответствующая p
   * @throws geom.ExZeroDivision
   * @throws geom.ExDegeneration
   */
  public Vect3d getPointOfEllipse(double p) throws ExZeroDivision, ExDegeneration{
    Ellipse3d ellipse = new Ellipse3d(_cone, _plane, _points);
    return _coneSysOfCoor.oldCoor(ellipse.getPoint(p));
  }

   /**
   * Возвращает точку участка гиперболы, параметризованного отрезком [0,1]
   * @param p параметр из отрезка [0,1]
   * @return точка участка гиперболы, соответствующая p
   */
  public Vect3d getPointOfHyperbola(double p) throws ExDegeneration {
    double a, b;
    NewSystemOfCoor planeSysOfCoor;
    Vect3d pInBottom1 = rNormalizedVector(new Vect3d(_plane.n().x(), _plane.n().y(), 0), _cone.r());
    Vect3d pInBottom2 = rNormalizedVector(new Vect3d(_plane.n().x(), _plane.n().y(), 0), -_cone.r());
    Line3d generatrix1 = new Line3d(_cone.v(), sub(pInBottom1, _cone.v()));
    Line3d generatrix2 = new Line3d(_cone.v(), sub(pInBottom2, _cone.v()));
    Vect3d curveVert1 = generatrix1.intersectionWithPlane(_plane);
    Vect3d curveVert2 = generatrix2.intersectionWithPlane(_plane);
    a = sub(curveVert1, curveVert2).norm() / 2;
    Vect3d centerOfCurve = new Vect3d((curveVert1.x() + curveVert2.x()) / 2, (curveVert1.y() + curveVert2.y()) / 2, (curveVert1.z() + curveVert2.z()) / 2);
    Vect3d plusY = sum(centerOfCurve, lineInOXY().l());
    if (curveVert1.z() > curveVert2.z()) {
      planeSysOfCoor = new NewSystemOfCoor(centerOfCurve, curveVert2, plusY, sum(centerOfCurve, vector_mul(sub(curveVert2, centerOfCurve), lineInOXY().l())));
    } else {
      planeSysOfCoor = new NewSystemOfCoor(centerOfCurve, curveVert1, plusY, sum(centerOfCurve, vector_mul(sub(curveVert1, centerOfCurve), lineInOXY().l())));
    }
    double bY = planeSysOfCoor.newCoor(_points.get(0)).y();
    double bX = planeSysOfCoor.newCoor(_points.get(0)).x();
    b = Math.sqrt(a * a * bY * bY / (bX * bX - a * a));
    double y1 = planeSysOfCoor.newCoor(_points.get(0)).y();
    double y2 = planeSysOfCoor.newCoor(_points.get(1)).y();
    double y = (1 - p) * y1 + p * y2;
    return _coneSysOfCoor.oldCoor(planeSysOfCoor.oldCoor(new Vect3d(a * Math.sqrt(y * y / (b * b) + 1), y, 0)));
  }

  /**
   * Возвращает точку участка параболы, параметризованного отрезком [0,1]
   * @param p параметр из отрезка [0,1]
   * @return точка участка параболы, соответствующая p
   * @throws geom.ExZeroDivision
   * @throws geom.ExDegeneration
   */
  public Vect3d getPointOfParabola(double p) throws ExZeroDivision, ExDegeneration {
    double a;
    NewSystemOfCoor planeSysOfCoor;
    Vect3d pInBottom1 = rNormalizedVector(new Vect3d(_plane.n().x(), _plane.n().y(), 0), _cone.r());
    Vect3d pInBottom2 = rNormalizedVector(new Vect3d(_plane.n().x(), _plane.n().y(), 0), -_cone.r());
    Line3d generatrix1 = new Line3d(_cone.v(), sub(pInBottom1, _cone.v()));
    Line3d generatrix2 = new Line3d(_cone.v(), sub(pInBottom2, _cone.v()));
    Vect3d curveVert;
    if (Checker.isOrthogonal(_plane.n(), generatrix1.l())) {
      curveVert = generatrix2.intersectionWithPlane(_plane);
    } else {
      curveVert = generatrix1.intersectionWithPlane(_plane);
    }
    Vect3d plusY = sum(curveVert, lineInOXY().l());
    planeSysOfCoor = new NewSystemOfCoor(curveVert, lineInOXY().projectOfPoint(O), plusY, sum(curveVert, vector_mul(sub(lineInOXY().projectOfPoint(O), curveVert), lineInOXY().l())));
    a = planeSysOfCoor.newCoor(_points.get(0)).x() / Math.pow(planeSysOfCoor.newCoor(_points.get(0)).y(), 2);
    double y1 = planeSysOfCoor.newCoor(_points.get(0)).y();
    double y2 = planeSysOfCoor.newCoor(_points.get(1)).y();
    double y = (1 - p) * y1 + p * y2;
    return _coneSysOfCoor.oldCoor(planeSysOfCoor.oldCoor(new Vect3d(a * y * y, y, 0)));
  }

  /**
   * Возвращает радиус окружности - сечения конуса
   * @return радиус
   */
  public double getRadiusOfCircle(){
    return _cone.r() * ( _cone.h().norm() - _plane.z(0,0) ) / _cone.h().norm();
  }

  /**
   * @return Высота конуса
   */
  public Vect3d getHeightOfCone(){
    return Vect3d.sub(_coneSysOfCoor.oldCoor(_cone.v()), _coneSysOfCoor.oldCoor(_cone.c()));
  }

  // Определяет тип сечения, инициализирует переменную _flag
  private String set_sectionType() throws ExDegeneration {
    // Угол наклона плоскости к оси меньше угла наклона образующей к оси
    if( cosHP() > cosHL() + eps() ){
      if( lineInOXY().distFromPoint(O) < _cone.r() ){
        if( _plane.containsPoint(_cone.v()) ){
          pointsInBottom();
          _points.add(_cone.v());
          return "triangle";
        } else {
          pointsInBottom();
          return "hyperbola";
        }
      } else if( lineInOXY().distFromPoint(O) == _cone.r() ){
        _points.add(lineInOXY().projectOfPoint(O));
        return "point";
      } else {
        return "empty";
      }

    // Угол наклона плоскости к оси равен углу наклона образующей к оси
    } else if( Math.abs(cosHP() - cosHL()) < eps() ){
      if( lineInOXY().distFromPoint(O) < _cone.r() ){
        pointsInBottom();
        return "parabola";
      } else if( Math.abs(lineInOXY().distFromPoint(O) - _cone.r()) <= eps() ){
        if( _plane.containsPoint(_cone.v()) ){
          _points.add(_cone.v());
          _points.add(lineInOXY().projectOfPoint(O));
          return "segment";
        } else {
          _points.add(lineInOXY().projectOfPoint(O));
          return "point";
        }
      } else {
        return "empty";
      }
    // Угол наклона плоскости к оси больше угла наклона образующей к оси, но меньше 90 градусов
    } else if( cosHP() + eps() < cosHL() && cosHP() > eps() ){
      if( lineInOXY().distFromPoint(O) < _cone.r() ){
        pointsInBottom();
        return "part of ellipse";
      } else if(Math.abs(lineInOXY().distFromPoint(O) - _cone.r()) < eps() ){
        if( checkEllipseOrEmpty() ){
          return "ellipse";
        } else {
          _points.add(lineInOXY().projectOfPoint(O));
          return "point";
        }
      } else if( lineInOXY().distFromPoint(O) < checkDist() && checkEllipseOrEmpty() ){
        return "ellipse";
      } else if( _plane.containsPoint(_cone.v()) ){
        _points.add(_cone.v());
        return "cone vertex";
      } else {
        return "empty";
      }
    // Угол наклона плоскости к оси равен 90 градусов
    } else if( cosHP() <= eps() ){
      if( 0 <= _plane.z(0, 0) && _plane.z(0, 0) <= _cone.h().norm() ){
        if( _plane.containsPoint(_cone.v()) ){
          _points.add(_cone.v());
          return "cone vertex";
        } else {
          _points.add(new Vect3d( 0, 0, _plane.z(0,0)));
          return "circle";
        }
      } else {
        return "empty";
      }
    }
    return null;
  }

  // Для случаев "hyperbola", "parabola", "part of ellipse" возвращает угловые точки сечения (лежащие в основаниия конуса на окружности)
  private void pointsInBottom() throws ExDegeneration {
    double coef = Math.sqrt(Math.pow(_cone.r(), 2) - Math.pow(lineInOXY().distFromPoint(O), 2));
    Vect3d point1 = sum(rNormalizedVector(lineInOXY().l(), coef), lineInOXY().projectOfPoint(O));
    Vect3d point2 = sum(rNormalizedVector(lineInOXY().l(), -coef), lineInOXY().projectOfPoint(O));
    _points.add(point1);
    _points.add(point2);
  }

  // Линия пересечения секущей плоскости и плоскости Oxy (плоскость дна конуса)
  private Line3d lineInOXY() throws ExDegeneration{
    if(Checker.isCollinear( _plane.n(), new Vect3d(0,0,1)))
      return null;
    else
      return _plane.intersectionWithPlane(new Plane3d(_cone.h(), O));
  }

  /*
   Величина критического расстояния от центра основания конуса до линии пересечения секущей плоскости с плоскостью дна конуса
   Используется при проверке пересечения конуса и плоскости в случае подозрения сечения на эллипс
   */
  private double checkDist() {
    return (_cone.h().norm() * sinHP()) / cosHP();
  }

  /*
   Проверка пересечения конуса и плоскости
   Если угол наклона большеплоскости больше чем угол наклона образующей к высоте конуса,
   расстояние от центра основания конуса до линии пересечения секущей плоскости с плоскостью дна конуса меньше критического значения,
   то плоскость может либо высекать эллипс, либо не иметь пересечения с конусом
   Функция возвращает true, если по обе стороны от секущей плоскости есть точки конуса
   */
  private boolean checkEllipseOrEmpty() {
    return inner_mul(_plane.n(), sub(_plane.pnt(), _cone.v())) * inner_mul(_plane.n(), _plane.pnt()) < 0;
  }

  // Косинус угла между образующей и высотой конуса
  private double cosHL() {
    return _cone.h().norm() / Math.sqrt(_cone.h().norm() * _cone.h().norm() + _cone.r() * _cone.r());
  }

  // Косинус угла наклона секущей плоскости к высоте конуса
  private double cosHP() {
    if( Checker.isCollinear( _plane.n(), new Vect3d(0,0,1) ) )
      return 0;
    else
      return Vect3d.cos(_cone.h(), _plane.projectionOfVect(_cone.h()));
  }

  // Синус угла наклона секущей плоскости к высоте конуса
  private double sinHP() {
    return Math.sqrt(1 - cosHP() * cosHP());
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon() {
    return getAsAbstractPolygon(POINTS_NUM);
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon(int numPoints) {
    if(sectionType().equals("empty")){
      return new ArrayList<>();
    } else if(sectionType().equals("circle")){
      return new Circle3d(getRadiusOfCircle(), getHeightOfCone(), points().get(0)).getAsAbstractPolygon();
    } else if(sectionType().equals("triangle") || sectionType().equals("segment") || sectionType().equals("point") || sectionType().equals("cone vertex")){
      return points();
    } else {
        ArrayList<Vect3d> points = points();
        try {
            for (double p = 0; p < 1; p+=1.0/numPoints) {
                if (sectionType().equals("ellipse") || sectionType().equals("part of ellipse"))
                    points.add(getPointOfEllipse(p));
                if (sectionType().equals("parabola"))
                    points.add(getPointOfParabola(p));
                if (sectionType().equals("hyperbola"))
                    points.add(getPointOfHyperbola(p));
            }
            sortPlanarConvexSet(points);
            return points;
        }
        catch (ExZeroDivision | ExDegeneration ex){
            System.out.println("Warning: Unknown type of coneSection");
            return new ArrayList<>();
        }
    }
  }

  public ArrayList<Polygon3d> faces() {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = getAsAbstractPolygon();
      Vect3d a = new Vect3d(points.get(0));
      Vect3d b = new Vect3d(points.get(1));
      Vect3d c = new Vect3d(points.get(points.size() - 1));
      Orientation bypass = Orientation.getBodyOrientation(Vect3d.O, a, b, c);
      if (bypass == Orientation.LEFT)
          Collections.reverse(points);
      faces.add(new Polygon3d(new ArrayList<>(points)));
    }
    catch (ExGeom ex) {
      System.out.println("Не удалось выполнить ConeSection3d.faces()");
    }
    return faces;
  }

 /**
  * В качестве основного набора точек - точки конуса, нормаль и точка плоскости
  * @return
  */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList <Vect3d> result = _coneSysOfCoor.coneInOldCoor(_cone).deconstr();
    result.add(_coneSysOfCoor.oldCoor(_plane.pnt()));
    result.add(_coneSysOfCoor.oldCoor(_plane.pnt2()));
    result.add(_coneSysOfCoor.oldCoor(_plane.pnt3()));
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new ConeSection3d (points);
  }

  @Override
  public Vect3d getUpVect() {
    return _coneSysOfCoor.planeInOldCoor(_plane).n();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.CONE_SECTION3D;
  }
}
