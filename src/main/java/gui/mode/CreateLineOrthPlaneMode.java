package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_FACET;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_P;
import static builders.BodyBuilder.BLDKEY_PLANE;
import builders.LineOrthPolyOrCircleBuilder;
import builders.LineOrthogonalPlaneBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_BodyBuilder;
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
public class CreateLineOrthPlaneMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types = new ArrayList<>();
  private String _planeID;
  private BodyType _type;

  public CreateLineOrthPlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types.clear();
    _types.addAll(BodyType.getPolygonTypes());
    _types.add(BodyType.PLANE);
    _types.add(BodyType.CIRCLE);
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
      _anchor.add(a);
      chooseAnchor(a);
      canvas().getHighlightAdapter().setTypes(_types);
    } else {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (!_types.contains(bd.type())) {
          return;
        }
        _type = bd.type();
        _planeID = id;
        createWithNameValidation();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.LINE_ORT_PLANE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.LINE_ORT_PLANE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsBodies(BodyType.getPolygonTypes(), 1)
            || _ctrl.containsBodies(BodyType.CIRCLE, 1)) && _ctrl.containsBodies(BodyType.POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Прямая,</strong><br>проходящая через точку<br>перпендикулярно данной плоскости (многоугольнику, окружности)";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_LINE_BY_POINT_ORTH_PLANE_OR_POLY;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    i_BodyBuilder builder = null;
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_P, new BuilderParam(BLDKEY_P, "P", BuilderParamType.ANCHOR, id(0)));
    if (_type == BodyType.PLANE) {
      params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Ортогональная плоскость", BuilderParamType.BODY, _planeID));
      builder = new LineOrthogonalPlaneBuilder(params);
    } else {
      params.put(BLDKEY_FACET, new BuilderParam("facet", "Ортогональная фигура", BuilderParamType.BODY, _planeID));
      builder = new LineOrthPolyOrCircleBuilder(params);
    }

    //!! TODO: добавить прямые углы
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку, через которую будет проходить прямая{MOUSELEFT}",
            "Выберите плоскость (многоугольник, окружность){MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.LINE.getName(_ctrl.getEditor());
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
