package gui.mode;

import bodies.BodyType;
import bodies.CircleBody;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_DISK;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PointOnCircleBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import config.Config;
import static config.Config.PRECISION;
import editor.ExNoBody;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
import opengl.textdrawinggl.TextDrawer;

/**
 *
 * @author alexeev
 */
public class CreatePointOnCircleMode extends CreateBodyMode {

  private String _circleID;
  private boolean _pointPicked;
  private Circle3d _currCircle;
  private Vect3d _currPoint;
  private Vect3d _currPointOnCircle;

  public CreatePointOnCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE);
    setCursor(Cursor.getDefaultCursor());
    _pointPicked = false;
    _currPoint = null;
    _currPointOnCircle = null;
    _currCircle = null;
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_CIRC.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_CIRC.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.CIRCLE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>,<br>закреплённая на окружности";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_ON_CIRCLE;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        _pointPicked = false;
        _circleID = canvas().getHighlightAdapter().id();
        if (_circleID != null) {
          _currPoint = canvas().getHighlightAdapter().point();
          if (_currPoint != null) {
            try {
              CircleBody circle = (CircleBody)_ctrl.getBody(_circleID);
              _currCircle = circle.circle();
              _currPointOnCircle = Vect3d.sum(Vect3d.rNormalizedVector(
                      Vect3d.sub(_currPoint, _currCircle.center()), _currCircle.radiusLength()),
                      _currCircle.center());
              _pointPicked = true;
            } catch (ExNoBody ex) {
            }
          }
        }
        _ctrl.redraw();
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && _pointPicked) {
          createWithNameValidation();
        }
      }
    };
  }

  @Override
  public void create() {
    try {
      double phi = _currCircle.getPolarAngleByPoint(_currPoint);
      HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
      params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
      params.put(BLDKEY_DISK, new BuilderParam(BLDKEY_DISK, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR,
              _ctrl.getBody(_circleID).getAnchorID(CircleBody.BODY_KEY_DISK)));
      params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.DOUBLE, phi));
      _ctrl.add(new PointOnCircleBuilder(params), checker, false);
      reset();
    } catch (ExNoBody | ExDegeneration ex) {
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку на окружности{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_pointPicked) {
      Drawer.setObjectColor(ren, ColorGL.BLACK);
      Drawer.drawSegmentStipple(ren, _currPoint, _currPointOnCircle);
      Drawer.drawPoint(ren, _currPoint);
      Drawer.drawPoint(ren, _currPointOnCircle);
      TextDrawer.drawText(ren, _name + _currPointOnCircle.toString(PRECISION.value(), canvas().is3d()),
              _currPointOnCircle, Config.TEXT_DRAWING_TYPE_ON_POINT);
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
