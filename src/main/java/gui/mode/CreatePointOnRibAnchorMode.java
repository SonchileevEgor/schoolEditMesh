package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.PointOnRibAnchorBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.ExBadRef;
import editor.i_Anchor;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.CreateBodyMode._param;
import static gui.mode.ScreenMode.checker;
import static gui.mode.param.CreateModeParamType.COEFFICIENT;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Render;

/**
 * Сценарий постановки точки на отрезок. Зациклен.
 *
 * @author alexeev
 */
public class CreatePointOnRibAnchorMode extends CreateBodyMode {

  public CreatePointOnRibAnchorMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.RIB);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_R.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_R.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_RIB, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>,<br>закреплённая на отрезке";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_ON_RIB_ANCHOR;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        String id = canvas().getHighlightAdapter().id();
        if (id != null) {
          // находим точку пересечения с отрезком
          Vect3d v = canvas().getHighlightAdapter().point();
          if (v != null) {
            try {
              i_Anchor rib = _ctrl.getAnchor(id, "rib");
              _anchorID.add(rib.id());
              _param.setValue(COEFFICIENT, Vect3d.getMultiplierForHull2(rib.getRib().a(), rib.getRib().b(), v));
              createWithNameValidation();
            } catch (ExBadRef | ExDegeneration ex) {
            }
          }
        }
      }
    };
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку на отрезке{MOUSELEFT}");
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
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, BuilderParam.RIB_ALIAS, BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT)));
    _ctrl.add(new PointOnRibAnchorBuilder(params), checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
