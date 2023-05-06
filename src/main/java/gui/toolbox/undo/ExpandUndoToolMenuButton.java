package gui.toolbox.undo;

import editor.i_EditorChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;


class ExpandUndoToolMenuButton extends JButton implements i_EditorChangeListener {
  private UndoWidget _widget;
  private EdtController _ctrl;

  public ExpandUndoToolMenuButton(EdtController ctrl) {
    super();
    _ctrl = ctrl;
    _ctrl.addEditorStateChangeListener(this);
    _widget = new UndoWidget(ctrl);
    setIcon(IconList.TRIANGLE_DOWN.getTinyIcon());
    setFocusable(false);

    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _widget.update();
        int y = ExpandUndoToolMenuButton.this.getHeight();
        int x = (ExpandUndoToolMenuButton.this.getLocationOnScreen().x +
                UndoPanel.WIDGET_SIZEX > java.awt.Toolkit.
                        getDefaultToolkit().getScreenSize().width) ?
                -UndoPanel.WIDGET_SIZEX :
                0;
        _widget.show(ExpandUndoToolMenuButton.this, x, y);
      }
    });
  }

  @Override
  public void updateEditorState() {
    setEnabled(!_ctrl.isUndoEmpty());
  }
}
