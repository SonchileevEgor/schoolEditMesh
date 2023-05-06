package opengl.scenegl;

import com.jogamp.common.nio.Buffers;
import editor.Editor;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import maquettes.ColorPoint;
import opengl.Drawer;
import geom.Vect3d;
import java.io.IOException;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Mesh3D;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GLAutoDrawable;
import maquettes.ImportCover;
import maquettes.PLYReader;
import maquettes.i_Cover;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;
import org.apache.commons.io.FilenameUtils;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.WETriangleMesh;

import static com.jogamp.opengl.GL.GL_FLOAT;

/**
 * Класс для отображения 3D-моделей.
 * @author Kurgansky
 */
public class SceneSTL extends SceneGL {
  PLYReader _plyReader;
  STLReader _stlReader;
  Editor _edt;

  //список мешей соотсвествует по индексам списку VBOMesh
  //получаем пары вида WETriangleMesh-VBOMesh
  ArrayList<WETriangleMesh> _meshes = new ArrayList<>();
  ArrayList<VBOMesh> _vbo = new ArrayList<>();

  public SceneSTL(Editor edt) {
      super(edt);
      _edt = edt;
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
        edt.addCover(cover);
      }
      addCovers(covers);
  }

  /**
   * Метод для загрузки на сцену 3D-модели в формате stl / ply
   * @param stlFileName - путь к файлу с моделью
   */
  public final void loadSTL(String stlFileName) throws IOException{
    if (FilenameUtils.getExtension(stlFileName).equals("ply")) { 
        _plyReader = new PLYReader(stlFileName);
        _meshes.add(_plyReader.loadPLY());
        _vbo.add(new VBOMesh());
    } else if (FilenameUtils.getExtension(stlFileName).equals("stl")) {
        _stlReader = new STLReader();
        _meshes.add((WETriangleMesh) _stlReader.loadBinary(stlFileName, WETriangleMesh.class)
            .faceOutwards());
        _vbo.add(new VBOMesh());
    }
  }

  @Override
  public void drawObjects() {
    GL2 gl = render.getGL();
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);

    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

    for (VBOMesh vbo : _vbo) {
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.VBOColors);
      gl.glColorPointer(4, GL_FLOAT, 0, 0);

      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.VBONormals);
      gl.glNormalPointer(GL_FLOAT, 0, 0);

      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.VBOVertices);
      gl.glVertexPointer(3, GL_FLOAT, 0, 0);

      gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vbo.VBOIndices);
      gl.glDrawElements(GL2.GL_TRIANGLES, vbo.indices.capacity(), GL2.GL_UNSIGNED_INT, 0);
    }

    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
    gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
    gl.glDisable(GL2.GL_COLOR_MATERIAL);
    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
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
    gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_TRUE);

    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);

    for(WETriangleMesh mesh : _meshes) {
      VBOMesh vbo = _vbo.get(_meshes.indexOf(mesh));

      float[] vertices = mesh.getUniqueVerticesAsArray();

      float[] normals = mesh.getNormalsForUniqueVerticesAsArray();

      int[] index = mesh.getFacesAsArray();

      //получаем цвет сетки (если он есть)
      try {
        if (_plyReader != null) {
          vbo._colors = new float[_plyReader.vertexColor.length * 4];
          int k = 0, j = 0;
          while (k < _plyReader.vertexColor.length) {
            vbo._colors[j++] = (float) (_plyReader.vertexColor[k][0] / 255.0);
            vbo._colors[j++] = (float) (_plyReader.vertexColor[k][1] / 255.0);
            vbo._colors[j++] = (float) (_plyReader.vertexColor[k][2] / 255.0);
            vbo._colors[j++] = 1.0f;
            k++;
          }
        } else {
          vbo._colors = new float[vertices.length * 4];
          int k = 0, j = 0;
          while (k < vertices.length) {
            vbo._colors[j++] = 0.5f;
            vbo._colors[j++] = 0.5f;
            vbo._colors[j++] = 0.5f;
            vbo._colors[j++] = 1.0f;
            k++;
          }
        }

        vbo._isColor = true;
      } catch (Exception ex) {
        System.out.println("Ошибка при получении цвета сетки: " + ex.getMessage());
      }

      //инициализируем спец.буфферы для OpenGL
      vbo.vert = Buffers.newDirectFloatBuffer(vertices.length);
      vbo.vert.put(vertices);
      vbo.vert.flip();

      vbo.norm = Buffers.newDirectFloatBuffer(normals.length);
      vbo.norm.put(normals);
      vbo.norm.flip();

      vbo.color = Buffers.newDirectFloatBuffer(vbo._colors.length);
      vbo.color.put(vbo._colors);
      vbo.color.flip();

      vbo.indices = Buffers.newDirectIntBuffer(index.length);
      vbo.indices.put(index);
      vbo.indices.flip();

      //загружаем буферы в GPU
      int[] temp = new int[3];
      gl.glGenBuffers(3, temp, 0);

      vbo.VBOVertices = temp[0];
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.VBOVertices);
      gl.glBufferData(GL2.GL_ARRAY_BUFFER, vbo.vert.capacity() * Buffers.SIZEOF_FLOAT,
          vbo.vert, GL2.GL_STATIC_DRAW);
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

      vbo.VBONormals = temp[1];
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.VBONormals);
      gl.glBufferData(GL2.GL_ARRAY_BUFFER, vbo.norm.capacity() * Buffers.SIZEOF_FLOAT,
          vbo.norm, GL2.GL_STATIC_DRAW);
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

      vbo.VBOIndices = temp[2];
      gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vbo.VBOIndices);
      gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, vbo.indices.capacity() * Buffers.SIZEOF_INT,
          vbo.indices, GL2.GL_STATIC_DRAW);
      gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

      int[] col = new int[1];
      gl.glGenBuffers(1, col, 0);
      vbo.VBOColors = col[0];
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.VBOColors);
      gl.glBufferData(GL2.GL_ARRAY_BUFFER, vbo.color.capacity() * Buffers.SIZEOF_FLOAT,
          vbo.color, GL2.GL_STATIC_DRAW);
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
    }
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

  private void addCovers(List<i_Cover> covers) {
    for (i_Cover cover : covers){
      Mesh3D mesh = cover.getMesh();
      VBOMesh vbo = new VBOMesh();
      //сохраняем цвета в буфер
      int k = 0;
      vbo._colors = new float[cover.getCover().getPointCover().size() * 4];
      for (ColorPoint cp : cover.getCover().getPointCover()) {
        vbo._colors[k++] = (float)cp.getColor().getRed();
        vbo._colors[k++] = (float)cp.getColor().getGreen();
        vbo._colors[k++] = (float)cp.getColor().getBlue();
        vbo._colors[k++] = (float)cp.getColor().getAlpha();
      }
      vbo._isColor = true;

      _meshes.add((WETriangleMesh) mesh);
      _vbo.add(vbo);
    }
  }
  
  @Override
  public SceneType getSceneType() {
      return SceneType.SceneSTL;
  }

  public void reload(List<i_Cover> covers){
    _vbo.clear();
    _meshes.clear();
    addCovers(covers);
    init(render.getDrawable());
    initPerspectiveParameters();
  }


  public WETriangleMesh getMesh(int index) {
    return _meshes.get(index);
  }

  public int getMeshesSize() {
    return _meshes.size();
  }

  /**
   * Находит дистанцию от 0 до проекции на какую-то ось самой удалённой точки
   * @return
   */
  public double getMaxDistance() {
    double result = 0;

    for (WETriangleMesh mesh : _meshes) {
      Vec3D v = mesh.getBoundingBox().getExtent();
      result = Math.max(Math.abs(v.x()), Math.max(Math.abs(v.y()),
          Math.max(Math.abs(v.z()), result)));
    }

    return result;
  }

  /**
   * Класс предназначен для того, чтобы отрисовывать каждый меш на GPU
   *
   * @author Kurgansky
   */
  class VBOMesh {
    int VBOVertices, VBONormals, VBOIndices, VBOColors;
    IntBuffer indices;
    FloatBuffer vert, norm, color;
    boolean _isColor = false;
    float[] _colors;
  }
}

