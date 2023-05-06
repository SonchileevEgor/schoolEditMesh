package gui.mode;

import bodies.BodyType;
import bodies.RibBody;
import builders.BodyBuilder;
import builders.LineXConeBuilder;
import builders.LineXCylinderBuilder;
import builders.LineXEllipsoidBuilder;
import builders.LineXLineBuilder;
import builders.LineXPlaneBuilder;
import builders.LineXRibBuilder;
import builders.LineXSphereBuilder;
import builders.param.BuilderParam;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import geom.Circle3d;
import geom.Cone3d;
import geom.Cylinder3d;
import geom.Ellipsoid3d;
import geom.ExGeom;
import geom.Line3d;
import geom.Sphere3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Render;

/**
 * Сечение объекта прямой.
 *
 * @author Elena
 */
public class CreateLineSectionMode extends CreateBodyMode implements i_FocusChangeListener {

  private BodyType _type; // type of object
  private i_Body _body;
  private String _bodyID, _lineID;
  private Line3d _line;

  public CreateLineSectionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.LINE);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
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
    if (_numOfChosenAnchors == 0) {
      try {
        i_Body bd = _ctrl.getBody(id);
        _lineID = id;
        if (checker.checkBodyTypeIsLine(bd.type())) {
          _line = (Line3d)bd.getGeom();
          _numOfChosenAnchors++;
          canvas().getHighlightAdapter().setTypes(BodyType.getTypesForLineSection());
          _ctrl.status().showMessage(_msg.current());
          _ctrl.redraw();
        }
      } catch (ExNoBody ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (!bd.type().isLineIntersectable()) {
          return;
        }
        _type = bd.type();
        _body = bd;
        _bodyID = id;
        _numOfChosenAnchors++;
        create();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите секущую прямую{MOUSELEFT}",
            "Выберите тело для пересечения{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_LINE_SECTION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.LINE_SECTION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.LINE_SECTION.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();

    i_BodyBuilder builder;
    try {
      ArrayList<Vect3d> intersectPoints;
      switch (_type) {
        case CYLINDER: {
          Cylinder3d cyl = (Cylinder3d)_body.getGeom();
          intersectPoints = cyl.intersect(_line);
          builder = new LineXCylinderBuilder();
          if (intersectPoints.size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_CYLINDER, _bodyID);
          builder.setValue(BodyBuilder.BLDKEY_LINE, _lineID);
          break;
        }
        case PLANE: {
          builder = new LineXPlaneBuilder(BodyType.POINT.getName(_ctrl.getEditor()));
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _bodyID);
          builder.setValue(BodyBuilder.BLDKEY_LINE, _lineID);
          break;
        }
        case CONE: {
          Cone3d cone = (Cone3d)_body.getGeom();
          intersectPoints = cone.intersect(_line);
          builder = new LineXConeBuilder();
          if (intersectPoints.size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_CONE, _bodyID);
          builder.setValue(BodyBuilder.BLDKEY_LINE, _lineID);
          break;
        }
        case LINE: {
          LineXLineBuilder bb = new LineXLineBuilder(BodyType.POINT.getName(_ctrl.getEditor()));
          bb.addLine1(_bodyID);
          bb.addLine2(_lineID);
          builder = bb;
          break;
        }
        case RIB: {
          LineXRibBuilder bb = new LineXRibBuilder(BodyType.POINT.getName(_ctrl.getEditor()));
          bb.addLine(_lineID);
          bb.addRib(_body.getAnchorID(RibBody.BODY_KEY_RIB));
          builder = bb;
          break;
        }
        case SPHERE: {
          Sphere3d sph = (Sphere3d)_body.getGeom();
          intersectPoints = sph.intersect(_line);
          builder = new LineXSphereBuilder();
          if (intersectPoints.size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_SPHERE, _bodyID);
          builder.setValue(BodyBuilder.BLDKEY_LINE, _lineID);
          break;
        }
        case ELLIPSOID: {
          Ellipsoid3d el = (Ellipsoid3d)_body.getGeom();
          intersectPoints = el.intersect(_line);
          builder = new LineXEllipsoidBuilder();
          if (intersectPoints.size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_ELLIPSOID, _bodyID);
          builder.setValue(BodyBuilder.BLDKEY_LINE, _lineID);
          break;
        }
        case CIRCLE: {
          Circle3d circ = (Circle3d)_body.getGeom();
          intersectPoints = circ.intersect(_line);
          builder = new LineXEllipsoidBuilder();
          if (intersectPoints.size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_CIRCLE, _bodyID);
          builder.setValue(BodyBuilder.BLDKEY_LINE, _lineID);
          break;
        }
        default:
          return;
      }
      _ctrl.add(builder, checker, false);
    } catch (ExGeom ex) {
    }
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Сечение</strong><br>объекта прямой";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.LINE, 1);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
