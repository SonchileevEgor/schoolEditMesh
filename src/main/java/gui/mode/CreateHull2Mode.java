package gui.mode;

import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PointBuilderHull2;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.i_Anchor;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.COEFFICIENT;
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
 * @author Лютенков
 */
public class CreateHull2Mode extends CreateBodyBy2PointsMode {

  public CreateHull2Mode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(COEFFICIENT, 1);
    _param.setValue(COEFFICIENT, 0.5);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
    //_ctrl.getCanvasCtrl().notifyModeChange(this);
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    i_Anchor a = getPointAnchor(id);
    //_param.setValue(COEFFICIENT, 0.5);
    //_param.setUsed(COEFFICIENT, 1);
    //_ctrl.getCanvasCtrl().notifyModeChange(this);
    //setValue(COEFFICIENT, 0.5); 
    if (a == null) {
      return;
    }
    if (_numOfChosenAnchors < 2) {
      if (_numOfChosenAnchors == 1
              && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      chooseAnchor(a);
    }
    if (_numOfChosenAnchors == 2) {
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      doIfTwoPointsChosen();
    }
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0));
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Vect3d result = Vect3d.conv_hull(point(0), point(1), valueAsDouble(COEFFICIENT));
      Drawer.drawPoint(ren, point(0));
      Drawer.drawPoint(ren, point(1));
      Drawer.drawPoint(ren, result);
      if (valueAsDouble(COEFFICIENT) - 1 > 0) {
        Drawer.drawSegmentStipple(ren, point(0), result);
      } else if (valueAsDouble(COEFFICIENT) < 0) {
        Drawer.drawSegmentStipple(ren, point(1), result);
      } else {
        Drawer.drawSegmentStipple(ren, point(0), point(1));
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}",
            "Выберите коэффициент{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.HULL_2.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.HULL_2.getLargeIcon();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_HULL_2;
  }

  @Override
  protected void setName() {
    _name = "";
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, BuilderParam.POINT_1_ALIAS, BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_B, new BuilderParam(BLDKEY_B, BuilderParam.POINT_2_ALIAS, BuilderParamType.ANCHOR, id(1)));
    params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT)));

    PointBuilderHull2 builder = new PointBuilderHull2(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Линейная комбинация</strong><br>двух точек";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(COEFFICIENT);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(COEFFICIENT, e.getWheelRotation());
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(COEFFICIENT, e);
    if (isChosen(COEFFICIENT)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
