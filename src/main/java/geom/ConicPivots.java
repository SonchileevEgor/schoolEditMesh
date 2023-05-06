package geom;

import java.util.ArrayList;

/**
 *
 * @author Ivan
 */
public class ConicPivots implements i_Geom {

  //coefficients of x and y in the equation of the second order curve on the plane OXY
//  private ArrayList<Double> _coef;
  private double[] _coef;
  
  private NewSystemOfCoor _conicNS;
  
  //five starting points
  private ArrayList<Vect3d> _startingPoints = new ArrayList<>();
  
  //type of conic
  private GeomType _type;
  
  private ArrayList<Vect3d> _pivots = new ArrayList<>();
  //reference point on the curve where the direct _pivots(0) and _pivots(1) two reference 
  //points on the line. For a couple of lines _pivots(0) and _pivots(1) support point of the
  //first straight line, and _pivots(2) and _pivots(3) reference point of the second line.
  //For the parabola _pivots(0) first point on derektrise, the second point _pivots(1) on derektrise,
  //_pivots(2) focus. For the first focus of the hyperbola _pivots(0), _pivots(1) of the second focus point _pivots(2) 
  //on hyperbole. For the first focus of the ellipse _pivots(0), _pivots(1) of the second focus, _pivots(2) point 
  //on the ellipse.For the circle _pivots(0), _pivots(1), _pivots(2) three points of the circle

  /**
   * Designer conic five points
   *
   * @param t1
   * @param t2
   * @param t3
   * @param t4
   * @param t5
   * @throws ExDegeneration
   */
  public ConicPivots(Vect3d t1, Vect3d t2, Vect3d t3, Vect3d t4, Vect3d t5) throws ExDegeneration {
    _startingPoints.add(t1);
    _startingPoints.add(t2);
    _startingPoints.add(t3);
    _startingPoints.add(t4);
    _startingPoints.add(t5);
    if (Checker.isCoplanar(_startingPoints)) {
      if (!Checker.constainsEqualPoints(_startingPoints)) {
        if (Checker.lieOnOneLine(_startingPoints)) {
          _type = GeomType.LINE3D;
          _pivots.add(t1);
          _pivots.add(t2);
        } else if (Checker.atLeast3PointsOneline(_startingPoints)) {
          if (Checker.threePointsOnTheLine(t1, t2, t3)) {
            if ((!Checker.threePointsOnTheLine(t1, t2, t4)) &&
                (!Checker.threePointsOnTheLine(t1, t2, t5))) {
              _type = GeomType.PAIROFLINES3D;
              _pivots.add(t1);
              _pivots.add(t3);
              _pivots.add(t4);
              _pivots.add(t5);
            } else {
              throw new ExDegeneration("Только четыре точки коники не должны лежать на одной прямой");
            }
          } else if ((!Checker.fourPointsOnOneLine(t1, t2, t4, t5))
                  & (!Checker.fourPointsOnOneLine(t2, t3, t4, t5))
                  & (!Checker.fourPointsOnOneLine(t1, t2, t4, t5))) {
            _type = GeomType.PAIROFLINES3D;
            if (Checker.threePointsOnTheLine(t1, t2, t4)) {
              _pivots.add(t1);
              _pivots.add(t4);
              _pivots.add(t3);
              _pivots.add(t5);
            }
            if (Checker.threePointsOnTheLine(t1, t2, t5)) {
              _pivots.add(t1);
              _pivots.add(t5);
              _pivots.add(t4);
              _pivots.add(t3);
            }
            if (Checker.threePointsOnTheLine(t1, t3, t4)) {
              _pivots.add(t1);
              _pivots.add(t3);
              _pivots.add(t2);
              _pivots.add(t5);
            }
            if (Checker.threePointsOnTheLine(t1, t3, t5)) {
              _pivots.add(t1);
              _pivots.add(t3);
              _pivots.add(t2);
              _pivots.add(t4);
            }
            if (Checker.threePointsOnTheLine(t2, t3, t4)) {
              _pivots.add(t2);
              _pivots.add(t3);
              _pivots.add(t1);
              _pivots.add(t5);
            }
            if (Checker.threePointsOnTheLine(t2, t3, t5)) {
              _pivots.add(t2);
              _pivots.add(t3);
              _pivots.add(t1);
              _pivots.add(t4);
            }
            if (Checker.threePointsOnTheLine(t1, t4, t5)) {
              _pivots.add(t1);
              _pivots.add(t4);
              _pivots.add(t2);
              _pivots.add(t3);
            }
            if (Checker.threePointsOnTheLine(t2, t4, t5)) {
              _pivots.add(t2);
              _pivots.add(t4);
              _pivots.add(t1);
              _pivots.add(t3);
            }
            if (Checker.threePointsOnTheLine(t3, t4, t5)) {
              _pivots.add(t3);
              _pivots.add(t4);
              _pivots.add(t1);
              _pivots.add(t2);
            }
          } else {
            throw new ExDegeneration("Только четыре точки коники не должны лежать на одной прямой");
          }
        } else {
          _conicNS = new NewSystemOfCoor(t1, Vect3d.sub(t2, t1), Vect3d.sub(t3, t1));
          try {
            generalEquationOfTheSecondOrderOnPlaneOXY();
          } catch( ExZeroDivision ex ){
            throw new ExDegeneration(ex.getMessage());
          }
          _type = setType();
          ArrayList<Vect3d> pivots = referencePointInTheCanonicalCoordinateSystem();
          NewSystemOfCoor conicNS1 = new NewSystemOfCoor(newCenter(), newAxis().get(0), newAxis().get(1));
          _pivots.add(_conicNS.oldCoor(conicNS1.oldCoor(pivots.get(0))));
          _pivots.add(_conicNS.oldCoor(conicNS1.oldCoor(pivots.get(1))));
          _pivots.add(_conicNS.oldCoor(conicNS1.oldCoor(pivots.get(2))));
        }
      } else {
        throw new ExDegeneration("Никакие две точки коники не должны совпадать");
      }
    } else {
      throw new ExDegeneration("Пять точек коники должны лежать в одной плоскости");
    }
  }

