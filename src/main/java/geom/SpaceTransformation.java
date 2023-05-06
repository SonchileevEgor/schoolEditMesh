package geom;

import static geom.GeomType.ANGLE3D;
import java.util.ArrayList;

/**
 *
 * @author Elena.
 */
public class SpaceTransformation {
  /**
   * Point that is symmetric with respect to given line.
   * @param point
   * @param line
   * @return
   */
  public static Vect3d pointSymUnderLine(Vect3d point, Line3d line) {
    if (line.contains(point))
      return point;
    Vect3d proj = line.projectOfPoint(point);
    Vect3d sym_point = proj.mul(2).sub(point);
    return sym_point;
  }

  /**
   * Point that is symmetric with respect to given plane.
   * @param point
   * @param plane
   * @return
   */
  public static Vect3d pointSymUnderPlane(Vect3d point, Plane3d plane) {
    if (plane.containsPoint(point))
      return point;
    Vect3d proj = plane.projectionOfPoint(point);
    Vect3d sym_point = proj.mul(2).sub(point);
    return sym_point;
  }

  /**
   * Point that is symmetric with respect to another point.
   * @param point
   * @param center
   * @return
   */
  public static Vect3d pointSymUnderPoint(Vect3d point, Vect3d center) {
    if (Vect3d.equals(point, center))
      return point;
    Vect3d po = Vect3d.sub(center, point);
    po = po.mul(2);
    return po.sum(point);
  }

  /**
   * Return geom object that is symmetric with respect to a line.
   * @param object
   */
  public static i_Geom objectSymUnderLine (i_Geom object, Line3d line) {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (pointSymUnderLine(old_points.get(i), line) );
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }

  /**
   * Return geom object that is symmetric with respect to a plane.
   * @param object
   * @param plane
   * @return
   */
  public static i_Geom objectSymUnderPlane (i_Geom object, Plane3d plane) {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (pointSymUnderPlane(old_points.get(i), plane) );
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }

  /**
   * Return geom object that is symmetric with respect to a point.
   * @param object
   * @param center
   * @return
   */

   public static i_Geom objectSymUnderPoint (i_Geom object, Vect3d center) {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (pointSymUnderPoint(old_points.get(i), center));
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }


  /**
   *
   * @param point
   * @param center of homothety
   * @param k factor of homothety
   * @return the result point
   */
  public static Vect3d homothetyOfPoint (Vect3d point, Vect3d center, double k) {
    Vect3d ox = Vect3d.sub(point, center);
    ox = ox.mul(k);
    return ox.sum(center);
  }

  /**
   * @param object
   * @param center of homothety
   * @param k factor of homothety
   * @return result object
   */
  public static  i_Geom homothetyOfObject (i_Geom object, Vect3d center, double k) {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (homothetyOfPoint(old_points.get(i), center, k) );
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }

  /**
   *
   * @param point
   * @param p1 - first end of vector
   * @param p2 - second end of vector
   * @param alpha - angle of rotation
   * @return point from axial rotation of point about vector p1p2 by angle alpha
   */
  public static Vect3d rotationOfPoint (Vect3d point, Vect3d p1, Vect3d p2, double alpha) throws ExDegeneration  {
    Line3d line = null;
    line = Line3d.line3dByTwoPoints(p1, p2);
    if (line.contains(point))
      return point;
    Plane3d plane = Plane3d.planeByPointOrthLine(point, line);
    Vect3d intersect = line.intersectionWithPlane(plane);

    Vect3d vect = Vect3d.sub(point, intersect);
    vect = vect.rotate(alpha, Vect3d.sub(p2, p1));
    return Vect3d.sum(vect, intersect);
 }

  /**
   *
   * @param object
   * @param p1 - first end of vector
   * @param p2 - second end of vector
   * @param alpha - angle of rotation
   * @return result object from axial rotation about vector p1p2 by angle alpha
   */
  public static  i_Geom rotationOfObject (i_Geom object, Vect3d p1, Vect3d p2, double alpha) throws ExDegeneration {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (rotationOfPoint(old_points.get(i), p1, p2, alpha) );
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }

  /**
   *
   * @param point
   * @param circ - circle of inversion
   * @return
   */
  public static Vect3d inversion2dOfPoint (Vect3d point, Circle3d circ) {
    double r = circ.radiusLength();
    double opLength = Vect3d.sub(circ.center(), point).norm();
    Vect3d result =  Vect3d.sub(point, circ.center()).mul(r*r/opLength/opLength).add(circ.center());
    return result;
  }

  /**
   *
   * @param object
   * @param circ - circle of inversion
   * @return
   */
  public static  i_Geom inversion2dOfObject (i_Geom object, Circle3d circ) {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (inversion2dOfPoint(old_points.get(i), circ) );
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }

  /**
   * Parallel translation by the vector {@code p1 -> p2}.
   * @param point
   * @param p1 start of vector
   * @param p2 end of vector
   * @return result point
   */
  public static Vect3d translationOfPoint (Vect3d point, Vect3d p1, Vect3d p2) throws ExDegeneration {
    if (p1.equals(p2))
      return point;
    Vect3d vect = Vect3d.sub(p2, p1);
    Line3d line = new Line3d (point, vect);
    vect = line.l().mul(vect.norm());
    vect = vect.add(point);
    return vect;
  }

  /**
   * Parallel translation by the vector {@code p1 -> p2}.
   * @param object
   * @param p1 start of vector
   * @param p2 end of vector
   * @return result object
   */
  public static i_Geom translationOfObject (i_Geom object, Vect3d p1, Vect3d p2) throws ExDegeneration {
    i_Geom result;
    ArrayList<Vect3d> old_points = object.deconstr();
    ArrayList<Vect3d> new_points = new ArrayList();
    for(int i = 0; i < old_points.size(); i++)
      new_points.add (translationOfPoint(old_points.get(i), p1, p2) );
    if (object.type() == ANGLE3D)
    {
      Angle3d angle = (Angle3d) object;
      Angle3d angleres = new Angle3d(new_points.get(0), new_points.get(1), new_points.get(2), angle.isLessThanPI());
      result = angleres;
    }
    else
      result = object.constr(new_points);
    return result;
  }
}
