package gui.toolbox.undo;

import gui.EdtController;
import gui.action.ActionFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Панель Undo/Redo.
 *
 * @author alexeev
 */
public class UndoPanel extends JPanel {
  public static final int WIDGET_SIZEX = 130;
  
  public UndoPanel(EdtController ctrl) {
    super();
    setLayout(new MigLayout(new LC(), new AC().sizeGroup("[24!][12!]").gap("0"), new AC()));
    JButton undoButton = new JButton(ActionFactory.UNDO.getAction(ctrl));
    undoButton.setFocusable(false);
    JButton redoButton = new JButton(ActionFactory.REDO.getAction(ctrl));
    redoButton.setFocusable(false);
    undoButton.setText("");
    redoButton.setText("");

    add(undoButton, new CC().width("24!").height("24!"));
    add(new ExpandUndoToolMenuButton(ctrl), new CC().width("12!").height("24!").wrap());
    add(redoButton, new CC().width("24!").height("24!"));
    add(new ExpandRedoToolMenuButton(ctrl), new CC().width("12!").height("24!"));
  }

}