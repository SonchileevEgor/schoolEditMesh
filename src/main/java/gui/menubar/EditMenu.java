package gui.menubar;

import gui.EdtController;
import gui.action.ActionFactory;
import gui.dialog.ConstructBodyDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Меню редактирования сцены.
 *
 * @author Vladislav Alexeev
 */
public class EditMenu extends JMenu {
  /**
   *
   * @param ctrl
   */
  public EditMenu(EdtController ctrl){
    super("Редактирование");
    initUndoMenuItem(ActionFactory.UNDO.getAction(ctrl));
    initRedoMenuItem(ActionFactory.REDO.getAction(ctrl));
    add(ActionFactory.SHOW_LOG.getAction(ctrl));
  }

  private void initUndoMenuItem(Action a) {
    JMenuItem undoMI = new JMenuItem(a);
    undoMI.setAccelerator(KeyStroke
        .getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    add(undoMI);
  }

  private void initRedoMenuItem(Action a) {
    JMenuItem redoMI = new JMenuItem(a);
    redoMI.setAccelerator(KeyStroke
        .getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    add(redoMI);
  }
}
