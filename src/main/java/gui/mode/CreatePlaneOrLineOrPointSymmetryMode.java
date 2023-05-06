package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_BODY;
import static builders.BodyBuilder.BLDKEY_CENTER;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import builders.SymmetryLineBuilder;
import builders.SymmetryPlaneBuilder;
import builders.SymmetryPointBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import geom.Line3d;
import geom.Plane3d;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Lena
 */
public class CreatePlaneOrLineOrPointSymmetryMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private BodyType _type, _type2;// type of object and plane or line
  private String _id1, _id2;

  public CreatePlaneOrLineOrPointSymmetryMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.add(BodyType.PLANE);
    _types.add(BodyType.LINE);
    _types.add(BodyType.POINT);
    canvas().getHighlightAdapter().setTypes(_types);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите плоскость, прямую или точку-центр симметрии{MOUSELEFT}",
            "Выберите тело для отображения{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw0(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      if (_type2 == BodyType.PLANE) {
        try {
          Drawer.drawPlaneSmart(ren, (Plane3d)_ctrl.getBody(_id2).getGeom(), true, TypeFigure.WIRE);
        } catch (ExNoBody ex) {
        }
      }
    }
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      if (_type2 == BodyType.LINE) {
        try {
          Line3d line = (Line3d)_ctrl.getBody(_id2).getGeom();
          Drawer.drawLine(ren, line.pnt(), line.pnt2());
        } catch (ExNoBody ex) {
        }
      } else {
        try {
          Drawer.drawPoint(ren, _ctrl.getAnchor(_id2).getPoint());
        } catch (ExNoAnchor ex) {
        }
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_3D_SYM;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.SYMMETRY.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.SYMMETRY.getLargeIcon();
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
        if (bd.type() == BodyType.PLANE || bd.type() == BodyType.LINE) {
          _type2 = bd.type();
          _id2 = id;
        } else if (bd.type() == BodyType.POINT) {
          _type2 = bd.type();
          i_Anchor a = getPointAnchor(id);
          if (a == null) {
            return;
          }
          _id2 = a.id();
        } else {
          return;
        }
        _ctrl.status().showMessage(_msg.current());
        _numOfChosenAnchors++;
        canvas().getHighlightAdapter().setTypes(BodyType.getAllTypes());
      } else {
        if (bd.type() == BodyType.INTERSECT_BODY) {
          return;
        }
        _type = bd.type();
        _id1 = id;
        create();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    i_BodyBuilder builder;
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _type.getName(_ctrl.getEditor())));
    params.put(BLDKEY_BODY, new BuilderParam(BLDKEY_BODY, "Тело для симметрии", BuilderParamType.BODY, _id1));
    if (_type2 == BodyType.PLANE) {
      params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Плоскость симметрии", BuilderParamType.BODY, _id2));
      builder = new SymmetryPlaneBuilder(params);
    } else if (_type2 == BodyType.LINE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Ось симметрии", BuilderParamType.BODY, _id2));
      builder = new SymmetryLineBuilder(params);
    } else {
      params.put(BLDKEY_CENTER, new BuilderParam(BLDKEY_CENTER, "Центр симметрии", BuilderParamType.ANCHOR, _id2));
      builder = new SymmetryPointBuilder(params);
    }
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Симметрия</strong><br>относительно плоскости, прямой или точки-центра";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsBodies(BodyType.LINE, 1)
            || _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
