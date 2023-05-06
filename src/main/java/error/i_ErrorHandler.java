package error;

/**
 * Error handler interface.
 *
 * @author alexeev
 */
public interface i_ErrorHandler {
  /**
   * Show error or notification message.
   *
   * @param message error or notification message
   * @param er error type
   */
  public void showMessage(String message, Error er);
}
