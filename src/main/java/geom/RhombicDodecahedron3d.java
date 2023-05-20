package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;

/**
 *
 * @author ???
 */
public class RhombicDodecahedron3d implements i_Geom {
  private double _angle;
  private Vect3d _a, _b, _c, _d, _a1, _b1, _c1, _d1, _e, _f, _g, _h, _i, _j;

  public static void print(Vect3d a) {
    System.out.println("x - " + a.x() + " , y - " + a.y() + " , z - " + a.z());
  }
  /**
  * Constructor of RhombicDodecahedron by two vertices and angle
  * @param a 1st vertex
  * @param b 2nd vertex
  * @param angle rotate angle
  * @throws ExGeom
  */
  public RhombicDodecahedron3d(Vect3d a, Vect3d b, double angle) throws ExGeom {
    _a = a;
    _b = b;
    _angle = angle;
    /*отношение большей диагонали к меньшей равен корень из 2 */
    double bigDiagonal = 2 * Vect3d.dist(a, b) * Math.sqrt(2) / Math.sqrt(3); // большая диагональ ромба
    double smallDiagonal = 2 * Vect3d.dist(a, b) / Math.sqrt(3); // маленькая диагональ ромба
    double heightRomb = 2 * ((bigDiagonal * smallDiagonal) / (4 * Vect3d.dist(a, b))); // высота ромба, 2 * радиус вписанной окр
    Polygon3d bottomBase = Polygon3d.regPolygonByTwoPoints(a, b, 4, angle); // ромб будем получать из квадрата
    /*получаем нормаль для того чтобы d и c приблизить к a и b соответственно*/
    Vect3d tmpNormal = Vect3d.sub(bottomBase.points().get(3), a).getNormalized();
    // двигаем ближе
    Vect3d _dtmp = Vect3d.sum(a, tmpNormal.mul(heightRomb));
    Vect3d _ctmp = Vect3d.sum(b, tmpNormal.mul(heightRomb));
    /*считаем на сколько надо сдвигать вправо эти точки по т.Пифагора*/
    double tmp1 = Math.pow(Vect3d.dist(a, b), 2) - Math.pow(heightRomb, 2);
    double shift = Math.sqrt(tmp1);
    Vect3d tmpNormalForShift = Vect3d.sub(b, a).getNormalized(); // вектор нормали для сдвига вправо
    // сдвигаем
    _d = Vect3d.sum(_dtmp, tmpNormalForShift.mul(shift));
    _c = Vect3d.sum(_ctmp, tmpNormalForShift.mul(shift));

    // подднимаем точнки нижнего основания
    tmpNormal = (new Polygon3d(_a, _b, _c, _d)).inCircle().normal().getNormalized();
    _a1 = Vect3d.sum(_a.duplicate(), tmpNormal.mul(-bigDiagonal));
    _b1 = Vect3d.sum(_b.duplicate(), tmpNormal.mul(-bigDiagonal));
    _c1 = Vect3d.sum(_c.duplicate(), tmpNormal.mul(-bigDiagonal));
    _d1 = Vect3d.sum(_d.duplicate(), tmpNormal.mul(-bigDiagonal));

    // будем получать точки боковых ромбов
    tmpNormalForShift = Vect3d.sub(_b, _d).getNormalized();
    // получим среднюю точку между c и c1
    Vect3d tmpPoint = Vect3d.sum(_c, _c1).mul(0.5);
    // от средней точки между c и c1 шифтуем влево вправо
    double tmp = smallDiagonal / 2;
    _f = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(-tmp));
    _e = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(tmp));
    // получим среднюю точку между a и a1
    tmpPoint = Vect3d.sum(_a, _a1).mul(0.5);
    // шифтуем от нее влево вправо
    _h = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(-tmp));
    _i = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(tmp));

    //найдем среднюю точку между _f и _d1
    tmpPoint = Vect3d.sum(_f, _d1).mul(0.5);
    tmpNormal = Vect3d.sub(tmpPoint, _c1).getNormalized();
    _g = Vect3d.sum(_c1.duplicate(), tmpNormal.mul(bigDiagonal));
    // найдем среднюю точку между b и i
    Vect3d tmpPoint1 = Vect3d.sum(_e, _b1).mul(0.5);
    Vect3d tmpNormal1 = Vect3d.sub(tmpPoint1, _c1).getNormalized();
    _j = Vect3d.sum(_c1.duplicate(), tmpNormal1.mul(bigDiagonal));
  }

 /**
  * Constructor of RhombicDodecahedron by two vertices and 1 point of plane
  * @param a 1st vertex
  * @param b 2nd vertex
  * @param c point of plane
  * @throws ExGeom
  */
  public RhombicDodecahedron3d(Vect3d a, Vect3d b, Vect3d c) throws ExGeom {
    _a = a;
    _b = b;
    //Острый угол каждого ромба (грани) ромбододекаэдра
    double angle = Math.acos(1.0 / 3.0);
    //Плоскость из данных точек необходима для нахождения вектора нормали в искомой грани
    Plane3d basePlane = new Plane3d(a, b ,c);
    _c = _b.sub(_a).rotate(angle, basePlane.n()).add(_a);
    Vect3d BA = a.sub(b);
    _d = _c.add(BA);
    
    /*отношение большей диагонали к меньшей равен корень из 2 */
    double bigDiagonal = 2 * Vect3d.dist(a, b) * Math.sqrt(2) / Math.sqrt(3); // большая диагональ ромба
    double smallDiagonal = 2 * Vect3d.dist(a, b) / Math.sqrt(3); // маленькая диагональ ромба    

    // подднимаем точнки нижнего основания
    Vect3d tmpNormal = (new Polygon3d(_a, _b, _c, _d)).inCircle().normal().getNormalized();
    _a1 = Vect3d.sum(_a.duplicate(), tmpNormal.mul(-bigDiagonal));
    _b1 = Vect3d.sum(_b.duplicate(), tmpNormal.mul(-bigDiagonal));
    _c1 = Vect3d.sum(_c.duplicate(), tmpNormal.mul(-bigDiagonal));
    _d1 = Vect3d.sum(_d.duplicate(), tmpNormal.mul(-bigDiagonal));

    // будем получать точки боковых ромбов
    Vect3d tmpNormalForShift = Vect3d.sub(_b, _d).getNormalized();
    // получим среднюю точку между c и c1
    Vect3d tmpPoint = Vect3d.sum(_c, _c1).mul(0.5);
    // от средней точки между c и c1 шифтуем влево вправо
    double tmp = smallDiagonal / 2;
    _f = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(-tmp));
    _e = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(tmp));
    // получим среднюю точку между a и a1
    tmpPoint = Vect3d.sum(_a, _a1).mul(0.5);
    // шифтуем от нее влево вправо
    _h = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(-tmp));
    _i = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(tmp));

    //найдем среднюю точку между _f и _d1
    tmpPoint = Vect3d.sum(_f, _d1).mul(0.5);
    tmpNormal = Vect3d.sub(tmpPoint, _c1).getNormalized();
    _g = Vect3d.sum(_c1.duplicate(), tmpNormal.mul(bigDiagonal));
    // найдем среднюю точку между b и i
    Vect3d tmpPoint1 = Vect3d.sum(_e, _b1).mul(0.5);
    Vect3d tmpNormal1 = Vect3d.sub(tmpPoint1, _c1).getNormalized();
    _j = Vect3d.sum(_c1.duplicate(), tmpNormal1.mul(bigDiagonal));
  }
  
  /**
  * Constructor of RhombicDodecahedron by two vertices and angle
  * @param a 1st vertex
  * @param b 2nd vertex
  * @param c 3rd vertex
  * @param d 4th vertex
  * @throws ExGeom
  */
  public RhombicDodecahedron3d(Vect3d a, Vect3d b, Vect3d c, Vect3d d, String direction) throws ExGeom {
      System.out.println("направление");
      System.out.println(direction);
      System.out.println("направление");

      System.out.println("Точки в геоме");
      System.out.println(a);
      System.out.println(b);
      System.out.println(c);
      System.out.println(d);
      System.out.println("Точки в геоме");
    _a = a;
    _b = b;
    _c = c;
    _d = d;
    /*отношение большей диагонали к меньшей равен корень из 2 */
    double bigDiagonal = 2 * Vect3d.dist(a, b) * Math.sqrt(2) / Math.sqrt(3); // большая диагональ ромба
    double smallDiagonal = 2 * Vect3d.dist(a, b) / Math.sqrt(3); // маленькая диагональ ромба

    // подднимаем (или опускаем) точнки нижнего основания
    Vect3d tmpNormal = (new Polygon3d(_a, _b, _c, _d)).inCircle().normal().getNormalized();
    if (direction == "up") {
        _a1 = Vect3d.sum(_a.duplicate(), tmpNormal.mul(-bigDiagonal));
        _b1 = Vect3d.sum(_b.duplicate(), tmpNormal.mul(-bigDiagonal));
        _c1 = Vect3d.sum(_c.duplicate(), tmpNormal.mul(-bigDiagonal));
        _d1 = Vect3d.sum(_d.duplicate(), tmpNormal.mul(-bigDiagonal));
    }
    else if (direction == "down") {
        _a1 = Vect3d.sub(_a.duplicate(), tmpNormal.mul(-bigDiagonal));
        _b1 = Vect3d.sub(_b.duplicate(), tmpNormal.mul(-bigDiagonal));
        _c1 = Vect3d.sub(_c.duplicate(), tmpNormal.mul(-bigDiagonal));
        _d1 = Vect3d.sub(_d.duplicate(), tmpNormal.mul(-bigDiagonal));
    }

    // будем получать точки боковых ромбов
    Vect3d tmpNormalForShift = Vect3d.sub(_b, _d).getNormalized();
    // получим среднюю точку между c и c1
    Vect3d tmpPoint = Vect3d.sum(_c, _c1).mul(0.5);
    // от средней точки между c и c1 шифтуем влево вправо
    double tmp = smallDiagonal / 2;
    _f = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(-tmp));
    _e = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(tmp));
    // получим среднюю точку между a и a1
    tmpPoint = Vect3d.sum(_a, _a1).mul(0.5);
    // шифтуем от нее влево вправо
    _h = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(-tmp));
    _i = Vect3d.sum(tmpPoint, tmpNormalForShift.mul(tmp));

    //найдем среднюю точку между _f и _d1
    tmpPoint = Vect3d.sum(_f, _d1).mul(0.5);
    tmpNormal = Vect3d.sub(tmpPoint, _c1).getNormalized();
    _g = Vect3d.sum(_c1.duplicate(), tmpNormal.mul(bigDiagonal));
    // найдем среднюю точку между b и i
    Vect3d tmpPoint1 = Vect3d.sum(_e, _b1).mul(0.5);
    Vect3d tmpNormal1 = Vect3d.sub(tmpPoint1, _c1).getNormalized();
    _j = Vect3d.sum(_c1.duplicate(), tmpNormal1.mul(bigDiagonal));
  }
  
