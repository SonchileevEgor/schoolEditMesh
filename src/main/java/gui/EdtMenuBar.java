package gui;

import gui.menubar.EditMenu;
import gui.menubar.Filemenu;
import gui.menubar.HelpMenu;
import gui.menubar.ViewMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author alexeev
 */
public class EdtMenuBar extends JMenuBar {

  public EdtMenuBar(EdtController ctrl){
    super();

    add(new Filemenu(ctrl));
    add(new EditMenu(ctrl));
    add(new ViewMenu(ctrl));
    add(new HelpMenu(ctrl));
  }
}