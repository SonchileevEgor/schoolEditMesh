package gui.mode;

import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_BETA;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PointBuilderHull3;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.i_Anchor;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.COEFFICIENT;
import static gui.mode.param.CreateModeParamType.COEFFICIENT2;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
//import gui.EdtCanvasController;

/**
 *
 * @author Лютенков
 */
public class CreateHull3Mode extends CreateBodyBy3PointsMode {

  public CreateHull3Mode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setValue(COEFFICIENT, 0.5);
    _param.setValue(COEFFICIENT2, 0.0);
    _param.setUsed(COEFFICIENT, 1);
    _param.setUsed(COEFFICIENT2, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT2);
    // _ctrl.getCanvasCtrl().notifyModeChange(this);
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    i_Anchor a = getPointAnchor(id);

    if (a == null) {
      return;
    }
    if (_numOfChosenAnchors < 3) {
      if (_numOfChosenAnchors >= 1 && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }

      if (!checker.checkPointsNotCollinear(_anchor, a)) {
        return;
      }
      chooseAnchor(a);
    }
    if (_numOfChosenAnchors == 3) {
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      doIfThreePointsChosen();
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
      Drawer.drawPoint(ren, point(1));
    }
    if (_numOfChosenAnchors == 3) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Vect3d result = Vect3d.getVectFromAffineCoords(point(0), point(1), point(2), valueAsDouble(COEFFICIENT), valueAsDouble(COEFFICIENT2));
      Drawer.drawPoint(ren, point(0));
      Drawer.drawPoint(ren, point(1));
      Drawer.drawPoint(ren, point(2));
      Drawer.drawPoint(ren, result);
      if (!isChosen(COEFFICIENT)) {
        if (valueAsDouble(COEFFICIENT) - 1 > 0) {
          Drawer.drawSegmentStipple(ren, point(0), result);
        } else if (valueAsDouble(COEFFICIENT) < 0) {
          Drawer.drawSegmentStipple(ren, point(1), result);
        } else {
          Drawer.drawSegmentStipple(ren, point(0), point(1));
        }
        Drawer.drawSegmentStipple(ren, result, point(2));
      } else {
        Drawer.drawSegmentStipple(ren, point(0), result);
        Drawer.drawSegmentStipple(ren, point(1), result);
        Drawer.drawSegmentStipple(ren, point(2), result);
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}",
            "Выберите третью точку{MOUSELEFT}",
            "Выберите первый коэффициент{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите второй коэффициент{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.HULL_3.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.HULL_3.getLargeIcon();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_HULL_3;
  }

  @Override
  protected void setName() {
    _name = "";
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, BuilderParam.POINT_1_ALIAS, BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_B, new BuilderParam(BLDKEY_B, BuilderParam.POINT_2_ALIAS, BuilderParamType.ANCHOR, id(1)));
    params.put(BLDKEY_C, new BuilderParam(BLDKEY_C, BuilderParam.POINT_3_ALIAS, BuilderParamType.ANCHOR, id(2)));
    params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, BuilderParam.COEF_1_ALIAS, BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT)));
    params.put(BLDKEY_BETA, new BuilderParam(BLDKEY_BETA, BuilderParam.COEF_2_ALIAS, BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT2)));

    PointBuilderHull3 builder = new PointBuilderHull3(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Линейная комбинация</strong><br>трех точек";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void doIfThreePointsChosen() {
    showValue(COEFFICIENT);
  }

  @Override
  protected void doIfThreePointsChosen(MouseWheelEvent e) {
    if (!isChosen(COEFFICIENT)) {
      changeValue(COEFFICIENT, e.getWheelRotation());
      canvas().notifyModeParamChange(COEFFICIENT);
    } else if (!isChosen(COEFFICIENT2)) {
      changeValue(COEFFICIENT2, e.getWheelRotation());
      canvas().notifyModeParamChange(COEFFICIENT2);
    }
  }

  @Override
  protected void doIfThreePointsChosen(KeyEvent e) {
    if (_numOfChosenAnchors == 3) {
      if (!isChosen(COEFFICIENT)) {
        changeValue(COEFFICIENT, e);
        canvas().notifyModeParamChange(COEFFICIENT);
        if (isChosen(COEFFICIENT)) {
          showValue(COEFFICIENT2);
        }
      } else if (!isChosen(COEFFICIENT2)) {
        changeValue(COEFFICIENT2, e);
        canvas().notifyModeParamChange(COEFFICIENT2);
      }
      if (press.enter(e) && isChosen(COEFFICIENT2)) {
        createWithNameValidation();
      }
    }
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
