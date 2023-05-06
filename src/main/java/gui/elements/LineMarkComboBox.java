package gui.elements;

import gui.ui.EdtListCellRenderer;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComboBox;
import javax.swing.JList;
import opengl.LineMark;
import static opengl.LineMark.SINGLE;
import static opengl.LineMark.V_STYLE;
import static opengl.LineMark.W_STYLE;

/**
 * Input field for line marks.
 *
 * @author Vladislav Alexeev
 */
public class LineMarkComboBox extends JComboBox {
  private LineMark _value;

  public LineMarkComboBox(LineMark initialValue) {
    super(LineMark.values());
    setRenderer(new LineMarkRenderer());
    setSelectedItem(initialValue);
  }

  class LineMarkRenderer extends EdtListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value instanceof LineMark) {
        setLineMark((LineMark) value);
      } else {
        setLineMark(LineMark.NONE);
      }

      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      if (_value != null) {
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 1));
        int h = getHeight();
        int w = getWidth();
        g2d.drawLine(0, h / 2, w, h / 2);
        switch (_value) {
          case NONE:
            break;
          case SINGLE:
            g.drawLine(w / 2, 5, w / 2, h - 5);
            break;
          case DOUBLE:
            g.drawLine(w / 2 - 2, 5, w / 2 - 2, h - 5);
            g.drawLine(w / 2 + 2, 5, w / 2 + 2, h - 5);
            break;
          case V_STYLE:
            g.drawLine(w / 2, 5, w / 2, h - 5);
            g.drawLine(w / 2 - 3, 5, w / 2 - 3, h - 5);
            g.drawLine(w / 2 + 3, 5, w / 2 + 3, h - 5);
            break;
          case W_STYLE:
            g.drawLine(w / 2 - 2, 5, w / 2 - 2, h - 5);
            g.drawLine(w / 2 + 2, 5, w / 2 + 2, h - 5);
            g.drawLine(w / 2 - 6, 5, w / 2 - 6, h - 5);
            g.drawLine(w / 2 + 6, 5, w / 2 + 6, h - 5);
            break;
        }
      }
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(100, 20);
    }

    public void setLineMark(LineMark value) {
      _value = value;
    }
  }
}
