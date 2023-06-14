package opengl;

import gui.elements.AngleStyle;
import com.jogamp.opengl.util.gl2.GLUT;
import editor.Editor;
import editor.IntersectAnalyser;
import editor.state.AnchorState;
import editor.state.DisplayParam;
import editor.state.EntityState;
import geom.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import opengl.colorgl.ColorGL;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.ViewMode;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import java.util.ArrayList;
import java.util.List;
import static util.Util.getCoefPlane;

/**
 * Contains high-level drawing methods for OpenGL SIC: All functions must
 * restore OpenGL state after calling(if this function changes OpenGL state for
 * it target).
 */
public class Drawer {

  private static double smartPlaneIndent = 0.5;
  private static double medianHatchIndentMultiplier = 0.011;
  private static int CONE_SLICES = 63;
  private static int CONE_STACKS = 20;
  private static int CYLINDER_SLICES = 63;
  private static int CYLINDER_STACKS = 20;

  static private TransformMatrix _trasformMatrix = new TransformMatrix();
  static private PlaneManager planeManager = new PlaneManager();
  static private float lineWidthSavedState;
  static private float glPolygonOffsetFactor, glPolygonOffsetUnits;

  /**
   * Режим, в котором все цвета граней становятся темно-серыми
   */
  static private boolean allObjectsForcedColor = false;
  /**
   * If true, all the planes will be rendered as endless, otherwise will be
   * limited by intersections with other bodies.
   */
  static private boolean drawBigPlanes = false;

  /**
   * @see #allObjectsForcedColor
   * @return
   */
  public static boolean isAllObjectsForcedColor() {
    return allObjectsForcedColor;
  }

  /**
   * @see #allObjectsForcedColor
   * @param allObjectsForcedColor
   */
  public static void setAllObjectsForcedColor(boolean allObjectsForcedColor) {
    Drawer.allObjectsForcedColor = allObjectsForcedColor;
  }

  /**
   * Get value of {@link #drawBigPlanes}
   *
   * @return
   */
  public static boolean isDrawBigPlanes() {
    return drawBigPlanes;
  }

  /**
   * Sets {@link #drawBigPlanes}
   *
   * @param drawBigPlanes
   */
  public static void setDrawBigPlanes(boolean drawBigPlanes) {
    Drawer.drawBigPlanes = drawBigPlanes;
  }

  static public void resetContext() {
    planeManager.reset();
  }

  public static double getSmartPlaneIndent() {
    return smartPlaneIndent;
  }

  public static void setSmartPlaneIndent(double smartPlaneIndent) {
    Drawer.smartPlaneIndent = smartPlaneIndent;
  }

  private static void saveLineWidthState(Render ren) {
    // Save global width of lines
    lineWidthSavedState = getCurrentLineWidth(ren);
  }

  public static void loadLineWidthState(Render ren) {
    // Load global width of lines before calling function
    Drawer.setLineWidth(ren, lineWidthSavedState);
  }

  /**
   * Get current transform matrices
   *
   * @param ren Object for drawing in OpenGL canvas
   * @return Transform matrices
   */
  public static TransformMatrix getTrasformMatrix(Render ren) {
    saveTransformMatrix(ren);
    return _trasformMatrix;
  }

  /**
   * Saves transformation matrix for use outside the function rendering scenes
   *
   * @param ren Object for drawing in OpenGL canvas
   */
  static public void saveTransformMatrix(Render ren) {
    GL2 gl = ren.getGL();
    double[] model = new double[16];
    double[] projection = new double[16];
    int[] viewport = new int[16];

    gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, model, 0);
    gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

