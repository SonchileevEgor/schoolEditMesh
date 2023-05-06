package opengl.sceneparameters;

import geom.Angle3d;

/**
 * Parameters of viewing volume for gluPerspective function
 *
 * @see <a href="http://www.felixgers.de/teaching/jogl/perspectiveProjection.html">Perspective Projection</a>
 * @see <a href="http://www.felixgers.de/teaching/jogl/orthoProjection.html">Ortho Projection</a>
 */
public class GluPerspectiveParameters {

  /** Параметр подобран на глаз для того, чтобы преобразование из перспективной проекции в ортогональную не сильно
    * визуально меняло масштаб всей сцены.
    */
  private double k = 10;

  /**
   * Угол визуального охвата (тета) в вертикальном направлении y в градусах
   */
  private double fovy;
  /**
   * Отношение ширины к высоте (x/y)
   */
  private double aspect;
  /**
   * Дистанция между точкой наблюдения и ближней отсекающей плоскостью.
   */
  private double zNear;
  /**
   * Дистанция между точкой наблюдения и дальней отсекающей плоскостью.
   */
  private double zFar;

  public GluPerspectiveParameters() {
    fovy = 45;
    aspect = 1;
    zNear = 0.1;
    zFar = 25;
  }

  public GluPerspectiveParameters(double fovy, double aspect, double zNear, double zFar){
    this.fovy = fovy;
    this.aspect = aspect;
    this.zNear = zNear;
    this.zFar = zFar;
  }

  /**
   * @see #fovy
   * @return
   */
  public double getFovy() {
    return fovy;
  }

  /**
   * @see #fovy
   * @param fovy
   */
  public void setFovy(double fovy) {
    this.fovy = fovy;
  }

  /**
   * @see #aspect
   * @return
   */
  public double getAspect() {
    return aspect;
  }

  /**
   * @see #aspect
   * @param aspect
   */
  public void setAspect(double aspect) {
    this.aspect = aspect;
  }

  /**
   * @see #zNear
   * @return
   */
  public double getzNear() {
    return zNear;
  }

  /**
   * @see #zNear
   * @param zNear
   */
  public void setzNear(double zNear) {
    this.zNear = zNear;
  }

  /**
   * @see #zFar
   * @return
   */
  public double getzFar() {
    return zFar;
  }

  /**
   * @see #zFar
   * @param zFar
   */
  public void setzFar(double zFar) {
    this.zFar = zFar;
  }

  //Functions for translate gluPerspective parameters to glFrustum parameters

  private double calcHalfH(){
    return Math.tan(Angle3d.degree2Radians(fovy / 2)) * zNear;
  }
  private double calcHalfW(){
    return calcHalfH() * aspect;
  }

  public double getLeft(){
    return -calcHalfW();
  }
  public double getRight(){
    return calcHalfW();
  }
  public double getBottom(){
    return -calcHalfH();
  }
  public double getTop(){
    return calcHalfH();
  }

  /**
   * Подсчёт такого расстояния от камеры,
   * при котором сцена полностью помещается в экран.
   *
   * @param width ширина сцены
   * @param height высота сцены
   * @return
   */
  public double getFitCamDist(double width, double height) {
    // вписывам по ширине
    double distW = width * 0.5 / calcHalfH() / aspect / k;
    // вписываем по высоте
    double distH = height * 0.5 / calcHalfH() / k;
    return Math.max(distW, distH);
  }

  // Functions for translate gluPerspective parameters to glOrtho parameters

  private double calcHalfH(double distance){
    return calcHalfH() * distance * k;
  }
  private double calcHalfW(double distance){
    return calcHalfH(distance) * aspect;
  }

  public double getLeft(double distance){
    return -calcHalfW(distance);
  }
  public double getRight(double distance){
    return calcHalfW(distance);
  }
  public double getBottom(double distance){
    return -calcHalfH(distance);
  }
  public double getTop(double distance){
    return calcHalfH(distance);
  }
}
