package gui.elements;

import geom.Vect3d;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Input panel for String function
 * @author alena
 */
public class FunctionInputPanel extends JPanel {
  private static final String VALUE_PROPERTY = "value";
  private InputTextField _inputField;

  /**
   * Input function panel.
   */
  public FunctionInputPanel() {
    super(new MigLayout(new LC().fillX().wrapAfter(2).hideMode(3)));
    JLabel fxLabel = new JLabel("f(x) = ");
    _inputField = new InputTextField("x^2+0.5*x+0.1");

    add(fxLabel);
    add(_inputField, new CC().growX().pushX());
  }

  public String fx() throws ParseException {
    return _inputField.getTextValue();
  }
}
