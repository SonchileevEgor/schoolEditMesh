package editor;

/**
 * Body lost exception.
 *
 * Thrown when body cannot build
 * because of some another body wasn't built
 * (<code>i_BodyState.isExists()</code> returns <code>false</code>).
 *
 * @author alexeev
 */
public class ExLostBody extends ExNoBody {
  public ExLostBody(String message) {
    super("Ошибка: " + message + " отсутствует на сцене!");
  }
}
