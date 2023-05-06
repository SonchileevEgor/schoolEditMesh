package maquettes;

import editor.i_Identifiable;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 *
 * @author Vladimir
 */
public interface i_Cover extends i_Identifiable{
    /**
     * Focus state
     * @return 
     */
    public boolean getChosen();
    
    public void setChosen(boolean state);
    /**
     * Type of the cover.
     * @return
     */
    public CoverType type();

    /**
     * Return Geom object of body.
     * @return
     */
    public i_Geom getGeom();
  
    /**
     * @return mesh
     */
    public Cover getCover();
    
    /**
     * Intersection of cover with ray.
     * Object is assumed as solid, boundary included.
     * Method is used when cover is picked on scene.
     * Render is needed for providing screen features (scale, resolution, etc.)
     * @param ren
     * @param ray
     * @param x Coordinate X of canvas. Redundant data used for performance.
     * @param y Coordinate Y of canvas. Redundant data used for performance.
     * @return 
     *    Intersection point (closest to camera) or null.
     */
    public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y);
    
    public void setMesh(WETriangleMesh mesh);

    public Mesh3D getMesh();
}
