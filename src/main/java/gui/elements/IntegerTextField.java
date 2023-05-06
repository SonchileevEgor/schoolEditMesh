package gui.elements;

import gui.ui.EdtFormattedTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;

/**
 * Input text field for parsing integers.
 * 
 * @author alexeev
 */
public class IntegerTextField extends EdtFormattedTextField {
  private boolean _isPositive;
  
  /**
   * Numeric input text field
   * @param defaultValue default value for field
   * @param positive TRUE if input is always positive<br> FALSE if it might be negative
   */
  public IntegerTextField(int defaultValue, boolean positive) {
    super(getFormat(positive));
    _isPositive = positive;
    
    DecimalFormat f = new DecimalFormat((positive) ? "#; #" : "#");
    f.setParseIntegerOnly(true);
    f.setPositiveSuffix("");
    
    setFormatterFactory(null);
    setValue(defaultValue);
    setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    setEditable(true);
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
  
  private static DecimalFormat getFormat(boolean positive) {
    DecimalFormat f = new DecimalFormat((positive) ? "#; #" : "#");
    f.setParseIntegerOnly(true);
    f.setPositiveSuffix("");
    return f;
  }
  
  /**
   *
   * @return
   * @throws ParseException
   */
  public int getNumericValue() throws ParseException {
    return Integer.parseInt(getValue().toString());
  }
  
  /**
   *
   * @return
   * @throws java.text.ParseException
   */
  public boolean verify() throws ParseException {
    if (_isPositive && getNumericValue() < 0) {
      return false;
    }
    return true;
  }
}
