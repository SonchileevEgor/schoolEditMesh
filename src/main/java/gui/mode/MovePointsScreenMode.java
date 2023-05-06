package gui.mode;

import gui.EdtController;
import gui.IconList;
import gui.mode.adapters.MovePointsMouseAdapter;
import java.awt.Cursor;
import javax.swing.Icon;

/**
 * User can move points. Points move in the plane of vision.
 *
 * @author alexeev
 * @deprecated
 */
public class MovePointsScreenMode extends ScreenMode {

  private MovePointsMouseAdapter _movePointsContinuousAdapter;

  public MovePointsScreenMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.status().showMessage("Перемещайте отмеченные точки при помощи мыши{MOUSELEFT}");
    _movePointsContinuousAdapter = new MovePointsMouseAdapter(_ctrl, canvas());
    addMouseListener(_movePointsContinuousAdapter);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    removeMouseListener(_movePointsContinuousAdapter);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.MOVE_PNT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.MOVE_PNT.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String description() {
    return "<html><strong>Режим перемещения точек</strong><br>Тяните за выделенные точки для изменения сцены";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_MOVE_POINTS;
  }
}
