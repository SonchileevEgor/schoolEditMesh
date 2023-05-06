package gui.menubar;

import gui.EdtController;
import gui.IconList;
import gui.dialog.AboutDialog;
import gui.dialog.HelpDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Help menu
 * @author alexeev
 */
public class HelpMenu extends JMenu {
  private EdtController _ctrl;

  /**
   *
   * @param ctrl
   */
  public HelpMenu(EdtController ctrl) {
    super("Справка");
    _ctrl = ctrl;
    initHotkeysMenuItem();
    initAboutDialog();
  }

  private void initHotkeysMenuItem() {
    JMenuItem hotkeyMI = new JMenuItem("Горячие клавиши", IconList.HELP.getMediumIcon());
    hotkeyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false));
    hotkeyMI.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        HelpDialog dialog = new HelpDialog(_ctrl.getFrame());
        dialog.setVisible(true);
      }
    });
    add(hotkeyMI);
  }

  private void initAboutDialog() {
    JMenuItem aboutMI = new JMenuItem("О программе", IconList.EMPTY.getMediumIcon());
    aboutMI.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        AboutDialog dialog = new AboutDialog(_ctrl);
        dialog.setVisible(true);
      }
    });
    add(aboutMI);
  }
}