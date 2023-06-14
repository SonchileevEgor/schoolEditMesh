package bodies;

import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import static editor.state.DisplayParam.*;
import editor.state.DisplayParamSet;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Список типов тел.
 *
 * @author alexeev
 */
public enum BodyType {
  ANGLE(new DisplayParamSet(CHOSEN, VISIBLE, ANGLE_STYLE, FILL_COLOR, CARCASS_COLOR,
          FILL_VISIBLE, CARCASS_THICKNESS, DRAW_FIRST_ANGLE_SIDE, DRAW_SECOND_ANGLE_SIDE),
          "ang", 96, true, true, false, false, false, false, false, false, false, false),
  ARC(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "arc", 70, false, true, false, false, false, false, false, false, false, false),
  CIRCLE(new DisplayParamSet(CHOSEN),
          "circ", 95, true, true, false, false, false, false, false, true, false, true),
  CONE(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR, FILL_COLOR, FILL_VISIBLE),
          "cone", 50, true, true, false, true, true, false, true, true, false, false),
  CONE_SECTION(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR, FILL_COLOR, FILL_VISIBLE),
          "sect", 0, true, true, false, false, false, false, false, false, false, false),
  CUBE(new DisplayParamSet(CHOSEN),
          "cube", 50, true, true, false, true, true, true, true, false, false, false),
  CYLINDER(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR, FILL_COLOR, FILL_VISIBLE),
          "cyl", 50, true, true, false, true, true, false, true, true, false, false),
  CYLINDER_SECTION(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR, FILL_COLOR, FILL_VISIBLE),
          "sect", 0, true, true, false, false, false, false, false, false, false, false),
  EMPTY(new DisplayParamSet(CHOSEN),
          "empty", -1, false, false, false, false, false, false, false, false, false, false),
  INTERSECT_BODY(new DisplayParamSet(CHOSEN),
          "intersect", -1, false, false, false, false, false, false, false, false, false, false),
  LINE(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR),
          "line", 97, false, true, false, false, false, false, false, true, true, false),
  PLANE(new DisplayParamSet(CHOSEN, VISIBLE, FILL_COLOR, PLANE_INDENT),
          "plane", 0, true, false, false, false, false, false, true, true, false, true),
  POINT(new DisplayParamSet(CHOSEN),
          "p", 100, false, false, false, false, false, false, false, false, false, false),
  POLYGON(new DisplayParamSet(CHOSEN),
          "poly", 90, true, true, true, false, false, false, false, true, false, true),
  PRISM(new DisplayParamSet(CHOSEN),
          "prism", 50, true, true, false, true, true, true, true, false, false, false),
  HEXPRISM(new DisplayParamSet(CHOSEN),
          "hexprism", 50, true, true, false, true, true, true, true, false, false, false),
  PARALLELEPIPED(new DisplayParamSet(CHOSEN),
          "parall", 50, true, true, false, true, true, true, false, false, false, false),
  PARALLELOGRAM(new DisplayParamSet(CHOSEN),
          "parall", 90, true, true, true, false, false, false, false, true, false, true),
  PYRAMID(new DisplayParamSet(CHOSEN),
          "pyr", 50, true, true, false, true, true, true, true, false, false, false),
  RAY(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR),
          "ray", 98, false, true, false, false, false, false, false, false, true, false),
  RECTANGLE(new DisplayParamSet(CHOSEN),
          "rect", 90, true, true, true, false, false, false, false, true, false, true),
  RECT_TRIANGLE(new DisplayParamSet(CHOSEN),
          "tr", 90, true, true, true, false, false, false, false, true, false, true),
  RIB(new DisplayParamSet(CHOSEN),
          "rib", 99, false, true, false, false, false, false, false, true, true, false),
  RHOMBUS(new DisplayParamSet(CHOSEN),
          "rhomb", 90, true, true, true, false, false, false, false, true, false, true),
  SPHERE(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR,
          FILL_COLOR, FILL_VISIBLE),
          "sph", 50, true, true, false, false, false, false, true, true, false, false),
  TETRAHEDRON(new DisplayParamSet(CHOSEN),
          "tetr", 50, true, true, false, true, true, true, true, false, false, false),
  TRAPEZE(new DisplayParamSet(CHOSEN),
          "trap", 90, true, true, true, false, false, false, false, true, false, true),
  TRIANGLE(new DisplayParamSet(CHOSEN),
          "tr", 90, true, true, true, false, false, false, false, true, false, true),
  DODECAHEDRON(new DisplayParamSet(CHOSEN),
          "dodec", 50, true, true, false, true, true, true, false, false, false, false),
  ICOSAHEDRON(new DisplayParamSet(CHOSEN),
          "icos", 50, true, true, false, true, true, true, false, false, false, false),
  TRUNCATED_OCTAHEDRON(new DisplayParamSet(CHOSEN),
          "trunc_dodec", 50, true, true, false, true, true, true, false, false, false, false),
  ELONGATED_DODECAHEDRON(new DisplayParamSet(CHOSEN),
          "elong_dodec", 50, true, true, false, true, true, true, false, false, false, false),
  RHOMBIC_DODECAHEDRON(new DisplayParamSet(CHOSEN),
          "rhomb_dodec", 50, true, true, false, true, true, true, false, false, false, false),
  ELLIPSE(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS, FILL_COLOR, FILL_VISIBLE),
          "ellipse", 95, true, true, false, false, false, false, false, false, false, true),
  HYPERBOLE(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS, DRAW_ASYMPTOTES),
          "hyp", 70, false, true, false, false, false, false, false, false, false, false),
  PARABOLA(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "par", 70, false, true, false, false, false, false, false, false, false, false),
  ELEMENTARYFUNCTION(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "hyp", 70, false, true, false, false, false, false, false, false, false, false),
  OCTAHEDRON(new DisplayParamSet(CHOSEN),
          "oct", 50, true, true, false, true, true, true, false, false, false, false),
  STELLAR_OCTAHEDRON(new DisplayParamSet(CHOSEN),
          "st_oct", 50, true, true, false, true, true, true, false, false, false, false),
  REG_TETRAHEDRON(new DisplayParamSet(CHOSEN),
          "reg_tetr", 50, true, true, false, true, true, true, true, false, false, false),
  ELLIPTIC_PARABOLOID(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "el_paraboloid", 50, false, true, false, false, false, false, false, false, false, false),
  HYPERBOLOID_OF_ONE_SHEET(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "hyp_one_sheet", 50, false, true, false, false, false, false, false, false, false, false),
  HYPERBOLOID_OF_TWO_SHEET(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "hyp_two_sheet", 50, false, true, false, false, false, false, false, false, false, false),
  ELLIPSOID(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "ellipsoid", 50, true, true, false, true, true, false, true, true, false, false),
  PAIROFLINES(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_THICKNESS, CARCASS_COLOR),
          "pair_of_lines", 94, false, true, false, false, false, false, false, true, true, false),
  CONIC(new DisplayParamSet(CHOSEN, VISIBLE, CARCASS_COLOR, CARCASS_THICKNESS),
          "conic", 93, false, true, false, false, false, false, false, false, false, false);
  
  public static ArrayList<BodyType> getBodiesWithCircumscribedSphere() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.hasCircumscribedSphere())
        result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getBodiesWithInscribedSphere() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.hasInscribedSphere())
        result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getBodiesForIntersection() {
    ArrayList<BodyType> result = new ArrayList<>();
    result.addAll(getPolygonTypes());
    result.add(BodyType.RIB);
    result.add(BodyType.CIRCLE);
    result.add(BodyType.LINE);
    result.add(BodyType.RAY);
    result.add(BodyType.ARC);
    result.add(BodyType.ANGLE);
    result.add(BodyType.ELLIPSE);
    result.add(BodyType.PARABOLA);
    result.add(BodyType.HYPERBOLE);
    result.add(BodyType.PAIROFLINES);
    return result;
  }

  public static ArrayList<BodyType> getPolygonTypes() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.isPolygon())
        result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getAllTypes() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getTypesForPlaneSection() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.isPlaneIntersectable())
        result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getTypesForLineSection() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.isLineIntersectable())
        result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getLinearBodies() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.isLinear())
        result.add(value);
    }
    return result;
  }

  public static ArrayList<BodyType> getPlainBodies() {
    ArrayList<BodyType> result = new ArrayList<>();
    for (BodyType value : BodyType.values()) {
      if (value.isPlain())
        result.add(value);
    }
    return result;
  }


  private final DisplayParamSet _params; // параметры отображения тела данного типа
  private final String _prefix;
  private final boolean _hasSurface;
  private final boolean _hasCarcass;
  private final boolean _isPolygon;
  private final boolean _hasInscribedSphere;
  private final boolean _hasCircumscribedSphere;
  private final boolean _isPolyhedron;
  private final boolean _isPlaneIntersectable;
  private final boolean _isLineIntersectable;
  private final boolean _isLinear;
  private final boolean _isPlain;
  
  // Приоритет при выборе в HighlightAdapter.
  // Чем больше, тем выше приоритет.
  public final int choosePriority;

  BodyType(DisplayParamSet params, String prefix, int choosePriority,
          boolean hasSurface, boolean hasCarcass, boolean isPolygon,
          boolean hasInscribedSphere, boolean hasCircumscribedSphere,
          boolean isPolyhedron, boolean isPlaneIntersectable, boolean isLineIntersectable,
          boolean isLinear, boolean isPlain) {
    _params = params;
    _prefix = prefix;
    _hasSurface = hasSurface;
    _hasCarcass = hasCarcass;
    _isPolygon = isPolygon;
    _hasInscribedSphere = hasInscribedSphere;
    _hasCircumscribedSphere = hasCircumscribedSphere;
    _isPolyhedron = isPolyhedron;
    _isPlaneIntersectable = isPlaneIntersectable;
    _isLineIntersectable = isLineIntersectable;
    _isLinear = isLinear;
    _isPlain = isPlain;
    this.choosePriority = choosePriority;
  }

  /**
   * Имеет ли билдер пересечения с прямой.
   * @return
   */
  public boolean isLineIntersectable() {
    return _isLineIntersectable;
  }

  /**
   * Является ли тело линейным.
   * @return
   */
  public boolean isLinear() {
    return _isLinear;
  }

  /**
   * Является ли тело плоским.
   * @return
   */
  public boolean isPlain() {
    return _isPlain;
  }

  /**
   * Список параметров отображения.
   * @return
   */
  public DisplayParamSet getParams() {
    return _params;
  }

  /**
   * Есть ли поверхность у тела данного типа.
   * @return
   */
  public boolean hasSurface() {
    return _hasSurface;
  }

  /**
   * Есть ли каркас у тела данного типа.
   * @return
   */
  public boolean hasCarcass() {
    return _hasCarcass;
  }

  /**
   * Является ли тело многоугольником.
   * @return
   */
  public boolean isPolygon() {
    return _isPolygon;
  }

  /**
   * Является ли тело многогранником.
   * @return
   */
  public boolean isPolyhedron() {
    return _isPolyhedron;
  }

  public boolean hasInscribedSphere() {
    return _hasInscribedSphere;
  }

  public boolean hasCircumscribedSphere() {
    return _hasCircumscribedSphere;
  }

  public boolean isPlaneIntersectable() {
    return _isPlaneIntersectable;
  }

  /**
   * Number of branch in body tree
   * reserved for this body type.
   * @return
   */
  public int getBranchType() {
    if (this == BodyType.POINT) {
      return 3;
    } else if (this == BodyType.RIB) {
      return 2;
    } else if (this.isPolygon()) {
      return 1;
    } else {
      return 4;
    }
  }

  /**
   * Get an automatically generated name for the next_th body of given type.
   * @param edt
   * @param next
   * @return
   */
  public String getName(Editor edt, int next) {
    ArrayList<String> titles = edt.bd().getAllTitles();
    int est = next;

    // Points change letter;
    // Other bodies keep prefix and change suffix number.
    if( this == POINT ){
      for (i_Anchor a: edt.anchors().getPointAnchors()) {
        try {
          titles.add(edt.getAnchorTitle(a.id()));
        } catch (ExNoAnchor ex) {}
      }
      String postfix = "";
      // If all letters are occupied, add hatch.
      while(true) {
        for (Character ch = 'A'; ch <= 'Z'; ch++) {
          String pointName = ch.toString() + postfix;
          // If there exists f.e. point E_1, we won't create point E
          final String pattern = String.format("^(%s|%s_.*)$", pointName, pointName);
          Predicate p = new Predicate() {
            @Override
            public boolean evaluate(Object str) {
              return Pattern.matches(pattern, (String)str);
            }
          };
          if (CollectionUtils.countMatches(titles, p) == 0) {
            if( est == 1 ){
              return pointName;
            } else {
              est--;
            }
          }
        }
        postfix += "'";
      }
    } else {
      String title = _prefix;
      int postfix = 0;
      while( est >= 1 ){
        while (titles.contains(title)) {
          postfix++;
          title = _prefix + postfix;
        }
        est--;
      }
      return title;
    }
  }

  /**
   * Get an automatically generated name.
   * @param edt
   * @return
   */
  public String getName(Editor edt) {
    return getName(edt, 1);
  }

}