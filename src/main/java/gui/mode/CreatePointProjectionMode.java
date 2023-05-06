package gui.mode;

import bodies.BodyType;
import bodies.RibBody;
import builders.AllBuildersManager;
import builders.AngleBetweenPlaneLineBuilder;
import builders.AngleBetweenRibLineBuilder;
import builders.AngleBetweenRibsBuilder;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PLANE;
import static builders.BodyBuilder.BLDKEY_POINT;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.PointOnLineProjectionBuilder;
import builders.PointOnPlaneProjectionBuilder;
import builders.ProjOnRibAnchorBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.ExBadRef;
import editor.i_Anchor;
import editor.i_Body;
  import editor.i_BodyBuilder;
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
 * Сценарий построения проекции точки на ребро, прямую, плоскость или грань.
 *
 * @author alexeev
 */
public class CreatePointProjectionMode extends CreateBodyMode implements i_FocusChangeListener {
  ArrayList<BodyType> _types;
  String _projID;         // ID грани / тела, на которую проецируем точку
  String _projBodyID;     // ID тела (не якоря)
  BodyType _type;

  public CreatePointProjectionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.add(BodyType.RIB);
    _types.add(BodyType.LINE);
    if( canvas().is3d() ){
      _types.addAll(BodyType.getPlainBodies());
    }
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) { }

  @Override
  public void focusLost(String id, boolean isBody) { }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) return;
    if (_numOfChosenAnchors == 0){
      i_Anchor a = getPointAnchor(id);
      if (a == null) return;
      chooseAnchor(a);
      canvas().getHighlightAdapter().setTypes(_types);
      _ctrl.redraw();
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (!_types.contains(bd.type())) return;
        _type = bd.type();
        _projID = _ctrl.getAnchorOrBodyID(id);
        _projBodyID = id;
        createWithNameValidation();
      } catch (ExBadRef ex) {}
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_PROJ_ON_FACE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_PROJ_ON_FACE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 1) &&
          (_ctrl.containsAnchors(AnchorType.ANC_RIB, 1) ||
           _ctrl.containsAnchors(AnchorType.ANC_POLY, 1) ||
           _ctrl.containsBodies(BodyType.LINE, 1) ||
           _ctrl.containsBodies(BodyType.PLANE, 1));
  }

  @Override
  public String description() {
    return "<html><strong>Перпендикуляр</strong><br>из точки";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_PROJECT_POINT;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    params.put(BLDKEY_POINT, new BuilderParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR, id(0)));
    i_BodyBuilder bb = null;
    if (_type == BodyType.RIB) {
      params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, BuilderParam.RIB_ALIAS, BuilderParamType.ANCHOR, _projID));
      bb = new ProjOnRibAnchorBuilder(params);
      _ctrl.add(bb, checker, false);
      
      // Добавляем прямые углы между отрезками.
      try {
        final AngleBetweenRibsBuilder angleBuilder1 = new AngleBetweenRibsBuilder(
                BodyType.ANGLE.getName(_ctrl.getEditor()));
        angleBuilder1.addRib1(_projID);
        angleBuilder1.addRib2(_ctrl.getAnchorID(bb.id(), RibBody.BODY_KEY_RIB));
        angleBuilder1.addDirection1(true);
        angleBuilder1.addDirection2(false);

        final AngleBetweenRibsBuilder angleBuilder2 = new AngleBetweenRibsBuilder(
                BodyType.ANGLE.getName(_ctrl.getEditor()));
        angleBuilder2.addRib1(_projID);
        angleBuilder2.addRib2(_ctrl.getAnchorID(bb.id(), RibBody.BODY_KEY_RIB));
        angleBuilder2.addDirection1(false);
        angleBuilder2.addDirection2(false);
        _ctrl.addBodies(new ArrayList<i_BodyBuilder>(){{
          add(angleBuilder1);
          add(angleBuilder2);
        }}, checker, false);
      } catch( ExBadRef ex ){}
    } else if (_type == BodyType.LINE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, _projID));
      bb = new PointOnLineProjectionBuilder(params);
      _ctrl.add(bb, checker, false);
      
      // Добавляем прямые углы между отрезком и прямой.
      try {
        final AngleBetweenRibLineBuilder angleBuilder1 = new AngleBetweenRibLineBuilder(
                BodyType.ANGLE.getName(_ctrl.getEditor()));
        angleBuilder1.addRib(_ctrl.getAnchorID(bb.id(), RibBody.BODY_KEY_RIB));
        angleBuilder1.addLine(_projID);
        angleBuilder1.addDirection1(true);
        angleBuilder1.addDirection2(false);

        final AngleBetweenRibLineBuilder angleBuilder2 = new AngleBetweenRibLineBuilder(
                BodyType.ANGLE.getName(_ctrl.getEditor()));
        angleBuilder2.addRib(_ctrl.getAnchorID(bb.id(), RibBody.BODY_KEY_RIB));
        angleBuilder2.addLine(_projID);
        angleBuilder2.addDirection1(false);
        angleBuilder2.addDirection2(false);
        _ctrl.addBodies(new ArrayList<i_BodyBuilder>(){{
          add(angleBuilder1);
          add(angleBuilder2);
        }}, checker, false);
      } catch( ExBadRef ex ){}
    } else if (_type.isPlain()) {
      params.put(BLDKEY_PLANE, new BuilderParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, _projBodyID));
      bb = new PointOnPlaneProjectionBuilder(params);
      _ctrl.add(bb, checker, false);
      
      // Добавляем прямые углы между отрезком и прямой.
      i_BodyBuilder angleBuilder = AllBuildersManager.create(
              AngleBetweenPlaneLineBuilder.ALIAS,
              BodyType.ANGLE.getName(_ctrl.getEditor()),
              _projBodyID, bb.id(), false, true);

      _ctrl.add(angleBuilder, checker, false);
    }
    if (bb != null) {
      reset();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку{MOUSELEFT}",
            "Выберите тело, на которое нужно опустить перпендикуляр{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) { }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}