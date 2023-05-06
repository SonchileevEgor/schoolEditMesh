package anchors;

import editor.AnchorType;
import editor.state.AnchorState;
import geom.Circle3d;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;

/**
 *
 * @author alexeev
 */
public class DiskAnchor extends Anchor {
  private Circle3d _disk;

  public DiskAnchor(Circle3d disk, String centerID, String anchorID, String bodyID,
          StringBuilder anchorName, AnchorState state){
    super(anchorID, bodyID, AnchorType.ANC_DISK, anchorName, state);
    _disk = disk;
    _pointIDs.add(centerID);
  }

  @Override
  public Circle3d getDisk(){
    return _disk;
  }

  public void drawCircle(Render ren) {
    if (!_state.isVisible()) {
      return;
    }
    Drawer.setObjectColorForAnchorCircles(ren, _state);
    Drawer.setLineWidth(ren, _state.isChosen() ? _state.getCarcassThickness() * 1.5 :
                                                 _state.getCarcassThickness());
    Drawer.drawCircle(ren, _disk.radiusLength(), _disk.center(), _disk.normal(), TypeFigure.WIRE);
  }

  public void drawDisk(Render ren) {
    if (!_state.isVisible())
      return;
    if (_state.isFilled()) {
      Drawer.setObjectColorForAnchorPolygons(ren, _state);
      Drawer.drawCircle(ren, _disk.radiusLength(), _disk.center(), _disk.normal(), TypeFigure.SOLID);
    }
  }

  @Override
  public String toString(int precision, boolean is3d) {
    return _state.isFilled() ? "Круг" : "Окружность";
  }

  @Override
  public void drawCarcass(Render ren) {
    drawCircle(ren);
  }

  @Override
  public void drawSurface(Render ren) {
    drawDisk(ren);
  }

  @Override
  public String alias() {
    return _state.isFilled() ? "круг" : "окружность";
  }
}
