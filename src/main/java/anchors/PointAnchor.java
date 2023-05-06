package anchors;

import config.Config;
import editor.AnchorType;
import editor.state.AnchorState;
import geom.Vect3d;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
import opengl.colortheme.CurrentTheme;
import opengl.sceneparameters.ViewMode;
import opengl.textdrawinggl.TextDrawer;

/**
 * Класс якоря-точки.
 * @author alexeev
 */
public class PointAnchor extends Anchor {
  private Vect3d _point;

  public PointAnchor(Vect3d point, String anchorID, String bodyID,
          StringBuilder anchorName, AnchorState state){
    super(anchorID, bodyID, AnchorType.ANC_POINT, anchorName, state);
    _point = point;
    _pointIDs.add(anchorID);
  }

  @Override
  public Vect3d getPoint(){
    return _point;
  }

  @Override
  public String toString(int precision, boolean is3d) {
    return _point.toString(precision, is3d);
  }

  @Override
  public void drawCarcass(Render ren) {
    if (!_state.isVisible())
      return;
    ColorGL pointFrameColor = (ren.getViewMode() == ViewMode.PENCIL) ? ColorGL.BLACK : ColorGL.RED;
    Drawer.setObjectColorForAnchorPoints(ren, _state);
    float thickness = _state.isChosen() ?
            _state.getFocusedPointThickness() * 2 :
            _state.getPointThickness() * 2;
    if (isMovable()) {
      Drawer.drawPointWithFrame(ren, _point, thickness, 2, pointFrameColor);
    } else {
      Drawer.drawPoint(ren, _point, thickness);
    }
    if (_state.isTitleVisible()) {
      Drawer.setObjectColor(ren, CurrentTheme.getColorTheme().getFontColorGL());
      TextDrawer.drawText(ren, _title.toString(), _point, Config.TEXT_DRAWING_TYPE_ON_POINT);
    }
  }

  @Override
  public void drawSurface(Render ren) {
    // NOTHING TO DO HERE
  }

  @Override
  public String alias() {
    return "точка";
  }
}
