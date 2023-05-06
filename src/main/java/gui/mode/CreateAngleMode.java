package gui.mode;

import bodies.BodyType;
import bodies.PointBody;
import builders.AngleBetweenPlaneLineBuilder;
import builders.AngleBetweenTwoLinesBuilder;
import builders.AngleByThreePointsBuilder;
import builders.RibBuilder;
import static config.Config.LOG_LEVEL;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import editor.state.DisplayParam;
import geom.Angle3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import gui.elements.AngleStyle;
import gui.elements.SidesSizeType;
import static gui.mode.ScreenMode.checker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;
import util.Log;

/**
 *
 * @author alexeev
 */
public class CreateAngleMode extends CreateBodyMode implements i_FocusChangeListener {

  private AngleStyle _angleStyle;
  private ArrayList<BodyType> _allowedTypes;
  private ArrayList<String> _ids;
  private ArrayList<BodyType> _types;

  public CreateAngleMode(EdtController ctrl) {
    super(ctrl);
    _ids = new ArrayList<>();
    _types = new ArrayList<>();
    _allowedTypes = new ArrayList<>();
    _angleStyle = AngleStyle.SINGLE;
  }

  public CreateAngleMode(EdtController ctrl, AngleStyle style) {
    this(ctrl);
    _angleStyle = style;
  }

  @Override
  public void run() {
    super.run();
    _ids.clear();
    _types.clear();
    _allowedTypes.clear();
    _allowedTypes.add(BodyType.POINT);
    _allowedTypes.addAll(BodyType.getPlainBodies());
    _allowedTypes.addAll(BodyType.getLinearBodies());
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(_allowedTypes);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите тела, по которым будет построен угол{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ANGLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {

  }

  @Override
  protected void nativeDraw0(Render ren) {

  }

  @Override
  protected void create() {
    if (_types.get(0) == BodyType.POINT) {
      createByThreePoints();
      reset();
    } else if (_types.get(0).isLinear() && _types.get(1).isLinear()) {
      createByLinearBodies();
    } else {
      createByPlainAndLinearBodies();
    }
  }

  /**
   * Создание угла по плоскому и линейному телу.
   */
  private void createByPlainAndLinearBodies() {
    i_BodyBuilder builders[] = new AngleBetweenPlaneLineBuilder[4];
    ArrayList<String> builder_ids = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      AngleBetweenPlaneLineBuilder builder = new AngleBetweenPlaneLineBuilder(_name);
      if (_types.get(0).isPlain()) {
        builder.addPlane(_ids.get(0));
        builder.addLine(_ids.get(1));
      } else {
        builder.addPlane(_ids.get(1));
        builder.addLine(_ids.get(0));
      }
      builder.addDirection1(i % 2 == 0); // чередуем направления
      builder.addDirection2(i / 2 == 0); // чередуем направления
      builders[i] = builder;
      builder_ids.add(builder.id());
    }

    if (!_ctrl.addBodies(Arrays.asList(builders), checker, false)) {
      reset();
      return;
    }

    for (i_BodyBuilder builder: builders) {
      try {
        _ctrl.getBody(builder.id()).getState().setParam(
                DisplayParam.SIDES_SIZE_TYPE, SidesSizeType.LONG_LONG);
        _ctrl.getBody(builder.id()).getState().setParam(DisplayParam.ANGLE_STYLE, _angleStyle);
      } catch (ExNoBody ex) {
        if (LOG_LEVEL.value() >= 2) {
          Log.out.println("Не удалось изменить параметр угла");
        }
      }
    }

    // Включаем режим выбора одного из четырёх углов.
    RetainOneBodyMode chooseAngleMode = new RetainOneBodyMode(_ctrl, builder_ids, "Выберите угол");
    canvas().setMode(chooseAngleMode);
  }

  /**
   * Создание угла по двум линейным телам.
   */
  private void createByLinearBodies() {
    i_BodyBuilder builders[] = new AngleBetweenTwoLinesBuilder[4];
    ArrayList<String> builder_ids = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      AngleBetweenTwoLinesBuilder builder = new AngleBetweenTwoLinesBuilder(_name);
      builder.addLine1(_ids.get(0));
      builder.addLine2(_ids.get(1));
      builder.addDirection1(i % 2 == 0); // чередуем направления
      builder.addDirection2(i / 2 == 0); // чередуем направления
      builders[i] = builder;
      builder_ids.add(builder.id());
    }

    if (!_ctrl.addBodies(Arrays.asList(builders), checker, false)) {
      reset();
      return;
    }

    for (i_BodyBuilder builder: builders) {
      try {
        _ctrl.getBody(builder.id()).getState().setParam(
                DisplayParam.SIDES_SIZE_TYPE, SidesSizeType.LONG_LONG);
        _ctrl.getBody(builder.id()).getState().setParam(DisplayParam.ANGLE_STYLE, _angleStyle);
      } catch (ExNoBody ex) {
        if (LOG_LEVEL.value() >= 2) {
          Log.out.println("Не удалось изменить параметр угла");
        }
      }
    }

    // Включаем режим выбора одного из четырёх углов.
    RetainOneBodyMode chooseAngleMode = new RetainOneBodyMode(_ctrl, builder_ids, "Выберите угол");
    canvas().setMode(chooseAngleMode);
  }