    _trasformMatrix.setModel(model);
    _trasformMatrix.setProjection(projection);
    _trasformMatrix.setViewport(viewport);
  }

  static public void setTransformMatrixForOpenGL(TransformMatrix transformMatrix, Render ren) {
    GL2 gl = ren.getGL();
    double[] model = transformMatrix.getModel();
//    double[] projection = transformMatrix.getProjection();
//    int[] viewport = transformMatrix.getViewport();
    gl.glLoadMatrixd(model, 0);
  }

  /**
   * Same as drawPoligon, only for four points
   *
   * @param ren object for drawing in OpenGL canvas
   * @param a 1st point
   * @param b 2nd point
   * @param d 3rd point
   * @param c 4th point
   * @param type Way to draw
   * @see #drawPolygon(Render, java.util.ArrayList, TypeFigure)
   */
  static public void drawQuad(Render ren, Vect3d a, Vect3d b, Vect3d d, Vect3d c, TypeFigure type) {
    ArrayList<Vect3d> points = new ArrayList<>();
    points.add(a);
    points.add(b);
    points.add(d);
    points.add(c);
    drawPolygon(ren, points, type);
  }

  /**
   * Set the zero vector normal to the body does not change color when the light
   * have been changed
   *
   * @param ren object for drawing in OpenGL canvas
   */
  static private void setZeroNormal(Render ren) {
    ren.getGL().glNormal3dv(new double[]{0, 0, 0}, 0);
  }

  /**
   * Draw segment
   *
   * @param ren object for drawing in OpenGL canvas
   * @param a 1st point of segment
   * @param b 2nd point of segment
   */
  static public void drawSegment(Render ren, Vect3d a, Vect3d b) {
    GL2 gl = ren.getGL();
    setZeroNormal(ren);
    gl.glBegin(GL.GL_LINES);
    gl.glVertex3dv(a.getArray(), 0);
    gl.glVertex3dv(b.getArray(), 0);
    gl.glEnd();
  }

  static public void setLineWidth(Render render, double width) {
    render.getGL().glLineWidth((float) width);
  }

  static public void setPairOfLinesWidth(Render render, double width) {
    render.getGL().glLineWidth((float) width);
  }
  /**
   * Draw segment
   *
   * @param ren object for drawing in OpenGL canvas
   * @param a 1st point of segment
   * @param b 2nd point of segment
   * @param lineWidth Width of drawing segment in pixels
   */
  static public void drawSegment(Render ren, Vect3d a, Vect3d b, double lineWidth) {
    saveLineWidthState(ren);
    setLineWidth(ren, lineWidth);
    drawSegment(ren, a, b);
    loadLineWidthState(ren);
  }

  /**
   * Draw segment by stipple
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param a 1st point of segment
   * @param b 2nd point of segment
   */
  static public void drawSegmentStipple(Render ren, Vect3d a, Vect3d b) {
    GL2 gl = ren.getGL();
    SwitcherStateGL.saveAndEnable(ren, GL2.GL_LINE_STIPPLE);
    gl.glLineStipple(1, (short) 0x00ff);
    drawSegment(ren, a, b);
    SwitcherStateGL.restore(ren, GL2.GL_LINE_STIPPLE);
  }

  /**
   * Draw segment by stipple
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param a 1st point of segment
   * @param b 2nd point of segment
   * @param lineWidth Width of drawing segment in pixels
   */
  static public void drawSegmentStipple(Render ren, Vect3d a, Vect3d b, float lineWidth) {
    saveLineWidthState(ren);
    Drawer.setLineWidth(ren, lineWidth);
    drawSegmentStipple(ren, a, b);
    loadLineWidthState(ren);
  }

  /**
   * Draw line through two points
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param a 1st point
   * @param b 2nd point (must be in visibility volume)
   */
  static public void drawLine(Render ren, Vect3d a, Vect3d b) {
    Vect3d dirLine = Vect3d.sub(a, b);
    dirLine = Vect3d.getNormalizedVector(dirLine);
    double k = ren.getViewVolume().getSizeVisible() * 4;
    dirLine.inc_mul(k);
    drawSegment(ren, Vect3d.sum(b, dirLine), Vect3d.sub(b, dirLine));
  }

  /**
   * Draw line through two points
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param a 1st point
   * @param b 2nd point (must be in visibility volume)
   */
  static public void drawPairOfLines(Render ren, Vect3d a, Vect3d b, Vect3d c, Vect3d d) {
    drawLine(ren, a, b);
    drawLine(ren, c, d);
  }

  /**
   * Draw line through two points
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param a 1st point
   * @param b 2nd point (must be in visibility volume)
   * @param lineWidth Width of drawing line in pixels
   */
  static public void drawLine(Render ren, Vect3d a, Vect3d b, float lineWidth) {
    saveLineWidthState(ren);
    Drawer.setLineWidth(ren, lineWidth);
    drawLine(ren, a, b);
    loadLineWidthState(ren);
  }

  /**
   * Draw ray by point and direction vector
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Start point of ray
   * @param direction Direction vector of ray
   */
  static public void drawRay(Render ren, Vect3d point, Vect3d direction) {
    Vect3d normDir = Vect3d.getNormalizedVector(direction);
    double k = ren.getViewVolume().getSizeVisible() * 4;
    normDir.inc_mul(k);
    drawSegment(ren, point, Vect3d.sum(point, normDir));
  }

  /**
   * Draw ray by two points.
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param startPoint Start point of ray
   * @param secondPoint Point on the ray. Ray passes through this point.
   */
  static public void drawRayByPoints(Render ren, Vect3d startPoint, Vect3d secondPoint) {
    Vect3d direction = Vect3d.sub(secondPoint, startPoint);
    drawRay(ren, startPoint, direction);
  }

  /**
   * Draw line through two points by stipple
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param a 1st point of line
   * @param b 2nd point of line (must be in visibility volume)
   */
  static public void drawLineStipple(Render ren, Vect3d a, Vect3d b) {
    GL2 gl = ren.getGL();
    SwitcherStateGL.saveAndEnable(ren, GL2.GL_LINE_STIPPLE);
    gl.glLineStipple(1, (short) 0x00ff);
    drawLine(ren, a, b);
    SwitcherStateGL.restore(ren, GL2.GL_LINE_STIPPLE);
  }

  /**
   * Draw end of arrow
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param startPoint Start point of arrow.
   * @param finishPoint Finish point of the arrow. Arrow indicates on this point
   * @param style Style of arrow's end.
   */
  static private void drawArrowEnd(Render ren, Vect3d startPoint, Vect3d finishPoint, StyleArrow style) {
    CameraPosition cam = ren.getCameraPosition();
    // Vect from start point to finish point
    Vect3d sf = Vect3d.sub(finishPoint, startPoint);
    double len = Vect3d.norm(sf);
    // Radius of base of conus of arrow's end
    double rad = cam.distance() * 0.008;
    // Size of arrow's end
    double sizeIndent = cam.distance() * 0.02;
    if (sizeIndent > len / 3) {
      sizeIndent = len / 3;
    }

    sf.inc_mul((len - sizeIndent) / len);

    switch (style) {
      case CONE:
        drawCone(ren, rad, Vect3d.sum(startPoint, sf), finishPoint, TypeFigure.SOLID, getCurrentLineWidth(ren), true);
        break;
      case LINES:
        // Vect from finish point to start point
        Vect3d fs = Vect3d.sub(startPoint, finishPoint);
        fs.inc_mul(sizeIndent);
        // Vector around which you must turn "fs", to get the line of end of the arrow
        Vect3d n = Vect3d.sub(cam.eye(), finishPoint);
        Matrix3 rotMat1 = Matrix3.getRotateMatrix(Angle3d.degree2Radians(20), n);
        Matrix3 rotMat2 = Matrix3.getRotateMatrix(Angle3d.degree2Radians(-20), n);
        Vect3d dirLine1 = rotMat1.mulOnVect(fs);
        Vect3d dirLine2 = rotMat2.mulOnVect(fs);
        drawSegment(ren, finishPoint, Vect3d.sum(finishPoint, dirLine1));
        drawSegment(ren, finishPoint, Vect3d.sum(finishPoint, dirLine2));
        break;
    }
  }

  /**
   * Draw end of arrow 2D
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param startPoint Start point of arrow.
   * @param finishPoint Finish point of the arrow. Arrow indicates on this
   * point.
   */
  static private void drawArrowEnd2D(Render ren, Vect3d startPoint, Vect3d finishPoint) {
    CameraPosition cam = ren.getCameraPosition();
    Vect3d sf = Vect3d.sub(finishPoint, startPoint);
    double len = Vect3d.norm(sf);
    // Size of arrow's end
    double sizeIndent = cam.distance() * 0.02;
    if (sizeIndent > len / 3) {
      sizeIndent = len / 3;
    }
    // Vect from finish point to start point
    Vect3d fs = Vect3d.sub(startPoint, finishPoint);
    fs.inc_mul(sizeIndent / len);
    // Vector around which you must turn "fs", to get the line of end of the arrow
    Vect3d n = new Vect3d(0, 0, 1);
    Vect3d dirLine1 = fs.rotate(0.523, n);
    Vect3d dirLine2 = fs.rotate(-0.523, n);
    drawSegment(ren, finishPoint, Vect3d.sum(finishPoint, dirLine1));
    drawSegment(ren, finishPoint, Vect3d.sum(finishPoint, dirLine2));
  }

  /**
   * Draw arrow from start point to finish point
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param startPoint Start point of arrow.
   * @param finishPoint Finish point of the arrow. Arrow indicates on this point
   * @param type Type of drawing arrow
   * @param style Style of arrow's end.
   */
  static public void drawArrow(Render ren, Vect3d startPoint, Vect3d finishPoint, TypeArrow type, StyleArrow style) {
    if (style == StyleArrow.NONE) {
      return;
    }

    GL2 gl = ren.getGL();
    gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
    SwitcherStateGL.saveAndEnable(ren, GL2.GL_DEPTH_TEST);
    SwitcherStateGL.saveAndEnable(ren, GL2.GL_LIGHTING);
    drawSegment(ren, startPoint, finishPoint);
    switch (type) {
      case SEGMENT:
        break;
      case ONE_END:
        drawArrowEnd(ren, startPoint, finishPoint, style);
        break;
      case TWO_ENDS:
        drawArrowEnd(ren, startPoint, finishPoint, style);
        drawArrowEnd(ren, finishPoint, startPoint, style);
        break;
      default:
        break;
    }
    SwitcherStateGL.restore(ren, GL2.GL_LIGHTING);
    SwitcherStateGL.restore(ren, GL2.GL_DEPTH_TEST);
  }

  /**
   * Draw arrow from start point to finish point in 2D
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param startPoint Start point of arrow.
   * @param finishPoint Finish point of the arrow. Arrow indicates on this point
   * @param type Type of drawing arrow
   */
  static public void draw2DArrow(Render ren, Vect3d startPoint, Vect3d finishPoint, TypeArrow type) {
    drawSegment(ren, startPoint, finishPoint);
    switch (type) {
      case SEGMENT:
        break;
      case ONE_END:
        drawArrowEnd2D(ren, startPoint, finishPoint);
        break;
      case TWO_ENDS:
        drawArrowEnd2D(ren, startPoint, finishPoint);
        drawArrowEnd2D(ren, finishPoint, startPoint);
        break;
      default:
        break;
    }
  }

  /**
   * Draw sphere in school style ( 2 circles on surface of sphere ). Rotates the
   * sphere so that it was directed into the camera
   *
   * @param ren object for drawing in OpenGL canvas
   * @param c Center of the sphere
   * @param r Radius of the sphere
   */
  static public void drawSphereCircles(Render ren, Vect3d c, double r) {
    CameraPosition cam = ren.getCameraPosition();
    // Vector form center of object to camera
    Vect3d norm1 = Vect3d.sub(cam.eye(), c);

    // The second point of the segment of "tangent".
    // In fact it is not a tangent, but for the rotation vector is also nice,
    // because the point T2 is on the sphere.
    SpherCoord spherCoord = new SpherCoord(cam.azimuth(),
            cam.zenith() - Angle3d.degree2Radians(20),
            cam.distance());
    Vect3d T2 = spherCoord.toCartesian();

    Vect3d tangent = Vect3d.sub(T2, cam.eye());
    // Compute the normal vector of the second circle by rotating the first
    Vect3d rotVec = Vect3d.vector_mul(norm1, tangent);
    Matrix3 matRot = Matrix3.getRotateMatrix(Angle3d.degree2Radians(90 - 15), rotVec.x(), rotVec.y(), rotVec.z());
    Vect3d norm2 = matRot.mulOnVect(norm1);

    drawCircle(ren, r, c, norm2, TypeFigure.WIRE);

    // Drawing of visible circle boundary
    Sphere3d sph = new Sphere3d(c, r);
    if (!Checker.isPointInSphere(cam.eye(), sph)) {
      Circle3d circ2;
      switch (ren.getProjection()) {
        case PERSPECTIVE_PROJECTION:
          circ2 = sph.getVisibleCircle(cam.eye());
          break;
        case ORTHO_PROJECTION:
          circ2 = sph.projectOnPlane(new Plane3d(cam.getEyeToCenterVect(), cam.center()));
          break;
        default:
          throw new AssertionError(ren.getProjection().name());
      }

      drawCircle(ren, circ2.radiusLength(), circ2.center(), circ2.normal(), TypeFigure.WIRE);
    }
  }

  /**
   * Draw sphere by facets
   *
   * @param ren object for drawing in OpenGL canvas
   * @param c Center of the sphere
   * @param r Radius of the sphere
   * @param lineWidth The width of the lines which are drawn on the object
   */
  static public void drawSphereFacets(Render ren, Vect3d c, double r, float lineWidth) {
    saveCurrentPolygonOffset(ren);
    setPolygonOffset(ren, calcPolygonOffset4CurvesBodies(lineWidth), glPolygonOffsetUnits);

    GL2 gl = ren.getGL();
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glTranslatef((float) c.x(), (float) c.y(), (float) c.z());
    glut.glutSolidSphere((float) r, 63, 20);
    gl.glPopMatrix();

    loadPolygonOffset(ren);
  }

  /**
   * Draw sphere without spherical triangle
   *
   * @param ren - object for drawing in OpenGL canvas
   * @param c - center sphere
   * @param r - radius sphere
   * @param points - vertex of spherical triangle
   */
  static public void drawSphereWithoutTriangle(Render ren, Vect3d c, double r, ArrayList<Vect3d> points) {
    GL2 gl = ren.getGL();
    GLUT glut = new GLUT();
    double[] equation = null;
    gl.glPushMatrix();
    gl.glTranslatef((float) c.x(), (float) c.y(), (float) c.z());

    gl.glEnable(GL2.GL_CLIP_PLANE0);
    equation = getCoefPlane(c, points.get(0), points.get(1));
    gl.glClipPlane(GL2.GL_CLIP_PLANE0, equation, 0);

    gl.glEnable(GL2.GL_CLIP_PLANE1);
    equation = getCoefPlane(c, points.get(0), points.get(2));
    gl.glClipPlane(GL2.GL_CLIP_PLANE0, equation, 0);

    gl.glEnable(GL2.GL_CLIP_PLANE2);
    equation = getCoefPlane(c, points.get(1), points.get(2));
    gl.glClipPlane(GL2.GL_CLIP_PLANE0, equation, 0);

    glut.glutSolidSphere((float) r, 63, 20);

    gl.glDisable(GL2.GL_CLIP_PLANE0);
    gl.glDisable(GL2.GL_CLIP_PLANE1);
    gl.glDisable(GL2.GL_CLIP_PLANE2);
    gl.glPopMatrix();
  }

  /**
   * Same as drawPolygon, only for three points
   *
   * @param ren object for drawing in OpenGL canvas
   * @param a 1st point
   * @param b 2nd point
   * @param c 3rd point
   * @param type Way to draw
   */
  static public void drawTriangle(Render ren, Vect3d a, Vect3d b, Vect3d c, TypeFigure type) {
    ArrayList<Vect3d> points = new ArrayList<>();
    points.add(a);
    points.add(b);
    points.add(c);
    drawPolygon(ren, points, type);
  }

  static public void drawTextOnSegment(Render ren, String str, Vect3d pointSegmentStart, Vect3d pointSegmentFinish) {

  }

  static public void drawTetrahedron(Render ren, ColorGL carcassColor,
          ColorGL facetColor, TypeFigure type, Vect3d a, Vect3d b, Vect3d c, Vect3d d) {
    GL2 gl = ren.getGL();
    if (type == TypeFigure.SOLID) {
      setObjectColor(ren, facetColor);
      gl.glBegin(GL2.GL_TRIANGLES);
      gl.glVertex3dv(a.getArray(), 0);
      gl.glVertex3dv(b.getArray(), 0);
      gl.glVertex3dv(c.getArray(), 0);
      gl.glVertex3dv(a.getArray(), 0);
      gl.glVertex3dv(b.getArray(), 0);
      gl.glVertex3dv(d.getArray(), 0);
      gl.glVertex3dv(a.getArray(), 0);
      gl.glVertex3dv(c.getArray(), 0);
      gl.glVertex3dv(d.getArray(), 0);
      gl.glVertex3dv(b.getArray(), 0);
      gl.glVertex3dv(c.getArray(), 0);
      gl.glVertex3dv(d.getArray(), 0);
      gl.glEnd();
    }
    setObjectColor(ren, carcassColor);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3dv(a.getArray(), 0);
    gl.glVertex3dv(b.getArray(), 0);
    gl.glVertex3dv(a.getArray(), 0);
    gl.glVertex3dv(c.getArray(), 0);
    gl.glVertex3dv(a.getArray(), 0);
    gl.glVertex3dv(d.getArray(), 0);
    gl.glVertex3dv(b.getArray(), 0);
    gl.glVertex3dv(c.getArray(), 0);
    gl.glVertex3dv(b.getArray(), 0);
    gl.glVertex3dv(d.getArray(), 0);
    gl.glVertex3dv(c.getArray(), 0);
    gl.glVertex3dv(d.getArray(), 0);
    gl.glEnd();
  }

  /**
   * Get coordinate of point in scene in window coordinates SIC: Can be properly
   * invoked only as a function of rendering scenes
   *
   * @param ren object for drawing in OpenGL canvas
   * @param obj point in scene
   * @param winCoord window coordinates [x, y]
   */
  /**
   * Convert the rotation matrix for use in OpenGL
   *
   * @param mat Rotation matrix
   * @return The rotation matrix for use in glMultMatrix.
   */
  static private double[] matrix2GLMatrix(Matrix3 mat) {
    double[][] mres = new double[3][3];
    // In Opengl matrix stored by columns
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        mres[i][j] = mat.get(j, i);
      }
    }

    // Transform into a 4x4 matrix, adding zeros all but the bottom right element to write one
    // and convert to array
    double[] res = new double[16];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        if (i < 3 && j < 3) {
          res[i * 4 + j] = mres[i][j];
        } else {
          if (i == 3 && j == 3) {
            res[i * 4 + j] = 1;
          } else {
            res[i * 4 + j] = 0;
          }
        }
      }
    }

    return res;
  }

  /**
   * Rotate object to pDirection point. Object is described by the center point
   * and direction vector. Rotate by GL matrix so that the vector 'vFace' were
   * directed from point 'pCenter' to point pDirection SIC: Function change the
   * OpenGL transformation matrix. You need to save GL transformation matrix
   * before using.
   *
   * @param gl Gl object with rotate method
   * @param vFace Vector perpendicular to the plane of the face, if object
   * center was in (0,0,0)
   * @param pCenter Point of the center of object
   * @param pDirection The point on which to rotate the object
   */
  static public void rotate(GL2 gl, Vect3d vFace, Vect3d pCenter, Vect3d pDirection) {
    Vect3d vCen2Dir = Vect3d.sub(pDirection, pCenter);
    Vect3d rotAx = Vect3d.vector_mul(vFace, vCen2Dir);
    if (Checker.isTwoPointsEqual(rotAx, Vect3d.O)) {
      rotAx = Vect3d.vector_mul(vFace, Vect3d.sum(vCen2Dir, new Vect3d(1, 1, 1)));
    }
    double angle = Vect3d.getAngle(vFace, vCen2Dir);
    double angleDegree = Angle3d.radians2Degree(angle);

    gl.glRotated(angleDegree, rotAx.x(), rotAx.y(), rotAx.z());
    //~ Matrix3 mat = getRotateMatrix(angle, rotAx.x(), rotAx.y(), rotAx.z());
    //~ gl.glMultMatrixd(matrix2GLMatrix(mat), 0);
  }

  /**
   * Get rotate matrix which uses by
   * {@link opengl.Drawer#rotate(com.jogamp.opengl.GL2, geom.Vect3d, geom.Vect3d, geom.Vect3d)}
   *
   * @param vFace Vector perpendicular to the plane of the face, if object
   * center was in (0,0,0)
   * @param pCenter Point of the center of object
   * @param pDirection The point on which to rotate the object
   * @return Rotation matrix
   */
  static public Matrix3 getRotateMatrix(Vect3d vFace, Vect3d pCenter, Vect3d pDirection) {
    Vect3d vCen2Dir = Vect3d.sub(pDirection, pCenter);
    Vect3d rotAx = Vect3d.vector_mul(vFace, vCen2Dir);
    double angle = Vect3d.getAngle(vFace, vCen2Dir);

    Matrix3 mat = Matrix3.getRotateMatrix(angle, rotAx.x(), rotAx.y(), rotAx.z());
    return mat;
  }

  /**
   * Draw a cone with a choice of ways to draw The cone is drawn from the zero
   * point (center of base is (0, 0, 0)) in the axis direction Z ( (0, 0, 1) is
   * normal vector for base )
   *
   * @param rad Radius of base
   * @param height Height of cone
   * @param type Way to draw
   */
  static private void drawDefaultCone(double rad, double height, TypeFigure type) {
    GLUT glut = new GLUT();
    switch (type) {
      case WIRE:
        glut.glutWireCone(rad, height, CONE_SLICES, CONE_STACKS);
        break;
      case SOLID:
        glut.glutSolidCone(rad, height, CONE_SLICES, CONE_STACKS);
        break;
    }
  }

  /**
   * Draw a cylinder with a choice of ways to draw The cylinder is drawn from
   * the zero point (center of first base is (0, 0, 0)) in the axis direction Z
   * ( (0, 0, 1) is normal vector for base )
   *
   * @param rad Radius of the first base
   * @param height Height of cylinder
   * @param type Way to draw
   */
  static private void drawDefaultCylinder(Render ren, double rad, double height, TypeFigure type) {
    GLUT glut = new GLUT();
    GLU glu = ren.getGLU();
    GLUquadric glUquadric = glu.gluNewQuadric();
    switch (type) {
      case WIRE:
        glut.glutWireCylinder(rad, height, CYLINDER_SLICES, CYLINDER_STACKS);
        break;
      case SOLID:
//        Now bases of cylinder drawing as anchors (separate object, not as part of cylinder)
//        glut.glutSolidCylinder(rad, heightLength, slices, stacks);
        glu.gluCylinder(glUquadric, rad, rad, height, CYLINDER_SLICES, CYLINDER_STACKS);
        break;
    }
  }

  /**
   * Draw the cone in 3d scene.
   *
   * @param ren object for drawing in OpenGL canvas
   * @param rad Radius of base
   * @param cenBot Center of base of cone
   * @param cenUp Apex of the cone
   * @param type Way to draw
   * @param lineWidth The width of the lines which are drawn on the object
   * @param drawBase
   */
  static public void drawCone(Render ren, double rad, Vect3d cenBot, Vect3d cenUp, TypeFigure type, float lineWidth, boolean drawBase) {
    GL2 gl = ren.getGL();
    saveCurrentPolygonOffset(ren);
    setPolygonOffset(ren, calcPolygonOffset4CurvesBodies(lineWidth), glPolygonOffsetUnits);
    // By default, the cone is drawn from the zero point in the axis direction Z
    Vect3d up = new Vect3d(0, 0, 1);
    Vect3d conUp = Vect3d.sub(cenUp, cenBot);
    gl.glPushMatrix();
    gl.glTranslated(cenBot.x(), cenBot.y(), cenBot.z());
    rotate(gl, up, cenBot, cenUp);
    drawDefaultCone(rad, conUp.norm(), type);
    gl.glPopMatrix();
    if (type == TypeFigure.SOLID && drawBase) {
      setPolygonOffset(ren, calcPolygonOffset4RibBodies(lineWidth), glPolygonOffsetUnits);
//      Circle now drawing as separate anchor
//      drawCircle(ren, rad, cenBot, conUp, type);
    }
    loadPolygonOffset(ren);
  }

  /**
   * Draw the cone in 3d scene by circle and two lines.
   *
   * @param ren object for drawing in OpenGL canvas
   * @param cone cone as math object
   * @throws geom.ExDegeneration
   */
  static public void drawConeCarcass(Render ren, Cone3d cone) throws ExDegeneration {
    // draw base
    Drawer.drawCircle(ren, cone.r(), cone.c(), cone.h(), TypeFigure.WIRE);
    // draw generators
    Vect3d eye = ren.getCameraPosition().eye();
    /* Проекция вершины конуса на плоскость основания конуса
     паралельно вектору "глаз - вершина конуса", если используется перспективная проекция.
     Для ортографической проекции вектор - "начало координат - глаз" */
    Vect3d projDirection;
    switch (ren.getProjection()) {
      case PERSPECTIVE_PROJECTION:
        projDirection = Vect3d.sub(cone.v(), eye);
        break;
      case ORTHO_PROJECTION:
        projDirection = eye;
        break;
      default:
        throw new AssertionError("Unsupported type of projection");
    }
    Vect3d projPoint = cone.basePlane().projectionOfPointAlongVect(cone.v(), projDirection);
    // Строятся точки касания для точки и окружности
    ArrayList<Vect3d> bottomPoints = cone.getBaseCircle().tangentPoints(projPoint);
    if (bottomPoints.size() == 2) {
      Drawer.drawSegment(ren, cone.v(), bottomPoints.get(0));
      Drawer.drawSegment(ren, cone.v(), bottomPoints.get(1));
    }
  }

  /**
   * Draw the cylinder in 3d scene
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param rad Radius of base
   * @param cenBot Center of the first base
   * @param cenUp Center of the second base
   * @param type Way to draw
   * @param lineWidth The width of the lines which are drawn on the object
   */
  static public void drawCylinder(Render ren, double rad, Vect3d cenBot, Vect3d cenUp, TypeFigure type, float lineWidth) {
    saveCurrentPolygonOffset(ren);
    setPolygonOffset(ren, calcPolygonOffset4CurvesBodies(lineWidth), glPolygonOffsetUnits);

    GL2 gl = ren.getGL();
    // By default, the cylinder is drawn from the zero point in the axis direction Z
    if (type == TypeFigure.SOLID) {
      Vect3d conUp = Vect3d.sub(cenUp, cenBot);
      gl.glPushMatrix();
      gl.glTranslated(cenBot.x(), cenBot.y(), cenBot.z());
      rotate(gl, Vect3d.UZ, cenBot, cenUp);
      drawDefaultCylinder(ren, rad, conUp.norm(), type);
      gl.glPopMatrix();
    } else if (type == TypeFigure.WIRE) {
      switch (ren.getProjection()) {
        case PERSPECTIVE_PROJECTION:
          drawCylinderCarcassWithPerspective(ren, rad, cenBot, cenUp);
          break;
        case ORTHO_PROJECTION:
          drawCylinderCarcassWithoutPerspective(ren, rad, cenBot, cenUp);
          break;
        default:
          throw new AssertionError("Unsupported type of projection");
      }
    }
    loadPolygonOffset(ren);
  }

  /**
   * Draw the cylinder in 3d scene by 2 circles and two lines.
   *
   * @param ren object for drawing in OpenGL cntr1
   * @param rad Radius of base
   * @param cen1 Center of 1st cylinder base
   * @param cen2 Center of 2nd cylinder base
   */
  static private void drawCylinderCarcassWithPerspective(Render ren, double rad, Vect3d cen1, Vect3d cen2) {
    Vect3d height = Vect3d.sub(cen2, cen1);
    // draw bases
    Drawer.drawCircle(ren, rad, cen1, height, TypeFigure.WIRE);
    Drawer.drawCircle(ren, rad, cen2, height, TypeFigure.WIRE);
    // draw generators
    // Строятся точки касания для точки и окружности
    ArrayList<Vect3d> generatorVertices = new ArrayList<>();
    try {
      generatorVertices.addAll(getVerticesOfCylinderGeneratorsFromOneBase(ren, rad, cen1, cen2));
      generatorVertices.addAll(getVerticesOfCylinderGeneratorsFromOneBase(ren, rad, cen2, cen1));
      // Отрисовка образующих. Если одно основание покрывает другое, образующие не изображаются
      if (generatorVertices.size() == 4) {
        // Выстраивание вершин в нужном порядке
        Vect3d first = generatorVertices.get(1);
        Vect3d.sortPlanarConvexSet(generatorVertices);
        Vect3d.rotatePointsList(generatorVertices, first);
        Drawer.drawSegment(ren, generatorVertices.get(0), generatorVertices.get(1));
        Drawer.drawSegment(ren, generatorVertices.get(2), generatorVertices.get(3));
      }
    } catch (ExDegeneration | ExZeroDivision ex) {

    }
  }

  static private void drawCylinderCarcassWithoutPerspective(Render ren, double rad, Vect3d cenBot, Vect3d cenUp) {
    Vect3d eye = ren.getCameraPosition().eye();
    Vect3d height = Vect3d.sub(cenUp, cenBot);
    Vect3d offset = Vect3d.vector_mul(height, eye);
    offset = Vect3d.mul(offset, rad / offset.norm());
    Drawer.drawSegment(ren, Vect3d.sum(cenUp, offset), Vect3d.sum(cenBot, offset));
    Drawer.drawSegment(ren, Vect3d.sub(cenUp, offset), Vect3d.sub(cenBot, offset));
  }

  /**
   * Находит лежащие в одном основании вершины образующих rad - радиус, сenBot и
   * cenUp - центры оснований цилиндра. Важен порядок передачи центров. Первый
   * центр (сenBot) считаем центром нижнего основания, второй (cenUp) -
   * верхнего.
   */
  static private ArrayList<Vect3d> getVerticesOfCylinderGeneratorsFromOneBase(
          Render ren, double rad, Vect3d cenBot, Vect3d cenUp) throws ExDegeneration {
    Vect3d height = Vect3d.sub(cenBot, cenUp);// высота цилиндра
    Vect3d eye = ren.getCameraPosition().eye();// глаз
    // Круг нижнеого основния
    Circle3d circleBot = new Circle3d(rad, height, cenBot);
    // Плоскость ижнего основания
    Plane3d planeBot = new Plane3d(height, cenBot);
    // Проекция глаза на плоскость нижного основания
    Vect3d eyeProj = planeBot.projectionOfPoint(eye);
    // Проводим точки касания из проекции глаза к окружности нижнего основания
    ArrayList<Vect3d> result = circleBot.tangentPoints(eyeProj);
    return result;
  }

  /**
   * Draw a circle with a choice of ways to draw. The circle is drawn from the
   * zero point in the axis direction Y ( (0, 1, 0) is normal vector )
   *
   * @param ren object for drawing in OpenGL canvas
   * @param rad Radius of circle
   * @param type Way to draw
   * @param numPoints Detailization of circle. More points give better result.
   */
  static private void drawDefaultCircle(Render ren, double rad, TypeFigure type, int numPoints) {
    double thetaStep = 2 * Math.PI / numPoints;
    double theta = 0;
    double phi = 0;
    ArrayList<Vect3d> points = new ArrayList<>();

    while (theta < 2 * Math.PI) {
      points.add(new Vect3d(
              rad * Math.sin(theta) * Math.cos(phi),
              rad * Math.sin(theta) * Math.sin(phi),
              rad * Math.cos(theta))
      );
      theta += thetaStep;
    }
    drawPolygon(ren, points, type);
  }

  /**
   * Draw circle in 3d scene
   *
   * @param ren object for drawing in OpenGL canvas
   * @param rad Radius of circle
   * @param center Center of circle
   * @param normal Normal vector
   * @param type Way to draw
   */
  static public void drawCircle(Render ren, double rad, Vect3d center, Vect3d normal, TypeFigure type) {
    double sizeVisible = ren.getViewVolume().getSizeVisible();
    // If circle isn't in view volume cube then don't drawing it.
    if (sizeVisible * Math.sqrt(3) + rad < center.norm()) {
      return;
    }
    // Calc number of points for circle
    double k = 100; // growth rate of the number of points depending from the scale
    double minNumPoints = 60; // Minimum number of points for drawing circle
    int numPoints = Math.min((int) (k * rad / ren.getCameraPosition().distance() + minNumPoints), 4000);
    GL2 gl = ren.getGL();
    // By default, the circle is drawn from the zero point in the axis direction Y
    Vect3d up = new Vect3d(0, 1, 0);
    gl.glPushMatrix();
    gl.glTranslated(center.x(), center.y(), center.z());
    rotate(gl, up, center, Vect3d.sum(center, normal));
    drawDefaultCircle(ren, rad, type, numPoints);
    gl.glPopMatrix();
  }

  /**
   * Draw circle sector in the plane Oxz from z axis to x axis. y axis is up.
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param rad Radius of circle
   * @param angle Angle of sector
   * @param indentAngle Angle at which it is necessary to retreat before drawing
   * sector
   */
  static private void drawDefaultCircleSection(Render ren, double rad, double angle,
          double indentAngle, TypeFigure type) {
    double theta = indentAngle;
    double phi = 0;
    ArrayList<Vect3d> points = new ArrayList<>();
    double finalAngle = angle + indentAngle;

    points.add(new Vect3d(0, 0, 0));
    while (theta < finalAngle) {
      points.add(new Vect3d(
              rad * Math.sin(theta) * Math.cos(phi),
              rad * Math.sin(theta) * Math.sin(phi),
              rad * Math.cos(theta))
      );
      theta += 0.1;
    }
    points.add(new Vect3d(
            rad * Math.sin(finalAngle) * Math.cos(phi),
            rad * Math.sin(finalAngle) * Math.sin(phi),
            rad * Math.cos(finalAngle)));
    drawPolygon(ren, points, type);
  }

  /**
   * Нарисовать сектор круга, заданный углом и радиусом.
   *
   * @param ren
   * @param angle
   * @param radius
   */
  public static void drawCircleSection(Render ren, Angle3d angle, double radius) {
    GL2 gl = ren.getGL();
    Vect3d up = new Vect3d(0, 1, 0);
    gl.glPushMatrix();
    gl.glTranslated(angle.vertex().x(), angle.vertex().y(), angle.vertex().z());
    rotate(gl, up, angle.vertex(), Vect3d.sum(angle.vertex(), angle.normal()));
    Matrix3 mat = Drawer.getRotateMatrix(up, angle.vertex(), Vect3d.sum(angle.vertex(), angle.normal()));
    Vect3d newBegin = mat.mulOnVect(new Vect3d(0, 0, 1));
    double addAngle = Vect3d.getAngle(newBegin, angle.side1());
    Vect3d checkNorm = Vect3d.vector_mul(newBegin, angle.side1());
    if (Vect3d.getAngle(angle.normal(), checkNorm) > Math.PI / 2) {
      addAngle *= -1;
    }

    drawDefaultCircleSection(ren, radius, angle.value(), addAngle, TypeFigure.SOLID);
    gl.glPopMatrix();
  }

  /**
   * Draw arc
   *
   * @param ren object for drawing in OpenGL canvas
   * @param arc math object arc
   * @throws ExDegeneration
   */
  static public void drawArc(Render ren, Arc3d arc, TypeFigure type) throws ExDegeneration {
    double delta;
    if (arc.value() < 1) {
      delta = arc.value() / 20;
    } else {
      delta = 0.05;
    }

    double currentValue = 0;
    ArrayList<Vect3d> points = new ArrayList<>();
    while (currentValue < arc.value()) {
      points.add(arc.getPoint(currentValue));
      currentValue += delta;
    }
    points.add(arc.getPoint(arc.value()));

    drawPolygon(ren, points, type);
  }

  /**
   * Draw parallelepiped
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param vert1 first vertex
   * @param vert2 second vertex
   * @param vert3 third vertex
   * @param vert4 fourth vertex
   * @param type Way to draw
   */
  static public void drawParallelepiped(Render ren,
          Vect3d vert1, Vect3d vert2, Vect3d vert3, Vect3d vert4, TypeFigure type) {
    Vect3d vert5 = Vect3d.sum(vert4, Vect3d.sub(vert2, vert1));
    Vect3d vert6 = Vect3d.sum(vert4, Vect3d.sub(vert3, vert1));
    Vect3d vert7 = Vect3d.sum(vert6, Vect3d.sub(vert2, vert1));
    Vect3d vert8 = Vect3d.sum(vert3, Vect3d.sub(vert2, vert1));
    if (type == TypeFigure.WIRE) {
      drawQuad(ren, vert1, vert2, vert8, vert3, TypeFigure.WIRE);
      drawQuad(ren, vert4, vert6, vert7, vert5, TypeFigure.WIRE);
      drawSegment(ren, vert1, vert4);
      drawSegment(ren, vert2, vert5);
      drawSegment(ren, vert3, vert6);
      drawSegment(ren, vert8, vert7);
    } else if (type == TypeFigure.SOLID) {
      drawQuad(ren, vert1, vert4, vert6, vert3, TypeFigure.SOLID);
      drawQuad(ren, vert1, vert2, vert5, vert4, TypeFigure.SOLID);
      drawQuad(ren, vert2, vert8, vert7, vert5, TypeFigure.SOLID);
      drawQuad(ren, vert3, vert6, vert7, vert8, TypeFigure.SOLID);
      drawQuad(ren, vert1, vert3, vert8, vert2, TypeFigure.SOLID);
      drawQuad(ren, vert4, vert6, vert7, vert5, TypeFigure.SOLID);
    }
  }

  /**
   * Draw a limited part of the plane with center in point
   *
   * @param ren object for drawing in OpenGL canvas
   * @param size size of drawing plane - length between center and edge
   * @param center Center point of plane
   * @param normal Normal vector of plane
   */
  static public void drawPlanePart(Render ren, double size, Vect3d center, Vect3d normal) {
    GL2 gl = ren.getGL();
    Vect3d up = new Vect3d(0, 0, 1);
    gl.glNormal3dv(up.getArray(), 0);
    gl.glPushMatrix();
    gl.glTranslated(center.x(), center.y(), center.z());
    rotate(gl, up, center, Vect3d.sum(center, normal));
    Drawer.drawQuad(ren,
            new Vect3d(-size, size, 0),
            new Vect3d(size, size, 0),
            new Vect3d(size, -size, 0),
            new Vect3d(-size, -size, 0),
            TypeFigure.SOLID);
    gl.glPopMatrix();
  }

  /**
   * Draw a plane (the size is only limited by visibility volume)
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param center Center point of plane (must be in visibility volume)
   * @param normal Normal vector of plane
   */
  static public void drawPlane(Render ren, Vect3d center, Vect3d normal) {
    drawPlane(ren, new Plane3d(normal, center));
  }

  /**
   * Draw a plane (the size is only limited by visibility volume)
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param plane Plane for drawing.
   */
  static public void drawPlane(Render ren, Plane3d plane) {
    double sizeVisible = ren.getViewVolume().getSizeVisible();
    drawPlanePart(ren, sizeVisible * 4, plane.pnt(), plane.n());
  }

  /**
   * Draws a rectangular piece of the plane that contains all the points of
   * intersection of this plane with the objects in the scene.
   * <br>If such points is not enough, the plane is drawn only limited by field
   * of view with using {@link #drawPlane(Render, geom.Vect3d, geom.Vect3d)}
   * function.
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param plane3d Plane for drawing
   * @param usingSmooth Use or not smoothing of edges
   * @param type Way to draw
   * @param indent Relative size of plane
   */
  static public void drawPlaneSmart(Render ren, Plane3d plane3d, boolean usingSmooth, TypeFigure type, double indent) {
    Editor editor = ren.getEditor();
    ArrayList<Vect3d> points = IntersectAnalyser.getIntersectionPoints(editor, plane3d);
    // Points of resulting rectangle(in some place of left and right edges)
    SmartPlane smartPlane = new SmartPlane(points, plane3d);
    // Check for degeneracy
    if (smartPlane.isCorrect()) {
      ArrayList<Vect3d> planePoints;
      if (usingSmooth) {
        planePoints = smartPlane.getAsSmoothRectangle(indent);
      } else {
        planePoints = smartPlane.getAsRectangle(indent);
      }

      if (type == TypeFigure.WIRE) {
        Drawer.drawPolygon(ren, planePoints, TypeFigure.WIRE);
      } else {
        // Drawing NOT convex polygon with using my handmade tessellation.
        // I know that in OpenGL tessellation already exist, but i'm a lazy ass.
        Vect3d center = Vect3d.conv_hull(planePoints.get(0), planePoints.get(1), 0.5);
        for (int i = 2; i < planePoints.size(); i++) {
          center = Vect3d.conv_hull(center, planePoints.get(i), 1.0 / (i + 1));
        }
        for (int i = 0; i < planePoints.size() - 1; i++) {
          Drawer.drawTriangle(ren, center, planePoints.get(i), planePoints.get(i + 1), TypeFigure.SOLID);
        }
        Drawer.drawTriangle(ren, center, planePoints.get(planePoints.size() - 1), planePoints.get(0), TypeFigure.SOLID);
      }
    } else {
      drawPlanePart(ren, indent, plane3d.pnt(), plane3d.n());
    }
  }

  /**
   * The same as
   * {@link #drawPlaneSmart(Render, geom.Plane3d, boolean, TypeFigure, double)},
   * with global indent
   */
  static public void drawPlaneSmart(Render ren, Plane3d plane3d, boolean usingSmooth, TypeFigure type) {
    drawPlaneSmart(ren, plane3d, usingSmooth, type, smartPlaneIndent);
  }

  /**
   * Draw a polygon on an arbitrary number of vertices. Points of polygon must
   * be in the same plane. Facet plane is considered the front if its vertices
   * appear counterclockwise.
   * <p>
   * <b>Unsafe</b>: Polygons on the same plane overlapping randomly
   *
   * @param ren object for drawing in OpenGL canvas
   * @param points An array of points, which will be built on polygon
   * @param type Way to draw
   */
  static private void drawPolygonUnsafe(Render ren, ArrayList<Vect3d> points, TypeFigure type) {
    GL2 gl = ren.getGL();
    int size = points.size();

    switch (type) {
      case WIRE:
        setZeroNormal(ren);
        gl.glBegin(GL2.GL_LINE_LOOP);
        break;
      case CURVE:
        setZeroNormal(ren);
        gl.glBegin(GL2.GL_LINE_STRIP);
        break;
      case SOLID:
        Vect3d norm = Vect3d.getNormal3Points(points.get(0), points.get(size / 3), points.get(2 * size / 3));
        gl.glNormal3dv(norm.getArray(), 0);
        gl.glBegin(GL2.GL_POLYGON);
        break;
      default:
        throw new RuntimeException("Wrong type of figure");
    }

    for (Vect3d point : points) {
      gl.glVertex3dv(point.getArray(), 0);
    }

    gl.glEnd();
  }

  /**
   * Draw curve by array of points. It's the same as call
   * {@link #drawPolygon(Render, ArrayList, TypeFigure)} with TypeFigure = CURVE
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param points An array of points, which will be used for built curve
   */
  static public void drawCurve(Render ren, ArrayList<Vect3d> points) {
    drawPolygonUnsafe(ren, points, TypeFigure.CURVE);
  }

  /**
   * Draw a polygon on an arbitrary number of vertices. Points of polygon must
   * be in the same plane. Facet plane is considered the front if its vertices
   * appear counterclockwise.
   * <br>Polygons are on the same plane will overlap in their order of draw
   * (with using {@link opengl.PlaneManager}).
   * <br>Use {@link #resetContext()} in the end of drawing scene.
   *
   * @param ren object for drawing in OpenGL canvas
   * @param points An array of points, which will be built on polygon
   * @param type Way to draw
   */
  static public void drawPolygon(Render ren, ArrayList<Vect3d> points, TypeFigure type) {
    if (type == TypeFigure.SOLID) {
      int size = points.size();
      // Solid polygon may contain 3 point and more
      if (size < 3) {
        return;
      }
      planeManager.addPolygon(points, ren);
      if (!planeManager.isOverlapped(points, ren)) {
        drawPolygonUnsafe(ren, points, type);
      }
    } else {
      drawPolygonUnsafe(ren, points, type);
    }
  }

  /**
   * Draw ellipse on given render.
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param el ellipse as math object
   * @param type Way to draw
   */
  static public void drawEllipse(Render ren, EllipseMain3d el, TypeFigure type) {
    ArrayList<Vect3d> points = new ArrayList<>();
    GL2 gl = ren.getGL();
//    gl.glPushMatrix();
//    gl.glTranslatef((float) el.center().x(), (float) el.center().y(), (float) el.center().z());
//    rotate(gl, Vect3d.UX, el.center(), el.f2());
    for (double i = 0; i < 2 * Math.PI; i += Math.abs(Math.sin(i)) * 0.1 + 0.01) {
//      points.add(el.getPointForDraw(i));
      points.add(el.getPoint(i));
    }
    drawPolygon(ren, points, type);
    gl.glPopMatrix();
  }

  /**
   * @param ren Object for drawing in OpenGL canvas
   * @param hyperbole hyperbole as math object
   * @param type Way to draw
   */
  static public void drawHyperbole(Render ren, Hyperbole3d hyperbole, TypeFigure type) {
    double index = ren.getViewVolume().getSizeVisible() * 2 / hyperbole.b();
    double k = Math.log(index + Math.sqrt(index * index + 1));
    ArrayList<Vect3d> leftBranchPoints = new ArrayList<>();
    ArrayList<Vect3d> rightBranchPoints = new ArrayList<>();
    for (double i = -k; i < k; i += Math.abs(i) * 0.1 + 0.01) {
      //правая ветка гиперболы
//      rightBranchPoints.add(hyperbole.getPointForDraw(i, true));
      rightBranchPoints.add(hyperbole.getPoint(i, true));
      //левая ветка гиперболы
//      leftBranchPoints.add(hyperbole.getPointForDraw(i, false));
      leftBranchPoints.add(hyperbole.getPoint(i, false));
    }
//    rightBranchPoints.add(hyperbole.getPointForDraw(k, true));
//    leftBranchPoints.add(hyperbole.getPointForDraw(k, false));
    rightBranchPoints.add(hyperbole.getPoint(k, true));
    leftBranchPoints.add(hyperbole.getPoint(k, false));

    GL2 gl = ren.getGL();
//    gl.glPushMatrix();
//    gl.glTranslatef((float) hyperbole.center().x(), (float) hyperbole.center().y(), (float) hyperbole.center().z());
//    rotate(gl, Vect3d.UX, hyperbole.center(), hyperbole.f2());
    drawPolygon(ren, leftBranchPoints, type);
    drawPolygon(ren, rightBranchPoints, type);
//    gl.glPopMatrix();
  }

  /**
   * @param ren Object for drawing in OpenGL canvas
   * @param parabola parabola as math object
   * @param type Way to draw
   */
  static public void drawParabola(Render ren, Parabola3d parabola, TypeFigure type) {
    ArrayList<Vect3d> points = new ArrayList<>();
    double k = ren.getViewVolume().getSizeVisible();
    for (double i = -k; i < k; i += Math.abs(i) * 0.1 + 0.01) {
//      points.add(parabola.getPointForDraw(i));
      points.add(parabola.getPoint(i));
    }
//    points.add(parabola.getPointForDraw(k));
    points.add(parabola.getPoint(k));
    GL2 gl = ren.getGL();
//    gl.glPushMatrix();
//    gl.glTranslatef((float) parabola.top().x(), (float) parabola.top().y(), (float) parabola.top().z());
//    rotate(gl, Vect3d.UY, parabola.top(), parabola.f());
    drawPolygon(ren, points, type);
//    gl.glPopMatrix();
  }
  
  /**
   * @param ren Object for drawing in OpenGL canvas
   * @param ElementaryFunction2d elementary function as math object
   * @param type Way to draw
   */
  static public void drawElementaryFunction(Render ren, ElementaryFunction2d elementaryFunction, TypeFigure type) {
    ArrayList<Vect3d> points = new ArrayList<>();
    double k = ren.getViewVolume().getSizeVisible();
    for (double i = -k; i < k; i += Math.abs(i) * 0.01 + 0.01) {
      points.add(elementaryFunction.getPoint(i));
    }
    points.add(elementaryFunction.getPoint(k));
    GL2 gl = ren.getGL();
    drawPolygon(ren, points, type);
  }


  /**
   * Draw a cone section
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param sect Cone section as math object
   * @param type Way to draw
   */
  static public void drawConeSection(Render ren, ConeSection3d sect, TypeFigure type) throws ExDegeneration {
    drawPolygon(ren, sect.getAsAbstractPolygon(), type);
  }

  /**
   * Draw a cylinder section
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param sect Cone section as math object
   * @param type Way to draw
   */
  static public void drawCylinderSection(Render ren, CylinderSection3d sect, TypeFigure type) throws ExDegeneration {
    drawPolygon(ren, sect.getAsAbstractPolygon(), type);
  }

  /**
   * Draw point in the scene
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   * @param pointSize Size of drawing point
   * @param alwaysVisible If true - point is drawn always, even when hidden by
   * other objects.
   * <br>If false - point is drawn only if not hidden by other objects.
   */
  static public void drawPoint(Render ren, Vect3d point, float pointSize, boolean alwaysVisible) {
    GL2 gl = ren.getGL();
    int[] currentDepthFunc = new int[1];
    gl.glPointSize(pointSize);
    if (!ren.isUseAntialiasing()) {
      SwitcherStateGL.saveAndEnable(ren, GL2.GL_ALPHA_TEST);
      gl.glAlphaFunc(GL2.GL_GREATER, (float) 0.5);
    }
    gl.glGetIntegerv(GL2.GL_DEPTH_FUNC, currentDepthFunc, 0);
    gl.glDepthFunc(GL2.GL_LEQUAL);
    if (alwaysVisible) {
      SwitcherStateGL.saveAndDisable(ren, GL2.GL_DEPTH_TEST);
    }

    gl.glBegin(GL2.GL_POINTS);
    gl.glVertex3dv(point.getArray(), 0);
    gl.glEnd();

    gl.glDepthFunc(currentDepthFunc[0]);
    if (alwaysVisible) {
      SwitcherStateGL.restore(ren, GL2.GL_DEPTH_TEST);
    }
    if (!ren.isUseAntialiasing()) {
      SwitcherStateGL.restore(ren, GL2.GL_ALPHA_TEST);
    }
  }

  /**
   * Draw point in the scene
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   * @param pointSize Size of drawing point
   */
  static public void drawPoint(Render ren, Vect3d point, float pointSize) {
    drawPoint(ren, point, pointSize, true);
  }

  /**
   * Draw point in the scene with default size
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   */
  static public void drawPoint(Render ren, Vect3d point) {
    drawPoint(ren, point, 6);
  }

  /**
   * Draw point of given size on the scene with frame.
   * <br>The inner part of the current point is drawn with the color specified
   * by {@link #setObjectColor(Render, ColorGL)}
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   * @param colorFrame Color of frame
   * @param pointSize Diameter of drawing point
   * @param frameSize Size of frame (Size of inner part = <i>pointSize</i> -
   * <i>frameSize</i> )
   */
  static public void drawPointWithFrame(Render ren, Vect3d point, float pointSize, float frameSize, ColorGL colorFrame) {
    ColorGL pointColor = getCurrentColor(ren);
    setObjectColor(ren, colorFrame);
    drawPoint(ren, point, pointSize);
    setObjectColor(ren, pointColor);
    drawPoint(ren, point, pointSize - frameSize);
  }

  /**
   * Draw point with default size on the scene with black frame.
   * <br>The inner part of the current point is drawn with the color specified
   * by {@link #setObjectColor(Render, ColorGL)}
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   * @param pointSize Diameter of drawing point
   */
  static public void drawPointWithFrame(Render ren, Vect3d point, float pointSize) {
    drawPointWithFrame(ren, point, pointSize, 2, ColorGL.BLACK);
  }

  /**
   * Draw point with default size on the scene with black frame.
   * <br>The inner part of the current point is drawn with the color specified
   * by {@link #setObjectColor(Render, ColorGL)}
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   */
  static public void drawPointWithFrame(Render ren, Vect3d point) {
    drawPointWithFrame(ren, point, 6);
  }

  /**
   * Get current color, sets by {@link #setObjectColor(Render, ColorGL)} )
   *
   * @param ren Object for drawing in OpenGL canvas
   * @return Current color
   */
  static public ColorGL getCurrentColor(Render ren) {
    GL2 gl = ren.getGL();
    double currentColor[] = new double[4];
    gl.glGetDoublev(GL2.GL_CURRENT_COLOR, currentColor, 0);
    return new ColorGL(currentColor, 4);
  }

  /**
   * Set color of OpenGL objects. All objects drawn after calling this function
   * will have the specified color. Every subsequent call of this function
   * cancels the previous action
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param color Color of drawing objects
   */
  static public void setObjectColor(Render ren, ColorGL color) {
    GL2 gl = ren.getGL();
    // Color overlay for background objects (for transparency of objects)
    if (allObjectsForcedColor) {
      double greyLevel = color.getAlpha();
      // TODO: delete this and set default alpha != 1 for all objects.
      if (greyLevel == 1.0) // By default alpha == 1 and objects are looking ugly.
      {
        greyLevel = 0.05;
      } else // Usually, people don't want to see black objects on the background.
      {
        greyLevel = greyLevel * 0.4;
      }
      gl.glColor4dv(new ColorGL(greyLevel, greyLevel, greyLevel, 0).getArray(), 0);
    } else {
      ColorGL alphaColor = new ColorGL(color.getArray(), 3);
      alphaColor.setColor(
              getAlphaColorComponent(color.getRed(), color.getAlpha()),
              getAlphaColorComponent(color.getGreen(), color.getAlpha()),
              getAlphaColorComponent(color.getBlue(), color.getAlpha())
      );
      // Set color in non-light mode
      gl.glColor3dv(alphaColor.getArray(), 0);
      // Set color in light mode
      // Attenuation of the effect of light on color of polygons
      double att = alphaColor.getRed() + alphaColor.getGreen() + alphaColor.getBlue();
      gl.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, (float) (2 + att));
      gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, alphaColor.getArrayFloat(), 0);
    }
  }

  static private double getAlphaColorComponent(double colorComponent, double alphaComponent) {
    return 1 - (1 - colorComponent) * alphaComponent;
  }

  static public void setObjectFacetColor(Render ren, EntityState state) {
    ColorGL facetColor;
    double intensity = 1;
    if (ren.isSceneIn3d()) {
      intensity = 0.98;
    }
    if (ren.getViewMode() == ViewMode.PENCIL) {
      facetColor = new ColorGL(intensity, intensity, intensity);
    } else {
      facetColor = ((ColorGL) state.getParam(DisplayParam.FILL_COLOR));
    }
    ColorGL chosenFacetsColor = facetColor.emphasize();
    if (state.isChosen()) {
      Drawer.setObjectColor(ren, chosenFacetsColor);
    } else {
      Drawer.setObjectColor(ren, facetColor);
    }
  }

  static public void setObjectCarcassColor(Render ren, EntityState state) {
    ColorGL carcassColor;
    if (ren.getViewMode() == ViewMode.PENCIL) {
      carcassColor = ColorGL.BLACK;
    } else {
      carcassColor = ((ColorGL) state.getParam(DisplayParam.CARCASS_COLOR));
    }
    ColorGL chosenCarcassColor = carcassColor.emphasize();
    if (state.isChosen()) {
      Drawer.setObjectColor(ren, chosenCarcassColor);
    } else {
      Drawer.setObjectColor(ren, carcassColor);
    }
  }

  static public void setObjectColorForAnchorPoints(Render ren, AnchorState anchorState) {
    ColorGL anchorColor;
    if (ren.getViewMode() == ViewMode.PENCIL) {
      anchorColor = ColorGL.BLACK;
    } else {
      anchorColor = (ColorGL) anchorState.getParam(DisplayParam.POINT_COLOR);
    }
    if (anchorState.isChosen()) {
      Drawer.setObjectColor(ren, anchorColor.emphasize());
    } else {
      Drawer.setObjectColor(ren, anchorColor);
    }
  }

  static public void setObjectColorForAnchorLines(Render ren, AnchorState anchorState) {
    ColorGL anchorColor;
    if (ren.getViewMode() == ViewMode.PENCIL) {
      anchorColor = ColorGL.BLACK;
    } else {
      anchorColor = (ColorGL) anchorState.getParam(DisplayParam.CARCASS_COLOR);
    }
    if (anchorState.isChosen()) {
      Drawer.setObjectColor(ren, anchorColor.emphasize());
    } else {
      Drawer.setObjectColor(ren, anchorColor);
    }
  }

  static public void setObjectColorForAnchorPolygons(Render ren, AnchorState anchorState) {
    ColorGL anchorColor;
    double intensity = 1;
    if (ren.isSceneIn3d()) {
      intensity = 0.98;
    }
    if (ren.getViewMode() == ViewMode.PENCIL) {
      anchorColor = new ColorGL(intensity, intensity, intensity);
    } else {
      anchorColor = (ColorGL) anchorState.getParam(DisplayParam.FILL_COLOR);
    }
    if (anchorState.isChosen()) {
      Drawer.setObjectColor(ren, anchorColor.emphasize());
    } else {
      Drawer.setObjectColor(ren, anchorColor);
    }
  }

  static public void setObjectColorForAnchorCircles(Render ren, AnchorState anchorState) {
    ColorGL anchorColor;
    if (ren.getViewMode() == ViewMode.PENCIL) {
      anchorColor = ColorGL.BLACK;
    } else {
      anchorColor = (ColorGL) anchorState.getParam(DisplayParam.CARCASS_COLOR);
    }
    if (anchorState.isChosen()) {
      Drawer.setObjectColor(ren, anchorColor.emphasize());
    } else {
      Drawer.setObjectColor(ren, anchorColor);
    }
  }

  /**
   * Check whether a point in the cube visibility (cube, beyond which nothing is
   * drawn) by using
   * {@link opengl.sceneparameters.ViewVolume#inViewingCube(geom.Vect3d)}
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   * @return true, if point in viewing cube
   */
  static public boolean inViewingCube(Render ren, Vect3d point) {
    return ren.getViewVolume().inViewingCube(point);
  }

  /**
   * Checks whether a point is in the volume of visibility by using
   * {@link opengl.sceneparameters.GluPerspectiveParameters}
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Tested point
   * @return true if visible
   */
  static public boolean inViewingVolume(Render ren, Vect3d point) {
    Vect3d cam2Zero = Vect3d.sub(new Vect3d(0, 0, 0), ren.getCameraPosition().eye());
    Vect3d cam2Point = Vect3d.sub(point, ren.getCameraPosition().eye());
    double alpha = Angle3d.radians2Degree(Vect3d.getAngle(cam2Point, cam2Zero));
    double viewingAngle = ren.getGluPerspectiveParameters().getFovy() / 2;
    double aspect = ren.getGluPerspectiveParameters().getAspect();
    if (aspect > 1) {
      viewingAngle *= aspect;
    }
    // If point is near origin
    if (Double.isNaN(alpha)) {
      return true;
    }
    return alpha <= viewingAngle;
  }

  /**
   * Checks point on viewing (point is viewing if point located in viewing cube
   * and in the field of view.) SIC: Function does not check for closing other
   * figures
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param point Coordinates of drawing point
   * @return true, if point is viewing
   */
  static public boolean isViewing(Render ren, Vect3d point) {
    return (inViewingCube(ren, point) && inViewingVolume(ren, point));
  }

  /**
   * Draw initial plane as grid. It is drawn in the plane Oxy with center at (0,
   * 0, 0)
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param numCells Number of cells from the center to the edge of the grid
   * @param meshSize The size of one cell
   */
  static public void drawInitialGrid(Render ren, int numCells, double meshSize) {
    for (int i = -numCells; i <= numCells; i++) {
      Vect3d startVertical = new Vect3d(-i * meshSize, -numCells * meshSize, 0);
      Vect3d finishVertical = new Vect3d(-i * meshSize, numCells * meshSize, 0);
      drawSegment(ren, startVertical, finishVertical);
      Vect3d startHorizontal = new Vect3d(-numCells * meshSize, i * meshSize, 0);
      Vect3d finishHorizontal = new Vect3d(numCells * meshSize, i * meshSize, 0);
      drawSegment(ren, startHorizontal, finishHorizontal);
    }
    double volume = ren.getViewVolume().getSizeVisible();
    drawQuad(ren, new Vect3d(-volume, -volume, 0),
            new Vect3d(-volume, volume, 0),
            new Vect3d(volume, volume, 0),
            new Vect3d(volume, -volume, 0), TypeFigure.WIRE);
  }

  /**
   * Draw initial plane as grid. It is drawn in the plane Oxy with center at (0,
   * 0, 0)
   *
   * @param ren Object for drawing in OpenGL canvas
   */
  static public void drawInitialGrid(Render ren) {
    drawInitialGrid(ren, ren.getInitialPlane().getNumCells(), ren.getInitialPlane().getMeshSize());
  }

  /**
   * Draw edges of view volume cube. It is drawn with center at (0, 0, 0)
   *
   * @param ren Object for drawing in OpenGL canvas
   */
  static public void drawViewVolumeCube(Render ren) {
    double volume = ren.getViewVolume().getSizeVisible();
    Drawer.drawParallelepiped(ren,
            new Vect3d(-volume, -volume, -volume),
            new Vect3d(-volume, -volume, volume),
            new Vect3d(-volume, volume, -volume),
            new Vect3d(volume, -volume, -volume), TypeFigure.WIRE);
  }

  /**
   * Drawing the hatch on the line
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param center Point where to place the hatch
   * @param vectLine Direction vector of the line on which the hatch
   */
  static public void drawHatch(Render ren, Vect3d center, Vect3d vectLine) {
    CameraPosition cam = ren.getCameraPosition();
    Vect3d center2eye = Vect3d.sub(cam.eye(), center);
    // Direction of hatching
    Vect3d hatchDir = Vect3d.getNormalizedVector(Vect3d.vector_mul(vectLine, center2eye));
    // Normalization factor for len of hatching relative to the distance from the center of the camera
    double k = cam.distance() * 0.005;
    hatchDir.inc_mul(k);
    Drawer.drawSegment(ren, Vect3d.sum(center, hatchDir), Vect3d.sub(center, hatchDir));
  }

  /**
   * Mark segment as median
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param center Point where to place the hatch
   * @param vectLine Direction vector of the line on which the hatch
   * @param mark Mark type
   */
  static public void drawMarkMedian(Render ren, Vect3d center, Vect3d vectLine, LineMark mark) {
    CameraPosition cam = ren.getCameraPosition();
    Vect3d normLine = Vect3d.getNormalizedVector(vectLine);
    // Indent size between strokes
    double sizeIndent = cam.distance() * medianHatchIndentMultiplier;
    Vect3d vectIndent = Vect3d.mul(normLine, sizeIndent);
    Vect3d vectHalfIndent = Vect3d.mul(vectIndent, 0.5);

    //ren.getGL().glLineWidth(5);
    Drawer.setObjectColor(ren, ColorGL.BLACK);

    switch (mark) {
      case NONE:
        break;
      case SINGLE:
        drawHatch(ren, center, vectLine);
        break;
      case DOUBLE:
        drawHatch(ren, Vect3d.sub(center, vectHalfIndent), vectLine);
        drawHatch(ren, Vect3d.sum(center, vectHalfIndent), vectLine);
        break;
      case V_STYLE:
        drawHatch(ren, Vect3d.sub(center, vectIndent), vectLine);
        drawHatch(ren, center, vectLine);
        drawHatch(ren, Vect3d.sum(center, vectIndent), vectLine);
        break;
      case W_STYLE:
        Vect3d vectOneAndHalfIndent = Vect3d.mul(vectIndent, 1.5);
        drawHatch(ren, Vect3d.sub(center, vectOneAndHalfIndent), vectLine);
        drawHatch(ren, Vect3d.sub(center, vectHalfIndent), vectLine);
        drawHatch(ren, Vect3d.sum(center, vectHalfIndent), vectLine);
        drawHatch(ren, Vect3d.sum(center, vectOneAndHalfIndent), vectLine);
        break;
    }
  }

  /**
   * draw mark of angle between plane and rib or line
   *
   * @param ren
   * @param plPerpBadge
   * @throws ExDegeneration
   */
