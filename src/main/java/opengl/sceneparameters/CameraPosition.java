package opengl.sceneparameters;

import geom.Checker;
import geom.SpherCoord;
import geom.Vect3d;

/**
 * Allows you to work with spherical coordinates of camera and get camera coordinates in cartesian coordinates
 */
public class CameraPosition {
  /**
   * Позиция глаза наблюдателя.
   */
  private Vect3d eyePos = new Vect3d();
  /**
   * Центральная точка, на которую направлен взгляд наблюдателя
   */
  private Vect3d centerPos = new Vect3d();
  /**
   * Вектор, направленный вверх для наблюдателя.
   */
  private Vect3d upPos = new Vect3d();

  /**
   * Был ли вектор, направленный вверх, перевернут.
   * <br>Вектор вверх меняется на противоположный для того, чтобы при больших поворотах камеры избежать
   * дергание при переходе через 180 градусов ( в 180 - 360 градусах поворота камера должна быть перевернута)
   */
  private boolean upReverse = false;

  /**
   * Position of camera in spherical coordinates
   * By default centerPos is (0, 0, 0)
   */
  SpherCoord spherCoord = new SpherCoord(Math.PI / 4, Math.PI / 4, 5);

  public CameraPosition(){
    updateState();
  }

  /**
   * Создать позицию камеры, определенную параметрами.
   * Центральная точка, на которую направлен взгляд наблюдателя будет (0, 0, 0)
   * @param eyePos Позиция глаза наблюдателя.
   */
  public CameraPosition(Vect3d eyePos){
    this(eyePos, new Vect3d(0, 0, 0));
  }

  /**
   * Создать позицию камеры, определенную параметрами.
   * @param eyePos Позиция глаза наблюдателя.
   * @param centerPos Центральная точка, на которую направлен взгляд наблюдателя
   */
  public CameraPosition(Vect3d eyePos, Vect3d centerPos){
    setPosition(eyePos, centerPos);
  }

  public CameraPosition(CameraPosition cam){
    this(cam.eye(), cam.center());
  }

  public final void setPosition(Vect3d eyePos, Vect3d centerPos){
    spherCoord = new SpherCoord(eyePos, centerPos);
    updateState();
  }

  /**
   * Переместить центральную точку, на которую направлен взгляд наблюдателя.
   * <br>Перемещение происходит в плоскости, перпендикулярной взгляду, которая проходит через точку {@link #centerPos}.
   * На вход подаются смещение в координатах этой плоскости.
   * @param x Смещение вдоль оси Ox.
   * @param y Смещение вдоль оси Oy.
   */
  public void moveCenterPos(double x, double y){
    Vect3d norm = spherCoord.getVect();
    Vect3d yVect = Vect3d.vector_mul(upPos, norm);
    Vect3d xVect = Vect3d.vector_mul(norm, yVect);
    xVect = Vect3d.getNormalizedVector(xVect);
    yVect = Vect3d.getNormalizedVector(yVect);
    xVect.inc_mul(x);
    yVect.inc_mul(y);
    Vect3d moveVect = Vect3d.sum(xVect, yVect);
    spherCoord.setCenterSpherical(Vect3d.sum(spherCoord.getCenterSpherical(), moveVect));
    updateState();
  }

  /**
   * Turn camera by angle in spherical coordinates
   * @param azimuth phi angle
   * @param zenith theta angle
   */
  public void turnCamera( double azimuth, double zenith) {
    spherCoord.setPhi(spherCoord.getPhi() + azimuth);
    spherCoord.setTheta(spherCoord.getTheta() + zenith);
    updateState();
  }

  /**
   * Set eye position at point.
   * <br>SIC: Sets centerPos at (0, 0, 0).
   * @param point Point of new eye position
   */
  public void turnCamera( Vect3d point ) {
    spherCoord = new SpherCoord(point);
    updateState();
  }

  /**
   * Change camera distance from the origin to the value.
   * If the "val" is positive, the camera distance from the origin increases, otherwise decrease.
   * @param val How much change the distance
   */
  public void changeCameraDistance(double val){
    setCameraDistance(spherCoord.getR() * (1 + val));
  }

  // Getters
  public Vect3d eye(){return new Vect3d(eyePos);}
  public Vect3d center(){return new Vect3d(centerPos);}
  public Vect3d up(){return new Vect3d(upPos);}

  public void setEye(Vect3d newEyePos){
    spherCoord = new SpherCoord(newEyePos);
    updateState();
  }

  /**
   * phi value
   * @return
   */
  public double azimuth(){return spherCoord.getPhi();}

  /**
   * theta value
   * @return
   */
  public double zenith(){return spherCoord.getTheta();}

  /**
   * rho value
   * @return
   */
  public double distance(){return spherCoord.getR();}

  public void setCameraDistance(double distance){
    if (distance <= 0)
      return;
    spherCoord.setR(distance);
    updateState();
  }

  /**
   * Update cartesian coordinates from spherical coordinates
   */
  private void updateState(){
    eyePos = spherCoord.toCartesian();
    centerPos = spherCoord.getCenterSpherical();

    upReverse = false;

    // For a given camera position artificially
    if (Math.abs(zenith()) <= Checker.eps()) {
      upPos = new Vect3d(0, 1, 0);
    }
    else if (Math.abs(zenith() - Math.PI) <= Checker.eps()){
      upPos = new Vect3d(0, 1, 0);
    }
    else{
      if ( !spherCoord.isBigTheta() ){
        upPos = new Vect3d(0, 0, 1);
      }
      else{
        upPos = new Vect3d(0, 0, -1);
        upReverse = true;
      }
    }
  }

  /**
   * Получить вектор, который находится в плоскости вектора {@link #up()} и взгляда
   * (ветор из {@link #center()} в {@link #eye()}) и при этом перпендикулярен последнему.
   * @return вектор, перпендикулярный направлению взгляда для данной точки, направленный вверх
   */
  public Vect3d getUpPerpendicularVect(){
    CameraPosition newCam = new CameraPosition(this);
    if (zenith() == 0){
      // Костыль для режима 2д, т к вектор {@link #up} в нем направлен по оси OY, а не по оси OZ.
      // Для этой ситуации камера находится где-то на оси OZ, а вектор вверх направлен по оси OY, получаем,
      // что вектор вверх и есть искомый вектор.
      return up();
    }
    else {
      if (upReverse)
        newCam.turnCamera(0, Math.PI / 2);
      else
        newCam.turnCamera(0, -Math.PI / 2);
    }
    return Vect3d.sub(newCam.eye(), newCam.center());
  }

  public Vect3d getEyeToCenterVect() {
    return Vect3d.sub(eyePos, centerPos);
  }
}