/**
 * @return all ribs of PrismSixCoal
 * @throws ExDegeneration
 */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_a, _b));
    r.add(new Rib3d(_b, _c));
    r.add(new Rib3d(_c, _d));
    r.add(new Rib3d(_d, _a));

    r.add(new Rib3d(_a1, _b1));
    r.add(new Rib3d(_b1, _c1));
    r.add(new Rib3d(_c1, _d1));
    r.add(new Rib3d(_d1, _a1));

    r.add(new Rib3d(_a, _h));
    r.add(new Rib3d(_a, _i));
    r.add(new Rib3d(_b, _j));
    r.add(new Rib3d(_c, _e));
    r.add(new Rib3d(_c, _f));
    r.add(new Rib3d(_d, _g));
    r.add(new Rib3d(_a1, _h));
    r.add(new Rib3d(_a1, _i));
    r.add(new Rib3d(_b1, _j));
    r.add(new Rib3d(_c1, _e));
    r.add(new Rib3d(_c1, _f));
    r.add(new Rib3d(_d1, _g));

    r.add(new Rib3d(_g, _f));
    r.add(new Rib3d(_g, _h));
    r.add(new Rib3d(_j, _i));
    r.add(new Rib3d(_j, _e));
    return r;
  }

 /**
 * @return all vertices of SixCoalPrism
 */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<>();
    p.add(_a);
    p.add(_b);
    p.add(_c);
    p.add(_d);
    p.add(_a1);
    p.add(_b1);
    p.add(_c1);
    p.add(_d1);
    p.add(_e);
    p.add(_f);
    p.add(_g);
    p.add(_h);
    p.add(_i);
    p.add(_j);

    return p;
  }

 /**
 * @return all faces of PrismSixCoal
 * @throws ExGeom
 */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> p = new ArrayList<>();
    p.add(new Polygon3d(_a, _b, _c, _d));
    p.add(new Polygon3d(_a1, _b1, _c1, _d1));
    p.add(new Polygon3d(_c, _f, _c1, _e));
    p.add(new Polygon3d(_a, _h, _a1, _i));
    p.add(new Polygon3d(_c1, _d1, _g, _f));
    p.add(new Polygon3d(_c, _d, _g, _f));
    p.add(new Polygon3d(_a, _d, _g, _h));
    p.add(new Polygon3d(_a1, _d1, _g, _h));
    p.add(new Polygon3d(_a1, _b1, _j, _i));
    p.add(new Polygon3d(_a, _b, _j, _i));
    p.add(new Polygon3d(_c, _b, _j, _e));
    p.add(new Polygon3d(_c1, _b1, _j, _e));

    return p;
  }

  public Vect3d A() {
    return _a.duplicate();
  }

  public Vect3d B() {
    return _b.duplicate();
  }

  public Vect3d C() {
    return _c.duplicate();
  }

  public Vect3d D() {
    return _d.duplicate();
  }

  public Vect3d A1() {
    return _a1.duplicate();
  }

  public Vect3d B1() {
    return _b1.duplicate();
  }

  public Vect3d C1() {
    return _c1.duplicate();
  }

  public Vect3d D1() {
    return _d1.duplicate();
  }

  public Vect3d E() {
    return _e.duplicate();
  }

  public Vect3d F() {
    return _f.duplicate();
  }

  public Vect3d G() {
    return _g.duplicate();
  }

  public Vect3d H() {
    return _h.duplicate();
  }

  public Vect3d I() {
    return _i.duplicate();
  }

  public Vect3d J() {
    return _j.duplicate();
  }
  
  public double surfaceArea() {
      return 8 * Math.sqrt(2) * Math.pow(this.ribLength(), 2);
  }
  
  public double ribLength(){
    return Vect3d.sub(_a, _b).norm();
  }
  
  public double volume() {
    return (16 / 9) * Math.sqrt(3) * Math.pow(this.ribLength(), 3);
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<Vect3d>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExGeom ex) {}
    return result;
  }

  /**
   *
   * @param line
   * @return Список точек пересечения ром. додекаэдра и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<>();
    for(int i = 0; i < faces.size(); i++) {
      for(int j = 0; j < faces().get(i).intersectWithLine(line).size(); j++) {
        if(!points.contains(faces().get(i).intersectWithLine(line).get(j))) {
          points.add(faces().get(i).intersectWithLine(line).get(j));
        }
      }
    }
    return points;
  }

  /**
   * Construct section of rhombic dodecahedron by plane
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   * @throws geom.ExZeroDivision
   */
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom {
    Rib3d[] ribs = new Rib3d[this.ribs().size()];
    for (int i = 0; i < this.ribs().size(); i++)
        ribs[i] = this.ribs().get(i);
    return sectionOfRibObject(ribs, plane);
  }

  @Override
  public boolean isOrientable() { return false; }

  @Override
  public ArrayList<Vect3d> deconstr() { return null; }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) { return null; }

  @Override
  public GeomType type() {
    return GeomType.DODECAHEDRON_ROMBIC3D;
  }
}
