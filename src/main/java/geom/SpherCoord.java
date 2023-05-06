package geom;

import static java.lang.Math.PI;

/**
 * A class for storing point in spherical coordinates and calculates it in Cartesian coordinates.
 *
 * @author maxbrainrus
 */
public class SpherCoord {
  private double phi; // [0, 2 * PI)
  private double theta; // [0, 2 * PI], if ( PI, 2 * PI) then bigTheta == true
  private double r; // >= 0
  private boolean bigTheta;
  /**
   * Центр (начало координат ) для сферических координат (Точка в декартовых координатах)
   */
  private Vect3d centerSpherical;

  public SpherCoord(double phi, double theta, double r) {
    setAll(phi, theta, r, new Vect3d(0, 0, 0));
  }

  public SpherCoord(double phi, double theta, double r, Vect3d centerSpherical) {
    setAll(phi, theta, r, centerSpherical);
  }

  /**
   * Create spherical coordinates from cartesian coordinates, if origin of spherical coordinates not in (0, 0, 0).
   * @param point Point in cartesian coordinates.
   */
  public SpherCoord(Vect3d point) {
    double r = point.norm();
    if (r <= 0) {
      setAll(0, 0, 0, new Vect3d(0, 0, 0));
    } else {
      double theta = Math.acos(point.z() / r);
      double phi = Math.atan2(point.y(), point.x());
      setAll(phi, theta, r, new Vect3d(0, 0, 0));
    }
  }

  /**
   * Create spherical coordinates from Cartesian coordinates, if origin of spherical coordinates not in
   * centerSpherical point.
   * @param point Point in Cartesian coordinates.
   * @param centerSpherical Origin of spherical coordinates.
   */
  public SpherCoord(Vect3d point, Vect3d centerSpherical) {
    SpherCoord tmp = new SpherCoord(Vect3d.sub(point, centerSpherical));
    tmp.setCenterSpherical(centerSpherical);
    this.setAllFrom(tmp);
  }

  /**
   * Get angle value in [0, 2*PI) interval.
   *
   * @param angle
   * @return
   */
  private static double convert0to2PI(double angle) {
    while (angle >= 2 * PI)
      angle -= 2 * PI;
    while (angle < 0)
      angle += 2 * PI;
    return angle;
  }

  /**
   * Copy data from other point.
   * @param spherCoord
   */
  public void setAllFrom(SpherCoord spherCoord) {
    setAll(spherCoord.getPhi(), spherCoord.getTheta(), spherCoord.getR(), spherCoord.getCenterSpherical());
  }

  /**
   * Theta test.
   *
   * @return true, if theta in ( PI, 2 * PI], false if theta in [0, PI]
   */
  public boolean isBigTheta() {
    return bigTheta;
  }

  /**
   * Check
   * Must be called in all methods, that change data of spherical coordinates.
   */
  private void checkCoordinates() {
    if (r < 0) {
      throw new AssertionError(String.format("Warning: Radius in SpherCoord can't be %f", r));
    }

    phi = convert0to2PI(phi);
    theta = convert0to2PI(theta);
    bigTheta = (theta > PI);
  }

  public Vect3d getCenterSpherical() {
    return centerSpherical;
  }

  public void setCenterSpherical(Vect3d centerSpherical) {
    this.centerSpherical = centerSpherical;
  }

  public double getPhi() {
    return phi;
  }

  public void setPhi(double phi) {
    this.phi = phi;
    checkCoordinates();
  }

  public double getTheta() {
    return theta;
  }

  public void setTheta(double theta) {
    this.theta = theta;
    checkCoordinates();
  }

  public double getR() {
    return r;
  }

  public void setR(double r) {
    this.r = r;
    checkCoordinates();
  }

  public void setAll(double phi, double theta, double r, Vect3d centerSpherical) {
    this.phi = phi;
    this.theta = theta;
    this.r = r;
    setCenterSpherical(centerSpherical);
    checkCoordinates();
  }

  /**
   * Check if two spherical coordinates are the same with an &epsilon;-error.
   *
   * @param a object to compare
   * @return true if equal
   */
  public boolean isEqual(SpherCoord a) {
    return (Checker.isEqual(phi, a.getPhi()) &&
            Checker.isEqual(theta, a.getTheta()) &&
            Checker.isEqual(r, a.getR()) &&
            Vect3d.equals(centerSpherical, a.getCenterSpherical()));
  }

  public String toString(int precision) {
    return String.format("(phi: %." + precision + "f; theta: %." + precision + "f; r: %." + precision + "f)"
            + " with center in (x: %." + precision + "f, y: %." + precision + "f, z: %." + precision + "f) ",
            phi, theta, r, centerSpherical.x(), centerSpherical.y(), centerSpherical.z());
  }

  /**
   * Get point in cartesian coordinates.
   * @return
   */
  public Vect3d toCartesian() {
    return new Vect3d(r * Math.sin(theta) * Math.cos(phi) + centerSpherical.x(),
            r * Math.sin(theta) * Math.sin(phi) + centerSpherical.y(),
            r * Math.cos(theta) + centerSpherical.z()
    );
  }

  /**
   * Углы phi и theta задают направление. Этот метод дает это направление как вектор
   * @return Вектор, заданный углами phi и theta, имеющий единичную длину.
   */
  public Vect3d getVect(){
    return new Vect3d(Math.sin(theta) * Math.cos(phi),
            Math.sin(theta) * Math.sin(phi),
            Math.cos(theta)
    );
  }
}
