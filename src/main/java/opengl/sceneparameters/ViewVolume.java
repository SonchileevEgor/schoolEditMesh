package opengl.sceneparameters;

import opengl.Render;
import geom.Cube3d;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;

import javax.media.opengl.GL2;
import java.util.ArrayList;

/**
 * Class to work with the volume of visibility
 */
public class ViewVolume {

    // Size of the cube of view volume
  private double _sizeVisible = 2.00001;

  /**
   * Get size of viewing cube (half of side)
   * @return Size of viewing cube (half of side)
   */
  public double getSizeVisible(){return _sizeVisible;}

  /**
   * Sets size of viewing cube (half of side)
   * <p>When installing the viewing volume is preferable to make it a little more (or less) so that there are
   * no points at the border viewing volume. Due to rounding errors such points will then appear,
   * then disappear when rendering the scene.
   * @param sizeVisible Size of viewing cube (half of side)
   */
  public void setSizeVisible(double sizeVisible){_sizeVisible = sizeVisible;}

  /**
   * Sets opengl clipping planes
   * @param ren Object for drawing in opengl canvas
   */
  public void setСlippingPlanes(Render ren){
    GL2 gl = ren.getGL();
    double size = _sizeVisible;
    double equation0[] = {1,0,0,size};
    double equation1[] = {-1,0,0,size};
    double equation2[] = {0,1,0,size};
    double equation3[] = {0,-1,0,size};
    double equation4[] = {0,0,1,size};
    double equation5[] = {0,0,-1,size};

    enableClippingPlanes(ren);

    gl.glClipPlane(GL2.GL_CLIP_PLANE0, equation0, 0);
    gl.glClipPlane(GL2.GL_CLIP_PLANE1, equation1, 0);
    gl.glClipPlane(GL2.GL_CLIP_PLANE2, equation2, 0);
    gl.glClipPlane(GL2.GL_CLIP_PLANE3, equation3, 0);
    gl.glClipPlane(GL2.GL_CLIP_PLANE4, equation4, 0);
    gl.glClipPlane(GL2.GL_CLIP_PLANE5, equation5, 0);
  }

  public void enableClippingPlanes(Render ren){
    GL2 gl = ren.getGL();
    gl.glEnable(GL2.GL_CLIP_PLANE0);
    gl.glEnable(GL2.GL_CLIP_PLANE1);
    gl.glEnable(GL2.GL_CLIP_PLANE2);
    gl.glEnable(GL2.GL_CLIP_PLANE3);
    gl.glEnable(GL2.GL_CLIP_PLANE4);
    gl.glEnable(GL2.GL_CLIP_PLANE5);
  }
  
  public void disableClippingPlanes(Render ren){
    GL2 gl = ren.getGL();
    gl.glDisable(GL2.GL_CLIP_PLANE0);
    gl.glDisable(GL2.GL_CLIP_PLANE1);
    gl.glDisable(GL2.GL_CLIP_PLANE2);
    gl.glDisable(GL2.GL_CLIP_PLANE3);
    gl.glDisable(GL2.GL_CLIP_PLANE4);
    gl.glDisable(GL2.GL_CLIP_PLANE5);
  }

  /**
   * Check whether a point in the cube visibility (cube, beyond which nothing is drawn)
   * @param point Drawing point
   * @return true, if point in viewing cube
   */
  public boolean inViewingCube(Vect3d point){
    double size = _sizeVisible;
    return (
            point.x() > -size && point.x() < size &&
            point.y() > -size && point.y() < size &&
            point.z() > -size && point.z() < size
    );
  }

  public ArrayList<Plane3d> getClippingPlanes() {
    ArrayList<Plane3d> planes = new ArrayList<>();
    Cube3d scene_cube = getAsCube();
    try {
      planes.add(new Plane3d(scene_cube.A1(), scene_cube.B1(), scene_cube.C1()));
      planes.add(new Plane3d(scene_cube.A2(), scene_cube.B2(), scene_cube.C2()));
      planes.add(new Plane3d(scene_cube.A1(), scene_cube.B1(), scene_cube.A2()));
      planes.add(new Plane3d(scene_cube.C1(), scene_cube.D1(), scene_cube.C2()));
      planes.add(new Plane3d(scene_cube.A1(), scene_cube.C1(), scene_cube.A2()));
      planes.add(new Plane3d(scene_cube.B1(), scene_cube.D1(), scene_cube.B2()));
    }
    catch (ExDegeneration ex) {
      ex.printStackTrace();
    }
    return planes;
  }
  
  /**
   * Get view volume as cube
   * @return View volume as cube
   */
  public Cube3d getAsCube() {
    Cube3d view_cube;
    try{
      Vect3d p1 = new Vect3d(-_sizeVisible, -_sizeVisible, -_sizeVisible);
      Vect3d p2 = new Vect3d(_sizeVisible, -_sizeVisible, -_sizeVisible);
      Vect3d p3 = new Vect3d(-_sizeVisible, _sizeVisible, -_sizeVisible);
      view_cube = new Cube3d(p1, p2, p3);
    }
    catch (ExDegeneration exDegeneration) {
      exDegeneration.printStackTrace();
      view_cube = null;
    }
    return view_cube;
  }


}
