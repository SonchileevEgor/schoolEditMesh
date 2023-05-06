package gui.mode;

import bodies.BodyType;
import builders.AllBuildersManager;
import builders.SphereBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.RADIUS;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Sphere building scenario. User chooses center and radius.
 *
 * @author alexeev
 */
public class CreateSphereMode extends CreateBodyMode implements i_FocusChangeListener {

  public CreateSphereMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());
    _param.setUsed(RADIUS, 1);
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
    return IconList.SPHERE_BY_POINT_AND_RADIUS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.SPHERE_BY_POINT_AND_RADIUS.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Сфера</strong><br>по центру и радиусу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_SPHERE;
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
            createWithNameValidation();
          }
        }
      }
    };
  }

  @Override
  protected void create() {
    i_BodyBuilder builder = AllBuildersManager.create(
            SphereBuilder.ALIAS, _name, id(0), valueAsDouble(RADIUS));
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
    if (id == null || !isBody || _numOfChosenAnchors == 1) {
      return;
    }
    i_Anchor a = getPointAnchor(id);
    if (a == null) {
      return;
    }
    chooseAnchor(a);
    showValue(RADIUS);
    canvas().getScaleAdapter().setBlocked(true); // avoid scaling on mouse wheel
    _ctrl.redraw();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите центр сферы {MOUSELEFT}", "Выберите радиус"
            + "{UP}{DOWN}{MOUSECENTER}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.SPHERE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
      Drawer.drawSphereCircles(ren, _anchor.get(0).getPoint(), valueAsDouble(RADIUS));
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
