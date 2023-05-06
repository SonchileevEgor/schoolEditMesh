package error;

import bodies.BodyType;
import editor.i_Anchor;
import geom.Angle3d;
import geom.Checker;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Vect3d;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Обработчик ошибок режимов построения.
 * @author Preobrazhenskaia, alexeev
 */
public class ModeErrorHandler implements i_ErrorHandler {

  Component _parent;

  public ModeErrorHandler(Component comp) {
    _parent = comp;
  }

  public ModeErrorHandler() {
    _parent = null;
  }

  /**
   * Проверка совпадения двух точек по значению.
   * @param v1
   * @param v2
   * @return
   */
  public boolean checkPointsDifferent(String v1, String v2) {
    if (v1.equals(v2)) {
      showMessage("Точки не должны совпадать!", Error.WARNING);
      return false;
    }
    return true;
  }

  /**
   * Проверка совпадения двух тел по ID.
   * @param id1
   * @param id2
   * @return
   */
  public boolean checkBodiesDifferent(String id1, String id2) {
    if (id1.equals(id2)) {
      showMessage("Тела не должны совпадать!", Error.WARNING);
      return false;
    }
    return true;
  }

  public boolean checkBasePointsDifferent(ArrayList<String> pointsID, String newAnchorID) {
    for (int j = 1; j < pointsID.size(); j++) {
      if (pointsID.get(j).equals(newAnchorID)) {
        showMessage("Точки основания не должны повторяться!", Error.WARNING);
        return false;
      }
    }
    return true;
  }

  public boolean checkPointsDifferent(ArrayList<String> pointsID, String newAnchorID) {
    for (int j = 0; j < pointsID.size(); j++) {
      if (pointsID.get(j).equals(newAnchorID)) {
        showMessage("Точки не должны повторяться!", Error.WARNING);
        return false;
      }
    }
    return true;
  }

  public boolean checkAngleLessThanPi(Angle3d angle) {
    if (!angle.isLessThanPI()) {
      showMessage("Угол должен быть меньше 180 градусов!", Error.WARNING);
      return false;
    }
    return true;
  }

  /**
   * Проверка, что угол равен нулю.
   * @param angle
   * @return
   */
  public boolean checkAngleIsZero(Angle3d angle) {
    if (angle.value() < Checker.eps()) {
      showMessage("Угол равен нулю!", Error.WARNING);
      return false;
    }
    return true;
  }

  public boolean checkPointsDifferent(String[] pointsID, String newAnchorID) {
    for (int j = 0; j < pointsID.length; j++) {
      //!! TODO: is this check necessary?
      if (pointsID[j] != null) {
        if (pointsID[j].equals(newAnchorID)) {
          showMessage("Точки не должны повторяться!", Error.WARNING);
          return false;
        }
      }
    }
    return true;
  }

  public boolean checkMoreThan2Points(ArrayList<i_Anchor> _anchor) {
    if (_anchor.size() < 3) {
      showMessage("В основании должны быть хотя бы три вершины!", Error.WARNING);
      return false;
    }
    return true;
  }

  public boolean checkPointsNotCollinear(ArrayList<Vect3d> points) {
    if (points.size() > 2) {
      ArrayList<Vect3d> threePoints = new ArrayList<>();
      for (int i = 0; i < points.size() - 2; i++) {
        threePoints.add(points.get(i));
        threePoints.add(points.get(i + 1));
        threePoints.add(points.get(points.size() - 1));
        try {
          if (Checker.lieOnOneLine(threePoints)) {
            showMessage("Точки не должны лежать на одной прямой!", Error.WARNING);
            return false;
          }
        } catch (ExDegeneration ex) {
          showMessage("Точки не должны совпадать!", Error.WARNING);
          return false;
        }
        threePoints.clear();
      }
    }
    return true;
  }

  public boolean checkPointsNotCollinear(ArrayList<i_Anchor> anchors, i_Anchor anchor) {
    ArrayList<Vect3d> points = new ArrayList<>();
    for (int i = 0; i < anchors.size(); i++) {
      points.add(anchors.get(i).getPoint());
    }
    points.add(anchor.getPoint());
    return checkPointsNotCollinear(points);
  }

  public boolean checkPointsNotCoplanar(ArrayList<Vect3d> points) {
    try {
      if (points.size() > 3 && !Checker.isCoplanar(points)) {
        showMessage("Точки должны лежать в одной плоскости!", Error.WARNING);
      }
    } catch (ExDegeneration ex) {
      return false;
    }
    return true;
  }
  
  /**
   * Check four points not on one line when five points not on
   * one line and check points not different
   * @return 
   */
  public boolean check4PointsNotOnOneLine(ArrayList<i_Anchor> anchors, i_Anchor anchor) {
    ArrayList<Vect3d> points = new ArrayList<>();
    for (int i = 0; i < anchors.size(); i++) {
      points.add(anchors.get(i).getPoint());
    }
    points.add(anchor.getPoint());
    return check4PointsNotOnOneLine(points);
  }

  public boolean check4PointsNotOnOneLine(ArrayList<Vect3d> points) {
    if (points.size() > 4) {
      try {
        if ((!Checker.constainsEqualPoints(points)) && (!Checker.lieOnOneLine(points))) {
          if (Checker.fourPointsOnOneLine(points.get(1), points.get(2), points.get(3), points.get(4))
                  | Checker.fourPointsOnOneLine(points.get(0), points.get(2), points.get(3), points.get(4))
                  | Checker.fourPointsOnOneLine(points.get(0), points.get(1), points.get(3), points.get(4))
                  | Checker.fourPointsOnOneLine(points.get(0), points.get(1), points.get(2), points.get(4))
                  | Checker.fourPointsOnOneLine(points.get(0), points.get(1), points.get(2), points.get(3))) {
            showMessage("Только четыре точки не должны лежать на одной прямой!", Error.WARNING);
            return false;
          }
        }
      } catch (ExDegeneration ex) {
        showMessage("Точки не должны совпадать!", Error.WARNING);
        return false;
      }
    }
    return true;
  }

