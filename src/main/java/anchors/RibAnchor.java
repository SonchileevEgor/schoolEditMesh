package anchors;

import config.Config;
import editor.AnchorType;
import editor.state.AnchorState;
import geom.Rib3d;
import geom.Vect3d;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeArrow;
import opengl.colortheme.CurrentTheme;
import opengl.textdrawinggl.TextDrawer;

/**
 *
 * @author alexeev
 */
public class RibAnchor extends Anchor {
  private Rib3d _rib;

  public RibAnchor(Rib3d rib, String pointID1, String pointID2, String anchorID,
          String bodyID, AnchorState state) {
    super(anchorID, bodyID, AnchorType.ANC_RIB, new StringBuilder(), state);
    _rib = rib;
    _pointIDs.add(pointID1);
    _pointIDs.add(pointID2);
  }

  @Override
  public Rib3d getRib() {
    return _rib;
  }

  @Override
  public boolean isEqualRib(String id1, String id2) {
    return (id1.equals(_pointIDs.get(0)) && id2.equals(_pointIDs.get(1))) ||
           (id1.equals(_pointIDs.get(1)) && id2.equals(_pointIDs.get(0)));
  }

  @Override
  public String toString(int precision, boolean is3d) {
    return _rib.toString(precision);
  }

  @Override
  public void drawCarcass(Render ren) {
    if (!_state.isVisible()) {
      return;
    }
    // рисуем отрезок
    Drawer.setObjectColorForAnchorLines(ren, _state);
    float thickness = _state.getCarcassThickness() * (_state.isChosen() ? 2 : 1);
    switch (_state.getLineType()) {
      case PLAIN:
        Drawer.drawSegment(ren, _rib.a(), _rib.b(), thickness);
        break;
      case DASHED:
        Drawer.drawSegmentStipple(ren, _rib.a(), _rib.b(), thickness);
        break;
    }
    // надпись над отрезком
    if (!_state.getLabel().isEmpty()) {
      Drawer.setObjectColor(ren, CurrentTheme.getColorTheme().getFontColorGL());
      TextDrawer.drawTextOnSegment(ren, _state.getLabel(),
              _rib.a(), _rib.b(),Config.TEXT_DRAWING_TYPE_ON_SEGMENT);
    }
    // метка на отрезке
    Drawer.drawMarkMedian(ren, _rib.center(), Vect3d.sub(_rib.b(), _rib.a()), _state.getMark());
  }

  @Override
  public void drawSurface(Render ren) {
    if (!_state.isVisible()) {
      return;
    }
    // стрелки
    Drawer.setObjectColorForAnchorLines(ren, _state);
    Drawer.drawArrow(ren, _rib.b(), _rib.a(), TypeArrow.ONE_END, _state.getArrowBegin());
    Drawer.drawArrow(ren, _rib.a(), _rib.b(), TypeArrow.ONE_END, _state.getArrowEnd());
  }

  @Override
  public String alias() {
    return "Отрезок";
  }
}
