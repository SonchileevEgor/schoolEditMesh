package gui.mode;

import bodies.BodyType;
import builders.EllipticParaboloidBuilder;
import builders.param.BuilderParam;
import geom.EllipticParaboloid3d;
import geom.ExDegeneration;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
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
public class CreateEllipticParaboloidMode extends CreateBodyBy3PointsMode {

  public CreateEllipticParaboloidMode(EdtController ctrl) {
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
            "Отметьте фокус параболоида{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ELLIPTIC_PARABOLOID.getName(_ctrl.getEditor());
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
        EllipticParaboloid3d par = new EllipticParaboloid3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawEllipticParaboloid(ren, par, TypeFigure.CURVE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ELLIPTIC_PARABOLOID;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PARABOLOID_BY_3_PNTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PARABOLOID_BY_3_PNTS.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> paraboloidParams = new HashMap<String, BuilderParam>();
    EllipticParaboloidBuilder builder = new EllipticParaboloidBuilder();
    builder.setValue(EllipticParaboloidBuilder.BLDKEY_NAME, _name);
    builder.setValue(EllipticParaboloidBuilder.BLDKEY_POINT_ON_LINE_1, _anchor.get(0).id());
    builder.setValue(EllipticParaboloidBuilder.BLDKEY_POINT_ON_LINE_2, _anchor.get(1).id());
    builder.setValue(EllipticParaboloidBuilder.BLDKEY_FOCUS, _anchor.get(2).id());
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Эллиптический параболоид</strong><br>по трем точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

}
