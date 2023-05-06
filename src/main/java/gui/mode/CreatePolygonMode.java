package gui.mode;

import builders.PolygonBuilder;
import builders.TriangleBuilder;
import gui.EdtController;
import gui.IconList;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Режим построения многоугольника. Пользователь выбирает вершины многоугольника. Вершина
 * добавляется в список в том случае, когда список проходит проверку на самопересечения, никакие три
 * точки не лежат на одной линии и никакие четыре точки не лежат в одной плоскости. Когда
 * пользователь замыкает многоугольник, происходит его построение и переход в режим просмотра сцены.
 *
 * @author alexeev, rita
 */
public class CreatePolygonMode extends CreateBodyByPoints {

  public CreatePolygonMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.POLYGON.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.POLYGON.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Многоугольник</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POLYGON;
  }

  @Override
  protected void doIfAllPointsChosen() {
    create();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setRepeatMessage("Отметьте вершины многоугольника{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = "";
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, point(i));
    }
    for (int i = 0; i < _anchor.size() - 1; i++) {
      Drawer.drawSegment(ren, point(i), point(i + 1));
    }
  }

  @Override
  protected void create() {
    String name;
    if (_anchorID.size() == 3) {
      TriangleBuilder builder = new TriangleBuilder();
      builder.addA(id(0));
      builder.addB(id(1));
      builder.addC(id(2));
      _ctrl.add(builder, checker, false);
    } else {
      PolygonBuilder builder = new PolygonBuilder();
      builder.addPoints(_anchorID);
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
