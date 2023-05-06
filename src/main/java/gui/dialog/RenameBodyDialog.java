package gui.dialog;

import editor.ExNoBody;
import gui.elements.NameTextField;
import gui.ui.EdtInputDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 * Диалог переименования тела.
 * @author alexeev
 */
public abstract class RenameBodyDialog extends EdtInputDialog {
  private NameTextField _titleField;
  private String _oldTitle;
  private String _newTitle;

  public RenameBodyDialog(JFrame owner, String title) throws ExNoBody {
    super(owner, "Переименовать тело");
    _oldTitle = title;
    _titleField = new NameTextField(_oldTitle);
    addItem("Введите имя", _titleField);
    addButtons();
    addFeedback();
    pack();
    setCenterLocation();
  }

  public abstract boolean isValidTitle(String newTitle);

  public String newTitle() {
    return _newTitle;
  }

  @Override
  protected AbstractAction OKAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        _newTitle = _titleField.getText();
        if (_newTitle.equals(_oldTitle)) {
          // тело осталось с прежним именем
          accepted = false;
          dispose();
        } else if (isValidTitle(_newTitle)) {
          // тело переименовано
          accepted = true;
          dispose();
        } else {
          showMessage("Введите другое имя!", 1);
        }
      }
    };
  }
}