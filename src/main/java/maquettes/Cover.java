package maquettes;

import geom.ExDegeneration;
import geom.Orientation;
import geom.Triang3d;
import geom.Vect3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import opengl.colorgl.ColorGL;

/**
 * @author Leonid Ivanovsky
 */
/**
 * class determines the mathematical model of generating cover around element(s) of body(ies)
 */
public class Cover {

  private ArrayList<ColorPoint> _points; // generating points of cover
  private ArrayList<TriangFace> _triangleFace; // formed face definitions of cover
  private HashMap<ColorPoint, Integer> _points_map;

  public Cover() {
    _points = new ArrayList<>();
    _triangleFace = new ArrayList<>();
    _points_map = new HashMap<>();
  }

  public void addPoints(List<ColorPoint> cover) {
    if (!cover.isEmpty()) {
      for (ColorPoint point: cover) {
        addPoint(point);
      }
    }
  }

  public void addPoints(List<Vect3d> cover, ColorGL color) {
    if (!cover.isEmpty()) {
      for (Vect3d point: cover) {
        addPoint(point, color);
      }
    }
  }

  public void addPoint(ColorPoint point) {
    if (point != null) {
      if (!_points_map.containsKey(point)) {
        _points.add(point);
        _points_map.put(point, _points.size() - 1);
      }
    }
  }

  public void addPoint(Vect3d point, ColorGL color) {
    if (point != null && color != null) {
      ColorPoint colorPoint = new ColorPoint(point, color, ""); //"" - формальность
      if (!_points_map.containsKey(colorPoint)) {
        _points.add(colorPoint);
        _points_map.put(colorPoint, _points.size() - 1);
      }
    }
  }

  public void removePoint(int idx_point) {
    if (idx_point >= 0 && idx_point < _points.size()) {
      ColorPoint point = _points.get(idx_point);
      _points_map.remove(point);
      _points.remove(idx_point);
    }
  }

  public ArrayList<ColorPoint> getPointCover() {
    return _points;
  }

  /**
   * Add triangles, which polygon contains. Triangles of polygon are determined from first point of
   * polygon
   *
   * @param polygon_points generating points for adding in cover
   * @param bypass used for determining the order of bypass by forming triangle
   * @param start_def used for describing triangles by their numbers
   */
  public void addTriangleFaceOnPolygon(ArrayList<Vect3d> polygon_points, Orientation bypass, int start_def) {
    int end = polygon_points.size() - 1;
    for (int i = 1; i < end; i++) {
      addTriangleFace(start_def, start_def + i, start_def + i + 1, bypass);
    }
  }

  /**
   * Add triangles, which prism lateral surfaces contain
   *
   * @param points generating points for adding in cover
   * @param bypass used for determining the order of bypass by forming triangle
   * @param start_def used for describing triangles by their numbers
   */
  public void addTriangleFaceOnPrismLatSurf(ArrayList<Vect3d> points, Orientation bypass, int start_def) {
    int points_on_base = points.size() / 2;
    for (int i = 0; i < points_on_base; i++) {
      int idx_a = i;
      int idx_b = (i + 1) % points_on_base;
      int idx_c = points_on_base + (i + 1) % points_on_base;
      int idx_d = i + points_on_base;
      addTriangleFace(start_def + idx_a, start_def + idx_b, start_def + idx_c, bypass);
      addTriangleFace(start_def + idx_a, start_def + idx_c, start_def + idx_d, bypass);
    }
  }

  /**
   * Add triangles, which pyramid lateral surfaces contain
   *
   * @param points generating points for adding in cover
   * @param start_def used for describing triangles by their numbers
   * @param is_reverseorder used for determining the numbers of generated cover points by forming
   * face definition
   */
  public void addTriangleFaceOnPyrLatSurf(ArrayList<Vect3d> points, int start_def, boolean is_reverseorder, Orientation bypass) {
    int end = points.size();
    for (int i = 1; i < end; i++) {
      if (i < end - 1) {
        if (is_reverseorder) {
          addTriangleFace(start_def, start_def - i, start_def - i - 1, bypass);
        } else {
          addTriangleFace(start_def, start_def + i, start_def + i + 1, bypass);
        }
      } else {
        if (is_reverseorder) {
          addTriangleFace(start_def, start_def - i, start_def - 1, bypass);
        } else {
          addTriangleFace(start_def, start_def + i, start_def + 1, bypass);
        }
      }
    }
  }

  public void addCover(Cover cover) {
    addPoints(cover.getPointCover());
    addTriangleFace(cover.getTriangleFaces());
  }

  public void addTriangleFace(List<TriangFace> face_defs) {
    if (!face_defs.isEmpty()) {
      for (TriangFace tr: face_defs) {
        addTriangleFace(tr);
      }
    }
  }

  public void addTriangleFace(TriangFace face_def) {
    if (face_def != null) {
      _triangleFace.add(face_def);
    }
  }

  public void addTriangleFace(int first_point, int second_point, int third_point, Orientation bypass) {
    TriangFace face_def;
    if (bypass == Orientation.RIGHT) {
      face_def = new TriangFace(first_point, second_point, third_point);
    } else // bypass == Orientation.LEFT
    {
      face_def = new TriangFace(first_point, third_point, second_point);
    }
    addTriangleFace(face_def);
  }

  public void addTriangleFace(int first_point, int second_point, int third_point) {
    addTriangleFace(first_point, second_point, third_point, Orientation.RIGHT);
  }

  public ArrayList<TriangFace> getTriangleFaces() {
    return _triangleFace;
  }

  public void removeTriangleFaces(int idx_def) {
    if (idx_def >= 0 && idx_def < _triangleFace.size()) {
      _triangleFace.remove(idx_def);
    }
  }

  public Triang3d get(int index) throws ExDegeneration {
    if (index > _triangleFace.size() || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + _triangleFace.size());
    }
    TriangFace triangFace = _triangleFace.get(index);
    Vect3d a = _points.get(triangFace.getFirstPoint()).getPoint();
    Vect3d b = _points.get(triangFace.getSecondPoint()).getPoint();
    Vect3d c = _points.get(triangFace.getThirdPoint()).getPoint();
    return new Triang3d(a, b, c);
  }

  public void add(Triang3d tr, ColorGL color) {
    ColorPoint point1 = new ColorPoint(tr.a(), color, ""); //третий параметр - чистая формальность,
    ColorPoint point2 = new ColorPoint(tr.b(), color, ""); //т.к. без него не будет работать,
    ColorPoint point3 = new ColorPoint(tr.c(), color, ""); //а здесь ColorPoint могут и не иметь id, 
    //т.к. используются для построения
    addPoint(point1);
    addPoint(point2);
    addPoint(point3);
    addTriangleFace(_points_map.get(point1), _points_map.get(point2), _points_map.get(point3));
  }

  public void addAll(List<Triang3d> trs, ColorGL color) {
    for (Triang3d tr: trs) {
      add(tr, color);
    }
  }

  public int getTriangCount() {
    return _triangleFace.size();
  }

}
