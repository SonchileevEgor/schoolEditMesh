package maquettes;

import static config.Config.MAQUETTE_RIB_PHASES;
import editor.i_Body;
import geom.ExDegeneration;
import geom.Orientation;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 *
 * @author Kurgansky
 */
public class ArcCover implements  i_GeneratedCover, i_Model, i_Cover  {
    private boolean _choosen = false;
    private ColorArc _arc; // arc around which must generate a cover
    private double _eps; // radius of the neighborhood around which points of the cover will be generated
    private int _idxOfPoint; // number of point, at which it can to describe face definitions
    private Cover _cover; // generated points from cover
    private double _angle; //
    private final i_Body _body;
    private WETriangleMesh _mesh;
    
  public ArcCover(i_Body body, ColorArc input_arc, double eps, int start_def) {
        _arc = input_arc;
        _eps = eps;
        _idxOfPoint = start_def;
        _cover = new Cover();
        _body = body;
  }

  @Override
  public void buildPointCover() {
    double delta = _arc.getArc().value() / 300; //step of partition
    _angle = delta;

    ArrayList<Vect3d> gen_points = new ArrayList<>();
    Vect3d norm = _arc.getArc().normal();
    norm.inc_mul(_eps);
    Vect3d res = Vect3d.sum(norm, Vect3d.sub(_arc.getArc().center(), _arc.getArc().vert1()));
    Vect3d rad_vect = Vect3d.sub(res, Vect3d.sub(_arc.getArc().center(), _arc.getArc().vert1()));

    res = Vect3d.sum(_arc.getArc().vert1(), rad_vect);
    gen_points.add(res);

    Plane3d pl = Plane3d.oXY();
      try {
        pl = new Plane3d(_arc.getArc().center(), _arc.getArc().vert1(), res);
      } catch (ExDegeneration ex) {}

    rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());

     while (!Vect3d.sum(_arc.getArc().vert1(), rad_vect).equals(res))
     {
       gen_points.add(Vect3d.sum(_arc.getArc().vert1(), rad_vect));
       rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
     }
    _cover.addPoints(gen_points, _arc.getColor());

    while (_angle < _arc.getArc().value()){
        Vect3d tmp = new Vect3d(_arc.getArc().getPointByPolar(_angle)); //точка на дуге
        if (tmp.equals(_arc.getArc().vert1()) || tmp.equals(_arc.getArc().vert2())) break;

        gen_points = new ArrayList<>();

        res = Vect3d.sum(norm, Vect3d.sub(_arc.getArc().center(), tmp));
        rad_vect = Vect3d.sub(res, Vect3d.sub(_arc.getArc().center(), tmp));

        res = Vect3d.sum(tmp, rad_vect);
        gen_points.add(Vect3d.sum(tmp, rad_vect));
          try {
            pl = new Plane3d(_arc.getArc().center(), tmp, res);
          } catch (ExDegeneration ex) {}
        rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
         while (!Vect3d.sum(tmp, rad_vect).equals(res))
         {
           gen_points.add(Vect3d.sum(tmp, rad_vect));
           rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
         }

        _cover.addPoints(gen_points, _arc.getColor());
        _angle += delta;
    }

    Vect3d tmp = new Vect3d(_arc.getArc().getPointByPolar(_angle));
    gen_points = new ArrayList<>();

        res = Vect3d.sum(norm, Vect3d.sub(_arc.getArc().center(), tmp));
        rad_vect = Vect3d.sub(res, Vect3d.sub(_arc.getArc().center(), tmp));

        res = Vect3d.sum(tmp, rad_vect);
        gen_points.add(Vect3d.sum(tmp, rad_vect));
          try {
            pl = new Plane3d(_arc.getArc().center(), tmp, res);
          } catch (ExDegeneration ex) {}
        rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
         while (!Vect3d.sum(tmp, rad_vect).equals(res))
         {
           gen_points.add(Vect3d.sum(tmp, rad_vect));
           rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
         }
         _cover.addPoints(gen_points, _arc.getColor());
  }

  @Override
  public void buildTriangularCover() {
    ArrayList<ColorPoint> points_cover = _cover.getPointCover();
        int last = points_cover.size()/MAQUETTE_RIB_PHASES.value() - 1;
        int start_def;
        for (int i=0; i<last; i++) {
            start_def = _idxOfPoint+i*(MAQUETTE_RIB_PHASES.value());
            Orientation bypass = Orientation.LEFT;
            if (i == 0 ||  i == last-1) { // first or last index
                ArrayList<Vect3d> basepoints = new ArrayList<>(); // base of rib, which contains triangles of 3d-model
                int begin, end;
                if (i ==0) {
                    begin = 0;
                    end = MAQUETTE_RIB_PHASES.value()-2;
                }
                else {
                    begin = MAQUETTE_RIB_PHASES.value();
                    end = 2*MAQUETTE_RIB_PHASES.value()-2;
                }
                for (int j = begin; j<=end+1; j++)
                    basepoints.add(_cover.getPointCover().get(MAQUETTE_RIB_PHASES.value()*i+j).getPoint());
                if (i == last-1)
                    _cover.addTriangleFaceOnPolygon(basepoints, bypass, start_def + begin);
                else
                    _cover.addTriangleFaceOnPolygon(basepoints, bypass.change(), start_def);
            }
            ArrayList<Vect3d> points = new ArrayList<>(); // lateral surfaces of rib, which contains triangles of 3d-model
            for (int j = 0; j<2*(MAQUETTE_RIB_PHASES.value()); j++)
                points.add(_cover.getPointCover().get((MAQUETTE_RIB_PHASES.value())*i+j).getPoint());
            _cover.addTriangleFaceOnPrismLatSurf(points, bypass, start_def);
        }
  }

  @Override
  public void sculptModels() {
    buildPointCover();
    buildTriangularCover();
  }

  @Override
  public Cover getCover() {
    return _cover;
  }

    @Override
    public CoverType type() {
        return CoverType.ARC;
    }

    @Override
    public i_Geom getGeom() {
        return _arc.getArc();
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
        result.inc_mul(_eps);
        return (result);
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