  /**
   * It is the general equation of the second order curve coefficients on the
   * plane
   *
   * Finds the coefficients as an array, where the first member(a11)
   * of the array is equal to the odds on for x ^ 2, the second(a12) for x * y,
   * while the third(a22) y ^ 2, the fourth(b1) for x, fifth(b2) at y, the
   * sixth(c)-free term.
   */
  public final void generalEquationOfTheSecondOrderOnPlaneOXY() throws ExZeroDivision {
    ArrayList<Vect3d> points = new ArrayList();
    points.add(_conicNS.newCoor(_startingPoints.get(0)));
    points.add(_conicNS.newCoor(_startingPoints.get(1)));
    points.add(_conicNS.newCoor(_startingPoints.get(2)));
    points.add(_conicNS.newCoor(_startingPoints.get(3)));
    points.add(_conicNS.newCoor(_startingPoints.get(4)));
    //при помощи общего уравнения кривых второго порядка составляем матрицу (систему уравнений)
    double[][] matr = new double[3][3];
    for (int i = 2; i < 5; i++) {
      matr[i - 2][0] = 2 * points.get(i).x() * points.get(i).y();
      matr[i - 2][1] = Math.pow(points.get(i).y(), 2);
      matr[i - 2][2] = 2 * points.get(i).y();
    }
    Matrix3 m = new Matrix3(matr);
    //находим коэффициенты уравнения 
    Vect3d coef = m.solve(points.get(2).x() * (points.get(1).x() - points.get(2).x()),
                             points.get(3).x() * (points.get(1).x() - points.get(3).x()),
                             points.get(4).x() * (points.get(1).x() - points.get(4).x()));
    _coef = new double[]{
      1.0, coef.x(), coef.y(), -points.get(1).x() / 2, coef.z(), 0.0
    };
  }

