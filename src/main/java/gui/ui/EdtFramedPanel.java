package gui.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

/**
 * Wrapper for main panels.
 * With frame and optionally title.
 *
 * @author alexeev
 */
public class EdtFramedPanel extends EdtPanel {
  public EdtFramedPanel(String title) {
    super(new FlowLayout(FlowLayout.LEFT, 0, 0));

    setBorder(BorderFactory.createTitledBorder(
      BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
      title, TitledBorder.LEFT, TitledBorder.TOP));
  }

  public EdtFramedPanel() {
    super(new FlowLayout(FlowLayout.LEFT, 0, 0));

    setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
  }
}
