package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.Inversion2dBuilder;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 * Inversion scenario.
 *
 * @author Elena
 */
public class Create2dInversionMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private String _id1, _id2;

  public Create2dInversionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.addAll(BodyType.getAllTypes());
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
    _msg.setMessage("Выберите тело для отображения{MOUSELEFT}",
            "Выберите окружность инверсии{MOUSELEFT}");
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
      if (_numOfChosenAnchors == 0 && _types.contains(bd.type())) {
        _ctrl.status().showMessage(_msg.current());
        _name = bd.type().getName(_ctrl.getEditor());
        _id1 = id;
        _numOfChosenAnchors++;
        canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE);
        _ctrl.redraw();
      } else if (_numOfChosenAnchors == 1 && (bd.type() == BodyType.CIRCLE)) {
        i_Anchor a = getDiskAnchor(id);
        if (a == null) {
          return;
        }
        _id2 = a.id();
        createWithNameValidation();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void setName() {
    //!! TODO: имя по умолчанию?
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_INVERSION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.INVERSION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.INVERSION.getLargeIcon();
  }

  @Override
  protected void create() {
    Inversion2dBuilder builder = new Inversion2dBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_BODY, _id1);
    builder.setValue(BodyBuilder.BLDKEY_CIRCLE, _id2);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Инверсия</strong><br>относительно окружности";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.CIRCLE, 1);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
