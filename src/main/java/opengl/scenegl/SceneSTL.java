package opengl.scenegl;

import editor.Editor;
import editor.i_Anchor;
import opengl.Drawer;
import geom.Vect3d;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Mesh3D;

import javax.media.opengl.GL2;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GLAutoDrawable;
import maquettes.ArcCover;
import maquettes.CoverContainer;
import maquettes.ImportCover;
import maquettes.PLYReader;
import maquettes.i_Cover;
import opengl.colorgl.ColorGL;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;
import org.apache.commons.io.FilenameUtils;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.WETriangleMesh;

/**
 * Created by Maks on 2/13/2015.
 * @author Vladimir
 */
public class SceneSTL extends SceneGL {
  PLYReader _plyReader;
  STLReader _stlReader;
  Editor _edt;
  public ArrayList<WETriangleMesh> _meshes = new ArrayList<>();

  public SceneSTL(Editor edt) {
      super(edt);
      _edt = edt;
  }
  
  public SceneSTL(Editor edt, CameraPosition cam, String stlFileName) throws IOException{
    super(edt, cam);
    _edt = edt;
    loadSTL(stlFileName);
    for (WETriangleMesh mesh : _meshes) {
        i_Cover cover = new ImportCover(mesh);
        edt.addCover(cover);
    }
  }

  public SceneSTL(Editor edt, String stlFileName) throws IOException{
    super(edt);
    _edt = edt;
    loadSTL(stlFileName);
    for (WETriangleMesh mesh : _meshes) {
        i_Cover cover = new ImportCover(mesh);
        edt.addCover(cover);
    }
  }
  
  public SceneSTL(Editor edt, List<i_Cover> covers) {
      super(edt);
      _edt = edt;
      _plyReader = null;
      _stlReader = null;
      for (i_Cover cover : covers){
          Mesh3D mesh = cover.getMesh();
          //mesh.faceOutwards();
          _meshes.add((WETriangleMesh) mesh);
          edt.addCover(cover);
      }
  }
  
  public SceneSTL(Editor edt, ArrayList<Mesh3D> meshes) {
      super(edt);
      _edt = edt;
      _plyReader = null;
      _stlReader = null;
      for (Mesh3D mesh : meshes) {
          mesh.faceOutwards();
          _meshes.add((WETriangleMesh) mesh);
          i_Cover cover = new ImportCover((WETriangleMesh) mesh);
          edt.addCover(cover);
      }
  }

  public final void loadSTL(String stlFileName) throws FileNotFoundException, IOException{
    if (FilenameUtils.getExtension(stlFileName).equals("ply")) { 
        _plyReader = new PLYReader(stlFileName);
        _meshes.add(_plyReader.loadPLY());
    } else if (FilenameUtils.getExtension(stlFileName).equals("stl")) {
        _stlReader = new STLReader();
        _meshes.add((WETriangleMesh) _stlReader.loadBinary(stlFileName, WETriangleMesh.class));
    }
    _meshes.get(_meshes.size() - 1).faceOutwards();
  }

  @Override
  public void drawObjects() {
    GL2 gl = render.getGL();
    render.getGL().glEnable(GL2.GL_LIGHTING);
    render.getGL().glEnable(GL2.GL_DEPTH_TEST);
    
    Vect3d a = new Vect3d();
    Vect3d b = new Vect3d();
    Vect3d c = new Vect3d();
    
    gl.glBegin(GL2.GL_TRIANGLES);
    for(WETriangleMesh mesh : _meshes)
        for (Face face : mesh.getFaces()) {
          a.set(face.a.x, face.a.y, face.a.z);
          b.set(face.b.x, face.b.y, face.b.z);
          c.set(face.c.x, face.c.y, face.c.z);

          double [] normal = new double[3];
          normal[0] = (double)face.normal.x();
          normal[1] = (double)face.normal.y();
          normal[2] = (double)face.normal.z();

          gl.glNormal3dv(normal, 0);
          
          if (_plyReader != null) {
                int red = (_plyReader._vertexColor[face.a.id][0] 
                    + _plyReader._vertexColor[face.b.id][0]
                    + _plyReader._vertexColor[face.c.id][0]) / 3,
                green = (_plyReader._vertexColor[face.a.id][1] 
                    + _plyReader._vertexColor[face.b.id][1]
                    + _plyReader._vertexColor[face.c.id][1]) / 3,
                blue = (_plyReader._vertexColor[face.a.id][2] 
                    + _plyReader._vertexColor[face.b.id][2]
                    + _plyReader._vertexColor[face.c.id][2]) / 3; 

            Drawer.setObjectColor(render, new ColorGL(red / 255.0, green / 255.0, blue / 255.0));
          } else {
              CoverContainer covers = _edt.covers();
              ColorGL color = null;
              for (i_Cover cover : covers.getCovers())
                  if (cover.getMesh().equals(mesh)){ 
                        color = cover.getChosen()? 
                                cover.getCover().getPointCover().get(0).getColor().emphasize() 
                                : cover.getCover().getPointCover().get(0).getColor();
                        break;
                  } else color = new ColorGL(Color.LIGHT_GRAY);
              Drawer.setObjectColor(render, color);
          }

          gl.glVertex3d(face.a.x, face.a.y, face.a.z);
          gl.glVertex3d(face.b.x, face.b.y, face.b.z);
          gl.glVertex3d(face.c.x, face.c.y, face.c.z);
        }
    gl.glEnd();
  }

  @Override
  public boolean is3d() {
    return true;
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    super.init(drawable);
    GL2 gl = render.getGL();

    // Background light
    float lmodel_ambient[] = {1.0f, 1.0f, 1.0f, 1f};
    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
    gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);

    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
  }

  @Override
  protected void initPerspectiveParameters() {
    GluPerspectiveParameters perPar = render.getGluPerspectiveParameters();
    CameraPosition cam = getCameraPosition();
    double fitCamDist = perPar.getFitCamDist(this.render.getWidth(), this.render.getHeight());
    perPar.setzFar(fitCamDist * cam.distance());
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    super.display(drawable);
    GL2 gl = render.getGL();
    CameraPosition cam = getCameraPosition();

    render.getViewVolume().setСlippingPlanes(render);

    // Light source (illuminant)
    Vect3d eye = cam.eye();
    float light_position[] = {(float)eye.x(), (float)eye.y(), (float)eye.z(), 1.0f};
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

    drawObjects();
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    drawAxesAndInitialGrid();
    drawViewVolumeCube();

    Drawer.saveTransformMatrix(render);
    render.getViewVolume().disableClippingPlanes(render);
  }

  public void addMesh(Mesh3D mesh) {
      mesh.faceOutwards();
      _meshes.add((WETriangleMesh) mesh);
  }
  
    @Override
    public SceneType getSceneType() {
        return SceneType.SceneSTL;
    }
}