package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.HyperboleBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExDegeneration;
import geom.Hyperbole3d;
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
class CreateHyperboleMode extends CreateBodyBy3PointsMode {

  public CreateHyperboleMode(EdtController ctrl) {
    super(ctrl);
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
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первый фокус гиперболы{MOUSELEFT}",
            "Отметьте второй фокус гиперболы{MOUSELEFT}",
            "Отметьте точку на гиперболе{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.HYPERBOLE.getName(_ctrl.getEditor());
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
        Hyperbole3d hyp = new Hyperbole3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawHyperbole(ren, hyp, TypeFigure.CURVE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_HYPERBOLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.HYPERBOLE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.HYPERBOLE.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> hyperboleParams = new HashMap<>();
    hyperboleParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    hyperboleParams.put("focus1", new BuilderParam("focus1", "Первый фокус", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    hyperboleParams.put("focus2", new BuilderParam("focus2", "Второй фокус", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    hyperboleParams.put("pointOnBound", new BuilderParam("pointOnBound", "Точка, лежащая на гиперболе", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    HyperboleBuilder builder = new HyperboleBuilder(hyperboleParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Гипербола</strong><br>по трем точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
