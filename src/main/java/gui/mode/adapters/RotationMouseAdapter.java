package gui.mode.adapters;

import gui.BasicEdtCanvasController;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * Mouse adapter for rotation screen mode.
 *
 * @author alexeev
 */
public class RotationMouseAdapter extends BasicEdtMouseAdapter {
  /**
   * Маски события мыши, используемая для вращения сцены
   */
  private int _rotationLeftMask = InputEvent.SHIFT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK;
  private int _rotationMidMask = InputEvent.BUTTON2_DOWN_MASK;
          
  /**
   * Флаг, показывающий, нажата ли в текущий момент кнопка.
   * Нужен для того, чтобы событие dragged не обрабатывалось без события pressed.
   * Такое может происходить, когда фокус был не на полотне,
   * а при переходе на полотно мы сразу повели мышь с нажатой клавишей.
   */
  private boolean _isPressed = false;

  private int _currX;
  private int _currY;

  public RotationMouseAdapter(BasicEdtCanvasController canvas) {
    super(canvas);
  }

  @Override
  public void reset() {
    super.reset();
    _rotationLeftMask = InputEvent.SHIFT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK;
    _rotationMidMask = InputEvent.BUTTON2_DOWN_MASK;
  }

  @Override
  public void mousePressed(MouseEvent me) {
    // блокировка вращения
    if (!_block) {
      if( me.getModifiersEx() == _rotationLeftMask ||
          me.getModifiersEx() == _rotationMidMask ){
        _isPressed = true;
        _currX = me.getX();
        _currY = me.getY();
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    // блокировка вращения
    if (!_block && _isPressed) {
      if( me.getModifiersEx() == _rotationLeftMask ||
          me.getModifiersEx() == _rotationMidMask ){
        int newX = me.getX();
        int newY = me.getY();
        int dx = _currX - newX;
        int dy = _currY - newY;
        _currX = newX;
        _currY = newY;
        if (_canvas.is3d()) {
          _canvas.turn(0.0025 * dx, 0.0025 * dy);
          _canvas.redraw();
        }
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    if (!_block) {
      _isPressed = false;
    }
  }
  
  /**
   * Set MMB as rotation button.
   * @param isMiddleButton
   * @param isShiftUsed
   */
  public void setMouseButtons(boolean isMiddleButton, boolean isShiftUsed) {
    _rotationMidMask = isMiddleButton ? InputEvent.BUTTON2_DOWN_MASK : 0;
    _rotationLeftMask = isMiddleButton ?
            (isShiftUsed ? InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK : 0) :
            InputEvent.BUTTON1_DOWN_MASK;
  }
}
