/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package builders;

import bodies.TetrahedronBody;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_D;
import editor.Editor;
import editor.ExBadRef;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Simplex3d;
import geom.Vect3d;
import gui.EdtController;
import minjson.JsonObject;

/**
 *
 * @author e-son
 */
public class CuboctahedronBuilder extends BodyBuilder {
    static public final String ALIAS = "Cuboctahedron";

    public CuboctahedronBuilder(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public i_Body create(Editor edt, i_ErrorHandler eh) {
        i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BLDKEY_A);
      String bID = getValueAsString(BLDKEY_B);
      String cID = getValueAsString(BLDKEY_C);
      String dID = getValueAsString(BLDKEY_D);

      Vect3d a = anchors.getVect(aID);
      Vect3d b = anchors.getVect(bID);
      Vect3d c = anchors.getVect(cID);
      Vect3d d = anchors.getVect(dID);

      TetrahedronBody result = new TetrahedronBody(_id, title(), new Simplex3d(a, b, c, d));

      result.addAnchor(TetrahedronBody.BODY_KEY_A, aID);
      result.addAnchor(TetrahedronBody.BODY_KEY_B, bID);
      result.addAnchor(TetrahedronBody.BODY_KEY_C, cID);
      result.addAnchor(TetrahedronBody.BODY_KEY_D, dID);

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Кубооктаэдр (Тетраэдр) не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new TetrahedronBody(_id, title());
    }
    }

    @Override
    public JsonObject getJSONParams() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String alias() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String description(EdtController ctrl, int precision) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public final void addA(String id) {
        setValue(BLDKEY_A, id);
    }

    public final void addB(String id) {
        setValue(BLDKEY_B, id);
    }

    public final void addC(String id) {
        setValue(BLDKEY_C, id);
    }

    public final void addD(String id) {
        setValue(BLDKEY_D, id);
    }

}
