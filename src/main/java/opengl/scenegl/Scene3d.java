package opengl.scenegl;

import opengl.Drawer;
import editor.Editor;
import geom.Vect3d;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;

/**
 * Scene3d drawing objects
 *
 * @see <a href=http://forum.vingrad.ru/forum/topic-161042/kw-opengl-java-jogl.html>Create JOGL application: Tutorial</a>
 */
public class Scene3d extends SceneEdt {

  public Scene3d(Editor edt) {
    super(edt);
  }

  public Scene3d(Editor edt, CameraPosition cameraPosition){
    super(edt, cameraPosition);
  }

  @Override
  public boolean is3d() {
    return true;
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    super.init(drawable);
    GL2 gl = render.getGL();

    // Background light
    float lmodel_ambient[] = {1.0f, 1.0f, 1.0f, 1f};
    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
    gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);

    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
  }

  @Override
  protected void initPerspectiveParameters() {
    GluPerspectiveParameters perPar = render.getGluPerspectiveParameters();
    CameraPosition cam = getCameraPosition();
    double fitCamDist = perPar.getFitCamDist(this.render.getWidth(), this.render.getHeight());
    perPar.setzFar(fitCamDist * cam.distance());
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    super.display(drawable);
    GL2 gl = render.getGL();
    CameraPosition cam = getCameraPosition();

    render.getViewVolume().setСlippingPlanes(render);

    // Light source (illuminant)
    Vect3d eye = cam.eye();
    float light_position[] = {(float)eye.x(), (float)eye.y(), (float)eye.z(), 1.0f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

    drawObjects();
    gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
    drawAxesAndInitialGrid();
    drawViewVolumeCube();
    drawAdditionalElements();

    Drawer.saveTransformMatrix(render);
    render.getViewVolume().disableClippingPlanes(render);
  }

  @Override
  public SceneType getSceneType() {
    return SceneType.Scene3D;
  }
}