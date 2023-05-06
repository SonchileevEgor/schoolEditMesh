package gui.mode;

import bodies.BodyType;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_DISK;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_P;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_POLYGON;
import builders.PlaneByLineParallelCircleBuilder;
import builders.PlaneByLineParallelPlaneBuilder;
import builders.PlaneByLineParallelPolygonBuilder;
import builders.PlaneByPointParallelCircleBuilder;
import builders.PlaneByPointParallelPlaneBuilder;
import builders.PlaneByPointParallelPolygonBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Plane building scenario. User chooses point on plane, and plane (polygon, circle) body, which is
 * parallel to the new plane.
 *
 * @author Elena
 */
public class CreatePlaneByPointParallelPlaneOrPolygonMode extends CreateBodyMode implements i_FocusChangeListener {

  private final ArrayList<BodyType> _types = new ArrayList<>();
  private BodyType _type; // point or line
  private BodyType _type2; // plane, polygon, line, rib or ray
  private String _id1, _id2;
  private Plane3d _plane; // plane that is given
  private Vect3d _point; // point on the result plane
  private Line3d _line;

  public CreatePlaneByPointParallelPlaneOrPolygonMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types.clear();
    _types.add(BodyType.LINE);
    _types.add(BodyType.POINT);
    _types.add(BodyType.PLANE);
    _types.add(BodyType.CIRCLE);
    _types.add(BodyType.RIB);
    _types.add(BodyType.RAY);
    _types.addAll(BodyType.getPolygonTypes());
    canvas().getHighlightAdapter().setTypes(BodyType.POINT, BodyType.LINE);
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
        _type = bd.type();
        if (bd.type() == BodyType.LINE) {
          _id1 = id;
          _line = (Line3d)bd.getGeom();
          _point = _line.pnt();
        } else if (bd.type() == BodyType.POINT) {
          i_Anchor a = getPointAnchor(id);
          if (a == null) {
            return;
          }
          _id1 = a.id();
          _point = a.getPoint();
        } else {
          return;
        }
        _numOfChosenAnchors++;
        canvas().getHighlightAdapter().setTypes(_types);
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } else {

        if (!_types.contains(bd.type())) {
          return;
        }
        _type2 = bd.type();
        _id2 = id;

        if (_type2 == BodyType.PLANE) {
          _plane = ((PlaneBody)bd).plane();
        } else if (_type2.isPolygon()) {
          i_Anchor a = getPolygonAnchor(id);
          if (a == null) {
            return;
          }
          try {
            _plane = a.getPoly().plane();
            _id2 = a.id();
          } catch (ExDegeneration ex) {
            return;
          }
        } else if (_type2 == BodyType.RIB) {
          i_Anchor a = getRibAnchor(id);
          if (a == null) {
            return;
          }
          _id2 = a.id();
        } else if (_type2 == BodyType.RAY) {
          _id2 = id;
        } else if (_type2 == BodyType.CIRCLE) {
          i_Anchor a = getDiskAnchor(id);
          if (a == null) {
            return;
          }
          _plane = a.getDisk().plane();
          _id2 = a.id();
        }

        _numOfChosenAnchors++;
        create();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PlANE_BY_PNT_PARALL_CIRC.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PlANE_BY_PNT_PARALL_CIRC.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsBodies(BodyType.getPolygonTypes(), 1)
            || _ctrl.containsBodies(BodyType.CIRCLE, 1))
            && (_ctrl.containsBodies(BodyType.LINE, 1) || _ctrl.containsBodies(BodyType.POINT, 1));
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>проходящая через точку или прямую<br>параллельно данной плоскости (многоугольнику, окружности)";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_PLANE_BY_POINT_PARAL_PLANE_OR_POLY;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    i_BodyBuilder builder = null;
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    if (_type == BodyType.POINT) {
      if (_type2 == BodyType.PLANE) {
        params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Параллельная плоскость", BuilderParamType.BODY, _id2));
        builder = new PlaneByPointParallelPlaneBuilder(params);
      } else if (_type2.isPolygon()) {
        params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Параллельный многоугольник", BuilderParamType.ANCHOR, _id2));
        builder = new PlaneByPointParallelPolygonBuilder(params);
      } else if (_type2 == BodyType.CIRCLE) {
        params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_DISK, new BuilderParam(BLDKEY_DISK, "Параллельная окружность", BuilderParamType.ANCHOR, _id2));
        builder = new PlaneByPointParallelCircleBuilder(params);
      }
    } else if (_type == BodyType.LINE) {
      if (_type2 == BodyType.PLANE) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Параллельная плоскость", BuilderParamType.BODY, _id2));
        builder = new PlaneByLineParallelPlaneBuilder(params);
      } else if (_type2.isPolygon()) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Параллельный многоугольник", BuilderParamType.ANCHOR, _id2));
        builder = new PlaneByLineParallelPolygonBuilder(params);
      } else if (_type2 == BodyType.CIRCLE) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_DISK, new BuilderParam(BLDKEY_DISK, "Параллельная окружность", BuilderParamType.ANCHOR, _id2));
        builder = new PlaneByLineParallelCircleBuilder(params);
      }
    }
    if (builder != null) {
      _ctrl.add(builder, checker, false);
      reset();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку или прямую, через которую будет проходить плоскость{MOUSELEFT}",
            "Выберите плоскость (многоугольник){MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
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
