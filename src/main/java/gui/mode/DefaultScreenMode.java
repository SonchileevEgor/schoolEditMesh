package gui.mode;

import bodies.BodyType;
import gui.EdtController;
import gui.IconList;
import gui.mode.adapters.MovePointsMouseAdapter;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

/**
 * Default screen mode. LMB - choose body / move point, RMB - properties, MMB - rotate, mouse wheel
 * - zoom.
 *
 * @author alexeev.
 */
public class DefaultScreenMode extends ScreenMode {

  private MovePointsMouseAdapter _movePointsAdapter;
  private ArrayList<BodyType> _defaultTypes;

  public DefaultScreenMode(EdtController ctrl) {
    super(ctrl);
    _defaultTypes = new ArrayList<BodyType>() {
      {
        add(BodyType.POINT);
        add(BodyType.ANGLE);
        addAll(BodyType.getLinearBodies());
        addAll(BodyType.getPlainBodies());
      }
    };
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(_defaultTypes);
    _movePointsAdapter = new MovePointsMouseAdapter(_ctrl, canvas());
    addMouseListener(_movePointsAdapter);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.getFocusCtrl().clearFocus();
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    removeMouseListener(_movePointsAdapter);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.MOVE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.MOVE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String description() {
    return "<html><strong>Стандартный режим просмотра</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_DEFAULT;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent me) {
        if (SwingUtilities.isLeftMouseButton(me)) {
          // выключаем вращение при перемещении якоря
          if (canvas().getNearestMovablePoint(me.getX(), me.getY()) != null) {
            canvas().getRotateAdapter().setBlocked(true);
            canvas().getScaleAdapter().setBlocked(true);
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent me) {
        canvas().getRotateAdapter().setBlocked(false);
        canvas().getScaleAdapter().setBlocked(false);
      }
    };
  }
}
