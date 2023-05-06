package geom;

/**
 *
 * @author alexeev
 */
public class GeomErrorHandler {
  public static void errorMessage(ExGeom ex) {
    System.out.print("Математическое тело не может быть построено по точкам: " + ex.getMessage());
  }

  public static void errorMessage(ExZeroDivision ex) {
    System.out.print("Математическое тело не может быть построено по точкам: " + ex.getMessage());  
  }
}
