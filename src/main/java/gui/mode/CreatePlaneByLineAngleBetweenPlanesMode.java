package gui.mode;

import bodies.BodyType;
import bodies.LineBody;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_POLYGON;
import builders.PlaneByLineAndAngleAtPlaneBuilder;
import builders.PlaneByLineAndAngleAtPolyBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.Checker;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import gui.mode.param.CreateModeParamType;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.StyleArrow;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;

/**
 * Plane by line and angle at given plane (line lies in given plane).
 *
 * @author Elena
 */
public class CreatePlaneByLineAngleBetweenPlanesMode extends CreateBodyMode implements i_FocusChangeListener {

  private BodyType _type; // plane or poly
  private String _id1, _id2;
  private Line3d _line;
  private Plane3d _plane;

  public CreatePlaneByLineAngleBetweenPlanesMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.LINE);
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
    try {
      i_Body bd = _ctrl.getBody(id);
      if (_numOfChosenAnchors == 0) {
        if (bd.type() != BodyType.LINE) {
          return;
        }
        _id1 = id;
        _numOfChosenAnchors++;
        _line = ((LineBody)bd).line();
        canvas().getHighlightAdapter().setTypes(BodyType.POLYGON, BodyType.PLANE);
        _ctrl.status().showMessage(_msg.current());
      } else if (_numOfChosenAnchors == 1) {
        _type = bd.type();
        if (_type.isPolygon()) {
          i_Anchor a = getPolygonAnchor(id);
          if (a == null) {
            return;
          }
          try {
            _plane = a.getPoly().plane();
            _id2 = a.id();
          } catch (ExDegeneration ex) {
          }
        } else if (_type == BodyType.PLANE) {
          _plane = ((PlaneBody)bd).plane();
          _id2 = id;
        } else {
          return;
        }
        if (!Checker.isLineLiesInPlane(_line, _plane)) {
          checker.showMessage("Прямая должна лежать в плоскости!", error.Error.WARNING);
          return;
        }
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _param.showValue(CreateModeParamType.ROT_ANGLE);
        canvas().getScaleAdapter().setBlocked(true);
        _ctrl.redraw();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите прямую, через которую будет строиться плоскость{MOUSELEFT}",
            "Выберите плоскость (многоугольник){MOUSELEFT}",
            "Выберите угол между плоскостями{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_ANG_PLANE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_ANG_PLANE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.LINE, 1) && (_ctrl.containsBodies(BodyType.PLANE, 1) || _ctrl.containsAnchors(AnchorType.ANC_POLY, 1));
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>проходящая через прямую<br>под углом к данной плоскости (многоугольнику)";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_BY_LINE_ANGLE_AT_PLANE_OR_POLY;
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
          if (!isChosen(ROT_ANGLE)) {
            changeValue(ROT_ANGLE, e);
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
          }
          if (isChosen(ROT_ANGLE)) {
            create();
          }
        }
      }
    };
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    if (_type == BodyType.PLANE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id1));
      params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Плоскость", BuilderParamType.BODY, _id2));
      params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION, valueAsDouble(ROT_ANGLE)));
      PlaneByLineAndAngleAtPlaneBuilder builder = new PlaneByLineAndAngleAtPlaneBuilder(params);
      _ctrl.add(builder, checker, false);
    } else {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id1));
      params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Многоугольник", BuilderParamType.ANCHOR, _id2));
      params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION, valueAsDouble(ROT_ANGLE)));
      PlaneByLineAndAngleAtPolyBuilder builder = new PlaneByLineAndAngleAtPolyBuilder(params);
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected void nativeDraw0(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.GREY);
      Plane3d plane = Plane3d.planeByLineAndAngleBetweenPlanes(_line, _plane, valueAsDouble(ROT_ANGLE));
      Drawer.drawPlane(ren, plane.pnt(), plane.n());

    }
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Plane3d plane = Plane3d.planeByLineAndAngleBetweenPlanes(_line, _plane, valueAsDouble(ROT_ANGLE));
      Drawer.drawPoint(ren, plane.pnt());
      Drawer.drawArrow(ren, plane.pnt(), Vect3d.sum(plane.pnt(), Vect3d.mul(plane.n(), 0.25 / plane.n().norm())), TypeArrow.ONE_END, StyleArrow.CONE);
    }
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
