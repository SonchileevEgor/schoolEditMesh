package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.LineByPointOnCircle2dBuilder;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.Circle3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.Cursor;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class Create2dLineByPointOnCircleMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _circleID;
  Vect3d _point;
  Circle3d _circ;

  public Create2dLineByPointOnCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());

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
      chooseAnchor(a);
      canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE);
      _point = a.getPoint();
    } else {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.CIRCLE) {
          return;
        }
        _circleID = id;
        _circ = (Circle3d)bd.getGeom();
        createWithNameValidation();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_LINE_BY_POINT_ON_CIRCLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TANGENT_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TANGENT_LINE.getLargeIcon();
  }

  @Override
  protected void create() {
    LineByPointOnCircle2dBuilder builder = new LineByPointOnCircle2dBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_POINT, id(0));
    builder.setValue(BodyBuilder.BLDKEY_CIRCLE, _circleID);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Прямая, касающаяся окружности</strong><br>в точке, принадлежащей окружности";
  }

  @Override
  protected boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.CIRCLE, 1)
            && _ctrl.containsBodies(BodyType.POINT, 1));
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку{MOUSELEFT}", "Выберите окружность{MOUSELEFT}");
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
