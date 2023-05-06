package opengl.sceneparameters;

import geom.Checker;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Created by maxbrainrus on 8/14/14.
 */
@Disabled
public class GluPerspectiveParametersTest {

  @Test
  public void testConversion(){
    GluPerspectiveParameters gluPerParam = new GluPerspectiveParameters();

    GLProfile glp = GLProfile.getDefault();
    GLCapabilities caps = new GLCapabilities(glp);
    GLCanvas canvas = new GLCanvas(caps);
    TestScene testScene = new TestScene(gluPerParam);
    canvas.addGLEventListener(testScene);

    canvas.setVisible(true);

    Frame frame = new Frame("AWT Window Test");
    frame.setSize(800, 800);
    frame.add(canvas);
    frame.setVisible(true);

    frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    });
    try{
      boolean notValid = true;
      while(notValid){
        Thread.sleep(20);
        for (int i = 0; i < 16; i++) {
          if (testScene.getProjection1()[i] != 0 || testScene.getProjection2()[i] != 0)
            notValid = false;
        }
      }
    }
    catch (InterruptedException ex){
      throw new RuntimeException(ex.getMessage());
    }
    for (int i = 0; i < 16; i++) {
      assertEquals(testScene.getProjection1()[i],
                   testScene.getProjection2()[i], Checker.eps(),
                   String.format("Element %d ", i));
    }
  }
}

class TestScene implements GLEventListener{
  GluPerspectiveParameters gluPerParam;
  double[] projection1 = new double[16];
  double[] projection2 = new double[16];

  public double[] getProjection1() {

    return projection1;
  }

  public double[] getProjection2() {
    return projection2;
  }

  TestScene(GluPerspectiveParameters gluPerParam){
    this.gluPerParam = gluPerParam;
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    GLU glu = new GLU();

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(
            gluPerParam.getFovy(),
            gluPerParam.getAspect(),
            gluPerParam.getzNear(),
            gluPerParam.getzFar()
    );
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection1, 0);
    gl.glLoadIdentity();
    gl.glFrustum(gluPerParam.getLeft(),
            gluPerParam.getRight(),
            gluPerParam.getBottom(),
            gluPerParam.getTop(),
            gluPerParam.getzNear(),
            gluPerParam.getzFar()
            );

    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection2, 0);

  }

  @Override
  public void dispose(GLAutoDrawable drawable) {

  }

  @Override
  public void display(GLAutoDrawable drawable) {

  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

  }
}
