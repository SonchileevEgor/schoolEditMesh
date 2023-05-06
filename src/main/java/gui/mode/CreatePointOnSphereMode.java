package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PHI;
import static builders.BodyBuilder.BLDKEY_SPHERE;
import static builders.BodyBuilder.BLDKEY_THETA;
import builders.PointOnSphereBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import config.Config;
import static config.Config.PRECISION;
import editor.ExNoBody;
import editor.i_Body;
  import editor.i_FocusChangeListener;
import geom.Vect3d;
  import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
import opengl.textdrawinggl.TextDrawer;

/**
 *
 * @author Vladimir
 */
public class CreatePointOnSphereMode extends CreateBodyMode implements i_FocusChangeListener{
  private Vect3d _currPoint;
  private Vect3d _currPointOnSphere;
  private String _sphereID;
  private boolean _pointPicked;

  public CreatePointOnSphereMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.SPHERE);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
   }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку на сфере{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_pointPicked) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawSegmentStipple(ren, _currPoint, _currPointOnSphere);
      Drawer.drawPoint(ren, _currPoint);
      Drawer.drawPoint(ren, _currPointOnSphere);
      TextDrawer.drawText(ren, _name + _currPointOnSphere.toString(PRECISION.value(), canvas().is3d()),
              _currPointOnSphere, Config.TEXT_DRAWING_TYPE_ON_POINT);
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void create() {
    double phi;
    if (_currPointOnSphere.y() < 0)
      phi = 2*Math.PI - Math.acos(_currPointOnSphere.projectOnOXY().x() / _currPointOnSphere.projectOnOXY().norm());
    else phi = Math.acos(_currPointOnSphere.projectOnOXY().x() / _currPointOnSphere.projectOnOXY().norm());
    double theta = Math.acos(_currPointOnSphere.z() / _currPointOnSphere.norm());
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    params.put(BLDKEY_SPHERE, new BuilderParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY, _sphereID));
    params.put(BLDKEY_PHI, new BuilderParam(BLDKEY_PHI, "Азимут", BuilderParamType.DOUBLE, phi));
    params.put(BLDKEY_THETA, new BuilderParam(BLDKEY_THETA, "Угол наклона", BuilderParamType.DOUBLE, theta));
    _ctrl.add(new PointOnSphereBuilder(params), checker, false);
    reset();
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        _pointPicked = false;
        _sphereID = canvas().getHighlightAdapter().id();
        if (_sphereID != null) {
          _currPoint = canvas().getHighlightAdapter().point();
          if (_currPoint != null) {
            _currPointOnSphere = _currPoint;
            _pointPicked = true;
          }
        }
        _ctrl.redraw();
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && _pointPicked)
          createWithNameValidation();
      }
    };
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_ON_SPHERE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_SPHERE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_SPHERE.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Точка</strong>,<br>закреплённая на сфере";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.SPHERE, 1);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) { }

  @Override
  public void focusLost(String id, boolean isBody) { }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) return;
    if (_numOfChosenAnchors == 0) {
      try {
        i_Body sph = _ctrl.getBody(id);
        if (sph.type() != BodyType.SPHERE) return;
        _sphereID = sph.id();
        _numOfChosenAnchors++;

        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {}
    }
  }
}
