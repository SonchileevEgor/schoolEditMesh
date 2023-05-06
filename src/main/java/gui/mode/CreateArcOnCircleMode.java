package gui.mode;

import bodies.BodyType;
import builders.ArcOnCircleBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author rita
 */
public class CreateArcOnCircleMode extends CreateBodyMode implements i_FocusChangeListener {

  public CreateArcOnCircleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.CIRCLE, BodyType.POINT);
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
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте окружность{MOUSELEFT}",
            "Отметьте начало дуги на окружности{MOUSELEFT}",
            "Отметьте конец дуги на окружности{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ARC.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    if (_numOfChosenAnchors == 2) {
      Drawer.drawPoint(ren, _anchor.get(1).getPoint());
    }
    if (_numOfChosenAnchors == 3) {
      Drawer.drawSegment(ren, _anchor.get(1).getPoint(), _anchor.get(2).getPoint());
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ARC_ON_CIRCLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ARC_ON_CIRC.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ARC_ON_CIRC.getLargeIcon();
  }

  @Override
  protected void create() {
    ArcOnCircleBuilder builder = new ArcOnCircleBuilder(_name);
    builder.addA(id(1));
    builder.addB(id(2));
    builder.addCircle(id(0));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Дуга</strong><br>по двум точкам на окружности";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    i_Anchor a = null;
    if (_numOfChosenAnchors < 3) {
      if (_numOfChosenAnchors == 0) {
        a = getDiskAnchor(id); // В списке якорей окружность имеет нулевой индекс,
      } else {
        a = getPointAnchor(id); // вершины - начиная с 1-ого.
      }
      if (a == null
              || (_numOfChosenAnchors == 2
              && !checker.checkPointsDifferent(_anchorID, a.id()))
              || (_numOfChosenAnchors > 0
              && !checker.checkPointOnCircle(_anchor.get(0), a))) {
        return;
      }
      chooseAnchor(a);
    }
    if (_numOfChosenAnchors == 3) {
      createWithNameValidation();
    }
  }

  @Override
  public boolean isEnabled() {
    return (!canvas().is3d() || _ctrl.containsAnchors(AnchorType.ANC_POINT, 2))
            && _ctrl.containsAnchors(AnchorType.ANC_DISK, 1);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
