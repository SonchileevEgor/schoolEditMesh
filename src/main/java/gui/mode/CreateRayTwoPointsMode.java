package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.RayTwoPointsBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoAnchor;
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
import opengl.colorgl.ColorGL;

/**
 *
 * @author Kurgansky
 */
public class CreateRayTwoPointsMode extends CreateBodyBy2PointsMode {

  public CreateRayTwoPointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void doIfTwoPointsChosen() {
    create();
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(0).getPoint();
      }
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}", "Выберите вторую точку{MOUSELEFT}");
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
    try {
      params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME,
              _ctrl.getAnchor(id(0)).getTitle() + _ctrl.getAnchor(id(1)).getTitle()));
    } catch (ExNoAnchor ex) {
      params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME,
              BodyType.RAY.getName(_ctrl.getEditor())));
    }
    params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "Вершина луча", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_B, new BuilderParam(BLDKEY_B, "Точка на продолжении луча", BuilderParamType.ANCHOR, id(1)));
    RayTwoPointsBuilder builder = new RayTwoPointsBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0));
      Drawer.drawPoint(ren, currPoint);
      Drawer.drawRayByPoints(ren, point(0), currPoint);
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RAY_TWO_POINTS;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RAY_TWO_POINTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RAY_TWO_POINTS.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Луч</strong><br>по двум точкам";
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
