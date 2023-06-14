package opengl.scenegl;

import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import static config.Config.AXES_VISIBLE;
import static config.Config.GRID_INTENSITY;
import static config.Config.GRID_VISIBLE;
import static config.Config.INITIAL_GRID_LINE_WIDTH;
import static config.Config.VIEW_VOLUME_VISIBLE;
import editor.Editor;
import geom.Cube3d;
import geom.Plane3d;
import geom.Vect3d;
import opengl.colorgl.ColorGL;
import opengl.colortheme.CurrentTheme;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.InitialPlane;
import opengl.sceneparameters.ViewMode;
import opengl.sceneparameters.ViewVolume;
import opengl.textdrawinggl.TextDrawer;
import util.Log;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import opengl.AxesDrawer;
import opengl.ByteMask;
import opengl.CalculatorGL;
import opengl.Drawer;
import opengl.ProjectionManager;
import opengl.ProjectionMode;
import static opengl.ProjectionMode.ORTHO_PROJECTION;
import static opengl.ProjectionMode.PERSPECTIVE_PROJECTION;
import opengl.Render;
import opengl.ScreenSizeManager;
import static opengl.sceneparameters.ViewMode.DOTTED_LINE;
import static opengl.sceneparameters.ViewMode.FACES_TRANSPARENT;
import static opengl.sceneparameters.ViewMode.LIGHT;
import static opengl.sceneparameters.ViewMode.PENCIL;

/**
 * Base class of Scenes classes
 *
 * @see <a href=http://forum.vingrad.ru/forum/topic-161042/kw-opengl-java-jogl.html>Create JOGL application: Tutorial</a>
 */
public abstract class SceneGL implements GLEventListener {
  protected boolean isBlendEquationAvailable;
  
  // Drawable and its variables
  protected Render render;
  protected byte[] byteMask;
  // Show view volume cube
//  private boolean viewVolumeCubeVisible = false;

  SceneGL() {}

  public SceneGL(Editor edt) {
    this(edt, new CameraPosition());
  }

  public SceneGL(Editor edt, CameraPosition cameraPosition) {
    render = new Render(edt);
    render.setCameraPosition(cameraPosition);
    byteMask = ByteMask.getByteMask45x3();
  }

  public ProjectionMode getProjection() {
    return render.getProjection();
  }

  public void setProjection(ProjectionMode projection) {
    render.setProjection(projection);
  }

  public void switchProjection(){
    switch (getProjection()){
      case PERSPECTIVE_PROJECTION:
        setProjection(ProjectionMode.ORTHO_PROJECTION);
        break;
      case ORTHO_PROJECTION:
        setProjection(ProjectionMode.PERSPECTIVE_PROJECTION);
        break;
      default:
        throw new RuntimeException("Unsupported type of projection");
    }
  }

  public Render getRender() {
    return render;
  }

  public void setRender(Render render) {
    this.render = render;
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    render.getGluPerspectiveParameters().setAspect((double) width / height);
    render.getGL().glViewport(0, 0, width, height);
    ScreenSizeManager.setScreenSize(width, height);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {}

  @Override
  public void init(GLAutoDrawable drawableLoc) {
    render.setDrawable(drawableLoc);
    GL2 gl = render.getGL();
    isBlendEquationAvailable = gl.isFunctionAvailable("glBlendEquation");
    if (gl.isExtensionAvailable("GL_ARB_multisample")) {
      gl.glEnable(GL2.GL_MULTISAMPLE);
    } else {
      Log.out.printf("Warning: GL_ARB_multisample is not supported by this platform%n");
    }
    gl.glClearColor(1, 1, 1, 1);
    gl.glShadeModel(GL2.GL_SMOOTH);

    gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
    //gl.glEnable(GL2.GL_POLYGON_OFFSET_LINE);
    gl.glPolygonOffset(1.0f, 1.0f);
    gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);

    gl.glEnable(GL2.GL_POINT_SMOOTH);
    gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
    gl.glPolygonStipple(byteMask, 0);

    // Optimization to eliminate the initial delay for drawing labels.
    TextDrawer.cacheLabel(render, "A");
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    render.setDrawable(drawable);
    GL2 gl = render.getGL();

    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    initProjection();
    gl.glLoadIdentity();
    Drawer.resetContext();
  }

