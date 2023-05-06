package gui.mode;

import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import javax.swing.Icon;

/**
 *
 * @author alexeev
 */
public class RotateSceneMode extends ScreenMode {

  public RotateSceneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getRotateAdapter().setMouseButtons(false, false);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ROTATE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ROTATE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String description() {
    return "<html><strong>Вращение сцены</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_ROTATE_SCENE;
  }
}
