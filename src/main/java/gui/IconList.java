package gui;

import bodies.BodyType;
import config.Config;
import javax.swing.ImageIcon;
import org.apache.commons.lang.StringUtils;

/**
 * List of icons using in project.
 *
 * @author alexeev
 */
public enum IconList {

  /**
   * Создание иконок для тел.
   * SIC! названия должны совпадать с названиями в BodyType.
   */
  CIRCLE("circle/pic (15).png"),
  CONE("cn_cyl/pic (34).png"),
  CONE_SECTION("section/cn_sec.png"),
  CUBE("regular polyhedron/pic (31).png"),
  CYLINDER("cn_cyl/pic (35).png"),
  CYLINDER_SECTION("section/pic (25).png"),
  LINE("1-dim/pic (48).png"),
  RAY("1-dim/ray_.png"),
  PLANE("plane/pic (7).png"),
  POINT("point/pic (62).png"),
  POLYGON("poly/poly.png"),
  PRISM("rib body/pic (29).png"),
  PYRAMID("rib body/pic (26).png"),
  RIB("1-dim/pic (49).png"),
  SPHERE("sphere/pic (1).png"),
  TETRAHEDRON("rib body/pic (25).png"),
  TRIANGLE("triangle/triang (2).png"),
  RECTANGLE("poly/rect.png"),
  RECT_TRIANGLE("poly/rect_tr.png"),
  RHOMBUS("poly/romb.png"),
  PARALLELOGRAM("poly/parallelog.png"),
  TRAPEZE("poly/trap.png"),
  DODECAHEDRON("regular polyhedron/pic (37).png"),
  TRUNCATED_OCTAHEDRON,
  RHOMBIC_DODECAHEDRON,
  ELONGATED_DODECAHEDRON,
  ICOSAHEDRON("regular polyhedron/pic (34).png"),
  OCTAHEDRON("regular polyhedron/pic (32).png"),
  STELLAR_OCTAHEDRON,
  REG_TETRAHEDRON("regular polyhedron/pic (33).png"),
  ELLIPSE("curve/pic (36).png"),
  HYPERBOLE("curve/pic (37).png"),
  PARABOLA("curve/pic (30).png"),
  CONIC("curve/co.png"),
  ELEMENTARYFUNCTION("curve/pic_elementary_function.png"),
  ANGLE("angle/pic (1).png"),
  ANGLE_SINGLE("angle_single.png"),
  ANGLE_DOUBLE("angle_double.png"),
  ANGLE_TRIPLE("angle_triple.png"),
  ANGLE_WAVED("angle_waved.png"),
  ARC("arc/pic (16).png"),
  ELLIPTIC_PARABOLOID,
  ELLIPSOID("curve body/el1.png"),
  HYPERBOLOID_OF_ONE_SHEET("curve body/hyp1.png"),
  HYPERBOLOID_OF_TWO_SHEET("curve body/hyp2.png"),
  INTERSECT_BODY,
  HEXPRISM,
  PARALLELEPIPED,
  PAIROFLINES,

  //file
  FILE_NEW("file/new.png"),
  FILE_OPEN("file/open.png"),
  FILE_SAVE("file/save.png"),
  FILE_EXIT("file/exit.png"),
  UNDO("file/undo.png"),
  REDO("file/redo.png"),
  SHOW_LOG("file/log2.png"),
  COLOR_SCH("file/color.png"),
  HELP("file/help2.png"),
  HELP_2("help.png"),
  OPTIONS("file/settings2.png"),
  COLORS_COLOR_SCH,
  PENSIL_COLOR_SCH,
  USER_COLOR_SCH,
  //browse mode
  MOVE("browse mode/curs.png"),
  MOVE_PNT("browse mode/curs_pnt.png"),
  MOVE_SCENE("browse mode/curs_sce.png"),
  ROTATE("browse mode/rotateview.png"),
  // point
  HULL_2("point/pic (620).png"),
  HULL_3("point/pic (19).png"),
  PNT_FACE("point/pic (17).png"),
  PNT_COOR("point/pic (63).png"),
  PNT_R("point/pic (616).png"),
  PNT_DISK("point/pic (13).png"),
  PNT_CIRC("point/pic (14).png"),
  PNT_ARC("point/pic (12).png"),
  PNT_LINE("point/pic (11).png"),
  PNT_SPHERE("point/pic (141).png"),
  // rib body
  PARALLEP("rib body/pic (24).png"),
  PYRAMID_BY_BASE_AND_TOP("rib body/pic (27).png"),
  REGULAR_PYRAMID("rib body/pic (33).png"),
  PRISM_BY_BASE_AND_TOP("rib body/pic (28).png"),
  REGULAR_PRISM("rib body/pic (32).png"),
  HEXREGULAR_PRISM("rib body/pic (32).png"),
  PYRAMID_RECTANG("rib body/req_pyr.png"),
  //cone
  CONE_BY_2_PNT("cn_cyl/cn_2_pnt.png"),
  //cylinder

