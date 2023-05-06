
package maquettes;

import geom.Orientation;
import geom.*;

import bodies.CylinderBody;
import static config.Config.LOG_LEVEL;
import java.util.ArrayList;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;
import util.Log;

/**
 *
 * @author Igor
 * @author Vladimir
 */
public class CylinderCover implements i_GeneratedCover, i_Model, i_Cover {
    private boolean _choosen = false;
    private CylinderBody _body; // body around which should generate a cover
    private ColorCylinder _cylinder;
    private double _thicknessCover; // thickness of the neighborhood around which points of the cover will be generated
    private int _idxOfPoint; // number of point, at which it can to describe face definitions
    private Cover _cover; // cover around body
    private ArrayList<Vect3d> _fst_base_points; // точки первого основания цилиндра
    private ArrayList<Vect3d> _snd_base_points; // точки второго основания цилиндра
    private ArrayList<Vect3d> _inside_fst_base_points; // точки первого основания внутреннего цилиндра
    private ArrayList<Vect3d> _inside_snd_base_points; // точки второго основания внутреннего цилиндра
    private WETriangleMesh _mesh;

    public CylinderCover(CylinderBody body, ColorCylinder cylinder, double thickness_bodycover, int start_def) throws ExDegeneration {
        _body = body;
        _cylinder = cylinder;
        _idxOfPoint = start_def;
        _thicknessCover = thickness_bodycover;
        _cover = new Cover();
        _fst_base_points = _body.cylinder().circ1().getAsAbstractPolygon();
        _snd_base_points = _body.cylinder().circ2().getAsAbstractPolygon();
        _inside_fst_base_points = new ArrayList<Vect3d>();
        _inside_snd_base_points = new ArrayList<Vect3d>();
    }

    /**
     * Создание точек внутреннего цилиндра путем сдвига исходных
     * точек на сумму векторов нормалей прилежащих граней с определёнными коэфициентами.
     * Точки внешнего цилиндра не меняются.
     *
     */
    private void buildCoverAroundCylinder(){
        double surf_coef = _thicknessCover;
        double base_coef = 2 * surf_coef;
        int size = _fst_base_points.size();
        for (int i=1; i<size + 1; i++) {
            Vect3d fst_body_vertex = _fst_base_points.get(i % size);
            Vect3d snd_body_vertex = _snd_base_points.get(i % size);
            Vect3d a = _fst_base_points.get((i-1) % size);
            Vect3d b = _fst_base_points.get((i+1) % size);
            Plane3d face1;
            Plane3d face2;
            Plane3d face3;
            try {
                face1 = new Plane3d(fst_body_vertex, b, snd_body_vertex);
                face2 = new Plane3d(fst_body_vertex, snd_body_vertex, a);
                face3 = new Plane3d(fst_body_vertex, a, b);
                Vect3d fst_normal_sum = Vect3d.mul(face1.n().sum(face2.n()), surf_coef).sum(Vect3d.mul(face3.n(), base_coef));
                Vect3d snd_normal_sum = Vect3d.mul(face1.n().sum(face2.n()), surf_coef).sub(Vect3d.mul(face3.n(), base_coef));
                _inside_fst_base_points.add(Vect3d.sub(fst_body_vertex, fst_normal_sum));
                _inside_snd_base_points.add(Vect3d.sub(snd_body_vertex, snd_normal_sum));
            }
            catch (ExDegeneration e) {
            }

        }
    }

    @Override
    public void buildPointCover() {
        _cover.addPoints(_fst_base_points, _cylinder.getColor());
        _cover.addPoints(_snd_base_points, _cylinder.getColor());
        _cover.addPoints(_inside_fst_base_points, _cylinder.getColor());
        _cover.addPoints(_inside_snd_base_points, _cylinder.getColor());

    }

    @Override
    public void buildTriangularCover() {
        try {
            Polygon3d fst_base = new Polygon3d(_fst_base_points);
            Polygon3d snd_base = new Polygon3d(_snd_base_points);
            Polygon3d inside_fst_base = new Polygon3d(_inside_fst_base_points);
            Polygon3d inside_snd_base = new Polygon3d(_inside_snd_base_points);
            int start_def = _idxOfPoint+_fst_base_points.size() * 2;
            int size = _fst_base_points.size();
            ArrayList<Vect3d> prism = new ArrayList<>();
            ArrayList<Vect3d> inside_prism = new ArrayList<>();
            prism.addAll(_fst_base_points);
            prism.addAll(_snd_base_points);
            inside_prism.addAll(_inside_fst_base_points);
            inside_prism.addAll(_inside_snd_base_points);
            _cover.addTriangleFaceOnPolygon(fst_base.points(), Orientation.LEFT, _idxOfPoint);
            _cover.addTriangleFaceOnPolygon(snd_base.points(), Orientation.RIGHT, _idxOfPoint + size);
            _cover.addTriangleFaceOnPrismLatSurf(prism, Orientation.RIGHT, _idxOfPoint);
            _cover.addTriangleFaceOnPolygon(inside_fst_base.points(), Orientation.RIGHT, start_def);
            _cover.addTriangleFaceOnPolygon(inside_snd_base.points(), Orientation.LEFT, start_def + size);
            _cover.addTriangleFaceOnPrismLatSurf(inside_prism, Orientation.LEFT, start_def);
        }
        catch (ExGeom ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.println(ex.getMessage());
          }
        }

    }

    @Override
    public void sculptModels() {
        buildCoverAroundCylinder();
        buildPointCover();
        buildTriangularCover();
    }

    @Override
    public Cover getCover() {
        return _cover;
    }

    @Override
    public CoverType type() {
        return CoverType.CYLINDER;
    }

    @Override
    public i_Geom getGeom() {
        return _cylinder.getCylinder();
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
