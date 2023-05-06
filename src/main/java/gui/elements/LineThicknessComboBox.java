package gui.elements;

import gui.ui.EdtListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 * Input field for line thickness.
 *
 * @author alexeev
 */
public class LineThicknessComboBox extends JComboBox {
  private Thickness _value;

  public LineThicknessComboBox(Thickness initialValue) {
    super(Thickness.values());
    setRenderer(new LineThicknessRenderer());
    setSelectedItem(initialValue);
  }

  class LineThicknessRenderer extends EdtListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value instanceof Thickness) {
        setThickness((Thickness) value);
      } else {
        setThickness(null);
      }
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      if (_value != null) {
        g2d.setStroke(_value.getStroke());
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
      }
    }

    @Override
    public Dimension getPreferredSize() {
      //!! TODO: Убрать этот метод
      return new Dimension(100, 20);
    }

    private void setThickness(Thickness value) {
      _value = value;
    }
  }
}