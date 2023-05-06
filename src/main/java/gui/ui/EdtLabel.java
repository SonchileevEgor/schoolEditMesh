package gui.ui;

import gui.IconList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Label with help pictogram.
 * Displays tooltip on pictogram hovering.
 *
 * @author alexeev
 */
public class EdtLabel extends JPanel {
  private static final Icon HELP_ICON;
  private static final Icon ALT_HELP_ICON;

  static {
    HELP_ICON = IconList.HELP_2.getSmallIcon();
    ALT_HELP_ICON = IconList.ALTHELP.getSmallIcon();
  }

  private final JLabel _helpLabel;

  /**
   * Label with help pictogram.
   * @param text
   * @param helpText
   */
  public EdtLabel(String text, String helpText) {
    super(new MigLayout(new LC().insetsAll("1").flowX().hideMode(3)));

    _helpLabel = new JLabel(HELP_ICON);
    JLabel textLabel = new JLabel(text);

    _helpLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        _helpLabel.setIcon(ALT_HELP_ICON);
        _helpLabel.revalidate();
        _helpLabel.repaint();
      }
      @Override
      public void mouseExited(MouseEvent e) {
        _helpLabel.setIcon(HELP_ICON);
        _helpLabel.revalidate();
        _helpLabel.repaint();
      }
    });

    _helpLabel.setToolTipText(helpText);

    add(_helpLabel);
    add(textLabel);
  }

  /**
   * Label without help pictogram.
   * @param text
   */
  public EdtLabel(String text) {
    this(text, null);
    _helpLabel.setVisible(false);
  }
}