  // sphere
  OUT_SPHERE("sphere/pic (19).png"),
  SPHERE_BY_POINT_AND_RADIUS("sphere/pic (1).png"),
  SPHERE_BY_2_POINTS("sphere/pic (14).png"),
  SPHERE_BY_4_POINTS("sphere/pic (13).png"),
  IN_SPHERE("sphere/pic (12).png"),
  TANGENT_PNT_TO_SPH("sphere/pic (20).png"),
  TANGENT_PLN_TO_SPH("sphere/pic (21).png"),
  // 2 order curve bodies
  PARABOLOID_BY_3_PNTS("curve body/par2.png"),
  PARABOLOID_BY_PARABOLA("curve body/par1.png"),
  // 2 order curves 
  FUNCTION("curve/pic f(x).png"),
  POLYNOMIAL("curve/pic_polynomial.png"),
  // plane
  PLANE_BY_PNT("plane/pic (3).png"),
  PLANE_BY_PNT_RIB("plane/pic (4).png"),
  PLANE_BY_3_POINTS("plane/pic (2).png"),
  PLANE_OXY("plane/pic (13).png"),
  PLANE_BY_PNT_LINE("plane/pic (5).png"),
  PlANE_BY_PNT_PARALL_CIRC("plane/pic (6).png"),
  PlANE_BY_LINE_PARALL_POLY("plane/pic (8).png"),
  PLANE_BY_PNT_ORTH_LINE("plane/pic (10).png"),
  PLANE_ANG_PLANE("plane/pic (9).png"),
  PLANE_BY_PNT_PARALL_LINE("plane/pic (11).png"),
  PLANE_BY_LINE_PARALL_LINE("plane/pic (12).png"),
  BISECTOR_PLANE("plane/pic (0).png"),
  TR_PLANE("plane/tr_pl.png"),// 24
  POL_PLANE("plane/pol_pl.png"),// 24
  // polygon
  REGULAR_POLYGON("poly/reg_poly.png"),
  SQUARE("poly/pic (26).png"),
  RHOMBUS_BY_SIDE("poly/pic (25).png"),
  ISOSCELES_TRAPEZE("poly/s_trap.png"),
  // 1-dimention
  LINE_PARAL_LINE("1-dim/pic (46).png"),
  LINE_BY_PNT_PARALL_POLY("1-dim/pic (414).png"),
  LINE_PARAL_PLANE("1-dim/.png"),
  LINE_ORT_LINE("1-dim/pic (47).png"),
  LINE_ORT_PLANE("1-dim/pic (413).png"),
  SKEW_LINES_DIST("1-dim/pic (411).png"),
  MID_PERP("1-dim/pic (41).png"),
  DISSECT_RIB("rib/rib_mid.png"),// 24
  RAY_TWO_POINTS("1-dim/ray2_.png"),
  RIB_BY_RIB("1-dim/e.png"),
  RIB_PROPORTIONALN_RIB("1-dim/e2.png"),
  RIB_BY_TWO_POINTS_AND_LENGTH("1-dim/e3.png"),
  // circle
  OUT_CIRCLE("circle/pic (19).png"),
  IN_CIRCLE("circle/pic (17).png"),
  CIRCLE_BY_3_POINTS("circle/pic (16).png"),
  CIRCLE_BY_PLANE("circle/pic (6).png"),
  TANGENT2("circle/pic (21).png"),
  TANGENT1("circle/pic (22).png"),
  CIRCLE_BY_2_POINTS("circle/pic (18).png"),
  TANG_POINT("circle/pic (20).png"),
  TANGENT_RIB("circle/pic (23).png"),
  CIRCLE_BY_DIAM_PNTS("circle/circ_d.png"),
  CIRCLE_BY_CENT("circle/circ.png"),
  TANGENT_LINE("circle/tang.png"),
  CIRCLE_IN_ANGLE("circle/cir1.png"),
  // arc
  ARC_HALF("arc/pic (17).png"),
  ARC_ON_CIRC("arc/pic (7).png"),
  // angle
  ANGLE_BIS("angle/pic (4).png"),
  ANGLE_BY_SIDE("angle/pic (3).png"),
  ANGLE_BIG_SIDE,
  // orthogonal
  PNT_PROJ_ON_LINE("orthogonal/pic (5).png"),
  PNT_PROJ_ON_PLANE("orthogonal/pic (22).png"),
  SPH_PROJ_ON_PLANE("orthogonal/pic (24).png"),
  RIB_PROJ_ON_PLANE("orthogonal/pic (23).png"),
  PNT_PROJ_ON_FACE("orthogonal/pic (22).png"),
  // section
  PLANE_SECTION("section/pic (25).png"),
  FACE_SECTION("section/pic (27).png"),
  RIB_SECTION("section/pic (28).png"),
  LINE_SECTION("section/pic (26).png"),
  BODY_INTERSECT("section/pic (30).png"),
  CURVE_INTERSECT("section/pic (22).png"),
  // space conversion
  PNT_ROTATION("space conversion/pic (23).png"),
  ROTATION("space conversion/pic (23).png"),
  SYMMETRY("space conversion/pic (22).png"),
  HOMOTHETY("space conversion/pic (9).png"),
  INVERSION("space conversion/pic ().png"),
  PARALLEL_TRANSLATION("space conversion/pic (10).png"),
  // action
  DELETE("action/del.png"),
  ACTION_HIDE("action/hide.png"),
  ACTION_SHOW("action/show.png"),
  RENAME("action/pic (31).png"),
  // triangle 24
  VIEW_IN_FRONT_OF_TRIANG("triangle/view.png"),
  MEDIAN("triangle/med.png"),
  HEIGHT("triangle/hi.png"),
  BISECTRIX("triangle/bis.png"),
  MIDDLE_PERPENDICULAR("triangle/mid_per.png"),
  CIRCLE_IN_TRIANGLE("triangle/in_c.png"),
  CIRCLE_OUT_TRIANGLE("triangle/out_c.png"),
  CIRCLE_EX_TRIANGLE("triangle/ex1.png"),
  CIRCLE_TRIANGLE("triangle/c.png"),
  POINT_TRIANGLE("triangle/p.png"),
  CHEVIANA_TRIANGLE("triangle/che.png"),
  CENTROID,
  ORTHOCENTER,
  // ?
  MODE_3D_ON("3d_icon_on.png"),
  MODE_3D_OFF("3d_icon_off.png"),
  MODE_STL_ON("stl_on.png"),
  MODE_STL_OFF("stl_off.png"),
  KEYBOARD_ON("keyboard_on.png"),
  KEYBOARD_OFF("keyboard_off.png"),
  TREE_OPEN("tree-open.png"),
  TREE_CLOSE("tree-close.png"),
  SHOW_LABEL("show_label.png"),
  HIDE_LABEL("hidelabel.gif"),
  CROSS("cross.png"),
  SHOWN("circle.png"),
  HIDDEN("circle-stroked.png"),
  EMPTY,
  KEY("key-icon.png"),
  ALTHELP("althelp.png"),
  CURSOR_GRAB("cursor/pad.png"),
  CURSOR_GRABBING("cursor_grabbing.gif"),
  TRIANGLE_UP("triangle-up.png"),
  TRIANGLE_DOWN("triangle-down.png"),
  TRIANGLE_RIGHT("triangle-right.png"),
  SHOW_GRID("grid.gif"),
  SHOW_AXES("axes.gif"),
  BORDER("square-stroked.png"),
  MAGNET("magnet.gif"),
  ZOOM_IN("zoomin.gif"),
  ZOOM_OUT("zoomout.gif"),
  FILL("water.png"),
  WASTE("waste-basket.png"),
  ERASER("eraser.png"),
  UNLIM_PL("pl.png"),
  OPEN_2D("2d_enter.png"),
  OPEN_3D("3d_enter.png"),
  SPLASH("splash.jpg"),
  LOAD_ANIMATED("load.gif"),
  CUBOCTAHEDRON,
  MESH3D("regular polyhedron/pic (33).png")
  ;