  abstract protected void initPerspectiveParameters();

  abstract public void drawObjects();

  private void initProjection(){
    initPerspectiveParameters();
    switch (getProjection()) {
      case PERSPECTIVE_PROJECTION:
        ProjectionManager.setPerspectiveProjection(render);
        break;
      case ORTHO_PROJECTION:
        ProjectionManager.setOrthoProjection(render);
        break;
      default:
        throw new RuntimeException("Unsupported type of projection");
    }
  }

  /**
   * Get screenshot of current render.
   *
   * @return
   */
  public BufferedImage getImage() {
    GLAutoDrawable drawable = render.getDrawable();
    drawable.getContext().makeCurrent();
    AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(drawable.getGLProfile(), false);
    BufferedImage image = glReadBufferUtil.readPixelsToBufferedImage(drawable.getGL(), true);
    drawable.getContext().release();
    return image;
  }

  /**
   * Get coordinates of point in scene in window coordinates
   *
   * @param obj      point in scene
   * @param winCoord window coordinates [x, y]
   */
  public void getDisplayCoord(Vect3d obj, double[] winCoord) {
    CalculatorGL.getDisplayCoord(render, obj, winCoord);
  }

  /**
   * Turn camera by angle in spherical coordinates
   *
   * @param azimuth phi angle
   * @param zenith  theta angle
   */
  public void turnCamera(double azimuth, double zenith) {
    getCameraPosition().turnCamera(azimuth, zenith);
    render.notifySceneRotated();
  }

  /**
   * Change camera distance from the origin to the value.
   * If the "val" is positive, the camera distance from the origin increases, otherwise decrease.
   * view volume related with camera distance value.
   *
   * @param val How much change the distance
   */
  public void changeCameraDistance(double val) {
    // restrict size of view volume
    CameraPosition cam = render.getCameraPosition();
    ViewVolume viewVolume = render.getViewVolume();
    InitialPlane initialPlane = render.getInitialPlane();

    if (cam.distance() * (1 + val) >= 1 && cam.distance() * (1 + val) <= 3000) {
      cam.changeCameraDistance(val);
      viewVolume.setSizeVisible(viewVolume.getSizeVisible() * (1 + val));
      initialPlane.setNumCells((int) (viewVolume.getSizeVisible() * 4 / initialPlane.getMeshSize()));
    }
    render.notifyScaleChange();
  }

  public void changeViewMode(ViewMode viewMode) {
    render.setViewMode(viewMode);
  }

  public void changeViewMode() {
    switch (render.getViewMode()) {
      case LIGHT:
        render.setViewMode(ViewMode.DOTTED_LINE);
        break;
      case DOTTED_LINE:
        render.setViewMode(ViewMode.PENCIL);
        break;
      case PENCIL:
        render.setViewMode(ViewMode.FACES_TRANSPARENT);
        break;
      case FACES_TRANSPARENT:
        render.setViewMode(ViewMode.LIGHT);
        break;
      default:
        break;
    }
  }

  public ViewVolume getViewVolume() {
    return render.getViewVolume();
  }

  public void setViewVolume(ViewVolume viewVolume) {
    render.setViewVolume(viewVolume);
  }

  public Cube3d getViewVolumeAsCube() {
    return getViewVolume().getAsCube();
  }

  public CameraPosition getCameraPosition() {
    return render.getCameraPosition();
  }

  public void setCameraPosition(CameraPosition cameraPosition) {
    render.setCameraPosition(cameraPosition);
  }

