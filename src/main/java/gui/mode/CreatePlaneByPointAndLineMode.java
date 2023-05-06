package gui.mode;

import bodies.BodyType;
import bodies.LineBody;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_P;
import builders.PlaneByPointAndLineBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.Line3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreatePlaneByPointAndLineMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _id1, _id2;
  private Line3d _line;
  private Vect3d _point;

  public CreatePlaneByPointAndLineMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
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
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      _id1 = a.id();
      _numOfChosenAnchors++;
      _point = a.getPoint();
      canvas().getHighlightAdapter().setTypes(BodyType.LINE);
      _ctrl.status().showMessage(_msg.current());
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.LINE) {
          return;
        }
        LineBody line = (LineBody)bd;
        _line = line.line();
        if (checker.checkPointOnLine(_line, _point)) {
          return;
        }
        _id2 = bd.id();
        create();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку{MOUSELEFT}", "Выберите прямую{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_BY_PNT_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_BY_PNT_LINE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.LINE, 1) && _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>проходящая через точку и прямую";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_BY_POINT_AND_LINE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "Точка плоскости", BuilderParamType.ANCHOR, _id1));
    params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, _id2));
    PlaneByPointAndLineBuilder builder = new PlaneByPointAndLineBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
