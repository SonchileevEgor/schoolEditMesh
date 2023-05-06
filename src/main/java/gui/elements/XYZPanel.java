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
 * Input panel for XYZ coordinates
 * @author alexeev
 */
public class XYZPanel extends JPanel {
  private static final String VALUE_PROPERTY = "value";
  private NumericTextField _xField;
  private NumericTextField _yField;
  private NumericTextField _zField;

  /**
   * Input coordinates panel.
   * @param is3d 2D or 3D coordinates.
   */
  public XYZPanel(boolean is3d) {
    super(new MigLayout(new LC().fillX().wrapAfter(2).hideMode(3)));
    JLabel xLabel = new JLabel("X");
    JLabel yLabel = new JLabel("Y");
    JLabel zLabel = new JLabel("Z");
    _xField = new NumericTextField(0, false);
    _yField = new NumericTextField(0, false);
    _zField = new NumericTextField(0, false);

    //!! Старое значение не передаётся. Для этого нужно исправить NumericTextField.
    PropertyChangeListener valueChanged =
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          try {
            Vect3d newValue = XYZPanel.this.getVect();
            //затычка для того, чтобы новое и старое значения отличались.
            Vect3d oldValue = Vect3d.sum(new Vect3d(0, 0, 1), newValue);
            firePropertyChange(VALUE_PROPERTY, oldValue, newValue);
          } catch (ParseException ex) {}
        }
      };

    _xField.addPropertyChangeListener("value", valueChanged);
    _yField.addPropertyChangeListener("value", valueChanged);
    _zField.addPropertyChangeListener("value", valueChanged);

    if( !is3d ){
      zLabel.setVisible(false);
      _zField.setVisible(false);
    }

    add(xLabel);
    add(_xField, new CC().growX().pushX());
    add(yLabel);
    add(_yField, new CC().growX().pushX());
    add(zLabel);
    add(_zField, new CC().growX().pushX());
  }

  public XYZPanel(double xValue, double yValue, double zValue, boolean is3d) {
    this(is3d);
    _xField.setValue(xValue);
    _yField.setValue(yValue);
    _zField.setValue(zValue);
  }

  public double x() throws ParseException {
    return _xField.getNumericValue();
  }

  public double y() throws ParseException {
    return _yField.getNumericValue();
  }

  public double z() throws ParseException {
    return _zField.getNumericValue();
  }

  public Vect3d getVect() throws ParseException {
    return new Vect3d(x(), y(), z());
  }
}
