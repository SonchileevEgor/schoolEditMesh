package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PlaneBySkewLinesBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Плоскость по скрещивающимся прямым.
 *
 * @author Elena
 */
public class CreatePlaneBySkewLinesMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _line1ID, _line2ID;

  public CreatePlaneBySkewLinesMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.LINE);
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
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.LINE) {
          return;
        }
        _line1ID = id;
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.LINE) {
          return;
        }
        _line2ID = id;
        create();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_BY_LINE_PARALL_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_BY_LINE_PARALL_LINE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.LINE, 2));
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость,</strong><br>проходящая через прямую параллельно другой прямой <br>(прямые скрещиваются)";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_BY_SKEW_LINES;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(PlaneBySkewLinesBuilder.BLDKEY_LINE1,
            new BuilderParam(PlaneBySkewLinesBuilder.BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, _line1ID));
    params.put(PlaneBySkewLinesBuilder.BLDKEY_LINE2,
            new BuilderParam(PlaneBySkewLinesBuilder.BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, _line2ID));
    PlaneBySkewLinesBuilder builder = new PlaneBySkewLinesBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите прямую, через которую будет проходить плоскость{MOUSELEFT}",
            "Выберите вторую прямую{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
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
