package gui.elements;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Spinner for choosing positive integer values.
 * @author alexeev
 */
public class PositiveSpinner extends JSpinner {
  public PositiveSpinner(int value) {
    super();
    setModel(new SpinnerNumberModel(value, 1, Integer.MAX_VALUE, 1));
  }

  public Object getValueAsDouble() {
    return ((Integer)getValue()).doubleValue();
  }
}
