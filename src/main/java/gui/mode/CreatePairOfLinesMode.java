package gui.mode;

import bodies.BodyType;
import builders.PairOfLinesBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.PairOfLines;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Ivan
 */
public class CreatePairOfLinesMode extends CreateBodyMode implements i_FocusChangeListener {

  protected Vect3d currPoint; // вспомогательная точка, используемая при предварительной отрисовке.

  public CreatePairOfLinesMode(EdtController ctrl) {
    super(ctrl);
  }

  protected void doIfFourPointsChosen() {
    createWithNameValidation();
  }

  protected void doIfFourPointsChosen(MouseWheelEvent e) {
  }

  protected void doIfFourPointsChosen(KeyEvent e) {
  }

  protected void doWhenThreePointsChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(2).getPoint();
      }
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую точку{MOUSELEFT}",
            "Отметьте вторую точку{MOUSELEFT}",
            "Отметьте третью точку{MOUSELEFT}",
            "Отметьте четвертую точку{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PAIROFLINES.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    // draw hyperbole on mouse move in 2D mode
    if (_numOfChosenAnchors == 3 && !is3d) {
      try {
        PairOfLines pairOfLines = new PairOfLines(anchor(0).getPoint(), anchor(1).getPoint(), anchor(2).getPoint(), currPoint);
        Drawer.drawPairOfLines(ren, anchor(0).getPoint(), anchor(1).getPoint(), anchor(2).getPoint(), currPoint);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 4) {
          doIfFourPointsChosen(e);
        }
      }
    };
  }

  @Override
  protected void create() {
    PairOfLinesBuilder builder = new PairOfLinesBuilder();
    builder.setValue(PairOfLinesBuilder.BLDKEY_NAME, _name);
    builder.setValue(PairOfLinesBuilder.BLDKEY_P1, _anchor.get(0).id());
    builder.setValue(PairOfLinesBuilder.BLDKEY_P2, _anchor.get(1).id());
    builder.setValue(PairOfLinesBuilder.BLDKEY_P3, _anchor.get(2).id());
    builder.setValue(PairOfLinesBuilder.BLDKEY_P4, _anchor.get(3).id());
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PAIR_OF_LINES;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PAIROFLINES.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PAIROFLINES.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Пара прямых</strong><br>по четырем точкам";
  }

  @Override
  protected boolean isEnabled() {
    return (!canvas().is3d() || _ctrl.containsAnchors(AnchorType.ANC_POINT, 4));
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
    i_Anchor a = getPointAnchor(id);
    if (a == null) {
      return;
    }
    if (_numOfChosenAnchors < 4) {
      if (_numOfChosenAnchors >= 1 && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      if (!checker.checkPointsNotCollinear(_anchor, a)) {
        return;
      }
      chooseAnchor(a);
      currPoint = a.getPoint();
    }
    if (_numOfChosenAnchors == 4) {
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      doIfFourPointsChosen();
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setCreatePointMode(true);
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 4) {
          doIfFourPointsChosen(e);
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (_numOfChosenAnchors == 3) {
          doWhenThreePointsChosen(e);
        }
      }
    };
  }
}
