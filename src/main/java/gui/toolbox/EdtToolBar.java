package gui.toolbox;

import com.jogamp.newt.event.KeyEvent;
import gui.EdtController;
import gui.IconList;
import gui.mode.ModeList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Parent class of toolbars.
 * @author alexeev
 */
public class EdtToolBar extends JPanel {
  // when menu is shown, another menu is shown by focusing, not by click
  boolean isOnHold;
  protected EdtController _ctrl;

  /**
   *
   * @param ctrl
   */
  protected EdtToolBar(EdtController ctrl) {
    super(new MigLayout(new LC().fillX().hideMode(3)));
    _ctrl = ctrl;
    setOpaque(false);
    initBrowseModeMenu();
  }

  private void initBrowseModeMenu() {
    EdtMenuItem defaultMenuItem = new EdtMenuItem(ModeList.MODE_DEFAULT.getAction(_ctrl));
    defaultMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false));
    EdtMenuItem moveSceneMenuItem = new EdtMenuItem(ModeList.MODE_MOVE_SCENE.getAction(_ctrl));
    EdtToolButton browseButton = new EdtToolButton(_ctrl, this, IconList.MOVE.getLargeIcon(),
      new EdtMenuItem[]{defaultMenuItem, moveSceneMenuItem});
    add(browseButton);
  }
}
