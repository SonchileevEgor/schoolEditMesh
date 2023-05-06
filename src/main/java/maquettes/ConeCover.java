
package maquettes;

import geom.Orientation;
import geom.*;

import bodies.ConeBody;
import static config.Config.LOG_LEVEL;
import editor.i_Body;
import java.util.ArrayList;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;
import util.Log;

/**
 *
 * @author Igor
 */
public class ConeCover implements i_GeneratedCover, i_Model, i_Cover {
    private boolean _choosen = false;
    private ConeBody _body; // body around which should generate a cover
    private ColorCone _cone;
    private double _thicknessCover; // thickness of the neighborhood around which points of the cover will be generated
    private int _idxOfPoint; // number of point, at which it can to describe face definitions
    private Cover _cover; // cover around body
    private ArrayList<Vect3d> _inside_points; // точки основания внутреннего конуса
    private ArrayList<Vect3d> _body_base_points; // точки основания внешнего конуса
    private Vect3d _inside_vertex; // внутренняя вершина
    private Vect3d _vert; // внешняя вершина
    private WETriangleMesh _mesh;

    public ConeCover(ConeBody body, ColorCone cone, double thickness_bodycover, int start_def) throws ExDegeneration {
        _body = body;
        _cone = cone;
        _idxOfPoint = start_def;
        _thicknessCover = thickness_bodycover;
        _cover = new Cover();
        _inside_points = new ArrayList<>();
        _body_base_points = _body.getBasePoints();
        _inside_vertex = new Vect3d();
        _vert = _body.vertex();
    }



    /**
     * Создание точек внутреннего конуса путем сдвига исходных
     * точек на сумму векторов нормалей прилежащих граней с определёнными коэфициентами.
     * Точки внешнего конуса не меняются.
     *
     */
    private void buildCoverAroundCone(){
        Vect3d delta_vert = new Vect3d();
        double radius = _body.radius();
        double height = _body.height().norm();
        double sinus = radius/ Math.sqrt(Math.pow(radius, 2) + Math.pow(height, 2));
        double surf_coef = _thicknessCover / (1 - sinus);
        double base_coef = 2 * surf_coef;
        int size = _body_base_points.size();
        for (int i=1; i<size + 1; i++) {
            Vect3d body_vertex = _body_base_points.get(i % size);
            Vect3d a = _body_base_points.get((i-1) % size);
            Vect3d b = _body_base_points.get((i+1) % size);
            Plane3d face1;
            Plane3d face2;
            Plane3d face3;
            try {
                face1 = new Plane3d(body_vertex, b, _vert);
                face2 = new Plane3d(body_vertex, _vert, a);
                face3 = new Plane3d(body_vertex, a, b);
                Vect3d normal_sum = Vect3d.mul(face1.n().sum(face2.n()), surf_coef).sum(Vect3d.mul(face3.n(), base_coef));
                _inside_points.add(Vect3d.sum(body_vertex, normal_sum));
                delta_vert.inc_add(face1.n());
            }
            catch (ExDegeneration e) {
            }

        }
        delta_vert =  delta_vert.getNormalized().mul(_thicknessCover / sinus);
        _inside_vertex = Vect3d.sum(_vert, delta_vert);

    }

    @Override
    public void buildPointCover() {
        _cover.addPoint(_inside_vertex, _cone.getColor());
        _cover.addPoints(_inside_points, _cone.getColor());
        _cover.addPoint(_vert, _cone.getColor());
        _cover.addPoints(_body_base_points, _cone.getColor());

    }

    @Override
    public void buildTriangularCover() {
        try {
            Polygon3d inside_base = new Polygon3d(_inside_points);
            Polygon3d outside_base = new Polygon3d(_body_base_points);
            int start_def = _idxOfPoint+inside_base.points().size() + 1;
            ArrayList<Vect3d> inside_pyr = new ArrayList<>();
            ArrayList<Vect3d> outside_pyr = new ArrayList<>();
            inside_pyr.add(_inside_vertex);
            outside_pyr.add(_vert);
            inside_pyr.addAll(_inside_points);
            outside_pyr.addAll(_body_base_points);
            _cover.addTriangleFaceOnPolygon(inside_base.points(), Orientation.LEFT, _idxOfPoint + 1);
            _cover.addTriangleFaceOnPyrLatSurf(inside_pyr, _idxOfPoint, false, Orientation.RIGHT);
            _cover.addTriangleFaceOnPolygon(outside_base.points(), Orientation.RIGHT, start_def + 1);
            _cover.addTriangleFaceOnPyrLatSurf(outside_pyr, start_def, false, Orientation.LEFT);
        }
        catch (ExGeom ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.println(ex.getMessage());
          }
        }
    }

    @Override
    public void sculptModels() {
        buildCoverAroundCone();
        buildPointCover();
        buildTriangularCover();
    }

    @Override
    public Cover getCover() {
        return _cover;
    }

    @Override
    public CoverType type() {
        return CoverType.CONE;
    }

    @Override
    public i_Geom getGeom() {
        return _cone.getCone();
    }

    @Override
    public String id() {
        return _body.id();
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
        Vect3d result = _body.intersectWithRay(ren, ray, x, y);
        result.inc_mul(_thicknessCover);
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
