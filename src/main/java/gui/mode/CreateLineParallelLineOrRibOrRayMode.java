package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_P;
import static builders.BodyBuilder.BLDKEY_RAY;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.LineParallelLineBuilder;
import builders.LineParallelRayBuilder;
import builders.LineParallelRibBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreateLineParallelLineOrRibOrRayMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private BodyType _type; //line, rib or ray
  private String _id1, _id2;

  public CreateLineParallelLineOrRibOrRayMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<BodyType>();
    _types.add(BodyType.LINE);
    _types.add(BodyType.RAY);
    _types.add(BodyType.RIB);
    canvas().getHighlightAdapter().setTypes(_types);
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
        _type = bd.type();
        if (_type == BodyType.RIB) {
          _id1 = getRibAnchor(id).id();
        } else if ((_type == BodyType.LINE) || (_type == BodyType.RAY)) {
          _id1 = id;
        } else {
          return;
        }
      } catch (ExNoBody ex) {
      }
      _numOfChosenAnchors++;
      canvas().getHighlightAdapter().setTypes(BodyType.POINT);
      _ctrl.status().showMessage(_msg.current());
    } else if (_numOfChosenAnchors == 1) {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      _id2 = a.id();
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите прямую (отрезок, луч){MOUSELEFT}",
            "Выберите точку, через которую будет проходить прямая{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.LINE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {

  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_LINE_PARALLEL_LINE_OR_RIB_OR_RAY;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.LINE_PARAL_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.LINE_PARAL_LINE.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "Точка", BuilderParamType.ANCHOR, _id2));
    if (_type == BodyType.LINE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Параллельная прямая", BuilderParamType.BODY, _id1));
      LineParallelLineBuilder builder = new LineParallelLineBuilder(params);
      _ctrl.add(builder, checker, false);
    } else if (_type == BodyType.RIB) {
      params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, "Параллельный отрезок", BuilderParamType.ANCHOR, _id1));
      LineParallelRibBuilder builder = new LineParallelRibBuilder(params);
      _ctrl.add(builder, checker, false);
    } else {
      params.put(BLDKEY_RAY, new BuilderParam(BLDKEY_RAY, "Параллельный луч", BuilderParamType.BODY, _id1));
      LineParallelRayBuilder builder = new LineParallelRayBuilder(params);
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.LINE, 1) || _ctrl.containsAnchors(AnchorType.ANC_RIB, 1)
            || _ctrl.containsBodies(BodyType.RAY, 1)) && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1);
  }

  @Override
  protected String description() {
    return "<html><strong>Прямая,</strong><br>проходящая через точку<br>параллельно другой прямой (отрезку, лучу)";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
