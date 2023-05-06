package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.Rotation2dBuilder;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.SpaceTransformation;
import geom.Vect3d;
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
import opengl.colorgl.ColorGL;

/**
 *
 * @author Elena
 */
public class Create2dRotationMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private BodyType _type; // type of object
  private String _bodyID;
  private i_Geom _g;

  public Create2dRotationMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.addAll(BodyType.getAllTypes());
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
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
      i_Anchor a = getPointAnchor(id);
      chooseAnchor(a);
      canvas().getHighlightAdapter().setTypes(_types);
      canvas().getScaleAdapter().setBlocked(true);
    } else if (_numOfChosenAnchors == 1) {
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
    _msg.setMessage("Выберите точку - центр вращения{MOUSELEFT}",
            "Выберите тело для отображения{MOUSELEFT}",
            "Выберите угол поворота{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
      Vect3d p2 = new geom.Vect3d(_anchor.get(0).getPoint().x(), _anchor.get(0).getPoint().y(), 1);
      i_Geom result;
      try {
        result = SpaceTransformation.rotationOfObject(_g, _anchor.get(0).getPoint(), p2, valueAsDouble(ROT_ANGLE));
        BodyDrawer.draw(ren, result);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_ROTATION;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          changeValue(ROT_ANGLE, e.getWheelRotation());
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
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
          chooseAngle(e);
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
  protected void create() {
    Rotation2dBuilder builder = new Rotation2dBuilder(_type.getName(_ctrl.getEditor()));
    builder.setValue(BodyBuilder.BLDKEY_BODY, _bodyID);
    builder.setValue(BodyBuilder.BLDKEY_CENTER, id(0));
    builder.setValue(BodyBuilder.BLDKEY_ANGLE, valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Поворот</strong><br>вокруг точки";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  protected void setName() {
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_ROTATION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_ROTATION.getLargeIcon();
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
