package gui.elements;

import gui.laf.AppColor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author alexeev
 */
public class FeedbackPanel extends JPanel {
  private Timer timer;
  private JLabel _feedback;

  public FeedbackPanel() {
    super(new MigLayout(new LC().insetsAll("0").fillX()));
    _feedback = new JLabel();
    _feedback.setForeground(Color.BLACK);
    timer = new Timer(2000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _feedback.setText(null);
        timer.stop();
        setBackground(null);
      }
    });
    add(_feedback, new CC().cell(0, 0).height("20::").alignX("center"));
  }

  /**
   * throwing error message on the feedback panel
   * @param err error message
   */
  public void error(final String err) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        setBackground(AppColor.ORANGE.color());
        showMessage(err);
      }
    });
  }
  
  public void warning(final String warn) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        setBackground(AppColor.ORANGE_VERY_LIGHT.color());
        showMessage(warn);
      }
    });
  }
  
  public void message(final String msg) {
    SwingUtilities.invokeLater(new Runnable(){
      @Override
      public void run() {
        showMessage(msg);
      }
    });
  }
  
  private void showMessage(String msg) {
    _feedback.setText(msg);
    if (timer.isRunning()) {
      timer.restart();
    } else {
      timer.start();
    }
  }
}
