package builders;

import bodies.CircleBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Tangent line from point to circle.
 *
 * @author alexeev
 */
public class TangentFromPointToCircleBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentFromPointToCircle";

  public TangentFromPointToCircleBuilder() {
  }
  
  public TangentFromPointToCircleBuilder(String name) {
    super(name);
  }
  
  public TangentFromPointToCircleBuilder(String id, String name) {
    super(id, name);
  }

  public TangentFromPointToCircleBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentFromPointToCircleBuilder (String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
    setValue(BLDKEY_DIRECTION, param.get(BLDKEY_DIRECTION).asBoolean());
  }

  public TangentFromPointToCircleBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CIRCLE, BuilderParam.CIRCLE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      CircleBody circ = (CircleBody)bodies.get(getValueAsString(BLDKEY_CIRCLE));
      if (!circ.exists())
        throw new ExLostBody("окружность");

      boolean dir = (boolean)getValue(BLDKEY_DIRECTION);
      String vertex = getValueAsString(BLDKEY_POINT);
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));

      if (dir) {//чтобы два раза не появлялись окна с ошибками
        if (!Checker.isPlaneContainPoint(circ.circle().plane(), point)) {
          throw new ExDegeneration("Точка должна лежать в плоскости окружности!");
        }
        if (Checker.isPointOnCircle(point, circ.circle())) {
          throw new ExDegeneration("Точка не может принадлежать окружности!");
        }
         if (Checker.isPointInCircle(point, circ.circle())) {
          throw new ExDegeneration("Точка не может лежать внутри окружности!");
        }
      }

      ArrayList<Vect3d> tangPoints = circ.circle().tangentPoints(point);
      Vect3d tangPoint = null;
      if (tangPoints.size() == 2 && dir == true /* выбрана вторая касательная */) {
        tangPoint = tangPoints.get(1);
      } else if (!tangPoints.isEmpty()){
        tangPoint = tangPoints.get(0);
      } else {
        throw new ExDegeneration("отсутствуют касательные");
      }
      RibBody result = new RibBody(_id, new Rib3d(point, tangPoint));
      result.addAnchor("A", anchors.get(vertex).id());
      edt.addAnchor(tangPoint, result, "B", title());
      result.addRibs(edt);
      //edt.anchors().get(result.getAnchorID(BLDKEY_RIB)).getState().setExtendEnd(true);
      _exists = true;
      return result;
    } catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists)
				eh.showMessage("Касательная не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    result.add(BLDKEY_DIRECTION, getValueAsBoolean(BLDKEY_DIRECTION));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Касательная</strong> из точки %s к окружности %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_CIRCLE)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
