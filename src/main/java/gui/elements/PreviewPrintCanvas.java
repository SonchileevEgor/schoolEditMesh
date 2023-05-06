package gui.elements;

import editor.Editor;
import gui.BasicEdtCanvas;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import maquettes.i_Cover;
import opengl.scenegl.SceneSTL;
import toxi.geom.mesh.Mesh3D;

/**
 * Canvas for preview of 3D-model.
 *
 * @author alexeev
 */
public class PreviewPrintCanvas extends BasicEdtCanvas {
  private String _stlFileName;
  private Editor _edt;
  private ArrayList<Mesh3D> _meshes = new ArrayList<>();
  private ArrayList<i_Cover> _covers = new ArrayList<>();

  public PreviewPrintCanvas(Editor edt, List<i_Cover> covers) {
      _stlFileName = null;
      _edt = edt;
      for (i_Cover cover : covers)
          _meshes.add(cover.getMesh());
      _covers = (ArrayList<i_Cover>) covers;
      setScene(new SceneSTL(edt, covers));
    }

  public void reload() throws IOException{
      if (_stlFileName != null)
        ((SceneSTL)_scene).loadSTL(_stlFileName);
      else {
        ((SceneSTL)_scene).reload(_covers);
      }
  }
}
