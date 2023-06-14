package gui;

import bodies.BodyType;
import bodies.PointBody;
import builders.BodyBuilder;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import static config.Config.EXTENSIONS;
import static config.Config.LOG_LEVEL;
import static config.Config.PRECISION;
import static config.Config.SCREEN_KEYBOARD_ON;
import config.LastOpenedScenesManager;
import editor.AnchorType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.ExNoBuilder;
import editor.InvalidBodyNameException;
import editor.URState;
import editor.UndoRedo;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_BodyContainer;
import editor.i_EditorChangeListener;
import editor.state.AnchorState;
import editor.state.BodyState;
import editor.state.DisplayParam;
import editor.state.i_AnchorStateChangeListener;
import editor.state.i_BodyStateChangeListener;
import error.i_ErrorHandler;
import gui.dialog.AppSettingsDialog;
import gui.dialog.BodySettingsDialog;
import gui.extensions.ExtensionManager;
import gui.inspector.EdtFocusController;
import gui.inspector.bodycontext.ContextMenuLauncher;
import gui.keyboard.KeyboardPanel;
import gui.layout.EdtLayoutController;
import gui.statusbar.StatusStrip;
import gui.ui.EdtColorChooserDialog;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import maquettes.i_Cover;
import opengl.scenegl.SceneGL;
import opengl.scenegl.i_GlobalModeChangeListener;
import util.Log;

/**
 * Main controller of the 3D-editor.
 */
public class EdtController {
  private final Editor _edt;
  private MainEdtCanvasController _canvas;
  private EdtLayoutController _layout;
  private ErrorController _error;
  private EdtFocusController _focusCtrl;
  private UndoRedo _ur = new UndoRedo();
  private ArrayList<i_EditorChangeListener> _editorStateListeners = new ArrayList<>();
  private ArrayList<i_AnchorStateChangeListener> _anchorStateListeners = new ArrayList<>();
  private ArrayList<i_BodyStateChangeListener> _bodyStateListeners = new ArrayList<>();
  private ArrayList<i_GlobalModeChangeListener> _modeChangeListeners = new ArrayList<>();
  private ArrayList<i_AppSettingsChangeListener> _appSettingsListeners = new ArrayList<>();
  private KeyboardPanel _keyboard;
  private BodySettingsDialog _bodySettingsDialog;
  private AppSettingsDialog _appSettingsDialog;
  private EdtColorChooserDialog _colorChooserDialog;
  private StlEdtCanvasController _stlCanvas;
  private ExtensionManager _extensions;

  /**
   * Build main controller.
   * @param path Path to scene file (can be null).
   */
  public EdtController(String path) {
    _edt = new Editor();
    if (path != null) {
      _edt.loadFromState(new URState(path));
    }
    _ur.reload(_edt);
    _focusCtrl = new EdtFocusController(this);
    // keyboard initialization is delayed by the reason of GUI parameters initialization
    _keyboard = null;
    _error = new ErrorController(this);
    
    _extensions = new ExtensionManager();
    // loading state of extension manager from configuration.
    _extensions.fromJson(EXTENSIONS.value());
    // save new state of extension manager to configuration.
    _extensions.saveState();
    
    // set status strip handler as default editor error handler
    _edt.setDefaultErrorHandler(_error.getStatusStripHandler());
    if (SCREEN_KEYBOARD_ON.value()) {
      _keyboard.setVisible(true);
    }
  }

  /**
   * Build main controller.
   * With default initial scene.
   */
  public EdtController() {
    this(null);
  }

  /**
   * Call this method after gui settings are installed.
   */
  public void initDialogs() {
    _colorChooserDialog = new EdtColorChooserDialog(_layout.getFrame());
    _bodySettingsDialog = new BodySettingsDialog(this);
    _appSettingsDialog = new AppSettingsDialog(this);
    addAppSettingsChangeListener(_appSettingsDialog);
    addGlobalModeChangeListener(_appSettingsDialog);
  }

