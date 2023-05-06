package geom;

import static geom.Checker.isOrthogonal;
import static geom.Vect3d.getAngle;
import static geom.Vect3d.sub;
import static geom.Vect3d.vector_mul;
import static java.lang.Math.PI;

/**
 * New system of coordinate.
 * Parallel shift and turn
 * @author Preobrazhenskaia
 */
public class NewSystemOfCoor{
  private Matrix3 _oldToNew;//Матрица перехода от старых координат к новым
  private Matrix3 _newToOld;//Матрица перехода от новых координат к старым
  private Vect3d _oldO;//В новой системе координат в эту точку перейдет центр
  
  /**
   * Constructor of new system of coordinates by cone
   * New zero is center of cone bottom,
   * axis Oz is cone axis,
   * plane Oxy is plane of cone bottom
   * @param cone math object cone
   * @throws ExZeroDivision
   */
  public NewSystemOfCoor(Cone3d cone) throws ExZeroDivision{
    _oldO = cone.c();
    //Векторы в старой системе координат:
    Vect3d oldVectZ = sub(cone.v(), cone.c());
    Vect3d oldVectX = preimageOf100(oldVectZ);
    Vect3d oldVectY = preimageOf010(oldVectZ, oldVectX);
    //Соответствующие векторы в новой системе координат:
    Vect3d newVectZ = new Vect3d(0,0,oldVectZ.norm());
    Vect3d newVectX = new Vect3d(1,0,0);
    Vect3d newVectY = new Vect3d(0,1,0);
    _oldToNew = transMatr(oldVectZ, oldVectX, oldVectY, newVectZ, newVectX, newVectY);//поворот осей координат
    _newToOld = transMatr(newVectZ, newVectX, newVectY, oldVectZ, oldVectX, oldVectY);
  }

  /**
   * Constructor of new system of coordinates by cylinder
   * New zero is center of cylinder bottom,
   * axis Oz is cylinder axis,
   * plane Oxy is plane of cylynder base
   * @param cylinder
   * @throws ExZeroDivision
   */
  public NewSystemOfCoor(Cylinder3d cylinder) throws ExZeroDivision{
    _oldO = cylinder.c0();
    //Векторы в старой системе координат:
    Vect3d oldVectZ = sub(cylinder.c1(), cylinder.c0());
    Vect3d oldVectX = preimageOf100(oldVectZ);
    Vect3d oldVectY = preimageOf010(oldVectZ, oldVectX);
    //Соответствующие векторы в новой системе координат:
    Vect3d newVectZ = new Vect3d(0,0,oldVectZ.norm());
    Vect3d newVectX = new Vect3d(1,0,0);
    Vect3d newVectY = new Vect3d(0,1,0);
    _oldToNew = transMatr(oldVectZ, oldVectX, oldVectY, newVectZ, newVectX, newVectY);//поворот осей координат
    _newToOld = transMatr(newVectZ, newVectX, newVectY, oldVectZ, oldVectX, oldVectY);
  }

  /**
   * Constructor of new system of coordinates by center, axis Ox, vector from plane Oxy
   * @param OldO new zero in old coordinates
   * @param axisX direction of Ox
   * @param vectInOxy vector from new Oxy in old coordinates
   * @throws geom.ExDegeneration
   */
  public NewSystemOfCoor(Vect3d OldO, Vect3d axisX, Vect3d vectInOxy) throws ExDegeneration {
    _oldO = OldO;
    //Векторы в старой системе координат:
    Vect3d oldVectX = axisX;
    Vect3d oldVectZ = vector_mul(oldVectX, vectInOxy);
    Vect3d oldVectY = vector_mul(oldVectZ, oldVectX);
    //Соответствующие векторы в новой системе координат:
    Vect3d newVectX = new Vect3d(oldVectX.norm(), 0, 0);
    Vect3d newVectY = new Vect3d(0, oldVectY.norm(), 0);
    Vect3d newVectZ = new Vect3d(0, 0, oldVectZ.norm());
    try {
      _oldToNew = transMatr(oldVectZ, oldVectX, oldVectY, newVectZ, newVectX, newVectY);//поворот осей координат
      _newToOld = transMatr(newVectZ, newVectX, newVectY, oldVectZ, oldVectX, oldVectY);
    } catch( ExZeroDivision ex ){
      throw new ExDegeneration("Ошибка: не удалось построить новую систему координат.");
    }
  }

  /**
   * Конструктор новой системы координат.
   * Требование: вектора [OldO, OldX], [OldO, OldY], [OldO, OldZ] ортогональны.
   * @param oldO координаты нового нуля в старой системе координат
   * @param oldX координаты точки на новой оси Ox в старой системе координат
   * @param oldY координаты точки на новой оси Oy в старой системе координат
   * @param oldZ координаты точки на новой оси Oz в старой системе координат
   * @throws ExDegeneration
   */
  public NewSystemOfCoor(Vect3d oldO, Vect3d oldX, Vect3d oldY, Vect3d oldZ) throws ExDegeneration {
    if( isOrthogonal( sub(oldX,oldO), sub(oldY,oldO) ) &&
        isOrthogonal( sub(oldZ,oldO), sub(oldY,oldO) ) &&
        isOrthogonal( sub(oldX,oldO), sub(oldZ,oldO) )) {
      _oldO = oldO;
      //Векторы в старой системе координат:
      Vect3d oldVectX = sub(oldX,oldO);
      Vect3d oldVectY = sub(oldY,oldO);
      Vect3d oldVectZ = sub(oldZ,oldO);
      //Соответствующие векторы в новой системе координат:
      Vect3d newVectX = new Vect3d( oldVectX.norm(), 0, 0 );
      Vect3d newVectY = new Vect3d( 0, oldVectY.norm(), 0 );
      Vect3d newVectZ = new Vect3d( 0, 0, oldVectZ.norm() );
      try {
        _oldToNew = transMatr(oldVectZ, oldVectX, oldVectY, newVectZ, newVectX, newVectY); //поворот осей координат
        _newToOld = transMatr(newVectZ, newVectX, newVectY, oldVectZ, oldVectX, oldVectY);
      } catch( ExZeroDivision ex ){
        throw new ExDegeneration("Не могу построить новую систему координат!");
      }
    } else {
      throw new ExDegeneration("Ошибка: базис системы координат не ортогонален.");
    }
  }

