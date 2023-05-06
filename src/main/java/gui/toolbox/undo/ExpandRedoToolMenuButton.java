package gui.toolbox.undo;

import editor.i_EditorChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author alexeev
 */
class ExpandRedoToolMenuButton extends JButton implements i_EditorChangeListener {
  private RedoWidget _widget;
  private EdtController _ctrl;

  public ExpandRedoToolMenuButton(EdtController ctrl) {
    super();
    _ctrl = ctrl;
    _ctrl.addEditorStateChangeListener(this);
    _widget = new RedoWidget(ctrl);
    setIcon(IconList.TRIANGLE_DOWN.getTinyIcon());
//    Util.setFixedSize(this, new Dimension(12, 24));
    setFocusable(false);

    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _widget.update();
        int y = ExpandRedoToolMenuButton.this.getHeight();
        int x = (ExpandRedoToolMenuButton.this.getLocationOnScreen().x +
                UndoPanel.WIDGET_SIZEX > java.awt.Toolkit.
                        getDefaultToolkit().getScreenSize().width) ?
                -UndoPanel.WIDGET_SIZEX :
                0;
        _widget.show(ExpandRedoToolMenuButton.this, x, y);
      }
    });
  }

  @Override
  public void updateEditorState() {
    setEnabled(!_ctrl.isRedoEmpty());
  }
}