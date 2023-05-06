package gui.mode;

import bodies.BodyType;
import builders.ArcBuilder;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.Arc3d;
import geom.ExDegeneration;
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
public class CreateArcMode extends CreateBodyBy3PointsMode {

  public CreateArcMode(EdtController ctrl) {
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
    _msg.setMessage("Отметьте начало дуги{MOUSELEFT}",
            "Отметьте точку на дуге{MOUSELEFT}",
            "Отметьте конец дуги{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ARC.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    if (_numOfChosenAnchors == 2 && Vect3d.dist(currPoint, anchor(1).getPoint()) > 0) {
      try {
        Drawer.drawPoint(ren, currPoint);
        Drawer.drawArc(ren, new Arc3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint),
                TypeFigure.CURVE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ARC;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ARC.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ARC.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> angleParams = new HashMap<>();
    angleParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    angleParams.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "Первый край дуги", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    angleParams.put(BLDKEY_B, new BuilderParam(BLDKEY_B, "Точка на дуге", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    angleParams.put(BLDKEY_C, new BuilderParam(BLDKEY_C, "Второй край дуги", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    ArcBuilder builder = new ArcBuilder(angleParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Дуга</strong><br>по трем точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
