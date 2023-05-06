package gui.mode;

import bodies.BodyType;
import bodies.LineBody;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.LineOnPlaneProjectionBuilder;
import builders.RibOnPlaneProjectionBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import geom.Checker;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreateLineOrRibProjectionOnPlaneMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<String> _bodyID = new ArrayList<>();
  private BodyType _type;
  private Vect3d _direction;
  private boolean _isProjectionPoint;

  public CreateLineOrRibProjectionOnPlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.RIB, BodyType.LINE);
    setCursor(Cursor.getDefaultCursor());
    _bodyID.clear();
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
        if (_type == BodyType.LINE) {
          _bodyID.add(id);
          _direction = ((LineBody)bd).l();
        } else if (_type == BodyType.RIB) {
          i_Anchor a = getRibAnchor(id);
          _bodyID.add(a.id());
          _direction = Vect3d.sub(a.getRib().b(), a.getRib().a());
        } else {
          return;
        }
        _numOfChosenAnchors++;
        canvas().getHighlightAdapter().setTypes(BodyType.PLANE);
        _ctrl.status().showMessage(_msg.current());
      } else {
        if (bd.type() != BodyType.PLANE) {
          return;
        }
        Plane3d pl = ((PlaneBody)bd).plane();
        _isProjectionPoint = Checker.isCollinear(pl.n(), _direction);
        _bodyID.add(id);
        create();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RIB_PROJ_ON_PLANE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RIB_PROJ_ON_PLANE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.LINE, 1)
            || _ctrl.containsBodies(BodyType.RIB, 1))
            && _ctrl.containsBodies(BodyType.PLANE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Проекция</strong><br> прямой или отрезка на плоскость";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_LINE_OR_RIB_PROJ_ON_PLANE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
    i_BodyBuilder builder = null;
    if (_isProjectionPoint) {
      _name = BodyType.POINT.getName(_ctrl.getEditor());
    } else {
      _name = _type.getName(_ctrl.getEditor());
    }
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Плоскость", BuilderParamType.BODY, _bodyID.get(1)));
    if (_type == BodyType.LINE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Проецируемая прямая", BuilderParamType.BODY, _bodyID.get(0)));
      builder = new LineOnPlaneProjectionBuilder(params);
    } else if (_type == BodyType.RIB) {
      params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, "Проецируемый отрезок", BuilderParamType.ANCHOR, _bodyID.get(0)));
      builder = new RibOnPlaneProjectionBuilder(params);
    }
    if (builder != null) {
      _ctrl.add(builder, checker, false);
      reset();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите прямую или отрезок для проекции{MOUSELEFT}",
            "Выберите плоскость{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
