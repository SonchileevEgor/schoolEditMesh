package anchors;

import editor.AnchorType;
import editor.state.AnchorState;
import geom.Polygon3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;

/**
 *
 * @author alexeev
 */
public class PolyAnchor extends Anchor {
  private Polygon3d _poly;

  public PolyAnchor(Polygon3d poly, ArrayList<String> pointIDs, String anchorID, String bodyID,
          AnchorState state){
    super(anchorID, bodyID, AnchorType.ANC_POLY, new StringBuilder(), state);
    _poly = poly;
    _pointIDs.addAll(pointIDs);
  }

  @Override
  public Polygon3d getPoly(){
    return _poly;
  }

  @Override
  public boolean isEqualPlane(List<String> anchorIDs) {
    if (anchorIDs.size() != _pointIDs.size())
      return false;

    ArrayList<String> my_IDs = new ArrayList<String>(_pointIDs);
    ArrayList<String> new_IDs = new ArrayList<String>(anchorIDs);
    Collections.sort(my_IDs);
    Collections.sort(new_IDs);
    for (int i = 0; i < new_IDs.size(); i++) {
      if (new_IDs.get(i).compareTo(my_IDs.get(i)) != 0){
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString(int precision, boolean is3d) {
    return "Многоугольник";
  }

  @Override
  public void drawCarcass(Render ren) {
    // NOTHING TO DO HERE
  }

  @Override
  public void drawSurface(Render ren) {
    if (!_state.isVisible()) {
      return;
    }
    Drawer.setObjectColorForAnchorPolygons(ren, _state);
    Drawer.drawPolygon(ren, _poly.points(), TypeFigure.SOLID);
  }

  @Override
  public String alias() {
    return _poly.isTriangle() ? "треугольник" : String.format("%d-угольник", _pointIDs.size());
  }
}
