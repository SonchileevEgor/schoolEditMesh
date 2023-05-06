package gui.mode;

import builders.CuboctahedronBuilder;
import builders.TetrahedronBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.Cursor;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Tetrahedron building scenario. User chooses four different points on scene.
 *
 * @author alexeev
 */
public class CreateCuboctahedronMode extends CreateBodyMode implements i_FocusChangeListener {

  public CreateCuboctahedronMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CUBOCTAHEDRON.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CUBOCTAHEDRON.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 4);
  }

  @Override
  public String description() {
    return "<html><strong>Кубооктаэдр</strong><br>по четырём точкам";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CUBOCTAHEDRON;
  }

  @Override
  protected void create() {
    CuboctahedronBuilder builder = new CuboctahedronBuilder(
            _anchor.get(0).getTitle() + _anchor.get(1).getTitle()
            + _anchor.get(2).getTitle() + _anchor.get(3).getTitle());
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addC(id(2));
    builder.addD(id(3));
    _ctrl.add(builder, checker, false);
    reset();
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
      return; // handle only bodies
    }
    i_Anchor a = getPointAnchor(id);
    if (a == null) {
      return;
    }
    if (!checker.checkPointsDifferent(_anchorID, a.id())) {
      return;
    }
    chooseAnchor(a);
    if (_numOfChosenAnchors == 4) {
      if (!checker.checkPointsNotCoplanar(
              new Vect3d[]{ _anchor.get(0).getPoint(), _anchor.get(1).getPoint(),
                _anchor.get(2).getPoint(), _anchor.get(3).getPoint() })) {
        _numOfChosenAnchors--;
        _anchor.remove(3);
        _anchorID.remove(3);
      } else {
        create();
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}",
            "Выберите третью точку{MOUSELEFT}",
            "Выберите четвёртую точку{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 0) {
      return;
    }
    Drawer.setObjectColor(ren, ColorGL.RED);
    if (_numOfChosenAnchors >= 2) {
      Drawer.drawSegment(ren, _anchor.get(0).getPoint(), _anchor.get(1).getPoint());
    }
    if (_numOfChosenAnchors >= 3) {
      Drawer.drawSegment(ren, _anchor.get(1).getPoint(), _anchor.get(2).getPoint());
      Drawer.drawSegment(ren, _anchor.get(2).getPoint(), _anchor.get(0).getPoint());
    }
    for (int i = 0; i < _numOfChosenAnchors; i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
