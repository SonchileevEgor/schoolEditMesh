package gui.toolbox.undo;

import gui.EdtController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;


/**
 * Список действий для Redo.
 * @author alexeev
 */
class RedoWidget extends JPopupMenu {
  private EdtController _ctrl;
  private int _counter;
  private List<JLabel> _labels;
  private JPanel _actionListPanel;
  private JLabel _header;

  public RedoWidget(EdtController ctrl){
    super();
    setLayout(new MigLayout());
    _counter = 0;
    _ctrl = ctrl;
    _labels = new ArrayList<>();
    _header = new JLabel("Повторить действий: " + _counter);
    _actionListPanel = new JPanel();

    JScrollPane actionListWrapper = new JScrollPane(_actionListPanel);
    _actionListPanel.setLayout(new BoxLayout(_actionListPanel, BoxLayout.Y_AXIS));

    add(_header, new CC().dockNorth());
    add(actionListWrapper, new CC().cell(0, 0).width("130!").height("160!"));

    update();
  }

  public final void update() {
    // чистим метки от слушателей.
    for (JLabel l : _labels) {
      for (MouseListener ml : l.getMouseListeners())
        l.removeMouseListener(ml);
      for (MouseMotionListener ml : l.getMouseMotionListeners())
        l.removeMouseMotionListener(ml);
    }

    _counter = 0;
    _labels.clear();
    _actionListPanel.removeAll();
    _header.setText("Повторить действий: " + _counter);

    ArrayList<String> descripts = _ctrl.getRedoDescripts();
    for (int i = descripts.size() - 1; i >= 0; i--) {
      // выводим в обратном порядке
      JLabel l = new JLabel(descripts.get(i));
      l.setOpaque(true);
      l.setBackground(Color.WHITE);
      final int undoCount = descripts.size() - i;
      l.addMouseMotionListener(new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
          _counter = undoCount;
          _header.setText("Повторить действий: " + _counter);
          for (int j = 0; j < _counter; j++) {
            _labels.get(j).setBackground(Color.LIGHT_GRAY);
          }
          for (int j = _counter; j < _labels.size(); j++) {
            _labels.get(j).setBackground(Color.WHITE);
          }
        }
      });
      l.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          for (int j = 0; j < _counter; j++) {
            _ctrl.redo();
          }
          RedoWidget.this.setVisible(false);
          _ctrl.redraw();
        }
      });
      _labels.add(l);
      _actionListPanel.add(l);
    }

    pack();
  }
}