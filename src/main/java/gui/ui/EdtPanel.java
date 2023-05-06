
package gui.ui;

import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * JPanel with default options included.
 * Requests focus on mouse press.
 *
 * @author alexeev
 */
public class EdtPanel extends JPanel {

  public EdtPanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    applyFocusSettings();
  }

  public EdtPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    applyFocusSettings();
  }

  public EdtPanel(LayoutManager layout) {
    super(layout);
    applyFocusSettings();
  }

  public EdtPanel() {
    super();
    applyFocusSettings();
  }

  private void applyFocusSettings() {
    setFocusable(true);
    setRequestFocusEnabled(true);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent me) {
        requestFocus();
      }
    });
  }
}
