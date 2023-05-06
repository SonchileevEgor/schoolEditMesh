package gui.ui;

import java.awt.Cursor;
import java.text.Format;
import javax.swing.JFormattedTextField;

/**
 * Wrapper for JFormattedTextField.
 *
 * @author alexeev
 */
public class EdtFormattedTextField extends JFormattedTextField {
  public static final String TEXT_PROPERTY = "text";

  public EdtFormattedTextField() {
    super();
    setHorizontalAlignment(JFormattedTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }
  public EdtFormattedTextField(AbstractFormatter formatter) {
    super(formatter);
    setHorizontalAlignment(JFormattedTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }
  public EdtFormattedTextField(AbstractFormatterFactory factory) {
    super(factory);
    setHorizontalAlignment(JFormattedTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }
  public EdtFormattedTextField(Format format) {
    super(format);
    setHorizontalAlignment(JFormattedTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }
  public EdtFormattedTextField(Object value) {
    super(value);
    setHorizontalAlignment(JFormattedTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }
  public EdtFormattedTextField(AbstractFormatterFactory factory, Object value) {
    super(factory, value);
    setHorizontalAlignment(JFormattedTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }
}