//  static public void drawPlaneAngleMark(Render ren, PlaneAngleBadge plPerpBadge) throws ExDegeneration {
//    Angle3d ang = new Angle3d();
//    if (plPerpBadge.points().size() == 3) {
//      ang = new Angle3d(plPerpBadge.points().get(0),
//              plPerpBadge.points().get(1),
//              plPerpBadge.points().get(2));
//    } else if (plPerpBadge.points().size() == 2) {
//      Vect3d v1 = plPerpBadge.points().get(0);
//      Vect3d center = plPerpBadge.points().get(1);
//      Vect3d eye = ren.getCameraPosition().eye();
//      Vect3d v2 = Vect3d.sum(center, Vect3d.vector_mul(Vect3d.sub(v1, center), Vect3d.sub(eye, center)));
//      ang = new Angle3d(v1, center, v2);
//    }
//    double size = 0.5;
//    drawMarkOfRightAngle(ren, ang, size, TypeFigure.SOLID);
//  }
  /**
   * Draw arc mark of angle
   *
   * @param ren object for drawing in OpenGL canvas
   * @param ang angle as math object
   * @param size radius of arc
   * @param style style of arc
   * @param type
   * @throws ExDegeneration
   */
  static public void drawAngleMark(Render ren, Angle3d ang, double size, AngleStyle style,
          TypeFigure type) throws ExDegeneration {
    double sizeIndent = size * 0.1;
    switch (style) {
      case SINGLE:
        if (type == TypeFigure.WIRE || type == TypeFigure.CURVE) {
          drawArc(ren, new Arc3d(ang, size), TypeFigure.CURVE);
        } else if (type == TypeFigure.SOLID) {
          drawCircleSection(ren, ang, size);
        }
        break;
      case DOUBLE:
        if (type == TypeFigure.WIRE || type == TypeFigure.CURVE) {
          drawArc(ren, new Arc3d(ang, size), TypeFigure.CURVE);
          drawArc(ren, new Arc3d(ang, size - sizeIndent), TypeFigure.CURVE);
        } else if (type == TypeFigure.SOLID) {
          drawCircleSection(ren, ang, size);
        }
        break;
      case TRIPLE:
        if (type == TypeFigure.WIRE || type == TypeFigure.CURVE) {
          drawArc(ren, new Arc3d(ang, size), TypeFigure.CURVE);
          drawArc(ren, new Arc3d(ang, size - sizeIndent), TypeFigure.CURVE);
          drawArc(ren, new Arc3d(ang, size - sizeIndent * 2), TypeFigure.CURVE);
        } else if (type == TypeFigure.SOLID) {
          drawCircleSection(ren, ang, size);
        }
        break;
      case WAVED:
        //!! TODO: написать волнистый угол.
        /*
       GL2 gl = ren.getGL();
       gl.glPushMatrix();

       double[] controlPoints = new double[]{
       1, 0, 0, 0.7, 0.7, 0, 0, 1, 0
       };

       gl.glMap1d(GL2.GL_MAP1_VERTEX_3, 0.0, 1.0, 3, 3, controlPoints, 0);
       gl.glEnable(GL2.GL_MAP1_VERTEX_3);

       gl.glBegin(GL.GL_LINE_STRIP);
       for (int i = 0; i <= 100; i++)
       gl.glEvalCoord1d(i / 100.0d);
       gl.glEnd();
        
       gl.glPopMatrix();
       */
    }
  }

  /**
   * Mark of right angle by square
   *
   * @param ren object for drawing in OpenGL canvas
   * @param ang angle as math object
   * @param size size of square mark
   */
  static public void drawMarkOfRightAngle(Render ren, Angle3d ang, double size, TypeFigure type) {
    Vect3d vect1 = Vect3d.rNormalizedVector(ang.side1(), size);
    Vect3d vect2 = Vect3d.rNormalizedVector(ang.side2(), size);
    Vect3d start1 = Vect3d.sum(ang.vertex(), vect1);
    Vect3d start2 = Vect3d.sum(ang.vertex(), vect2);
    Vect3d finish = Vect3d.sum(start1, vect2);
    switch (type) {
      case SOLID:
        drawQuad(ren, ang.vertex(), start1, finish, start2, TypeFigure.SOLID);
        break;
      case WIRE:
        drawSegment(ren, start1, finish);
        drawSegment(ren, start2, finish);
    }
  }

  static public void drawPrism(Render ren, Prism3d prism, TypeFigure type) {
    try {
      Polygon3d[] bases = prism.base();
      int baseSize = prism.points().size() / 2;
      drawPolygon(ren, bases[0].points(), type);
      drawPolygon(ren, bases[1].points(), type);
      switch (type) {
        case SOLID:
          for (int i = 0; i < baseSize; i++) {
            drawQuad(ren,
                    prism.points().get(i),
                    prism.points().get((i + 1) % baseSize),
                    prism.points().get((i + 1) % baseSize + baseSize),
                    prism.points().get(i + baseSize), type);
          }
          break;
        case WIRE:
          for (int i = 0; i < baseSize; i++) {
            drawSegment(ren, prism.points().get(i), prism.points().get(i + baseSize));
          }
          break;
      }
    } catch (ExGeom ex) {
    }
  }

  static public void drawPyramid(Render ren, Pyramid3d pyr, TypeFigure type) {
    try {
      int baseSize = pyr.vertNumber() - 1;
      drawPolygon(ren, pyr.base().points(), type);
      switch (type) {
        case SOLID:
          for (int i = 0; i < baseSize; i++) {
            drawTriangle(ren, pyr.points().get(i), pyr.points().get((i + 1) % baseSize), pyr.top(), type);
          }
          break;
        case WIRE:
          for (int i = 0; i < baseSize; i++) {
            drawSegment(ren, pyr.points().get(i), pyr.top());
          }
          break;
      }
    } catch (ExGeom ex) {
    }
  }

  /**
   * Enable and disable of polygon stipple.
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param use true - enable, false - disable.
   */
  static public void usePolygonStipple(Render ren, boolean use) {
    if (use) {
      ren.getGL().glEnable(GL2.GL_POLYGON_STIPPLE);
    } else {
      ren.getGL().glDisable(GL2.GL_POLYGON_STIPPLE);
    }
  }

  static public boolean isPolygonStipple(Render ren) {
    return ren.getGL().glIsEnabled(GL2.GL_POLYGON_STIPPLE);
  }

  static public float getCurrentLineWidth(Render ren) {
    float[] lineWidth = new float[1];
    ren.getGL().glGetFloatv(GL2.GL_LINE_WIDTH, lineWidth, 0);
    return lineWidth[0];
  }

  static public float calcPolygonOffset4RibBodies(double lineWidth) {
    return (float) (0.5 * lineWidth + 0.5);
  }

  static public float calcPolygonOffset4CurvesBodies(double lineWidth) {
    return (float) (lineWidth + 1.5);
  }

  /**
   * FOR TEST USE ONLY! Draw triangle for test polygon offset with different
   * line width.
   *
   * @param render
   * @param type
   */
  static public void drawTestTriangle(Render render, TypeFigure type) {
    GL2 gl = render.getGL();
    Vect3d a = new Vect3d(-0.5, -0.5, 0);
    Vect3d b = new Vect3d(1.5, 0, 0);
    Vect3d c = new Vect3d(0, 1.5, 0);
    switch (type) {
      case SOLID:
        Vect3d m = Vect3d.centerOfMass(a, b, c);
        gl.glPolygonOffset(calcPolygonOffset4RibBodies(5), 1.0f);
        Drawer.drawTriangle(render, a, b, m, TypeFigure.SOLID);
        gl.glPolygonOffset(calcPolygonOffset4RibBodies(1), 1.0f);
        Drawer.drawTriangle(render, a, c, m, TypeFigure.SOLID);
        gl.glPolygonOffset(calcPolygonOffset4RibBodies(3), 1.0f);
        Drawer.drawTriangle(render, b, c, m, TypeFigure.SOLID);
        gl.glPolygonOffset(1.0f, 1.0f);
        break;
      case WIRE:
        Drawer.drawSegment(render, a, b, 5);
        Drawer.drawSegment(render, a, c, 1);
        Drawer.drawSegment(render, b, c, 3);
        break;
    }
  }

  static public void saveCurrentPolygonOffset(Render ren) {
    GL2 gl = ren.getGL();
    float[] params = new float[2];
    gl.glGetFloatv(GL2.GL_POLYGON_OFFSET_FACTOR, params, 0);
    gl.glGetFloatv(GL2.GL_POLYGON_OFFSET_UNITS, params, 1);
    glPolygonOffsetFactor = params[0];
    glPolygonOffsetUnits = params[1];
  }

  static public void loadPolygonOffset(Render ren) {
    ren.getGL().glPolygonOffset(glPolygonOffsetFactor, glPolygonOffsetUnits);
  }

  /**
   * Wrapper of glPolygonOffset
   *
   * @param ren Object for drawing in OpenGL canvas
   * @param factor Specifies a scale factor that is used to create a variable
   * depth offset for each polygon.
   * @param units Is multiplied by an implementation-specific value to create a
   * constant depth offset.
   * @see
   * <a href="https://www.khronos.org/opengles/sdk/docs/man/xhtml/glPolygonOffset.xml">glPolygonOffset</a>
   */
  static public void setPolygonOffset(Render ren, float factor, float units) {
    ren.getGL().glPolygonOffset(factor, units);
  }

  static public float getCurrentPolygonOffsetUnits(Render ren) {
    float[] params = new float[1];
    ren.getGL().glGetFloatv(GL2.GL_POLYGON_OFFSET_UNITS, params, 0);
    return params[0];
  }

  public static boolean isPlaneManagerEnabled() {
    return planeManager.isEnabled();
  }

  static public void setPlaneManagerEnabled(boolean enabled) {
    planeManager.setEnabled(enabled);
  }

  static public void setPlaneManagerReadOnlyEnabled(boolean enabled) {
    planeManager.setReadOnly(enabled);
  }

  static public List<List<DrawingPolygon>> getOverlappedPolygons() {
    return planeManager.getOverlappedPolygons();
  }

  static public void drawDodecahedron(Render ren, Dodecahedron3d dod, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < dod.ribs().size(); i++) {
      drawSegment(ren, dod.ribs().get(i).a(), dod.ribs().get(i).b());
    }
  }

  static public void drawTruncatedOctahedron(Render ren, TruncatedOctahedron3d dod, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < dod.ribs().size(); i++) {
      drawSegment(ren, dod.ribs().get(i).a(), dod.ribs().get(i).b());
    }
  }

  static public void drawElongatedDodecahedron(Render ren, ElongatedDodecahedron3d dod, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < dod.ribs().size(); i++) {
      drawSegment(ren, dod.ribs().get(i).a(), dod.ribs().get(i).b());
    }
  }

  static public void drawRhombicDodecahedron(Render ren, RhombicDodecahedron3d dod, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < dod.ribs().size(); i++) {
      drawSegment(ren, dod.ribs().get(i).a(), dod.ribs().get(i).b());
    }
  }

  static public void drawOctahedron(Render ren, Octahedron3d okt, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < okt.ribs().size(); i++) {
      drawSegment(ren, okt.ribs().get(i).a(), okt.ribs().get(i).b());
    }
  }

  static public void drawStellaOctahedron(Render ren, StellaOctahedron3d dod, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < dod.ribs().size(); i++) {
      drawSegment(ren, dod.ribs().get(i).a(), dod.ribs().get(i).b());
    }
  }

  static public void drawIcosahedron(Render ren, Icosahedron3d ic, TypeFigure type) throws ExDegeneration {
    for (int i = 0; i < ic.ribs().size(); i++) {
      Drawer.drawSegment(ren, ic.ribs().get(i).a(), ic.ribs().get(i).b());
    }
  }

  public static void drawEllipticParaboloidFacets(Render ren, EllipticParaboloid3d paraboloid) {
    // фомирование списка точек правой ветки образующей параболы
    double r = ren.getInitialPlane().getMeshSize();
    double maxPoints = 15;
    double znam = 1 + (maxPoints - 1) * 2;
    // шаг вращения полученных точек
    double angle = Math.PI / 20;
    // список точек орбазующей параболы
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    // список точек основания параболоида
    ArrayList<Vect3d> base = new ArrayList<Vect3d>();
    int in = 0;
    for (double i = 0.005; i < maxPoints * r; i += r * 1.2 * in / znam) {
      points.add(paraboloid.getPointForDraw(i, 0));
      in++;
    }
    points.add(paraboloid.getPointForDraw(maxPoints * r, 0));
    for(double j = 0; j < Math.PI * 2; j += angle) {
      base.add(points.get(0).rotate(j, Vect3d.UY));
    }
    // формирование матрицы преобразования
    GL2 gl = ren.getGL(); 
    gl.glPushMatrix();
    gl.glTranslated(paraboloid.top().x(), paraboloid.top().y(), 
            paraboloid.top().z());
    rotate(gl, Vect3d.UY, paraboloid.top(), paraboloid.f());
    //рисование основания и поверхности параболоида
    gl.glEnable(gl.GL_NORMALIZE);
    drawPolygon(ren, base, TypeFigure.SOLID);
    drawSurfaceOfRot(angle, ren, points, gl, Vect3d.UY);
    gl.glDisable(gl.GL_NORMALIZE);
    gl.glPopMatrix();
  }

  public static void drawEllipticParaboloid(Render ren, EllipticParaboloid3d paraboloid, TypeFigure type) {
    GL2 gl = ren.getGL();
    gl.glPushMatrix();
    gl.glTranslated(paraboloid.top().x(), paraboloid.top().y(), paraboloid.top().z());
    rotate(gl, Vect3d.UY, paraboloid.top(), paraboloid.f());
    ArrayList<Vect3d> points = new ArrayList<>();
    double k = ren.getViewVolume().getSizeVisible();
    for (double i = - k; i < k; i += Math.abs(i) * 0.1 + 0.01) {
      points.add(paraboloid.getPointForDraw(i, 0));
    }
    points.add(paraboloid.getPointForDraw(k, 0));
    drawPolygon(ren, points, type);
    gl.glPopMatrix();
  }
  
  public static void drawEllipsoid(Render ren, Ellipsoid3d ellipsoid, TypeFigure type) {
    ArrayList<Vect3d> points = new ArrayList<>();
    for (double i = 0; i < 2 * Math.PI; i += Math.abs(Math.sin(i)) * 0.1 + 0.05) {
      points.add(ellipsoid.getPoint(i, 0));
    }
    drawPolygon(ren, points, type);
  }

  static public void drawEllipsoidFacets(Render ren, Vect3d c, double r, double coef, Vect3d centr, Vect3d f) {
    GL2 gl = ren.getGL();
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glTranslatef((float) c.x(), (float) c.y(), (float) c.z());
    rotate(gl, Vect3d.UX, centr, f);
    gl.glScaled(coef, 1.0d, 1.0d);
    glut.glutSolidSphere((float) r, 70, 30);
    gl.glPopMatrix();
    loadPolygonOffset(ren);
  }

  public static void drawHyperboloidOfOneSheet(Render ren, HyperboloidOfOneSheet3d hyperboloid, TypeFigure type) throws ExDegeneration {
    double index = ren.getViewVolume().getSizeVisible() * 2 / hyperboloid.b();
    double k = Math.log(index + Math.sqrt(index * index + 1));
    GL2 gl = ren.getGL(); 
    gl.glPushMatrix();
    gl.glTranslated(hyperboloid.center().x(), hyperboloid.center().y(), 
            hyperboloid.center().z());
    rotate(gl, Vect3d.UY, hyperboloid.center(), hyperboloid.getPointImagynaryAxle());
      ArrayList<Vect3d> leftBranchPoints = new ArrayList<>();
      ArrayList<Vect3d> rightBranchPoints = new ArrayList<>();
      for (double i = -k; i < k; i += Math.abs(i) * 0.1 + 0.01) {
        //правая ветка
        rightBranchPoints.add(hyperboloid.getPoint(i, true, 0));
        //левая ветка
        leftBranchPoints.add(hyperboloid.getPoint(i, false, 0));
      }
      rightBranchPoints.add(hyperboloid.getPoint(k, true, 0));
      leftBranchPoints.add(hyperboloid.getPoint(k, false, 0));

      drawPolygon(ren, leftBranchPoints, type);
      drawPolygon(ren, rightBranchPoints, type);
    gl.glPopMatrix();
  }
  
  public static void drawHyperboloidOfOneSheetFacets(Render ren, HyperboloidOfOneSheet3d hyperboloid, TypeFigure type) throws ExDegeneration {
    // формирование списка точек, по которым строится правая ветка образующей гиперболы
    double index = ren.getViewVolume().getSizeVisible() * 2 / hyperboloid.b();
    double k = Math.log(index + Math.sqrt(index * index + 1));
    // шаг вращения полученных точек
    double ang = Math.PI / 20;
    ArrayList<Vect3d> Points = new ArrayList<>();
    for (double i = -k; i < k; i += Math.abs(i) * 0.1 + 0.05) {
      //правая ветка
      Points.add(hyperboloid.getPoint(i, true, 0));
    }
    Points.add(hyperboloid.getPoint(k, true, 0));
    // формирование матрицы преобразования
    GL2 gl = ren.getGL();
    gl.glPushMatrix();
    gl.glTranslated(hyperboloid.center().x(), hyperboloid.center().y(),
             hyperboloid.center().z());
    rotate(gl, Vect3d.UY, hyperboloid.center(), hyperboloid.getPointImagynaryAxle());
    // рисование поверхности однополстного гиперболоида
    gl.glEnable(gl.GL_NORMALIZE);
    drawSurfaceOfRot(ang, ren, Points, gl, Vect3d.UY);
    gl.glDisable(gl.GL_NORMALIZE);
    gl.glPopMatrix();
  }
  
  

  public static void drawHyperboloidOfTwoSheet(Render ren, HyperboloidOfTwoSheet3d hyperboloid, TypeFigure type) {
    double index = ren.getViewVolume().getSizeVisible() * 2 / hyperboloid.b();
    double k = Math.log(index + Math.sqrt(index * index + 1));
    GL2 gl = ren.getGL();
    gl.glPushMatrix();
    gl.glTranslated( hyperboloid.center().x(), hyperboloid.center().y(),
            hyperboloid.center().z());
    rotate(gl, Vect3d.UX, hyperboloid.center(), hyperboloid.f1());
    ArrayList<Vect3d> leftBranchPoints = new ArrayList<>();
    ArrayList<Vect3d> rightBranchPoints = new ArrayList<>();
    for (double i = -k; i < k; i += Math.abs(i) * 0.1 + 0.01) {
      //правая ветка гиперболы
      rightBranchPoints.add(hyperboloid.getPoint(i, true, 0));
      //левая ветка гиперболы
      leftBranchPoints.add(hyperboloid.getPoint(i, false, 0));
    }
    rightBranchPoints.add(hyperboloid.getPoint(k, true, 0));
    leftBranchPoints.add(hyperboloid.getPoint(k, false, 0));

    drawPolygon(ren, leftBranchPoints, type);
    drawPolygon(ren, rightBranchPoints, type);
    gl.glPopMatrix();
  }
  
  public static void drawHyperboloidOfTwoSheetFacets(Render ren, HyperboloidOfTwoSheet3d hyperboloid) throws ExDegeneration {
    // формирование списков точек, по которым строятся нижние половины обоих ветвей образующей гиперболы
    double index = ren.getViewVolume().getSizeVisible() * 2 / hyperboloid.b();
    double k = Math.log(index + Math.sqrt(index * index + 1));
    // шаг вращения полученных точек
    double angle = Math.PI / 20;
    // списки точек оснований 
    ArrayList<Vect3d> baseL = new ArrayList<>();
    ArrayList<Vect3d> baseR = new ArrayList<>();
    // списки точек образующей гиперболы
    ArrayList<Vect3d> leftBranchPoints = new ArrayList<>();
    ArrayList<Vect3d> rightBranchPoints = new ArrayList<>();
    for (double i = 0.01; i <= k; i += Math.abs(i) * 0.1 + 0.01) {
      //правая ветка гиперболы
      rightBranchPoints.add(hyperboloid.getPoint(i, true, 0));
      //левая ветка гиперболы
      leftBranchPoints.add(hyperboloid.getPoint(i, false, 0));
    }
    rightBranchPoints.add(hyperboloid.getPoint(k, true, 0));
    //левая ветка гиперболы
    leftBranchPoints.add(hyperboloid.getPoint(k, false, 0));
    for(double j = 0; j < Math.PI * 2; j += angle) {
      baseL.add(leftBranchPoints.get(0).rotate(j, Vect3d.UX));
      baseR.add(rightBranchPoints.get(0).rotate(j, Vect3d.UX));
    }
    // формирование матрицы преобразования
    GL2 gl = ren.getGL(); 
    gl.glPushMatrix();
    gl.glTranslated(hyperboloid.center().x(), hyperboloid.center().y(), 
            hyperboloid.center().z());
    rotate(gl, Vect3d.UX, hyperboloid.center(), hyperboloid.f1()); 
    // рисование оснований и поверхностей у двуполостного гиперболоида
    drawPolygon(ren, baseL, TypeFigure.SOLID);
    drawPolygon(ren, baseR, TypeFigure.SOLID);
    gl.glEnable(gl.GL_NORMALIZE);
    drawSurfaceOfRot(angle, ren, rightBranchPoints, gl, Vect3d.UX);
    drawSurfaceOfRot(angle, ren, leftBranchPoints, gl, Vect3d.UX);
    gl.glDisable(gl.GL_NORMALIZE);
    gl.glPopMatrix();
  }
  /**
   * Draw Surface of rotation
   * @param ang step for rotation points
   * @param ren object for drawing in OpenGL canvas
   * @param points forming line (curve)
   * @param gl changes in canvas
   * @param axis of rotation
   */
  public static void drawSurfaceOfRot(double ang, Render ren, ArrayList<Vect3d> points, GL2 gl, Vect3d axis) {
    for (int i = 0; i < points.size() - 1; i++) {
      gl.glBegin(GL2.GL_QUAD_STRIP);
      for (double j = 0; j < Math.PI * 2; j += ang) {
        Vect3d rotP1 = points.get(i).rotate(j, axis);
        Vect3d rotP2 = points.get(i + 1).rotate(j, axis);
        Vect3d rotP3 = points.get(i).rotate(j + ang, axis);
        Vect3d normalQuads = Vect3d.getNormal3Points(rotP1, rotP2, rotP3);
        gl.glNormal3f((float)normalQuads.x(), (float)normalQuads.y(), (float)normalQuads.z());
        gl.glVertex3d(rotP1.x(), rotP1.y(), rotP1.z());
        gl.glVertex3d(rotP2.x(), rotP2.y(), rotP2.z());
      }
      gl.glVertex3d(points.get(i).x(), points.get(i).y(), points.get(i).z());
      gl.glVertex3d(points.get(i + 1).x(), points.get(i + 1).y(), points.get(i + 1).z());
      gl.glEnd();
    }
  }

  static public void drawMenger(Render ren, MengerSponge m, TypeFigure type) {
    ArrayList<Vect3d> p = new ArrayList<>();
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m.length; j++) {
        for (int k = 0; k < m.length; k++) {
          if (m.b[i][j][k]) {
            double step = 1.0 / m.length;
            Vect3d a = new Vect3d(step * i, step * j, step * k);
            Vect3d b = new Vect3d(step * i, step * (j + 1), step * k);
            Vect3d c = new Vect3d(step * (i + 1), step * (j + 1), step * k);
            Vect3d d = new Vect3d(step * (i + 1), step * j, step * k);
            Vect3d a1 = new Vect3d(step * i, step * j, step * (k + 1));
            Vect3d b1 = new Vect3d(step * i, step * (j + 1), step * (k + 1));
            Vect3d c1 = new Vect3d(step * (i + 1), step * (j + 1), step * (k + 1));
            Vect3d d1 = new Vect3d(step * (i + 1), step * j, step * (k + 1));

            p.clear();
            p.add(a);
            p.add(b);
            p.add(c);
            p.add(d);
            drawPolygonUnsafe(ren, p, type);

            p.clear();
            p.add(a);
            p.add(b);
            p.add(b1);
            p.add(a1);
            drawPolygonUnsafe(ren, p, type);

            p.clear();
            p.add(b);
            p.add(c);
            p.add(c1);
            p.add(b1);
            drawPolygonUnsafe(ren, p, type);

            p.clear();
            p.add(c);
            p.add(d);
            p.add(d1);
            p.add(c1);
            drawPolygonUnsafe(ren, p, type);

            p.clear();
            p.add(d);
            p.add(a);
            p.add(a1);
            p.add(d1);
            drawPolygonUnsafe(ren, p, type);

            p.clear();
            p.add(a1);
            p.add(b1);
            p.add(c1);
            p.add(d1);
            drawPolygonUnsafe(ren, p, type);
          }
        }
      }
    }
  }
}
