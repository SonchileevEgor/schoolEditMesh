package gui.mode;

import bodies.BodyType;
import bodies.CircleBody;
import builders.BodyBuilder;
import builders.TangentRib2dBuilder;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.LENGTH;
import static gui.mode.param.CreateModeParamType.RATIO;
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
 *
 * @author Elena
 */
public class Create2dTangentRibToCircleMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _pointID, _circleID;
  private CircleBody _circle;
  private Vect3d _point;

  public Create2dTangentRibToCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _param.setUsed(RATIO, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RATIO);
    _param.setUsed(LENGTH, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(LENGTH);

    canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    if (_numOfChosenAnchors == 0) {
      try {
        i_Body a = _ctrl.getBody(id);
        if (a.type() != BodyType.CIRCLE) {
          return;
        }
        _circle = (CircleBody)a;
        _circleID = a.id();
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        canvas().getScaleAdapter().setBlocked(true);
        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      _pointID = a.id();
      _point = a.getPoint();
      _numOfChosenAnchors++;
      _ctrl.status().showMessage(_msg.current());
      showValue(RATIO);
      canvas().getScaleAdapter().setBlocked(true);
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите окружность{MOUSELEFT}",
            "Выберите точку на окружности{MOUSELEFT}",
            "Выберите отношение, заданное точкой{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите длину отрезка{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.RIB.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      try {
        Rib3d result = Rib3d.tangentRibByPointLengthAndRatio2d(
                _point, valueAsDouble(RATIO), valueAsDouble(LENGTH), _circle.circle());
        Drawer.setObjectColor(ren, ColorGL.RED);
        Drawer.drawPoint(ren, result.a());
        Drawer.drawPoint(ren, result.b());
        Drawer.drawSegment(ren, result.a(), result.b());
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TANGENT_RIB_2D;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TANGENT_RIB.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TANGENT_RIB.getLargeIcon();
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          if (!isChosen(RATIO)) {
            changeValue(RATIO, e.getWheelRotation());
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(RATIO);
          } else if (!isChosen(LENGTH)) {
            changeValue(LENGTH, e.getWheelRotation());
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(LENGTH);
          }
        }
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 2) {
          if (!isChosen(RATIO)) {
            changeValue(RATIO, e);
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(RATIO);
            if (isChosen(RATIO)) {
              showValue(LENGTH);
            }
          } else {
            changeValue(LENGTH, e);
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(LENGTH);
          }
          if (isChosen(LENGTH)) {
            createWithNameValidation();
          }
        }
      }
    };
  }

  @Override
  protected void create() {
    TangentRib2dBuilder builder = new TangentRib2dBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_DISK, _circleID);
    builder.setValue(BodyBuilder.BLDKEY_POINT, _pointID);
    builder.setValue(BodyBuilder.BLDKEY_COEFFICIENT, valueAsDouble(RATIO));
    builder.setValue(TangentRib2dBuilder.BLDKEY_RIB_LENGTH, valueAsDouble(LENGTH));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Отрезок, касательный к окружности</strong><br> в данной точке (принадлежащей окружности)";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.CIRCLE, 1) && _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
