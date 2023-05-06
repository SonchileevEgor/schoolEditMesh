package gui.mode.adapters;

import editor.ExNoBuilder;
import editor.i_Anchor;
import editor.i_BodyBuilder;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import gui.EdtController;
import gui.MainEdtCanvasController;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author alexeev
 */
public class MoveSceneMouseAdapter extends MainEdtMouseAdapter {
  private Vect3d _position;
  private boolean _onHold = false;
  boolean _sceneMoved = false;

  public MoveSceneMouseAdapter(EdtController ctrl, MainEdtCanvasController canvas) {
    super(ctrl, canvas);
    _onHold = false;
  }

   @Override
  public void mousePressed(MouseEvent e) {
    if (!SwingUtilities.isLeftMouseButton(e))
      return;
    _onHold = true;
    try {
      _position = _canvas.getSightRay(e.getX(), e.getY()).
          intersectionWithPlane(_canvas.getScene().getPlaneByPoint(new Vect3d(0, 0, 0)));
    } catch (ExDegeneration ex) {
      _position = new Vect3d(0, 0, 0);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (!SwingUtilities.isLeftMouseButton(e) || !_onHold)
      return;
    _sceneMoved = true;
    try {
      Ray3d sight = _canvas.getSightRay(e.getX(), e.getY());
      // Плоскость, проходящая через 0, на которую мы смотрим.
      Plane3d sightPlane = _canvas.getScene().getPlaneByPoint(new Vect3d(0, 0, 0));
      Vect3d position = sight.intersectionWithPlane(sightPlane);
      Vect3d offset = Vect3d.sub(position, _position);
      for (i_Anchor a : _mainCanvas.getBasicPoints()) {
        try {
          i_BodyBuilder pointBuilder = _ctrl.getBuilder(a.getBodyID());
          Vect3d oldVect = (Vect3d)pointBuilder.getValue("P");
          pointBuilder.setValue("P", Vect3d.sum(oldVect, offset));
        } catch (ExNoBuilder ex_b) {}
      }
      _position = position;
      _ctrl.rebuild();
      _ctrl.redraw();
    } catch (ExDegeneration ex) {}
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (!SwingUtilities.isLeftMouseButton(e))
      return;
    if( _onHold && _sceneMoved ){
      _ctrl.setUndo("Перемещение сцены");
      _ctrl.notifyEditorStateChange();
    }
    _onHold = false;
    _sceneMoved = false;
  }
}
