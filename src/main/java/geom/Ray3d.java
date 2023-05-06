package geom;

import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sub;
import java.util.ArrayList;

/**
 *
 * @author igor
 */
public class Ray3d implements i_Geom {

  private Vect3d _pnt;  //point
  private Vect3d _l;    //direction vector

  public Ray3d(){_l = new Vect3d(1,1,1); _pnt = new Vect3d(2,2,2);}

  public Ray3d(Vect3d t, Vect3d l) throws ExDegeneration {
    if (l.norm() < Checker.eps()) {
      throw new ExDegeneration("Направляющий вектор слишком мал.");
    }
    _pnt = t;
    _l = rNormalizedVector(l, 1);
  }

  public Ray3d(ArrayList<Vect3d> points) {
    try {
      Ray3d ray =  ray3dByTwoPoints (points.get(0), points.get(1));
      _pnt = ray._pnt;
      _l = ray._l;
    } catch (ExDegeneration ex) {
    }
  }

  /**
   * Созадет луч по двум точка. 1-я точка вершина, 2-я направление
   * @param t
   * @param a
   * @return
   * @throws ExDegeneration
   */
  public static Ray3d ray3dByTwoPoints(Vect3d t, Vect3d a)
    throws ExDegeneration{
    return new Ray3d(t, rNormalizedVector(sub(a, t), 1));
  }

    /**
   * Созадет луч по точке и углу. Only 2d mode!
   * @param a
   * @param alpha
   * @return
   */
   public static Ray3d ray2dByPoint(Vect3d a, double alpha) {
      Vect3d b = new Vect3d(a.x() + 5, a.y(), a.z()); //2 точка на луче
      Vect3d c = new Vect3d(a.x(), a.y(), a.z() + 5); //точка для построения оси вращения
      Vect3d os = new Vect3d(Vect3d.sub(a, c));
      b = b.rotate(alpha, os);
    try {
      return new Ray3d(a, b);
    } catch (ExDegeneration ex) {
      return new Ray3d();
    }
  }
  /**
   * @return vertex of ray
   */
  public Vect3d pnt(){ return _pnt; }

  /**
   * @return direction vector of the ray
   */
  public Vect3d l(){ return _l; }

  public Vect3d pnt2(){return Vect3d.sum(_pnt, _l);}

  /**
   * проверяем принадлежит ли точка лучу.
   * Точка не может совпадать с вершиной.
   * @param point
   * @return
   */
  public boolean containsPoint(Vect3d point){
    if (Vect3d.equals(point, _pnt)) {
      return false;
    }
    try {
      if (Checker.pointLieOnTheLine(line(), point) && Checker.isCodirectional(_l, Vect3d.sub(point, _pnt))) {
        return true;
      }
    } catch (ExDegeneration ex) {}
    return false;
  }

  public Line3d line() throws ExDegeneration {
    return new Line3d(_pnt,_l);
  }

  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> point = this.line().intersect(ray);
      if (!point.isEmpty()) {
        if(containsPoint(point.get(0)))
          result.add(point.get(0));
      }
    } catch (ExDegeneration ex) {}
    return result;
  }

  public Vect3d intersectionWithPlane(Plane3d plane) throws ExDegeneration {
    Vect3d intersect = line().intersectionWithPlane(plane);
    if (containsPoint(intersect)) {
      return intersect;
    } else {
      throw new ExDegeneration("");
    }
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
    return new Ray3d(points);
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.RAY3D;
  }
}
