package geom;

import bodies.PolygonBody;
import editor.i_Body;
import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import geom.ColoredVector3d;
import opengl.colorgl.ColorGL;

/**
 * @author alexeev
 */
public class Minkowski2d implements i_Geom, i_OrientableGeom {
  private ArrayList<Vect3d> _points;
  private ArrayList<ColoredVector3d> _vectors;

    public ArrayList<ColoredVector3d> getVectors() {
        return _vectors;
    }

  /**
   * Constructor of cube by 3 points:
   * vertices of isosceles right triangle (ab=ac, a=pi/2).
     * @param a1
     * @param b1
   * @throws ExDegeneration
   */
//  public Minkowski2d(i_Body a1, i_Body b1) throws ExDegeneration {
//        ArrayList<Vect3d> points = new ArrayList<Vect3d>();
//        System.out.println("BBBBBBBBBBBBBBBBBB");
//        Polygon3d a = (Polygon3d) a1.getGeom();
//        Polygon3d b = (Polygon3d) b1.getGeom();
//        System.out.println(a.getAsAbstractPolygon());
//        System.out.println(b.getAsAbstractPolygon());
//        System.out.println("BBBBBBBBBBBBBBBBBB");
//        for (Vect3d v1 : a.getAsAbstractPolygon()) {
//            for (Vect3d v2 : b.getAsAbstractPolygon()) {
//                points.add(new Vect3d(v1.x() + v2.x(), v1.y() + v2.y(), 0));
//            }
//        }
//        Vect3d center = new Vect3d();
//        for (Vect3d v : points) {
//            center.set_x(center.x() + v.x());
//            center.set_y(center.y() + v.y());
//        }
//        center.set_x(center.x() / points.size());
//        center.set_y(center.y() / points.size());
//
//        Collections.sort(points, new Comparator<Vect3d>() {
//            @Override
//            public int compare(Vect3d p1, Vect3d p2) {
//                double angle1 = Math.atan2(p1.y() - center.y(), p1.x() - center.x());
//                double angle2 = Math.atan2(p2.y() - center.y(), p2.x() - center.x());
//                if (angle1 < angle2) {
//                    return -1;
//                } else if (angle1 > angle2) {
//                    return 1;
//                } else {
//                    return 0;
//                }
//            }
//        });
//        List<Vect3d> uniqueVertices = new ArrayList<>();
//        uniqueVertices.add(points.get(0));
//        for (int i = 1; i < points.size(); i++) {
//            Vect3d prev = uniqueVertices.get(uniqueVertices.size() - 1);
//            Vect3d curr = points.get(i);
//            if (prev.x() != curr.x() || prev.y() != curr.y()) {
//                uniqueVertices.add(curr);
//            }
//        }
//        System.out.println("Было точек до выпуклой оболочки");
//        System.out.println(uniqueVertices.size());
//        _points = (ArrayList<Vect3d>) convexHull(uniqueVertices);
//        System.out.println("Стало после выпуклой оболочки");
//        System.out.println(_points.size());
//  }
  
    public Minkowski2d(i_Body a1, i_Body b1) throws ExDegeneration {
        Polygon3d a = (Polygon3d) a1.getGeom();
        Polygon3d b = (Polygon3d) b1.getGeom();
        
        
        ArrayList<Vect3d> K = a.getAsAbstractPolygon();
        ArrayList<Vect3d> L = b.getAsAbstractPolygon();
        // превращаем каждое ребро вектором и добавляем в список векторов
        ArrayList<ColoredVector3d> vectors = new ArrayList<ColoredVector3d>();
        for (int i = 0; i < K.size(); i++) {
            ColoredVector3d v = new ColoredVector3d(K.get(i), K.get((i + 1) % K.size()));
            vectors.add(v);
        }
        for (int i = 0; i < L.size(); i++) {
            ColoredVector3d v = new ColoredVector3d(L.get(i), L.get((i + 1) % L.size()));
            vectors.add(v);
        }
        
        // объединяем векторы с одинаковым направлением
        for (int i = 0; i < vectors.size(); i++) {
            ColoredVector3d v1 = vectors.get(i);
            for (int j = i + 1; j < vectors.size(); j++) {
                ColoredVector3d v2 = vectors.get(j);
                if (v1.sameDirection(v2)) {
                    ColoredVector3d v3 = new ColoredVector3d(v1.start, v2.end);
                    vectors.set(i, v3);
                    vectors.remove(j);
                    j--;
                }
            }
        }

        // сортируем векторы по углу относительно оси x
        Collections.sort(vectors, new Comparator<ColoredVector3d>() {
            public int compare(ColoredVector3d v1, ColoredVector3d v2) {
                double angle1 = Math.atan2(v1.y, v1.x);
                double angle2 = Math.atan2(v2.y, v2.x);
                if (angle1 < angle2)
                    return -1;
                else if (angle1 > angle2)
                    return 1;
                else
                    return 0;
            }
        });
        
        ArrayList<ColoredVector3d> vs = new ArrayList<ColoredVector3d>();
        
        Vect3d o = new Vect3d(0, 0, 0);
        for (ColoredVector3d v : vectors) {
            vs.add(new ColoredVector3d(o, new Vect3d(o.x() + v.x, o.y() + v.y, 0)));
        }
        _vectors = vs;
        
        ArrayList<Vect3d> sum = new ArrayList<Vect3d>();
        // строим ломаную линию по векторам
        Vect3d start = new Vect3d(0, 0, 0);
        sum.add(start);
        for (ColoredVector3d v : vectors) {
            Vect3d end = new Vect3d(start.x() + v.x, start.y() + v.y, 0);
            sum.add(end);
            start = end;
        }

        _points = sum;
    }
    
