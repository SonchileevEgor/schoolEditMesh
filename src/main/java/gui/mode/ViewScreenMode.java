package gui.mode;

import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import javax.swing.Icon;

/**
 * View screen mode. User can turn scene in three dimensions.
 *
 * @author alexeev
 * @deprecated
 */
public class ViewScreenMode extends ScreenMode {

  public ViewScreenMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setBlocked(true);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
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
  public String description() {
    return "<html><strong>Просмотр сцены</strong>";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_VIEW;
  }
}
