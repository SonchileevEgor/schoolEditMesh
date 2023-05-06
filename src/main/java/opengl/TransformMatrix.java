package opengl;

import geom.Vect3d;

/**
 * A class for storing transformation matrices of OpenGL
 */
public class TransformMatrix {
  private double[] _model = new double[16];
  private double[] _projection = new double[16];
  private int[] _viewport = new int[16];

  public double[] getModel() {
    return _model;
  }

  public void setModel(double[] model){
    for(int i = 0; i < 16; i++){
      _model[i] = model[i];
    }
  }

  public Matrix4 getModelExt() {
    return new Matrix4(_model);
  }

  public double[] getProjection() {
    return _projection;
  }

  public void setProjection(double[] projection){
    for(int i = 0; i < 16; i++){
      _projection[i] = projection[i];
    }
  }

  public int[] getViewport(){return _viewport;}

  public void setViewport(int[] viewport) {
    for (int i = 0; i < 16; i++) {
      _viewport[i] = viewport[i];
    }
  }

  public TransformMatrix copy() {
    TransformMatrix res = new TransformMatrix();
    res.setModel(_model);
    res.setProjection(_projection);
    res.setViewport(_viewport);
    return res;
  }
}

/**
 * Class for storing matrix 4x4
 */
class Matrix4 {
  double[][] _data = new double[4][4];
  Matrix4(double[] glMatrix){
    setMatrix(glMatrix);
  }

  Matrix4() {
    double[] oneMatrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1};
    setMatrix(oneMatrix);
  }
  void setMatrix(double[] glMatrix){
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        _data[j][i] = glMatrix[i*4+j];// In Opengl matrix stored by columns
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        res.append(_data[i][j]).append(" ");
      }
      res.append('\n');
    }
    return res.toString();
  }

  /**
   * Multiply matrix by a column vector.
   * <p>Need to get the real coordinates of points and vectors using OpenGL transformations
   * @param v Vector, which multiply
   * @param w The fourth component of the vector. For points w = 1, for vectors w = 0.
   * @return Vector multiplied by the matrix.
   * @see <a href="http://opengl-tutorial.blogspot.ru/p/3.html">Matrix in OpenGL: tutorial</a>
   */
  Vect3d mulOnVect(Vect3d v, double w){
    double x = _data[0][0]*v.x() + _data[0][1]*v.y() + _data[0][2]*v.z() + w * _data[0][3];
    double y = _data[1][0]*v.x() + _data[1][1]*v.y() + _data[1][2]*v.z() + w * _data[1][3];
    double z = _data[2][0]*v.x() + _data[2][1]*v.y() + _data[2][2]*v.z() + w * _data[2][3];
    return new Vect3d(x,y,z);
  }
}