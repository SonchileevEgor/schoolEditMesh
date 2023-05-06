package gui.dialog;

import builders.AllBuildersManager;
import builders.param.BuilderParamType;
import editor.ExBadRef;
import editor.i_BodyBuilder;
import geom.Vect3d;
import gui.EdtController;
import gui.ui.EdtInputDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Диалог создания тела.
 * В текстовое поле вводится строка, по которой вызывается соответствующий билдер.
 * 
 * @author alexeev
 */
public class ConstructBodyDialog extends EdtInputDialog {
  EdtController _ctrl;
  JTextField _inputField = new JTextField(100);
  
  public ConstructBodyDialog(EdtController ctrl) {
    super(ctrl.getFrame(), "Создать тело");
    _ctrl = ctrl;
    
    _inputField.setHorizontalAlignment(JTextField.LEFT);
    _inputField.setLayout(new MigLayout(new LC().fillX()));
    
    addItem(_inputField, "center");
    addButtons();
    
    pack();
    setCenterLocation();
  }
  
  @Override
  protected ActionListener OKAction() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        String text = _inputField.getText();
        System.out.println(text);
        Pattern pat = Pattern.compile("([a-zA-Z0-9]*)\\[(.*)\\]");
        System.out.println(pat.pattern());
        try {
          Matcher match = pat.matcher(text);
          if( match.matches() ){
            String alias = match.group(1);
            String paramsString = match.group(2);
            ArrayList<String> params = parseArgs(paramsString);
            i_BodyBuilder builder = AllBuildersManager.create(alias);
            ArrayList<String> keys = builder.keys();
            if( keys.size() != params.size() ){
              throw new ParseException(
                      String.format("Неверное количество параметров билдера!"
                              + "Ожидалось %d, введено %d", keys.size(), params.size()), -1);
            }
            for( int paramNum = 0; paramNum < keys.size(); paramNum++ ){
              String key = keys.get(paramNum);
              String paramString = params.get(paramNum);
              BuilderParamType paramType = builder.getParamType(key);
              Object paramValue = null;
              // parse param value
              switch( paramType ) {
                case NAME:
                  paramValue = paramString;
                  break;
                case ANCHOR:
                  paramValue = _ctrl.getAnchorByTitle(paramString);
                  break;
                case ANCHOR_LIST:
                  //!! TODO: распарсить ANCHOR_LIST.
                  break;
                case POINT:
                case VECT:
                  paramValue = Vect3d.fromString(paramString);
                  break;
                case BODY:
                  paramValue = _ctrl.getBodyByTitle(paramString);
                  break;
                case INT:
                  paramValue = Integer.parseInt(paramString);
                  break;
                case DOUBLE:
                case DOUBLE_POSITIVE:
                case ANGLE_VALUE:
                case ANGLE_ROTATION:
                  paramValue = Double.parseDouble(paramString);
                  break;
                case BOOLEAN:
                  paramValue = Boolean.parseBoolean(paramString);
                  break;
              }
              builder.setValue(key, paramValue);
            }
            _ctrl.add(builder, _ctrl.error().getStatusStripHandler(), false);
            _ctrl.redraw();
          }
        } catch( NumberFormatException | ParseException | ExBadRef ex ){
          ex.printStackTrace();
          _ctrl.error().getStatusStripHandler().showMessage(ex.getMessage(), error.Error.WARNING);
        }
//        accepted = true;
//        dispose();
      }
    };
  }
  
  /**
   * Выделить список аргументов из выражения.
   * Рассматриваются только скобки первого уровня.
   * Например, выражение Plane[[1, 2, 3], [-1, 0, 0]] содержит два аргумента:
   * [1, 2, 3] и [-1, 0, 0].
   * @param expr
   * @return 
   * @throws ParseException
   */
  private ArrayList<String> parseArgs(String expr) throws ParseException {
//    int startIdx = expr.indexOf('[');
//    int endIdx = expr.lastIndexOf(']');
//    if( startIdx == -1 || endIdx == -1 || startIdx > endIdx )
//      throw new ParseException("Cannot parse an expression, check the parentheses order.", startIdx);
//    String argString = expr.substring(startIdx + 1, endIdx);
    ArrayList<String> args = new ArrayList<>();
    int parenthesesCounter = 0;
    String currParam = "";
    for( int i = 0; i < expr.length(); i++ ){
      Character currChar = expr.charAt(i);
      if( (parenthesesCounter == 0) && (currChar == ';') ){
        args.add(currParam.trim());
        currParam = "";
      } else {
        switch( currChar ){
          case '[':
            if( parenthesesCounter != 0 ){
              currParam += currChar;
            }
            parenthesesCounter++;
            break;
          case ']':
            if( parenthesesCounter != 1 ){
              currParam += currChar;
            }
            parenthesesCounter--;
            break;
          default:
            currParam += currChar; // не добавляем скобки первого уровня.
        }
      }
    }
    if( parenthesesCounter != 0 ){
      throw new ParseException("Cannot parse an expression, check the parentheses.", -1);
    } else {
      args.add(currParam.trim());
    }
    return args;
  }
}
