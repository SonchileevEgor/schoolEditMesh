package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_BODY;
import static builders.BodyBuilder.BLDKEY_CENTER;
import static builders.BodyBuilder.BLDKEY_COEFFICIENT;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.HomothetyBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.Plane3d;
import geom.SpaceTransformation;
import geom.Vect3d;
import geom.i_Geom;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.COEFFICIENT;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Elena
 */
public class CreateHomothetyMode extends CreateBodyMode implements i_FocusChangeListener {

  private i_Body _body;
  private Vect3d _center;
  private BodyType _type;// type of object
  private ArrayList<BodyType> _types;
  private String _bodyID, _id2;

  public CreateHomothetyMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.addAll(BodyType.getAllTypes());
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
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
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      _id2 = a.id();
      _center = a.getPoint();
      _numOfChosenAnchors++;
      _ctrl.status().showMessage(_msg.current());
      canvas().getHighlightAdapter().setTypes(_types);
      canvas().getScaleAdapter().setBlocked(true);
      _ctrl.redraw();
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() == BodyType.INTERSECT_BODY) {
          return;
        }
        _type = bd.type();
        _bodyID = id;
        _body = bd;
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _param.showValue(COEFFICIENT);
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите точку - центр гомотетии{MOUSELEFT}",
            "Выберите тело для отображения{MOUSELEFT}",
            "Выберите коэффициент гомотетии{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw0(Render ren) {
    if ((_numOfChosenAnchors == 2) && (_type == BodyType.PLANE)) {
      //плоскость строится отдельно, чтобы не перекрывала сцену
      Drawer.setObjectColor(ren, ColorGL.GREY);
      Plane3d plane = (Plane3d)SpaceTransformation.homothetyOfObject(
              _body.getGeom(), _center, valueAsDouble(COEFFICIENT));
      Drawer.drawPlane(ren, plane.pnt(), plane.n());
    }
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _center);
    }
    if (_numOfChosenAnchors == 2 && _type != BodyType.PLANE) {
      i_Geom result = SpaceTransformation.homothetyOfObject(
              _body.getGeom(), _center, valueAsDouble(COEFFICIENT));

      Drawer.setObjectColor(ren, ColorGL.RED);
      BodyDrawer.draw(ren, result);
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_HOMOTHETY;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.HOMOTHETY.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.HOMOTHETY.getLargeIcon();
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          if (!isChosen(COEFFICIENT)) {
            changeValue(COEFFICIENT, e.getWheelRotation());
            _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
          }
        }
      }
    };
  }

  protected void chooseCoef(KeyEvent e) {
    if (!isChosen(COEFFICIENT)) {
      changeValue(COEFFICIENT, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(COEFFICIENT);
    }
    if (isChosen(COEFFICIENT)) {
      create();
    }
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 2) {
          chooseCoef(e);
        }
      }
    };
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();

    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _type.getName(_ctrl.getEditor())));
    params.put(BLDKEY_BODY, new BuilderParam(BLDKEY_BODY, "Тело", BuilderParamType.BODY, _bodyID));
    params.put(BLDKEY_CENTER, new BuilderParam(BLDKEY_CENTER, "Центр гомотетии", BuilderParamType.ANCHOR, _id2));
    params.put(BLDKEY_COEFFICIENT, new BuilderParam(BLDKEY_COEFFICIENT, "Коэффициент гомотетии", BuilderParamType.DOUBLE, valueAsDouble(COEFFICIENT)));
    HomothetyBuilder builder = new HomothetyBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Гомотетия</strong><br>с заданными центром и коэффициентом";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.POINT, 1) && _ctrl.getBodyContainer().size() > 1;
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
