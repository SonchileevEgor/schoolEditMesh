package opengl.scenegl;

/**
 * Enumerator for difinotion type of scene
 * @author Vladimir
 */
public enum SceneType {
  SceneSTL(true),
  Scene3D(true), 
  Scene2D(false);

  private boolean _is3D;
  SceneType(boolean is3D){
    _is3D = is3D;
  }

  public boolean is3d(){
    return _is3D;
  }
}