  public boolean isUseAntialiasing() {
    return render.isUseAntialiasing();
  }

  public void setUseAntialiasing(boolean useAntialiasing) {
    render.setUseAntialiasing(useAntialiasing);
  }

  /**
   * Переместить центральную точку, на которую направлен взгляд наблюдателя.
   * <br>Перемещение происходит в плоскости, перпендикулярной взгляду, которая проходит через точку
   * {@link CameraPosition#centerPos}.
   * На вход подаются смещение в координатах этой плоскости.
   * @param x Смещение вдоль оси Ox.
   * @param y Смещение вдоль оси Oy.
   */
  public void moveCenterPos(double x, double y) {
    render.getCameraPosition().moveCenterPos(x, y);
  }

  protected void calcInitialPlaneParameters(){
    render.getInitialPlane().setMeshSize(AxesDrawer.getMarkupSize(render) / 2);
    render.getInitialPlane().setNumCells((int) (render.getViewVolume().getSizeVisible() * 4 / render.getInitialPlane().getMeshSize()));
  }

  public void drawAxesAndInitialGrid(){
    calcInitialPlaneParameters();
    drawInitialGrid();
    drawAxes();
  }

  public void drawAxes(){
    GL2 gl = render.getGL();
    gl.glDisable(GL2.GL_DEPTH_TEST);
    if (AXES_VISIBLE.value()) {
      Drawer.setLineWidth(render, 1);
      List<ColorGL> axesColors;
      if (render.getViewMode() == ViewMode.PENCIL || !is3d()){
        axesColors = new ArrayList<>();
        axesColors.add(ColorGL.BLACK);
        axesColors.add(ColorGL.BLACK);
        axesColors.add(ColorGL.BLACK);
      } else {
        axesColors = CurrentTheme.getColorTheme().getColorsOfCoordinateAxes();
      }
      if (is3d()) {
        AxesDrawer.drawCoordinateAxes(render, axesColors);
      } else {
        AxesDrawer.drawCoordinateAxesXY(render, axesColors);
      }
    }
    gl.glEnable(GL2.GL_DEPTH_TEST);
  }

  public void drawInitialGrid() {
    Drawer.setLineWidth(render, INITIAL_GRID_LINE_WIDTH.value());
    if (GRID_VISIBLE.value()) {
      double ci = GRID_INTENSITY.value();
      double ciCorrector = 0.1;
      ColorGL initGridColor = new ColorGL(ci, ci, ci);
      if (isUseAntialiasing()) {
        // Antialiasing makes color brighter
        initGridColor.setColor(ci-ciCorrector, ci-ciCorrector, ci-ciCorrector);
      }
      Drawer.setObjectColor(render, initGridColor);
      Drawer.setLineWidth(render, INITIAL_GRID_LINE_WIDTH.value());
      Drawer.drawInitialGrid(render);
    }
  }

  public void drawViewVolumeCube(){
    if (VIEW_VOLUME_VISIBLE.value()) {
      Drawer.setObjectColor(render, ColorGL.GREY);
      Drawer.drawViewVolumeCube(render);
    }
  }

  /**
   * Плоскость с вектором нормали, равным вектору взгляда, содержащая данную точку.
   * @param point
   * @return
   */
  public Plane3d getPlaneByPoint(Vect3d point) {
    return CalculatorGL.getPlaneByPoint(render, point);
  }

  public abstract SceneType getSceneType();

  public boolean is3d() {
    return render.isSceneIn3d();
  }

  public void setGridColorIntensity(double gridColorIntensity) {
    if (gridColorIntensity > 1.0 || gridColorIntensity < 0)
      throw new RuntimeException("Wrong initialGridColorIntensity. Must be in [0, 1]");
    GRID_INTENSITY.setValue(gridColorIntensity);
  }

  public double getInitialGridColorIntensity() {
    return GRID_INTENSITY.value();
  }
}