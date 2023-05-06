package geom;

public class Det23 {
  public static double calc(
    double a11, double a12,
    double a21, double a22
  ){
    return a11 * a22 - a12 * a21;
  }
  
  public static double calc(
    double a11, double a12, double a13,
    double a21, double a22, double a23,
    double a31, double a32, double a33
  ){
    return
      a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 -
      a13 * a22 * a31 - a11 * a23 * a32 - a12 * a21 * a33;
  }
};
