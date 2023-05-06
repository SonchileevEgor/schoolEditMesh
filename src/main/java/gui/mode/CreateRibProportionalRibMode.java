package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.RibProportionalRibBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import geom.ExDegeneration;
import geom.Rib3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.COEFFICIENT;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Лютенков
 */
public class CreateRibProportionalRibMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  BodyType _type;

  public CreateRibProportionalRibMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(1));
    }
    if (_numOfChosenAnchors == 3) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Rib3d result = null;
      try {
        Rib3d tmp = _anchor.get(0).getRib();
        result = Rib3d.RibProportionalRibAndTwoPnts(tmp, point(1), point(2), valueAsDouble(COEFFICIENT));
      } catch (ExDegeneration ex) {
      }

      Drawer.drawPoint(ren, result.a());
      Drawer.drawPoint(ren, result.b());
      Drawer.drawSegment(ren, result.a(), result.b());
    }
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<BodyType>();
    _types.add(BodyType.RIB);
    canvas().getHighlightAdapter().setTypes(_types);
    _param.setUsed(COEFFICIENT, 1);
    _param.setValue(COEFFICIENT, 0.5);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
    _ctrl.redraw();
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
    if (_numOfChosenAnchors == 0) {
      i_Anchor a = getRibAnchor(id);
      chooseAnchor(a);
      canvas().getHighlightAdapter().setTypes(BodyType.POINT);
      canvas().getHighlightAdapter().setCreatePointMode(true);
    } else if (_numOfChosenAnchors == 1) {
      i_Anchor a = getPointAnchor(id);
      chooseAnchor(a);
    } else if (_numOfChosenAnchors == 2) {
      i_Anchor a = getPointAnchor(id);
      if (!checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      chooseAnchor(a);
      showValue(COEFFICIENT);
      canvas().getScaleAdapter().setBlocked(true);
    }
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 3) {
          if (!isChosen(COEFFICIENT)) {
            changeValue(COEFFICIENT, e.getWheelRotation());
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
          }
        }
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 3) {
          showValue(COEFFICIENT);
          changeValue(COEFFICIENT, e);
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
          if (isChosen(COEFFICIENT)) {
            createWithNameValidation();
          }
        }
      }
    };
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите отрезок{MOUSELEFT}",
            "Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}",
            "Выберите длину отрезка{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RIB_PROPORTIONALN_RIB.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RIB_PROPORTIONALN_RIB.getLargeIcon();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RIB_PROPORTIONALN_RIB;
  }

  @Override
  protected void setName() {
    _name = BodyType.RIB.getName(_ctrl.getEditor());
  }

  @Override
  protected void create() {
    RibProportionalRibBuilder builder = new RibProportionalRibBuilder();
    builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
    builder.setValue(BodyBuilder.BLDKEY_A, id(1));
    builder.setValue(BodyBuilder.BLDKEY_B, id(2));
    builder.setValue(BodyBuilder.BLDKEY_RIB, id(0));
    builder.setValue(BodyBuilder.BLDKEY_COEFFICIENT, valueAsDouble(COEFFICIENT));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Отрезок</strong><br>пропорциональный данному";
  }

  @Override
  protected boolean isEnabled() {
    return (_ctrl.containsAnchors(AnchorType.ANC_RIB, 1)
            && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1)
            && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
