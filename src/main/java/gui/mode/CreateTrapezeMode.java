package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.TrapezeBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExGeom;
import geom.Polygon3d;
import gui.EdtController;
import gui.IconList;
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
 * Trapeze building scenario. User chooses three points and length of one base.
 *
 * @author rita
 */
public class CreateTrapezeMode extends CreateBodyBy3PointsMode {

  public CreateTrapezeMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(WIDTH, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
  }

  @Override
  protected void doIfThreePointsChosen() {
    showValue(WIDTH);
  }

  @Override
  protected void doIfThreePointsChosen(MouseWheelEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
      showValue(WIDTH);
      _ctrl.redraw();
    }
  }

  @Override
  protected void doIfThreePointsChosen(KeyEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
    }
    if (isChosen(WIDTH)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую точку трапеции{MOUSELEFT}",
            "Отметьте вторую точку трапеции{MOUSELEFT}",
            "Отметьте третью точку трапеции{MOUSELEFT}",
            "Выберите длину основания трапеции{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.TRAPEZE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors < 3) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      for (int i = 0; i < _anchor.size(); i++) {
        Drawer.drawPoint(ren, _anchor.get(i).getPoint());
      }
      for (int i = 0; i < _anchor.size() - 1; i++) {
        Drawer.drawSegment(ren, _anchor.get(i).getPoint(), _anchor.get(i + 1).getPoint());
      }
    } else {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(1).getPoint(), _anchor.get(1).getState().getFocusedPointThickness() * 2);
      try {
        Polygon3d poly = Polygon3d.trapezeBy3PntsBaseLength(_anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(), _anchor.get(2).getPoint(), valueAsDouble(WIDTH));
        Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TRAPEZE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TRAPEZE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TRAPEZE.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> paralParams = new HashMap<String, BuilderParam>();
    paralParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    paralParams.put(BLDKEY_A, new BuilderParam(BLDKEY_A, BuilderParam.VERT_1_BASE_1_ALIAS, BuilderParamType.ANCHOR, _anchor.get(0).id()));
    paralParams.put(BLDKEY_B, new BuilderParam(BLDKEY_B, BuilderParam.VERT_2_BASE_1_ALIAS, BuilderParamType.ANCHOR, _anchor.get(1).id()));
    paralParams.put(BLDKEY_C, new BuilderParam(BLDKEY_C, BuilderParam.VERT_BASE_2_ALIAS, BuilderParamType.ANCHOR, _anchor.get(2).id()));
    paralParams.put("baseLength", new BuilderParam("baseLength", BuilderParam.LENGTH_OF_BASE_ALIAS, BuilderParamType.DOUBLE_POSITIVE, valueAsDouble(WIDTH)));
    TrapezeBuilder builder = new TrapezeBuilder(paralParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Трапеция</strong><br>по трем вершинам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
