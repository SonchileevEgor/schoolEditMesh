package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_POLYGON;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.LineXPolyBuilder;
import builders.PolyXPlaneBuilder;
import builders.PolyXPolyBuilder;
import builders.RibXPolyBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
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
public class CreatePolySectionMode extends CreateBodyMode implements i_FocusChangeListener {

  private BodyType _type;// type of object
  private i_Body _body;
  private ArrayList<BodyType> _types;
  private String _bodyID, _polyID;

  public CreatePolySectionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.addAll(BodyType.getPolygonTypes());
    _types.add(BodyType.PLANE);
    _types.add(BodyType.LINE);
    _types.add(BodyType.RIB);

    canvas().getHighlightAdapter().setTypes(BodyType.getPolygonTypes());
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
        if (checker.checkBodyTypeIsPolygon(bd.type())) {
          _polyID = bd.getAnchorID("facet");
          _numOfChosenAnchors++;
          canvas().getHighlightAdapter().setTypes(_types);
          _ctrl.status().showMessage(_msg.current());
          _ctrl.redraw();
        }
      } catch (ExNoBody ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (!_types.contains(bd.type())) {
          return;
        }
        _type = bd.type();
        _body = bd;
        _bodyID = id;
        _numOfChosenAnchors++;
        create();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите секущий многоугольник{MOUSELEFT}",
            "Выберите плоскость, прямую или ребро для пересечения{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POLY_SECTION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.FACE_SECTION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.FACE_SECTION.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();

    i_BodyBuilder builder = null;
    switch (_type) {
      case PLANE:
        params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, BodyType.RIB.getName(_ctrl.getEditor())));
        params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, "Плоскость", BuilderParamType.BODY, _bodyID));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Многоугольник", BuilderParamType.ANCHOR, _polyID));
        builder = new PolyXPlaneBuilder(params);
        break;
      case LINE:
        params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor())));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Многоугольник", BuilderParamType.ANCHOR, _polyID));
        params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _bodyID));
        builder = new LineXPolyBuilder(params);
        break;
      case RIB:
        params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor())));
        params.put(BLDKEY_POLYGON, new BuilderParam(BLDKEY_POLYGON, "Многоугольник", BuilderParamType.ANCHOR, _polyID));
        params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, _body.getAnchorID("rib")));
        builder = new RibXPolyBuilder(params);
        break;
      default:
        if (_type.isPolygon()) {
          params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, BodyType.RIB.getName(_ctrl.getEditor())));
          params.put(PolyXPolyBuilder.BLDKEY_POLYGON1,
                  new BuilderParam(PolyXPolyBuilder.BLDKEY_POLYGON1, "Первый многоугольник", BuilderParamType.ANCHOR, _polyID));
          params.put(PolyXPolyBuilder.BLDKEY_POLYGON2,
                  new BuilderParam(PolyXPolyBuilder.BLDKEY_POLYGON2, "Второй многоугольник", BuilderParamType.ANCHOR, _body.getAnchorID("facet")));
          builder = new PolyXPolyBuilder(params);
        }
    }
    if (builder != null) {
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Пересечение</strong><br>объекта с многоугольником";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.getPolygonTypes(), 1);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