  public static IconList getByBodyType(BodyType type) {
    return IconList.valueOf(type.name());
  }

  public static ImageIcon getSmallIcon(BodyType type) {
    return getByBodyType(type).getSmallIcon();
  }

  public static ImageIcon getLargeIcon(BodyType type) {
    return getByBodyType(type).getLargeIcon();
  }

  /**
   * Get key button image.
   * @param key
   *  key name.
   * @return 
   */
  public static ImageIcon getKeyImage(String key) {
    return new ImageIcon(Config.USER_ICONS_DIR + "/24/keys/" + StringUtils.upperCase(key) + ".png");
  }
  
  private String _path;

  private IconList(String path) {
    _path = path;
  }

  private IconList() {
    this("empty.gif");
  }

  /**
   * Get 12x12 icon.
   * @return
   */
  public ImageIcon getTinyIcon() {
    return new ImageIcon(Config.USER_ICONS_DIR + "/12/" + _path);
  }

  /**
   * Get 16x16 or 18x18 icon.
   * @return
   */
  public ImageIcon getSmallIcon() {
    return new ImageIcon(Config.USER_ICONS_DIR + "/18/" + _path);
  }

  /**
   * Get 24x24 icon.
   * @return
   */
  public ImageIcon getMediumIcon() {
    return new ImageIcon(Config.USER_ICONS_DIR + "/24/" + _path);
  }

  /**
   * Get 32x32 icon.
   * @return
   */
  public ImageIcon getLargeIcon() {
    return new ImageIcon(Config.USER_ICONS_DIR + "/32/" + _path);
  }

  /**
   * Get icon from images folder.
   * @return
   */
  public ImageIcon getImage() {
    return new ImageIcon(Config.USER_ICONS_DIR + "/images/" + _path);
  }

}
