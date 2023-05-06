package editor.behavior;

import geom.Arc3d;
import geom.Checker;
import geom.Circle3d;
import geom.Cylinder3d;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.Line3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Sphere3d;
import geom.Vect3d;
import gui.MainEdtCanvasController;
import java.util.ArrayList;
import util.Fatal;

/**
 * Factory of point move behaviors.
 * @author alexeev
 */
public class BehaviorFactory {
  /**
   * Fix point.
   * @return
   */
  public static i_PointMoveBehavior createFixedBehavior() {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
        return point;
      }

      @Override
      public Behavior type() {
        return Behavior.FIXED;
      }
    };
  }

  /**
   * Свободное перемещение точки.
   * @return
   */
  public static i_PointMoveBehavior createFreeBehavior() {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
        try {
          return canvas.getSightRay(newX, newY).
              intersectionWithPlane(canvas.getScene().getPlaneByPoint(point));
        } catch (ExDegeneration ex) {
          return point;
        }
      }

      @Override
      public Behavior type() {
        return Behavior.FREE;
      }
    };
  }

  /**
   * Перемещение вдоль линии.
   * @param line
   * @return
   */
  public static i_PointMoveBehavior createLineBehavior(final Line3d line) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
          try {
            Vect3d pointOnPlane = canvas.getSightRay(newX, newY).
              intersectionWithPlane(canvas.getScene().getPlaneByPoint(point));
            return line.projectOfPoint(pointOnPlane);
          } catch (ExDegeneration ex) {
            return point;
          }
      }

      @Override
      public Behavior type() {
        return Behavior.LINE;
      }
    };
  }


  /**
   * Перемещение по отрезку.
   * @param rib
   * @return
   */
  public static i_PointMoveBehavior createRibBehavior(final Rib3d rib) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
        try {
          Vect3d pointOnPlane = canvas.getSightRay(newX, newY).
            intersectionWithPlane(canvas.getScene().getPlaneByPoint(point));
          return rib.getClosestPointToPoint(pointOnPlane);
        } catch (ExDegeneration ex) {
          return point;
        }
      }

      @Override
      public Behavior type() {
        return Behavior.RIB;
      }
    };
  }

  /**
   * Перемещение на плоскости.
   * @param plane
   * @return
   */
  public static i_PointMoveBehavior createPlaneBehavior(final Plane3d plane) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
        try {
          return canvas.getSightRay(newX, newY).intersectionWithPlane(plane);
        } catch (ExDegeneration ex) {
          return point;
        }
      }

      @Override
      public Behavior type() {
        return Behavior.PLANE;
      }
    };
  }

  /**
   * Перемещение по окружности.
   * @param circle
   * @return
   */
  public static i_PointMoveBehavior createCircleBehavior(final Circle3d circle) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
          try {
            Vect3d newPoint = canvas.getSightRay(newX, newY).intersectionWithPlane(circle.plane());
            return Vect3d.sum(
                Vect3d.rNormalizedVector(
                    Vect3d.sub(newPoint, circle.center()),
                    circle.radiusLength()),
                circle.center());
          } catch (ExDegeneration ex) {
            return point;
          }
      }

      @Override
      public Behavior type() {
        return Behavior.CIRCLE;
      }
    };
  }

    /**
   * Перемещение по дуге.
   * @param arc
   * @return
   */
  public static i_PointMoveBehavior createArcBehavior(final Arc3d arc) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
          try {
            Vect3d newPoint = canvas.getSightRay(newX, newY).intersectionWithPlane(arc.circle().plane());
            return Vect3d.sum(
                Vect3d.rNormalizedVector(Vect3d.sub(newPoint, arc.center()), arc.r()),
                arc.center());
          } catch (ExDegeneration ex) {
            return point;
          }
      }

      @Override
      public Behavior type() {
        return Behavior.ARC;
      }
    };
  }

  /**
   * Перемещение внутри круга.
   * @param disk
   * @return
   */
  public static i_PointMoveBehavior createDiskBehavior(final Circle3d disk) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
          try {
            Vect3d newPoint = canvas.getSightRay(newX, newY).
                intersectionWithPlane(disk.plane());
            // точка должна оставаться в пределах круга.
            if (Vect3d.dist(disk.center(), newPoint) <= disk.radiusLength()) {
              return newPoint;
            } else {
              return Vect3d.sum(Vect3d.rNormalizedVector(
                Vect3d.sub(newPoint, disk.center()), disk.radiusLength()), disk.center());
            }
          } catch (ExDegeneration ex) {
            return point;
          }
      }

      @Override
      public Behavior type() {
        return Behavior.DISK;
      }
    };
  }

  /**
   * Перемещение внутри многоугольника.
   * @param poly
   * @return
   */
  public static i_PointMoveBehavior createPolygonBehavior(final Polygon3d poly) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY) {
        try {
          Vect3d newPoint = canvas.getSightRay(newX, newY).
              intersectionWithPlane(poly.plane());

          // точка должна оставаться в пределах грани.
          if (geom.Checker.isPointOnClosePolygon(poly, newPoint)) {
            return newPoint;
          } else {
            return poly.getClosestPointToPointInPlane(newPoint);
          }
        } catch (ExDegeneration ex) {
          return point;
        }
      }

      @Override
      public Behavior type() {
        return Behavior.POLYGON;
      }
    };
  }

  /**
   * Перемещение по сфере.
   * @param sph
   * @return
   */
  public static i_PointMoveBehavior createSphereBehavior(final Sphere3d sph) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point,
              double startX, double startY, double newX, double newY) {
        try {
          // луч взгляда
          Ray3d sight = canvas.getSightRay(newX, newY);

          // если взгляд пересекает сферу, то возвращаем точку пересечения
          ArrayList<Vect3d> intersect = sph.intersect(sight);
          if (!intersect.isEmpty()) {
            if (intersect.size() == 2)
              if (Vect3d.dist(point, intersect.get(0)) > Vect3d.dist(point, intersect.get(1)))
                return intersect.get(1);
            else
              return intersect.get(0);
          } else {
            // если нет, то выбор точки зависит от проекции
            switch (canvas.getScene().getProjection()) {
              case PERSPECTIVE_PROJECTION:
              {
                // положение камеры
                Vect3d eye = canvas.getScene().getCameraPosition().eye();
                // граница видимой части сферы
                Circle3d visibleCirc = sph.getVisibleCircle(eye);
                // плоскость, содержащая луч взгляда и центр сферы
                Plane3d pl = new Plane3d(eye, sight.pnt2(), sph.center());
                intersect = pl.intersectWithCircle(visibleCirc);
                if (intersect.isEmpty()) {
                  return point.duplicate();
                } else if (intersect.size() == 1){
                  return intersect.get(0);
                } else {
                  // из двух точек выбираем ту, которая находится между лучами (внутри острого угла)
                  Vect3d p0 = intersect.get(0);
                  Vect3d v0 = Vect3d.sub(p0, eye);
                  Vect3d a = Vect3d.sub(sph.center(), eye);
                  if (Vect3d.inner_mul(a, v0) > 0 ^ Vect3d.inner_mul(sight.l(), v0) > 0) {
                    return intersect.get(0);
                  } else {
                    return intersect.get(1);
                  }
                }
              }
              case ORTHO_PROJECTION:
              {
                Vect3d normal = sight.l();
                Plane3d pl = new Plane3d(normal, canvas.getScene().getCameraPosition().center());
                Vect3d h = sight.intersectionWithPlane(pl);
                Vect3d proj = pl.projectionOfPoint(sph.center());
                Vect3d s = Vect3d.rNormalizedVector(Vect3d.sub(h, proj), sph.radius());
                return Vect3d.sum(sph.center(), s);
              }
              default:
                Fatal.warning("Тип проекции не поддерживается");
            }
          }
        } catch (ExDegeneration ex) {}
        return point.duplicate();
      }

      @Override
      public Behavior type() {
        return Behavior.SPHERE;
      }
    };
  }

  /**
   * Перемещение точки по боковой части цилиндра.
   * @param cylinder
   * @return
   */
  public static i_PointMoveBehavior createCylinderBehavior(final Cylinder3d cylinder) {
    return new i_PointMoveBehavior() {
      @Override
      public Vect3d position(MainEdtCanvasController canvas, Vect3d point,
              double startX, double startY, double newX, double newY) {
        try {
          // луч взгляда
          Ray3d sight = canvas.getSightRay(newX, newY);

          //находим образующие, которые являются граничными при переходе с задней части цилиндра на переднюю
          Vect3d eye = canvas.getScene().getCameraPosition().eye();
          Vect3d eyeProj = cylinder.circ1().plane().projectionOfPoint(eye);
          ArrayList<Vect3d> result1 = cylinder.circ1().tangentPoints(eyeProj);
          eyeProj = cylinder.circ2().plane().projectionOfPoint(eye);
          ArrayList<Vect3d> result2 = cylinder.circ2().tangentPoints(eyeProj);
          ArrayList<Rib3d> generatrix = new ArrayList<>();
          generatrix.add(new Rib3d(result1.get(0), result2.get(0)));
          generatrix.add(new Rib3d(result1.get(1), result2.get(1)));

          if (Checker.isTwoLinesIntersect(generatrix.get(0).line(), generatrix.get(1).line())){
            generatrix.set(0, new Rib3d(result1.get(0), result2.get(1)));
            generatrix.set(1, new Rib3d(result1.get(1), result2.get(0)));
          }
                
          // если взгляд пересекает цилиндр, то возвращаем точку пересечения
          ArrayList<Vect3d> intersect = cylinder.intersectSideSurfaceWithRay(point, sight);
          if (!intersect.isEmpty()){
            if ((intersect.size() == 2) && (!Checker.isPointOnSegment(generatrix.get(0), intersect.get(1))) &&
                    (!Checker.isPointOnSegment(generatrix.get(1), intersect.get(1))) &&
                    (Vect3d.dist(point, intersect.get(0)) > Vect3d.dist(point, intersect.get(1))))
              return intersect.get(1);
            if ((!Checker.isPointOnSegment(generatrix.get(0), intersect.get(0))) &&
                    (!Checker.isPointOnSegment(generatrix.get(1), intersect.get(0))))
              return intersect.get(0);
          } else {
            // если нет, то выбор точки зависит от проекции
            switch (canvas.getScene().getProjection()) {
              case PERSPECTIVE_PROJECTION:
              {
                //находим образующие, которые являются граничными при переходе с задней части цилиндра на переднюю
/*                Vect3d eye = canvas.getScene().getCameraPosition().eye();
                Vect3d eyeProj = cylinder.circ1().plane().projectionOfPoint(eye);
                ArrayList<Vect3d> result1 = cylinder.circ1().tangentPoints(eyeProj);
                eyeProj = cylinder.circ2().plane().projectionOfPoint(eye);
                ArrayList<Vect3d> result2 = cylinder.circ2().tangentPoints(eyeProj);
                ArrayList<Rib3d> generatrix = new ArrayList<>();
                generatrix.add(new Rib3d(result1.get(0), result2.get(0)));
                generatrix.add(new Rib3d(result1.get(1), result2.get(1)));

                if (Checker.isTwoLinesIntersect(generatrix.get(0).line(), generatrix.get(1).line())){
                  generatrix.set(0, new Rib3d(result1.get(0), result2.get(1)));
                  generatrix.set(1, new Rib3d(result1.get(1), result2.get(0)));
                }
*/
                Polygon3d poly = new Polygon3d(generatrix.get(0).a(),generatrix.get(1).a(),
                    generatrix.get(1).b(), generatrix.get(0).b());                
                Vect3d inter = sight.line().intersectionWithPlane(poly.plane());

                //перемещение по образующим (простое)
/*                if (!Checker.isPointOnClosePolygon(poly, inter))
                {
                  Vect3d p1 = generatrix.get(0).line().projectOfPoint(inter);
                  Vect3d p2 = generatrix.get(1).line().projectOfPoint(inter);
                  if ((Vect3d.dist(inter, p1) < Vect3d.dist(inter, p2))&&(cylinder.contains(p1)))
                    return p1;
                  if ((Vect3d.dist(inter, p2) < Vect3d.dist(inter, p1))&&(cylinder.contains(p2)))
                    return p2;
                }
*/
                //перемещение по образующим (сложное)
                if ((!Checker.isPointOnClosePolygon(poly, inter)) ||
                    (Checker.isPointOnSegment(generatrix.get(0), inter)) ||
                    (Checker.isPointOnSegment(generatrix.get(1), inter)))
                {
                  Vect3d p;
                  double diff = Vect3d.sub(point, generatrix.get(0).line().projectOfPoint(point)).norm();
                  if (diff < Vect3d.sub(generatrix.get(1).line().projectOfPoint(point), point).norm()) {
                    Vect3d diffhigh = Vect3d.sub(generatrix.get(0).line().projectOfPoint(inter),
                          generatrix.get(0).line().projectOfPoint(point));
                    p = Vect3d.sum(point, diffhigh);
                  }
                  else
                  {
                    Vect3d diffhigh = Vect3d.sub(generatrix.get(1).line().projectOfPoint(inter),
                          generatrix.get(1).line().projectOfPoint(point));
                    p = Vect3d.sum(point, diffhigh);
                  }
                  if ((cylinder.contains(p))&&(!Checker.isPointInCircle(p, cylinder.circ1()))&&
                          (!Checker.isPointInCircle(p, cylinder.circ2())))
                    return p;
                }

                //перемещение по дуге - части окружности одного из оснований цилиндра (простое)
/*                ArrayList<Arc3d> arcbase = new ArrayList<>();
                arcbase.add(new Arc3d(result1.get(0), cylinder.circ1().intersect(new Line3d(point,
                        cylinder.h())).get(0), result1.get(1)));
                arcbase.add(new Arc3d(result2.get(0), cylinder.circ2().intersect(new Line3d(point,
                        cylinder.h())).get(0), result2.get(1)));

                if ((Vect3d.dist(point, cylinder.c0())) < (Vect3d.dist(point, cylinder.c1())))
                {
                  Vect3d newPoint = canvas.getSightRay(newX, newY).intersectionWithPlane(
                          arcbase.get(0).circle().plane());
                    return Vect3d.sum(Vect3d.rNormalizedVector(Vect3d.sub(newPoint, arcbase.get(0).center()),
                            arcbase.get(0).r()), arcbase.get(0).center());
                }
                else
                {
                  Vect3d newPoint = canvas.getSightRay(newX, newY).intersectionWithPlane(
                          arcbase.get(1).circle().plane());
                  return Vect3d.sum(Vect3d.rNormalizedVector(Vect3d.sub(newPoint, arcbase.get(1).center()),
                            arcbase.get(1).r()), arcbase.get(1).center());
                }
*/
                
                //перемещение по дуге - части окружности одного из оснований цилиндра (сложное)
                ArrayList<Arc3d> arcbase = new ArrayList<>();
                arcbase.add(new Arc3d(result1.get(0), cylinder.circ1().intersect(new Line3d(point,
                        cylinder.h())).get(0), result1.get(1)));
                arcbase.add(new Arc3d(result2.get(0), cylinder.circ2().intersect(new Line3d(point,
                        cylinder.h())).get(0), result2.get(1)));

                if ((Vect3d.dist(point, cylinder.c0())) < (Vect3d.dist(point, cylinder.c1())))
                {
                  Vect3d newPoint = canvas.getSightRay(newX, newY).intersectionWithPlane(
                          arcbase.get(0).circle().plane());
                    return Vect3d.sum(Vect3d.rNormalizedVector(Vect3d.sub(newPoint, arcbase.get(0).center()),
                            arcbase.get(0).r()), arcbase.get(0).center());
                }
                else
                {
                  Vect3d newPoint = canvas.getSightRay(newX, newY).intersectionWithPlane(
                          arcbase.get(1).circle().plane());
                  return Vect3d.sum(Vect3d.rNormalizedVector(Vect3d.sub(newPoint, arcbase.get(1).center()),
                            arcbase.get(1).r()), arcbase.get(1).center());
                }
              }
            }
          }
        } catch (ExGeom | ExZeroDivision ex) {}
        return point.duplicate();
      }
      
      @Override
      public Behavior type() {
        return Behavior.CYLINDER;
      }
    };
  }
}

