package gui.action;

import editor.i_EditorChangeListener;
import gui.EdtController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * Basic implementation of i_EdtAction interface.
 * @author alexeev
 */
public class EdtAction extends AbstractAction implements i_EditorChangeListener {
  private EdtController _ctrl;

  public EdtAction() {};

  /**
   *
   * @param ctrl
   * @param name The key used for storing the String name for the action, used for a menu or button.
   * @param description The key used for storing a short String description for the action, used for tooltip text.
   * @param smallIcon The key used for storing a small Icon, such as ImageIcon.
   * @param largeIcon The key used for storing an Icon.
   */
  public EdtAction(EdtController ctrl, String name, String description, Icon smallIcon, Icon largeIcon) {
    super(name, smallIcon);
    _ctrl = ctrl;

    putValue(Action.LONG_DESCRIPTION, description);
    putValue(Action.LARGE_ICON_KEY, largeIcon);
    _ctrl.addEditorStateChangeListener(this);

    updateEditorState();
    //!! TODO: Убрать этот вызов из конструктора.
    // Сейчас он используется для того, чтобы действие было включено / выключено корректно
    // сразу после его создания.
  }

  /**
   * Возвращает true, если переключает режим.
   * @return
   */
  public boolean isMode() {
    return false;
  }

  /**
   * Возвращает true, если показывает диалоговое окно.
   * @return
   */
  public boolean isDialog() {
    return false;
  }

  @Override
  public void updateEditorState() {  }

  @Override
  public void actionPerformed(ActionEvent e) { }
}
