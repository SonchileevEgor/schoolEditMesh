package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_BODY;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.TranslationBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.StyleArrow;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;

/**
 * @author Elena
 */
public class CreateParallelTranslationMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  BodyType _type;// type of object
  String _bodyID;

  public CreateParallelTranslationMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.addAll(BodyType.getAllTypes());
    canvas().getHighlightAdapter().setTypes(BodyType.POINT);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите начало вектора переноса{MOUSELEFT}",
            "Выберите конец вектора переноса{MOUSELEFT}",
            "Выберите тело для отображения{MOUSELEFT}");
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
    if (_numOfChosenAnchors <= 1) {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      if (_numOfChosenAnchors == 1 && !checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      _anchor.add(a);
      _anchorID.add(a.id());
      _numOfChosenAnchors++;
      _ctrl.status().showMessage(_msg.current());
      canvas().getHighlightAdapter().setTypes(_types);
    } else if (_numOfChosenAnchors == 2) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() == BodyType.INTERSECT_BODY) {
          return;
        }
        _type = bd.type();
        _bodyID = id;
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        create();
      } catch (ExNoBody ex) {
      }
      _ctrl.redraw();
    }
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
    if (_numOfChosenAnchors >= 2) {
      Drawer.drawPoint(ren, _anchor.get(1).getPoint());
      Drawer.drawArrow(ren, _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), TypeArrow.ONE_END, StyleArrow.CONE);
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TRANSLATION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PARALLEL_TRANSLATION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PARALLEL_TRANSLATION.getLargeIcon();
  }

  @Override
  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (_numOfChosenAnchors == 3) {
          create();
        }
      }
    };
  }

  @Override
  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
    };
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _type.getName(_ctrl.getEditor())));
    params.put(BLDKEY_BODY, new BuilderParam(BLDKEY_BODY, "body", BuilderParamType.BODY, _bodyID));
    params.put(TranslationBuilder.BLDKEY_P1,
            new BuilderParam(TranslationBuilder.BLDKEY_P1, "Начало вектора переноса", BuilderParamType.ANCHOR, id(0)));
    params.put(TranslationBuilder.BLDKEY_P2,
            new BuilderParam(TranslationBuilder.BLDKEY_P2, "Конец вектора переноса", BuilderParamType.ANCHOR, id(1)));
    TranslationBuilder builder = new TranslationBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Параллельный перенос</strong><br>на вектор";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.POINT, 2);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
