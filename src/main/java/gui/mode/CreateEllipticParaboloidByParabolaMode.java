package gui.mode;

import bodies.BodyType;
import static bodies.BodyType.PARABOLA;
import builders.EllipticParaboloidByParabolaBuilder;
import editor.ExNoBody;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.Cursor;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Ivan
 */
public class CreateEllipticParaboloidByParabolaMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _bodyID;
  private BodyType _type;

  public CreateEllipticParaboloidByParabolaMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.PARABOLA);
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
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите параболу{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ELLIPTIC_PARABOLOID.getName(_ctrl.getEditor());
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ELLIPTIC_PARABOLOID_BY_PARABOLA;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PARABOLOID_BY_PARABOLA.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PARABOLOID_BY_PARABOLA.getLargeIcon();
  }

  @Override
  protected void create() {
    if (_type == BodyType.PARABOLA) {
      EllipticParaboloidByParabolaBuilder builder = new EllipticParaboloidByParabolaBuilder();
      builder.setValue(EllipticParaboloidByParabolaBuilder.BLDKEY_NAME, _name);
      builder.setValue(EllipticParaboloidByParabolaBuilder.BLDKEY_PARABOLA, _bodyID);
      _ctrl.add(builder, checker, false);
      exit();
    } else {
      reset();
    }
  }

  @Override
  protected String description() {
    return "<html><strong>Эллиптический параболоид</strong><br>по параболе";
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

  @Override
  protected boolean isEnabled() {
    return (!canvas().is3d() || _ctrl.containsBodies(BodyType.PARABOLA, 1));
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
    _bodyID = id;
    try {
      _type = _ctrl.getBody(_bodyID).type();
      if (_type.equals(PARABOLA)) {
        createWithNameValidation();
      }
    } catch (ExNoBody ex) {
    }
  }

}
