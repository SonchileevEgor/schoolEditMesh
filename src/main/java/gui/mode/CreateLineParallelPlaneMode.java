package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_P;
import static builders.BodyBuilder.BLDKEY_PLANE;
import builders.LineParalPlaneByAngleBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 * Строится прямая, проходящая через точку параллельно плоскости Пользователь выбирает плоскость,
 * точку, а затем вторую точку прямой, но прямая строится по углу между направляющим вектором прямой
 * и вектором в плоскости (соединяющим первую и вторую точки плоскости)
 *
 * @author Elena
 */
public class CreateLineParallelPlaneMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types = new ArrayList<>();
  private String _planeID;
  private BodyType _type;
  private Vect3d _point, _point1;
  private Plane3d _plane, _plane2;
  private boolean f = false; //!! TODO: что такое f?
  private double _angle;

  public CreateLineParallelPlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types.clear();
    _types.addAll(BodyType.getPolygonTypes());
    _types.add(BodyType.PLANE);
    _types.add(BodyType.CIRCLE);
    canvas().getHighlightAdapter().setTypes(_types);
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
        if (!_types.contains(bd.type())) {
          return;
        }
        _type = bd.type();
        _planeID = id;
        if (_type.isPolygon()) {
          Polygon3d poly = (Polygon3d)bd.getGeom();
          _plane = poly.plane();
        } else if (_type == BodyType.CIRCLE) {
          Circle3d circ = (Circle3d)bd.getGeom();
          _plane = circ.plane();
        } else {
          _plane = (Plane3d)bd.getGeom();
        }
        _ctrl.status().showMessage(_msg.current());
        _numOfChosenAnchors++;
        _ctrl.redraw();
        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
      } catch (ExNoBody | ExDegeneration ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      _anchor.add(a);
      _anchorID.add(a.id());
      _numOfChosenAnchors++;
      _ctrl.redraw();
      _point = a.getPoint();
      if (_plane.containsPoint(_point)) {
        _angle = 0;
        createWithNameValidation();
      }
    }
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (_numOfChosenAnchors != 2) {
          return;
        }
        _ctrl.status().showMessage(_msg.current());
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        if (!f) {
          return;
        }
        try {
          ArrayList<Vect3d> points = _plane2.intersect(canvas().getSightRay(e.getX(), e.getY()));
          if (!points.isEmpty()) {
            if (points.get(0).equals(_point)) {
              return;
            }
            _point1 = points.get(0);
            _angle = Vect3d.getAngle(Vect3d.sub(_point1, _point), Vect3d.sub(_plane.pnt(), _plane.getSecondPoint(1)));
            if (Vect3d.tripleProd(Vect3d.sub(_plane.pnt(),
                    _plane.getSecondPoint(1)),
                    Vect3d.sub(_point1, _point), _plane.n()) < 0) {
              _angle = 2 * Math.PI - _angle;
            }
            _numOfChosenAnchors++;
            f = false;
            createWithNameValidation();
          }
        } catch (ExDegeneration ex) {
        }
      }
    };
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.LINE_BY_PNT_PARALL_POLY.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.LINE_BY_PNT_PARALL_POLY.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsBodies(BodyType.getPolygonTypes(), 1)
            || _ctrl.containsBodies(BodyType.CIRCLE, 1)) && _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Прямая,</strong><br>проходящая через точку<br>параллельно данной плоскости (многоугольнику, окружности)";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_LINE_BY_POINT_PARAL_PLANE_OR_POLY;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "Точка", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Параллельная плоскость", BuilderParamType.BODY, _planeID));
    params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, "Угол", BuilderParamType.ANGLE_ROTATION, _angle));
    LineParalPlaneByAngleBuilder builder = new LineParalPlaneByAngleBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите плоскость (многоугольник, окружность)",
            "Выберите точку, через которую будет проходить прямая", "Выберите вторую точку прямой (на параллельной плоскости)");
  }

  @Override
  protected void setName() {
    _name = BodyType.LINE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      try {
        _plane2 = Plane3d.planeByPointParalPlane(_point, _plane);
      } catch (ExDegeneration ex) {
      }
      Drawer.setObjectColor(ren, ColorGL.GREY);
      Drawer.drawPlaneSmart(ren, _plane2, false, TypeFigure.SOLID);
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _point);
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
