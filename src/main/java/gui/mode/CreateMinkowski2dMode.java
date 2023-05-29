package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import builders.MinkowskiSumBuilder;
import editor.ExNoBody;
import geom.ExDegeneration;
import geom.Minkowski2d;
import geom.ColoredVector3d;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeArrow;

/**
 *
 * @author e-son
 */
public class CreateMinkowski2dMode extends CreateBodyMode {

  public CreateMinkowski2dMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Векторами отмечены стороны многогранников");
  }

  @Override
  protected void setName() {
    _name = BodyType.POLYGON.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 3) {
        MinkowskiSumBuilder builder = new MinkowskiSumBuilder("ТЕСТ123");
        builder.setValue(BLDKEY_A, _ctrl.getFocusCtrl().getFocusedBodies().get(0));
        builder.setValue(BLDKEY_B, _ctrl.getFocusCtrl().getFocusedBodies().get(1));

        _ctrl.add(builder, null, false);    
    }
    _numOfChosenAnchors++;
  }

  @Override
  protected void create() {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_MINKOWSKI_SUM_2D;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.REGULAR_POLYGON.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.REGULAR_POLYGON.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Сумма Минковского</strong><br>двух многоугольников";
  }

  @Override
  protected void nativeDraw0(Render ren) {
    Minkowski2d sumRes = null;
    try {
        sumRes = new Minkowski2d(
                _ctrl.getBody(_ctrl.getFocusCtrl().getFocusedBodies().get(0)), 
                _ctrl.getBody(_ctrl.getFocusCtrl().getFocusedBodies().get(1)),
                _ctrl.getEditor().getBodySurfaceColor(_ctrl.getFocusCtrl().getFocusedBodies().get(0)),
                _ctrl.getEditor().getBodySurfaceColor(_ctrl.getFocusCtrl().getFocusedBodies().get(1)));
    }catch (ExNoBody ex) {
        Logger.getLogger(CreateMinkowski2dMode.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ExDegeneration ex) {
      Logger.getLogger(CreateMinkowski2dMode.class.getName()).log(Level.SEVERE, null, ex);
    }

    Drawer.setLineWidth(ren, 2.0);
    ArrayList<ColoredVector3d> vects = sumRes.getVectors();
    for (int i = 0; i < vects.size(); i++) {
        Drawer.setObjectColor(ren, vects.get(i).getColor());
        Drawer.draw2DArrow(ren, vects.get(i).start, vects.get(i).end, TypeArrow.ONE_END);
    }
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  protected boolean isEnabled() {
      return true;
  }
}