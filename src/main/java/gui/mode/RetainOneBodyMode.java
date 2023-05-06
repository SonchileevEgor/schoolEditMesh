package gui.mode;

import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;

/**
 * Режим, в котором пользователь выбирает одно из тел в списке. Остальные тела из списка удаляются,
 * после чего происходит переход в стандартный режим.
 *
 * @author alexeev
 */
public class RetainOneBodyMode extends ScreenMode implements i_FocusChangeListener {

  ArrayList<String> _ids;
  String _message;

  public RetainOneBodyMode(EdtController ctrl, ArrayList<String> ids, String message) {
    super(ctrl);
    _ids = ids;
    _message = message;
  }

  @Override
  public void run() {
    _ctrl.status().showMessage(_message);
    canvas().getHighlightAdapter().setAllowedIDs(_ids);
    canvas().getHighlightAdapter().setAllowedSelectionByIDMode(true);
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
  }

  @Override
  public void dispose() {
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
    _ctrl.status().clear();
    canvas().getHighlightAdapter().reset();
  }

  @Override
  public ModeList alias() {
    return null;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.EMPTY.getSmallIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.EMPTY.getLargeIcon();
  }

  @Override
  protected String description() {
    return "Оставьте одно тело";
  }

  @Override
  protected boolean isEnabled() {
    return true;
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    if (_ids.contains(id)) {
      // ID тела есть в списке. Выбранное - оставляем, остальные - удаляем.
      _ids.remove(id);
      _ctrl.removeBodies(_ids);
      _ctrl.redraw();
      canvas().setDefaultMode();
    }
  }
}
