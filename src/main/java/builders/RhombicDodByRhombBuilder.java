package builders;

import bodies.RhombicDodecahedronBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author e-son
 */
public class RhombicDodByRhombBuilder extends BodyBuilder {
  static public final String ALIAS = "RhombicDodecahedron";
  
  public RhombicDodByRhombBuilder() {
  }

  public RhombicDodByRhombBuilder(String name) {
    super(name);
  }

  public RhombicDodByRhombBuilder(String id, String name) {
    super(id, name);
  }

  public RhombicDodByRhombBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RhombicDodByRhombBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_C, param.get(BLDKEY_C).asString());
    setValue(BLDKEY_D, param.get(BLDKEY_D).asString());
  }

  public RhombicDodByRhombBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_C, BuilderParam.VERT_3_ALIAS, BuilderParamType.ANCHOR, 99);
    addParam(BLDKEY_D, BuilderParam.VERT_4_ALIAS, BuilderParamType.ANCHOR, 98);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {

        System.out.println(BLDKEY_A);
        System.out.println(BLDKEY_B);
        System.out.println(BLDKEY_C);
        System.out.println(BLDKEY_D);
      RhombicDodecahedronBody result = new RhombicDodecahedronBody(_id, title(),
              anchors.getVect(getValueAsString(BLDKEY_A)),
              anchors.getVect(getValueAsString(BLDKEY_B)),
              anchors.getVect(getValueAsString(BLDKEY_C)),
              anchors.getVect(getValueAsString(BLDKEY_D)));

      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());
      result.addAnchor("C", anchors.get(getValueAsString(BLDKEY_C)).id());
      result.addAnchor("D", anchors.get(getValueAsString(BLDKEY_D)).id());

//      edt.addAnchor(result.C(), result, "C");
//      edt.addAnchor(result.D(), result, "D");

      edt.addAnchor(result.A1(), result, "A1");
      edt.addAnchor(result.B1(), result, "B1");
      edt.addAnchor(result.C1(), result, "C1");
      edt.addAnchor(result.D1(), result, "D1");

      edt.addAnchor(result.E(), result, "E");
      edt.addAnchor(result.F(), result, "F");
      edt.addAnchor(result.G(), result, "G");
      edt.addAnchor(result.H(), result, "H");
      edt.addAnchor(result.I(), result, "I");
      edt.addAnchor(result.J(), result, "J");

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Ромбододекаэдр не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new RhombicDodecahedronBody(_id, title());
    }
  }

  
