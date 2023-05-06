package geom;

/**
 * Bundle for values of trigonometric functions of fixed angle.
 * @author ltwood
 */
public class SinCos {
  public SinCos() { _sin = 0.0; _cos = 1.0; }
  public SinCos( double al ) { _sin = Math.sin(al); _cos = Math.cos(al); }
  public SinCos( double s, double c ) { _sin = s; _cos = c; }

  public void reset( double al ) { _sin = Math.sin(al); _cos = Math.cos(al); }
  public void reset( double s, double c ) { _sin = s; _cos = c; }

  public double sin() { return _sin; }
  public double cos() { return _cos; }

  private double _sin, _cos;
};
