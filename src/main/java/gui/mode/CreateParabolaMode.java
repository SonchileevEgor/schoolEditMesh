package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.ParabolaBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExDegeneration;
import geom.Parabola3d;
import gui.EdtController;
import gui.IconList;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author VitaliiZah
 */
class CreateParabolaMode extends CreateBodyBy3PointsMode {

  public CreateParabolaMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void doIfThreePointsChosen() {
    createWithNameValidation();
  }

  @Override
  protected void doIfThreePointsChosen(MouseWheelEvent e) {
  }

  @Override
  protected void doIfThreePointsChosen(KeyEvent e) {
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(1).getPoint();
      }
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую точку директрисы{MOUSELEFT}",
            "Отметьте вторую точку директрисы{MOUSELEFT}",
            "Отметьте фокус параболы{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PARABOLA.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    // draw hyperbole on mouse move in 2D mode
    if (_numOfChosenAnchors == 2 && !is3d) {
      try {
        Parabola3d par = new Parabola3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawParabola(ren, par, TypeFigure.CURVE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PARABOLA;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PARABOLA.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PARABOLA.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> parabolaParams = new HashMap<String, BuilderParam>();
    parabolaParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    parabolaParams.put("pOnLine1", new BuilderParam("pOnLine1", "Точка на директрисе", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    parabolaParams.put("pOnLine2", new BuilderParam("pOnLine2", "Вторая точка на директрисе", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    parabolaParams.put("focus", new BuilderParam("focus", "Фокус", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    ParabolaBuilder builder = new ParabolaBuilder(parabolaParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Парабола</strong><br>по трем точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
