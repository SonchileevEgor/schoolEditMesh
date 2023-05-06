package gui;

import editor.i_ErrorListener;
import error.ModeErrorHandler;
import error.SilentErrorHandler;
import error.StatusStripErrorHandler;
import java.util.ArrayList;

/**
 * Error handling base class.
 *
 * @author alexeev.
 */
public class ErrorController {
  private SilentErrorHandler _silentHandler;
  private ModeErrorHandler _modeHandler;
  private StatusStripErrorHandler _statusHandler;
  private ArrayList<i_ErrorListener> _errorListeners = new ArrayList<>();

  public ErrorController(EdtController ctrl) {
    _silentHandler = new SilentErrorHandler();
    _modeHandler = new ModeErrorHandler();
    _statusHandler = new StatusStripErrorHandler(ctrl);
  }

  /**
   * Get default silent error handler.
   * @return
   */
  public SilentErrorHandler getSilentHandler() {
    return _silentHandler;
  }

  /**
   * Get default mode error handler.
   * @return
   */
  public ModeErrorHandler getModeHandler() {
    return _modeHandler;
  }

  /**
   * Get default status strip error handler.
   * @return
   */
  public StatusStripErrorHandler getStatusStripHandler() {
    return _statusHandler;
  }
  
  /**
   * Observer section.
   */
  
  /**
   * Register error listener.
   * @param e 
   */
  public final void addErrorListener(i_ErrorListener e) {
    _errorListeners.add(e);
  }
  
  /**
   * Unregister error listener.
   * @param e 
   */
  public final void removeErrorListener(i_ErrorListener e) {
    _errorListeners.remove(e);
  }
  
  /**
   * Notify error listeners to handle error message.
   * @param msg 
   */
  public final void notifyErrorListeners(String msg) {
    for (i_ErrorListener e : _errorListeners) {
      e.handleError(msg);
    }
  }
}
