package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.HalfCircBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.Arc3d;
import geom.ExDegeneration;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
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
 * @author rita
 */
public class CreateHalfCircleMode extends CreateBodyBy2PointsMode {

  public CreateHalfCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _numOfChosenAnchors; i++) {
      Drawer.drawPoint(ren, point(i));
    }
    if (_numOfChosenAnchors == 2) {
      try {
        Arc3d arc = Arc3d.halfCircleBy2PntsAngle(
                _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
        Drawer.drawArc(ren, arc, TypeFigure.CURVE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину полуокружности{MOUSELEFT}",
            "Выберите вторую вершину полуокружности{MOUSELEFT}",
            "Выберите положение {UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ARC.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ARC_HALF.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ARC_HALF.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Полуокружность</strong><br>по двум точкам на концах диаметра";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_HALF_CIRCLE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> arcParams = new HashMap<>();
    arcParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    arcParams.put(BLDKEY_A, new BuilderParam(BLDKEY_A, BuilderParam.VERT_1_DIAM_ALIAS, BuilderParamType.ANCHOR, _anchor.get(0).id()));
    arcParams.put(BLDKEY_B, new BuilderParam(BLDKEY_B, BuilderParam.VERT_2_DIAM_ALIAS, BuilderParamType.ANCHOR, _anchor.get(1).id()));
    arcParams.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION, valueAsDouble(ROT_ANGLE)));
    HalfCircBuilder builder = new HalfCircBuilder(arcParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(ROT_ANGLE, e.getWheelRotation());
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(ROT_ANGLE, e);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    if (isChosen(ROT_ANGLE)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
