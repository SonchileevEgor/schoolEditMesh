package gui.toolbox;

import gui.EdtController;
import gui.action.EdtAction;
import gui.action.EdtModeAction;
import gui.laf.ToolButtonUI;
import gui.mode.i_ModeChangeListener;
import gui.mode.ScreenMode;
import gui.ui.EdtToolMenu;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;


/**
 * Button on the toolbar.
 *
 * @author alexeev
 */
class EdtToolButton extends JButton implements i_ModeChangeListener {
  private static Border CHECKED_BORDER;
  private static Border UNCHECKED_BORDER;
  static {
    UNCHECKED_BORDER = BorderFactory.createSoftBevelBorder(
          BevelBorder.LOWERED, Color.BLACK, Color.LIGHT_GRAY);
    CHECKED_BORDER = BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(new Color(.2f, .6f, 1f), 2), UNCHECKED_BORDER);
  }

  private EdtToolMenu _popup;
  private EdtToolBar _parent;
  private ArrayList<EdtAction> _actions;

  public EdtToolButton(EdtController ctrl, EdtToolBar parent, Icon icon) {
    super();
    setUI(ToolButtonUI.createUI(this));
    setIcon(icon);
    setBorder(UNCHECKED_BORDER);

    _parent = parent;
    _actions = new ArrayList<EdtAction>();
    _popup = new EdtToolMenu();
    _popup.setFocusable(false); // меню не может отобрать фокус у клавиши

    ctrl.getMainCanvasCtrl().addModeChangeListener(this);

    addMouseListener(new MouseAdapter(){
      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          _parent.isOnHold = true;
          _popup.show(EdtToolButton.this, 0, 36);
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        if (_parent.isOnHold) {
          _popup.show(EdtToolButton.this, 0, 36);
        }
      }
    });

    addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        _parent.isOnHold = false;
      }
    });
  }

  public EdtToolButton(EdtController ctrl, EdtToolBar parent, Icon icon, ArrayList<EdtAction> actions) {
    this(ctrl, parent, icon);
    _actions.addAll(actions);
    for (EdtAction a : actions) {
      _popup.add(a);
    }
  }

  public EdtToolButton(EdtController ctrl, EdtToolBar parent, Icon icon, EdtMenuItem[] items) {
    this(ctrl, parent, icon);
    for (EdtMenuItem mi : items) {
      EdtAction a = mi.getAction();
      _actions.add(a);
      _popup.add(mi);
    }
  }

  public void setHighlighted(boolean highlight) {
    setBorder(highlight ? CHECKED_BORDER : UNCHECKED_BORDER);
  }

  @Override
  public void updateMode(ScreenMode mode) {
    // есть ли в списке действий включающее данный режим
    boolean check = false;
    for (EdtAction a : _actions) {
      if (a.isMode()) {
        if (((EdtModeAction)a).mode().equals(mode.alias())) {
          check = true;
          setIcon(mode.getLargeIcon());
        }
      }
    }
    setHighlighted(check);
  }
}
