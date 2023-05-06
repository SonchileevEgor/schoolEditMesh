package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_C1;
import static builders.BodyBuilder.BLDKEY_C2;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_RADIUS;
import builders.CylinderBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.RADIUS;
import java.awt.Cursor;
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
 * Cylinder building scenario.
 *
 * @author alexeev
 */
public class CreateCylinderMode extends CreateBodyBy2PointsMode {

  public CreateCylinderMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    setCursor(Cursor.getDefaultCursor());
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
    return IconList.CYLINDER.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CYLINDER.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Цилиндр</strong><br>по центрам оснований и радиусу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CYLINDER;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_C1, new BuilderParam(BLDKEY_C1, "Центр первого основания", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_C2, new BuilderParam(BLDKEY_C2, "Центр второго основания", BuilderParamType.ANCHOR, id(1)));
    params.put(BLDKEY_RADIUS, new BuilderParam(BLDKEY_RADIUS, "Радиус основания", BuilderParamType.DOUBLE_POSITIVE, valueAsDouble(RADIUS)));
    CylinderBuilder builder = new CylinderBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
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
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите центр нижнего основания{MOUSELEFT}",
            "Выберите центр верхнего основания{MOUSELEFT}",
            "Выберите радиус{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CYLINDER.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawCylinder(ren, valueAsDouble(RADIUS),
              _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), TypeFigure.WIRE, 2);
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
