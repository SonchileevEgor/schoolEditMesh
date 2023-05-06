package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_ARC;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PointOnArcByRatioBuilder;
import static builders.PointOnArcByRatioBuilder.BLDKEY_RATIO;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import config.Config;
import static config.Config.PRECISION;
import editor.ExNoBody;
import geom.Arc3d;
import geom.ExDegeneration;
import geom.Ray3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
import opengl.textdrawinggl.TextDrawer;

/**
 *
 * @author rita
 */
public class CreatePointOnArcMode extends CreateBodyMode {

  private String _arcID;
  private boolean _pointPicked;
  private Arc3d _currArc;
  private Vect3d _currPoint;
  private Vect3d _currPointOnArc;

  public CreatePointOnArcMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.ARC);
    setCursor(Cursor.getDefaultCursor());
    _pointPicked = false;
    _currPoint = null;
    _currPointOnArc = null;
    _currArc = null;
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_ARC.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_ARC.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.ARC, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>,<br>закреплённая на дуге";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_ON_ARC;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        _pointPicked = false;
        _arcID = canvas().getHighlightAdapter().id();
        if (_arcID == null) {
          _pointPicked = false;
          return;
        }
        _currPoint = canvas().getHighlightAdapter().point();
        if (_currPoint == null) {
          _pointPicked = false;
          return;
        }
        try {
          _currArc = (Arc3d)_ctrl.getBody(_arcID).getGeom();
          Ray3d rayInArcPlane = Ray3d.ray3dByTwoPoints(_currArc.center(), _currPoint);
          ArrayList<Vect3d> ArcXRayInArcPlane = _currArc.intersect(rayInArcPlane);
          if (ArcXRayInArcPlane.size() == 1) {
            _currPointOnArc = ArcXRayInArcPlane.get(0);
            _pointPicked = true;
          }
        } catch (ExNoBody | ExDegeneration ex) {
          _pointPicked = false;
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

  public void create() {
    try {
      double phi = _currArc.getRatioByPoint(_currPoint);
      HashMap<String, BuilderParam> params = new HashMap<>();
      params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
      params.put(BLDKEY_ARC, new BuilderParam(BLDKEY_ARC, BuilderParam.ARC_ALIAS, BuilderParamType.BODY, _arcID));
      params.put(BLDKEY_RATIO, new BuilderParam(BLDKEY_RATIO, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, phi));
      _ctrl.add(new PointOnArcByRatioBuilder(params), checker, false);
      reset();
    } catch (ExDegeneration ex) {
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку на дуге{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_pointPicked) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _currPointOnArc);
      TextDrawer.drawText(ren, _name + _currPointOnArc.toString(PRECISION.value(), canvas().is3d()),
              _currPointOnArc, Config.TEXT_DRAWING_TYPE_ON_POINT);
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
