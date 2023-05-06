package opengl.scenegl;

import opengl.Drawer;
import opengl.SwitcherStateGL;
import opengl.drawing.DrawingQueue;
import editor.Editor;
import geom.Plane3d;
import geom.Vect3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;
import opengl.sceneparameters.ViewMode;

/**
 * Created by maxbrainrus on 8/14/14.
 */
public class Scene2d extends SceneEdt {
  static final double EPS_GL = 0.001;

  public Scene2d(Editor editor){
    this(editor, new CameraPosition(new Vect3d(0, 0, 1), new Vect3d(0, 0, 0)));
  }
  public Scene2d(Editor editor, CameraPosition cameraPosition){
    super(editor, cameraPosition);
    render.setViewMode(ViewMode.DOTTED_LINE);
    viewPlane = new Plane3d(new Vect3d(0, 0, 1), new Vect3d(0, 0, 0));
    render.setSceneIn3d(false);
    //setProjection(ProjectionMode.ORTHO_PROJECTION);
  }

  public Plane3d getViewPlane() { return viewPlane; }

  @Override
  protected void initPerspectiveParameters() {
    GluPerspectiveParameters perPar = render.getGluPerspectiveParameters();
    CameraPosition cam = render.getCameraPosition();
    perPar.setzNear(cam.distance() - EPS_GL);
    perPar.setzFar(cam.distance() + EPS_GL);
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    super.display(drawable);

    render.getViewVolume().setСlippingPlanes(render);

    drawObjects();
    drawAxesAndInitialGrid();
    drawViewVolumeCube();
    drawAdditionalElements();

    Drawer.saveTransformMatrix(render);
    render.getViewVolume().disableClippingPlanes(render);
  }

  @Override
  public void drawObjects() {
    Drawer.setPlaneManagerEnabled(false);
    GL2 gl = render.getGL();
    switch (render.getViewMode()) {
      case LIGHT:
        gl.glEnable(GL2.GL_LIGHTING);
        break;
      case DOTTED_LINE:
        gl.glDisable(GL2.GL_LIGHTING);
        break;
      default:
        break;
    }

    if (render.isUseAntialiasing()) {
      gl.glEnable(GL2.GL_LINE_SMOOTH);
      gl.glEnable(GL2.GL_POLYGON_SMOOTH);
      gl.glEnable(GL2.GL_BLEND);
    } else {
      gl.glDisable(GL2.GL_LINE_SMOOTH);
      gl.glDisable(GL2.GL_POLYGON_SMOOTH);
      gl.glDisable(GL2.GL_BLEND);
    }

    gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ZERO);

    // Рисуем дополнительные элементы
    DrawingQueue.drawLowPriority(render);

    SwitcherStateGL.saveAndDisable(render, GL2.GL_DEPTH_TEST);
    drawFaces();
    SwitcherStateGL.restore(render, GL2.GL_DEPTH_TEST);

    if (isUseAntialiasing())
      gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
    SwitcherStateGL.saveAndDisable(render, GL2.GL_DEPTH_TEST);
    drawCarcasses();
    SwitcherStateGL.restore(render, GL2.GL_DEPTH_TEST);
  }

  Plane3d viewPlane;

    @Override
    public SceneType getSceneType() {
        return SceneType.Scene2D;
    }
}