  /**
   * finds parameters of the parabola, hyperbola, ellipse and a circle in the
   * canonical form
   *
   * @return the parameters curves(parabola, hyperbola, ellipse, circle) in the
   * canonical having seen an array, for parabola - p, for hyperbola-realAxis,
   * imaginaryAxis, respectively, for ellipse - bigAxis, smallAxis respectively,
   * for the circle-radius.
   */
  public final ArrayList<Vect3d> referencePointInTheCanonicalCoordinateSystem() throws ExDegeneration {
    ArrayList<Vect3d> pivots = new ArrayList<>();
    if (_type == GeomType.ELLIPSE3D) {
      double bigAxle = Math.sqrt(-det33() / (a().get(0) * det22()));
      double smallAxle = Math.sqrt(-det33() / (a().get(1) * det22()));
      double c = Math.sqrt(bigAxle * bigAxle - smallAxle * smallAxle);
      pivots.add(new Vect3d(-c, 0, 0));
      pivots.add(new Vect3d(c, 0, 0));
      pivots.add(new Vect3d(0, smallAxle, 0));
    }
    if ((_type == GeomType.CIRCLE3D)) {
      double radius = Math.sqrt(-det33() / (a().get(0) * det22()));
      pivots.add(new Vect3d(0, radius, 0));
      pivots.add(new Vect3d(-radius, 0, 0));
      pivots.add(new Vect3d(radius, 0, 0));
    }
    if (_type == GeomType.HYPERBOLE3D) {
      double realAxle = Math.sqrt(-det33() / (a().get(0) * det22()));
      double imaginaryAxle = Math.sqrt(det33() / (a().get(1) * det22()));
      double c = Math.sqrt(realAxle * realAxle + imaginaryAxle * imaginaryAxle);
      pivots.add(new Vect3d(-c, 0, 0));
      pivots.add(new Vect3d(c, 0, 0));
      pivots.add(new Vect3d(c, (imaginaryAxle * imaginaryAxle) / realAxle, 0));
    }
    if (_type == GeomType.PARABOLA3D) {
      double p = Math.sqrt(-det33() / Math.pow(r(), 3));
      pivots.add(new Vect3d(-p / 2, 1, 0));
      pivots.add(new Vect3d(-p / 2, -2, 0));
      pivots.add(new Vect3d(p / 2, 0, 0));
    }
    return pivots;
  }

  /**
   * It is a vector parallel to the transfer, which is necessary to move the
   * curve of the second order in the canonical form
   *
   * @return
   * @throws geom.ExDegeneration
   */
  public Vect3d newCenter() throws ExDegeneration {
    Vect3d vect;
    if (_type == GeomType.PARABOLA3D) {
      double m = _coef[3] * newAxis().get(0).getNormalized().x() + _coef[4] * newAxis().get(0).getNormalized().y();
      double a = a().get(1) * newAxis().get(1).x();
      double b = a().get(1) * newAxis().get(1).y();
      double c = newAxis().get(1).x() * _coef[3] + newAxis().get(1).y() * _coef[4];
      double d = (_coef[3] + m * newAxis().get(0).getNormalized().x()) / (_coef[4] + m * newAxis().get(0).getNormalized().y());
      vect = new Vect3d(c / (b * d - a), -d * c / (b * d - a), 0);

    } else {
      vect = new Vect3d((_coef[2] * _coef[3] - _coef[1] * _coef[4])
              / (_coef[1] * _coef[1] - _coef[0] * _coef[2]),
              (_coef[0] * _coef[4] - _coef[1] * _coef[3])
              / (_coef[1] * _coef[1] - _coef[0] * _coef[2]), 0);
    }
    return vect;
  }

