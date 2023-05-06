package gui.mode;

import bodies.BodyType;
import bodies.CircleBody;
import static builders.BodyBuilder.BLDKEY_DISK;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_PHI;
import static builders.BodyBuilder.BLDKEY_RHO;
import builders.PointOnDiskBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
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
public class CreatePointOnDiskMode extends CreateBodyMode {

  private double _rho;
  private double _phi;
  private String _id;

  public CreatePointOnDiskMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PNT_DISK.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PNT_DISK.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.CIRCLE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>,<br>закреплённая на круге";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_ON_DISK;
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
          Vect3d v = canvas().getHighlightAdapter().point();
          if (v != null) {
            try {
              CircleBody disk = (CircleBody)_ctrl.getBody(id);
              _rho = Vect3d.dist(disk.center(), v) / disk.circle().radiusLength();
              _phi = disk.circle().getPolarAngleByPoint(v);
              _id = disk.getAnchorID("disk");
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
    _msg.setMessage("Укажите точку внутри круга{MOUSELEFT}");
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
    params.put(BLDKEY_DISK, new BuilderParam(BLDKEY_DISK, BuilderParam.DISK_ALIAS, BuilderParamType.ANCHOR, _id));
    params.put(BLDKEY_RHO, new BuilderParam(BLDKEY_RHO, BuilderParam.DIST_TO_CENTER_ALIAS, BuilderParamType.DOUBLE, _rho));
    params.put(BLDKEY_PHI, new BuilderParam(BLDKEY_PHI, BuilderParam.ANGLE_ALIAS, BuilderParamType.DOUBLE, _phi));
    _ctrl.add(new PointOnDiskBuilder(params), checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
