package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.HyperboloidOfTwoSheetBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExDegeneration;
import geom.HyperboloidOfTwoSheet3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
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
 * @author Vitaliy
 */
public class CreateHyperboloidOfTwoSheetMode extends CreateBodyBy3PointsMode {

  public CreateHyperboloidOfTwoSheetMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(1).getPoint();
      }
      _ctrl.redraw();
    }
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
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первый фокус гиперболоида{MOUSELEFT}",
            "Отметьте второй фокус гиперболоида{MOUSELEFT}",
            "Отметьте точку на гиперболоиде{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.HYPERBOLOID_OF_TWO_SHEET.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    // draw hyperbole on mouse move in 2D mode
    if (_numOfChosenAnchors == 2 && !is3d) {
      try {
        HyperboloidOfTwoSheet3d hyp = new HyperboloidOfTwoSheet3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawHyperboloidOfTwoSheet(ren, hyp, TypeFigure.CURVE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_HYPERBOLOID_OF_TWO_SHEET;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.HYPERBOLOID_OF_TWO_SHEET.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.HYPERBOLOID_OF_TWO_SHEET.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> hyperboloidParams = new HashMap<String, BuilderParam>();
    hyperboloidParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    hyperboloidParams.put("focus1", new BuilderParam("focus1", "Первый фокус", BuilderParamType.ANCHOR, _anchor.get(0).id()));
    hyperboloidParams.put("focus2", new BuilderParam("focus2", "Второй фокус", BuilderParamType.ANCHOR, _anchor.get(1).id()));
    hyperboloidParams.put("pointOnBound", new BuilderParam("pointOnBound", "Точка, лежащая на гиперболоиде", BuilderParamType.ANCHOR, _anchor.get(2).id()));
    HyperboloidOfTwoSheetBuilder builder = new HyperboloidOfTwoSheetBuilder(hyperboloidParams);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Двуполостный гиперболоид</strong><br>по трем точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

}
