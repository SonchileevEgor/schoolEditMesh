package gui.mode;

import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.CURSOR_GRAB;
import gui.mode.adapters.BasicEdtMouseAdapter;
import gui.mode.adapters.MoveSceneMouseAdapter;
import javax.swing.Icon;

/**
 * Scene move scenario. User may turn a whole scene. Only "basic" point moves, and scene rebuilds.
 *
 * @author alexeev
 */
public class MoveSceneMode extends ScreenMode {

  private BasicEdtMouseAdapter _moveSceneAdapter;

  public MoveSceneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    setCursor(CURSOR_GRAB);
    _ctrl.status().showMessage("Перемещайте отмеченные точки при помощи мыши{MOUSELEFT}");
    _moveSceneAdapter = new MoveSceneMouseAdapter(_ctrl, canvas());
    addMouseListener(_moveSceneAdapter);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    removeMouseListener(_moveSceneAdapter);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.MOVE_SCENE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.MOVE_SCENE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String description() {
    return "<html><strong>Перемещение сцены</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_MOVE_SCENE;
  }
}
