package geom;

import java.util.ArrayList;
import java.util.Collections;
import static geom.Checker.eps;
import static geom.Vect3d.*;

/**
 * Math object — cylinder section.
 *
 * @author
 */
public class CylinderSection3d implements AbstractPolygon, i_Geom, i_OrientableGeom {
  private ArrayList<Vect3d> _points; // Опорные точки сечения
  // Тип сечения. Принимает одно из следующих значений:
  //"rectangle", "truncated ellipse", "duble truncated ellipse", "segment", "point", "circle", "ellipse", "empty"
  private String _sectType;
  private Cylinder3d _cyl; // Цилиндр
  private Plane3d _plane;// Секущая плоскость
  private Line3d _lineInBase0;// Линия пересечения секущей плоскости и плоскости Oxy (плоскость дна конуса)
  private Line3d _lineInBase1;// Линия пересечения секущей плоскости и плоскости Oxy (плоскость дна конуса)
  private NewSystemOfCoor _cylSysOfCoor; // Система координат, связанная с цилиндром

  /**
   * Constructor of cylinder section by plane
   * @param cylinder cylinder
   * @param plane section plan
   * @throws ExZeroDivision
   * @throws ExDegeneration
   */
  public CylinderSection3d(Cylinder3d cylinder, Plane3d plane) throws ExZeroDivision, ExDegeneration{
    _cylSysOfCoor = new NewSystemOfCoor(cylinder);
    _cyl = _cylSysOfCoor.cylinderInNewCoor(cylinder);
    _plane = _cylSysOfCoor.planeInNewCoor(plane);
    _lineInBase0 = lineInBase(_cyl.c0());
    _lineInBase1 = lineInBase(_cyl.c1());
    _points = new ArrayList();
    set_sectionType();
  }

   public CylinderSection3d(ArrayList<Vect3d> points) throws ExDegeneration{
    ArrayList<Vect3d> cyl_pnts = new ArrayList();
    cyl_pnts.add(points.get(0));
    cyl_pnts.add(points.get(1));
    cyl_pnts.add(points.get(2));
    Cylinder3d cyl = Cylinder3d.constrByPoints(cyl_pnts);
    Plane3d plane = new Plane3d(points.get(3), points.get(4), points.get(5));
    try {
      _cylSysOfCoor = new NewSystemOfCoor(cyl);
      _cyl = _cylSysOfCoor.cylinderInNewCoor(cyl);
      _plane = _cylSysOfCoor.planeInNewCoor(plane);
      _lineInBase0 = lineInBase(_cyl.c0());
      _lineInBase1 = lineInBase(_cyl.c1());
      _points = new ArrayList();
      set_sectionType();
    } catch (ExZeroDivision ex) {
      GeomErrorHandler.errorMessage(ex);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
  }
   }

   /**
   * @return тип сечения, один из следующих:
   * "rectangle", "part of ellipse", "segment", "point", "ellipse", "empty", "circle"
   */
  public String sectionType(){
    return _sectType;
  }

   /**
   * @return количество опорных точек сечения
   * "rectangle", "duble truncated ellipse" -> 4;
   * "truncated ellipse", "segment" -> 2;
   * "point", "circle" -> 1;
   * "ellipse", "empty" -> 0.
   */
  public int pointsQuantity(){
    return _points.size();
  }

  /**
   * @return Список опорных точек сечения
   * <br>"rectangle" -> вершины прямоугольника;
   * <br>"truncated ellipse" -> концы отрезка сечения, лежащего в основании цилиндра;
   * <br>"duble truncated ellipse" -> концы двух отрезков сечения, лежащих в основаниях цилиндра;
   * <br>"segment" -> концы отрезка - точки на окружностях оснований цилиндра;
   * <br>"point" -> точка на окружности основания цилиндра;
   * <br>"circle" -> центр окружности;
   * <br>"ellipse", "empty" -> пустой список.
   */
  public ArrayList<Vect3d> points(){
    ArrayList<Vect3d> oldPoints = new ArrayList();
    for(int i = 0; i < _points.size(); i++) {
      oldPoints.add(_cylSysOfCoor.oldCoor(_points.get(i)));
    }
    return oldPoints;
  }