  /**
   * Transfer center of coordinate to new new point
   * @param newCenter new center of coordinate
   * @throws ExDegeneration 
   */
  public void makeNewCenter(Vect3d newCenter) throws ExDegeneration {
    _oldO = newCenter;
  } 
 
  /**
   * Rotation around OZ by angle
   * @param angle rotation angle
   */
  public void rotateAroundOZ(double angle) {
    _oldToNew.leftMultiply(rotMatrixByAngle(angle));
    _newToOld.rightMultiply(rotMatrixByAngle(-angle));
  }
  
  /**
   * Rotate matrix by angle 
   * (cos a  -sin a  0)
   * (sin a   cos a  0)
   * (  0      0     1)
   * @param angle rotate angle
   * @return Matrix3
   */
  private Matrix3 rotMatrixByAngle(double angle) {
    return new Matrix3(Math.cos(angle), -Math.sin(angle), 0,
                       Math.sin(angle), Math.cos(angle), 0,
                       0, 0, 1);
  }
  
  /**
   * @param x old coordinates
   * @return new coordinates of point
   */
  public Vect3d newCoor(Vect3d x){
    Vect3d vec = new Vect3d(x.x(),x.y(),x.z());
    // Displacement of zero,
    vec.inc_sub(_oldO);
    // tern axises.
    return _oldToNew.mulOnVect(vec);
  }

  /**
   * @param x new coordinates
   * @return old coordinates
   */
  public Vect3d oldCoor(Vect3d x){
    Vect3d vec = _newToOld.mulOnVect(x);
    // Tern axises,
    vec.inc_add(_oldO);
    // displacement of zero.
    return vec;
  }

  /**
   * Get the oriented angle between axis Ox and given vector.
   * Ox is axis of new system of coordinate where given vector v has zero applicate
   * @param v given vector
   * @return angle between the vectors in radians (0, 2pi)
   * @throws geom.ExZeroDivision
   */
  public double getPolarAngleInOxy(Vect3d v) throws ExZeroDivision{
    if(this.newCoor(v).y() > 0)
      return getAngle(new Vect3d(1, 0, newCoor(v).z()), newCoor(v));
    else
      return 2 * PI - getAngle(new Vect3d(1, 0, 0), newCoor(v));
  }

  private Vect3d preimageOf100(Vect3d v){
    Vect3d vec;
    if(v.y() == 0 && v.z() == 0)
      vec = new Vect3d(v.x(),v.y()+1,v.z());
    else
      vec = new Vect3d(v.x()+1,v.y(),v.z());
    return Vect3d.getNormalizedVector( Vect3d.vector_mul(vec,v));
  }

  private Vect3d preimageOf010(Vect3d v, Vect3d p){
    return Vect3d.getNormalizedVector( Vect3d.vector_mul(v,p));
  }

  private Matrix3 transMatr(Vect3d oldV, Vect3d oldP, Vect3d oldQ,
          Vect3d newV, Vect3d newP, Vect3d newQ) throws ExZeroDivision {
    Matrix3 a = new Matrix3(oldV.x(), oldV.y(), oldV.z(),
                            oldP.x(), oldP.y(), oldP.z(),
                            oldQ.x(), oldQ.y(), oldQ.z());
    Vect3d trans1 = a.solve(newV.x(), newP.x(), newQ.x());
    Vect3d trans2 = a.solve(newV.y(), newP.y(), newQ.y());
    Vect3d trans3 = a.solve(newV.z(), newP.z(), newQ.z());
    return new Matrix3(trans1.x(), trans1.y(), trans1.z(), 
                       trans2.x(), trans2.y(), trans2.z(),
                       trans3.x(), trans3.y(), trans3.z());
  }

  /**
   * @param plane
   * @return plane in new coordinates
   */
  public Plane3d planeInNewCoor(Plane3d plane){
    return new Plane3d(_oldToNew.mulOnVect(plane.n()),newCoor(plane.pnt()));
  }

  /**
   * @param plane
   * @return plane in old coordinates
   */
  public Plane3d planeInOldCoor(Plane3d plane){
    return new Plane3d(_newToOld.mulOnVect(plane.n()), oldCoor(plane.pnt()));
  }

  /**
   * @param cone
   * @return cone in new coordinates
   */
  public Cone3d coneInNewCoor(Cone3d cone){
    return new Cone3d(newCoor(cone.v()),newCoor(cone.c()),cone.r());
  }

  public Cone3d coneInOldCoor(Cone3d cone){
    return new Cone3d(oldCoor(cone.v()),oldCoor(cone.c()),cone.r());
  }

 public Cylinder3d cylinderInNewCoor(Cylinder3d cylinder) throws ExDegeneration {
    return new Cylinder3d(newCoor(cylinder.c0()), newCoor(cylinder.c1()), cylinder.r());
  }

 public Cylinder3d cylinderInOldCoor(Cylinder3d cylinder) throws ExDegeneration {
    return new Cylinder3d(oldCoor(cylinder.c0()), oldCoor(cylinder.c1()), cylinder.r());
  }
}
