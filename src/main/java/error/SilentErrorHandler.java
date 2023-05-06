package error;

/**
 * Silent error handler.
 * 
 * @author alexeev
 */
public class SilentErrorHandler implements i_ErrorHandler {
  @Override
  public void showMessage(String message, Error er) {
    // do nothing
  }
}
