package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.IsoscelesTrapezeBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Куганский
 */
public class CreateIsoscelesTrapezeMode extends CreateBodyBy3PointsMode {

  public CreateIsoscelesTrapezeMode(EdtController ctrl) {
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
    _msg.setMessage("Отметьте первую точку равнобокой трапеции{MOUSELEFT}",
            "Отметьте вторую точку равнобокой трапеции{MOUSELEFT}",
            "Отметьте третью точку равнобокой трапеции{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.TRAPEZE.getName(_ctrl.getEditor());
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
          Polygon3d poly = Polygon3d.isoscelesTrapezeBy3Pnts(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
          Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
        }
      } catch (ExDegeneration ex) {
      } catch (ExGeom ex) {
        Logger.getLogger(CreateParallelogramMode.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ISOSCELES_TRAPEZE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ISOSCELES_TRAPEZE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ISOSCELES_TRAPEZE.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> paralParams = new HashMap<String, BuilderParam>();
    paralParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME, _name));
    paralParams.put(BLDKEY_A, new BuilderParam(BLDKEY_A, BuilderParam.VERT_1_BASE_1_ALIAS, BuilderParamType.ANCHOR, _anchor.get(0).id()));
    paralParams.put(BLDKEY_B, new BuilderParam(BLDKEY_B, BuilderParam.VERT_2_BASE_1_ALIAS, BuilderParamType.ANCHOR, _anchor.get(1).id()));
    paralParams.put(BLDKEY_C, new BuilderParam(BLDKEY_C, BuilderParam.VERT_BASE_2_ALIAS, BuilderParamType.ANCHOR, _anchor.get(2).id()));
    IsoscelesTrapezeBuilder builder = new IsoscelesTrapezeBuilder(paralParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Равнобокая трапеция</strong><br>по трем вершинам";
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
