package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.CircleInAngleBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import static config.Config.GUI_EPS;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.Angle3d;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Ray3d;
import geom.Rib2d;
import geom.Vect2d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.CreateBodyMode._param;
import static gui.mode.ScreenMode.checker;
import static gui.mode.param.CreateModeParamType.COEFFICIENT;
import gui.mode.param.DistanceModeParam;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Elena
 */
public class CreateCircleInAngleMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _angleID;
  private Angle3d _angle;
  private Vect3d _point;
  private Line3d _bisectrix;
  private boolean f = false;//нужен, чтобы сначала рисовалась биссектриса, а потом выбиралась точка

  public CreateCircleInAngleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.ANGLE);
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
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.ANGLE) {
          return;
        }
        _angleID = id;
        _angle = (Angle3d)bd.getGeom();
        if (!checker.checkAngleLessThanPi(_angle)) {
          return;
        }
        if (!checker.checkAngleIsZero(_angle)) {
          return;
        }
        _bisectrix = _angle.bisectrix();
        _ctrl.status().showMessage(_msg.current());
        _numOfChosenAnchors++;
        _ctrl.redraw();
      } catch (ExNoBody | ExDegeneration ex) {
      }
    }
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (_numOfChosenAnchors != 1) {
          return;
        }
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        if (!f) {
          return;
        }

        ArrayList<Vect3d> points = new ArrayList();
        try {
          double[] coord1 = new double[2];
          double[] coord2 = new double[2];
          _ctrl.getMainCanvasCtrl().getScene().getDisplayCoord(_bisectrix.pnt(), coord1);
          _ctrl.getMainCanvasCtrl().getScene().getDisplayCoord(_bisectrix.pnt2(), coord2);
          Rib2d segmentOnScreen = new Rib2d(new Vect2d(coord1[0], coord1[1]),
                  new Vect2d(coord2[0], coord2[1]));
          if (segmentOnScreen.line().distFromPoint(
                  new Vect2d(e.getX(), _ctrl.getMainCanvasCtrl().getCanvas().getHeight() - e.getY())) <= GUI_EPS.value()) {
            try {
              Ray3d sight = _ctrl.getMainCanvasCtrl().getSightRay(e.getX(), e.getY());
              Plane3d pl = new Plane3d(_bisectrix.pnt(), _bisectrix.pnt2(),
                      Vect3d.sum(_bisectrix.pnt2(), sight.l()));
              Vect3d v = _bisectrix.intersectionWithLine(sight.line().projectOnPlane(pl));
              if (_ctrl.getMainCanvasCtrl().isInViewingCube(v)) {
                points.add(v);
              }
            } catch (ExDegeneration ex) {
            }
          }

          if (!points.isEmpty()) {
            Ray3d ray = Ray3d.ray3dByTwoPoints(_angle.vertex(),
                    _bisectrix.projectOfPoint(_angle.pointOnFirstSide()));
            if (!ray.containsPoint(points.get(0))
                    || Vect3d.dist(points.get(0), _angle.vertex()) <= DistanceModeParam.MIN_VALUE) {
              return;
            }
            _point = points.get(0);
            _param.setValue(COEFFICIENT, Vect3d.getMultiplierForHull2(_angle.vertex(),
                    _bisectrix.projectOfPoint(_angle.pointOnFirstSide()), _point));
            f = false;
            _numOfChosenAnchors++;
            createWithNameValidation();
          }
        } catch (ExDegeneration ex) {
        }
      }
    };
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_IN_ANGLE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_IN_ANGLE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.ANGLE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Вписать окружность в угол";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CIRCLE_IN_ANGLE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, "Угол", BuilderParamType.BODY, _angleID));
    params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT)));
    CircleInAngleBuilder builder = new CircleInAngleBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите угол{MOUSELEFT}",
            "Выберите центр окружности - точку на биссектрисе{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawLine(ren, _bisectrix.pnt(), _bisectrix.pnt2());
      f = true;
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
