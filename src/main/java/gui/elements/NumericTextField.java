package gui.elements;

import static config.Config.PRECISION;
import gui.ui.EdtFormattedTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import javax.swing.*;

/**
 * Input text field for parsing double numbers.
 *
 * @author alexeev
 */
public class NumericTextField extends EdtFormattedTextField {
  private boolean _positive;
  private DecimalFormat _localFormat;
  private DecimalFormat _usFormat;

  /**
   * Numeric input text field
   * @param defaultValue default value for field
   * @param positive TRUE if input is always positive<br> FALSE if it might be negative
   */
  public NumericTextField(double defaultValue, boolean positive) {
    super();
    _positive = positive;

    String formatString = "#.";
    for (int i = 0; i < PRECISION.value(); i++) {
      formatString += "#";
    }

    if (positive) {
      formatString += "; " + formatString;
    }

    _localFormat = new DecimalFormat(formatString, new DecimalFormatSymbols(Locale.getDefault()));
    _usFormat = new DecimalFormat(formatString, new DecimalFormatSymbols(Locale.US));

    setValue(defaultValue);

    setFormatterFactory(new AbstractFormatterFactory() {
      @Override
      public AbstractFormatter getFormatter(JFormattedTextField tf) {
        return new JFormattedTextField.AbstractFormatter() {
          @Override
          public Object stringToValue(String text) throws ParseException {
            if( text.isEmpty() ){
              throw new ParseException("Text field is empty", 0);
            }
            ParsePosition ps = new ParsePosition(0);
            Number result = _localFormat.parse(text, ps);
            if (ps.getIndex() == text.length())
              return result;
            ps.setIndex(0);
            result = _usFormat.parse(text, ps);
            if (ps.getIndex() == text.length())
              return result;
            throw new ParseException("Parse error", 0);
          }

          @Override
          public String valueToString(Object value) throws ParseException {
            if( value == null ){
              throw new ParseException("Value is null", 0);
            }
            try {
              return _localFormat.format(value);
            } catch (IllegalArgumentException ex){
              try {
                return _usFormat.format(value);
              } catch (IllegalArgumentException ex2) {
                throw new ParseException(value.toString(), 0);
              }
            }
          }
        };
      }
    });
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

  /**
   * Числовое значение ввода.
   * @return
   * @throws ParseException ошибка ввода:
   * либо невозможно распознать строку как число,
   * либо введено отрицательное число в поле ввода положительных чисел
   */
  public double getNumericValue() throws ParseException {
    Object value = getValue();
    if( value == null ){
      throw new ParseException("поле ввода пусто", 0);
    }
    double result = Double.parseDouble(getValue().toString());
    if (_positive && result < 0)
      throw new ParseException("поле не поддерживает ввод отрицательных значений", 0);
    return result;
  }
}
