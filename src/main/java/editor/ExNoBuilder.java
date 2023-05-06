package editor;

/**
 *
 * @author alexeev
 */
public class ExNoBuilder extends ExBadRef {
  private static final long serialVersionUID = 1L;
  
  public ExNoBuilder(String message) {
    super("не могу найти билдер <" + message + ">");
  }
}