//  @Override
//  public i_Body create (Editor edt, i_ErrorHandler eh){
//    i_AnchorContainer anchors = edt.anchors();
//    try {
//
//      RhombicDodecahedronBody result = new RhombicDodecahedronBody(_id, title(),
//              anchors.getVect(getValueAsString(BLDKEY_A)),
//              anchors.getVect(getValueAsString(BLDKEY_B)),
//              anchors.getVect(getValueAsString(BLDKEY_C)));
//
//      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
//      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());
////      result.addAnchor("C", anchors.get(getValueAsString(BLDKEY_C)).id());
//
//      edt.addAnchor(result.C(), result, "C");
//      edt.addAnchor(result.D(), result, "D");
//
//      edt.addAnchor(result.A1(), result, "A1");
//      edt.addAnchor(result.B1(), result, "B1");
//      edt.addAnchor(result.C1(), result, "C1");
//      edt.addAnchor(result.D1(), result, "D1");
//
//      edt.addAnchor(result.E(), result, "E");
//      edt.addAnchor(result.F(), result, "F");
//      edt.addAnchor(result.G(), result, "G");
//      edt.addAnchor(result.H(), result, "H");
//      edt.addAnchor(result.I(), result, "I");
//      edt.addAnchor(result.J(), result, "J");
//
//      result.addRibs(edt);
//      result.addPlanes(edt);
//      
//      
//      RhombicDodecahedronBody result2 = new RhombicDodecahedronBody(_id, title(),
//              result.A1(),
//              result.B1(),
//              result.C1());
//
//      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
//      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());
//      result.addAnchor("C", anchors.get(getValueAsString(BLDKEY_C)).id());
//
//      edt.addAnchor(result2.D(), result2, "D2");
//
//      edt.addAnchor(result2.A1(), result2, "A12");
//      edt.addAnchor(result2.B1(), result2, "B12");
//      edt.addAnchor(result2.C1(), result2, "C12");
//      edt.addAnchor(result2.D1(), result2, "D12");
//
//      edt.addAnchor(result2.E(), result2, "E2");
//      edt.addAnchor(result2.F(), result2, "F2");
//      edt.addAnchor(result2.G(), result2, "G2");
//      edt.addAnchor(result2.H(), result2, "H2");
//      edt.addAnchor(result2.I(), result2, "I2");
//      edt.addAnchor(result2.J(), result2, "J2");
//
//      result2.addRibs(edt);
//      result2.addPlanes(edt);
//      
//      
//      _exists = true;
//      return result;
//    } catch (ExGeom | ExBadRef ex) {
//      if (_exists) {
//        eh.showMessage("Ромбододекаэдр не построен: " + ex.getMessage(), error.Error.WARNING);
//        _exists = false;
//      }
//      return new RhombicDodecahedronBody(_id, title());
//    }
//    i_AnchorContainer anchors = edt.anchors();
//    try {
//
//      RhombicDodecahedronBody result = new RhombicDodecahedronBody(_id, title(),
//              anchors.getVect(getValueAsString(BLDKEY_A)),
//              anchors.getVect(getValueAsString(BLDKEY_B)),
//              anchors.getVect(getValueAsString(BLDKEY_C)));
//
//      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
//      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());
//      result.addAnchor("C", anchors.get(getValueAsString(BLDKEY_C)).id());
//
////      edt.addAnchor(result.C(), result, "C");
//      edt.addAnchor(result.D(), result, "D");
//
//      edt.addAnchor(result.A1(), result, "A1");
//      edt.addAnchor(result.B1(), result, "B1");
//      edt.addAnchor(result.C1(), result, "C1");
//      edt.addAnchor(result.D1(), result, "D1");
//
//      edt.addAnchor(result.E(), result, "E");
//      edt.addAnchor(result.F(), result, "F");
//      edt.addAnchor(result.G(), result, "G");
//      edt.addAnchor(result.H(), result, "H");
//      edt.addAnchor(result.I(), result, "I");
//      edt.addAnchor(result.J(), result, "J");
//
//      result.addRibs(edt);
//      result.addPlanes(edt);
//      
//      
//      RhombicDodecahedronBody result2 = new RhombicDodecahedronBody(_id, title(),
//              result.A1(),
//              result.B1(),
//              result.C1());
//
//      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
//      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());
//      result.addAnchor("C", anchors.get(getValueAsString(BLDKEY_C)).id());
//
//      edt.addAnchor(result2.D(), result2, "D2");
//
//      edt.addAnchor(result2.A1(), result2, "A12");
//      edt.addAnchor(result2.B1(), result2, "B12");
//      edt.addAnchor(result2.C1(), result2, "C12");
//      edt.addAnchor(result2.D1(), result2, "D12");
//
//      edt.addAnchor(result2.E(), result2, "E2");
//      edt.addAnchor(result2.F(), result2, "F2");
//      edt.addAnchor(result2.G(), result2, "G2");
//      edt.addAnchor(result2.H(), result2, "H2");
//      edt.addAnchor(result2.I(), result2, "I2");
//      edt.addAnchor(result2.J(), result2, "J2");
//
//      result2.addRibs(edt);
//      result2.addPlanes(edt);
//      
//      
//      _exists = true;
//      return result;
//    } catch (ExGeom | ExBadRef ex) {
//      if (_exists) {
//        eh.showMessage("Ромбододекаэдр не построен: " + ex.getMessage(), error.Error.WARNING);
//        _exists = false;
//      }
//      return new RhombicDodecahedronBody(_id, title());
//    }
//  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject() {
    };
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_D, getValueAsString(BLDKEY_D));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Ромбододекаэдр</strong><br> со стороной основания %s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Ромбододекаэдр</strong>";
    }
  }
}

