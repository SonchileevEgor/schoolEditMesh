package gui.dialog.create;

import error.i_ErrorHandler;
import gui.EdtController;
import gui.ui.EdtInputDialog;

/**
 * Диалог создания тела / нескольких тел.
 * @author alexeev
 */
public class CreateBodyDialog extends EdtInputDialog {
  protected EdtController _ctrl;
  protected i_ErrorHandler _errorHandler;

  /**
   * Constructs dialog for creating a body
   * @param ctrl
   * @param title title of the dialog
   */
  public CreateBodyDialog(EdtController ctrl, i_ErrorHandler errorHandler, String title) {
    super(ctrl.getFrame(), title);
    _ctrl = ctrl;
    _errorHandler = errorHandler;
  }
}
