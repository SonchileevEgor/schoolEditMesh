package gui.mode;

import static config.Config.ANGLE_STEP;
import geom.Checker;
import gui.EdtController;
import gui.mode.param.ModeParam;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Preobrazhenskaia
 */
public class Press {

  private EdtController _ctrl;

  Press(EdtController ctrl) {
    _ctrl = ctrl;
  }

  public double defaultLength() {
    return _ctrl.getMainCanvasCtrl().getMeshSize();
  }

  public boolean enter(KeyEvent e) {
    return e.getKeyCode() == KeyEvent.VK_ENTER;
  }

  public boolean up(KeyEvent e) {
    return e.getKeyCode() == KeyEvent.VK_UP;
  }

  public boolean down(KeyEvent e) {
    return e.getKeyCode() == KeyEvent.VK_DOWN;
  }

  public double incAngle(double angle) {
    return (angle + ANGLE_STEP.value()) % (Math.PI * 2);
  }

  public double decAngle(double angle) {
    return (angle - ANGLE_STEP.value()) % (Math.PI * 2);
  }

  public double decDouble(double value) {
    double result = value - delta();
    if (result >= minDouble() - Checker.eps()) {
      return result;
    } else {
      return value;
    }
  }

  public double incDouble(double value) {
    double result = value + delta();
    if (result <= ModeParam.MAX_DOUBLE + Checker.eps()) {
      return result;
    } else {
      return value;
    }
  }

  public int incInt(int n) {
    if (n < ModeParam.MAX_INT) {
      n++;
    }
    return n;
  }

  public int decInt(int n) {
    if (n > ModeParam.MIN_INT) {
      n--;
    }
    return n;
  }

  public double wheelChangeDouble(double value, MouseWheelEvent e) {
    double result = value - e.getWheelRotation() * delta();
    if (result >= minDouble() - Checker.eps() && result <= ModeParam.MAX_DOUBLE + Checker.eps()) {
      return result;
    } else {
      return value;
    }
  }

  public double wheelChangeAngle(double angle, MouseWheelEvent e) {
    return (angle - e.getWheelRotation() * ANGLE_STEP.value()) % (Math.PI * 2);
  }

  public int wheelChangeInt(int n, MouseWheelEvent e) {
    int result = n - e.getWheelRotation();
    if (result <= ModeParam.MAX_INT && result >= ModeParam.MIN_INT) {
      return result;
    } else {
      return n;
    }
  }

  private double minDouble() {
    return _ctrl.getMainCanvasCtrl().getMeshSize() / 10;
  }

  private double delta() {
    return _ctrl.getMainCanvasCtrl().getMeshSize() / 5;
  }
}
