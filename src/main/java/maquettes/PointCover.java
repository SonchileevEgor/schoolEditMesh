package maquettes;

import bodies.SphereBody;
import static config.Config.MAQUETTE_VERTICE_MERIDIANS;
import static config.Config.MAQUETTE_VERTICE_PARALLELS;
import geom.Orientation;
import geom.Ray3d;
import geom.Sphere3d;
import geom.Vect3d;
import geom.i_Geom;

import java.util.ArrayList;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 * @author Leonid Ivanovsky
 */

/**
 * In fact, generated cover around point will have a shere's shape
 */

public class PointCover implements i_GeneratedCover, i_Model, i_Cover {
    private boolean _chosen = false;
    private ColorPoint _vertice; // point around which must generate a cover
    private double _eps; // radius of the 3d-neighborhood around which points of the cover will be generated
    private int _idxOfPoint; // number of point, at which it can to describe face definitions
    private Cover _cover; // cover around point
    private WETriangleMesh _mesh;
    private String _id = new String();
    
    public PointCover(ColorPoint input_vertice, int start_def, String id) {
        _vertice = input_vertice;
        _eps = input_vertice.getCoverRadius();
        _idxOfPoint = start_def;
        _cover = new Cover();
        _id = id;
    }

    /**
    * Build point based on the polar coordinate system from epsilon-neighborhood of rib by number of fraction and phase rotation
    * @param rand_point
    * @param parallel
    * @param meridian
    * @return
    */
    private void buildPointFromNeighborhood(Vect3d rand_point, int parallel, int meridian) {
        double phi = meridian*2*Math.PI/MAQUETTE_VERTICE_MERIDIANS.value();
        double theta = parallel*Math.PI/(MAQUETTE_VERTICE_PARALLELS.value()-1);
        double x_coor = _vertice.getPoint().x() + _eps*Math.sin(theta)*Math.cos(phi);
        double y_coor = _vertice.getPoint().y() + _eps*Math.sin(theta)*Math.sin(phi);
        double z_coor = _vertice.getPoint().z() + _eps*Math.cos(theta);
        rand_point.set_x(x_coor);
        rand_point.set_y(y_coor);
        rand_point.set_z(z_coor);
    }

    /**
     * Get random point from epsilon-neighborhood of rib by number of fraction and phase rotation
     * @param parallel
     * @param meridian
     * @return
     */
    private Vect3d getPointFromNeighborhood(int parallel, int meridian) {
        Vect3d rand_point = new Vect3d();
        buildPointFromNeighborhood(rand_point, parallel, meridian);
        return rand_point;
    }

    /**
     * Build point cover for point cloud.
     * The cover around a vertice is similar to globe.
     */
    @Override
    public void buildPointCover() {
        for (int parallel = 0; parallel < MAQUETTE_VERTICE_PARALLELS.value(); parallel++) {
            if ((parallel != 0) && (parallel != MAQUETTE_VERTICE_PARALLELS.value() - 1)) {
                for (int meridian = 0; meridian < MAQUETTE_VERTICE_MERIDIANS.value(); meridian++) {
                    Vect3d rand_point = getPointFromNeighborhood(parallel, meridian);
                    _cover.addPoint(new ColorPoint(rand_point, _vertice.getColor(), _id));
                }
            }
            else {
                Vect3d rand_point = getPointFromNeighborhood(parallel, 0);
                _cover.addPoint(new ColorPoint(rand_point, _vertice.getColor(), _id));
            }
        }
    }

    /**
     * Build triangular cover for output in stl-file
     */
    @Override
    public void buildTriangularCover() {
        Orientation bypass = Orientation.RIGHT;
        buildTriangularCover(bypass);
    }
    
    public void buildTriangularCover(Orientation bypass) {
        ArrayList<ColorPoint> vertice_points = _cover.getPointCover();
        ArrayList<Vect3d> points = new ArrayList<>();
        int end = vertice_points.size();
        for (int i=0; i<end; i++) { // The bypass performed as in the globe
            if (i == 0 || i == end-1) { // upper and lower points of the bypass
                boolean is_reverseorder = false;
                ArrayList<Vect3d> basepoints = new ArrayList<>();
                basepoints.add(vertice_points.get(i).getPoint());
                for (int j=1; j<=MAQUETTE_VERTICE_MERIDIANS.value(); j++) {
                    if (i == 0)
                        basepoints.add(vertice_points.get(i+j).getPoint());
                    else
                        basepoints.add(vertice_points.get(i-j).getPoint());
                }
                if (i == end-1)
                    is_reverseorder = true;
                int start_def = _idxOfPoint+i;
                _cover.addTriangleFaceOnPyrLatSurf(basepoints, start_def, is_reverseorder, bypass);
            }
            else {
                points.add(vertice_points.get(i).getPoint());
                if (points.size() == 2*MAQUETTE_VERTICE_MERIDIANS.value()) {
                    //Orientation bypass = Orientation.LEFT;
                    int start_def =  _idxOfPoint+i-points.size()+1;
                    _cover.addTriangleFaceOnPrismLatSurf(points, bypass.change(), start_def);
                    for (int j=0; j<points.size(); j++)
                        points.remove(0);
                }
            }
        }
    }

    /**
     * Sculpt model of point
     */
    @Override
    public void sculptModels() {
        buildPointCover();
        PointCover.this.buildTriangularCover();
    }

    public void SculptModels(Orientation bypass) {
        buildPointCover();
        buildTriangularCover(bypass);
    }
    /**
     * Return generated points around point
     * @return
     */
    @Override
    public Cover getCover() {
        return _cover;
    }

    @Override
    public CoverType type() {
        return CoverType.POINT;
    }

    @Override
    public i_Geom getGeom() {
        return _vertice.getPoint();
    }

    @Override
    public String id() {
        return _id;
    }
    
    @Override
    public boolean getChosen() {
        return _chosen;
    }

    @Override
    public void setChosen(boolean state) {
        _chosen = state;
    }

    @Override
    public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
        SphereBody sphere = new SphereBody("", "", new Sphere3d(_vertice.getPoint(), _eps));
        return sphere.intersectWithRay(ren, ray, x, y);
    }

    @Override
    public void setMesh(WETriangleMesh mesh) {
        _mesh = mesh;
    }
    
    @Override
    public Mesh3D getMesh() {
        return _mesh;
    }
}