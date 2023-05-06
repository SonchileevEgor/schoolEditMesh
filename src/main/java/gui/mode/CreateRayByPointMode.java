package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.RayByPointBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.Ray3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Куганский
 */
public class CreateRayByPointMode extends CreateBodyMode implements i_FocusChangeListener {

  public CreateRayByPointMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setCreatePointMode(true);
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте вершину луча{MOUSELEFT}",
            "Выберите направление {UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.RAY.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    if (_numOfChosenAnchors == 1) {
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
      Ray3d n = Ray3d.ray2dByPoint(point(0), valueAsDouble(ROT_ANGLE));
      Drawer.drawRay(ren, n.pnt(), n.l());
    }
  }

  @Override
  protected void create() {
    RayByPointBuilder builder = new RayByPointBuilder(_name);
    builder.setValue(BLDKEY_A, _anchor.get(0).id());
    builder.setValue(BLDKEY_ALPHA, valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RAY_BY_POINT;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RAY.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RAY.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Луч</strong><br>по вершине и направлению";
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 1) {
          changeValue(ROT_ANGLE, e.getWheelRotation());
        }
        _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 1) {
          changeValue(ROT_ANGLE, e);
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
          if (isChosen(ROT_ANGLE)) {
            createWithNameValidation();
          }
        }
      }
    };
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
    if (_numOfChosenAnchors < 1) {
      chooseAnchor(a);
    }
    if (_numOfChosenAnchors == 1) {
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      showValue(ROT_ANGLE);
    }
  }

  @Override
  protected boolean isEnabled() {
    return !canvas().is3d() || _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
