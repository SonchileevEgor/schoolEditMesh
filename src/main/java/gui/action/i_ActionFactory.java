package gui.action;

import gui.EdtController;

/**
 *
 * @author alexeev
 */
public interface i_ActionFactory {
  public EdtAction getAction(final EdtController ctrl, final Object... args);
}
