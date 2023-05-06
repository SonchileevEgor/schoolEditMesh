package gui.elements;

import java.awt.Cursor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Текстовое поле, служащее меткой.
 * Значение нельзя изменить.
 * @author alexeev
 */
public class ImmutableTextField extends JTextField {
  private static final Cursor TEXT_CURSOR = new Cursor(Cursor.TEXT_CURSOR);
  public ImmutableTextField(String text){
    super(text);
    setEditable(false);
    setOpaque(true);
    setHorizontalAlignment(SwingConstants.RIGHT);
    setBorder(null);
    setCursor(TEXT_CURSOR);
  }
}
