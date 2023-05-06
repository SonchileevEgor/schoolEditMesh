package gui.elements;

import gui.ui.EdtTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

/**
 * Input text field for avoiding name duplication
 * @author alexeev
 */
public class NameTextField extends EdtTextField {

  public NameTextField() {
    super();
    
    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent fe) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            selectAll();
          }
        });
      }
      @Override
      public void focusLost(FocusEvent fe) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            select(0, 0);
          }
        });
      }
    });
  }
  
  public NameTextField(String initValue) {
    this();
    setText(initValue);
  }
}