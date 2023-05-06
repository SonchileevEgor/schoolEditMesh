package bodies;

import static config.Config.DEFAULT_FONT;
import opengl.colorgl.ColorGL;
import editor.Editor;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.state.AnchorState;
import editor.state.DisplayParam;
import geom.*;
import geom.ElongatedDodecahedron3d;
import geom.RhombicDodecahedron3d;
import geom.StellaOctahedron3d;
import geom.TruncatedOctahedron3d;
import gui.EdtController;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import util.Util;

/**
 * Класс, предназначенный для вывода информации о телах.
 *
 * @author alexeev
 */
public class BodyInfoConstructor {
  private BodyInfoConstructor() { }

  /**
   * Создание изображения с информацией о теле.
   *
   * @param edt
   * @param bodyID
   * @param precision точность вывода чисел (количество знаков после запятой)
   * @return
   * @throws ExNoBody
   * @throws editor.ExNoAnchor
   */
  public static Image getInfoAsImage(Editor edt, String bodyID, int precision) throws ExNoBody, ExNoAnchor {
    i_Body bd = edt.getBody(bodyID);
    BufferedImage im = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
    Graphics g = im.getGraphics();
    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    g.setFont(DEFAULT_FONT.value());
    g.fillRect(0, 0, 300, 300);
    switch (bd.type()) {
      case CIRCLE:
        {
          CircleBody c = (CircleBody)bd;
          AnchorState diskState = edt.getAnchor(bodyID, "disk").getState();
          g.setColor(((ColorGL)diskState.getParam(DisplayParam.FILL_COLOR)).toRGBAColor());
          g.fillOval(25, 25, 250, 250);
          g.setColor(((ColorGL)diskState.getParam(DisplayParam.CARCASS_COLOR)).toRGBAColor());
          g.drawOval(25, 25, 250, 250);
          g.setColor(Color.BLACK);
          g.drawLine(150, 150, 100, 100);
          g.drawLine(150, 150, 100, 264);
          g.drawLine(65, 200, 48, 275);
          g.drawLine(61, 285, 125, 285);
          g.drawArc(48, 264, 40, 21, 180, 90);
          g.fillOval(147, 147, 6, 6);
          g.fillOval(62, 197, 6, 6);
          g.fillPolygon(new int[]{100, 113, 107}, new int[]{100, 107, 113}, 3);
          g.drawString("Центр", 158, 150);
          g.drawString(c.center().toString(precision, true), 158, 165);
          g.drawString("Вектор нормали", 115, 85);
          g.drawString(c.normal().toString(precision, true), 115, 100);
          g.drawString(String.format("R = %." + precision + "f", c.circle().radiusLength()), 132, 220);
          g.drawString(String.format("S = %." + precision + "f", c.circle().area()), 105, 300);
        }
        break;
      case CONE:
        {
          ConeBody c = (ConeBody)bd;
          AnchorState diskState = edt.getAnchor(bodyID, "disk").getState();
          g.setColor(((ColorGL)diskState.getParam(DisplayParam.FILL_COLOR)).toRGBAColor());
          g.fillOval(25, 225, 250, 50);
          g.setColor(((ColorGL)diskState.getParam(DisplayParam.CARCASS_COLOR)).toRGBAColor());
          g.drawOval(25, 225, 250, 50);
          g.setColor(Color.BLACK);
          g.drawLine(150, 250, 275, 250);
          g.fillOval(147, 247, 6, 6);
          g.drawString(c.center().toString(precision, true), 154, 265); // центр
          g.drawString(String.format("R = %." + precision + "f", c.radius()), 132, 220); // радиус
          g.drawString(String.format("h = %." + precision + "f", c.height().norm()), 105, 300); // высота
        }
        break;
      case CONE_SECTION:
        break;
      case CUBE:
        break;
      case CYLINDER:
        break;
      case CYLINDER_SECTION:
        break;
      case LINE:
        break;
      case PLANE:
        break;
      case POINT:
        break;
      case POLYGON:
        break;
      case PRISM:
        break;
      case PYRAMID:
        break;
      case RIB:
        break;
      case SPHERE:
        break;
      case TETRAHEDRON:
        break;
      case RAY:
        break;
      case TRIANGLE:
        break;
      case PARALLELEPIPED:
        break;
      default:
        throw new AssertionError(bd.type().name());
    }
    return im;
  }

