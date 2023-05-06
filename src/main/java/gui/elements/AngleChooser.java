package gui.elements;

import static config.Config.PRECISION;
import geom.Angle3d;
import gui.laf.AppColor;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.JPanel;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import util.Util;

/**
 *
 * @author alexeev
 */
public class AngleChooser extends JPanel {
  // Angle presicion (in degrees).
  private static final double ANGLE_PRESICION = 5;

  private final NumericTextField _valueField;
  private AngularCircle _circle;
  private double _degreeValue;
  private double _prevValue;
  private double _value;

  public AngleChooser(double value) {
    setLayout(new MigLayout(new LC().fillX()));
    _prevValue = value;
    _value = value;
    _circle = new AngularCircle();
    _valueField = new NumericTextField(Angle3d.radians2Degree(_value), false);
    _valueField.addPropertyChangeListener("value", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent e) {
        try {
          _degreeValue = ((NumericTextField)e.getSource()).getNumericValue();
          _degreeValue = Angle3d.toMinus180to180Interval(_degreeValue);
          setValue(Math.toRadians(_degreeValue));
          _circle.repaint();
        } catch (ParseException ex) {}
      }
    });

    add(_valueField, new CC().dockSouth());
    add(_circle, new CC().dockEast().width("75!").height("75!"));
  }

  public double getValue() {
    return _value;
  }

  void setValue(double value) {
    AngleChooser.this.firePropertyChange("angle", _value, value);
    _value = value;
    _valueField.setText(Util.valueOf(_degreeValue, PRECISION.value()));
  }

  class AngularCircle extends JPanel implements MouseListener, MouseMotionListener {
    private Color _bgColor;

    public AngularCircle() {
      _bgColor = AppColor.ORANGE_ULTRA_LIGHT.color();
      addMouseListener(this);
      addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      int _size = getWidth();
      int _halfSize = _size / 2;
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      // draw disk
      g.setColor(_bgColor);
      g.fillOval(10, 10, _size - 19, _size - 19);

      // draw cross
      g.setColor(Color.BLACK);
      g.drawLine(_size * 1 / 4, _halfSize, _halfSize - 3, _halfSize);
      g.drawLine(_halfSize + 3, _halfSize, _size * 3 / 4, _halfSize);
      g.drawLine(_halfSize, _size * 1 / 4, _halfSize, _halfSize - 3);
      g.drawLine(_halfSize, _halfSize + 3, _halfSize, _size * 3 / 4);
      g.fillOval(_halfSize - 3, _halfSize - 3, 7, 7);

      // draw border circle
      g.drawOval(10, 10, _size - 20, _size - 20);

      // draw pointer
      g.drawLine(_halfSize, _halfSize,
              _halfSize + (int)Math.floor((_halfSize - 10) * Math.cos(_value)),
              _halfSize - (int)Math.floor((_halfSize - 10) * Math.sin(_value)));
    }

    @Override
    public void mousePressed(MouseEvent e) {
      _prevValue = _value;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
      AngleChooser.this.firePropertyChange("angle", _prevValue, _value);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      _bgColor = AppColor.ORANGE_ULTRA_LIGHT.color();
      revalidate();
      repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
      _bgColor = AppColor.ORANGE_ULTRA_LIGHT.color();
      revalidate();
      repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      double x = e.getX() - getWidth() / 2;
      double y = getWidth() / 2 - e.getY();
      if ((x != 0) && (y != 0)) {
        _value = Angle3d.getRoundedAngle(Math.atan2(y, x), ANGLE_PRESICION);
        _degreeValue = Math.toDegrees(_value);
        _valueField.setText(Util.valueOf(_degreeValue, PRECISION.value()));
        revalidate();
        repaint();
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
  }
}