  private void createByThreePoints() {
    ArrayList<i_BodyBuilder> builders = new ArrayList<>();

    try {
      // Если нет ребра AB, создаём его.
      if (_ctrl.getAnchorContainer().findEqual(_ids.get(0), _ids.get(1)) == null) {
        RibBuilder rb = new RibBuilder(_ctrl.getAnchorTitle(_ids.get(0))
                + _ctrl.getAnchorTitle(_ids.get(1)));
        rb.addA(_ids.get(0));
        rb.addB(_ids.get(1));
        builders.add(rb);
      }

      // Если нет ребра BC, создаём его.
      if (_ctrl.getAnchorContainer().findEqual(_ids.get(1), _ids.get(2)) == null) {
        RibBuilder rb = new RibBuilder(_ctrl.getAnchorTitle(_ids.get(1))
                + _ctrl.getAnchorTitle(_ids.get(2)));
        rb.addA(_ids.get(1));
        rb.addB(_ids.get(2));
        builders.add(rb);
      }
    } catch (ExNoAnchor ex) {
    }

    AngleByThreePointsBuilder builder = new AngleByThreePointsBuilder(_name);
    builder.addPointOnFirstSide(_ids.get(0));
    builder.addVertex(_ids.get(1));
    builder.addPointOnSecondSide(_ids.get(2));

    try {
      Angle3d ang = new Angle3d(
              _ctrl.getAnchor(_ids.get(0)).getPoint(),
              _ctrl.getAnchor(_ids.get(1)).getPoint(),
              _ctrl.getAnchor(_ids.get(2)).getPoint()
      );
      Vect3d eye = _ctrl.getScene().getCameraPosition().eye();
      builder.addLessThanPiParam(ang.isLessThanPI(eye));
    } catch (ExNoAnchor ex) {
      builder.addLessThanPiParam(true);
    }
    builders.add(builder);

    _ctrl.addBodies(builders, checker, false);

    try {
      _ctrl.getBody(builder.id()).getState().setParam(
              DisplayParam.SIDES_SIZE_TYPE, SidesSizeType.SHORT_SHORT);
      _ctrl.getBody(builder.id()).getState().setParam(DisplayParam.ANGLE_STYLE, _angleStyle);
    } catch (ExNoBody ex) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.println("Не удалось изменить параметр угла");
      }
    }
  }

  /**
   * Установить стиль создаваемого угла.
   *
   * @param style
   */
  public void setAngleStyle(AngleStyle style) {
    _angleStyle = style;
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ANGLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ANGLE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ANGLE.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Угол</strong><br>Выберите три точки или два тела";
  }

  @Override
  protected boolean isEnabled() {
    return true;
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
    try {
      i_Body bd = _ctrl.getBody(id);
      if (!_allowedTypes.contains(bd.type())) {
        return;
      }
      if (bd.type() == BodyType.POINT) {
        String anchorID = _ctrl.getAnchorID(id, PointBody.BODY_KEY_POINT);
        if (!checker.checkPointsDifferent(_ids, anchorID)) {
          return;
        }
        _ids.add(anchorID);
      } else {
        if (_numOfChosenAnchors > 0) {
          if (!checker.checkBodiesDifferent(_ids.get(0), id)) {
            return;
          }
        }
        _ids.add(bd.id());
      }
      _types.add(bd.type());
      if (_numOfChosenAnchors == 0) {
        if (bd.type() == BodyType.POINT) {
          _allowedTypes.clear();
          _allowedTypes.add(BodyType.POINT); // построение по трём точкам
        } else if (bd.type().isPlain()) {
          _allowedTypes.remove(BodyType.POINT);
          _allowedTypes.removeAll(BodyType.getPlainBodies()); // построение по плоскости и линии
        } else {
          _allowedTypes.remove(BodyType.POINT); // построение по линии и ещё чему-нибудь
        }
        canvas().getHighlightAdapter().setTypes(_allowedTypes);
        _numOfChosenAnchors++;
      } else if (_numOfChosenAnchors == 1) {
        _numOfChosenAnchors++;
        if (bd.type() != BodyType.POINT) {
          create(); // все построения, кроме 3 точек, двухшаговые
        }
      } else {
        create(); // построение по трем точкам
      }
    } catch (ExNoBody | ExNoAnchor ex) {
    }
  }

}
