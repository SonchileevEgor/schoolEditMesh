package editor;

import anchors.*;
import opengl.colorgl.ColorGL;
import bodies.BodyType;
import bodies.CircleBody;
import bodies.PointBody;
import bodies.PolygonBody;
import bodies.RibBody;
import bodies.TriangleBody;
import builders.AllBuildersManager;
import static config.Config.LOG_LEVEL;
import editor.state.AnchorManager;
import editor.state.AnchorState;
import editor.state.BodyState;
import editor.state.BodyStateManager;
import editor.state.DisplayParam;
import editor.state.EntityState;
import error.SilentErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Polygon3d;
import geom.Rib3d;
import geom.Triang3d;
import geom.Vect3d;
import error.i_ErrorHandler;
import java.util.ArrayList;
import java.util.List;
import maquettes.CoverContainer;
import maquettes.i_Cover;
import minjson.JsonArray;
import minjson.JsonObject;
import minjson.JsonValue;
import opengl.colortheme.CurrentTheme;
import util.Log;

/**
 * The top level editor implementation.
 *
 * @author ltwood
 */
public class Editor {
  private final BodyBuilderContainer _bb = new BodyBuilderContainer();
  private final i_BodyContainer _bd = new BodyContainer();
  private final i_AnchorContainer _anchors = new AnchorContainer();
  private final AnchorManager _anchMgr = new AnchorManager();
  private final BodyStateManager _bodyMgr = new BodyStateManager();
  private final CoverContainer _cc = new CoverContainer();
  private i_ErrorHandler _defaultErrorHandler = new SilentErrorHandler();

  public Editor() {
    config.Temp.CURRENT_FILE = null;
    config.Temp.FILE_SAVED = false;
  }

  public BodyBuilderContainer bb() {
    return _bb;
  }

  public i_BodyContainer bd() {
    return _bd;
  }

  public CoverContainer covers() {
      return _cc;
  }
  
  public i_AnchorContainer anchors() {
    return _anchors;
  }

  public AnchorManager anchMgr() {
    return _anchMgr;
  }

  public BodyStateManager bodyMgr() {
    return _bodyMgr;
  }

  public void clear() {
    _bb.clear();
    _bd.clear();
    _cc.clear();
    _anchors.clear();
    _anchMgr.clear();
    _bodyMgr.clear();
    config.Temp.CURRENT_FILE = null;
    config.Temp.FILE_SAVED = false;
  }

  public void setDefaultErrorHandler(i_ErrorHandler eh) {
    _defaultErrorHandler = eh;
  }

  /**
   * Load editor from Undo / Redo state.
   *
   * @param urstate Undo / Redo state.
   */
  public final void loadFromState(URState urstate) {
    if (LOG_LEVEL.value() >= 1) {
      Log.out.printf("BodyContainer: loading state...%n");
    }
    clear();
    _bodyMgr.loadJson(urstate.getGraphicState());
    _anchMgr.loadJson(urstate.getAnchorState());
    JsonArray st = urstate.getState();

    int count = st.size();
    for (int j = 0; j < count; j++) {
      JsonObject jobj = st.get(j).asObject();
      String id = jobj.get("id").asString();
      String type = jobj.get("type").asString();
      
      // Ключа "title" может и не быть, например, для многоугольников
      //!! TODO: внести имя в список параметров билдера в JSON-формате
      JsonValue titleValue = jobj.get("title");
      String title = (titleValue == null) ? "" : titleValue.asString();
      
      JsonObject param = jobj.get("params").asObject();

      i_BodyBuilder bb = AllBuildersManager.createFromJson(id, type, title, param);
      if (bb != null){
        _bb.add(bb);
        if (LOG_LEVEL.value() >= 2) {
          Log.out.printf("BodyContainer: add: type=<%s> id=<%s>%n", type, title);
        }
        i_Body b = bb.create(this, _defaultErrorHandler);
        addBody(b);
      }
    }
    Log.out.printf("BodyContainer: loading completed%n");
  }