    public Minkowski2d(i_Body a1, i_Body b1, ColorGL color_a1, ColorGL color_a2) throws ExDegeneration {
        Polygon3d a = (Polygon3d) a1.getGeom();
        Polygon3d b = (Polygon3d) b1.getGeom();
        
        
        ArrayList<Vect3d> K = a.getAsAbstractPolygon();
        ArrayList<Vect3d> L = b.getAsAbstractPolygon();
        // превращаем каждое ребро вектором и добавляем в список векторов
        ArrayList<ColoredVector3d> vectors = new ArrayList<ColoredVector3d>();
        for (int i = 0; i < K.size(); i++) {
            ColoredVector3d v = new ColoredVector3d(K.get(i), K.get((i + 1) % K.size()));
            v.setColor(color_a1);
            vectors.add(v);
        }
        for (int i = 0; i < L.size(); i++) {
            ColoredVector3d v = new ColoredVector3d(L.get(i), L.get((i + 1) % L.size()));
            v.setColor(color_a2);
            vectors.add(v);
        }
        
        // объединяем векторы с одинаковым направлением
        for (int i = 0; i < vectors.size(); i++) {
            ColoredVector3d v1 = vectors.get(i);
            for (int j = i + 1; j < vectors.size(); j++) {
                ColoredVector3d v2 = vectors.get(j);
                if (v1.sameDirection(v2)) {
                    ColoredVector3d v3 = new ColoredVector3d(v1.start, v2.end);
                    vectors.set(i, v3);
                    vectors.remove(j);
                    j--;
                }
            }
        }

        // сортируем векторы по углу относительно оси x
        Collections.sort(vectors, new Comparator<ColoredVector3d>() {
            public int compare(ColoredVector3d v1, ColoredVector3d v2) {
                double angle1 = Math.atan2(v1.y, v1.x);
                double angle2 = Math.atan2(v2.y, v2.x);
                if (angle1 < angle2)
                    return -1;
                else if (angle1 > angle2)
                    return 1;
                else
                    return 0;
            }
        });
        
        ArrayList<ColoredVector3d> vs = new ArrayList<ColoredVector3d>();
        
        Vect3d o = new Vect3d(0, 0, 0);
        for (ColoredVector3d v : vectors) {
            ColoredVector3d colored_v = new ColoredVector3d(o, new Vect3d(o.x() + v.x, o.y() + v.y, 0));
            colored_v.setColor(v.getColor());
            vs.add(colored_v);
        }
        _vectors = vs;
        
        ArrayList<Vect3d> sum = new ArrayList<Vect3d>();
        // строим ломаную линию по векторам
        Vect3d start = new Vect3d(0, 0, 0);
        sum.add(start);
        for (ColoredVector3d v : vectors) {
            Vect3d end = new Vect3d(start.x() + v.x, start.y() + v.y, 0);
            sum.add(end);
            start = end;
        }

        _points = sum;
    }

  /**
   * Constructor of cube by points:
   */
  public Minkowski2d(ArrayList<Vect3d> points) throws ExDegeneration {

  }
  
  public static List<Vect3d> convexHull(List<Vect3d> points) {
    // 1. Найти точку с минимальной y-координатой (если таких несколько, выбрать точку с минимальной x-координатой) и добавить ее в выпуклую оболочку.
    Vect3d minYPoint = points.get(0);
    for (Vect3d point : points) {
        if (point.y() < minYPoint.y() || (point.y() == minYPoint.y() && point.x() < minYPoint.x())) {
            minYPoint = point;
        }
    }
    List<Vect3d> convexHull = new ArrayList<>();
    convexHull.add(minYPoint);

    // 2. Найти следующую точку, которая образует наименьший угол с предыдущей точкой.
    Vect3d current = minYPoint;
    do {
        Vect3d next = null;
        for (Vect3d point : points) {
            if (point.equals(current)) {
                continue;
            }
            if (next == null || orientation(current, next, point) > 0 ||
                    (orientation(current, next, point) == 0 && distance(current, point) > distance(current, next))) {
                next = point;
            }
        }
        current = next;
        convexHull.add(current);
    } while (!current.equals(minYPoint));

    return convexHull;
    }

    // Функция определения ориентации трех точек
    private static int orientation(Vect3d p1, Vect3d p2, Vect3d p3) {
        return (int) ((p2.y() - p1.y()) * (p3.x() - p2.x()) - (p2.x() - p1.x()) * (p3.y() - p2.y()));
    }

    // Функция вычисления расстояния между двумя точками
    private static double distance(Vect3d p1, Vect3d p2) {
        return Math.sqrt(Math.pow(p1.x() - p2.x(), 2) + Math.pow(p1.y() - p2.y(), 2));
    }

  /**
   * @return all vertices of minkowski sum
   */
  public ArrayList<Vect3d> points(){
    ArrayList<Vect3d> points = new ArrayList<Vect3d>(_points);
    return points;
  }

  /**
   * @return all faces of minkowski
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> faces = new ArrayList<Polygon3d>();
    faces.add(new Polygon3d(_points));
    return faces;
  }


  /**
   * @param line
   * @return Список точек пересечения симплекса и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for(int i=0; i<faces.size(); i++ )
      for(int j=0; j<faces().get(i).intersectWithLine(line).size(); j++)
        if(!points.contains(faces().get(i).intersectWithLine(line).get(j)))
           points.add(faces().get(i).intersectWithLine(line).get(j));

    return points;
  }

  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExGeom ex) {}
    return result;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    // в качестве основного набора точек - вершины
    return points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    Minkowski2d cube = null;
    try {
      cube = new Minkowski2d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return cube;
  }

  @Override
  public Vect3d getUpVect() {
    return Vect3d.sub(points().get(0), points().get(1)).getNormalized();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.CUBE3D;
  }
}