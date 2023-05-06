package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_CENTER;
import static builders.BodyBuilder.BLDKEY_NORMAL;
import static builders.BodyBuilder.BLDKEY_RADIUS;
import builders.CircleCenterRadiusBuilder;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.RADIUS;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import static opengl.TypeFigure.WIRE;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Лютенков
 */
public class Create2dCircleByCenterRadiusMode extends CreateBodyMode implements i_FocusChangeListener {

  public Create2dCircleByCenterRadiusMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
    canvas().getHighlightAdapter().setCreatePointMode(true);
    _param.setUsed(RADIUS, 1);
    _param.setValue(RADIUS, 0.5);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_BY_CENT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_BY_CENT.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String description() {
    return "<html><strong>Окружность</strong><br>по центру и радиусу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCLE_CENTER_RADIUS_2D;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 1) {
          changeValue(RADIUS, e.getWheelRotation());
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
        }
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 1) {
          changeValue(RADIUS, e);
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
          if (isChosen(RADIUS)) {
            create();
          }
        }
      }
    };
  }

  @Override
  protected void create() {
    CircleCenterRadiusBuilder builder = new CircleCenterRadiusBuilder(_name);
    builder.setValue(BLDKEY_NORMAL, new Vect3d(0, 0, 1));
    builder.setValue(BLDKEY_CENTER, id(0));
    builder.setValue(BLDKEY_RADIUS, valueAsDouble(RADIUS));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
//    _param.setUsed(RADIUS, 1);
    setValue(RADIUS, _ctrl.getMainCanvasCtrl().getMeshSize() * 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
    if (id == null || !isBody || _numOfChosenAnchors == 1) {
      return;
    }
    i_Anchor a = getPointAnchor(id);
    chooseAnchor(a);
    canvas().getHighlightAdapter().setCreatePointMode(false);
    canvas().getScaleAdapter().setBlocked(true); // avoid scaling on mouse wheel
    showValue(RADIUS);
    _ctrl.redraw();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите центр окружности{MOUSELEFT}", "Выберите радиус"
            + "{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
      Drawer.drawCircle(ren, valueAsDouble(RADIUS), _anchor.get(0).getPoint(), new Vect3d(0.0, 0.0, 1.0), WIRE);
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
