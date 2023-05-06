package gui.mode;

import bodies.BodyType;
import bodies.LineBody;
import bodies.RayBody;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_POINT;
import static builders.BodyBuilder.BLDKEY_RAY;
import builders.PlaneParalLineByPointBuilder;
import builders.PlaneParalRayByPointBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import gui.mode.param.CreateModeParamType;
import static gui.mode.param.CreateModeParamType.ANGLE2;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.StyleArrow;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Elena
 */
public class CreatePlaneParalLineByPointMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _id1, _id2;
  private ArrayList<BodyType> _types;
  private BodyType _type;
  private Line3d _line;
  private Vect3d _point;

  public CreatePlaneParalLineByPointMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.add(BodyType.LINE);
    _types.add(BodyType.RAY);
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
    _param.setUsed(ANGLE2, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE2);
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
      if (a == null) {
        return;
      }
      _id1 = a.id();
      _numOfChosenAnchors++;
      _point = a.getPoint();

      canvas().getHighlightAdapter().setTypes(_types);
      _ctrl.status().showMessage(_msg.current());
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() == BodyType.RAY) {
          RayBody ray = (RayBody)bd;
          try {
            _line = ray.ray().line();
            _type = BodyType.RAY;
          } catch (ExDegeneration ex) {
          }
        } else {
          if (checker.checkBodyTypeIsLine(bd.type())) {
            LineBody line = (LineBody)bd;
            _line = line.line();
            _type = BodyType.LINE;
          }
        }
        if (checker.checkPointOnLine(_line, _point)) {
          return;
        }
        _id2 = bd.id();
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _param.showValue(CreateModeParamType.ANGLE2);
        canvas().getScaleAdapter().setBlocked(true);
        _ctrl.redraw();

      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку, через которую будет строиться плоскость{MOUSELEFT}",
            "Выберите прямую или луч{MOUSELEFT}",
            "Выберите угол наклона плоскости{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_BY_PNT_PARALL_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_BY_PNT_PARALL_LINE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.LINE, 1) || _ctrl.containsBodies(BodyType.RAY, 1)) && _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>проходящая через точку параллельно прямой, лучу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_PAR_LINE_BY_POINT;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          changeValue(ANGLE2, e.getWheelRotation());
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE2);
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
          if (!isChosen(ANGLE2)) {
            changeValue(ANGLE2, e);
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE2);
          }
          if (isChosen(ANGLE2)) {
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
    params.put(BLDKEY_POINT, new BuilderParam(BLDKEY_POINT, "Точка", BuilderParamType.ANCHOR, _id1));
    if (_type == BodyType.LINE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id2));
      params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
              Math.PI - valueAsDouble(ANGLE2)));
      PlaneParalLineByPointBuilder builder = new PlaneParalLineByPointBuilder(params);
      _ctrl.add(builder, checker, false);
    } else {
      params.put(BLDKEY_RAY, new BuilderParam(BLDKEY_RAY, "Луч", BuilderParamType.BODY, _id2));
      params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
              Math.PI - valueAsDouble(ANGLE2)));
      PlaneParalRayByPointBuilder builder = new PlaneParalRayByPointBuilder(params);
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected void nativeDraw0(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.GREY);
      Plane3d plane = null;
      try {
        plane = Plane3d.planeByPointParalLine(_point, _line, Math.PI - valueAsDouble(ANGLE2));
      } catch (ExDegeneration ex) {
      }
      Drawer.drawPlane(ren, plane.pnt(), plane.n());
    }
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      try {
        Vect3d proj = _line.projectOfPoint(_point);
        Drawer.setObjectColor(ren, ColorGL.RED);
        Drawer.drawPoint(ren, _point);
        Drawer.drawPoint(ren, proj);
        Drawer.drawArrow(ren, _point, proj, TypeArrow.ONE_END, StyleArrow.CONE);
        Plane3d plane = Plane3d.planeByPointParalLine(_point, _line, Math.PI - valueAsDouble(ANGLE2));
        Drawer.drawPoint(ren, plane.pnt());
        Drawer.drawArrow(ren, plane.pnt(), Vect3d.sum(plane.pnt(), Vect3d.mul(plane.n(), 0.25 / plane.n().norm())), TypeArrow.ONE_END, StyleArrow.CONE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
