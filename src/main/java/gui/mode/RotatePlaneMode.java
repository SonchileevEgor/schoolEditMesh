package gui.mode;

import builders.BodyBuilder;
import static config.Config.PRECISION;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.ExNoBuilder;
import geom.SpherCoord;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

/**
 * Plane rotation scenario. User may rotate plane built by point and normal around that point.
 *
 * @author alexeev
 */
public class RotatePlaneMode extends ScreenMode {

  private int _currX;
  private int _currY;
  private SpherCoord _sphNormal; // spherical coords of normal
  private Vect3d _normal; // normal vector of the plane
  private String _planeID;

  public RotatePlaneMode(EdtController ctrl, String planeID) throws ExNoBody, ExNoAnchor {
    super(ctrl);
    _planeID = planeID;
  }

  @Override
  public void run() {
    super.run();
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      _normal = (Vect3d)_ctrl.getBuilder(_planeID).getValue(BodyBuilder.BLDKEY_NORMAL);
      _sphNormal = new SpherCoord(_normal);
      _ctrl.status().showMessage("Поворот плоскости с нажатой левой клавишей мыши{MOUSELEFT}");
      _ctrl.redraw();
    } catch (ExNoBuilder ex) {
      exit();
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ROTATE.getMediumIcon();
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
    return "Повернуть плоскость";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_ROTATE_PLANE;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        _currX = e.getX();
        _currY = e.getY();
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        int newX = e.getX();
        int newY = e.getY();
        int dx = newX - _currX;
        int dy = newY - _currY;
        _currX = newX;
        _currY = newY;
        _sphNormal.setPhi(_sphNormal.getPhi() + 0.0025 * dx);
        _sphNormal.setTheta(_sphNormal.getTheta() + 0.0025 * dy);
        _normal = _sphNormal.toCartesian();
        // меняем вектор нормали у плоскости
        _ctrl.status().showValue(String.format("Вектор нормали: %s",
                _normal.toString(PRECISION.value(), true)));
        try {
          _ctrl.getBuilder(_planeID).setValue(BodyBuilder.BLDKEY_NORMAL, _normal);
          _ctrl.rebuild();
          _ctrl.redraw();
        } catch (ExNoBuilder ex) {
          exit();
        }
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          _ctrl.setUndo("Поворот плоскости");
          exit();
        }
      }
    };
  }
}
