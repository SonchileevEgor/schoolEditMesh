package gui.elements;

import editor.Editor;
import gui.BasicEdtCanvas;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import maquettes.i_Cover;
import opengl.scenegl.SceneSTL;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 * Canvas for preview of 3D-model.
 *
 * @author alexeev
 */
public class PreviewPrintCanvas extends BasicEdtCanvas {
  private String _stlFileName;
  private Editor _edt;
  private ArrayList<Mesh3D> _meshes = new ArrayList<>();

  public PreviewPrintCanvas(Editor edt, ArrayList<Mesh3D> meshes) {
      _stlFileName = null;
      _edt = edt;
      _meshes.addAll(meshes);
      setScene(new SceneSTL(edt, _meshes));
  }
  public PreviewPrintCanvas(Editor edt, String stlFileName) throws IOException {
    super();
    _edt = edt;
    loadSTL(stlFileName);
  }

    public PreviewPrintCanvas(Editor edt, List<i_Cover> covers) {
        _stlFileName = null;
      _edt = edt;
      for (i_Cover cover : covers)
          _meshes.add(cover.getMesh());
      setScene(new SceneSTL(edt, covers));
    }

  private final void loadSTL(String stlFileName) throws IOException{
    _stlFileName = stlFileName;
    setScene(new SceneSTL(_edt, stlFileName));
  }

  public void reload() throws IOException{
      if (_stlFileName != null)
        ((SceneSTL)_scene).loadSTL(_stlFileName);
      else {
          ((SceneSTL)_scene)._meshes.clear();
          for (Mesh3D _mesh : _meshes)
            ((SceneSTL)_scene)._meshes.add((WETriangleMesh)_mesh);
      }
  }
}