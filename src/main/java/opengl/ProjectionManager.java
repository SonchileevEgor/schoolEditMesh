package opengl;

import geom.Vect3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;

/**
 * Содержит методы для устанвки матрицы проекции, используя параметры из {@link opengl.Render}
 */
public class ProjectionManager {
  private ProjectionManager(){}

  /**
   * Set perspective projection matrix in OpenGL state.
   * <br>After calling this function sets glMatrixMode to  GL_MODELVIEW
   * @param ren Drawing context
   */
  public static void setPerspectiveProjection(Render ren){
    GL2 gl = ren.getGL();
    GLU glu = new GLU();
    GluPerspectiveParameters gluPerParam = ren.getGluPerspectiveParameters();
    CameraPosition cam = ren.getCameraPosition();
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(
            gluPerParam.getFovy(),
            gluPerParam.getAspect(),
            gluPerParam.getzNear(),
            gluPerParam.getzFar()
    );
    glu.gluLookAt(cam.eye().x(), cam.eye().y(), cam.eye().z(),
            cam.center().x(), cam.center().y(), cam.center().z(),
            cam.up().x(), cam.up().y(), cam.up().z());
    gl.glMatrixMode(GL2.GL_MODELVIEW);

  }

  /**
   * Set orthographic projection matrix in OpenGL state.
   * <br>After calling this function sets glMatrixMode to GL_MODELVIEW.
   * @param ren Drawing context
   */
  public static void setOrthoProjection(Render ren){
    GL2 gl = ren.getGL();
    GLU glu = new GLU();
    GluPerspectiveParameters gluPerParam = ren.getGluPerspectiveParameters();
    CameraPosition cam = ren.getCameraPosition();
    double camDistance = cam.distance();
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    gl.glOrtho(gluPerParam.getLeft(camDistance),
            gluPerParam.getRight(camDistance),
            gluPerParam.getBottom(camDistance),
            gluPerParam.getTop(camDistance),
            gluPerParam.getzNear(),
            gluPerParam.getzFar());

    Vect3d eye = cam.eye();
    Vect3d center = cam.center();
    Vect3d up = cam.up();

    glu.gluLookAt(eye.x(), eye.y(), eye.z(),
                  center.x(), center.y(), center.z(),
                  up.x(), up.y(), up.z());

    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }
}
