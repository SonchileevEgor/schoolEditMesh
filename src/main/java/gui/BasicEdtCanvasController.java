package gui;

import geom.ExDegeneration;
import geom.Ray3d;
import geom.Vect3d;
import gui.mode.adapters.RotationMouseAdapter;
import gui.mode.adapters.ScalingMouseAdapter;
import gui.mode.i_ModeChangeListener;
import java.awt.Cursor;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import opengl.CalculatorGL;
import opengl.Render;
import opengl.scenegl.SceneGL;

/**
 * Controller for BasicEdtCanvas.
 *
 * @author alexeev
 */
public class BasicEdtCanvasController {
  protected final BasicEdtCanvas _canvas;
  protected final ArrayList<i_ModeChangeListener> _listeners;

  // permanent mouse listeners
  private final RotationMouseAdapter _rotateAdapter;
  private final ScalingMouseAdapter _scaleAdapter;

  public BasicEdtCanvasController(BasicEdtCanvas canvas) {
    _canvas = canvas;
    _listeners = new ArrayList<i_ModeChangeListener>();
    _rotateAdapter = new RotationMouseAdapter(this);
    _scaleAdapter = new ScalingMouseAdapter(this);
  }

  public ScalingMouseAdapter getScaleAdapter() {
    return _scaleAdapter;
  }

  public RotationMouseAdapter getRotateAdapter() {
    return _rotateAdapter;
  }

  public void initMouseListeners() {
    addMouseListener(_rotateAdapter);
    addMouseListener(_scaleAdapter);
  }

  public BasicEdtCanvas getCanvas() {
    return _canvas;
  }

  public final SceneGL getScene() {
    return _canvas.getScene();
  }

  public boolean is3d() {
    return _canvas.getScene().is3d();
  }

  /**
   * Get sight line by X, Y coordinates on screen.
   * Y -> canvas height - Y
   * @param xCoord
   * @param yCoord
   * @return
   * @throws ExDegeneration
   */
  public Ray3d getSightRay(double xCoord, double yCoord) throws ExDegeneration {
    Render render = _canvas.getScene().getRender();
    return CalculatorGL.getSightRay(render, xCoord, yCoord);
  }

  /**
   * Turn scene on the specified angles in spherical coordinates.
   * @param d_phi
   * @param d_theta
   */
  public void turn(double d_phi, double d_theta) {
    getScene().turnCamera(d_phi, d_theta);
  }

  public final void addKeyListener(KeyListener k) {
    _canvas.addKeyListener(k);
  }

  public final void removeKeyListener(KeyListener k) {
    _canvas.removeKeyListener(k);
  }

  public final void addMouseListener(MouseAdapter ma) {
    _canvas.addMouseListener(ma);
    _canvas.addMouseMotionListener(ma);
    _canvas.addMouseWheelListener(ma);
  }

  public final void removeMouseListener(MouseAdapter ma) {
    _canvas.removeMouseListener(ma);
    _canvas.removeMouseMotionListener(ma);
    _canvas.removeMouseWheelListener(ma);
  }

  public void setCursor(Cursor c) {
    _canvas.setCursor(c);
  }

  /**
   * Distance from camera position to center.
   * Delegate method from {@link #opengl.sceneparameters.CameraPosition}.
   * @return
   */
  public double getCameraDistance() {
    return _canvas.getScene().getCameraPosition().distance();
  }

  /**
   * Get the nearest point to the camera position.
   *
   * @param points nonempty list of points.
   * @return
   */
  public Vect3d getNearestPointToCamera(List<Vect3d> points) {
    if (points.isEmpty())
      throw new AssertionError("Point list is empty");

    double dist = Double.MAX_VALUE;
    Vect3d eye = getScene().getCameraPosition().eye();
    Vect3d result = points.get(0);
    for (Vect3d point : points) {
      double tempDist = Vect3d.dist(eye, point);
      if (dist > tempDist) {
        dist = tempDist;
        result = point;
      }
    }
    return result.duplicate();
  }

  /**
   * Get length of cell of initial plane's grid.
   * @return
   */
  public double getMeshSize() {
    return getScene().getRender().getInitialPlane().getMeshSize();
  }

  /**
   * Get length of visible cube's edge.
   *
   * @return
   */
  public double getVisibleSize() {
    return getScene().getRender().getViewVolume().getSizeVisible();
  }

  /**
   * Check if given point is in viewing cube.
   *
   * @param v
   * @return
   */
  public boolean isInViewingCube(Vect3d v) {
    return getScene().getRender().getViewVolume().inViewingCube(v);
  }

  /**
   * Redraw scene on canvas
   * using OpenGL rendering.
   */
  public void redraw() {
    _canvas.redraw();
  }
}
