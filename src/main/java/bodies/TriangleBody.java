package bodies;

import static bodies.PolygonBody.BODY_KEY_POLYGON;
import static config.Config.LOG_LEVEL;
import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import util.Log;

/**
 * Meta body - triangle.
 *
 */
public class TriangleBody extends BodyAdapter implements i_PlainBody {
  private Triang3d _triangle;// math object triangle

  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_C = "C";
  public static final String BODY_KEY_AB = "rib_AB";
  public static final String BODY_KEY_BC = "rib_BC";
  public static final String BODY_KEY_AC = "rib_AC";
  public static final String BODY_KEY_FACET = "facet";

  /**
   * Constructor of triangle by math triangle
   * @param id
   * @param triang math object Rib3d
   */
  public TriangleBody (String id, Triang3d triang){
    super(id, "");
    _triangle = triang;
    _alias = "треугольник";
    _isRenamable = false;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   */
  public TriangleBody(String id) {
    super(id, "");
    _alias = "треугольник";
    _isRenamable = false;
    _exists = false;
  }

  // Getters for all vertices of triangle
  public Vect3d A(){ return _triangle.a(); }
  public Vect3d B(){ return _triangle.b(); }
  public Vect3d C(){ return _triangle.c(); }

  @Override
  public String getTitle() {
    return _exists ? "%" + getAnchorID(BODY_KEY_POLYGON) + "%" : "?";
  }
  
  /**
   * @return math object triangle
   */
  public Triang3d triangle() { return _triangle; }

  /**
   * Get opposite face key by key of vertex.
   * @param vertexKey
   * @return
   */
  public String getOppositeEdgeKey( String vertexKey ){
    switch (vertexKey) {
      case BODY_KEY_A:
        return BODY_KEY_BC;
      case BODY_KEY_B:
        return BODY_KEY_AC;
      case BODY_KEY_C:
        return BODY_KEY_AB;
      default:
        throw new RuntimeException("Неверный ключ вершины треугольника" + _title);
    }
  }

  @Override
  public BodyType type() { return BodyType.TRIANGLE; }

  @Override
  public void addRibs(Editor edt){
    try {
      Vect3d a = _triangle.a();
      Vect3d b = _triangle.b();
      Vect3d c = _triangle.c();

      String idA = getAnchorID(BODY_KEY_A);
      String idB = getAnchorID(BODY_KEY_B);
      String idC = getAnchorID(BODY_KEY_C);

      // adding the ribs
      Rib3d rib1 = new Rib3d(a, b);
      edt.addAnchor(rib1, idA, idB, this, BODY_KEY_AB);
      Rib3d rib2 = new Rib3d(a, c);
      edt.addAnchor(rib2, idA, idC, this, BODY_KEY_AC);
      Rib3d rib3 = new Rib3d(b, c);
      edt.addAnchor(rib3, idB, idC, this, BODY_KEY_BC);
    } catch (ExDegeneration ex) { }
  }

  @Override
  public void addPlanes(Editor edt){
    try{
      Vect3d a = _triangle.a();
      Vect3d b = _triangle.b();
      Vect3d c = _triangle.c();

      String idA = getAnchorID(BODY_KEY_A);
      String idB = getAnchorID(BODY_KEY_B);
      String idC = getAnchorID(BODY_KEY_C);

      Polygon3d poly1 = new Polygon3d(a, b, c);
      ArrayList<String> pointIDs = new ArrayList<>();
      pointIDs.add(idA);
      pointIDs.add(idB);
      pointIDs.add(idC);
      edt.addAnchor(poly1, pointIDs, this, BODY_KEY_FACET);
    } catch(ExGeom ex){
      util.Fatal.warning("Cannot create Polygon3d from triangle: " + ex.getMessage());
    }
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
      TriangleBody triang = (TriangleBody) result;
      edt.addAnchor(triang.A(), result, BODY_KEY_A);
      edt.addAnchor(triang.B(), result, BODY_KEY_B);
      edt.addAnchor(triang.C(), result, BODY_KEY_C);
  }

  @Override
  public void glDrawFacets(Render ren){
    // рисуются только якоря
  }

  @Override
  public void glDrawCarcass(Render ren){
    // рисуются только якоря
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      try {
          faces = _triangle.faces();
      } catch (ExGeom e) {
        if (LOG_LEVEL.value() >= 2) {
          Log.out.println(e.getMessage());
        }
      }
      return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    //TODO: Return intersection with plane and return as AbstractPolygon
    return super.getIntersectionWithPlane(plane);
  }

  @Override
  public i_Geom getGeom() {
    return _triangle;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new TriangleBody(id, (Triang3d) geom);
  }
  
  @Override
  public Plane3d plane() {
    try {
      return _triangle.plane();
    } catch( ExDegeneration ex ){
      return Plane3d.oXY();
    }
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return _triangle.intersectWithRayTransversal(ray);
  }
}