  /**
   * Возвращает точку эллипса, параметризованого отрезком [0,1]
   * @param p параметр из отрезка [0,1]
   * @return точка эллипса, соответствующая p
   * @throws geom.ExZeroDivision
   * @throws geom.ExDegeneration
   */
  public Vect3d getPointOfEllipse(double p) throws ExZeroDivision, ExDegeneration{
    Ellipse3d ellipse = new Ellipse3d(_cyl, _plane, _points);
    return _cylSysOfCoor.oldCoor(ellipse.getPoint(p));
  }

  /**
   * Возвращает радиус окружности - сечения цилиндра
   * @return радиус
   */
  public double getRadiusOfCircle(){
    return _cyl.r();
  }

  /**
   * @return Высота цилиндра в исходных координатах
   */
  public Vect3d getHeightOfCylinder(){
    return Vect3d.sub(_cylSysOfCoor.oldCoor(_cyl.c0()), _cylSysOfCoor.oldCoor(_cyl.c1()));
  }

  public NewSystemOfCoor getCylSysOfCoor(){
 return _cylSysOfCoor;
}

  // Определяет тип сечения, инициализирует переменную _flag
  private void set_sectionType() throws ExDegeneration{
    // Плоскость параллельна оси
    if(1 - cosHP() <= eps()){
      // Расстояние от плоскости до оси меньше радиуса
      if(dist(_cyl.c0()) + eps() < _cyl.r()){
        _sectType = "rectangle";
        pointsInBase(_cyl.c0());
        pointsInBase(_cyl.c1());
      // Расстояние от плоскости до оси равно радиусу
      }else if(Math.abs(dist(_cyl.c0()) - _cyl.r()) < eps()){
        _sectType = "segment";
        _points.add(lineInBase(_cyl.c0()).projectOfPoint(_cyl.c0()));
        _points.add(lineInBase(_cyl.c1()).projectOfPoint(_cyl.c1()));
      // Расстояние от плоскости до оси больше радиуса
      }else if(dist(_cyl.c0()) > _cyl.r() + eps()){
        _sectType = "empty";
      }else{
        _sectType = "Polundra!";
      }
    // Плосксть перпендикулярна оси
    }else if(cosHP() <= eps()){
      // Плоскость пересекает ось
      if(-eps() <= _plane.z(0, 0) && _plane.z(0, 0) <= _cyl.h().norm() + eps()){
        _sectType = "circle";
        _points.add(new Vect3d(0, 0, _plane.z(0, 0)));
      // Плоскость не имеет пересечения с цилиндром
      }else if(-eps() > _plane.z(0, 0) || _plane.z(0, 0) > _cyl.h().norm() + eps()){
        _sectType = "empty";
      }else{
        _sectType = "Polundra!";
      }
    // Плоскость пересекает ось цилиндра под углом больше нуля и меньше угла между диагональю и осью.
    // Под диагональю подразумевается диагональ прямоугольника - проекции цилиндра на плоскость параллельную оси цилиндра.
    }else if(cosHP() + eps() < 1 && cosHP() > cosHD() + eps()){
      // Плоскость пересекает оба основания цилиндра
      if(dist(_cyl.c0()) < _cyl.r() - eps() && dist(_cyl.c1()) < _cyl.r() - eps()){
        _sectType = "duble truncated ellipse";
        pointsInBase(_cyl.c0());
        pointsInBase(_cyl.c1());
      // Плоскость пересекает 1ое основание цилинда
      }else if(dist(_cyl.c0()) >= _cyl.r() - eps() && dist(_cyl.c1()) < _cyl.r() - eps()){
        _sectType = "truncated ellipse";//1
        pointsInBase(_cyl.c1());
      // Плоскость пересекает 0ое основание цилинда
      }else if(dist(_cyl.c0()) < _cyl.r() - eps() && dist(_cyl.c1()) >= _cyl.r() - eps()){
        _sectType = "truncated ellipse";//0
        pointsInBase(_cyl.c0());
      // Плоскость пересекает 1ое основание в одной точке
      }else if(dist(_cyl.c0()) > _cyl.r() + eps() && Math.abs(dist(_cyl.c1()) - _cyl.r()) <= eps()){
        _sectType = "point";//1
        _points.add(lineInBase(_cyl.c1()).projectOfPoint(_cyl.c1()));
      // Плоскость пересекает 0ое основание в одной точке
      }else if(dist(_cyl.c1()) > _cyl.r() + eps() && Math.abs(dist(_cyl.c0()) - _cyl.r()) <= eps()){
        _sectType = "point";//0
        _points.add(lineInBase(_cyl.c0()).projectOfPoint(_cyl.c0()));
      // Плоскость не имеет пересечения с цилиндром
      }else if(dist(_cyl.c0()) > _cyl.r() + eps() || dist(_cyl.c1()) > _cyl.r() + eps()){
        _sectType = "empty";
      }else{
        _sectType = "Polundra!";
      }
    // Плоскость пересекает ось цилиндра под углом больше или равным углу между диагональю и осью и меньше PI/2.
    // Под диагональю подразумевается диагональ прямоугольника - проекции цилиндра на плоскость параллельную оси цилиндра.
    }else if(cosHP() <= cosHD() + eps() && cosHP() > eps()){
      // Плоскость пересекает только боковую поверхность цилиндра
      if(_plane.z(0, 0) >= x() - eps() && _plane.z(0, 0) <= _cyl.h().norm() - x() + eps()){
        _sectType = "ellipse";
      // Плоскость пересекает 1ое основаниецилиндра
      }else if(_plane.z(0, 0) > _cyl.h().norm() - x() + eps() && _plane.z(0, 0) < _cyl.h().norm() + x() - eps()){
        _sectType = "truncated ellipse";//1
        pointsInBase(_cyl.c1());
      // Плоскость пересекает 0ое основаниецилиндра
      }else if(_plane.z(0, 0) >  - x() + eps() && _plane.z(0, 0) <  x() - eps()){
        _sectType = "truncated ellipse";//0
        pointsInBase(_cyl.c0());
      // Плоскость пересекает 1ое основание в одной точке
      }else if(Math.abs(_plane.z(0, 0) - _cyl.h().norm() - x()) <= eps()){
        _sectType = "point";//1
        _points.add(lineInBase(_cyl.c1()).projectOfPoint(_cyl.c1()));
      // Плоскость пересекает 0ое основание в одной точке
      }else if(Math.abs(_plane.z(0, 0) + x()) <= eps()){
        _points.add(lineInBase(_cyl.c0()).projectOfPoint(_cyl.c0()));
        _sectType = "point";//0
      // Плоскость не имеет пересечения с цилиндром
      }else if(_plane.z(0, 0) <  - x() - eps() || _plane.z(0, 0) >  _cyl.h().norm() + x() + eps()){
        _sectType = "empty";
      }else{
        _sectType = "Polundra!";
      }
    }else{
      _sectType = "Polundra!";
    }
  }

