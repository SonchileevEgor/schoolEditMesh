package gui;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import opengl.i_RotationListener;
import opengl.i_ScaleChangeListener;
import opengl.scenegl.SceneGL;

/**
 * GLCanvas wrapper extended for application needs.
 *
 * @author alexeev
 */
public class BasicEdtCanvas extends GLJPanel implements i_RotationListener, i_ScaleChangeListener {
  protected SceneGL _scene;

  public BasicEdtCanvas() {
    super(new GLCapabilities(GLProfile.getDefault()));
    addGLEventListener(_scene);
    setFocusable(true);
  }

  public SceneGL getScene() {
    return _scene;
  }

  public void setScene(SceneGL s) {
    if( _scene != null ){
      _scene.getRender().removeDisplayListener(this);
      _scene.getRender().removeRotationListener(this);
    }
    removeGLEventListener(_scene);
    _scene = s;
    addGLEventListener(_scene);
    _scene.getRender().addDisplayListener(this);
    _scene.getRender().addRotationListener(this);
  }

  public void redraw() {
    display();
  }

  @Override
  public void sceneRotated() {
    display();
  }

  @Override
  public void scaleChanged(double distance, double meshSize) {
    display();
  }
}
