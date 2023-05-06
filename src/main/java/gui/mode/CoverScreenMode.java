package gui.mode;

import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import javax.swing.Icon;
import opengl.drawing.DrawingQueue;

/**
 *
 * @author Vladimir
 */
public class CoverScreenMode extends ScreenMode {

  public CoverScreenMode(EdtController ctrl) {
    super(ctrl);
  }

  /**
   * Run this mode. Setting default mode of adapters.
   */
  @Override
  public void run() {
    stlCanvas().getScaleAdapter().reset();
    stlCanvas().getRotateAdapter().reset();
    stlCanvas().getCoverHighlightAdapter().reset();
    setCursor(Cursor.getDefaultCursor());
    _nativeAdapter = getNativeMouseListener();
    _nativeKeyAdapter = getNativeKeyListener();
    addMouseListener(_nativeAdapter);
    addKeyListener(_nativeKeyAdapter);
  }

  /**
   * Remove mouse and key listeners.
   */
    @Override
  public void dispose() {
    _ctrl.status().clear();
    DrawingQueue.clear();
    removeMouseListener(_nativeAdapter);
    removeKeyListener(_nativeKeyAdapter);
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_COVER;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.MOVE.getSmallIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.MOVE.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Режим просмотра моделей</strong>";
  }

  @Override
  protected boolean isEnabled() {
    return true;
  }
}
