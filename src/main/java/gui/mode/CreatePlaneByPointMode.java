package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PlaneBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import static config.Config.PRECISION;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Drawer;
import opengl.Render;
import opengl.StyleArrow;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;
import opengl.drawing.DrawingAction;
import opengl.drawing.DrawingQueue;

/**
 * Plane building script. User chooses point and normal vector.
 *
 * @author alexeev
 */
public class CreatePlaneByPointMode extends CreateBodyMode implements i_FocusChangeListener {

  // coords of point on screen
  private int _currX;
  private int _currY;
  private boolean _rotateFlag = false; // true, if left mouse button is pressed
  private geom.SpherCoord _sphNormal; // spherical coords of normal
  private Vect3d _normal; // normal vector of the plane

  public CreatePlaneByPointMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());
    _rotateFlag = false;
    DrawingQueue.add(new DrawingAction(0) {
      @Override
      public void draw(Render ren) {
        if (_numOfChosenAnchors == 1) {
          Drawer.setObjectColor(ren, ColorGL.GREY);
          Drawer.drawPlane(ren, _anchor.get(0).getPoint(), _normal);
        }
      }
    });
    DrawingQueue.add(new DrawingAction(1) {
      @Override
      public void draw(Render ren) {
        if (_numOfChosenAnchors == 1) {
          Drawer.setObjectColor(ren, ColorGL.RED);
          Drawer.drawPoint(ren, _anchor.get(0).getPoint());
          Drawer.drawArrow(ren, _anchor.get(0).getPoint(),
                  Vect3d.sum(_anchor.get(0).getPoint(),
                          Vect3d.mul(_normal, 0.25 / _normal.norm())),
                  TypeArrow.ONE_END, StyleArrow.CONE);
        }
      }
    });
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_BY_PNT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_BY_PNT.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>содержащая данную точку";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_BY_PNT;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e) || _numOfChosenAnchors != 1) {
          return;
        }
        _rotateFlag = true;
        _currX = e.getX();
        _currY = e.getY();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (_numOfChosenAnchors == 1) {
          _rotateFlag = false;
        }
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e) || _numOfChosenAnchors != 1 || !_rotateFlag) {
          return;
        }
        int newX = e.getX();
        int newY = e.getY();
        int dx = newX - _currX;
        int dy = newY - _currY;
        _currX = newX;
        _currY = newY;
        _sphNormal.setPhi(_sphNormal.getPhi() + 0.0025 * dx);
        _sphNormal.setTheta(_sphNormal.getTheta() + 0.0025 * dy);
        _normal = _sphNormal.toCartesian();
        _ctrl.status().showValue(String.format("Вектор нормали: %s", _normal.toString(PRECISION.value(), true)));
        _ctrl.redraw();
      }
    };
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
    _sphNormal = new geom.SpherCoord(
            canvas().getScene().getCameraPosition().azimuth(),
            canvas().getScene().getCameraPosition().zenith(),
            canvas().getScene().getCameraPosition().distance());
    _normal = canvas().getScene().getCameraPosition().eye();
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    _ctrl.status().showValue(String.format("Вектор нормали: %s",
            _normal.toString(PRECISION.value(), true)));
    canvas().getHighlightAdapter().setBlocked(true);
    _ctrl.redraw();
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 1) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            createWithNameValidation();
          }
        }
      }
    };
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> planeParams = new HashMap<String, BuilderParam>();
    planeParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    planeParams.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "Точка плоскости", BuilderParamType.ANCHOR, id(0)));
    planeParams.put("normal", new BuilderParam("normal", "Вектор нормали", BuilderParamType.VECT, _normal));
    PlaneBuilder builder = new PlaneBuilder(planeParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку на плоскости{MOUSELEFT}",
            "Поверните плоскость {MOUSELEFT}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
