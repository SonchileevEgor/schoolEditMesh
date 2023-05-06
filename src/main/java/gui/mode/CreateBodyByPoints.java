package gui.mode;

import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import java.util.Collection;

/**
 *
 * @author rita
 */
public abstract class CreateBodyByPoints extends CreateBodyMode implements i_FocusChangeListener {

  public CreateBodyByPoints(EdtController ctrl) {
    super(ctrl);
  }

  abstract protected void doIfAllPointsChosen();

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
    if (_numOfChosenAnchors == 0) {
      chooseAnchor(a);
    } else {
      // 1st point has already chosen.
      if (!checker.checkBasePointsDifferent(_anchorID, a.id())) {
        return;
      }
      if (a.id().equals(id(0))) {
        // Base closed
        if (!checker.checkMoreThan2Points(_anchor)) {
          return;
        }
        doIfAllPointsChosen();
      } else {
        // проверяем на самопересечения, лежат ли точки на одной линии и на компланарность
        if (!checker.checkPointSuits(_anchor, a.getPoint())) {
          return;
        }
        chooseAnchor(a);
      }
    }
  }

  @Override
  public boolean isEnabled() {
    return (!canvas().is3d() || _ctrl.containsAnchors(AnchorType.ANC_POINT, 3));
  }
}
