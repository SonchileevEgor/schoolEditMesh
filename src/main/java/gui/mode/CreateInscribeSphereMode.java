package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.SphereInConeBuilder;
import builders.SphereInCubeBuilder;
import builders.SphereInCylinderBuilder;
import builders.SphereInDodecahedronBuilder;
import builders.SphereInIcosahedronBuilder;
import builders.SphereInOctahedronBuilder;
import builders.SphereInParallelepipedBuilder;
import builders.SphereInPrismBuilder;
import builders.SphereInPyramidBuilder;
import builders.SphereInTetrahedronBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * @author alexeev
 */
public class CreateInscribeSphereMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _bodyID;
  private BodyType _type;

  public CreateInscribeSphereMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    canvas().getHighlightAdapter().setTypes(BodyType.getBodiesWithInscribedSphere());
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
    return IconList.IN_SPHERE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.IN_SPHERE.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.getBodiesWithInscribedSphere(), 1);
  }

  @Override
  public String description() {
    return "Вписать сферу";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_INSCRIBE_SPHERE;
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> sphereParams = new HashMap<String, BuilderParam>();
    i_BodyBuilder builder = null;
    sphereParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    if (_type == BodyType.CONE) {
      sphereParams.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, _bodyID));
      builder = new SphereInConeBuilder(sphereParams);
    } else if (_type == BodyType.CUBE) {
      sphereParams.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, _bodyID));
      builder = new SphereInCubeBuilder(sphereParams);
    } else if (_type == BodyType.CYLINDER) {
      sphereParams.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, _bodyID));
      builder = new SphereInCylinderBuilder(sphereParams);
    } else if (_type == BodyType.TETRAHEDRON || _type == BodyType.REG_TETRAHEDRON) {
      sphereParams.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereInTetrahedronBuilder(sphereParams);
    } else if (_type == BodyType.PYRAMID) {
      sphereParams.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, _bodyID));
      builder = new SphereInPyramidBuilder(sphereParams);
    } else if (_type == BodyType.PRISM) {
      sphereParams.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, _bodyID));
      builder = new SphereInPrismBuilder(sphereParams);
    } else if (_type == BodyType.OCTAHEDRON) {
      sphereParams.put("oct", new BuilderParam("oct", "Октаэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereInOctahedronBuilder(sphereParams);
    } else if (_type == BodyType.ICOSAHEDRON) {
      sphereParams.put("ico", new BuilderParam("ico", "Икосаэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereInIcosahedronBuilder(sphereParams);
    } else if (_type == BodyType.DODECAHEDRON) {
      sphereParams.put("dodec", new BuilderParam("dodec", "Додекаэдр", BuilderParamType.BODY, _bodyID));
      builder = new SphereInDodecahedronBuilder(sphereParams);
    } else if (_type == BodyType.PARALLELEPIPED) {
      sphereParams.put("par", new BuilderParam("par", "Прямоугольный параллелепипед", BuilderParamType.BODY, _bodyID));
      builder = new SphereInParallelepipedBuilder(sphereParams);
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
      if (_type.hasInscribedSphere()) {
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
