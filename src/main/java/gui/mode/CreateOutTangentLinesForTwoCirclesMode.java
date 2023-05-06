package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_C1;
import static builders.BodyBuilder.BLDKEY_C2;
import static builders.BodyBuilder.BLDKEY_DIRECTION;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.OutTangentForTwoCirclesBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Outer tangent lines for two circles building scenario.
 *
 * @author alexeev
 */
public class CreateOutTangentLinesForTwoCirclesMode extends CreateBodyMode implements i_FocusChangeListener {

  private static final String SUFFIX_1 = "_1";
  private static final String SUFFIX_2 = "_2";
  private boolean _circle1_chosen;
  private String _id1;
  private String _id2;

  public CreateOutTangentLinesForTwoCirclesMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE);
    _circle1_chosen = false;
    _id1 = null;
    _id2 = null;
    setCursor(Cursor.getDefaultCursor());
    _ctrl.status().showMessage("Выберите первую окружность{MOUSELEFT}");
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TANGENT_OUTER_TWO_CIRCLES;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TANGENT2.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TANGENT2.getLargeIcon();
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
      if (_ctrl.getBody(id).type() != BodyType.CIRCLE) {
        return;
      }
      if (_circle1_chosen) {
        if (!checker.checkBodiesDifferent(_id1, id)) {
          return;
        }
        _id2 = id;
        try {
          validateName(SUFFIX_1);
          validateName(SUFFIX_2);
          create();
        } catch (InvalidBodyNameException ex) {
          _ctrl.status().showMessage(ex.getMessage());
        }
      } else {
        _id1 = id;
        _circle1_chosen = true;
        _ctrl.status().showMessage("Выберите вторую окружность{MOUSELEFT}");
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name + SUFFIX_1));
    params.put(BLDKEY_C1, new BuilderParam(BLDKEY_C1, "Первая окружность", BuilderParamType.BODY, _id1));
    params.put(BLDKEY_C2, new BuilderParam(BLDKEY_C2, "Вторая окружность", BuilderParamType.BODY, _id2));
    params.put(BLDKEY_DIRECTION, new BuilderParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN, true));
    _ctrl.add(new OutTangentForTwoCirclesBuilder(params), checker, false);

    params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name + SUFFIX_2));
    params.put(BLDKEY_C1, new BuilderParam(BLDKEY_C1, "Первая окружность", BuilderParamType.BODY, _id1));
    params.put(BLDKEY_C2, new BuilderParam(BLDKEY_C2, "Вторая окружность", BuilderParamType.BODY, _id2));
    params.put(BLDKEY_DIRECTION, new BuilderParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN, false));
    _ctrl.add(new OutTangentForTwoCirclesBuilder(params), checker, false);

    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Внешние касательные</strong><br>к двум окружностям";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.CIRCLE, 2);
  }

  @Override
  protected void setCreationMessengers() {
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
