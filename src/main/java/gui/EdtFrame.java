package gui;

import gui.action.ActionFactory;
import gui.statusbar.StatusStrip;
import gui.toolbox.ToolPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author alexeev
 */
public class EdtFrame extends JFrame {
  private final EdtController _ctrl;

  /**
   * Build main application frame.
   *
   * @param ctrl
   * @param tp tool panel
   * @param sp status strip
   * @param centerPanel
   */
  public EdtFrame(EdtController ctrl, ToolPanel tp,
          StatusStrip sp, JSplitPane centerPanel) {
    super("3D–SchoolEdit | Редактор стереометрических чертежей");
    _ctrl = ctrl;

    setLayout(new MigLayout(new LC().fill().insetsAll("0"), new AC().fill(), new AC().fill()));
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setBackground(Color.WHITE);
    add(tp, new CC().dockNorth());
    add(sp, new CC().dockSouth());

    // Вот эта хрень вылетает на java 16. На java 8 работает.
    add(centerPanel, new CC().growX());

    double frameWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.75;
    double frameHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.75;

    setPreferredSize(new Dimension((int)frameWidth, (int)frameHeight));

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ActionFactory.APP_CLOSE.getAction(_ctrl).actionPerformed(
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
      }

      @Override
      public void windowDeactivated(WindowEvent e) {
        // When window lost focus, it stops listening keyboard and mouse wheel events.
        _ctrl.getMainCanvasCtrl().disableKeyDispatcher();
        _ctrl.getMainCanvasCtrl().disableMouseWheelDispatcher();
      }

      @Override
      public void windowActivated(WindowEvent e) {
        // When window gain focus, it starts listening keyboard and mouse wheel events.
        _ctrl.getMainCanvasCtrl().enableKeyDispatcher();
        _ctrl.getMainCanvasCtrl().enableMouseWheelDispatcher();
      }
    });
    pack();
  }
}