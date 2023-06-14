package opengl;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import static config.Config.ANTIALIASING_ON;
import editor.Editor;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.util.ArrayList;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;
import opengl.sceneparameters.InitialPlane;
import opengl.sceneparameters.ViewMode;
import opengl.sceneparameters.ViewVolume;

/**
 * Class - wrapper of OpenGL objects (Containing all the drawing context)
 */
public class Render {
  private final Editor editor;

  private ArrayList<i_ScaleChangeListener> _scaleListeners = new ArrayList<>();
  private ArrayList<i_RotationListener> _rotListeners = new ArrayList<>();
  private GLAutoDrawable drawable;
  private TextRenderer textRenderer;
  private CameraPosition cameraPosition;
  private ViewVolume viewVolume;
  private GluPerspectiveParameters gluPerspectiveParameters;
  private ViewMode viewMode;
  private InitialPlane initialPlane;
  private ProjectionMode projection;
  private boolean sceneIn3d;

  public Render(Editor editor) {
    this.editor = editor;

    textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 10));
    cameraPosition = new CameraPosition();
    viewVolume = new ViewVolume();
    gluPerspectiveParameters = new GluPerspectiveParameters();
    viewMode = ViewMode.LIGHT;
    initialPlane = new InitialPlane();
    projection = ProjectionMode.PERSPECTIVE_PROJECTION;
    sceneIn3d = true;
  }

  public boolean isSceneIn3d() {
    return sceneIn3d;
  }

  public void setSceneIn3d(boolean sceneIn3d) {
    this.sceneIn3d = sceneIn3d;
  }

  public ProjectionMode getProjection() {
    return projection;
  }

  public void setProjection(ProjectionMode projection) {
    this.projection = projection;
  }

  //!! TODO: Рендеру не обязательно знать Editor.
  // Проблема только с рисованием плоскостей.
  // (их размер зависит от других объектов на сцене)
  public Editor getEditor() {
    return editor;
  }

  public void setDrawable(GLAutoDrawable drawable) {
    this.drawable = drawable;
  }

  public GLAutoDrawable getDrawable() {
    return drawable;
  }

  public GL2 getGL() {
    return drawable.getGL().getGL2();
  }

  public int getWidth() {
    return drawable.getSurfaceWidth();
  }

  public int getHeight() {
    return drawable.getSurfaceHeight();
  }

  public CameraPosition getCameraPosition() {
    return cameraPosition;
  }

  public TextRenderer getTextRenderer() {
    return textRenderer;
  }

  public GluPerspectiveParameters getGluPerspectiveParameters() {
    return gluPerspectiveParameters;
  }

  public InitialPlane getInitialPlane() {
    return initialPlane;
  }

  public ViewMode getViewMode() {
    return viewMode;
  }

  public ViewVolume getViewVolume() {
    return viewVolume;
  }

  public boolean isUseAntialiasing() {
    return ANTIALIASING_ON.value();
  }
  
  public void setUseAntialiasing(boolean useAntialiasing) {
    ANTIALIASING_ON.setValue(useAntialiasing);
  }

  public void setViewMode(ViewMode viewMode) {
    this.viewMode = viewMode;
  }

  public void setTextRenderer(TextRenderer textRenderer) {
    this.textRenderer = textRenderer;
  }

  public void setViewVolume(ViewVolume viewVolume) {
    this.viewVolume = viewVolume;
  }

  public void setGluPerspectiveParameters(GluPerspectiveParameters gluPerspectiveParameters) {
    this.gluPerspectiveParameters = gluPerspectiveParameters;
  }

  public void setInitialPlane(InitialPlane initialPlane) {
    this.initialPlane = initialPlane;
  }

  public void setCameraPosition(CameraPosition cameraPosition) {
    this.cameraPosition = cameraPosition;
  }

  public GLU getGLU(){
    return GLU.createGLU(getGL());
  }

  public GLUT getGLUT(){
    return new GLUT();
  }

  /**
   * Register display change listener.
   * @param listener
   */
  public void addDisplayListener(i_ScaleChangeListener listener){
    _scaleListeners.add(listener);
  }
  /**
   * Unregister display change listener.
   * @param listener
   */
  public void removeDisplayListener(i_ScaleChangeListener listener){
    _scaleListeners.remove(listener);
  }
  /**
   * Notify registered scale change listeners to update.
   */
  public void notifyScaleChange(){
    for (i_ScaleChangeListener l : _scaleListeners) {
      l.scaleChanged(cameraPosition.distance(), initialPlane.getMeshSize());
    }
  }

  /**
   * Register scene rotation listener.
   * @param listener
   */
  public void addRotationListener(i_RotationListener listener){
    _rotListeners.add(listener);
  }
  /**
   * Unregister rotation listener.
   * @param listener
   */
  public void removeRotationListener(i_RotationListener listener){
    _rotListeners.remove(listener);
  }

  /**
   * Notify registered rotation listeners to update.
   */
  public void notifySceneRotated(){
    for (i_RotationListener l : _rotListeners) {
      l.sceneRotated();
    }
  }
}