package gui.mode.adapters;

import bodies.PointBody;
import builders.i_MovablePointBuilder;
import editor.ExNoBody;
import editor.ExNoBuilder;
import geom.Vect3d;
import gui.EdtController;
import gui.MainEdtCanvasController;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author alexeev
 */
public class MovePointsMouseAdapter extends MainEdtMouseAdapter {
  private int _currX;
  private int _currY;
  private int _newX;
  private int _newY;
  private String _pointID;

  boolean _onHold = false; //флаг, показывающий, захвачена ли точка
  boolean _pointMoved = false;

  public MovePointsMouseAdapter(EdtController ctrl, MainEdtCanvasController canvas) {
    super(ctrl, canvas);
  }

  @Override
  public void mousePressed(MouseEvent me) {
    if (!SwingUtilities.isLeftMouseButton(me))
      return;

    _currX = me.getX();
    _currY = me.getY();
    _pointID = _mainCanvas.getNearestMovablePoint(_currX, _currY);
    if (_pointID != null) {
      _onHold = true;
      _canvas.redraw();
    }
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    if (!SwingUtilities.isLeftMouseButton(me) || !_onHold)
      return;
    _pointMoved = true;
    _newX = me.getX();
    _newY = me.getY();
    try {
      PointBody point = (PointBody)_ctrl.getBody(_pointID);
      Vect3d newPosition = point.getBehavior().position(
          _mainCanvas, point.point(), _currX, _currY, _newX, _newY);
      ((i_MovablePointBuilder)_ctrl.getBuilder(_pointID)).movePoint(newPosition);
      _currX = _newX;
      _currY = _newY;
      _ctrl.rebuild();
      _ctrl.redraw();
    } catch (ExNoBody | ExNoBuilder ex) {
      _onHold = false;
      _pointID = null;
    }
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    if( !SwingUtilities.isLeftMouseButton(me) )
      return;
    if( _pointMoved ){
      _ctrl.setUndo("Перемещение точки");
      _ctrl.notifyEditorStateChange();
    }
    _onHold = false;
    _pointMoved = false;
  }
}