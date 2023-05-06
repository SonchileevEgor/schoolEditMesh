package geom;

import java.util.ArrayList;

/**
 * Сектор круга.
 * Определяется дугой окружности.
 * @author alexeev
 */
public class Sector3d implements i_Geom, i_OrientableGeom {
  private Arc3d _arc;
  
  /**
   * Сектор по дуге.
   * @param arc 
   */
  public Sector3d( Arc3d arc ){
    _arc = arc;
  }
  
  /**
   * Сектор по центру, нормали, точке дуги и углу.
   * @param center центр круга
   * @param normal нормаль к сектору
   * @param vert1 вершина дуги
   * @param angle угловая величина
   */
  public Sector3d( Vect3d center, Vect3d normal, Vect3d vert1, double angle ){
    _arc = new Arc3d(center, normal, vert1, angle);
  }
  
  /**
   * Сектор по углу (объект) и радиусу.
   * @param angle угол
   * @param radius радиус
   */
  public Sector3d( Angle3d angle, double radius ){
    _arc = new Arc3d(angle, radius);
  }
  
  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<>();
    Circle3d circ = _arc.circle();
    try {
      Vect3d diskIntersect = circ.intersectInteriorWithRay(ray);
      if( diskIntersect != null ){
        double angle = circ.getPolarAngleByPointAndStartPointOnCircle(diskIntersect, _arc.vert1());
        if (0 <= angle && angle <= _arc.value())
          result.add(diskIntersect);
      }
    } catch( ExDegeneration ex ){}
    return result;
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.SECTOR3D;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return _arc.deconstr();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Sector3d(new Arc3d(points));
    } catch(ExDegeneration ex) {
      throw new RuntimeException("невозможно построить сектор");
    }
  }

  @Override
  public Vect3d getUpVect() {
    return _arc.getUpVect();
  }
}