  public Editor getEditor() {
    return _edt;
  }

  public void clearEditor() {
    _edt.clear();
    _ur.reload(_edt);
  }

  public void loadFile( String filename ) {
    _edt.clear();
    _edt.loadFromState(new URState(filename));
    _ur.reload(_edt);
    config.Temp.CURRENT_FILE = filename;
    config.Temp.FILE_SAVED = true;
    LastOpenedScenesManager.add(filename);
  }

  public MainEdtCanvasController getMainCanvasCtrl() {
    return _canvas;
  }

  public StlEdtCanvasController getStlCanvasCtrl() {
      return _stlCanvas;
  }

  public EdtFocusController getFocusCtrl() {
    return _focusCtrl;
  }

  public SceneGL getScene() {
    return _canvas.getScene();
  }

  public KeyboardPanel getKeyboard() {
    if (_keyboard == null) {
      _keyboard = KeyboardPanel.create(this);
    }
    return _keyboard;
  }

  /**
   * Get error controller.
   * @return
   */
  public ErrorController error() {
    return _error;
  }

  /**
   * Get body by given ID.
   * @param id
   * @return
   * @throws editor.ExNoBody
   */
  public i_Body getBody(String id) throws ExNoBody {
    return _edt.getBody(id);
  }

  /**
   * Get builder by given ID.
   * @param id
   * @return
   * @throws editor.ExNoBuilder
   */
  public final i_BodyBuilder getBuilder(String id) throws ExNoBuilder {
    return _edt.bb().get(id);
  }

  /**
   * Get anchor by given ID.
   * @param id
   * @return
   * @throws ExNoAnchor
   */
  public i_Anchor getAnchor(String id) throws ExNoAnchor {
    return _edt.anchors().get(id);
  }

  /**
   * Get ID of body, or ID of anchor if body is a point, rib, polygon or circle.
   * Needed for create body modes.
   * @param id
   * @return
   *  id,       if body is not a point, rib, circle or polygon
   *  anchorID, instead
   */
  public String getAnchorOrBodyID(String id) throws ExNoBody, ExNoAnchor {
    i_Body bd = _edt.getBody(id);
    if (bd.type() == BodyType.POINT) {
      return getAnchorID(id, "P");
    } else if (bd.type() == BodyType.RIB) {
      return getAnchorID(id, "rib");
    } else if (bd.type() == BodyType.CIRCLE) {
      return getAnchorID(id, "disk");
    } else if (bd.type().isPolygon()) {
      return getAnchorID(id, "facet");
    } else {
      return id;
    }
  }

  /**
   * Get description for body with given ID.
   * @param id
   * @return
   */
  public String getDescription(String id) {
    try {
      i_BodyBuilder bb = getBuilder(id);
      return bb.description(this, PRECISION.value());
    } catch (ExNoBuilder ex) {
      return "";
    }
  }

  /**
   * Get anchor by body ID and key of the anchor.
   * @param bodyID
   * @param key
   * @return
   * @throws ExNoAnchor
   * @throws ExNoBody
   */
  public i_Anchor getAnchor(String bodyID, String key) throws ExNoAnchor, ExNoBody {
    return _edt.getAnchor(bodyID, key);
  }

  /**
   * Get anchor ID by body ID and key of the anchor.
   * @param bodyID
   * @param key
   * @return
   * @throws ExNoAnchor
   * @throws ExNoBody
   */
  public String getAnchorID(String bodyID, String key) throws ExNoAnchor, ExNoBody {
    return _edt.getAnchorID(bodyID, key);
  }

  /**
   * Название якоря, выводимое в GUI.
   * @param anchorID
   * @return
   * @throws editor.ExNoAnchor
   */
  public String getAnchorTitle(String anchorID) throws ExNoAnchor {
    return _edt.getAnchorTitle(anchorID);
  }
  
