package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.PointBuilder;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import opengl.Render;

/**
 *
 * @author alexeev
 */
public class Create2dPointMode extends CreateBodyMode implements i_FocusChangeListener {

  Vect3d _p;

  public Create2dPointMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes();
    _ctrl.status().showMessage("Поставьте точку на плоскость{MOUSELEFT}");
    _name = BodyType.POINT.getName(_ctrl.getEditor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.POINT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.POINT.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Точка</strong>";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POINT_2D;
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
  }

  @Override
  public MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          try {
            _p = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
            create();
          } catch (ExDegeneration ex) {
          }
        }
      }
    };
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
  protected void create() {
    PointBuilder bb = new PointBuilder(_name);
    bb.setValue(BodyBuilder.BLDKEY_P, _p);
    _ctrl.add(bb, null, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