  /**
   * @return new axis OX and OY in which the conic (parabola, hyperbola,
   * ellipse, circle) has the canonical form
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> newAxis() throws ExDegeneration {
    Vect3d OX = Vect3d.UX;
    Vect3d OY = Vect3d.UY;
    if (!Checker.isEqual(a().get(0), a().get(1))) {
      if (_type == GeomType.PARABOLA3D) {
        OX = new Vect3d(-_coef[1], _coef[0], 0);
        OY = new Vect3d(_coef[0], _coef[1], 0);
        if (r() * (OX.x() * _coef[3] + OX.y() * _coef[4]) > 0) {
          OX = new Vect3d(_coef[1], -_coef[0], 0);
        }
      } else if (Checker.isEqual(_coef[1], 0)) {
        if (_coef[0] * _coef[2] > 0) {
          if (Math.abs(_coef[0]) < Math.abs(_coef[2])) {
            OX = new Vect3d(1, 0, 0);
            OY = new Vect3d(0, 1, 0);
          } else {
            OX = new Vect3d(0, 1, 0);
            OY = new Vect3d(1, 0, 0);
          }
        }
        if (_coef[0] * _coef[2] < 0) {
          if (_coef[3] * _coef[3] * _coef[2] + _coef[4] * _coef[4] * _coef[0] < 0) {
            OX = new Vect3d(1, 0, 0);
            OY = new Vect3d(0, 1, 0);
          } else {
            OX = new Vect3d(0, 1, 0);
            OY = new Vect3d(1, 0, 0);
          }
        }
      } else {
        OX = new Vect3d(1, (a().get(0) - _coef[0]) / _coef[1], 0);
        OY = new Vect3d(1, (a().get(1) - _coef[0]) / _coef[1], 0);
      }
    }
    ArrayList<Vect3d> newAxis = new ArrayList<>();
    newAxis.add(OX);
    newAxis.add(OY);
    return newAxis;
  }

  /**
   * Specifies the shape of the curve (parabola, hyperbola, ellipse, circle) on
   * the five-point
   *
   * @return GeomType
   * @throws ExDegeneration
   */
  public final GeomType setType() throws ExDegeneration {
    GeomType type = null;
    if (Checker.isEqual(det22(), 0)) {
      type = GeomType.PARABOLA3D;
    } else {
      if (det22() > 0) {
        if (r() * det33() < 0) {
          type = GeomType.ELLIPSE3D;
        }
        if (Checker.isEqual(r() * r() - 4 * det22(), 0)) {
          type = GeomType.CIRCLE3D;
        }
      }
      if (det22() < 0) {
        type = GeomType.HYPERBOLE3D;
      }
    }
    return type;
  }

  /**
   *
   * @return coef general equation second order on plane OXY
   */
  public double[] coef() {
    return _coef;
  }

  public NewSystemOfCoor conicaNS() {
    return _conicNS;
  }

  private double det33() {
    return Det23.calc(_coef[0], _coef[1], _coef[3],
                      _coef[1], _coef[2], _coef[4],
                      _coef[3], _coef[4], 0);
  }

  private double det22() {
    return Det23.calc(_coef[0], _coef[1],
                      _coef[1], _coef[2]);
  }

  private double r() {
    return _coef[0] + _coef[2];
  }

  private ArrayList<Double> a() throws ExDegeneration {
    double a1 = (r() + Math.sqrt(r() * r() - 4 * det22())) / 2;
    double a2 = (r() - Math.sqrt(r() * r() - 4 * det22())) / 2;
    double k;
    if (_type == GeomType.ELLIPSE3D) {
      if (Math.abs(a1) >= Math.abs(a2)) {
        k = a1;
        a1 = a2;
        a2 = k;
      }
    }
    if (_type == GeomType.HYPERBOLE3D) {
      if (Checker.isEqual(det33(), 0)) {
        if (a1 < 0) {
          k = a1;
          a1 = a2;
          a2 = k;
        }
      } else if (det33() * a1 < 0) {
        k = a1;
        a1 = a2;
        a2 = k;
      }
    }
    if (_type == GeomType.PARABOLA3D) {
      if (!Checker.isEqual(a1, 0)) {
        k = a1;
        a1 = a2;
        a2 = k;
      }
    }
    ArrayList<Double> a = new ArrayList<>();
    a.add(a1);
    a.add(a2);
    return a;
  }

  /**
   *
   * @return pivots of conic
   */
  public ArrayList<Vect3d> pivots() {
    return _pivots;
  }

  /**
   *
   * @return GeomType
   */
  @Override
  public GeomType type() {
    return _type;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return new ArrayList<>(_startingPoints);
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new ConicPivots(_startingPoints.get(0),
                             _startingPoints.get(1),
                             _startingPoints.get(2),
                             _startingPoints.get(3),
                             _startingPoints.get(4));
    } catch( ExDegeneration ex ){
      return null;
    }
  }
}
