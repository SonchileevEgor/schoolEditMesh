package maquettes;

import static config.Config.LOG_LEVEL;
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

/**
 * In fact, generated cover around face will have a prism's shape
 */

public class FaceCover implements i_GeneratedCover, i_Model, i_Cover {
    private boolean _choosen = false;
    private ColorFace _face; // face around which should generate a cover
    private double _thicknessCover; // thickness of the neighborhood around which points of the cover will be generated
    private BodyDimension _dimension; // dimension of body
    private int _idxOfPoint; // number of point, at which it can to describe face definitions
    //private HashMap<CoordPlanes, Polygon3d> _faceProections; // proections of face on every coordinate plane
    //private HashMap<CoordPlanes, Double> _areaFaceProections; // area of proections
    //private CoordAxis _proectAxis; // axis about which will generate a cover
    private Prism3d _prismCover; // prism with generated points around face
    private Cover _cover; // cover around face
    private WETriangleMesh _mesh;
    private String _id = new String();
    
    public FaceCover(ColorFace face_body, double thickness_facecover, BodyDimension dim, int start_def, String id) throws ExDegeneration {
        _face = face_body;
        _idxOfPoint = start_def;
        _thicknessCover = thickness_facecover;
        _dimension = dim;
        _cover = new Cover();
       // _areaFaceProections = new HashMap<>();
       // CalcFaceProectionsOnCoordPlaces();
       // DetermineAxisOfProecting();
       _id = id;
    }

    /**
     * Calculate face projections on every coordinate planes, if they exist
     */
   /* private void CalcFaceProectionsOnCoordPlaces() throws ExDegeneration {
        _faceProections = new HashMap<>();
        ArrayList<Vect3d> proection_points = new ArrayList<>();
        proection_points = _face.getFace().getProectionPointsOnCoordPlane(CoordPlanes.OXY);
        if (!Checker.isContainEqualPoints(proection_points))
            if (!Checker.lieOnOneLine(proection_points)) // trying to add proection of face on coordinate plane OXY
                try {
                    _faceProections.put(CoordPlanes.OXY, new Polygon3d(proection_points));
                }
                catch (ExGeom | ExZeroDivision ex) {
                    ex.printStackTrace();
                }
        proection_points = _face.getFace().getProectionPointsOnCoordPlane(CoordPlanes.OXZ);
        if (!Checker.isContainEqualPoints(proection_points))
            if (!Checker.lieOnOneLine(proection_points)) // trying to add proection of face on coordinate plane OXY
                try {
                    _faceProections.put(CoordPlanes.OXZ, new Polygon3d(proection_points));
                }
                catch (ExGeom | ExZeroDivision ex) {
                    ex.printStackTrace();
                }
        proection_points = _face.getFace().getProectionPointsOnCoordPlane(CoordPlanes.OYZ);
        if (!Checker.isContainEqualPoints(proection_points))
            if (!Checker.lieOnOneLine(proection_points)) // trying to add proection of face on coordinate plane OXY
                try {
                    _faceProections.put(CoordPlanes.OYZ, new Polygon3d(proection_points));
                }
                catch (ExGeom | ExZeroDivision ex) {
                    ex.printStackTrace();
                }
    }
*/
    /**
     * Calculate areas of existing face's proections on coordinate planes
     */
  /*  private void CalcAreasOfFaceProections() {
        for (Map.Entry<CoordPlanes, Polygon3d> entrykey: _faceProections.entrySet()) {
            CoordPlanes key_plane = entrykey.getKey();
            Polygon3d cur_polygon = _faceProections.get(entrykey.getKey());
            _areaFaceProections.put(key_plane, cur_polygon.AreaOfConvexPolygon());
        }
    } */

    /**
     * Determine coordinate axis of proecting with appropriate maximal face's proection
     */
  /*  private void DetermineAxisOfProecting() {
        CalcAreasOfFaceProections();
        CoordPlanes plane = CoordPlanes.OXY;
        Double max_area = 0.0;
        for (Map.Entry<CoordPlanes, Double> entrykey: _areaFaceProections.entrySet()) {
            CoordPlanes key_plane = entrykey.getKey();
            Double cur_area = _areaFaceProections.get(key_plane);
            if (cur_area > max_area) {
                max_area = cur_area;
                plane = key_plane;
            }
        }
        _proectAxis = plane.DeterminePerpendicularCoordAxis();
    } */

