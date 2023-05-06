package gui.mode;

import bodies.BodyType;
import builders.ConicByFivePointsBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import geom.Circle3d;
import geom.ConicPivots;
import geom.EllipseMain3d;
import geom.ExDegeneration;
import static geom.GeomType.CIRCLE3D;
import geom.Hyperbole3d;
import geom.Parabola3d;
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
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Ivan
 */
public class CreateConicByFivePointsMode extends CreateBodyMode implements i_FocusChangeListener {

  protected Vect3d currPoint; // вспомогательная точка, используемая при предварительной отрисовке.

  public CreateConicByFivePointsMode(EdtController ctrl) {
    super(ctrl);
  }

  protected void doIfFivePointsChosen() {
    createWithNameValidation();
  }

  protected void doIfFivePointsChosen(MouseWheelEvent e) {
  }

  protected void doIfFivePointsChosen(KeyEvent e) {
  }

  protected void doWhenFourPointsChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(3).getPoint();
      }
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую точку коники{MOUSELEFT}",
            "Отметьте вторую точку коники{MOUSELEFT}",
            "Отметьте третью точку коники{MOUSELEFT}",
            "Отметьте четвертую точку коники{MOUSELEFT}",
            "Отметьте пятую точку коники{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CONIC.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    // draw conic on mouse move in 2D mode
    if (_numOfChosenAnchors == 4 && !is3d) {
      try {
        ConicPivots conic = new ConicPivots(anchor(0).getPoint(), anchor(1).getPoint(), anchor(2).getPoint(),
                anchor(3).getPoint(), currPoint);
        switch (conic.type()) {
          case PARABOLA3D:
            Parabola3d parabola = new Parabola3d(conic.pivots().get(0), conic.pivots().get(1), conic.pivots().get(2));
            Drawer.drawParabola(ren, parabola, TypeFigure.CURVE);
            break;
          case ELLIPSE3D:
            EllipseMain3d ellipse = new EllipseMain3d(conic.pivots().get(0), conic.pivots().get(1), conic.pivots().get(2));
            Drawer.drawEllipse(ren, ellipse, TypeFigure.WIRE);
            break;
          case HYPERBOLE3D:
            Hyperbole3d hyperbole = new Hyperbole3d(conic.pivots().get(0), conic.pivots().get(1), conic.pivots().get(2));
            Drawer.drawHyperbole(ren, hyperbole, TypeFigure.CURVE);
            break;
          case CIRCLE3D:
            Circle3d circle = new Circle3d(conic.pivots().get(0), conic.pivots().get(1), conic.pivots().get(2));
            Drawer.drawCircle(ren, circle.radiusLength(), circle.center(), circle.normal(), TypeFigure.WIRE);
            break;
          case LINE3D:
            Drawer.drawLine(ren, point(0), currPoint);
            break;
          case PAIROFLINES3D:
            Drawer.drawPairOfLines(ren, conic.pivots().get(0), conic.pivots().get(1), conic.pivots().get(2), conic.pivots().get(3));
            break;
        }
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
        if (_numOfChosenAnchors == 5) {
          doIfFivePointsChosen(e);
        }
      }
    };
  }

  @Override
  protected void create() {
    ConicByFivePointsBuilder builder = new ConicByFivePointsBuilder();
    builder.setValue(ConicByFivePointsBuilder.BLDKEY_NAME, _name);
    builder.setValue(ConicByFivePointsBuilder.BLDKEY_P1, _anchor.get(0).id());
    builder.setValue(ConicByFivePointsBuilder.BLDKEY_P2, _anchor.get(1).id());
    builder.setValue(ConicByFivePointsBuilder.BLDKEY_P3, _anchor.get(2).id());
    builder.setValue(ConicByFivePointsBuilder.BLDKEY_P4, _anchor.get(3).id());
    builder.setValue(ConicByFivePointsBuilder.BLDKEY_P5, _anchor.get(4).id());
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CONIC;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CONIC.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CONIC.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Коника</strong><br>по пяти точкам";
  }

  @Override
  protected boolean isEnabled() {
    return (!canvas().is3d() || _ctrl.containsAnchors(AnchorType.ANC_POINT, 5));
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
    if (_numOfChosenAnchors < 5) {
      if (_numOfChosenAnchors >= 1 && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      if (!checker.check4PointsNotOnOneLine(_anchor, a)) {
        return;
      }
      chooseAnchor(a);
      currPoint = a.getPoint();
    }
    if (_numOfChosenAnchors == 5) {
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      doIfFivePointsChosen();
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
        if (_numOfChosenAnchors == 5) {
          doIfFivePointsChosen(e);
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (_numOfChosenAnchors == 4) {
          doWhenFourPointsChosen(e);
        }
      }
    };
  }
}
