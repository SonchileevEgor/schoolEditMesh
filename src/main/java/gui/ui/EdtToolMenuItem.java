package gui.ui;

import gui.EdtController;
import gui.IconList;
import javax.swing.JMenuItem;

/**
 *
 * @author alexeev
 */
public class EdtToolMenuItem extends JMenuItem {

  /**
   *
   * @param name
   * @param icon
   * @param ctrl
   */
  public EdtToolMenuItem(String name, IconList icon, EdtController ctrl) {
    super(name, icon.getLargeIcon());
    _ctrl = ctrl;
  }
  
  protected EdtController _ctrl;
}
