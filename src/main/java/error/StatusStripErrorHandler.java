package error;

import gui.EdtController;

/**
 * Builder error handler.
 * @author alexeev
 */
public class StatusStripErrorHandler implements i_ErrorHandler {
  private EdtController _ctrl;
  
  public StatusStripErrorHandler(EdtController ctrl) {
    _ctrl = ctrl;
  }
  
  @Override
  public void showMessage(String message, Error er) {
    _ctrl.status().error(message);
    _ctrl.error().notifyErrorListeners(message);
    util.Fatal.warning(message);
  }
}
