package gui.mode;

import bodies.BodyType;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_CIRCLE;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_P;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_POLYGON;
import static builders.BodyBuilder.BLDKEY_RAY;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.PlaneByLineOrthLineBuilder;
import builders.PlaneByLineOrthPolyBuilder;
import builders.PlaneByLineOrthRayBuilder;
import builders.PlaneByLineOrthRibBuilder;
import builders.PlaneByLineOrthogonalDiskBuilder;
import builders.PlaneByLineOrthogonalPlaneBuilder;
import builders.PlaneByPointOrthDiskBuilder;
import builders.PlaneByPointOrthLineBuilder;
import builders.PlaneByPointOrthPlaneBuilder;
import builders.PlaneByPointOrthPolyBuilder;
import builders.PlaneByPointOrthRayBuilder;
import builders.PlaneByPointOrthRibBuilder;
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
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
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
public class CreatePlaneOrthPlaneOrLineMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private BodyType _type; // point or line
  private BodyType _type2; // plane, polygon, line, rib or ray
  private boolean _isAngleNeeded = false; //if need angle (when result plane must be perpendicular to plane through a point or orthogonal line); if don't need angle, use default value of angle = 0
  private String _id1, _id2;
  private Vect3d _point; // point on the result plane
  private Plane3d _plane; // plane that is given
  private Line3d _line;

  public CreatePlaneOrthPlaneOrLineMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.add(BodyType.LINE);
    _types.add(BodyType.POINT);
    _types.add(BodyType.PLANE);
    _types.add(BodyType.CIRCLE);
    _types.add(BodyType.RIB);
    _types.add(BodyType.RAY);
    _types.addAll(BodyType.getPolygonTypes());
    canvas().getHighlightAdapter().setTypes(BodyType.POINT, BodyType.LINE);
    _isAngleNeeded = false;
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

        // check if need angle
        if (_type2 == BodyType.PLANE || _type2.isPolygon() || _type2 == BodyType.CIRCLE) {
          if (_type == BodyType.LINE) {
            // if line is orth to given plane then choose angle
            if (Checker.isCollinear(_plane.n(), _line.l())) {
              _isAngleNeeded = true;
            }
          } else {
            // for point
            _isAngleNeeded = true;
          }
        }
        if (_isAngleNeeded) {
          _ctrl.status().showMessage(_msg.current());
          canvas().getHighlightAdapter().setBlocked(true);
          canvas().getScaleAdapter().setBlocked(true);
          _ctrl.redraw();
        } else {
          create();
        }
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку или прямую{MOUSELEFT}",
            "Выберите плоскость (многоугольник, окружность, прямую, отрезок, луч){MOUSELEFT}",
            "Поверните плоскость{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_BY_PNT_ORTH_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_BY_PNT_ORTH_LINE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsAnchors(AnchorType.ANC_POINT, 1) || _ctrl.containsBodies(BodyType.LINE, 1))
            && (_ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsAnchors(AnchorType.ANC_POLY, 1)
            || _ctrl.containsAnchors(AnchorType.ANC_DISK, 1)
            || _ctrl.containsAnchors(AnchorType.ANC_RIB, 1)
            || _ctrl.containsBodies(BodyType.LINE, 1)
            || _ctrl.containsBodies(BodyType.RAY, 1));
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>проходящая через прямую или точку по нормали";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_ORTH_PLANE_OR_LINE;
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
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          changeValue(ROT_ANGLE, e.getWheelRotation());
        }
        _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
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

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    if (_type == BodyType.LINE) {
      if (_type2 == BodyType.PLANE) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Ортогогальная плоскость", BuilderParamType.BODY, _id2));
        params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
                valueAsDouble(ROT_ANGLE)));
        PlaneByLineOrthogonalPlaneBuilder builder = new PlaneByLineOrthogonalPlaneBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2 == BodyType.CIRCLE) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_CIRCLE, new BuilderParam(BLDKEY_CIRCLE, "Ортогональная окружность", BuilderParamType.ANCHOR, _id2));
        params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
                valueAsDouble(ROT_ANGLE)));
        PlaneByLineOrthogonalDiskBuilder builder = new PlaneByLineOrthogonalDiskBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2.isPolygon()) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Ортогональный многоугольник", BuilderParamType.ANCHOR, _id2));
        params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
                valueAsDouble(ROT_ANGLE)));
        PlaneByLineOrthPolyBuilder builder = new PlaneByLineOrthPolyBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2 == BodyType.LINE) {
        params.put(PlaneByLineOrthLineBuilder.BLDKEY_LINE1,
                new BuilderParam(PlaneByLineOrthLineBuilder.BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, _id1));
        params.put(PlaneByLineOrthLineBuilder.BLDKEY_LINE2,
                new BuilderParam(PlaneByLineOrthLineBuilder.BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, _id2));
        PlaneByLineOrthLineBuilder builder = new PlaneByLineOrthLineBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2 == BodyType.RIB) {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, "Ортогональный отрезок", BuilderParamType.ANCHOR, _id2));
        PlaneByLineOrthRibBuilder builder = new PlaneByLineOrthRibBuilder(params);
        _ctrl.add(builder, checker, false);
      } else {
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, _id1));
        params.put(BLDKEY_RAY, new BuilderParam(BLDKEY_RAY, "Ортогональный луч", BuilderParamType.BODY, _id2));
        PlaneByLineOrthRayBuilder builder = new PlaneByLineOrthRayBuilder(params);
        _ctrl.add(builder, checker, false);
      }
    } else {
      // _type = POINT
      if (_type2 == BodyType.PLANE) {
        params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "A", BuilderParamType.ANCHOR, _id1));
        params.put(PlaneByPointOrthPlaneBuilder.BLDKEY_P1,
                new BuilderParam(PlaneByPointOrthPlaneBuilder.BLDKEY_P1, "Плоскость", BuilderParamType.BODY, _id2));
        params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
                valueAsDouble(ROT_ANGLE)));
        PlaneByPointOrthPlaneBuilder builder = new PlaneByPointOrthPlaneBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2 == BodyType.CIRCLE) {
        params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "A", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_CIRCLE, new BuilderParam(BLDKEY_CIRCLE, "Окружность", BuilderParamType.ANCHOR, _id2));
        params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
                valueAsDouble(ROT_ANGLE)));
        PlaneByPointOrthDiskBuilder builder = new PlaneByPointOrthDiskBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2.isPolygon()) {
        params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "A", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Грань", BuilderParamType.ANCHOR, _id2));
        params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, "Угол поворота", BuilderParamType.ANGLE_ROTATION,
                valueAsDouble(ROT_ANGLE)));
        PlaneByPointOrthPolyBuilder builder = new PlaneByPointOrthPolyBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2 == BodyType.LINE) {
        params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "P", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id2));
        PlaneByPointOrthLineBuilder builder = new PlaneByPointOrthLineBuilder(params);
        _ctrl.add(builder, checker, false);
      } else if (_type2 == BodyType.RIB) {
        params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "P", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, _id2));
        PlaneByPointOrthRibBuilder builder = new PlaneByPointOrthRibBuilder(params);
        _ctrl.add(builder, checker, false);
      } else {
        // type2 = RAY
        params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "P", BuilderParamType.ANCHOR, _id1));
        params.put(BLDKEY_RAY, new BuilderParam(BLDKEY_RAY, "Луч", BuilderParamType.BODY, _id2));
        PlaneByPointOrthRayBuilder builder = new PlaneByPointOrthRayBuilder(params);
        _ctrl.add(builder, checker, false);
      }
    }
    reset();
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1 && _type != BodyType.LINE) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _point);
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.GREY);
      if (_isAngleNeeded) {
        try {
          Plane3d plane = Plane3d.planeByPointOrthPlane(_point, _plane, valueAsDouble(ROT_ANGLE));
          Drawer.drawPlane(ren, _point, plane.n());
          Drawer.setObjectColor(ren, ColorGL.RED);
          Drawer.drawPoint(ren, _point);
          Drawer.drawArrow(ren, _point, Vect3d.sum(_point, Vect3d.mul(plane.n(), 0.25 / plane.n().norm())), TypeArrow.ONE_END, StyleArrow.CONE);
        } catch (ExDegeneration ex) {
        }
      }
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
