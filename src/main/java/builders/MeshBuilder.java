package builders;

import bodies.MeshBody;
import bodies.RhombicDodecahedronBody;
import bodies.RibBody;
import bodies.TriangleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Mesh3d;
import geom.Rib3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import minjson.JsonObject;
import opengl.LineMark;

/**
 *
 * @author e-son
 */
public class MeshBuilder extends BodyBuilder {
  static public final String ALIAS = "Mesh";

  public MeshBuilder() {
  }
  
  public MeshBuilder(String name) {
    super(name);
  }

  public MeshBuilder(String id, String name) {
    super(id, name);
  }

  public MeshBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public MeshBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
  }

  public MeshBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Многогранник", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_Body bd = null;
      try {
          bd = edt.getBody(getValueAsString(BLDKEY_BODY));
      } catch (ExNoBody ex) {
          Logger.getLogger(MeshBuilder.class.getName()).log(Level.SEVERE, null, ex);
      }

    HashMap<String, String> pointsAnchors = new HashMap<String, String>();

    for(Map.Entry<String, String> entry : bd.getAnchors().entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();

        if (!key.contains("plane") && !key.contains("rib")) {
            pointsAnchors.put(key, value);
        }
    }

    ArrayList<String> pnts = new ArrayList<String>(pointsAnchors.keySet());
    
    ArrayList<RhombicDodecahedronBody> rds = new ArrayList<RhombicDodecahedronBody>();

    for (int i=0; i < bd.getAllFaces(edt).size(); i++) {
        RhombicDodByRhombBuilder builder = new RhombicDodByRhombBuilder("Ромбододекаэдр " + i + 1);
        i_AnchorContainer anchors = edt.anchors();

        for (int j=0; j<pnts.size(); j++) {
            try {
                if (bd.getAllFaces(edt).get(i).points().get(0).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {                        
                    builder.setValue(BLDKEY_A, pointsAnchors.get(pnts.get(j)));
                }
            } catch (ExNoAnchor ex) {
                Logger.getLogger(MeshBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {                        
                if (bd.getAllFaces(edt).get(i).points().get(1).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {
                    builder.setValue(BLDKEY_B, pointsAnchors.get(pnts.get(j)));
                }
            } catch (ExNoAnchor ex) {
                Logger.getLogger(MeshBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {                        
                if (bd.getAllFaces(edt).get(i).points().get(2).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {
                    builder.setValue(BLDKEY_C, pointsAnchors.get(pnts.get(j)));
                }
            } catch (ExNoAnchor ex) {
                Logger.getLogger(MeshBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {                        
                if (bd.getAllFaces(edt).get(i).points().get(3).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {
                    builder.setValue(BLDKEY_D, pointsAnchors.get(pnts.get(j)));
                }
            } catch (ExNoAnchor ex) {
                Logger.getLogger(MeshBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }


        };
        builder.setValue(BLDKEY_NORMAL, (i%2==1) ? "up" : "down");
        if (i == 2) {
            builder.setValue(BLDKEY_NORMAL, "up");
        }
        if (i == 3) {
            builder.setValue(BLDKEY_NORMAL, "down");
        }
        edt.add(builder, null, false);
        rds.add(builder.getResult());
    }
    Mesh3d m3d = new Mesh3d(rds);
    MeshBody result = null;
      try {
          result = new MeshBody(_id, title(), m3d);
      } catch (ExGeom ex) {
          Logger.getLogger(MeshBuilder.class.getName()).log(Level.SEVERE, null, ex);
      }
    return result;
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BODY, getValueAsString(BLDKEY_BODY));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сетка</strong> многогранника %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сетка</strong>";
    }
  }
}
