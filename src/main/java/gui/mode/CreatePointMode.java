package gui.mode;

import bodies.BodyType;
import builders.AllBuildersManager;
import builders.PointBuilder;
import config.Config;
import static config.Config.PRECISION;
import editor.i_BodyBuilder;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.Timer;
import opengl.Drawer;
import opengl.Render;
import opengl.StyleArrow;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;
import opengl.textdrawinggl.TextDrawer;
import util.Util;

/**
 * Create point scenario. 1st step: select point on Oxy plane. 2nd step: change Z coordinate.
 *
 * @author alexeev
 */
public class CreatePointMode extends CreateBodyMode {

  /**
   * Arrow length with respect to camera distance.
   */
  private static final double ARROW_LENGTH_MULTIPLIER = 0.05;

  /**
   * Z coordinate change step with respect to mesh size.
   */
  private static final double Z_STEP = 0.1;

  /**
   * Direction of up arrow.
   */
  private final Vect3d _upArrowVectDirection = new Vect3d(0, 0, 1);

  private Vect3d _point; // position of point
  private Vect3d _projection; // projection of point

  private boolean _isArrowHovered; // flag indicates when some arrow hovered
  private boolean _isHoveredUpArrow; // flag indicates when up arrow hovered

  private Timer _t; // timer controls speed of point moving
  private int _timeAfterStart;
  private boolean _direction; // direction of point moving (up or down)

  public CreatePointMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    setCursor(Cursor.getDefaultCursor());
    _projection = null;
    _point = null;
    _direction = false;
    _isArrowHovered = false;
    _isHoveredUpArrow = false;
    _timeAfterStart = 0;
    _t = new Timer(75, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _timeAfterStart++;
        if (_timeAfterStart < 9) {
          changeZ(1);
        } else {
          changeZ(4);
        }
      }
    });
    _t.setInitialDelay(0);
    _ctrl.redraw();
  }

  /**
   * Change Z coordinate of new point.
   */
  private void changeZ(int times) {
    if (_direction) {
      _point.set_z(_point.z() + canvas().getMeshSize() * Z_STEP * times);
    } else {
      _point.set_z(_point.z() - canvas().getMeshSize() * Z_STEP * times);
    }
    _ctrl.status().showValue(_point.toString(PRECISION.value(), true));
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _t.stop();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.POINT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.POINT.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // only LMB is pressed
        if (e.getModifiersEx() != BUTTON1_DOWN_MASK) {
          return;
        }

        if (_numOfChosenAnchors == 0) {
          try {
            // вычисляем координаты точки
            Vect3d p = getVectOnInitialPlane(e.getX(), e.getY());
            // точка должна попасть в зону видимости
            if (canvas().isInViewingCube(p)) {
              _point = p;
              _projection = new Vect3d(_point.x(), _point.y(), 0);
              _numOfChosenAnchors++;
              _ctrl.status().showMessage(_msg.current());
              _ctrl.status().showValue(_point.toString(PRECISION.value(), true));
              _ctrl.redraw();
            }
          } catch (ExDegeneration ex) {
            _ctrl.status().showMessage("Не могу поставить точку на плоскость OXY");
          }
        } else if (_isArrowHovered) {
          canvas().getScaleAdapter().setBlocked(true);
          _direction = _isHoveredUpArrow;
          _t.start();
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        _t.stop();
        _timeAfterStart = 0;
        canvas().getScaleAdapter().setBlocked(false);
        _ctrl.redraw();
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        try {
          if (_numOfChosenAnchors == 0) {
            _projection = getVectOnInitialPlane(e.getX(), e.getY());
            _ctrl.status().showValue(_projection.toString(PRECISION.value(), true));
          } else {
            Vect3d p = canvas().getSightRay(e.getX(), e.getY()).intersectionWithPlane(
                    canvas().getScene().getPlaneByPoint(_projection));
            // if user hovers on arrow
            if (Vect3d.dist(p, _projection) < canvas().getCameraDistance() * ARROW_LENGTH_MULTIPLIER) {
              boolean isHoveredUpArrow = (p.z() > _projection.z());
              if (!_isArrowHovered || _isHoveredUpArrow != isHoveredUpArrow) {
                _isArrowHovered = true;
                _isHoveredUpArrow = isHoveredUpArrow;
                _ctrl.redraw();
              }
            } else {
              if (_isArrowHovered) {
                _isArrowHovered = false;
                _ctrl.redraw();
              }
            }
          }
        } catch (ExDegeneration ex) {

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
          if (press.up(e)) {
            double step = canvas().getMeshSize() / 5;
            if (_point.z() + step <= canvas().getScene().getViewVolume().getSizeVisible()) {
              _point.set_z(_point.z() + step);
              _ctrl.status().showValue(_point.toString(PRECISION.value(), true));
              _ctrl.redraw();
            }
          } else if (press.down(e)) {
            double step = canvas().getMeshSize() / 5;
            if (_point.z() - step >= -canvas().getScene().getViewVolume().getSizeVisible()) {
              _point.set_z(_point.z() - step);
              _ctrl.status().showValue(_point.toString(PRECISION.value(), true));
              _ctrl.redraw();
            }
          } else if (press.enter(e)) {
            createWithNameValidation();
          }
        }
      }
    };
  }

  private Vect3d getVectOnInitialPlane(double mouseX, double mouseY) throws ExDegeneration {
    Ray3d sight = canvas().getSightRay(mouseX, mouseY);
    Vect3d result = sight.intersectionWithPlane(Plane3d.oXY());
    /**
     * Если включен режим притягивания к сетке, округляем координаты точки.
     */
    if (canvas().isMagnet()) {
      result = new Vect3d(Util.round(result.x(), canvas().getMeshSize()),
              Util.round(result.y(), canvas().getMeshSize()), 0);
    }
    return result;
  }

  @Override
  protected void create() {
    i_BodyBuilder bb = AllBuildersManager.create(PointBuilder.ALIAS, _name, _point);
    _ctrl.add(bb, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage(
            "Выберите проекцию точки на плоскость Oxy{MOUSELEFT}",
            "Выберите координату Z{UP}{DOWN}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      // draw projection
      Drawer.setObjectColor(ren, ColorGL.LIGHT_GREY);
      Drawer.drawSegmentStipple(ren, _point, _projection);
      Drawer.drawPoint(ren, _projection);

      // draw point
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _point);
      TextDrawer.drawText(ren, _name + _point.toString(PRECISION.value(), true), _point,
              Config.TEXT_DRAWING_TYPE_ON_POINT);

      // draw arrows
      double arrowLength = canvas().getCameraDistance() * ARROW_LENGTH_MULTIPLIER;
      Vect3d upArrow = Vect3d.rNormalizedVector(_upArrowVectDirection, arrowLength);

      Drawer.setObjectColor(ren, (_isArrowHovered && _isHoveredUpArrow) ? ColorGL.GREY : ColorGL.LIGHT_GREY);
      Drawer.drawArrow(ren, _projection, Vect3d.sum(_projection, upArrow),
              TypeArrow.ONE_END, StyleArrow.CONE);

      Drawer.setObjectColor(ren, (_isArrowHovered && !_isHoveredUpArrow) ? ColorGL.GREY : ColorGL.LIGHT_GREY);
      Drawer.drawArrow(ren, _projection, Vect3d.sub(_projection, upArrow),
              TypeArrow.ONE_END, StyleArrow.CONE);
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
