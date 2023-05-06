package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.RhombusByDiagonalBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExGeom;
import geom.Polygon3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import static gui.mode.param.CreateModeParamType.WIDTH;
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
public class CreateRhombusByDiagonalMode extends CreateBodyBy2PointsMode {

  public CreateRhombusByDiagonalMode(EdtController ctrl) {
    super(ctrl);
  }
  
  @Override
  public void run() {
    super.run();
    _param.setUsed(WIDTH, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
    _param.setUsed(ROT_ANGLE, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(WIDTH);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(WIDTH)){
      changeValue(WIDTH, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
    } else {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
      if(isChosen(WIDTH))
        showValue(ROT_ANGLE);
    } else {
      changeValue(ROT_ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
    if(isChosen(ROT_ANGLE)){
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину{MOUSELEFT}",
            "Выберите вторую вершину, диагонально противоположную первой{MOUSELEFT}",
            "Выберите длину второй диагонали{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите угол поворота ромба{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.RHOMBUS.getName(_ctrl.getEditor());
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RHOMBUS_BY_DIAGONAL;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RHOMBUS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RHOMBUS.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, BuilderParam.VERT_1_DIAG_ALIAS, BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_C, new BuilderParam(BLDKEY_C, BuilderParam.VERT_2_DIAG_ALIAS, BuilderParamType.ANCHOR, id(1)));
    params.put(RhombusByDiagonalBuilder.BLDKEY_DIAG_LENGTH,
            new BuilderParam(RhombusByDiagonalBuilder.BLDKEY_DIAG_LENGTH, BuilderParam.LENGTH_OF_DIAG_ALIAS, BuilderParamType.DOUBLE_POSITIVE, valueAsDouble(WIDTH)));
    params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION, valueAsDouble(ROT_ANGLE)));
    RhombusByDiagonalBuilder builder = new RhombusByDiagonalBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Ромб</strong><br>по двум вершинам диагонали";
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    if (_numOfChosenAnchors == 1)
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
    if (_numOfChosenAnchors == 2) {
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
      Drawer.drawPoint(ren, point(1), anchor(1).getState().getFocusedPointThickness() * 2);
      try {
        Polygon3d rho = Polygon3d.rhombusBy2PntsDiagLengthAng(point(0), point(1), valueAsDouble(WIDTH), valueAsDouble(ROT_ANGLE));
        Drawer.drawPolygon(ren, rho.points(), TypeFigure.WIRE);
      } catch (ExGeom ex) {}
    }
  }

  @Override
  protected void nativeDraw0(Render ren) { }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
