package geom;

import static config.Config.LOG_LEVEL;
import java.util.ArrayList;
import java.util.Iterator;
import util.Log;
import parser.MathParser;

/**
 * Elementary function
 * @author alena
 */

public class ElementaryFunction2d implements i_Geom {
    
    private String _fxValue;
    
    public String pointValue() {
        return _fxValue;
    }

  /**
   * Constructor of elementaryFunction
   */
    public ElementaryFunction2d(String  pointValue) throws ExDegeneration {
     _fxValue =  pointValue;;
  }
  
  /**
   * return point of the function
   * @param param параметр по которому строится точка [-infinity, +infinity]
   * @return точку функции
   */
  public Vect3d getPoint(double param) {
    double elementaryFunctionValue = 0;
    Double dparam = param;
    String newFx = _fxValue.replaceAll("x", dparam.toString());
    MathParser parser = new MathParser();
    try{
               elementaryFunctionValue = parser.Parse(newFx);
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
    Vect3d p = new Vect3d(param, elementaryFunctionValue, 0);
    return p;
  }
  
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    /// TODO check не восстанавливается по точкам
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    /// TODO check не восстанавливается по точкам
    return null;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }
  
  @Override
  public GeomType type() {
    return GeomType.ELEMENTARYFUNCTION2D;
  }
}
