package gui.mode;

import bodies.BodyType;
import bodies.LineBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.PointOnLineBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
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
 *
 * @author alexeev
 */
public class CreatePointOnLineMode extends CreateBodyMode {

  String _lineID;

  public CreatePointOnLineMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.LINE);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_LINE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.LINE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>,<br>закреплённая на прямой";
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
        _lineID = canvas().getHighlightAdapter().id();
        if (_lineID != null) {
          Vect3d v = canvas().getHighlightAdapter().point();
          // находим точку пересечения с прямой.
          if (v != null) {
            try {
              LineBody line = (LineBody)_ctrl.getBody(_lineID);
              _param.setValue(COEFFICIENT, Vect3d.getMultiplierForHull2(line.pnt(), line.pnt2(), v));
              createWithNameValidation();
            } catch (ExNoBody | ExDegeneration ex) {
            }
          }
        }
      }
    };
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Укажите точку на прямой{MOUSELEFT}");
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
    params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, _lineID));
    params.put(BLDKEY_ALPHA, new BuilderParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT)));
    _ctrl.add(new PointOnLineBuilder(params), checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
