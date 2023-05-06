package maquettes;


import geom.*;

import java.util.ArrayList;

public class EarClipping {
  private ArrayList<Triang3d> _tr = new ArrayList<>();
  private ArrayList<Vect3d> _points = new ArrayList<>();

  public EarClipping(ArrayList<Vect3d> points){
    _points = points;
  }

  public ArrayList<Triang3d> triangulate() throws ExDegeneration, ExZeroDivision {
    if (_tr.size() > 0)
      return _tr;

    ArrayList<Vect3d> points = (ArrayList<Vect3d>)_points.clone();
    ArrayList<Angle3d> angles = calculateAngles(points);
    while (_tr.size() < _points.size() - 2) {
      int index = 0;
      for (int j=1; j < angles.size(); j++)
        if (angles.get(index).value() > angles.get(j).value())
          index = j;

      Angle3d ang = angles.get(index);
      if (ang.value() < Math.PI) {
        int indexF = index - 1;
        int indexL = index + 1;
        if (index == 0)
          indexF = points.size() - 1;
        else if (index == points.size() - 1)
          indexL = 0;
        Vect3d p1 = points.get(indexF);
        Vect3d p2 = points.get(index);
        Vect3d p3 = points.get(indexL);
        Triang3d tr = new Triang3d(p1, p2, p3);
        boolean contain = false;
        for (int i = 0; i < _points.size(); i++) {
          Vect3d curPoint = _points.get(i);
          if ((curPoint != p1 && curPoint != p2 && curPoint != p3) && (tr.contains(curPoint))) {
            contain = true;
            break;
          }
        }
        if (!contain) {
          _tr.add(tr);
          points.remove(index);
          angles = calculateAngles(points);
        }
      }
    }
    return _tr;
  }

  public static ArrayList<Angle3d> calculateAngles(ArrayList<Vect3d> points) {
    ArrayList<Angle3d> angles = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      int iF = i - 1;
      int iL = i + 1;
      if (i == 0)
        iF = points.size() - 1;
      else if (i == points.size() - 1)
        iL = 0;
      angles.add(new Angle3d(points.get(iF), points.get(i), points.get(iL)));
    }
    return angles;
  }
}
