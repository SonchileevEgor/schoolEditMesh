package gui.layout;

import editor.i_ErrorListener;
import gui.EdtController;
import gui.IconList;
import gui.laf.AppColor;
import gui.ui.AlphaDecorator;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Panel for logging errors.
 *
 * @author alexeev
 */
public class EdtConsole extends JPanel implements i_ErrorListener {
  /**
   * Minimum available capacity value.
   */
  private static final int MIN_CAPACITY = 10;

  private final EdtController _ctrl;

  /**
   * Maximum number of messages stored in log panel.
   */
  private int _capacity = 30;

  /**
   * Number of log messages.
   */
  private int _size = 0;

  private LogPanel _logPanel;

  public EdtConsole(EdtController ctrl) {
    super(new MigLayout(new LC().fillX(), new AC().fill()));
    _ctrl = ctrl;
    setOpaque(false);
    _logPanel = new LogPanel();
    _logPanel.setBorder(null);
    _logPanel.setOpaque(false);
    JScrollPane consoleWrapper = new JScrollPane(_logPanel);
    consoleWrapper.getViewport().setOpaque(false);
    consoleWrapper.setBorder(null); // как ни странно, этот метод удаляет внутреннюю рамку.
    consoleWrapper.setBackground(AppColor.WHITE.opaque());
    add(new HeaderPanel(), new CC().dockNorth());
    add(new AlphaDecorator(consoleWrapper), new CC().height("120"));
  }

  /**
   * Set capacity value.
   * If given value less than MIN_CAPACITY,
   * set capacity to MIN_CAPACITY.
   *
   * @param cap
   */
  public void setCapacity(int cap) {
    _capacity = Math.max(MIN_CAPACITY, cap);
  }

  public int capacity() {
    return _capacity;
  }

  public int numMessages() {
    return _size;
  }

  public void addMessage(String msg) {
    _logPanel.enqueue(msg);
    if (_size == _capacity) {
      _logPanel.dequeue();
    } else {
      _size++;
    }
    update();
  }

  public void clear() {
    _logPanel.clear();
    _size = 0;
    update();
  }

  private void update() {
    revalidate();
    repaint();
  }

  @Override
  public void handleError(String msg) {
    addMessage(msg);
  }

  class LogPanel extends JPanel {
    public LogPanel() {
      super();
      setLayout(new MigLayout(new LC().fillX().flowY().insetsAll("2"), new AC(), new AC().gap("0")));
    }

    /**
     * Add message to the queue.
     * @param msg
     */
    public void enqueue(String msg) {
      JTextField msgLabel = new JTextField(msg);
      msgLabel.setEditable(false);
      msgLabel.setOpaque(false);
      msgLabel.setBorder(null);
      msgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
      add(msgLabel);
    }

    /**
     * Remove the first component of the queue.
     */
    public void dequeue() {
      remove(0);
    }

    public void clear() {
      removeAll();
    }
  }

  class HeaderPanel extends JPanel {
    public HeaderPanel() {
      super();
      setLayout(new MigLayout(new LC().fillX()));
      setBackground(AppColor.GRAY.opaque());
      JPanel buttonsPanel = new JPanel(new MigLayout());
      buttonsPanel.setOpaque(false);

      JButton clearButton = new JButton(IconList.ERASER.getTinyIcon());
      clearButton.setToolTipText("Очистить список");
      clearButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          EdtConsole.this.clear();
        }
      });
      buttonsPanel.add(clearButton, new CC().width("16!").height("16!"));

      JButton closeButton = new JButton(IconList.CROSS.getTinyIcon());
      closeButton.setToolTipText("Скрыть окно");
      closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          EdtConsole.this.setVisible(false);
          _ctrl.getLayoutController().notifyLayoutChanged();
        }
      });
      buttonsPanel.add(closeButton, new CC().width("16!").height("16!"));

      add(buttonsPanel, new CC().dockEast().gapX("0", "2"));
      add(new JLabel("Сообщения об ошибках"), new CC().dockWest().gapX("2", "0"));
    }
  }
}
