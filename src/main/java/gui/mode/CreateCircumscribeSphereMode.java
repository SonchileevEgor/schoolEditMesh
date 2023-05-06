package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.SphereOutConeBuilder;
import builders.SphereOutCubeBuilder;
import builders.SphereOutCylinderBuilder;
import builders.SphereOutDodecahedronBuilder;
import builders.SphereOutIcosahedronBuilder;
import builders.SphereOutOctahedronBuilder;
import builders.SphereOutParallelepipedBuilder;
import builders.SphereOutPrismBuilder;
import builders.SphereOutPyramidBuilder;
import builders.SphereOutTetrahedronBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Circumscribed sphere building script. User chooses single body on scene.
 *
 * @author alexeev
 */
public class CreateCircumscribeSphereMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _bodyID;
  private BodyType _type;

  public CreateCircumscribeSphereMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.getBodiesWithCircumscribedSphere());
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.OUT_SPHERE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.OUT_SPHERE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.getBodiesWithCircumscribedSphere(), 1);
  }

  @Override
  public String description() {
    return "Описать сферу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CIRCUMSCRIBE_SPHERE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> sphereParams = new HashMap<String, BuilderParam>();
    i_BodyBuilder builder = null;
    sphereParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    if (_type == BodyType.CONE) {
      sphereParams.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutConeBuilder(sphereParams);
    } else if (_type == BodyType.CUBE) {
      sphereParams.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutCubeBuilder(sphereParams);
    } else if (_type == BodyType.CYLINDER) {
      sphereParams.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutCylinderBuilder(sphereParams);
    } else if (_type == BodyType.PRISM) {
      sphereParams.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutPrismBuilder(sphereParams);
    } else if (_type == BodyType.PYRAMID) {
      sphereParams.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutPyramidBuilder(sphereParams);
    } else if (_type == BodyType.TETRAHEDRON || _type == BodyType.REG_TETRAHEDRON) {
      sphereParams.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutTetrahedronBuilder(sphereParams);
    } else if (_type == BodyType.OCTAHEDRON) {
      sphereParams.put("oct", new BuilderParam("oct", "Октаэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutOctahedronBuilder(sphereParams);
    } else if (_type == BodyType.ICOSAHEDRON) {
      sphereParams.put("ico", new BuilderParam("ico", "Икосаэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutIcosahedronBuilder(sphereParams);
    } else if (_type == BodyType.DODECAHEDRON) {
      sphereParams.put("dodec", new BuilderParam("dodec", "Додекаэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutDodecahedronBuilder(sphereParams);
    } else if (_type == BodyType.PARALLELEPIPED) {
      sphereParams.put("par", new BuilderParam("par", "Прямоугольный параллелепипед", BuilderParamType.BODY, _bodyID));
      builder = new SphereOutParallelepipedBuilder(sphereParams);
    }
    if (builder != null) {
      _ctrl.add(builder, checker, false);
      exit();
    } else {
      reset();
    }
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
    _bodyID = id;
    try {
      _type = _ctrl.getBody(_bodyID).type();
      if (_type.hasCircumscribedSphere()) {
        createWithNameValidation();
      }
    } catch (ExNoBody ex) {
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите тело{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.SPHERE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
