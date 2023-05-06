package opengl.sceneparameters;

/**
 * Initial plane parameters (drawing as grid)
 */
public class InitialPlane {
  /**
   * Number of cells from the center to the edge of the grid
   */
  private int _numCells;
  /**
   * The size of one cell
   */
  private double _meshSize;

  public InitialPlane(){
    _numCells = 15;
    _meshSize = 0.25;
  }

  public int getNumCells(){return _numCells;}
  public double getMeshSize(){return  _meshSize;}

  public void setNumCells(int numCells){ _numCells = numCells;}
  public void setMeshSize(double meshSize){_meshSize = meshSize;}

  public double getSizeInitPlane(){return _numCells * _meshSize;}
}