  /**
   * Название тела, выводимое в GUI.
   * Если по краям строки стоят проценты %...%, то предполагается, что это — ID якоря.
   * Оно заменяется на имя якоря.
   * @param bodyID
   * @return
   * @throws editor.ExNoBody
   */
  public String getBodyTitle(String bodyID) throws ExNoBody {
    String title = _edt.getBody(bodyID).getTitle();
    if( title.startsWith("%") && title.endsWith("%") ) {
      try {
        // конвертируем ID якоря в имя.
        return _edt.getAnchorTitle(title.substring(1, title.length() - 1));
      } catch( ExNoAnchor ex ){}
    }
    
    return title;
  }

  /**
   * Название якоря, выводимое в GUI.
   * @param bodyID
   * @param key
   * @return
   * @throws editor.ExNoBody
   * @throws editor.ExNoAnchor
   */
  public String getAnchorTitle(String bodyID, String key) throws ExNoBody, ExNoAnchor {
    return _edt.getAnchorTitle(bodyID, key);
  }

  /**
   * Get list of all bodies.
   * @return
   */
  public ArrayList<i_Body> getBodies() {
    return _edt.bd().getAllBodies();
  }

  /**
   * Список тел, имеющих заданный тип.
   * @param type тип тела
   * @return
   */
  public ArrayList<i_Body> getBodiesByType(BodyType type) {
    return _edt.bd().getBodiesByType(type);
  }

  /**
   * Тела, соответствующих одному из типов в списке.
   * @param type
   * @return
   */
  public Iterable<i_Body> getBodiesByTypes(List<BodyType> type){
    return Iterables.concat(Lists.transform(type, new Function<BodyType, ArrayList<i_Body>>(){
      @Override
      public ArrayList<i_Body> apply(BodyType input) {
        return getBodiesByType(input);
      }
    }));
  }

  /**
   * Тела по списку ID.
   * @param ids
   * @return
   */
  public Iterable<i_Body> getBodiesByIDs(List<String> ids){
    return _edt.bd().getBodiesByIDs(ids);
  }

  /**
   * Список многоугольников.
   * @return
   */
  public ArrayList<i_Body> getPolygonBodies() {
    return _edt.bd().getPolygonBodies();
  }

  /**
   * Установить CanvasController.
   * @param canvas
   */
  public void setMainCanvasController(MainEdtCanvasController canvas) {
    _canvas = canvas;
  }

  public void setStlCanvasController(StlEdtCanvasController canvas) {
      _stlCanvas = canvas;
  }

  public EdtLayoutController getLayoutController() {
    return _layout;
  }

  public void setLayoutController(EdtLayoutController layout) {
    _layout = layout;
  }

  /**
   * Adapt scale for editor.
   */
  public void fitEditor() {
    double maxDistance = _edt.getMaxDistance();
    double visibleSize = _canvas.getVisibleSize();
    while (maxDistance * 3 > visibleSize && visibleSize <= 10000) {
      visibleSize *= 1.25;
      _canvas.getScene().changeCameraDistance(0.25);
    }
  }

  /**
   * Показать диалог опций тела.
   */
  public void showBodySettingsDialog() {
    if (!_bodySettingsDialog.isVisible()) {
      _bodySettingsDialog.setCenterLocation();
      _bodySettingsDialog.setVisible(true);
    }
  }

  /**
   * Show application settings dialog.
   */
  public void showAppSettingsDialog() {
    if (!_appSettingsDialog.isVisible()) {
      _appSettingsDialog.setCenterLocation();
      _appSettingsDialog.setVisible(true);
    }
  }

  /**
   * Show color choose dialog.
   * @param listener
   * @param c initial color (may be null).
   * @return new color (or null)
   */
  public Color showColorChooserDialog( ChangeListener listener, Color c ) {
    if( c != null ){
      _colorChooserDialog.setColor(c);
    }
    _colorChooserDialog.setColorChangeListener(listener);
    _colorChooserDialog.setCenterLocation();
    _colorChooserDialog.setVisible(true);

    return _colorChooserDialog.isColorChanged() ? _colorChooserDialog.getColor() : null;
  }

