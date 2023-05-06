package util;

public class Scale {
  // scale real number from interval [a, b] to the interval [c, d]
  static public double coord( double x, double a, double b, double c, double d ){
    return c + (d-c) * (x-a)/(b-a);
  }

  // scale real number from interval [a, b] to the integer interval [c, d]
  static public int interval( double x, double a, double b, int c, int d ){
    int res = (int)coord(x, a, b, c, d);
    return (res < c) ? c : ((res > d) ? d : res);
  }
};
