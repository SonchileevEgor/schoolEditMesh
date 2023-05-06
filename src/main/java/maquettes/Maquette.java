package maquettes;

import geom.ExDegeneration;
import gui.EdtController;

import java.io.IOException;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.OBJWriter;
import toxi.geom.mesh.STLWriter;
import toxi.geom.mesh.WETriangleMesh;

/**
 * Class, which realize the main process of this package:
 * generating cover and printing results in special files.
 *
 * @author Leonid Ivanovsky
 */
public class Maquette {

    private Carcasse _carcasse;
    private String _nameMaquetteFile;

    public Maquette(EdtController ec, String name_maquette_file) {
        _nameMaquetteFile = name_maquette_file;
        _carcasse = new Carcasse(ec);
    }

    /**
     * Make files of 3d-model (.ply)
     */
    private void makeMaquetteFiles(MeshFileType type) throws IOException{
        if (null != type) switch (type) {
            case PLY:{
                PLYWriter out = new PLYWriter();
                Cover cover = new Cover();
                for (int k = 0; k < _carcasse.getCovers().size(); k++)
                    cover.addCover(_carcasse.getCovers().get(k).getCover());
                out.saveMesh(cover, _nameMaquetteFile);
                break;
                }
            case STL:{
                STLWriter out = new STLWriter();
                Cover cover = new Cover();
                for (int k = 0; k < _carcasse.getCovers().size(); k++)
                    cover.addCover(_carcasse.getCovers().get(k).getCover());
                out.beginSave(_nameMaquetteFile, cover.getTriangCount());
                WETriangleMesh mesh = new WETriangleMesh();
                for (int k = 0; k < _carcasse.getMeshes().size(); k++)
                    mesh.addMesh(_carcasse.getMeshes().get(k));
                for (Face f : mesh.getFaces())
                    out.face(f.a, f.b, f.c, f.normal, 0);
                out.endSave();
                    break;
                }
            case OBJ:{
                // TODO: write currect obj-saving
                OBJWriter out = new OBJWriter();
                out.beginSave(_nameMaquetteFile);
                out.endSave();
                    break;
                }
            default:
                break;
        }
    }

    /**
     * Build maquette of 3d-model
     * @param type
     * @throws IOException exception with makeMaquetteFiles
     * @throws geom.ExDegeneration with _carcasse.getMeshes()
     */
    public void buildMaquette(MeshFileType type) throws IOException, ExDegeneration {
        _carcasse.sculptModels();
        makeMaquetteFiles(type);
    }

    /**
     * Return carcasse
     * @return carcasse
     */
    public Carcasse getCarcasse() {
        return _carcasse;
    }
  /**
   * Set filename for model output.
   * @param fname
   */
  public void setFilename(String fname) {
    _nameMaquetteFile = fname;
  }
}
