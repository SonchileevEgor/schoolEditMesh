package gui.mode;

import bodies.BodyType;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_BETA;
import static builders.BodyBuilder.BLDKEY_FACET;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import builders.PointOnPlaneBuilder;
import builders.PointOnPolyAnchorBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
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
import opengl.Render;

/**
 *
 * @author alexeev
 */
public class CreatePointOnPolyOrPlaneMode extends CreateBodyMode {

  Vect3d _point;
  String _id;
  BodyType _type;
  double[] _multipliers;

  public CreatePointOnPolyOrPlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.POLYGON, BodyType.PLANE);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_FACE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_FACE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsAnchors(AnchorType.ANC_POLY, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong><br>закреплённая на грани или плоскости";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_ON_POLY_ANCHOR;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        String id = canvas().getHighlightAdapter().id();
        if (id != null) {
          try {
            i_Body bd = _ctrl.getBody(id);
            _point = canvas().getHighlightAdapter().point();
            if (_point != null) {
              _type = bd.type();
              if (_type == BodyType.PLANE) {
                PlaneBody plane = (PlaneBody)bd;
                _multipliers = Vect3d.getMultipliersForHull3(
                        plane.plane().pnt(), plane.plane().pnt2(), plane.plane().pnt3(), _point);
                _id = id;
              } else if (_type.isPolygon()) {
                try {
                  i_Anchor poly = _ctrl.getAnchor(id, "facet");
                  _multipliers = Vect3d.getMultipliersForHull3(
                          poly.getPoly().points().get(0),
                          poly.getPoly().points().get(1),
                          poly.getPoly().points().get(2), _point);
                  _id = poly.id();
                } catch (ExNoAnchor ex) {
                }
              }
            }
            createWithNameValidation();
          } catch (ExNoBody | ExDegeneration ex) {
          }
        }
      }
    };
  }

  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, BuilderParam.COEF_1_ALIAS, BuilderParamType.DOUBLE, _multipliers[0]));
    params.put(BLDKEY_BETA, new BuilderParam(BLDKEY_BETA, BuilderParam.COEF_2_ALIAS, BuilderParamType.DOUBLE, _multipliers[1]));
    if (_type == BodyType.PLANE) {
      params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, _id));
      _ctrl.add(new PointOnPlaneBuilder(params), checker, false);
    } else if (_type.isPolygon()) {
      params.put(BLDKEY_FACET, new BuilderParam(BLDKEY_FACET, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR, _id));
      _ctrl.add(new PointOnPolyAnchorBuilder(params), checker, false);
    }
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку на многоугольнике{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
