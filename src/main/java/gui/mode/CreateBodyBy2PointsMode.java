package gui.mode;

import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.Vect3d;
import gui.EdtController;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;

/**
 *
 * @author rita
 */
public abstract class CreateBodyBy2PointsMode extends CreateBodyMode implements i_FocusChangeListener {

  protected Vect3d currPoint; // вспомогательная точка, используемая при предварительной отрисовке.

  public CreateBodyBy2PointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setCreatePointMode(true);
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  abstract protected void doIfTwoPointsChosen();

  abstract protected void doIfTwoPointsChosen(MouseWheelEvent e);

  abstract protected void doIfTwoPointsChosen(KeyEvent e);

  /**
   * Mouse-depending action after than one points are chosen.
   *
   * @param e mouse event.
   * @author alexeev
   */
  abstract protected void doWhenOnePointChosen(MouseEvent e);

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
    if (_numOfChosenAnchors < 2) {
      if (_numOfChosenAnchors == 1 && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      chooseAnchor(a);
      currPoint = a.getPoint();
    }
    if (_numOfChosenAnchors == 2) {
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      doIfTwoPointsChosen();
    }
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          doIfTwoPointsChosen(e);
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (_numOfChosenAnchors == 1) {
          doWhenOnePointChosen(e);
        }
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 2) {
          doIfTwoPointsChosen(e);
        }
      }
    };
  }

  @Override
  public boolean isEnabled() {
    return !canvas().is3d() || _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }
}
