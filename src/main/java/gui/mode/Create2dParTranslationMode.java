package gui.mode;

import gui.EdtController;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Elena
 */
public class Create2dParTranslationMode extends CreateParallelTranslationMode {

  public Create2dParTranslationMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_TRANSLATION;
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
    if (_numOfChosenAnchors >= 2) {
      Drawer.drawPoint(ren, _anchor.get(1).getPoint());
      Drawer.draw2DArrow(ren, _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), TypeArrow.ONE_END);
    }
  }

}
