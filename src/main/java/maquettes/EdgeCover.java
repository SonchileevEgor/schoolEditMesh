package maquettes;

import static config.Config.LOG_LEVEL;
import static config.Config.MAQUETTE_RIB_PHASES;
import static config.Config.MAQUETTE_RIB_SLICES;
import geom.Orientation;
import geom.*;

import java.util.ArrayList;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;
import util.Log;

/**
 * @author Leonid Ivanovsky
 */

// FIXME: имеется проблема смыкания сгенерированных оболочек для круглых тел

/**
 * In fact, generated cover around edge will have a cylinder's shape
 */

public class EdgeCover implements i_GeneratedCover, i_Model, i_Cover {
    private boolean _choosen = false;
    private ColorEdge _edge; // rib around which must generate a cover
    private double _eps; // radius of the neighborhood around which points of the cover will be generated
    private Vect3d _edgeOrientProections; // proections of edge
    private int _idxOfPoint; // number of point, at which it can to describe face definitions
    private Cover _cover; // generated points from cover
    private WETriangleMesh _mesh;
    private String _id = new String();
    
    public EdgeCover(ColorEdge input_edge, double eps, int start_def, String id) {
        _edge = input_edge;
        _eps = eps;
        _idxOfPoint = start_def;
        _cover = new Cover();
        _edgeOrientProections = new Vect3d();
        CalculateRibOrientProections();
        _id = id;
    }

    /**
     * Calculate orient projections of rib
     */
    private void CalculateRibOrientProections() {
        _edgeOrientProections = _edge.getEdge().b().sub(_edge.getEdge().a());
    }

    /**
     * Get circle layer which contains generated points
     * @param slice number of slice, which determines the index number of generating circle around the rib.
     * The index number determines an coordinate of generating circle's center.
     * The generating circle contains generating points around a point on the rib.
     * @return circle, which contains generating points for cover
     */
    private Circle3d getCircleLayer(int slice) {
        double coef = (double) slice / MAQUETTE_RIB_SLICES.value();
        double xo = _edge.getEdge().a().x() + _edgeOrientProections.x()*coef;
        double yo = _edge.getEdge().a().y() + _edgeOrientProections.y()*coef;
        double zo = _edge.getEdge().a().z() + _edgeOrientProections.z()*coef;
        Vect3d circle_center = new Vect3d(xo, yo, zo);
        try {
            Vect3d circle_normal = _edge.getEdge().line().l();
            if (!circle_normal.equals(Vect3d.O)) // if normal of the future circle layer is not zero-vector
                return new Circle3d(Vect3d.rNormalizedVector(circle_normal, _eps), circle_center);
        } catch (ExDegeneration ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.println(ex.getMessage());
          }
        }
        return null;
    }

    /**
     * Build point cover for point cloud
     */
    @Override
    public void buildPointCover() {
        for (int slice=0; slice<=MAQUETTE_RIB_SLICES.value(); slice++) {
            Circle3d gen_circle = getCircleLayer(slice);
            ArrayList<Vect3d> gen_points = gen_circle.getAsAbstractPolygon(MAQUETTE_RIB_PHASES.value());
            _cover.addPoints(gen_points, _edge.getColor());
        }
    }

    /**
     * Build triangular cover for output in stl-file
     */
    @Override
    public void buildTriangularCover() {
        ArrayList<ColorPoint> points_cover = _cover.getPointCover();
        int last = points_cover.size()/MAQUETTE_RIB_PHASES.value() - 1;
        int start_def;
        for (int i=0; i<last; i++) {
            start_def = _idxOfPoint+i*(MAQUETTE_RIB_PHASES.value()+1);
            Orientation bypass = Orientation.RIGHT;
            if (i == 0 ||  i == last-1) { // first or last index
                ArrayList<Vect3d> basepoints = new ArrayList<>(); // base of rib, which contains triangles of 3d-model
                int begin, end;
                if (i ==0) {
                    begin = 0;
                    end = MAQUETTE_RIB_PHASES.value()-1;
                }
                else {
                    begin = MAQUETTE_RIB_PHASES.value();
                    end = 2*MAQUETTE_RIB_PHASES.value()-1;
                }
                for (int j = begin; j<=end+1; j++)
                    basepoints.add(_cover.getPointCover().get(MAQUETTE_RIB_PHASES.value()*i+j).getPoint());
                if (i == last-1)
                    _cover.addTriangleFaceOnPolygon(basepoints, bypass, start_def + begin+1);
                else
                    _cover.addTriangleFaceOnPolygon(basepoints, bypass.change(), start_def);
            }
            ArrayList<Vect3d> points = new ArrayList<>(); // lateral surfaces of rib, which contains triangles of 3d-model
            for (int j = 0; j<2*(MAQUETTE_RIB_PHASES.value()+1); j++)
                points.add(_cover.getPointCover().get((MAQUETTE_RIB_PHASES.value()+1)*i+j).getPoint());
            _cover.addTriangleFaceOnPrismLatSurf(points, bypass, start_def);
        }
    }

    /**
     * Sculpt covers of rib
     */
    @Override
    public void sculptModels() {
        buildPointCover();
        buildTriangularCover();
    }

    /**
     * Return generated points around rib
     * @return
     */
    @Override
    public Cover getCover() {
        return _cover;
    }

    @Override
    public CoverType type() {
        return CoverType.EDGE;
    }

    @Override
    public i_Geom getGeom() {
        return _edge.getEdge(); 
    }

    @Override
    public String id() {
        return _id;
    }
    
    @Override
    public boolean getChosen() {
        return _choosen;
    }

    @Override
    public void setChosen(boolean state) {
        _choosen = state;
    }

    @Override
    public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
        ArrayList<Vect3d> intersect = _edge.getEdge().intersect(ray);
        Vect3d result = intersect.get(0).sub(ren.getCameraPosition().eye());
        result.inc_mul(_eps);
        return result;
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