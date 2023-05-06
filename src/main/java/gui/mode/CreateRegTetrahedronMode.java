package gui.mode;

import bodies.BodyType;
import builders.RegTetrahedronBuilder;
import geom.ExGeom;
import geom.Simplex3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 * Create mode of regtetrahedron by two points and angle
 *
 * @author kaznin
 */
public class CreateRegTetrahedronMode extends CreateBodyBy2PointsMode {

  public CreateRegTetrahedronMode(EdtController ctrl) {
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
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      try {
        Simplex3d tet = Simplex3d.regTetrahedronBy2PntsAngle(_anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
        Drawer.drawTetrahedron(ren, ColorGL.RED, null, TypeFigure.WIRE, tet.a(), tet.b(), tet.c(), tet.d());
      } catch (ExGeom ex) {
      }
      Drawer.drawPoint(ren, _anchor.get(1).getPoint(), _anchor.get(1).getState().getFocusedPointThickness() * 2);
    }
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint(), _anchor.get(0).getState().getFocusedPointThickness() * 2);
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину тетраэдра{MOUSELEFT}",
            "Выберите вторую вершину тетраэдра{MOUSELEFT}",
            "Выберите положение{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.REG_TETRAHEDRON.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.REG_TETRAHEDRON.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.REG_TETRAHEDRON.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Правильный тетраэдр</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_REGTETRAHEDRON;
  }

  @Override
  protected void create() {
    RegTetrahedronBuilder builder = new RegTetrahedronBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addAngle(valueAsDouble(ROT_ANGLE));
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
      createWithNameValidation();// создаём тетраэдр
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
