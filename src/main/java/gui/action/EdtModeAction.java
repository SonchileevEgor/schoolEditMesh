package gui.action;

import gui.EdtController;
import gui.mode.ModeList;
import javax.swing.Icon;

/**
 * Действие, связанное с переключением режима.
 *
 * @author alexeev
 */
public class EdtModeAction extends EdtAction {
  private ModeList _mode;

  private EdtModeAction() { }

  public EdtModeAction(EdtController ctrl, String name, String description, Icon smallIcon, Icon largeIcon, ModeList mode) {
    super(ctrl, name, description, smallIcon, largeIcon);
    _mode = mode;
  }

  @Override
  public boolean isMode() {
    return true;
  }

  public ModeList mode() {
    return _mode;
  }
}
