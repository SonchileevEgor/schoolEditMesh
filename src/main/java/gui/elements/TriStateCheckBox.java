package gui.elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;

/**
 *
 * @author alexeev
 */
public class TriStateCheckBox extends JCheckBox {
  private boolean _clicked = false;
  
  public TriStateCheckBox() {
    super();
    setSelected(false);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!_clicked) {
          _clicked = true;
          repaint();
        }
      }
    });
  }
  
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!_clicked) {
      Graphics2D gr = (Graphics2D)g;
      g.setColor(getBackground());
      gr.fillRect(6, 6, getWidth() - 12, getHeight() - 12);
      g.setColor(getForeground());
      gr.fillPolygon(new int[]{6, getWidth() - 6, 6}, new int[]{6, 6, getHeight() - 6}, 3);
    }
  }
}
