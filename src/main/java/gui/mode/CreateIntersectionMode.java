package gui.mode;

import bodies.BodyType;
import builders.IntersectionBuilder;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreateIntersectionMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types = BodyType.getBodiesForIntersection();
  private String _id1, _id2;

  public CreateIntersectionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _id1 = _id2 = null;
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(_types);
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую кривую для пересечения{MOUSELEFT}",
            "Выберите вторую кривую{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.INTERSECT_BODY.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_INTERSECTION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CURVE_INTERSECT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CURVE_INTERSECT.getLargeIcon();
  }

  @Override
  protected void create() {
    IntersectionBuilder builder = new IntersectionBuilder(_name);
    builder.addBody1(_id1);
    builder.addBody2(_id2);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Пересечение</strong><br>двух кривых";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(_types, 1);
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
      if (!_types.contains(bd.type())) {
        return;
      }
      if (_numOfChosenAnchors == 0) {
        _id1 = id;
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } else if (_numOfChosenAnchors == 1) {
        _id2 = id;
        if (!checker.checkBodiesDifferent(_id1, _id2)) {
          return;
        }
        createWithNameValidation();
      }
    } catch (ExNoBody ex) {
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
