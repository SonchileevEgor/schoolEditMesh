package bodies;

import opengl.Drawer;
import opengl.Render;
import opengl.SmartPlane;
import opengl.TypeFigure;
import editor.Editor;
import editor.state.DisplayParam;
import editor.IntersectAnalyser;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import opengl.sceneparameters.ViewMode;
import util.Fatal;
import util.Util;

/**
 * Meta body - plane.
 *
 */
public class PlaneBody extends BodyAdapter implements i_PlainBody {
  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_C = "C";
  
  private Plane3d _plane; // math object plane

  /**
   * Constructor of plane by math object plane
   * @param id
   * @param title
   * @param plane math object Plane3d
   */
  public PlaneBody(String id, String title, Plane3d plane){
    super(id, title);
    _plane = plane;
    _alias = "плоскость";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public PlaneBody(String id, String title) {
    super(id, title);
    _alias = "плоскость";
    _exists = false;
  }

  /**
   * @return base point of plane
   */
  public Vect3d point(){ return _plane.pnt(); }

  /**
   * @return normal vector for plane
   */
  public Vect3d normal(){ return _plane.n(); }


  /**
   * Check plane contains point
   * @param p point which is checked
   * @return true if plane contains point, false otherwise
   */
  public boolean containsPoint(Vect3d p){
    return _plane.containsPoint(p);
  }

  @Override
  public Plane3d plane(){
    return _plane;
  }

  @Override
  public BodyType type() {
    return BodyType.PLANE;
  }

  @Override
  public void glDrawCarcass(Render ren){
    // Plane has not carcass
  }

  @Override
  public void glDrawFacets(Render ren) {
    if (!_exists)
      return;
    if (_state.isVisible()) {
      Drawer.setObjectFacetColor(ren, _state);
      if (ren.getViewMode() == ViewMode.PENCIL)
        Drawer.setObjectColor(ren, Drawer.getCurrentColor(ren).emphasize());
      if (Drawer.isDrawBigPlanes()) {
        Drawer.drawPlane(ren, _plane);
      } else {
        Drawer.drawPlaneSmart(ren, _plane, false, TypeFigure.SOLID,
            (double)_state.getParam(DisplayParam.PLANE_INDENT));
      }
    }
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    ArrayList<Vect3d> face_points = IntersectAnalyser.getIntersectionPoints(edt, _plane);
    SmartPlane smart_face = new SmartPlane(face_points, _plane);
    try {
      // TODO: пока при создании грани плоскости отступ от крайних точек пересечения с телами сцены считается равным 0.5
      faces = _plane.faces(smart_face.getAsRectangle(0.5));
    } catch (ExGeom ex) {
      Fatal.warning("Error @ PlaneBody.getAllFaces()");
    }
    return faces;
  }

  @Override
  public i_Geom getGeom() {
    return _plane;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new PlaneBody (id, title, (Plane3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    PlaneBody plane = (PlaneBody) result;
    edt.addAnchor(plane._plane.pnt(), result, "A");
    edt.addAnchor(plane._plane.pnt2(), result, "B");
    edt.addAnchor(plane._plane.pnt3(), result, "C");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: пересекать только с изображением плоскости.
    try {
      return ray.intersectionWithPlane(_plane);
    } catch( ExDegeneration ex ){
      return null;
    }
  }
};


