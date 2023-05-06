package gui.mode;

import bodies.BodyType;
import builders.BisectrixOfAngle2DBuilder;
import builders.BodyBuilder;
import editor.ExNoBody;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 * Bisectrix of angle creating mode. User chooses angle object on scene.
 */
public class Create2dAngleBisectrixMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _id1;

  public Create2dAngleBisectrixMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.ANGLE);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите угол{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.LINE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ANGLE_BIS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ANGLE_BIS.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.ANGLE, 1);
  }

  public String shortDescription() {
    return "<html><strong>Биссектриса угла";
  }

  public String longDescription() {
    return "<html><strong>Биссектриса угла";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_ANGLE_BISECTRIX;
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
    _id1 = id;
    try {
      if (_ctrl.getBody(_id1).type() == BodyType.ANGLE) {
        createWithNameValidation();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void create() {
    BisectrixOfAngle2DBuilder builder = new BisectrixOfAngle2DBuilder();
    builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
    builder.setValue(BodyBuilder.BLDKEY_ANGLE, _id1);
    _ctrl.add(builder, checker, false);

    //~~ SIC: углы не добавляются, чтобы не перегружать чертёж.
    reset();
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected String description() {
    return "<html><strong>Биссектриса угла";
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
