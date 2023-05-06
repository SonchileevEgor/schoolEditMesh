package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.TangentFromPointToCircleBuilder;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Anchor;
import editor.i_Body;
import java.util.Collection;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.Checker;
import geom.Circle3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author alexeev
 */
public class CreateTangentLinesToCircleMode extends CreateBodyMode implements i_FocusChangeListener {

  private static final String SUFFIX_1 = "_1";
  private static final String SUFFIX_2 = "_2";
  private String _circleID;
  Vect3d _point;
  Circle3d _circ;

  public CreateTangentLinesToCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.status().showMessage("Выберите точку");
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
        try {
          validateName(SUFFIX_1);
          validateName(SUFFIX_2);
          create();
        } catch (InvalidBodyNameException ex) {
          _ctrl.status().showMessage(ex.getMessage());
        }
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TANGENT_TO_CIRCLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TANGENT1.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TANGENT1.getLargeIcon();
  }

  @Override
  protected void create() {
    TangentFromPointToCircleBuilder builder = new TangentFromPointToCircleBuilder(_name + SUFFIX_1);
    builder.setValue(BodyBuilder.BLDKEY_POINT, id(0));
    builder.setValue(BodyBuilder.BLDKEY_CIRCLE, _circleID);
    builder.setValue(BodyBuilder.BLDKEY_DIRECTION, true);
    _ctrl.add(builder, checker, false);

    if (Checker.isPlaneContainPoint(_circ.plane(), _point)
            && !Checker.isPointOnCircle(_point, _circ)
            && !Checker.isPointInCircle(_point, _circ)) {
      TangentFromPointToCircleBuilder builder2 = new TangentFromPointToCircleBuilder(_name + SUFFIX_2);
      builder2.setValue(BodyBuilder.BLDKEY_POINT, id(0));
      builder2.setValue(BodyBuilder.BLDKEY_CIRCLE, _circleID);
      builder2.setValue(BodyBuilder.BLDKEY_DIRECTION, false);
      _ctrl.add(builder2, checker, false);
    }

    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Касательные</strong><br>из точки к окружности";
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
