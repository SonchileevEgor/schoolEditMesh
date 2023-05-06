package gui.mode;

import bodies.BodyType;
import builders.BisectorPlaneOf2PlanesBuilder;
import static builders.BodyBuilder.BLDKEY_DIRECTION;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Body;
import java.util.Collection;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author ???
 */
public class CreateBisectorPlaneMode extends CreateBodyMode implements i_FocusChangeListener {

  private static final String SUFFIX_1 = "_1";
  private static final String SUFFIX_2 = "_2";
  private String _planeID_1, _planeID_2, _resID_1, _resID_2;
  private boolean _direction = true;

  public CreateBisectorPlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.PLANE);
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
      // выбираем 1-ю плоскость
      try {
        i_Body pl1 = _ctrl.getBody(id);
        if (pl1.type() != BodyType.PLANE) {
          return;
        }
        _planeID_1 = id;
        _numOfChosenAnchors++;

        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      // выбираем 2-ю плоскость, проверяем на совпадение,
      // создаём биссекторные плоскости.
      try {
        i_Body pl2 = _ctrl.getBody(id);
        if (pl2.type() != BodyType.PLANE) {
          return;
        }
        _planeID_2 = id;
        if (!checker.checkBodiesDifferent(_planeID_1, _planeID_2)) {
          return;
        }
        try {
          validateName(SUFFIX_1);
          validateName(SUFFIX_2);
          create();
        } catch (InvalidBodyNameException ex) {
          _ctrl.status().showMessage(ex.getMessage());
        }
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  public KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 2) {
          if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            _direction = true;
            _ctrl.getFocusCtrl().setFocusOnBody(_resID_1);
            _ctrl.redraw();
          } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            _direction = false;
            _ctrl.getFocusCtrl().setFocusOnBody(_resID_2);
            _ctrl.redraw();
          } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            _ctrl.removeBody(_direction ? _resID_2 : _resID_1);
            exit();
          }
        }
      }
    };
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.BISECTOR_PLANE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.BISECTOR_PLANE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.PLANE, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Биссекторная плоскость</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_BISECTOR_PLANE;
  }

  @Override
  protected void create() {
    // создаём две биссекторные плоскости.
    HashMap<String, BuilderParam> params1 = new HashMap<>();
    params1.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name + SUFFIX_1));
    params1.put(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE1,
            new BuilderParam(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE1, "1-я плоскость", BuilderParamType.BODY, _planeID_1));
    params1.put(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE2,
            new BuilderParam(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE2, "2-я плоскость", BuilderParamType.BODY, _planeID_2));
    params1.put(BLDKEY_DIRECTION, new BuilderParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN, true));
    i_BodyBuilder builder1 = new BisectorPlaneOf2PlanesBuilder(params1);
    HashMap<String, BuilderParam> params2 = new HashMap<>();
    params2.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name + SUFFIX_2));
    params2.put(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE1,
            new BuilderParam(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE1, "1-я плоскость", BuilderParamType.BODY, _planeID_1));
    params2.put(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE2,
            new BuilderParam(BisectorPlaneOf2PlanesBuilder.BLDKEY_PLANE2, "2-я плоскость", BuilderParamType.BODY, _planeID_2));
    params2.put(BLDKEY_DIRECTION, new BuilderParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN, false));
    i_BodyBuilder builder2 = new BisectorPlaneOf2PlanesBuilder(params2);
    if (_ctrl.add(builder1, checker, false) && _ctrl.add(builder2, checker, false)) {
      _resID_1 = builder1.id();
      _resID_2 = builder2.id();
      _numOfChosenAnchors++;
      _ctrl.status().showMessage(_msg.current());
      _ctrl.getFocusCtrl().setFocusOnBody(_resID_1);
      canvas().getHighlightAdapter().setBlocked(true);
      _ctrl.redraw();
    } else {
      exit();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую плоскость{MOUSELEFT}",
            "Выберите вторую плоскость{MOUSELEFT}",
            "Выберите биссекторную плоскость{UP}{DOWN}{ENTER}");
    // + "<br>Если вы хотите оставить обе плоскости, нажмите ESC</html>");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
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
