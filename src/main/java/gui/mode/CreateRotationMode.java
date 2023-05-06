package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.RotationBuilder;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import java.util.Collection;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.SpaceTransformation;
import geom.i_Geom;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.StyleArrow;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;

/**
 * Rotation scenario. User chooses body for rotation, and two endpoints of rotation axis.
 *
 * @author Elena
 */
public class CreateRotationMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private BodyType _type; // type of object
  private String _bodyID;
  private i_Geom _g;

  public CreateRotationMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.addAll(BodyType.getAllTypes());
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
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
    if (_numOfChosenAnchors <= 1) {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      if (_numOfChosenAnchors == 1 && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      _anchorID.add(a.id());
      _anchor.add(a);
      _numOfChosenAnchors++;
      canvas().getScaleAdapter().setBlocked(true);
      _ctrl.status().showMessage(_msg.current());
      if (_numOfChosenAnchors == 2) {
        canvas().getHighlightAdapter().setTypes(_types);
      }
    } else if (_numOfChosenAnchors == 2) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() == BodyType.INTERSECT_BODY) {
          return;
        }
        _type = bd.type();
        _bodyID = id;
        _g = bd.getGeom();
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
      _param.showValue(ROT_ANGLE);
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите начало вектора оси вращения{MOUSELEFT}",
            "Выберите конец вектора оси вращения{MOUSELEFT}",
            "Выберите тело для отображения{MOUSELEFT}",
            "Выберите угол поворота{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw0(Render ren) {
    if ((_numOfChosenAnchors == 3) && (_type == BodyType.PLANE)) {//плоскость строится отдельно, чтобы не перекрывала сцену
      Drawer.setObjectColor(ren, ColorGL.GREY);
      i_Geom result = null;
      try {
        result = SpaceTransformation.rotationOfObject(
                _g, _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
      } catch (ExDegeneration ex) {
      }
      Plane3d plane = (Plane3d)result;
      Drawer.drawPlane(ren, plane.pnt(), plane.n());
    }
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
    if (_numOfChosenAnchors >= 2) {
      Drawer.drawPoint(ren, _anchor.get(1).getPoint());
      Drawer.drawArrow(ren, _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), TypeArrow.ONE_END, StyleArrow.CONE);
    }
    if (_numOfChosenAnchors == 3) {
      i_Geom result = null;
      try {
        result = SpaceTransformation.rotationOfObject(
                _g, _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
      } catch (ExDegeneration ex) {
      }
      if (_type != BodyType.PLANE) {
        BodyDrawer.draw(ren, result);
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ROTATION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ROTATION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ROTATION.getLargeIcon();
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 3) {
          if (!isChosen(ROT_ANGLE)) {
            changeValue(ROT_ANGLE, e.getWheelRotation());
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
          } else {
            create();
          }
        }
      }
    };
  }

  protected void chooseAngle(KeyEvent e) {
    if (!isChosen(ROT_ANGLE)) {
      changeValue(ROT_ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
    if (isChosen(ROT_ANGLE)) {
      create();
    }
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 3) {
          chooseAngle(e);
        }
      }
    };
  }

  @Override
  protected void create() {
    RotationBuilder builder = new RotationBuilder(_type.getName(_ctrl.getEditor()));
    builder.setValue(BodyBuilder.BLDKEY_BODY, _bodyID);
    builder.setValue(RotationBuilder.BLDKEY_P1, id(0));
    builder.setValue(RotationBuilder.BLDKEY_P2, id(1));
    builder.setValue(BodyBuilder.BLDKEY_ANGLE, valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Поворот</strong><br>вокруг оси, заданной вектором";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.POINT, 2);
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
