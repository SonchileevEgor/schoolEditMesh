package geom;

public class ExZeroDivision extends Exception{
  private static final long serialVersionUID = 1L;

  public ExZeroDivision( String message ){
    super("division by zero " + message);
  }
}
