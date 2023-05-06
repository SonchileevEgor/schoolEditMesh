package gui.ui;

import gui.dialog.EdtDialog;
import gui.elements.FeedbackPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * default input dialog class
 * @author alexeev
 */
public class EdtInputDialog extends EdtDialog {
  /**
   * State of the dialog.
   * TRUE, if dialog is confirmed
   */
  public boolean accepted;

  protected Timer timer;
  protected FeedbackPanel _feedback;

  public EdtInputDialog(JFrame owner, String str) {
    super(owner, str);
    accepted = false;
    _feedback = new FeedbackPanel();

    setModal(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setLayout(new MigLayout(new LC().fillX().wrapAfter(2).insetsAll("5"),
            new AC().gap("2"), new AC().gap("5")));
    setLocationRelativeTo(getOwner());
  }

  /**
   * Вывод сообщения в панель обратной связи.
   * @param msg
   * @param status
   *    статус сообщения
   *    0 - обычное, 1 - предупреждение, 2 - ошибка.
   */
  public void showMessage(String msg, int status) {
    switch(status) {
      case 1:
        _feedback.warning(msg);
        break;
      case 2:
        _feedback.error(msg);
        break;
      default:
        _feedback.message(msg);
        break;
    }
    _feedback.error(msg);
  }

  /**
   * Действие по нажатию кнопки "ОК".
   * @return
   */
  protected ActionListener OKAction() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        accepted = true;
      }
    };
  }

  /**
   * Действие по нажатию кнопки "Отмена".
   * @return
   */
  protected ActionListener cancelAction() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        dispose();
      }
    };
  }

  /**
   * Добавить элемент ввода comp с подписью label.
   * @param label
   * @param comp
   */
  public void addItem(String label, JComponent comp) {
    add(new JLabel(label), new CC().alignX("left").width("100::"));
    add(comp, new CC().alignX("right").width("50::"));
  }

  /**
   * Add component below other elements with center alignment.
   * @param comp
   * @param alignX
   *  выравнивание (left / right / center)
   */
  public void addItem(JComponent comp, String alignX) {
    add(comp, new CC().spanX(2).alignX(alignX).width("150::"));
  }

  /**
   * Добавить кнопки.
   */
  public void addButtons() {
    JPanel buttonsPanel = new JPanel(new MigLayout(new LC().flowX()));

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Отмена");

    okButton.addActionListener(OKAction());
    cancelButton.addActionListener(cancelAction());
    getRootPane().setDefaultButton(okButton);
    buttonsPanel.add(okButton);
    buttonsPanel.add(cancelButton);

    add(buttonsPanel, new CC().spanX(2).alignX("center"));
  }

  /**
   * Добавить панель обратной связи.
   */
  public void addFeedback() {
    addItem(_feedback, "center");
  }
}