  public StatusStrip status() {
    return _layout.getStatusStrip();
  }

  public EdtFrame getFrame() {
    return _layout.getFrame();
  }

  /**
   * Check whether editor has body with given ID.
   * @param bodyID
   * @return
   */
  public boolean containsBody(String bodyID) {
    return _edt.bd().contains(bodyID);
  }

  /**
   * Установка видимости тела.
   * @param bodyID
   * @param visible
   * @param withPts рассматривать точки
   * @param withRibs рассматривать рёбра
   * @param withFacets рассматривать грани
   * @param withDisks рассматривать круги
   * @throws editor.ExNoBody
   */
  public void setBodyVisible(String bodyID, boolean visible,
          boolean withPts, boolean withRibs, boolean withFacets, boolean withDisks)
          throws ExNoBody {
    i_Body body = getBody(bodyID);

    if( !body.exists())
      return;

    if( body.getState().hasParam(DisplayParam.VISIBLE) )
      body.getState().setVisible(visible);

    // Список изменённых якорей
    // Их нужно оповестить об изменении
    ArrayList<String> changedAnchors = new ArrayList<>();

    if (visible) {
      for (String anchorID : body.getAnchors().values()) {
        try {
          i_Anchor a = getAnchor(anchorID);
          if ((a.getAnchorType() == AnchorType.ANC_POLY && withFacets) ||
              (a.getAnchorType() == AnchorType.ANC_RIB && withRibs) ||
              (a.getAnchorType() == AnchorType.ANC_POINT && withPts) ||
              (a.getAnchorType() == AnchorType.ANC_DISK && withDisks)) {
            a.setVisible(visible);
            changedAnchors.add(anchorID);
          }
        } catch (ExNoAnchor ex) { }
      }
    } else {
      for (String anchorID : body.getAnchors().values()) {
        try {
          i_Anchor a = getAnchor(anchorID);

          if ((a.getAnchorType() == AnchorType.ANC_POLY && !withFacets) ||
              (a.getAnchorType() == AnchorType.ANC_RIB && !withRibs) ||
              (a.getAnchorType() == AnchorType.ANC_POINT && !withPts) ||
              (a.getAnchorType() == AnchorType.ANC_DISK && !withDisks)) {
            continue;
          }

          getAnchor(anchorID).setVisible(false);
          changedAnchors.add(anchorID);
        } catch (ExNoAnchor ex) {}
      }
    }
    notifyAnchorStateChange(changedAnchors);
  }

  /**
   * Проверка: видимо ли данное тело.
   * Если видимо не полностью - то false.
   * Если тело не найдено - то false.
   * Считаем следующим образом:
   * если видна хотя бы часть тела (часть каркаса или поверхности),
   * оно считается видимым.
   *
   * @param bodyID
   * @return
   */
  public boolean isBodyVisible(String bodyID){
    try {
      i_Body body = getBody(bodyID);

      if (body.getState().hasParam(DisplayParam.VISIBLE)) {
        return (boolean)body.getState().getParam(DisplayParam.VISIBLE);
      }
      if (body.type() == BodyType.POINT) {
        i_Anchor a = getDuplicateAnchor(bodyID);
        if( a != null ){
          return a.isVisible();
        } else {
          return false;
        }
      }
      for (String anchorID : body.getAnchors().values()) {
        try {
          i_Anchor a = getAnchor(anchorID);
          if ((a.getAnchorType() != AnchorType.ANC_POINT) && a.isVisible()) {
            return true;
          }
        } catch (ExNoAnchor ex) { }
      }
      return false;
    } catch (ExNoBody ex) {}
    return false;
  }

