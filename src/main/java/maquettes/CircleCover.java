package maquettes;

import static config.Config.MAQUETTE_RIB_PHASES;
import editor.i_Body;
import geom.*;
import java.util.ArrayList;
import geom.Orientation;
import opengl.Render;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 *
 * @author Kurgansky
 */
public class CircleCover implements i_GeneratedCover, i_Model, i_Cover {
  private boolean _choosen = false;
  private ColorCircle _circle;
  private double _eps;
  private int _idxOfPoint;
  private Cover _cover;
  private double _angle;
  private i_Body _body;
  private WETriangleMesh _mesh;

  public CircleCover(i_Body body, ColorCircle input_circle, double eps, int start_def) {
        _body = body;
        _circle = input_circle;
        _eps = eps;
        _idxOfPoint = start_def;
        _cover = new Cover();
  }

  @Override
  public void buildPointCover() {
    double delta = Math.PI * 2 / 300; //step of partition
    _angle = delta;

    ArrayList<Vect3d> gen_points = new ArrayList<>();
    Vect3d norm = _circle.getCircle().normal();
    norm.inc_mul(_eps);
    Vect3d res = Vect3d.sum(norm, Vect3d.sub(_circle.getCircle().center(), _circle.getCircle().getPointByPolar(0, _circle.getCircle().radiusLength())));
    Vect3d rad_vect = Vect3d.sub(res, Vect3d.sub(_circle.getCircle().center(), _circle.getCircle().getPointByPolar(0, _circle.getCircle().radiusLength())));

    res = Vect3d.sum(_circle.getCircle().getPointByPolar(0, _circle.getCircle().radiusLength()), rad_vect);
    gen_points.add(res);

    Plane3d pl = Plane3d.oXY();
      try {
        pl = new Plane3d(_circle.getCircle().center(),_circle.getCircle().getPointByPolar(0, _circle.getCircle().radiusLength()), res);
      } catch (ExDegeneration ex) {}

    rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());

     while (!Vect3d.sum(_circle.getCircle().getPointByPolar(0, _circle.getCircle().radiusLength()), rad_vect).equals(res))
     {
       gen_points.add(Vect3d.sum(_circle.getCircle().getPointByPolar(0, _circle.getCircle().radiusLength()), rad_vect));
       rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
     }
    _cover.addPoints(gen_points, _circle.getColor());

    while (_angle < Math.PI * 2){
        Vect3d tmp = new Vect3d(_circle.getCircle().getPointByPolar(_angle, _circle.getCircle().radiusLength())); //точка на дуге

        gen_points = new ArrayList<>();

        res = Vect3d.sum(norm, Vect3d.sub(_circle.getCircle().center(), tmp));
        rad_vect = Vect3d.sub(res, Vect3d.sub(_circle.getCircle().center(), tmp));

        res = Vect3d.sum(tmp, rad_vect);
        gen_points.add(Vect3d.sum(tmp, rad_vect));
          try {
            pl = new Plane3d(_circle.getCircle().center(), tmp, res);
          } catch (ExDegeneration ex) {}
        rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
         while (!Vect3d.sum(tmp, rad_vect).equals(res))
         {
           gen_points.add(Vect3d.sum(tmp, rad_vect));
           rad_vect = rad_vect.rotate((Math.PI*2) / MAQUETTE_RIB_PHASES.value(), pl.n());
         }

        _cover.addPoints(gen_points, _circle.getColor());
        _angle += delta;
    }
  }

  @Override
  public void buildTriangularCover() {
    ArrayList<ColorPoint> points_cover = _cover.getPointCover();
        int last = points_cover.size()/MAQUETTE_RIB_PHASES.value() - 1;
        int start_def;
        for (int i=0; i<last; i++) {
            start_def = _idxOfPoint+i*(MAQUETTE_RIB_PHASES.value());
            Orientation bypass = Orientation.LEFT;
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
        return CoverType.CIRCLE;
    }

    @Override
    public i_Geom getGeom() {
        return _circle.getCircle();
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