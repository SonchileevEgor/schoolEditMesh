package gui.menubar;

import config.LastOpenedScenesManager;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 *
 * @author alexeev
 */
public class Filemenu extends JMenu {
  private static final long serialVersionUID = 2L;
  private EdtController _ctrl;

  /**
   *
   * @param ctrl
   */
  public Filemenu(final EdtController ctrl){
    super("Файл");
    _ctrl = ctrl;

    initNewMenuItem(ActionFactory.FILE_NEW.getAction(ctrl));
    initOpenMenuItem(ActionFactory.FILE_OPEN.getAction(ctrl));
    initSaveMenuItem(ActionFactory.FILE_SAVE.getAction(ctrl));
    initSaveAsMenuItem(ActionFactory.FILE_SAVE_AS.getAction(ctrl));
    initRecentMenuItem();
    addSeparator();
    add(ActionFactory.FILE_EXPORT_PNG.getAction(ctrl));
    add(ActionFactory.FILE_EXPORT_3D.getAction(ctrl));
    addSeparator();
    initQuitMenuItem(ActionFactory.APP_CLOSE.getAction(ctrl));
  }

  private void initNewMenuItem(Action a) {
    JMenuItem newMI = new JMenuItem(a);
    newMI.setAccelerator(KeyStroke
        .getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    add(newMI);
  }

  private void initOpenMenuItem(Action a) {
    JMenuItem openMI = new JMenuItem(a);
    openMI.setAccelerator(KeyStroke
        .getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    add(openMI);
  }

  private void initRecentMenuItem() {
    JMenu recentMenu = new JMenu("Недавние сцены...");
    recentMenu.setIcon(IconList.EMPTY.getMediumIcon());
    recentMenu.addMenuListener(new MenuListener(){
      @Override
      public void menuSelected(MenuEvent e) {
        // construct menu when invoked
        JMenu menu = (JMenu)e.getSource();
        menu.removeAll();
        ArrayList<String> filenames = LastOpenedScenesManager.getFilenames();
        ArrayList<String> paths = LastOpenedScenesManager.getPaths();
        for( int i = 0; i < LastOpenedScenesManager.size(); i++ ){
          // проверяем, открыт ли файл из списка в текущий момент
          // если открыт, то выводить его в интерфейс нет смысла
          if( paths.get(i).equals(config.Temp.CURRENT_FILE) )
            continue;
          JMenuItem mi = new JMenuItem(filenames.get(i), IconList.EMPTY.getMediumIcon());
          final String fname = paths.get(i);
          mi.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
              ActionFactory.FILE_OPEN.getAction(_ctrl, fname).actionPerformed(e);
            }
          });
          menu.add(mi);
        }
      }

      @Override
      public void menuDeselected(MenuEvent e) { }

      @Override
      public void menuCanceled(MenuEvent e) { }
    });
    add(recentMenu);
  }

  private void initSaveMenuItem(Action a) {
    JMenuItem saveMI = new JMenuItem(a);
    saveMI.setAccelerator(KeyStroke
        .getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    add(saveMI);
  }

  private void initSaveAsMenuItem(Action a) {
    JMenuItem saveAsMI = new JMenuItem(a);
    saveAsMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
        InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
    add(saveAsMI);
  }

  private void initQuitMenuItem(Action a) {
    JMenuItem closeMI = new JMenuItem(a);
    closeMI.setAccelerator(KeyStroke
        .getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    add(closeMI);
  }
};
