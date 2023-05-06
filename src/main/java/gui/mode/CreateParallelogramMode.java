package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.ParallelogramBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author rita
 */
public class CreateParallelogramMode extends CreateBodyBy3PointsMode {

  public CreateParallelogramMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void doIfThreePointsChosen() {
    createWithNameValidation();
  }

  @Override
  protected void doIfThreePointsChosen(MouseWheelEvent e) {
  }

  @Override
  protected void doIfThreePointsChosen(KeyEvent e) {
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(0).getPoint();
      }
      _ctrl.redraw();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую точку параллелограмма{MOUSELEFT}",
            "Отметьте вторую точку параллелограмма{MOUSELEFT}",
            "Отметьте третью точку параллелограмма{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PARALLELOGRAM.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    for (int i = 0; i < _anchor.size() - 1; i++) {
      Drawer.drawSegment(ren, _anchor.get(i).getPoint(), _anchor.get(i + 1).getPoint());
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.drawPoint(ren, currPoint);
      try {
        if (Vect3d.dist(currPoint, anchor(1).getPoint()) > 0) {
          Polygon3d poly = Polygon3d.parallelogramBy3Pnts(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
          Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
        }
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PARALLELOGRAM;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PARALLELOGRAM.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PARALLELOGRAM.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> paralParams = new HashMap<String, BuilderParam>();
    paralParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    paralParams.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "Первая вершина параллелограмма", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    paralParams.put(BLDKEY_B, new BuilderParam(BLDKEY_B, "Вторая вершина параллелограмма", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    paralParams.put(BLDKEY_C, new BuilderParam(BLDKEY_C, "Третья вершина параллелограмма", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    ParallelogramBuilder builder = new ParallelogramBuilder(paralParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Параллелограмм</strong><br>по трем вершинам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