  /**
   * //!! TODO: What is "suits"?
   * проверяем на самопересечения, лежат ли точки на одной линии и на компланарность
   * @param anchors
   * @param point
   * @return
   */
  public boolean checkPointSuits(ArrayList<i_Anchor> anchors, Vect3d point) {
    ArrayList<Vect3d> points = new ArrayList<>();
    for (int i = 0; i < anchors.size(); i++) {
      points.add(anchors.get(i).getPoint());
    }
    points.add(point);

    /**
     * Компилятор (вроде бы) доходит до первого значения false, после чего
     * возвращает результат выражения, не вычисляя последующие логические
     * значения. В результате, всегда должно выводиться не более одного
     * сообщения об ошибке.
     */
    return checkPointsNotCollinear(points)
            && checkPointsNotCoplanar(points)
            && checkPolygonWithoutSelfIntersections(points)
            && checkPolygonConvex(points);
  }

  public boolean checkPointsNotCoplanar(Vect3d[] points) {
    ArrayList<Vect3d> pnts = new ArrayList<>();
    for (int i = 0; i < points.length; i++) {
      if (points[i] != null) {
        pnts.add(points[i]);
      }
    }
    try {
      if (pnts.size() > 3 && Checker.isCoplanar(pnts)) {
        showMessage("Точки не должны лежать в одной плоскости!", Error.WARNING);
        return false;
      }
    } catch (ExDegeneration ex) {
      return false;
    }
    return true;
  }

  public boolean checkPolygonConvex(ArrayList<Vect3d> points) {
    try {
      if (points.size() > 3 && !Checker.isConvexPolygon(points)) {
        showMessage("Многоугольник должен быть выпуклым!", Error.WARNING);
        return false;
      }
    } catch (ExDegeneration ex) {
      return false;
    }
    return true;
  }

  public boolean checkPolygonWithoutSelfIntersections(ArrayList<Vect3d> points) {
    try {
      if (points.size() > 3 && Checker.hasChainSelfIntersect(points)) {
        showMessage("Стороны многоугольника не должны пересекаться друг с другом!", Error.WARNING);
        return false;
      }
    } catch (ExDegeneration ex) {
      return false;
    }
    return true;
  }

  public boolean checkPlaneNotContainsPoint(Plane3d plane, Vect3d point) {
    if (plane.containsPoint(point)) {
      showMessage("Вершина не должна находиться в плоскости основания!", Error.WARNING);
      return false;
    }
    return true;
  }

  public boolean checkBaseContainsPoint(i_Anchor base, String newAnchorID) {
    if (!base.arrayIDs().contains(newAnchorID)) {
      showMessage("Эта точка не является точкой основания!", Error.WARNING);
      return false;
    }
    return true;
  }

  public boolean checkPointOnCircle(i_Anchor circAnc, i_Anchor pntAnc) {
    Circle3d circ = circAnc.getDisk();
    Vect3d pnt = pntAnc.getPoint();
    if (Checker.isPointOnCircle(pnt, circ)) {
      return true;
    } else {
      showMessage("Точка должна лежать на окружности!", Error.WARNING);
      return false;
    }
  }

  public boolean checkPointOnLine(Line3d line, Vect3d point) {
    if (line.contains(point)) {
      showMessage("Точка не должна лежать на прямой!", Error.WARNING);
      return true;
    } else {
      return false;
    }
  }

  public boolean checkBodyTypeIsPlane(BodyType bt) {
    return checkBodyType(bt, BodyType.PLANE, "Выбрана не плоскость!");
  }

  public boolean checkBodyTypeIsLine(BodyType bt) {
    return checkBodyType(bt, BodyType.LINE, "Выбрана не прямая!");
  }

  public boolean checkBodyTypeIsPolygon(BodyType bt) {
    if ((bt != BodyType.POLYGON) && (bt != BodyType.TRIANGLE)) {
      return checkBodyType(bt, BodyType.POLYGON, "Выбран не многоугольник!");
    } else {
      return true;
    }
  }

  public boolean checkBodyTypeIsRib(BodyType bt) {
    return checkBodyType(bt, BodyType.RIB, "Выбрано не ребро!");
  }

  public boolean checkBodyTypeIsSphere(BodyType bt) {
    return checkBodyType(bt, BodyType.SPHERE, "Выбрана не сфера!");
  }

  private boolean checkBodyType(BodyType bt1, BodyType bt2, String message) {
    if (bt1 == bt2) {
      return true;
    } else {
      showMessage(message, Error.WARNING);
      return false;
    }
  }

  @Override
  public void showMessage(String message, Error er) {
    switch (er) {
      case FATAL:
        JOptionPane.showMessageDialog(_parent, message, "Ошибка", JOptionPane.CLOSED_OPTION);
        break;
      case WARNING:
      case NOTIFICATION:
        JOptionPane.showMessageDialog(_parent, message, "Предупреждение", JOptionPane.CLOSED_OPTION);
        break;
      default:
        throw new AssertionError(er.name());
    }
  }
}
