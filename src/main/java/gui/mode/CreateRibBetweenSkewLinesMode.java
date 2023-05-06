package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.RibBetweenSkewLinesBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_FocusChangeListener;
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
public class CreateRibBetweenSkewLinesMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<String> _lineID = new ArrayList<>();

  public CreateRibBetweenSkewLinesMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.LINE);
    setCursor(Cursor.getDefaultCursor());
    _lineID.clear();
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.SKEW_LINES_DIST.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.SKEW_LINES_DIST.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.LINE, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Отрезок,</strong><br>реализующий расстояние между скрещивающимися прямыми";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RIB_BY_SKEW_LINES;
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    try {
      i_Body bd = _ctrl.getBody(id);
      if (bd.type() != BodyType.LINE) {
        return;
      }
      if (_numOfChosenAnchors == 0) {
        _lineID.add(id);
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
      } else {
        if (!checker.checkBodiesDifferent(id, _lineID.get(0))) {
          return;
        }
        _lineID.add(id);
        createWithNameValidation();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(RibBetweenSkewLinesBuilder.BLDKEY_LINE1,
            new BuilderParam(RibBetweenSkewLinesBuilder.BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, _lineID.get(0)));
    params.put(RibBetweenSkewLinesBuilder.BLDKEY_LINE2,
            new BuilderParam(RibBetweenSkewLinesBuilder.BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, _lineID.get(1)));
    RibBetweenSkewLinesBuilder builder = new RibBetweenSkewLinesBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую прямую{MOUSELEFT}",
            "Выберите вторую прямую{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.RIB.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