  /**
   * Установить параметр тела.
   * Для всех его якорей, у которых есть этот параметр,
   * он применяется.
   * @param bodyID
   * @param p
   * @param value
   */
  public void setBodyParam(String bodyID, DisplayParam p, Object value) {
    try {
      i_Body bd = getBody(bodyID);
      BodyState state = bd.getState();
      if (state.hasParam(p)) {
        state.setParam(p, value);
      }
      for (String anchorID : bd.getAnchors().values()) {
        try {
          i_Anchor a = getAnchor(anchorID);
          AnchorState anchState = a.getState();
          if (anchState.hasParam(p)) {
            anchState.setParam(p, value);
          }
        } catch (ExNoAnchor ex) {}
      }
    } catch (ExNoBody ex) {}
  }

  /**
   * Redraw scene on canvas
   * using OpenGL rendering.
   */
  public void redraw(){
    // перерисовка занимает длительное время,
    // запускаем её в отдельном потоке
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        _canvas.redraw();
      }
    });
  }

  /**
   * Rebuild scene.
   *
   * @param eh Error handler (null allowed).
   */
  public void rebuild(i_ErrorHandler eh) {
    config.Temp.FILE_SAVED = false;
    _edt.rebuild(eh);
    notifyEditorStateChange();
  }

  /**
   * Rebuild scene.
   * Using default error handler.
   */
  public void rebuild() {
    rebuild(null);
  }

  /**
   * Пытаемся добавить билдер в редактор.
   * Если тело не может быть создано билдером, то
   * при <code>nullable == true</code> добавляем билдер и тело-пустышку,
   * иначе - билдер не добавляется.
   * @param bb BodyBuilder to add
   * @param eh Error handler (null allowed).
   * @param nullable
   * @return Error status.
   */
  public boolean add(i_BodyBuilder bb, i_ErrorHandler eh, boolean nullable) {
    config.Temp.FILE_SAVED = false;
    boolean result = _edt.add(bb, eh, nullable);
    setUndo("Добавление тела");
    notifyEditorStateChange();
    return result;
  }

  /**
   * Просто добавление билдера без обработки ошибок и каких-либо дополнительных действий.
   * @param bb
   * @return
   */
  public boolean addSilently(i_BodyBuilder bb) {
    boolean result = _edt.add(bb, null, false);
    return result;
  }

  /**
   * Добавляем несколько тел в редактор.
   * @param builders список билдеров.
   * @param eh обработчик ошибок.
   * @param nullable
   * @return False, if got error.
   */
  public boolean addBodies(List<i_BodyBuilder> builders, i_ErrorHandler eh, boolean nullable) {
    config.Temp.FILE_SAVED = false;
    boolean errFlag = true;
    for( i_BodyBuilder builder: builders ){
      errFlag &= _edt.add(builder, eh, nullable);
    }
    setUndo("Добавление тел");
    notifyEditorStateChange();
    return errFlag;
  }

  /**
   * Remove body by ID.
   *
   * @param bodyID Body ID to remove.
   */
  public void removeBody(String bodyID) {
    config.Temp.FILE_SAVED = false;
    String newFocusedBodyID = _focusCtrl.getBodyIDToFocus();
    _edt.removeBody(bodyID);
    _focusCtrl.clearFocus();
    setUndo("Удаление тела");
    notifyEditorStateChange();
    if (newFocusedBodyID != null) {
      _focusCtrl.setFocusOnBody(newFocusedBodyID);
    }
  }

  /**
   * Remove set of bodies.
   *
   * @param bodyIDs List of body IDs.
   */
  public void removeBodies(List<String> bodyIDs) {
    config.Temp.FILE_SAVED = false;
    String newFocusedBodyID = _focusCtrl.getBodyIDToFocus();
    _edt.removeBodies(bodyIDs);
    _focusCtrl.clearFocus();
    setUndo("Удаление нескольких тел");
    notifyEditorStateChange();
    if (newFocusedBodyID != null) {
      _focusCtrl.setFocusOnBody(newFocusedBodyID);
    }
  }

  /**
   * Get BodyContainer instance of editor.
   * @return
   */
  public i_BodyContainer getBodyContainer() {
    return _edt.bd();
  }

  public Iterable<i_Cover> getMeshes() {
        return _edt.covers().getCovers();
    }
  /**
   * Get AnchorContainer instance of editor.
   * @return
   */
  public i_AnchorContainer getAnchorContainer() {
    return _edt.anchors();
  }

  /**
   * Get maximal length of edge.
   * @return
   */
  public double getMaxEdgeLength() {
    return _edt.anchors().getMaxEdgeLength();
  }

  /**
   * Get minimal length of edge.
   * @return
   */
  public double getMinEdgeLength() {
    return _edt.anchors().getMinEdgeLength();
  }

  /**
   * Get maximal area of face.
   * @return
   */
  public double getMaxFaceArea() {
    return _edt.anchors().getMaxFaceArea();
  }

  /**
   * Get minimal area of face.
   * @return
   */
  public double getMinFaceArea() {
    return _edt.anchors().getMinFaceArea();
  }

  /**
   * Проверка, существует ли тело с данным именем.
   * Новое имя должно быть уникальным и непустым.
   * 
   * ! Тяжелый метод, не запускайте в цикле !
   * 
   * @param newTitle новое имя тела
   * @throws editor.InvalidBodyNameException
   */
  public void validateBodyTitle(String newTitle) throws InvalidBodyNameException {
    String tmp = newTitle.trim();
    if (tmp.isEmpty()) {
      throw new InvalidBodyNameException("Имя должно быть непустым!");
    } else if (!tmp.matches("[A-Za-zА-Яа-я0-9]+.*")) {
      throw new InvalidBodyNameException("Имя должно начинаться на букву или цифру!");
    } else {
      for( String bodyID : _edt.bd().getBodyIDs()){
        try {
          if( getBodyTitle(bodyID).equals(newTitle) ){
            throw new InvalidBodyNameException("Имя уже используется!");
          }
        } catch( ExNoBody ex ){}
      }
    }
  }

  public String getBodyByTitle(String title) throws ExNoBody {
    return _edt.bd().getBodyIdByTitle(title);
  }

  public String getAnchorByTitle(String title) throws ExNoAnchor {
    return _edt.getAnchorByTitle(title);
  }
  
  /**
   * Переименование якоря-точки.
   * Проверка допустимости имени не проверяется, используйте функцию @link{isValidBodyTitle}.
   * @param anchorID
   *  ID якоря-точки
   * @param newTitle
   *  Новое имя точки
   */
  public void renameAnchorPoint(String anchorID, String newTitle){
    StringBuilder name = _edt.anchMgr().getTitleRef(anchorID);
    if( name != null ){
      name.setLength(0);
      name.append(newTitle);
    }
  }

  /**
   * Переименование тела.
   * Проверка на валидность переименования не проходится!
   * Чтобы проверить валидность, используйте функцию @link{isValidBodyTitle}.
   * @param bodyID
   * @param newTitle
   */
  public void renameBody(String bodyID, String newTitle) {
    try {
      i_Body bd = getBody(bodyID);

      if (bd.type() == BodyType.POINT) {
        // Синхронизируем переименование точки и соответствующего якоря.
        renameAnchorPoint(bd.getAnchorID(PointBody.BODY_KEY_POINT), newTitle);
      }

      if (bd.hasBuilder()) {
        getBuilder(bodyID).setValue(BodyBuilder.BLDKEY_NAME, newTitle);
      }
    } catch (ExNoBody | ExNoBuilder ex) {}
  }

  /**
   * Создать точку восстановления сцены.
   * !! SIC: точка создаётся <b>после</b> действия.
   * @param eventString описание действия
   */
  public void setUndo(String eventString) {
    _ur.addUndo(_edt, eventString);
  }

  /**
   * Откатить изменения сцены до ближайшей точки восстановления.
   */
  public void undo() {
    if( _ur.undo() ){
      _edt.loadFromState(_ur.getCurrState());
      notifyEditorStateChange();
    }
  }

  /**
   * Восстановить одно изменение после отката.
   */
  public void redo() {
    if( _ur.redo() ){
      _edt.loadFromState(_ur.getCurrState());
      notifyEditorStateChange();
    }
  }

  /**
   * Checks if undo queue is empty.
   * @return
   */
  public boolean isUndoEmpty() {
    return _ur.isUndoEmpty();
  }

  /**
   * Checks if redo queue is empty.
   * @return
   */
  public boolean isRedoEmpty() {
    return _ur.isRedoEmpty();
  }

  /**
   * Get descriptions for undo operations.
   * @return
   */
  public ArrayList<String> getUndoDescripts() {
    return _ur.getAllUndoDescriptions();
  }

  /**
   * Get descriptions for redo operations.
   * @return
   */
  public ArrayList<String> getRedoDescripts() {
    return _ur.getAllRedoDescriptions();
  }
  
  public ExtensionManager getExtensionMgr() {
    return _extensions;
  }

  /**
   * Проверка, есть ли на сцене #<code>num</code> якорей заданного типа.
   * @param type тип якоря
   * @param numAnchors количество якорей
   * @return
   */
  public boolean containsAnchors(AnchorType type, int numAnchors) {
    return _edt.anchors().getAnchorsByType(type).size() >= numAnchors;
  }

  /**
   * Проверка, есть ли на сцене <code>num</code> тел заданного типа.
   * @param type тип тела
   * @param numBodies количество тел
   * @return
   */
  public boolean containsBodies(BodyType type, int numBodies) {
    int counter = 0;
    for (i_Body b : getBodies())
      if (b.type() == type)
        counter++;

    return counter >= numBodies;
  }

  /**
   * Проверка, есть ли на сцене <code>num</code> тел одного из заданных типов.
   * @param types список типов тел
   * @param numBodies количество тел
   * @return
   */
  public boolean containsBodies(ArrayList<BodyType> types, int numBodies) {
    int counter = 0;
    for (i_Body b : getBodies()) {
      if (types.contains(b.type()))
        counter++;
    }

    return counter >= numBodies;
  }

  /**
   * Show context menu associated with body.
   * @param bodyID
   * @param invoker
   * @param mouseX
   * @param mouseY
   */
  public final void showBodyContextMenu(String bodyID, Component invoker, int mouseX, int mouseY) {
    try {
      ContextMenuLauncher.launch(this, invoker, bodyID, mouseX, mouseY);
    } catch( ExNoBody ex ) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.printf("Cannot show context menu: %s", ex.getMessage());
      }
    }
  }

  /**
   * Get anchor that duplicates given body.
   * If body is not a point / rib / poly / circle,
   * or if ID is invalid,
   * returns null.
   *
   * @param bodyID
   * @return
   */
  public i_Anchor getDuplicateAnchor(String bodyID) {
    try {
      i_Body bd = getBody(bodyID);
      if (bd.type() == BodyType.POINT) {
        return getAnchor(bd.getAnchorID("P"));
      } else if (bd.type() == BodyType.RIB) {
        return getAnchor(bd.getAnchorID("rib"));
      } else if (bd.type() == BodyType.CIRCLE) {
        return getAnchor(bd.getAnchorID("disk"));
      } else if (bd.type().isPolygon()) {
        return getAnchor(bd.getAnchorID("facet"));
      }
    } catch (ExNoBody | ExNoAnchor ex) {}
    return null;
  }

  /**
   * Observer pattern section.
   * Here we are register anchor, body and editor state change listeners.
   * We cannot register them in body or anchor instances in the reason of
   * creating new instances of body or anchor on each rebuild of editor.
   */

  /**
   * Register editor state change listener.
   * @param listener
   */
  public void addEditorStateChangeListener(i_EditorChangeListener listener){
    _editorStateListeners.add(listener);
  }

  /**
   * Unregister editor state change listener.
   * @param listener
   */
  public void removeEditorStateChangeListener(i_EditorChangeListener listener){
    _editorStateListeners.remove(listener);
  }

  /**
   * Notify registered editor state change listeners to update.
   */
  public void notifyEditorStateChange(){
    for (i_EditorChangeListener l : _editorStateListeners){
      l.updateEditorState();
    }
  }

  public void clearEditorStateChangeListeners(){
    _editorStateListeners.clear();
  }

  /**
   * Register anchor state change listener.
   * @param listener
   */
  public void addAnchorStateChangeListener(i_AnchorStateChangeListener listener){
    _anchorStateListeners.add(listener);
  }
  /**
   * Unregister anchor state change listener.
   * @param listener
   */
  public void removeAnchorStateChangeListener(i_AnchorStateChangeListener listener){
    _anchorStateListeners.remove(listener);
  }
  /**
   * Notify registered anchor state change listeners to update.
   * @param anchorID ID of updated anchor
   */
  public void notifyAnchorStateChange(String anchorID){
    for (i_AnchorStateChangeListener l : _anchorStateListeners){
      l.updateAnchorState(anchorID);
    }
  }

  /**
   * Notify registered anchor state change listeners to update.
   * @param anchorIDs list of changed anchors
   */
  public void notifyAnchorStateChange(List<String> anchorIDs){
    for (i_AnchorStateChangeListener l : _anchorStateListeners){
      l.updateAnchorState(anchorIDs);
    }
  }

  /**
   * Register body state change listener.
   * @param listener
   */
  public void addBodyStateChangeListener(i_BodyStateChangeListener listener){
    _bodyStateListeners.add(listener);
  }
  /**
   * Unregister body state change listener.
   * @param listener
   */
  public void removeBodyStateChangeListener(i_BodyStateChangeListener listener){
    _bodyStateListeners.remove(listener);
  }
  /**
   * Notify registered body state change listeners to update.
   * @param bodyID
   */
  public void notifyBodyStateChange(String bodyID){
    for (i_BodyStateChangeListener l : _bodyStateListeners){
      l.updateBodyState(bodyID);
    }
  }

  /**
   * Register body state change listener.
   * @param listener
   */
  public void addGlobalModeChangeListener(i_GlobalModeChangeListener listener){
    _modeChangeListeners.add(listener);
  }
  /**
   * Unregister body state change listener.
   * @param listener
   */
  public void removeGlobalModeChangeListener(i_GlobalModeChangeListener listener){
    _modeChangeListeners.remove(listener);
  }
  /**
   * Notify registered body state change listeners to update.
   */
  public void notifyGlobalModeChange(){
    for (i_GlobalModeChangeListener l : _modeChangeListeners){
      l.modeChanged(_canvas.getScene().getSceneType());
    }
  }

  /**
   * Register body state change listener.
   * @param listener
   */
  public void addAppSettingsChangeListener(i_AppSettingsChangeListener listener){
    _appSettingsListeners.add(listener);
  }
  /**
   * Unregister body state change listener.
   * @param listener
   */
  public void removeAppSettingsChangeListener(i_AppSettingsChangeListener listener){
    _appSettingsListeners.remove(listener);
  }
  /**
   * Notify registered body state change listeners to update.
   */
  public void notifyAppSettingsChange(){
    for (i_AppSettingsChangeListener l : _appSettingsListeners){
      l.settingsChanged();
    }
  }

  public i_Cover getCover(String id) {
    return _edt.getCover(id);
  }

  public boolean containsCover(String id) {
    return _edt.covers().contains(id);
  }
};