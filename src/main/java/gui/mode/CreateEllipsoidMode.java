package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.EllipsoidBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.Ellipsoid3d;
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
 * @author Vitaliy
 */
public class CreateEllipsoidMode extends CreateBodyBy3PointsMode {

  CreateEllipsoidMode(EdtController ctrl) {
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
    _msg.setMessage("Отметьте первый фокус эллипсоида{MOUSELEFT}",
            "Отметьте второй фокус эллипсоида{MOUSELEFT}",
            "Отметьте точку на эллипсоиде{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ELLIPSOID.getName(_ctrl.getEditor());
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
        Ellipsoid3d ell = new Ellipsoid3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawEllipsoid(ren, ell, TypeFigure.WIRE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> ellipsoidParams = new HashMap<>();
    ellipsoidParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    ellipsoidParams.put("focus1", new BuilderParam("focus1", "Первый фокус", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    ellipsoidParams.put("focus2", new BuilderParam("focus2", "Второй фокус", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    ellipsoidParams.put("pointOnBound", new BuilderParam("pointOnBound", "Точка на границе эллипсоида", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    EllipsoidBuilder builder = new EllipsoidBuilder(ellipsoidParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ELLIPSOID;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ELLIPSOID.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ELLIPSOID.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Эллипсоид</strong><br>по трем точкам";
  }

}
