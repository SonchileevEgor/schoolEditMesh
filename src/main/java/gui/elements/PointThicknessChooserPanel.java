package gui.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import util.Util;

/**
 *
 * @author alexeev
 */
public class PointThicknessChooserPanel extends JButton {
  private PointThicknessSlider _slider;
  private boolean _isSliderShown;
  private Thickness _value;

  public PointThicknessChooserPanel(Thickness value) {
    setFocusable(false);
    _slider = new PointThicknessSlider(value);
    _isSliderShown = false;
    _value = value;
    _slider.setFocusable(true);
    addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        if (_isSliderShown) {
          _isSliderShown = false;
        } else {
          _isSliderShown = true;
          final JPopupMenu popup = new JPopupMenu();
          popup.add(_slider);
          _slider.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
              _isSliderShown = false;
              _slider.removeFocusListener(this);
              popup.setVisible(false);
            }
          });
          popup.show(PointThicknessChooserPanel.this, 0, PointThicknessChooserPanel.this.getHeight());
          _slider.requestFocus();
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g.setColor(Color.BLACK);
    if (_value != null) {
      g2d.setStroke(_value.getStroke());
      int centerX = getWidth() / 2;
      int centerY = getHeight() / 2;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.fillOval(centerX - (int)_value.getValue(),
                   centerY - (int)_value.getValue(),
                             (int)_value.getValue() * 2,
                             (int)_value.getValue() * 2);
    }
  }

  public Thickness getThickness() {
    return _slider.getThicknessValue();
  }

  public void addSliderChangeListener(ChangeListener chl) {
    _slider.addChangeListener(chl);
  }

  public void removeSliderChangeListener(ChangeListener chl) {
    _slider.removeChangeListener(chl);
  }
  
  public void setThickness(Thickness value) {
    _slider.setValue((int)value.getValue());
  }

  void setThicknessValue(Thickness value) {
    _value = value;
  }

  class PointThicknessSlider extends JSlider {
    public PointThicknessSlider(Thickness value) {
      super(1, Thickness.values().length);
      setPreferredSize(new Dimension(80, 40));
      setValue((int)value.getValue());
      setPaintTicks(true);
      setPaintLabels(true);

      Hashtable labels = new Hashtable<Integer, Integer>();
      for (int i = 1; i <= 5; i++) {
        JLabel l = new JLabel(String.valueOf(i));
        labels.put(i, l);
      }
      setLabelTable(labels);

      setMinorTickSpacing(1);
      setMajorTickSpacing(4);

      addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          Thickness newValue = ((PointThicknessSlider)e.getSource()).getThicknessValue();
          PointThicknessChooserPanel.this.setThicknessValue(newValue);
          PointThicknessChooserPanel.this.repaint();
        }
      });
    }

    public Thickness getThicknessValue() {
      return Thickness.get(getValue());
    }
  }
}