package opengl;

import javax.media.opengl.GL2;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper on glEnable()/glDisable() with automatic save of state and restore support.
 */
public class SwitcherStateGL {
  static private Map<Integer, Boolean> savedStatesGL = new HashMap<>();
  static private int savedStateDepthFunc = GL2.GL_LESS;
  static private BlendFuncState savedStateBlendFunc = new BlendFuncState(GL2.GL_ONE, GL2.GL_ZERO);
  private SwitcherStateGL(){}
  static public void saveOldState(Render ren, int cap){
    byte[] oldState = new byte[1];
    GL2 gl = ren.getGL();
    gl.glGetBooleanv(cap, oldState, 0);
    if (oldState[0] == 0x01)
      savedStatesGL.put(cap, true);
    else if (oldState[0] == 0x00)
      savedStatesGL.put(cap, false);
    else
      System.out.println("SwitcherStateGL not works correctly");
  }
  static public void deleteOldState(int cap){
    savedStatesGL.remove(cap);
  }
  static public void saveAndEnable(Render ren, int cap){
    GL2 gl = ren.getGL();
    saveOldState(ren, cap);
    gl.glEnable(cap);
  }
  static public void saveAndDisable(Render ren, int cap){
    GL2 gl = ren.getGL();
    saveOldState(ren, cap);
    gl.glDisable(cap);
  }
  static public void restore(Render ren, int cap){
    GL2 gl = ren.getGL();
    if (savedStatesGL.containsKey(cap)){
      if (savedStatesGL.get(cap) == true)
        gl.glEnable(cap);
      else
        gl.glDisable(cap);
    }
    else{
      System.out.println(String.format("Warning: restore of cap = %d is impossible", cap));
    }
  }

  static public void saveOldStateDepthFunc(Render ren){
    GL2 gl = ren.getGL();
    int[] param = new int[1];
    gl.glGetIntegerv(GL2.GL_DEPTH_FUNC, param, 0);
    savedStateDepthFunc = param[0];
  }
  static public void saveAndChangeDepthFunc(Render ren, int newDepthFunc){
    saveOldStateDepthFunc(ren);
    ren.getGL().glDepthFunc(newDepthFunc);
  }
  static public void restoreDepthFunc(Render ren){
    ren.getGL().glDepthFunc(savedStateDepthFunc);
  }

  static public void saveOldStateBlendFunc(Render ren){
    GL2 gl = ren.getGL();
    int[] param = new int[2];
    gl.glGetIntegerv(GL2.GL_BLEND_SRC, param, 0);
    gl.glGetIntegerv(GL2.GL_BLEND_DST, param, 1);
    savedStateBlendFunc.setSrcFactor(param[0]);
    savedStateBlendFunc.setDstFactor(param[1]);
  }
  static public void saveAndChangeBlendFunc(Render ren, int sfactor, int dfactor){
    saveOldStateBlendFunc(ren);
    ren.getGL().glBlendFunc(sfactor, dfactor);
  }
  static public void restoreBlendFunc(Render ren){
    GL2 gl = ren.getGL();
    gl.glBlendFunc(savedStateBlendFunc.getSrcFactor(), savedStateBlendFunc.getDstFactor());
  }

}

class BlendFuncState{
  private int srcFactor;
  private int dstFactor;
  BlendFuncState(int srcFactor, int dstFactor){
    setSrcFactor(srcFactor);
    setDstFactor(dstFactor);
  }

  public int getSrcFactor() {
    return srcFactor;
  }

  public void setSrcFactor(int srcFactor) {
    this.srcFactor = srcFactor;
  }

  public int getDstFactor() {
    return dstFactor;
  }

  public void setDstFactor(int dstFactor) {
    this.dstFactor = dstFactor;
  }
}
