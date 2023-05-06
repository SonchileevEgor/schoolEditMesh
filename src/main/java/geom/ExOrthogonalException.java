package geom;

/**
 * Исключение, свидетельствующее о перпендикулярности каких-то объектов.
 * @author alexeev
 */
public class ExOrthogonalException extends ExDegeneration {
  public ExOrthogonalException(String message) {
    super(message);
  }
}
