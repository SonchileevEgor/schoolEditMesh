package gui.mode;

import bodies.BodyType;
import bodies.RibBody;
import builders.BodyBuilder;
import builders.LineXRibBuilder;
import builders.RibXPlaneBuilder;
import builders.RibXPolyBuilder;
import builders.RibXRibBuilder;
import editor.ExNoBody;
import editor.i_Body;
import java.util.Collection;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import java.util.Collection;
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
public class CreateRibSectionMode extends CreateBodyMode implements i_FocusChangeListener {

  private BodyType _type;// type of object
  private i_Body _body;
  private ArrayList<BodyType> _types;
  private String _bodyID, _ribID;

  public CreateRibSectionMode(EdtController ctrl) {
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

    canvas().getHighlightAdapter().setTypes(BodyType.RIB);
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
        _ribID = bd.getAnchorID("rib");
        if (checker.checkBodyTypeIsRib(bd.type())) {
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
    _msg.setMessage("Выберите секущий отрезок{MOUSELEFT}",
            "Выберите плоскость, прямую или ребро для пересечения{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RIB_SECTION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RIB_SECTION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RIB_SECTION.getLargeIcon();
  }

  @Override
  protected void create() {
    i_BodyBuilder builder = null;
    switch (_type) {
      case PLANE:
        builder = new RibXPlaneBuilder();
        builder.setValue(BodyBuilder.BLDKEY_PLANE, _bodyID);
        builder.setValue(BodyBuilder.BLDKEY_RIB, _ribID);
        break;
      case LINE:
        builder = new LineXRibBuilder();
        builder.setValue(BodyBuilder.BLDKEY_LINE, _bodyID);
        builder.setValue(BodyBuilder.BLDKEY_RIB, _ribID);
        break;
      case RIB:
        builder = new RibXRibBuilder();
        builder.setValue(RibXRibBuilder.BLDKEY_RIB1, _ribID);
        builder.setValue(RibXRibBuilder.BLDKEY_RIB2, _body.getAnchorID(RibBody.BODY_KEY_RIB));
        break;
      default:
        if (_type.isPolygon()) {
          builder = new RibXPolyBuilder();
          builder.setValue(BodyBuilder.BLDKEY_POLYGON, _body.getAnchorID("facet"));
          builder.setValue(BodyBuilder.BLDKEY_RIB, _ribID);
        }
    }
    if (builder != null) {
      builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Пересечение</strong><br>объекта с отрезком";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.RIB, 1);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
