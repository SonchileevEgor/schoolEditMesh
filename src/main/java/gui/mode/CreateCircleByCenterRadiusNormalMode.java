package gui.mode;

import bodies.BodyType;
import bodies.LineBody;
import bodies.RayBody;
import static builders.BodyBuilder.BLDKEY_CENTER;
import static builders.BodyBuilder.BLDKEY_LINE;
import static builders.BodyBuilder.BLDKEY_NAME;
import static builders.BodyBuilder.BLDKEY_RADIUS;
import static builders.BodyBuilder.BLDKEY_RAY;
import static builders.BodyBuilder.BLDKEY_RIB;
import builders.CircleByCenterRadiusLineBuilder;
import builders.CircleByCenterRadiusRayBuilder;
import builders.CircleByCenterRadiusRibBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.AnchorType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import geom.ExDegeneration;
import geom.Rib3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.RADIUS;
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
import static opengl.TypeFigure.WIRE;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Лютенков
 */
public class CreateCircleByCenterRadiusNormalMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  private BodyType _type; //plane or poly
  private String _id1;

  public CreateCircleByCenterRadiusNormalMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _types = new ArrayList<BodyType>();
    _types.add(BodyType.LINE);
    _types.add(BodyType.RAY);
    _types.add(BodyType.RIB);
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
    canvas().getHighlightAdapter().setCreatePointMode(true);
    canvas().getScaleAdapter().setBlocked(true); // avoid scaling on mouse wheel
    _param.setUsed(RADIUS, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_BY_CENT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_BY_CENT.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.LINE, 1) || _ctrl.containsAnchors(AnchorType.ANC_RIB, 1)
            || _ctrl.containsBodies(BodyType.RAY, 1)) && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Окружность</strong><br>по центру и радиусу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCLE_CENTER_RADIUS_NORMAL;
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (_numOfChosenAnchors == 2) {
          changeValue(RADIUS, e.getWheelRotation());
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
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
          changeValue(RADIUS, e);
          _ctrl.getMainCanvasCtrl().notifyModeParamChange(RADIUS);
          if (isChosen(RADIUS)) {
            createWithNameValidation();
          }
        }
      }
    };
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_CENTER, new BuilderParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_RADIUS, new BuilderParam(BLDKEY_RADIUS, "Радиус", BuilderParamType.DOUBLE_POSITIVE, valueAsDouble(RADIUS)));
    if (_type == BodyType.LINE) {
      params.put(BLDKEY_LINE, new BuilderParam(BLDKEY_LINE, "Прямая", BuilderParamType.BODY, _id1));
      CircleByCenterRadiusLineBuilder builder = new CircleByCenterRadiusLineBuilder(params);
      _ctrl.add(builder, checker, false);
    } else if (_type == BodyType.RIB) {
      params.put(BLDKEY_RIB, new BuilderParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, _id1));
      CircleByCenterRadiusRibBuilder builder = new CircleByCenterRadiusRibBuilder(params);
      _ctrl.add(builder, checker, false);
    } else {
      params.put(BLDKEY_RAY, new BuilderParam(BLDKEY_RAY, "Луч", BuilderParamType.BODY, _id1));
      CircleByCenterRadiusRayBuilder builder = new CircleByCenterRadiusRayBuilder(params);
      _ctrl.add(builder, checker, false);
    }
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
      return;
    }
    if (_numOfChosenAnchors == 0) {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      chooseAnchor(a);
      canvas().getHighlightAdapter().setCreatePointMode(false);
      canvas().getHighlightAdapter().setTypes(_types);
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        _type = bd.type();
        if (_type == BodyType.RIB) {
          _id1 = getRibAnchor(id).id();
        } else if ((_type == BodyType.LINE) || (_type == BodyType.RAY)) {
          _id1 = id;
        } else {
          return;
        }
      } catch (ExNoBody ex) {
      }
      _numOfChosenAnchors++;
      showValue(RADIUS);
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите центр окружности{MOUSELEFT}",
            "Выберите нормаль (отрезок, прямая или луч){MOUSELEFT}",
            "Выберите радиус {UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
      if (_type == BodyType.RIB) {
        try {
          Rib3d rib = _ctrl.getAnchor(_id1).getRib();
          Drawer.drawCircle(ren, valueAsDouble(RADIUS), _anchor.get(0).getPoint(), rib.line().l(), WIRE);
        } catch (ExDegeneration | ExNoAnchor ex) {
        }
      } else {
        try {
          if (_type == BodyType.LINE) {
            LineBody line = (LineBody)_ctrl.getBody(_id1);
            Drawer.drawCircle(ren, valueAsDouble(RADIUS), _anchor.get(0).getPoint(), line.l(), WIRE);
          } else {
            RayBody ray = (RayBody)_ctrl.getBody(_id1);
            Drawer.drawCircle(ren, valueAsDouble(RADIUS), _anchor.get(0).getPoint(), ray.ray().l(), WIRE);
          }
        } catch (ExNoBody ex) {
        }
      }
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
