package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.EllipseMainBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.EllipseMain3d;
import geom.ExDegeneration;
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
public class CreateEllipseMainMode extends CreateBodyBy3PointsMode {

  public CreateEllipseMainMode(EdtController ctrl) {
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
    _msg.setMessage("Отметьте первый фокус эллипса{MOUSELEFT}",
            "Отметьте второй фокус эллипса{MOUSELEFT}",
            "Отметьте точку на эллипсе{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ELLIPSE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    // draw ellipse on mouse move in 2D mode
    if (_numOfChosenAnchors == 2 && !is3d) {
      try {
        EllipseMain3d ell = new EllipseMain3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawEllipse(ren, ell, TypeFigure.WIRE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ELIPSE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ELLIPSE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ELLIPSE.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> ellipseParams = new HashMap<>();
    ellipseParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    ellipseParams.put("focus1", new BuilderParam("focus1", "Первый фокус", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    ellipseParams.put("focus2", new BuilderParam("focus2", "Второй фокус", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    ellipseParams.put("pointOnBound", new BuilderParam("pointOnBound", "Точка на границе эллипса", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    EllipseMainBuilder builder = new EllipseMainBuilder(ellipseParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Эллипс</strong><br>по трем точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
