package gui.toolbox;

import gui.action.EdtAction;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * Wrapper for JMenuItem using EdtAction.
 *
 * @author alexeev
 */
public class EdtMenuItem extends JMenuItem {
  private EdtAction _action;

  public EdtMenuItem (EdtAction a) {
    super(a);
    _action = a;
  }

  @Override
  public EdtAction getAction() {
    return _action;
  }

  @Override
  public void setAction(Action a) {
    if (a instanceof EdtAction) {
      super.setAction(a);
      _action = (EdtAction)a;
    }
  }
}
