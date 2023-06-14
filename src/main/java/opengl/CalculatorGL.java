package opengl;

import geom.ExDegeneration;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import opengl.sceneparameters.CameraPosition;

/**
 * Functions for calculating coordinates in OpenGL context.
 */
public class CalculatorGL {
  /**
   * Получить плоскость с вектором нормали, равным вектору взгляда, содержащая данную точку.
   * @param ren Контекст сцены
   * @param point Точка, которую содержит построенная плоскость
   * @return Плоскость с вектором нормали, равным вектору взгляда, содержащая данную точку.
   */
  static public Plane3d getPlaneByPoint(Render ren, Vect3d point) {
    CameraPosition cam = ren.getCameraPosition();
    return new Plane3d(Vect3d.sub(cam.eye(), cam.center()), point);
  }

  /**
   * Get sight line by X, Y coords on screen.
   * @param render opengl context.
   * @param xCoord X coord on screen.
   * @param yCoord Y coord on screen.
   * @param flipY Use or not flip y coordinate. If true y takes values 0->height from up to bottom, else from bottom to up.
   * @return Line of sight which contains given point
   * @throws ExDegeneration if can't construct line.
   */
  static public Ray3d getSightRay(Render render, double xCoord, double yCoord, boolean flipY) throws ExDegeneration {
    render.getDrawable().getContext().makeCurrent();
    GL2 gl = render.getGL();
    double[] model = new double[16];
    double[] projection = new double[16];
    int[] viewport = new int[16];

    gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, model, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);

    double[] point1 = new double[16];
    double[] point2 = new double[16];

    double fixedYCoord;
    if (flipY)
      fixedYCoord = render.getHeight() - yCoord;
    else
      fixedYCoord = yCoord;

    render.getGLU().gluUnProject(xCoord, fixedYCoord, 0, model, 0, projection, 0, viewport, 0, point1, 0);
    render.getGLU().gluUnProject(xCoord, fixedYCoord, 1, model, 0, projection, 0, viewport, 0, point2, 0);
    render.getDrawable().getContext().release();

    return Ray3d.ray3dByTwoPoints(new Vect3d(point1[0], point1[1], point1[2]),
        new Vect3d(point2[0], point2[1], point2[2]));
  }

  /**
   * The same as {@link #getSightRay(Render, double, double, boolean)} with flip of y coord.
   */
  static public Ray3d getSightRay(Render render, double xCoord, double yCoord) throws ExDegeneration {
    return getSightRay(render, xCoord, yCoord, true);
  }

  /**
   * Get coordinates of point in scene in window coordinates
   *
   * @param render   opengl context
   * @param obj      point in scene
   * @param winCoord window coordinates [x, y]
   */
  static public void getDisplayCoord(Render render, Vect3d obj, double[] winCoord) {
    GLAutoDrawable drawable = render.getDrawable();
    drawable.getContext().makeCurrent();
    GL2 gl = render.getGL();
    double[] model = new double[16];
    double[] projection = new double[16];
    int[] viewport = new int[16];

    gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, model, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);

    double[] winPos = new double[16];
    render.getGLU().gluProject(obj.x(), obj.y(), obj.z(), model, 0, projection, 0, viewport, 0, winPos, 0);

    winCoord[0] = winPos[0];
    winCoord[1] = winPos[1];
    drawable.getContext().release();
  }

  static public Vect3d addIndentForPoint(Render ren, Vect3d point, int indent) throws ExDegeneration {
    double[] winCoord = new double[2];
    getDisplayCoord(ren, point, winCoord);
    winCoord[0] += indent; // x
    winCoord[1] += indent; // y
    ArrayList<Vect3d> points = getPlaneByPoint(ren, point).intersect(getSightRay(ren, winCoord[0], winCoord[1], false));
    if (points.isEmpty()) {
      throw new ExDegeneration("");
    }
    return points.get(0);
  }
}
