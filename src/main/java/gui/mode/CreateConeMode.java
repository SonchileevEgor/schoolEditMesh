package gui.mode;

import bodies.BodyType;
import builders.ConeBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import geom.Cone3d;
import geom.ExDegeneration;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.RADIUS;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Cone building scenario. User chooses base center, vertex and radius.
 *
 * @author alexeev
 */
public class CreateConeMode extends CreateBodyBy2PointsMode {

  public CreateConeMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(RADIUS, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CONE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CONE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Конус</strong><br>по вершине, центру и радиусу основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CONE;
  }

  @Override
  protected void doIfTwoPointsChosen() {
    canvas().getScaleAdapter().setBlocked(true);
    showValue(RADIUS);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(RADIUS, e.getWheelRotation());
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(RADIUS, e);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
    if (isChosen(RADIUS)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> coneParams = new HashMap<>();
    coneParams.put(ConeBuilder.BLDKEY_NAME, new BuilderParam(ConeBuilder.BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    coneParams.put(ConeBuilder.BLDKEY_VERTEX, new BuilderParam(ConeBuilder.BLDKEY_VERTEX, "Вершина конуса", BuilderParamType.ANCHOR, id(0)));
    coneParams.put(ConeBuilder.BLDKEY_CENTER, new BuilderParam(ConeBuilder.BLDKEY_CENTER, "Центр основания", BuilderParamType.ANCHOR, id(1)));
    coneParams.put(ConeBuilder.BLDKEY_RADIUS, new BuilderParam(ConeBuilder.BLDKEY_RADIUS, "Радиус основания", BuilderParamType.DOUBLE_POSITIVE, valueAsDouble(RADIUS)));
    ConeBuilder builder = new ConeBuilder(coneParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите вершину конуса{MOUSELEFT}",
            "Выберите центр основания{MOUSELEFT}",
            "Выберите радиус{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CONE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      try {
        Drawer.drawConeCarcass(ren, new Cone3d(_anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(), valueAsDouble(RADIUS)));
      } catch (ExDegeneration ex) {
      }
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(1).getPoint());
    }
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
