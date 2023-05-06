package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_BASE;
import static builders.BodyBuilder.BLDKEY_HEIGHT;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_POINT_NUM;
import builders.PyramidRectangBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Pyramid3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import static java.lang.Math.abs;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;
import opengl.drawing.DrawingAction;
import opengl.drawing.DrawingQueue;

/**
 *
 * @author Kurgansky
 */
public class CreatePyramidRectangMode extends CreateBodyMode implements i_FocusChangeListener {
  /*
   * step 1 - choose polygon step 2 - choose point - base of height step 3 - choose height
   */

  private int _step;
  private double _height;
  private int _num; //number of point - base of height

  public CreatePyramidRectangMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_step == 2) {
          _num += e.getWheelRotation();
          _ctrl.redraw();
        } else if (_step == 3) {
          _height = press.wheelChangeDouble(_height, e);
          //press.showHeight(_height);
          _ctrl.redraw();
        }
      }
    };
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_step == 2) {
          if (press.up(e)) {
            _num++;
            _ctrl.redraw();
          } else if (press.down(e)) {
            _num--;
            _ctrl.redraw();
          } else if (press.enter(e)) {
            _step++;
            _ctrl.status().showMessage(_msg.current());
            //press.showHeight(_height);
            _ctrl.redraw();
          }
        } else if (_step == 3) {
          if (press.up(e)) {
            _height = press.incDouble(_height);
            //press.showHeight(_height);
            _ctrl.redraw();
          } else if (press.down(e)) {
            _height = press.decDouble(_height);
            //press.showHeight(_height);
            _ctrl.redraw();
          } else if (press.enter(e)) {
            create();
          }
        }
      }
    };
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PYRAMID_RECTANG;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PYRAMID_RECTANG.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PYRAMID_RECTANG.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Прямоугольная пирамида</strong><br>по основанию и высоте";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POLY, 1);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody || _step >= 2) {
      return;
    }
    i_Anchor a = getPolygonAnchor(id);
    if (a == null) {
      return;
    }
    if (_step == 1) {
      if (_numOfChosenAnchors < 1) {
        chooseAnchor(a);
      }
      canvas().getScaleAdapter().setBlocked(true);
      canvas().getHighlightAdapter().setCreatePointMode(false);
    } else {
      _ctrl.status().showMessage(_msg.current());
    }

    _step++;
    _ctrl.redraw();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_BASE, new BuilderParam(BLDKEY_BASE, "Основание", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_HEIGHT, new BuilderParam(BLDKEY_HEIGHT, "Высота", BuilderParamType.DOUBLE_POSITIVE, _height));
    params.put(BLDKEY_POINT_NUM, new BuilderParam(BLDKEY_POINT_NUM, "Номер основания высоты", BuilderParamType.INT, (Object)abs(_num % _anchor.get(0).getPoly().vertNumber())));
    PyramidRectangBuilder builder = new PyramidRectangBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.POLYGON);

    _step = 1;
    _num = 0;
    _height = press.defaultLength();

    DrawingQueue.add(new DrawingAction(1) {
      @Override
      public void draw(Render ren) {
        Drawer.setObjectColor(ren, ColorGL.RED);

        if (_step >= 2) {
          try {
            Drawer.setObjectColor(ren, ColorGL.RED);
            Polygon3d poly = _anchor.get(0).getPoly();
            Pyramid3d pyr = Pyramid3d.rectangPyramidByPoly(poly, abs(_num % poly.vertNumber()), _height);
            for (int i = 0; i < pyr.vertNumber() - 1; i++) {
              Drawer.drawSegment(ren, pyr.top(), pyr.base().points().get(i));
            }
          } catch (ExGeom ex) {
          }
        }
      }
    });
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите основание пирамиды{MOUSELEFT}",
            "Выберите вершину - основание высоты пирамиды{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите высоту{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PYRAMID.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
