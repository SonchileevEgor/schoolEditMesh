package gui.elements;

import gui.ui.EdtTextField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Formatted text field with format XXXXX-XXXX-XXXX-XXXXX.
 * @author alexeev
 */
public class LicenseKeyTextField extends EdtTextField {
  public static String TEXT_PROPERTY = "text";

  public LicenseKeyTextField() {
    addPropertyChangeListener(TEXT_PROPERTY, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        String text = evt.getNewValue().toString();
        text = convert(text);
        setText(text);
        revalidate();
        repaint();
      }
    });
  }

  /**
   * Convert input string to the license key format.
   * @param input
   * @return
   */
  private String convert( String input ){
    char c;
    String s = input.toUpperCase();
    StringBuilder out = new StringBuilder();
    int i = 0;
    int outLength = 0;
    while( i < s.length() && outLength < 21 ){
      c = s.charAt(i);
      if( Character.isLetterOrDigit(c) ){
        // character is correct
        out.append(c);
        outLength++;
      } else if ( c == '-' ) {
        if( outLength == 5 || outLength == 10 || outLength == 15 ){
          out.append(c);
          outLength++;
        }
      }
      if( outLength == 5 || outLength == 10 || outLength == 15 ){
        out.append('-');
        outLength++;
      }
      i++;
    }

    // remove last hyphen
    if( outLength > 0 ){
      if( out.charAt(outLength - 1) == '-' ){
        out.deleteCharAt(outLength - 1);
      }
    }

    return out.toString();
  }
}
