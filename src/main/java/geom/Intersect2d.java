package geom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author Igor
 */
public class Intersect2d {
  public static ArrayList<Vect3d> intersect(Rib3d rib1, Rib3d rib2) throws ExGeom {
    return rib1.intersect(rib2);
  }

  public static ArrayList<Vect3d> intersect(Rib3d rib, Line3d line) throws ExDegeneration {
    return rib.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Circle3d circ) throws ExDegeneration {
    return rib.intersect(circ);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Polygon3d poly) throws ExDegeneration {
    return rib.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Ray3d ray) throws ExDegeneration {
    return rib.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Arc3d arc) throws ExDegeneration {
    return arc.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Angle3d ang) throws ExDegeneration, ExGeom {
    return ang.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, EllipseMain3d el) throws ExZeroDivision, ExDegeneration {
    return el.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Hyperbole3d hyp) throws ExZeroDivision, ExDegeneration {
    return hyp.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, Parabola3d par) throws ExDegeneration {
    return par.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Rib3d rib, PairOfLines pairOfLines) throws ExDegeneration, ExZeroDivision {
    return pairOfLines.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Rib3d rib) throws ExDegeneration {
    return rib.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line1, Line3d line2) throws ExDegeneration {
    return line1.intersect(line2);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Circle3d circ) throws ExDegeneration {
    return circ.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Polygon3d poly) throws ExDegeneration {
    return poly.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Ray3d ray) throws ExDegeneration {
    return line.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Arc3d arc) throws ExDegeneration {
    return arc.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Angle3d ang) throws ExDegeneration, ExGeom {
    return ang.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, EllipseMain3d el) throws ExDegeneration, ExGeom, ExZeroDivision {
    return el.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Hyperbole3d hyp) throws ExDegeneration, ExGeom, ExZeroDivision {
    return hyp.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Parabola3d par) throws ExDegeneration, ExGeom {
    return par.intersect(line);
  }
   public static ArrayList<Vect3d> intersect(Line3d line, PairOfLines pairOfLines) throws ExDegeneration, ExGeom, ExZeroDivision {
    return pairOfLines.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Rib3d rib) throws ExDegeneration {
    return rib.intersect(circ);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Line3d line) throws ExDegeneration {
    return circ.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ1, Circle3d circ2) throws ExDegeneration {
    return circ1.intersect(circ2);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Polygon3d poly) throws ExDegeneration {
    return circ.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Ray3d ray) throws ExDegeneration {
    return circ.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Arc3d arc) throws ExDegeneration {
    return arc.intersect(circ);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Angle3d ang) throws ExDegeneration {
    return ang.intersect(circ);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Rib3d rib) throws ExDegeneration {
    return rib.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Line3d line) throws ExDegeneration{
    return poly.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Circle3d circ) throws ExDegeneration {
    return circ.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly1, Polygon3d poly2) throws ExDegeneration {
    return poly1.intersect(poly2);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Ray3d ray) throws ExDegeneration {
    return poly.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Arc3d arc) throws ExDegeneration {
    return arc.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Angle3d ang) throws ExDegeneration, ExGeom {
    return ang.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, Rib3d rib) throws ExDegeneration {
    return rib.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, Line3d line) throws ExDegeneration {
    return line.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, Circle3d circ) throws ExDegeneration {
    return circ.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, Polygon3d poly) throws ExDegeneration {
    return poly.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray1, Ray3d ray2) throws ExDegeneration {
    return ray1.intersect(ray2);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, EllipseMain3d el) throws ExDegeneration, ExZeroDivision {
    return el.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, Hyperbole3d hyp) throws ExDegeneration, ExZeroDivision {
    return hyp.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, Parabola3d par) throws ExDegeneration {
    return par.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray, PairOfLines pairOfLines) throws ExDegeneration {
    return pairOfLines.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Hyperbole3d hyp, Ray3d ray) throws ExDegeneration, ExZeroDivision {
    return hyp.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Hyperbole3d hyp, Line3d line) throws ExDegeneration, ExZeroDivision {
    return hyp.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Hyperbole3d hyp, Rib3d rib) throws ExDegeneration, ExZeroDivision {
    return hyp.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(EllipseMain3d el, Rib3d rib) throws ExDegeneration, ExZeroDivision {
    return el.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(EllipseMain3d el, Ray3d ray) throws ExDegeneration, ExZeroDivision {
    return el.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(EllipseMain3d el, Line3d line) throws ExDegeneration, ExZeroDivision {
    return el.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Parabola3d par, Ray3d ray) throws ExDegeneration {
    return par.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(Parabola3d par, Line3d line) throws ExDegeneration {
    return par.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Parabola3d par, Rib3d rib) throws ExDegeneration {
    return par.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(PairOfLines pairOfLines, Ray3d ray) throws ExDegeneration {
    return pairOfLines.intersect(ray);
  }
  public static ArrayList<Vect3d> intersect(PairOfLines pairOfLines, Line3d line) throws ExDegeneration, ExZeroDivision {
    return pairOfLines.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(PairOfLines pairOfLines, Rib3d rib) throws ExDegeneration, ExZeroDivision {
    return pairOfLines.intersect(rib);
  }
  //triangle
   public static ArrayList<Vect3d> intersect(Triang3d tr, Rib3d rib) throws ExDegeneration {
    return rib.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Triang3d tr, Line3d line) throws ExDegeneration {
    return tr.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Triang3d tr, Circle3d circ) throws ExDegeneration {
    return circ.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Triang3d tr, Polygon3d poly) throws ExDegeneration {
    return poly.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Triang3d tr, Ray3d ray2) throws ExDegeneration {
    return tr.intersect(ray2);
  }
  public static ArrayList<Vect3d> intersect(Triang3d tr, Arc3d arc) throws ExDegeneration {
    return arc.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Triang3d tr, Angle3d ang) throws ExDegeneration, ExGeom{
    return ang.intersect(tr);
  }
  //arc
  public static ArrayList<Vect3d> intersect(Arc3d arc, Ray3d ray2) throws ExDegeneration {
    return arc.intersect(ray2);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc, Rib3d rib) throws ExDegeneration {
    return arc.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc, Line3d line) throws ExDegeneration {
    return arc.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc, Polygon3d poly) throws ExDegeneration {
    return arc.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc, Triang3d tr) throws ExDegeneration {
    return arc.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc, Circle3d circ) throws ExDegeneration {
    return arc.intersect(circ);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc1, Arc3d arc2) throws ExDegeneration {
    return arc1.intersect(arc2);
  }
  public static ArrayList<Vect3d> intersect(Arc3d arc, Angle3d ang) throws ExDegeneration, ExGeom{
    return ang.intersect(arc);
  }
  //angle
  public static ArrayList<Vect3d> intersect(Angle3d ang, Circle3d circ) throws ExDegeneration {
    return ang.intersect(circ);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang, Rib3d rib) throws ExDegeneration, ExGeom {
    return ang.intersect(rib);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang, Line3d line) throws ExDegeneration, ExGeom {
    return ang.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang, Polygon3d poly) throws ExDegeneration, ExGeom {
    return ang.intersect(poly);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang, Arc3d arc) throws ExDegeneration, ExGeom{
    return ang.intersect(arc);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang, Triang3d tr) throws ExDegeneration, ExGeom{
    return ang.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang1, Angle3d ang2) throws ExDegeneration, ExGeom{
    return ang1.intersect(ang2);
  }
  public static ArrayList<Vect3d> intersect(Angle3d ang, Ray3d ray) throws ExDegeneration, ExGeom{
    return ang.intersect(ray);
  }
  //----
   public static ArrayList<Vect3d> intersect(Rib3d rib, Triang3d tr) throws ExDegeneration {
    return rib.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Line3d line, Triang3d tr) throws ExDegeneration {
    return tr.intersect(line);
  }
  public static ArrayList<Vect3d> intersect(Circle3d circ, Triang3d tr) throws ExDegeneration {
    return circ.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Polygon3d poly, Triang3d tr) throws ExDegeneration {
    return poly.intersect(tr);
  }
  public static ArrayList<Vect3d> intersect(Ray3d ray2, Triang3d tr) throws ExDegeneration {
    return tr.intersect(ray2);
  }
  public static ArrayList<Vect3d> intersect(Triang3d poly, Triang3d tr) throws ExDegeneration {
    return poly.intersect(tr);
  }
  
  /**
   * Функция строящая пересечение двух кривых (отрезок, прямая, луч, многоугольник, окружность).
   * Возвращаются точки, которых до этого не было.
   * Т.е. Если отрезки накладываются, то не возвращается ничего (пустой массив).
   * Это хорошо для добавления точек на сцену, но не позволит построить многоугольник, являющийся пересечением двух многоугольников
   * @param geom1
   * @param geom2
   * @return
   */
  public static ArrayList<Vect3d> intersect2d(i_Geom geom1, i_Geom geom2){
    try {
      Method m = Intersect2d.class.getMethod("intersect", geom1.getClass(), geom2.getClass());
      return (ArrayList<Vect3d>)m.invoke(Intersect2d.class, geom1, geom2);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
      return new ArrayList<>();
    }
  }
}