  public static String[][] getInfo(EdtController ctrl, String bodyID, int precision) throws ExNoBody{
    i_Body bd = ctrl.getBody(bodyID);
    if (!bd.exists()) {
      return new String[][]{{"", "Тело не построено"}};
    }
    switch (bd.type()) {
      case ANGLE:
      {
        String[][] result = new String[3][2];
        Angle3d angle = ((AngleBody)bd).angle();
        result[0][0] = "Вершина";
        result[0][1] = angle.vertex().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Величина угла";
        result[1][1] = Util.valueOf(Angle3d.radians2Degree(angle.value()), precision) + "\u00B0";
        if (ctrl.getScene().is3d()) {
          result[2][0] = "Вектор нормали";
          result[2][1] = angle.normal().toString(precision, true);
        }
        return result;
      }
      case ARC:
      {
        String[][] result = new String[5][2];
        Arc3d a = (Arc3d)((ArcBody)bd).getGeom();
        result[0][0] = "Центр";
        result[0][1] = a.center().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Радиус";
        result[1][1] = Util.valueOf(a.r(), precision);
        result[2][0] = "Угловая величина";
        result[2][1] = Util.valueOf(a.value(), precision);
        result[3][0] = "Длина";
        result[3][1] = Util.valueOf(a.length(), precision);
        if (ctrl.getScene().is3d()) {
          result[4][0] = "Вектор нормали";
          result[4][1] = a.normal().toString(precision, true);
        }
        return result;
      }
      case CIRCLE:
      {
        String[][] result = new String[5][2];
        Circle3d c = ((CircleBody)bd).circle();
        result[0][0] = "Центр";
        result[0][1] = c.center().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Радиус";
        result[1][1] = Util.valueOf(c.radiusLength(), precision);
        result[2][0] = "Площадь";
        result[2][1] = Util.valueOf(c.area(), precision);
        result[3][0] = "Длина окружности";
        result[3][1] = Util.valueOf(c.length(), precision);
        if (ctrl.getScene().is3d()) {
          result[4][0] = "Вектор нормали";
          result[4][1] = c.normal().toString(precision, true);
        }
        return result;
      }
      case CONE:
      {
        String[][] result = new String[5][2];
        Cone3d c = ((ConeBody)bd).cone();
        result[0][0] = "Вершина";
        result[0][1] = c.v().toString(precision, true);
        result[1][0] = "Центр основания";
        result[1][1] = c.c().toString(precision, true);
        result[2][0] = "Радиус основания";
        result[2][1] = Util.valueOf(c.r(), precision);
        result[3][0] = "Длина высоты";
        result[3][1] = Util.valueOf(c.hLen(), precision);
        result[4][0] = "Площадь поверхности";
        result[4][1] = Util.valueOf(c.surfaceArea(), precision);
        return result;
      }
      case CONE_SECTION:
      {
        String[][] result = new String[2][2];
        ConeSection3d cs = ((ConeSectionBody)bd).coneSection();
        result[0][0] = "Тип сечения конуса";
        result[0][1] = ((ConeSectionBody)bd).ruType();
        result[1][0] = "Вектор нормали";
        result[1][1] = cs.getUpVect().toString(precision, true);
        return result;
      }
      case CUBE:
      {
        String[][] result = new String[11][2];
        Cube3d c = ((CubeBody)bd).cube();
        result[0][0] = "Число вершин куба";
        result[0][1] = String.valueOf(8);
        result[1][0] = "Число ребер куба";
        result[1][1] = String.valueOf(12);
        result[2][0] = "Число граней куба";
        result[2][1] = String.valueOf(6);
        result[3][0] = "Центр";
        result[3][1] = c.center().toString(precision, true);
        result[4][0] = "Длина стороны";
        result[4][1] = Util.valueOf(c.edgeLength(), precision);
        result[5][0] = "Радиус вписанной окружности";
        result[5][1] = Util.valueOf(c.edgeLength() / 2, precision);
        result[6][0] = "Радиус описанной окружности";
        result[6][1] = Util.valueOf(c.diagonalLength() / 2, precision);
        result[7][0] = "Длина главной диагонали";
        result[7][1] = Util.valueOf(c.diagonalLength(), precision);
        result[8][0] = "Площадь одной грани";
        result[8][1] = Util.valueOf(c.edgeLength() * c.edgeLength(), precision);
        result[9][0] = "Площадь полной поверхности";
        result[9][1] = Util.valueOf(c.surfaceArea(), precision);
        result[10][0] = "Объём";
        result[10][1] = Util.valueOf(c.volume(), precision);
        return result;
      }
      case CYLINDER:
      {
        String[][] result = new String[7][2];
        Cylinder3d c = ((CylinderBody)bd).cylinder();
        result[0][0] = "Центр первого основания";
        result[0][1] = c.c0().toString(precision, true);
        result[1][0] = "Центр второго основания";
        result[1][1] = c.c1().toString(precision, true);
        result[2][0] = "Высота";
        result[2][1] = Util.valueOf(c.h().norm(), precision);
        result[3][0] = "Радиус основания";
        result[3][1] = Util.valueOf(c.r(), precision);
        result[4][0] = "Площадь боковой поверхности";
        result[4][1] = Util.valueOf(c.sideSurfaceArea(), precision);
        result[5][0] = "Площадь полной поверхности";
        result[5][1] = Util.valueOf(c.surfaceArea(), precision);
        result[6][0] = "Объём";
        result[6][1] = Util.valueOf(c.volume(), precision);
        return result;
      }
      case CYLINDER_SECTION:
      {
        String[][] result = new String[2][2];
        CylinderSection3d cs = ((CylinderSectionBody)bd).cylinderSection();
        result[0][0] = "Тип сечения конуса";
        result[0][1] = ((CylinderSectionBody)bd).ruType();
        result[1][0] = "Вектор нормали";
        result[1][1] = cs.getUpVect().toString(precision, true);
        return result;
      }
      case DODECAHEDRON:
      {
        String[][] result = new String[10][2];
        Dodecahedron3d d = ((DodecahedronBody)bd).dodecahedron();
        result[0][0] = "Число вершин додекаэдра";
        result[0][1] = String.valueOf(20);
        result[1][0] = "Число ребер додекаэдра";
        result[1][1] = String.valueOf(30);
        result[2][0] = "Число граней додекаэдра";
        result[2][1] = String.valueOf(12);
        result[3][0] = "Центр";
        try {
          result[3][1] = d.center().toString(precision, true);
        } catch (ExGeom ex) {
          result[3][1] = "не удалось определить";
        }
        result[4][0] = "Длина стороны";
        result[4][1] = Util.valueOf(d.ribLength(), precision);
        result[5][0] = "Радиус вписанной окружности";
        result[5][1] = Util.valueOf(d.inRadius(), precision);
        result[6][0] = "Радиус описанной окружности";
        result[6][1] = Util.valueOf(d.outRadius(), precision);
        result[7][0] = "Площадь одной грани";
        result[7][1] = Util.valueOf(d.faceArea(), precision);
        result[8][0] = "Площадь полной поверхности";
        result[8][1] = Util.valueOf(d.surfaceArea(), precision);
        result[9][0] = "Объём";
        result[9][1] = Util.valueOf(d.volume(), precision);
        return result;
      }
      case ELONGATED_DODECAHEDRON:
      {
        String[][] result = new String[4][2];
        ElongatedDodecahedron3d d = ((ElongatedDodecahedronBody)bd).elongatedDodecahedron();
        result[0][0] = "Число вершин удл. додекаэдра";
        result[0][1] = String.valueOf(18);
        result[1][0] = "Число ребер удл. додекаэдра";
        result[1][1] = String.valueOf(28);
        result[2][0] = "Число граней удл. додекаэдра";
        result[2][1] = String.valueOf(12);
        result[3][0] = "Длина стороны";
        result[3][1] = Util.valueOf(d.ribLength(), precision);
        return result;
      }
      case RHOMBIC_DODECAHEDRON:
      {
        String[][] result = new String[4][2];
        RhombicDodecahedron3d d = ((RhombicDodecahedronBody)bd).rhombicDodecahedron();
        result[0][0] = "Число вершин ромб. додекаэдра";
        result[0][1] = String.valueOf(14);
        result[1][0] = "Число ребер ромб. додекаэдра";
        result[1][1] = String.valueOf(24);
        result[2][0] = "Число граней ромб. додекаэдра";
        result[2][1] = String.valueOf(12);
        result[3][0] = "Длина стороны";
        result[3][1] = Util.valueOf(d.ribLength(), precision);
        /*
        result[4][0] = "Площадь полной поверхности";
        result[4][1] = Util.valueOf(d.surfaceArea(), precision);
        result[5][0] = "Объём";
        result[5][1] = Util.valueOf(d.volume(), precision);
        */
        return result;
      }
      case TRUNCATED_OCTAHEDRON:
      {
        String[][] result = new String[4][2];
        TruncatedOctahedron3d d = ((TruncatedOctahedronBody)bd).truncatedOctahedron();
        result[0][0] = "Число вершин усеч. октаэдра";
        result[0][1] = String.valueOf(24);
        result[1][0] = "Число ребер усеч. октаэдра";
        result[1][1] = String.valueOf(36);
        result[2][0] = "Число граней усеч. октаэдра";
        result[2][1] = String.valueOf(14);
        result[3][0] = "Длина стороны";
        result[3][1] = Util.valueOf(d.ribLength(), precision);
        /*
        result[4][0] = "Площадь полной поверхности";
        result[4][1] = Util.valueOf(d.surfaceArea(), precision);
        result[5][0] = "Объём";
        result[5][1] = Util.valueOf(d.volume(), precision);
        */
        return result;
      }
      case STELLAR_OCTAHEDRON:
      {
        String[][] result = new String[4][2];
        StellaOctahedron3d d = ((StellaOctahedronBody)bd).stellaOctahedron();
        result[0][0] = "Число вершин";
        result[0][1] = String.valueOf(14);
        result[1][0] = "Число ребер";
        result[1][1] = String.valueOf(36);
        result[2][0] = "Число граней";
        result[2][1] = String.valueOf(24);
        result[3][0] = "Длина стороны";
        result[3][1] = Util.valueOf(d.ribLength(), precision);

        return result;
      }
      case ELLIPSE:
      {
        String[][] result = new String[7][2];
        EllipseMain3d e = ((EllipseMainBody)bd).ellipse();
        result[0][0] = "Фокус 1";
        result[0][1] = e.f1().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Фокус 2";
        result[1][1] = e.f2().toString(precision, ctrl.getScene().is3d());
        result[2][0] = "Длина большой полуоси";
        result[2][1] = Util.valueOf(e.bigAxle(), precision);
        result[3][0] = "Длина малой полуоси";
        result[3][1] = Util.valueOf(e.smallAxle(), precision);
        result[4][0] = "Фокальное расстояние";
        result[4][1] = Util.valueOf(Vect3d.dist(e.f1(), e.f2()), precision);
        result[5][0] = "Эксцентриситет";
        result[5][1] = Util.valueOf(e.e(), precision);
        if (ctrl.getScene().is3d())
        {
          result[6][0] = "Вектор нормали";
          try {
            result[6][1] = e.normal().toString(precision, true);
          } catch (ExDegeneration ex) {
            result[6][1] = "Не удалось вычислить";
          }
        }
        //result[6][0] = "Уравнение эллипса";
        return result;
      }
      case HYPERBOLE:
      {
        String[][] result = new String[7][2];
        Hyperbole3d h = ((HyperboleBody)bd).hyperbole();
        result[0][0] = "Фокус 1";
        result[0][1] = h.f1().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Фокус 2";
        result[1][1] = h.f2().toString(precision, ctrl.getScene().is3d());
        result[2][0] = "Длина большой полуоси";
        result[2][1] = Util.valueOf(h.realAxle(), precision);
        result[3][0] = "Длина малой полуоси";
        result[3][1] = Util.valueOf(h.b(), precision);
        result[4][0] = "Фокальное расстояние";
        result[4][1] = Util.valueOf(Vect3d.dist(h.f1(), h.f2()), precision);
        result[5][0] = "Эксцентриситет";
        result[5][1] = Util.valueOf(h.e(), precision);
        if (ctrl.getScene().is3d())
        {
          result[6][0] = "Вектор нормали";
          try {
            result[6][1] = h.normal().toString(precision, true);
          } catch (ExDegeneration ex) {
            result[6][1] = "Не удалось вычислить";
          }
        }
        //result[6][0] = "Уравнение гиперболы";
        return result;
      }
      case ICOSAHEDRON:
      {
        String[][] result = new String[10][2];
        Icosahedron3d i = ((IcosahedronBody)bd).icosahedron();
        result[0][0] = "Число вершин икосаэдра";
        result[0][1] = String.valueOf(12);
        result[1][0] = "Число ребер икосаэдра";
        result[1][1] = String.valueOf(30);
        result[2][0] = "Число граней икосаэдра";
        result[2][1] = String.valueOf(20);
        result[3][0] = "Центр";
        try {
          result[3][1] = i.center().toString(precision, true);
        } catch (ExGeom ex) {
          result[3][1] = "не удалось определить";
        }
        result[4][0] = "Длина стороны";
        result[4][1] = Util.valueOf(i.ribLength(), precision);
        result[5][0] = "Радиус вписанной окружности";
        result[5][1] = Util.valueOf(i.inRadius(), precision);
        result[6][0] = "Радиус описанной окружности";
        result[6][1] = Util.valueOf(i.outRadius(), precision);
        result[7][0] = "Площадь одной грани";
        result[7][1] = Util.valueOf(i.faceArea(), precision);
        result[8][0] = "Площадь полной поверхности";
        result[8][1] = Util.valueOf(i.surfaceArea(), precision);
        result[9][0] = "Объём";
        result[9][1] = Util.valueOf(i.volume(), precision);
        return result;
      }
      case LINE:
      {
        String[][] result = new String[0][0];
        Line3d l = ((LineBody)bd).line();
        //result[0][0] = "Уравнение";
        //result[0][1] = l.getCanonicalEquation(precision);
        return result;
      }
      case OCTAHEDRON:
      {
        String[][] result = new String[10][2];
        Octahedron3d o = ((OctahedronBody)bd).octahedron();
        result[0][0] = "Число вершин октаэдра";
        result[0][1] = String.valueOf(6);
        result[1][0] = "Число ребер октаэдра";
        result[1][1] = String.valueOf(12);
        result[2][0] = "Число граней октаэдра";
        result[2][1] = String.valueOf(8);
        result[3][0] = "Центр";
        try {
          result[3][1] = o.center().toString(precision, true);
        } catch (ExGeom ex) {
          result[3][1] = "не удалось определить";
        }
        result[4][0] = "Длина стороны";
        result[4][1] = Util.valueOf(o.ribLength(), precision);
        result[5][0] = "Радиус вписанной окружности";
        result[5][1] = Util.valueOf(o.inRadius(), precision);
        result[6][0] = "Радиус описанной окружности";
        result[6][1] = Util.valueOf(o.outRadius(), precision);
        result[7][0] = "Площадь одной грани";
        result[7][1] = Util.valueOf(o.faceArea(), precision);
        result[8][0] = "Площадь полной поверхности";
        result[8][1] = Util.valueOf(o.surfaceArea(), precision);
        result[9][0] = "Объём";
        result[9][1] = Util.valueOf(o.volume(), precision);
        return result;
      }
      case PARABOLA:
      {
        String[][] result = new String[3][2];
        Parabola3d p = ((ParabolaBody)bd).parabola();
        result[0][0] = "Фокус";
        result[0][1] = p.f().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Эксцентриситет";
        result[1][1] = Util.valueOf(1, precision);
        if (ctrl.getScene().is3d())
        {
          result[2][0] = "Вектор нормали";
          result[2][1] = p.getUpVect().toString(precision, true);
        }
        //result[2][0] = "Уравнение гиперболы";
        //result[3][0] = "Уравнение директрисы";
        return result;
      }
      case PAIROFLINES: 
      {
        String[][] result = new String[0][0];
        PairOfLines pairOfLines = ((PairOfLinesBody) bd).pairOfLines();
        return result;
      }
      case ELLIPSOID: 
      {
        String[][] result = new String[5][2];
        Ellipsoid3d ed = ((EllipsoidBody)bd).ellipsoid();
        result[0][0] = "Фокус 1";
        result[0][1] = ed.f1().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Фокус 2";
        result[1][1] = ed.f2().toString(precision, ctrl.getScene().is3d());
        result[2][0] = "Длина большой полуоси";
        result[2][1] = Util.valueOf(ed.bigAxle(), precision);
        result[3][0] = "Длина малой полуоси";
        result[3][1] = Util.valueOf(ed.smallAxle(), precision);
        result[4][0] = "Фокальное расстояние";
        result[4][1] = Util.valueOf(Vect3d.dist(ed.f1(), ed.f2()), precision);
        //result[5][0] = "Эксцентриситет";
        //result[5][1] = Util.valueOf(e.e(), precision);
        //result[6][0] = "Уравнение эллипса";
        return result;
      }
      case HYPERBOLOID_OF_ONE_SHEET:
      {
        String[][] result = new String[6][2];
        HyperboloidOfOneSheet3d hd1 = ((HyperboloidOfOneSheetBody)bd).hyperboloid();
        result[0][0] = "Фокус 1";
        result[0][1] = hd1.f1().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Фокус 2";
        result[1][1] = hd1.f2().toString(precision, ctrl.getScene().is3d());
        result[2][0] = "Длина большой полуоси";
        result[2][1] = Util.valueOf(hd1.realAxle(), precision);
        result[3][0] = "Длина малой полуоси";
        result[3][1] = Util.valueOf(hd1.b(), precision);
        result[4][0] = "Фокальное расстояние";
        result[4][1] = Util.valueOf(Vect3d.dist(hd1.f1(), hd1.f2()), precision);
        result[5][0] = "Эксцентриситет";
        result[5][1] = Util.valueOf(hd1.e(), precision);
        //result[6][0] = "Уравнение гиперболы";
        return result;
      }
      case HYPERBOLOID_OF_TWO_SHEET:
      {
        String[][] result = new String[6][2];
        HyperboloidOfTwoSheet3d hd2 = ((HyperboloidOfTwoSheetBody)bd).hyperboloid();
        result[0][0] = "Фокус 1";
        result[0][1] = hd2.f1().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Фокус 2";
        result[1][1] = hd2.f2().toString(precision, ctrl.getScene().is3d());
        result[2][0] = "Длина большой полуоси";
        result[2][1] = Util.valueOf(hd2.realAxle(), precision);
        result[3][0] = "Длина малой полуоси";
        result[3][1] = Util.valueOf(hd2.b(), precision);
        result[4][0] = "Фокальное расстояние";
        result[4][1] = Util.valueOf(Vect3d.dist(hd2.f1(), hd2.f2()), precision);
        result[5][0] = "Эксцентриситет";
        result[5][1] = Util.valueOf(hd2.e(), precision);
        //result[6][0] = "Уравнение гиперболы";
        return result;
      }
      case ELLIPTIC_PARABOLOID:
      {
        String[][] result = new String[2][2];
        EllipticParaboloid3d elpd = ((EllipticParaboloidBody)bd).paraboloid();
        result[0][0] = "Фокус";
        result[0][1] = elpd.f().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Эксцентриситет";
        result[1][1] = Util.valueOf(1, precision);
        //result[2][0] = "Уравнение гиперболы";
        //result[3][0] = "Уравнение директрисы";
        return result;
      }
      case PLANE:
      {
        String[][] result = new String[1][2];
        Plane3d pl = ((PlaneBody)bd).plane();
        result[0][0] = "Нормаль";
        result[0][1] = pl.n().toString();
        return result;
      }
      case POINT:
      {
        String[][] result = new String[1][2];
        Vect3d v = ((PointBody)bd).point();
        result[0][0] = "Координаты";
        result[0][1] = v.toString(precision, ctrl.getScene().is3d());
        return result;
      }
      case POLYGON:
      {
        String[][] result = new String[3][2];
        Polygon3d poly = ((PolygonBody)bd).polygon();
        result[0][0] = "Периметр";
        result[0][1] = Util.valueOf(poly.perimeter(), precision);
        result[1][0] = "Площадь";
        result[1][1] = Util.valueOf(poly.area(), precision);
        if (ctrl.getScene().is3d())
        {
          result[2][0] = "Вектор нормали";
          try {
            result[2][1] = poly.plane().n().toString(precision, true);
          } catch (ExDegeneration ex) {
            result[2][1] = "Не удалось вычислить";
          }
        }
        return result;
      }
      case PARALLELEPIPED:
      case PRISM:
      {
        String[][] result = new String[3][2];
        Prism3d prism = ((PrismBody)bd).prism();
        result[0][0] = "Площадь боковой поверхности";
        result[0][1] = Util.valueOf(prism.lateralSurfaceArea(), precision);
        result[1][0] = "Площадь полной поверхности";
        result[1][1] = Util.valueOf(prism.surfaceArea(), precision);
        result[2][0] = "Объём";
        result[2][1] = Util.valueOf(prism.volume(), precision);
        return result;
      }
      case HEXPRISM:
      {
        String[][] result = new String[3][2];
        Prism3d prism = ((HexagonalPrismBody)bd).hexagonalprism();
        result[0][0] = "Площадь боковой поверхности";
        result[0][1] = Util.valueOf(prism.lateralSurfaceArea(), precision);
        result[1][0] = "Площадь полной поверхности";
        result[1][1] = Util.valueOf(prism.surfaceArea(), precision);
        result[2][0] = "Объём";
        result[2][1] = Util.valueOf(prism.volume(), precision);
        return result;
      }
      case PYRAMID:
      {
        String[][] result = new String[5][2];
        Pyramid3d pyr = ((PyramidBody)bd).pyramid();
        result[0][0] = "Вершина";
        result[0][1] = pyr.top().toString(precision, true);
        result[1][0] = "Высота";
        result[1][1] = Util.valueOf(pyr.heightLength(), precision);
        result[2][0] = "Площадь боковой поверхности";
        result[2][1] = Util.valueOf(pyr.lateralSurfaceArea(), precision);
        result[3][0] = "Площадь полной поверхности";
        result[3][1] = Util.valueOf(pyr.surfaceArea(), precision);
        result[4][0] = "Объём";
        result[4][1] = Util.valueOf(pyr.volume(), precision);
        return result;
      }
      case RAY:
        break;
      case REG_TETRAHEDRON:
      {
        String[][] result = new String[10][2];
        Simplex3d rt = ((TetrahedronBody)bd).tetrahedron();
        result[0][0] = "Число вершин правильного тетраэдра";
        result[0][1] = String.valueOf(4);
        result[1][0] = "Число ребер правильного тетраэдра";
        result[1][1] = String.valueOf(6);
        result[2][0] = "Число граней правильного тетраэдра";
        result[2][1] = String.valueOf(4);
        result[3][0] = "Центр";
        try {
          result[3][1] = rt.inCenter().toString(precision, true);
        } catch (ExGeom ex) {
          result[3][1] = "не удалось определить";
        }
        result[4][0] = "Длина стороны";
        result[4][1] = Util.valueOf(Vect3d.dist(rt.a(),rt.b()), precision);
        result[5][0] = "Радиус вписанной окружности";
        result[5][1] = Util.valueOf(rt.inRadius(), precision);
        result[6][0] = "Радиус описанной окружности";
        result[6][1] = Util.valueOf(rt.outRadius(), precision);
        result[7][0] = "Площадь одной грани";
        result[7][1] = Util.valueOf(rt.faceArea(0), precision);
        result[8][0] = "Площадь полной поверхности";
        result[8][1] = Util.valueOf(rt.surfaceArea(), precision);
        result[9][0] = "Объём";
        result[9][1] = Util.valueOf(rt.volume(), precision);
        return result;
      }
      case RIB:
      {
        String[][] result = new String[3][2];
        Rib3d r = ((RibBody)bd).rib();
        result[0][0] = "Первая вершина";
        result[0][1] = r.a().toString(precision, ctrl.getScene().is3d());
        result[1][0] = "Вторая вершина";
        result[1][1] = r.b().toString(precision, ctrl.getScene().is3d());
        result[2][0] = "Длина";
        result[2][1] = Util.valueOf(r.length(), precision);
        //result[3][0] = "Уравнение";
        //result[3][1] = "Не удалось вычислить";
        return result;
      }
      case SPHERE:
      {
        String[][] result = new String[4][2];
        Sphere3d s = ((SphereBody)bd).sphere();
        result[0][0] = "Центр";
        result[0][1] = s.center().toString(precision, true);
        result[1][0] = "Радиус";
        result[1][1] = Util.valueOf(s.radius(), precision);
        result[2][0] = "Площадь поверхности";
        result[2][1] = Util.valueOf(s.area(), precision);
        result[3][0] = "Объём";
        result[3][1] = Util.valueOf(s.volume(), precision);
        return result;
      }
      case TETRAHEDRON:
      {
        String[][] result = new String[2][2];
        Simplex3d t = ((TetrahedronBody)bd).tetrahedron();
        result[0][0] = "Площадь поверхности";
        result[0][1] = Util.valueOf(t.surfaceArea(), precision);
        result[1][0] = "Объём";
        result[1][1] = Util.valueOf(t.volume(), precision);
        return result;
      }
      case TRIANGLE:
      {
        String[][] result = new String[3][2];
        Triang3d t = ((TriangleBody)bd).triangle();

        result[0][0] = "Периметр";
        result[0][1] = Util.valueOf(t.perimeter(), precision);
        result[1][0] = "Площадь";
        result[1][1] = Util.valueOf(t.area(), precision);
        if(ctrl.getScene().is3d()) {
          result[2][0] = "Вектор нормали";
          try {
            result[2][1] = t.plane().n().toString(precision, ctrl.getScene().is3d());
          } catch (ExDegeneration ex) {
            result[2][1] = "Не удалось вычислить";
          }
        }
        return result;
      }
    }
    return new String[0][0];
  }
}