  // Инициализация прямой пересечения секущей плоскости и плоскости Oxy
  private Line3d lineInBase(Vect3d c) throws ExDegeneration{
    Plane3d basePlane = new Plane3d(_cyl.h(), c);
    if(Checker.isCollinear(_plane.n(), _cyl.h()))
      return null;
    else
      return _plane.intersectionWithPlane(basePlane);
  }

  // Вершины отрезка, лежащего в основании - часть границы сечения
  private void pointsInBase(Vect3d c) throws ExDegeneration{
    double coef = Math.sqrt(Math.pow(_cyl.r(), 2) - Math.pow(dist(c), 2));
    Vect3d point1 = sum(rNormalizedVector(lineInBase(c).l(), coef), lineInBase(c).projectOfPoint(c));
    Vect3d point2 = sum(rNormalizedVector(lineInBase(c).l(), -coef), lineInBase(c).projectOfPoint(c));
    _points.add(point1);
    _points.add(point2);
  }

  // Расстояние в плоскости основания между центром @param c этого основания и секущей плоскостью
  private double dist(Vect3d c) throws ExDegeneration{
    return lineInBase(c).distFromPoint(c);
  }

  // Косинус угла наклона секущей плоскости к высоте цилиндра
  private double cosHP(){
    if(Checker.isCollinear( _plane.n(), new Vect3d(0,0,1)))
      return 0;
    else
      return Vect3d.cos(_cyl.h(), _plane.projectionOfVect(_cyl.h()));
  }

  // Косинус угла наклона секущей плоскости к высоте цилиндра
  private double tgHP(){
    double sinHP = Math.sqrt(1 - Math.pow(cosHP(), 2));
      return sinHP / cosHP();
  }

