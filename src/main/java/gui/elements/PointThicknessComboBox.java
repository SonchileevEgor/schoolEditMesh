package gui.elements;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Input field for point thickness.
 * 
 * @author alexeev
 */
public class PointThicknessComboBox extends JComboBox {
  public PointThicknessComboBox(Thickness initialValue) {
    super(Thickness.values());
    setRenderer(new PointThicknessRenderer());
    setSelectedItem(initialValue);
  }
}

class PointThicknessRenderer extends JPanel implements ListCellRenderer {
  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    if (value instanceof Thickness) {
      setLineType((Thickness) value);
    } else {
      setLineType(null);
    }
    return this;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    if (_value != null) {
      g2d.setStroke(_value.getStroke());
      int centerX = getWidth() / 2;
      int centerY = getHeight() / 2;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.fillOval(centerX - (int)_value.getValue(), centerY - (int)_value.getValue(), (int)_value.getValue() * 2, (int)_value.getValue() * 2);
    }
  }

  private void setLineType(Thickness value) {
    _value = value;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(40, 20);
  }
  
  private Thickness _value;
}