    /**
     * Build special prism around face.
     * The buildings depends on coordinate axis of proecting with maximal face's proection.
     * This axis determines what's the dimension contain the thickness of special prism.
     * Around this prism will be generated covers
     */
    private void BuildPrismCoverAroundFace() {
        ArrayList<Vect3d> base_prism = new ArrayList<>();
        ArrayList<Vect3d> face_points = _face.getFace().points();
        // build bottom base of special prism
        Vect3d normal = new Vect3d();
        try {
            normal = _face.getFace().plane().n();
        }
        catch (ExDegeneration e) {
        }
        normal.inc_mul(_thicknessCover);
        for (int i=0; i<face_points.size(); i++) {
            Vect3d face_vertex = face_points.get(i);
            /*double x_coor = face_vertex.x();
            double y_coor = face_vertex.y();
            double z_coor = face_vertex.z();
            switch (_proectAxis) {
                case X:
                    x_coor -= _thicknessCover;
                    break;
                case Y:
                    y_coor -= _thicknessCover;
                    break;
                case Z:
                    z_coor -= _thicknessCover;
                    break;
                default: break;
            } */
            base_prism.add(Vect3d.sum(face_vertex, normal));
        }
        // build one of the vertices from top base of special prism
        /*double x_top = face_points.get(0).x();
        double y_top = face_points.get(0).y();
        double z_top = face_points.get(0).z();
        switch (_proectAxis) {
            case X:
                x_top += _thicknessCover;
                break;
            case Y:
                y_top += _thicknessCover;
                break;
            case Z:
                z_top += _thicknessCover;
                break;
            default: break;
        } */

        Vect3d top_vertex = face_points.get(0).sub(normal);
        try { // trying to build special prism cover
            Polygon3d base = new Polygon3d(base_prism);
            _prismCover = new Prism3d(top_vertex, base);
        }
        catch (ExGeom ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.println(ex.getMessage());
          }
        }
    }

    @Override
    public void buildPointCover() {
        _cover.addPoints(_prismCover.points(), _face.getColor());
    }

    @Override
    public void buildTriangularCover() {
        try {
            Polygon3d[] bases_prismcover = _prismCover.base();
            int start_def = _idxOfPoint+bases_prismcover[1].points().size();
            if (_dimension == BodyDimension.PLANE) {
                _cover.addTriangleFaceOnPolygon(bases_prismcover[0].points(), Orientation.RIGHT, _idxOfPoint);
                _cover.addTriangleFaceOnPrismLatSurf(_prismCover.points(), Orientation.LEFT, _idxOfPoint);
                _cover.addTriangleFaceOnPolygon(bases_prismcover[1].points(), Orientation.LEFT, start_def);
            }
            else {
                _cover.addTriangleFaceOnPolygon(bases_prismcover[0].points(), Orientation.RIGHT, _idxOfPoint);
                _cover.addTriangleFaceOnPrismLatSurf(_prismCover.points(), Orientation.LEFT, _idxOfPoint);
                _cover.addTriangleFaceOnPolygon(bases_prismcover[1].points(), Orientation.LEFT, start_def);
            }

        }
        catch (ExGeom ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.println(ex.getMessage());
          }
        }

    }

    @Override
    public void sculptModels() {
        BuildPrismCoverAroundFace();
        buildPointCover();
        buildTriangularCover();
    }

    @Override
    public Cover getCover() {
        return _cover;
    }

    @Override
    public CoverType type() {
        return CoverType.FACE;
    }

    @Override
    public i_Geom getGeom() {
        return _face.getFace();
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
        ArrayList<Vect3d> intersect = _face.getFace().intersect(ray);
        Vect3d result = intersect.get(0).sub(ren.getCameraPosition().eye());
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