  public void writeToFile(String filename){
    new URState(this).writeToFile(filename);
  }

  /**
   * Trying to add BodyBuilder to Editor
   * if body cannot be created:
   * <strong>if nullable = TRUE</strong> add the null-body
   * <strong>otherwise</strong> don't add anything
   * @param bb BodyBuilder to add.
   * @param eh Error handler (null allowed).
   * @param nullable
   * @return error status.
   */
  public boolean add(i_BodyBuilder bb, i_ErrorHandler eh, boolean nullable) {
    i_Body bd = (eh == null) ? bb.create(this, _defaultErrorHandler) : bb.create(this, eh);
    if (nullable || bd.exists()) {
      _bb.add(bb);
      addBody(bd);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Перестроение класса Editor.
   * Если какое-либо тело не может быть построено,
   * то добавляется "тело-пустышка".
   *
   * @param eh Error handler (null allowed).
   */
  public void rebuild(i_ErrorHandler eh) {
    boolean useHandler = (eh != null);
    _bd.clear();
    _anchors.clear();
    for (int j = 0; j < _bb.len(); j++){
      i_BodyBuilder bb = _bb.get(j);
      i_Body bd = useHandler ? bb.create(this, eh) : bb.create(this, _defaultErrorHandler);
      addBody(bd);
    }
  }

  /**
   * Rebuild editor from builder with given ID.
   * @param builderID
   */
  /*public void rebuild(String builderID) {

  }*/

  /**
   * Get body by ID.
   * @param bodyID
   * @return
   * @throws editor.ExNoBody
   */
  public final i_Body getBody(String bodyID) throws ExNoBody {
    return _bd.get(bodyID);
  }

  /**
   * Remove body by ID.
   * @param bodyID
   */
  public final void removeBody(String bodyID) {
    List<String> bodies = new ArrayList<String>();
    bodies.add(bodyID);
    removeBodies(bodies);
  }

  /**
   * Remove list of bodies.
   * @param bodyIDs
   */
  public final void removeBodies(List<String> bodyIDs) {
    for (String bodyID : bodyIDs) {
      _bb.remove(bodyID);
    }

    _bd.clear();
    _anchors.clear();

    ArrayList<String> listToRemove = new ArrayList<String>();
    for (int j = 0; j < _bb.len(); j++){
      i_BodyBuilder bb = _bb.get(j);
      i_Body bd = bb.create(this, _defaultErrorHandler);
      if(bd.exists()){
        addBody(bd);
      } else {
        listToRemove.add(bb.id());
      }
    }

    for (String del : listToRemove) {
      _bb.remove(del);
    }

    //!! TODO: организовать нормальную обработку несозданных тел
  }

  /**
   * Add body into container.
   * Link body state with body.
   * @param b
   * @param state
   */
  private void addBody(i_Body b) {
    BodyState state;
    if (!_bodyMgr.containsKey(b.id())){
      state = new BodyState(b.type());
      _bodyMgr.add(b.id(), state);
    } else {
      state = _bodyMgr.getState(b.id());
    }
    b.setState(state);
    _bd.add(b);
  }

  /**
   * Add anchor into container. Link body with the new anchor.
   * @param point anchor coords
   * @param body body linked to this anchor
   * @param designation designation of anchor
   * @param anchorName name of anchor (can be null)
   */
  public void addAnchor(Vect3d point, i_Body body, String designation, String anchorName) {
    /**
     * Логика распределения имён якорей.
     * Якорь получает формат [тип якоря][ID породившего тела][n]
     * где [n] = количество якорей данного типа, порождённых данным телом плюс 1.
     */
    String bodyID = body.id();
    int index = _anchors.getAnchors(bodyID, AnchorType.ANC_POINT).size() + 1;
    String anchorID = "PNT" + bodyID + index;

    AnchorState state;
    StringBuilder anchTitle;
    if (!_anchMgr.containsKey(anchorID)){
      state = new AnchorState(AnchorType.ANC_POINT);
      if( anchorName == null ) {
        anchorName = BodyType.POINT.getName(this);
      }
      anchTitle = new StringBuilder(anchorName);
      _anchMgr.add(anchorID, anchTitle, state);
    } else {
      state = _anchMgr.getState(anchorID);
      anchTitle = _anchMgr.getTitleRef(anchorID);
    }

    i_Anchor a = new PointAnchor(point, anchorID, bodyID, anchTitle, state);
    _anchors.add(a);

    // Дублируем каждый якорь телом.
    if (body.type() != BodyType.POINT) {
      PointBody pt = new PointBody("CLONE" + anchorID, point);
      pt.addAnchor("P", anchorID);
      pt.setHasBuilder(false);
      addBody(pt);
    }

    body.addAnchor(designation, a.id());
  }

  /**
   * Add anchor-rib into container. Link body with the new anchor.
   * @param rib anchor
   * @param id1 anchor-point 1
   * @param id2 anchor-point 2
   * @param body body linked to this anchor
   * @param designation designation of anchor
   */
  public void addAnchor(Rib3d rib, String id1, String id2, i_Body body, String designation) {
    /**
     * Логика распределения имён якорей.
     * Якорь получает формат [тип якоря][ID породившего тела][n]
     * где [n] = количество якорей данного типа, порождённых данным телом плюс 1.
     */
    i_Anchor b = _anchors.findEqual(id1, id2);

    // bind anchor to the body.
    if (b == null) {
      // If not duplicated, create new anchor.
      String bodyID = body.id();
      int index = _anchors.getAnchors(bodyID, AnchorType.ANC_RIB).size() + 1;
      String anchorID = "RIB" + bodyID + index;

      AnchorState state;
      if (!_anchMgr.containsKey(anchorID)){
        state = new AnchorState(AnchorType.ANC_RIB);
        _anchMgr.add(anchorID, new StringBuilder(), state);
      } else {
        state = _anchMgr.getState(anchorID);
      }

      i_Anchor a = new RibAnchor(rib, id1, id2, anchorID, bodyID, state);
      _anchors.add(a);

      // Дублируем каждый якорь телом.
      if (body.type() != BodyType.RIB) {
        RibBody r = new RibBody("CLONE" + anchorID, rib);
        r.addAnchor(RibBody.BODY_KEY_A, id1);
        r.addAnchor(RibBody.BODY_KEY_B, id2);
        r.addAnchor(RibBody.BODY_KEY_RIB, anchorID);
        r.setHasBuilder(false);
        addBody(r);
      }

      body.addAnchor(designation, a.id());
    } else {
      body.addAnchor(designation, b.id());
    }
  }

  /**
   * Add anchor-rib into container. Link body with the new anchor.
   * @param poly anchor
   * @param pointIDs array of anchor-point IDs
   * @param body body linked to this anchor
   * @param designation designation of anchor
   */
  public void addAnchor(Polygon3d poly, ArrayList<String> pointIDs, i_Body body, String designation) {
    /**
     * Логика распределения имён якорей.
     * Якорь получает формат [тип якоря][ID породившего тела][n]
     * где [n] = количество якорей данного типа, порождённых данным телом плюс 1.
     */
    i_Anchor b = _anchors.findEqual(pointIDs);

    // привязываем якорь к телу
    if(b == null) {// Если не было его еще, то добавляем
      String bodyID = body.id();
      int index = _anchors.getAnchors(bodyID, AnchorType.ANC_POLY).size() + 1;
      String anchorID = "POL" + bodyID + index;

      AnchorState state;
      StringBuilder anchTitle;
      if (!_anchMgr.containsKey(anchorID)){
        state = new AnchorState(AnchorType.ANC_POLY);
        anchTitle = new StringBuilder();
        _anchMgr.add(anchorID, anchTitle, state);
      } else {
        state = _anchMgr.getState(anchorID);
      }

      i_Anchor a = new PolyAnchor(poly, pointIDs, anchorID, bodyID, state);
      _anchors.add(a);

      // Дублируем каждый якорь телом.
      if (!body.type().isPolygon()) {
        try {
          if (pointIDs.size() == 3) {
            Vect3d vertA = a.getPoly().points().get(0);
            Vect3d vertB = a.getPoly().points().get(1);
            Vect3d vertC = a.getPoly().points().get(2);
            String idA = pointIDs.get(0);
            String idB = pointIDs.get(1);
            String idC = pointIDs.get(2);

            TriangleBody tr = new TriangleBody("CLONE" + anchorID,
                    new Triang3d(vertA, vertB, vertC));
            tr.addAnchor("A", idA);
            tr.addAnchor("B", idB);
            tr.addAnchor("C", idC);

            Rib3d rib1 = new Rib3d(vertA, vertB);
            addAnchor(rib1, idA, idB, tr, "rib_AB");
            Rib3d rib2 = new Rib3d(vertA, vertC);
            addAnchor(rib2, idA, idC, tr, "rib_AC");
            Rib3d rib3 = new Rib3d(vertB, vertC);
            addAnchor(rib3, idB, idC, tr, "rib_BC");

            tr.addAnchor("facet", anchorID);
            tr.setHasBuilder(false);

            addBody(tr);
          } else {
            PolygonBody polBody = new PolygonBody("CLONE" + anchorID, poly);
            ArrayList<Vect3d> points = poly.points();
            int num = poly.vertNumber();
            for(int i = 0; i < num; i++){
              String idA = pointIDs.get(i);
              String idB = pointIDs.get((i + 1) % num);
              Rib3d rib = new Rib3d(points.get(i), points.get((i + 1) % num));
              polBody.addAnchor(String.valueOf(i), idA);
              addAnchor(rib, idA, idB, polBody, "rib_" + String.valueOf(i) + String.valueOf((i + 1) % num));
            }
            polBody.addAnchor("facet", anchorID);
            polBody.setHasBuilder(false);

            addBody(polBody);
          }
        } catch (ExDegeneration ex) { }
      }

      body.addAnchor(designation, a.id());
    }
    else {
      body.addAnchor(designation, b.id());
    }
  }
  /**
   * Логика распределения имён якорей.
   * Якорь получает формат [тип якоря][ID породившего тела][n]
   * где [n] = количество якорей данного типа, порождённых данным телом плюс 1.
   * @param disk
   * @param centerID center of disk
   * @param body
   * @param designation
   * @param anchorName
   */
  public void addAnchor(Circle3d disk, String centerID, i_Body body, String designation, String anchorName) {
    String bodyID = body.id();
    int index = _anchors.getAnchors(bodyID, AnchorType.ANC_DISK).size() + 1;
    String anchorID = "DISK" + bodyID + index; // build anchor ID

    AnchorState state;
    StringBuilder title;
    if (!_anchMgr.containsKey(anchorID)){
      state = new AnchorState(AnchorType.ANC_DISK);
      title = new StringBuilder(anchorName);
      _anchMgr.add(anchorID, title, state);
    } else {
      state = _anchMgr.getState(anchorID);
      title = _anchMgr.getTitleRef(anchorName);
    }

    i_Anchor a = new DiskAnchor(disk, centerID, anchorID, bodyID, title, state);
    // привязываем якорь к телу
    _anchors.add(a);

    // Дублируем каждый якорь телом.
    if (body.type() != BodyType.CIRCLE) {
      CircleBody circ = new CircleBody("CLONE" + anchorID, _anchMgr.getTitleValue(anchorID), disk);
      circ.addAnchor("disk", anchorID);
      circ.setHasBuilder(false);
      addBody(circ);
    }

    body.addAnchor(designation, a.id());
  }

  /**
   * Add anchor-point into container.
   * Name of anchor generated by default.
   */
  public void addAnchor(Vect3d point, i_Body body, String designation) {
    addAnchor(point, body, designation, null);
  }

  /**
   * Add anchor-disk into container with default state.
   * Name of anchor generated from body title.
   */
  public void addAnchor(Circle3d disk, String centerID, i_Body body, String designation) {
    String anchorName = body.getTitle() + "." + designation;
    addAnchor(disk, centerID, body, designation, anchorName);
  }

  public void addCover(i_Cover cover) {
      _cc.add(cover);
  }
  
  /**
   * Возвращает цвет поверхности тела.
   * Если тело не имеет поверхности, возвращает стандартный цвет.
   * Если тело разноцветное, возвращает первый из цветов (при переборе якорей).
   * @param bodyID
   * @return
   */
  public ColorGL getBodySurfaceColor(String bodyID) {
    try {
      i_Body bd = _bd.get(bodyID);
      BodyState bs = bd.getState();
      if (bs.hasParam(DisplayParam.FILL_COLOR))
        return (ColorGL)bs.getParam(DisplayParam.FILL_COLOR);
      for (String anchorID : bd.getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          if (a.getAnchorType() == AnchorType.ANC_POLY ||
              a.getAnchorType() == AnchorType.ANC_DISK) {
            return (ColorGL)a.getState().getParam(DisplayParam.FILL_COLOR);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
    return CurrentTheme.getColorTheme().getFacetsFiguresColorGL();
  }

  /**
   * Возвращает цвет каркаса тела.
   * Если тело не имеет каркаса, возвращает стандартный цвет.
   * Если тело разноцветное, возвращает первый из цветов (при переборе якорей).
   * @param bodyID
   * @return
   */
  public ColorGL getBodyCarcassColor(String bodyID){
    try {
      i_Body bd = _bd.get(bodyID);
      BodyState bs = bd.getState();
      if (bs.hasParam(DisplayParam.CARCASS_COLOR))
        return (ColorGL)bs.getParam(DisplayParam.CARCASS_COLOR);
      for (String anchorID : bd.getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          if (a.getAnchorType() == AnchorType.ANC_RIB ||
              a.getAnchorType() == AnchorType.ANC_DISK) {
            return (ColorGL)a.getState().getParam(DisplayParam.CARCASS_COLOR);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
    return CurrentTheme.getColorTheme().getCarcassFiguresColorGL();
  }

  /**
   * Возвращает цвет точек тела.
   * Если цветов несколько, возвращает первый попавшийся.
   * Если точек нет, возвращает стандартный цвет темы для точек.
   * @param bodyID
   * @return
   */
  public ColorGL getBodyPointsColor(String bodyID){
    try {
      i_Body bd = _bd.get(bodyID);
      for (String anchorID : bd.getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          if (a.getAnchorType() == AnchorType.ANC_POINT) {
            return (ColorGL)a.getState().getParam(DisplayParam.POINT_COLOR);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
    return CurrentTheme.getColorTheme().getPointsColorGL();
  }

  /**
   * Меняет цвет точек, являющихся якорями данного тела.
   * @param bodyID
   * @param color
   */
  public void setBodyPointsColor(String bodyID, ColorGL color) {
    try {
      i_Body bd = _bd.get(bodyID);
      for (String anchorID : bd.getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          if (a.getAnchorType() == AnchorType.ANC_POINT) {
            a.getState().setParam(DisplayParam.POINT_COLOR, color);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
  }

  /**
   * Установить видимость поверхности тела.
   * @param bodyID
   * @param isFilled
   * @throws ExNoBody
   */
  public void setBodyFill(String bodyID, boolean isFilled) throws ExNoBody {
    EntityState bs = _bodyMgr.getState(bodyID);
    if (bs.hasParam(DisplayParam.FILL_VISIBLE))
      bs.setParam(DisplayParam.FILL_VISIBLE, isFilled);
    for( String anchorID : _bd.get(bodyID).getAnchors().values() ){
      try {
        i_Anchor a = _anchors.get(anchorID);
        if (a.getAnchorType() == AnchorType.ANC_POLY) {
          a.getState().setParam(DisplayParam.VISIBLE, isFilled);
        } else if (a.getAnchorType() == AnchorType.ANC_DISK) {
          a.getState().setParam(DisplayParam.FILL_VISIBLE, isFilled);
        }
      } catch (ExNoAnchor ex) {}
    }
  }

  /**
   * Установить цвет поверхности тела.
   * Устанавливается цвет тела, после чего все якоря-грани,
   * принадлежащие этому телу, также перекрашиваются.
   * @param bodyID
   * @param c
   */
  public void setBodyFacetColor(String bodyID, ColorGL c) {
    EntityState bs = _bodyMgr.getState(bodyID);
    if (bs.hasParam(DisplayParam.FILL_COLOR))
      bs.setParam(DisplayParam.FILL_COLOR, c);
    try {
      for (String anchorID : _bd.get(bodyID).getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          if (a.getAnchorType() == AnchorType.ANC_POLY ||
              a.getAnchorType() == AnchorType.ANC_DISK) {
            a.getState().setParam(DisplayParam.FILL_COLOR, c);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
  }

  /**
   * Установить цвет каркаса тела.
   * Устанавливается цвет каркаса тела, после чего все якоря-рёбра,
   * принадлежащие этому телу, также перекрашиваются.
   * @param bodyID
   * @param c
   */
  public void setBodyCarcassColor(String bodyID, ColorGL c) {
    BodyState bs = _bodyMgr.getState(bodyID);
    if (bs.hasParam(DisplayParam.CARCASS_COLOR))
      bs.setParam(DisplayParam.CARCASS_COLOR, c);
    try {
      for (String anchorID : _bd.get(bodyID).getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          AnchorType type = a.getAnchorType();
          if (type == AnchorType.ANC_RIB || type == AnchorType.ANC_DISK) {
            a.getState().setParam(DisplayParam.CARCASS_COLOR, c);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
  }

  /**
   * Скрыть или отобразить грани данного тела.
   * @param bodyID
   * @param visible
   */
  public void setBodyFacetsVisible(String bodyID, boolean visible) throws ExNoBody {
    BodyState bs = _bodyMgr.getState(bodyID);
    if (bs.hasParam(DisplayParam.FILL_VISIBLE))
      bs.setParam(DisplayParam.FILL_VISIBLE, visible);
    for (String anchorID : _bd.get(bodyID).getAnchors().values()) {
      try {
        i_Anchor a = _anchors.get(anchorID);
        if (_anchors.get(anchorID).getAnchorType() == AnchorType.ANC_POLY) {
          a.setVisible(visible);
        }
      } catch (ExNoAnchor ex) {}
    }
  }

  /**
   * Проверяем, можно ли переименовать точку-тело.
   * Новое имя должно отличаться от всех имён тел и якорей.
   *
   * @param bodyID
   * @param newTitle
   * @return true, если переименование возможно, либо новое имя совпадает со старым.<br>
   * false, если переименование невозможно.
   *
   * <strong>!!Предполагается, что обозначения якоря и точки совпадают изначально!!</strong>
   * @throws editor.ExNoBody
   */
  public boolean isPointNameValid(String bodyID, String newTitle) throws ExNoBody {
    // имя должно быть непустым
    if (newTitle.isEmpty())
      return false;

    String oldTitle = _bd.get(bodyID).getTitle();
    // имя не поменяется
    if (newTitle.equals(oldTitle))
      return true;
    // имя тела уже используется
    if (_bd.hasTitle(newTitle))
      return false;
    // имя якоря уже используется
    if (_anchors.hasTitle(newTitle))
      return false;

    return true;
  }

  /**
   * Показана ли поверхность у выбранного тела.
   * Если поверхности нет - то false.
   * Если поверхность залита не полностью - то false.
   * Если тело не найдено - то false.
   * @param bodyID
   * @return
   */
  public boolean isBodyFilled(String bodyID){
    try {
      i_Body bd = _bd.get(bodyID);
      BodyState bs = bd.getState();
      if (bs.hasParam(DisplayParam.FILL_VISIBLE)) {
        return (boolean)bs.getParam(DisplayParam.FILL_VISIBLE);
      }
      for (String anchorID : _bd.get(bodyID).getAnchors().values()) {
        try {
          i_Anchor a = _anchors.get(anchorID);
          if (a.getAnchorType() == AnchorType.ANC_POLY) {
            if (!a.getState().isVisible()) {
              // Если какая-то часть поверхности не залита,
              // считаем, что она не залита.
              return false;
            }
          }
          if (a.getAnchorType() == AnchorType.ANC_DISK) {
            if (!a.getState().isVisible() || !a.getState().isFilled()) {
              return false;
            }
          }
        } catch (ExNoAnchor ex) {}
      }
      return true;
    } catch(ExNoBody ex) {}
    return false;
  }

  public String getAnchorID(String bodyID, String anchorKey) throws ExNoBody {
    return _bd.get(bodyID).getAnchorID(anchorKey);
  }

  /**
   * Get anchor by body ID and key of the anchor.
   * @param bodyID
   * @param anchorKey
   * @return
   * @throws ExNoAnchor
   * @throws ExNoBody
   */
  public i_Anchor getAnchor(String bodyID, String anchorKey) throws ExNoAnchor, ExNoBody {
    return _anchors.get(getAnchorID(bodyID, anchorKey));
  }

  /**
   * Название якоря, выводимое в GUI.
   * @param anchorID
   * @return
   * @throws editor.ExNoAnchor
   */
  public String getAnchorTitle(String anchorID) throws ExNoAnchor {
    i_Anchor a = _anchors.get(anchorID);
    switch (a.getAnchorType()) {
      case ANC_POINT: // see next
      case ANC_DISK:
        return _anchMgr.getTitleValue(anchorID);
      case ANC_RIB:
        ArrayList<String> ids = a.arrayIDs();
        return getAnchorTitle(ids.get(0)) + getAnchorTitle(ids.get(1));
      case ANC_POLY:
        StringBuilder result = new StringBuilder();
        for (String id : a.arrayIDs()) {
          result.append(getAnchorTitle(id));
        }
        return result.toString();
      default:
        throw new AssertionError(a.getAnchorType().name());
    }
  }
  
  public String getAnchorByTitle(String title) throws ExNoAnchor {
    for( i_Anchor a : _anchors.getAnchors() ){
      if( getAnchorTitle(a.id()).equals(title) ){
        return a.id();
      }
    }
    throw new ExNoAnchor("Нет якоря с именем <" + title + ">");
  }

  /**
   * Название якоря, выводимое в UI.
   *
   * @param bodyID  ID тела
   * @param anchorKey  ключ привязки якоря к телу
   * @return
   * @throws ExNoBody
   * @throws ExNoAnchor
   */
  public String getAnchorTitle(String bodyID, String anchorKey) throws ExNoBody, ExNoAnchor {
    return getAnchorTitle(getAnchorID(bodyID, anchorKey));
  }

  /**
   * Расстояние от нуля до наиболее удалённой от центра точки.
   * (В проекции на какую-либо из координатных осей)
   *
   * @return
   */
  public double getMaxDistance() {
    double result = 0;
    for (i_Anchor a : _anchors.getPointAnchors()) {
      result = Math.max(Math.max(Math.abs(a.getPoint().x()), Math.abs(a.getPoint().y())),
                        Math.max(Math.abs(a.getPoint().z()), result));
    }
    return result;
  }

    public i_Cover getCover(String id) {
        return _cc.get(id);
    }
};
