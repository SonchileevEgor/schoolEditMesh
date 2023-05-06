package gui.elements;

import gui.ui.EdtTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import javax.swing.*;

/**
 * @author alena
 */
public class InputTextField extends EdtTextField {

  public InputTextField() {
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
  
  public InputTextField(String initValue) {
    this();
    setText(initValue);
  }
  
  /**
   * Текстовое значение ввода.
   * @return
   * @throws ParseException ошибка ввода в случае пустой строки
   */
  public String getTextValue() throws ParseException {
    String result = getText();
    if( result == "" ){
      throw new ParseException("поле ввода пусто", 0);
    }
    return result;
  }
}