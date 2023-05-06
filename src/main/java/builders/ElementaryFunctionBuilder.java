package builders;

import bodies.ElementaryFunctionBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.ElementaryFunction2d;
import geom.Vect3d;
import gui.EdtController;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import minjson.JsonObject;
import parser.MathParser;

/**
 *
 * @author alena
 */
public class ElementaryFunctionBuilder extends BodyBuilder {
  static public final String ALIAS = "ElementaryFunction";
  
  public ElementaryFunctionBuilder() {
  }

  public ElementaryFunctionBuilder(String id, String name) {
    super(id, name);
  }

  public ElementaryFunctionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ElementaryFunctionBuilder(HashMap<String, BuilderParam> params) throws ParseException  {
    super(params);
    String fx = getValueAsString(BLDKEY_FUNCTION_STRING);
//    Pattern funcionRegExp = Pattern.compile("(?:[0-9-+*\\/^()x]|abs|e\\^x|ln|log|a?(?:sin|cos|tan)h?\\()+");
//    Matcher matcher = funcionRegExp.matcher(fx);
//    boolean isMatch = matcher.matches();
//    if (!isMatch)
//        throw new ParseException("Функция введена неверно", -1);
    Double doubleValue = 0.54326;
    for (int i = 0; i <= 9; i++) {
        fx = fx.replaceAll(Integer.toString(i)+"x", Integer.toString(i)+"*x");
    }
    String newFx = fx.replaceAll("x", doubleValue.toString());
    MathParser parser = new MathParser();
    try{
              parser.Parse(newFx);
            } catch(Exception e){
                throw new ParseException("Функция введена неверно: \n"
                        +e.getMessage(), -1);
            }
  }

  public ElementaryFunctionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_FUNCTION_STRING, param.get(BLDKEY_FUNCTION_STRING).asString());
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_FUNCTION_STRING, "Строка функции", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      String fx = getValueAsString(BLDKEY_FUNCTION_STRING);
      for (int i = 0; i <= 9; i++) {
        fx = fx.replaceAll(Integer.toString(i)+"x", Integer.toString(i)+"*x");
    }
      ElementaryFunction2d elementaryFunction = new ElementaryFunction2d(fx);
      ElementaryFunctionBody result = new ElementaryFunctionBody(_id, title(), elementaryFunction);
      _exists = true;
      return result;
    } catch (ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Функция не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new ElementaryFunctionBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_FUNCTION_STRING, getValueAsString(BLDKEY_FUNCTION_STRING));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Функция</strong> %s ",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_FUNCTION_STRING)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Функция</strong>";
    }
  }
}
