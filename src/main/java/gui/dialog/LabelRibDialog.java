package gui.dialog;

import editor.ExNoAnchor;
import gui.elements.NameTextField;
import gui.ui.EdtInputDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;


/**
 *
 * @author Лютенков
 */
public class LabelRibDialog extends EdtInputDialog{
  private NameTextField _labelField;
  private String _oldLabel;
  private String _newLabel;

  public LabelRibDialog(JFrame owner, String label) throws ExNoAnchor {
    super(owner, "Надпись над отрезком");
    _oldLabel = label;
    _labelField = new NameTextField(_oldLabel);
    addItem("Введите надпись", _labelField);
    addButtons();
    addFeedback();
    pack();
    setCenterLocation();
  }

  public String newLabel() {
    return _newLabel;
  }

  @Override
  protected AbstractAction OKAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        _newLabel = _labelField.getText();
        if (_newLabel.equals(_oldLabel)) {
          accepted = false;
          dispose();
        } else {
          accepted = true;
          dispose();
        }
      }
    };
  }
    
}
