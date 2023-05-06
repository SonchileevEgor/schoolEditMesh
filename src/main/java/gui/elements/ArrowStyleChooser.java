package gui.elements;

import gui.ui.EdtListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComboBox;
import javax.swing.JList;
import opengl.StyleArrow;

/**
 * Input field for arrow styles.
 *
 * @author alexeev
 */
public class ArrowStyleChooser extends JComboBox {
  private final boolean _leftOriented;
  private StyleArrow _value;

  /**
   *
   * @param initialValue
   * @param leftOriented
   *    Orientation of arrow.
   */
  public ArrowStyleChooser(StyleArrow initialValue, boolean leftOriented) {
    super(StyleArrow.values());
    setRenderer(new ArrowStyleRenderer());
    setSelectedItem(initialValue);
    _leftOriented = leftOriented;
  }

  class ArrowStyleRenderer extends EdtListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value instanceof StyleArrow) {
        setArrowStyle((StyleArrow) value);
      } else {
        setArrowStyle(StyleArrow.getDefault());
      }
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int w = getWidth();
      int h = getHeight();
      switch( _value ){
        case CONE:
          g.fillOval(w * 9 / 20, h / 4, w / 10, h / 2);
          g.fillPolygon(new int[]{ w / 2, w / 2, _leftOriented ? w / 4 : w * 3 / 4 },
                        new int[]{ h / 4, h * 3 / 4, h / 2 }, 3);
          g.setColor(Color.WHITE);
          g.fillOval(w * 9 / 20 + 1, h / 4 + 1, w / 10 - 2, h / 2 - 2);
          g.setColor(Color.BLACK);
          g.drawLine(_leftOriented ? w * 3 / 4 : w / 4, h / 2, w / 2, h / 2);
          break;
        case LINES:
          g.drawLine(w / 4, h / 2, w * 3 / 4, h / 2);
          if( _leftOriented ){
            g.drawLine(w / 2, h / 4, w / 4, h / 2);
            g.drawLine(w / 2, h * 3 / 4, w / 4, h / 2);
          } else {
            g.drawLine(w / 2, h / 4, w * 3 / 4, h / 2);
            g.drawLine(w / 2, h * 3 / 4, w * 3 / 4, h / 2);
          }
          break;
        case NONE:
          g.drawLine(w / 4, h / 2, w * 3 / 4, h / 2);
          break;
      }
    }

    @Override
    public Dimension getPreferredSize() {
      //!! TODO: Убрать этот метод
      return new Dimension(100, 20);
    }

    private void setArrowStyle(StyleArrow value) {
      _value = value;
    }
  }
}