  // Косинус угла наклона диагонали прямоугольника (цилиндра в профиль) к высоте цилиндра
  private double cosHD(){
    double diag = Math.sqrt(Math.pow(_cyl.h().norm(), 2) + 4 * Math.pow(_cyl.r(), 2));
    return _cyl.h().norm() / diag;
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon() {
    return getAsAbstractPolygon(100);
  }

    @Override
    public ArrayList<Vect3d> getAsAbstractPolygon(int numPoints) {
        if (sectionType().equals("empty")) {
            return new EmptyPolygon().getAsAbstractPolygon();
        } else if (sectionType().equals("Polundra!")) {
            return new EmptyPolygon().getAsAbstractPolygon();
        } else if (sectionType().equals("circle")) {
            return new Circle3d(getRadiusOfCircle(), getHeightOfCylinder(), points().get(0)).getAsAbstractPolygon();
        } else if (sectionType().equals("rectangle")) {
            ArrayList<Vect3d> rectPoints = points();
            try {
                sortPlanarConvexSet(rectPoints);
                return rectPoints;
            } catch (ExZeroDivision | ExDegeneration ex) {
                System.out.println("Warning: WTF? Rectangle is not a rectangle?");
                return new EmptyPolygon().getAsAbstractPolygon();
            }
        } else if (sectionType().equals("segment")) {
            ArrayList<Vect3d> pnts = points();
            if (pnts.get(0).equals(pnts.get(1))) {
                System.out.println("Warning: Points of segment are equals!");
            }
            return pnts;
        } else if (sectionType().equals("point")) {
            ArrayList<Vect3d> point = points();
            return point;
        } else {// ellipse, truncated ellipse, duble truncated ellipse
            ArrayList<Vect3d> points = points();
            if (sectionType().equals("ellipse") || sectionType().equals("truncated ellipse") || sectionType().equals("duble truncated ellipse")) {
                try {
                    for (double p = 0; p < 1; p+=1.0/numPoints) {
                        points.add(getPointOfEllipse(p));
                    }
                    sortPlanarConvexSet(points);
                    return points;
                }
                catch (ExDegeneration | ExZeroDivision ex){
                    System.out.println("Warning: Unknown error in CylinderSection: " + ex.getMessage());
                    return new EmptyPolygon().getAsAbstractPolygon();
                }
            } else {
                System.out.println("Warning: Unknown type of CylinderSection");
                return new EmptyPolygon().getAsAbstractPolygon();
            }
        }
    }

    // Вспомогательная величина для определения положения плоскости относительно цилиндра
  private double x(){
    return _cyl.r() / tgHP();
  }

  public ArrayList<Polygon3d> faces() {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      try {
          ArrayList<Vect3d> points = getAsAbstractPolygon();
          Vect3d a = new Vect3d(points.get(0));
          Vect3d b = new Vect3d(points.get(1));
          Vect3d c = new Vect3d(points.get(points.size()-1));
          Orientation bypass = Orientation.getBodyOrientation(new Vect3d(), a, b, c);
          if (bypass == Orientation.LEFT)
              Collections.reverse(points);
          faces.add(new Polygon3d(new ArrayList<>(points)));
      }
      catch (ExGeom ex) {}
      return faces;
  }

 /**
  * В качестве основного набора точек - точки цилиндра и нормаль и точка плоскости.
  * @return
  */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList <Vect3d> result = null;
      try {
          result = _cylSysOfCoor.cylinderInOldCoor(_cyl).deconstr();
      } catch (ExDegeneration ex) {  }
    result.add(_cylSysOfCoor.oldCoor(_plane.pnt()));
    result.add(_cylSysOfCoor.oldCoor(_plane.pnt2()));
    result.add(_cylSysOfCoor.oldCoor(_plane.pnt3()));
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
      try {
          return new CylinderSection3d(points);
      } catch (ExDegeneration ex) {}
      return null;
  }

  public Cylinder3d cyl() {
    return _cyl;
  }

  public Plane3d plane () {
    return _plane;
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public Vect3d getUpVect() {
    return _plane.n();
  }

  @Override
  public GeomType type() {
    return GeomType.CYLINDER_SECTION3D;
  }
}