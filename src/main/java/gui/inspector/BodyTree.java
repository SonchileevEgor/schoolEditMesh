package gui.inspector;

import bodies.BodyType;
import bodies.PointBody;
import static config.Config.PRECISION;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Body;
import editor.state.i_BodyStateChangeListener;
import gui.EdtController;
import gui.IconList;
import gui.elements.NameTextField;
import gui.laf.AppColor;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

/**
 * Дерево тел, присутствующих на сцене.
 *
 * @author alexeev
 */
public class BodyTree extends JTree implements i_BodyStateChangeListener {

  private DefaultMutableTreeNode _points;
  private DefaultMutableTreeNode _ribs;
  private DefaultMutableTreeNode _facets;
  private DefaultMutableTreeNode _bodies;

  private DefaultTreeModel _model;

  // храним множество путей в дереве к телам.
  private HashMap<String, TreePath> _idToPath;

  private EdtController _ctrl;

  /**
   * Main BodyTree located in InspectorPanel. Only main BodyTree listens RMB clicks.
   */
  private boolean _isMainBodyTree;

  // Is some cell currently editing.
  boolean _isCurrentlySomeNodeEditing;

  /**
   * Дерево тел на сцене.
   *
   * @param ctrl
   * @param listenRightMouseButton включить прослушивание правой клавиши мыши.
   */
  public BodyTree(EdtController ctrl, boolean listenRightMouseButton) {
    super();
    _ctrl = ctrl;
    _bodies = new DefaultMutableTreeNode("Тела");
    _points = new DefaultMutableTreeNode("Точки");
    _ribs = new DefaultMutableTreeNode("Отрезки");
    _facets = new DefaultMutableTreeNode("Многоугольники");
    _idToPath = new HashMap<>();
    _isMainBodyTree = listenRightMouseButton;

    DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    root.add(_bodies);
    root.add(_points);
    root.add(_ribs);
    root.add(_facets);

    _model = new DefaultTreeModel(root) {
      @Override
      public boolean isLeaf(Object node) {
        if (node.equals(_bodies)
                || node.equals(_points)
                || node.equals(_ribs)
                || node.equals(_facets)) {
          return false;
        } else {
          return super.isLeaf(node);
        }
      }
    };

    // leaf nodes cannot be expanded, but we need to expand by default bodies node.
    setModel(_model);
    expandPath(new TreePath(_bodies.getPath()));

    getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    setEditable(_isMainBodyTree);

    setCellRenderer(new BodyNodeRenderer(ctrl));
    setCellEditor(new BodyCellEditor(ctrl, this));

    setToggleClickCount(1);
    setRootVisible(false);

    update();
    updateModel();

    for (TreeSelectionListener tsl: getListeners(TreeSelectionListener.class)) {
      removeTreeSelectionListener(tsl);
    }
    
    // Выключаем режим изменения имени тела, когда изменяется выбранное тело в дереве.
    addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        _isCurrentlySomeNodeEditing = false;
        // We shut down main canvas key dispatcher when editing name of body.
        _ctrl.getMainCanvasCtrl().enableKeyDispatcher();
      }
    });

    // Для разделения событий нажатия левой и правой клавиш мыши
    // этот код помещён в MouseListener, а не в TreeSelectionListener.
    // Также при выборе тела мы изменяем фокус на теле, после чего следует оповещение об
    // изменении фокуса.
    // Поскольку данный класс является слушателем изменений фокуса, после данного события
    // может происходить изменение выбранных узлов дерева.
    // Это, в свою очередь, вызывает обработку соответствующего события всеми зарегистрированными
    // TreeSelectionListener 'ами. Поэтому, если этот код будет добавлен в TreeSelectionListener,
    // придётся бороться с циклическими оповещениями TreeSelectionListener <--> FocusListener.
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        final TreePath path = getPathForLocation(e.getX(), e.getY());
        if (path == null) {
          if (!e.isControlDown()) {
            // Снимаем фокус с объектов, если ни в один узел не попали.
            _ctrl.getFocusCtrl().clearFocus();
          }
          return;
        }
        Object node = path.getLastPathComponent();
        if (node instanceof BodyNode) {
          final String bodyID = ((BodyNode)node).getBodyID();
          if (SwingUtilities.isRightMouseButton(e)) {
            if (_isMainBodyTree) {
              _ctrl.getFocusCtrl().setFocusOnBody(bodyID);
              _ctrl.showBodyContextMenu(bodyID, (Component)e.getSource(), e.getX(), e.getY());
            }
          } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.isControlDown()) {
              _ctrl.getFocusCtrl().toggleFocusOnBody(bodyID);
            } else {
              if (e.getClickCount() == 1 || !_ctrl.getFocusCtrl().contains(bodyID)) {
                // Если устанавливать фокус по каждом клику, то двойные клики в CellEditor
                // не будут обрабатываться, т. к. модель дерева после каждого клика
                // будет обновляться.
                _ctrl.getFocusCtrl().setFocusOnBody(bodyID);
              }
            }
          }
          _ctrl.redraw();
        }
      }
    });
  }

  public boolean isCellEditing() {
    return _isCurrentlySomeNodeEditing;
  }

  @Override
  public boolean stopEditing() {
    _isCurrentlySomeNodeEditing = false;

    // We shut down main canvas key dispatcher when editing name of body.
    _ctrl.getMainCanvasCtrl().enableKeyDispatcher();
    return super.stopEditing();
  }

  /**
   * Актуализировать содержимое дерева. После этого метода нужно обновить модель (вызов
   * updateModel).
   */
  public final void update() {
    _points.removeAllChildren();
    _ribs.removeAllChildren();
    _facets.removeAllChildren();
    _bodies.removeAllChildren();
    _idToPath.clear();
    for (i_Body bd: _ctrl.getBodies()) {
      try {
        BodyNode node = new BodyNode(_ctrl, bd.id());
        if (bd.type() == BodyType.POINT) {
          _points.add(node);
        } else if (bd.type() == BodyType.RIB) {
          _ribs.add(node);
        } else if (bd.type().isPolygon()) {
          _facets.add(node);
        } else {
          _bodies.add(node);
        }
        _idToPath.put(bd.id(), new TreePath(node.getPath()));
      } catch (ExNoBody ex) {
      }
    }
  }

  /**
   * Обновление структуры дерева. Вызывать как можно реже, т. к. метод дорогой.
   */
  public final void updateModel() {
    // Если я скажу об изменении корня, то сбросятся данные об открытых ветках
    // (все закроются), приходится так.
    _model.nodeStructureChanged(_points);
    _model.nodeStructureChanged(_ribs);
    _model.nodeStructureChanged(_facets);
    _model.nodeStructureChanged(_bodies);
  }

  /**
   * Обновить фокус на всех телах.
   */
  public void updateFocus() {
    clearSelection();
    List<String> focusedBodies = _ctrl.getFocusCtrl().getFocusedBodies();
    for (int i = 0; i < _points.getChildCount(); i++) {
      BodyNode node = (BodyNode)_points.getChildAt(i);
      TreePath path = new TreePath(node.getPath());
      if (focusedBodies.contains(node.getBodyID())) {
        addSelectionPath(path);
      }
    }
    for (int i = 0; i < _ribs.getChildCount(); i++) {
      BodyNode node = (BodyNode)_ribs.getChildAt(i);
      TreePath path = new TreePath(node.getPath());
      if (focusedBodies.contains(node.getBodyID())) {
        addSelectionPath(path);
      }
    }
    for (int i = 0; i < _facets.getChildCount(); i++) {
      BodyNode node = (BodyNode)_facets.getChildAt(i);
      TreePath path = new TreePath(node.getPath());
      if (focusedBodies.contains(node.getBodyID())) {
        addSelectionPath(path);
      }
    }
    for (int i = 0; i < _bodies.getChildCount(); i++) {
      BodyNode node = (BodyNode)_bodies.getChildAt(i);
      TreePath path = new TreePath(node.getPath());
      if (focusedBodies.contains(node.getBodyID())) {
        addSelectionPath(path);
      }
    }
  }

  /**
   * Убрать фокус со всех тел
   */
  public void focusCleared() {
    clearSelection();
  }

  /**
   * Убрать фокус с одного тела
   *
   * @param bodyID
   */
  public void focusLost(String bodyID) {
    removeSelectionPath(_idToPath.get(bodyID));
  }

  /**
   * Добавить фокус для одного тела
   *
   * @param bodyID
   */
  public void focusApply(String bodyID) {
    TreePath pathToNode = _idToPath.get(bodyID);
    if (getEditingPath() != pathToNode) {
      addSelectionPath(_idToPath.get(bodyID));
      TreeNode node = (BodyNode)pathToNode.getLastPathComponent();
      _model.nodeChanged(node);
    }
  }

  @Override
  public String convertValueToText(Object value,
          boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    if (value instanceof BodyNode) {
      String bodyID = ((BodyNode)value).getUserObject();
      String text = "";
      try {
        i_Body bd = _ctrl.getBody(bodyID);
        try {
          text = "<html><strong>" + StringUtils.capitalize(bd.alias()) + "</strong> "
                  + _ctrl.getBodyTitle(((BodyNode)value).getBodyID());
        } catch (ExNoBody ex) {
          text = "<html><strong>" + StringUtils.capitalize(bd.alias()) + "</strong> " + bd.getTitle();
        }
        if (!bd.exists()) {
          text += " (не удалось построить)";
        } else if (bd.type() == BodyType.POINT) {
          // выводим точку с координатами
          text += " ".concat(((PointBody)bd).point().toString(PRECISION.value(), _ctrl.getScene().is3d()));
        }
      } catch (ExNoBody ex) {
      }
      return text;
    } else {
      return value.toString();
    }
  }

  @Override
  public void updateBodyState(String bodyID) {
    //TODO: разобраться с порядком вызова
    update();
    updateFocus();
    updateModel();
    updateFocus();
  }
}

/**
 * Tree node-representative of body.
 *
 * @author alexeev
 */
class BodyNode extends DefaultMutableTreeNode {

  String _bodyID;
  EdtController _ctrl;

  BodyNode(EdtController ctrl, String bodyID) throws ExNoBody {
    super(ctrl.getBody(bodyID));
    _bodyID = bodyID;
    _ctrl = ctrl;
  }

  @Override
  public String getUserObject() {
    return _bodyID;
  }

  String getBodyID() {
    return _bodyID;
  }
}

/**
 * Cell renderer for BodyTree.
 *
 * @author alexeev
 */
class BodyNodeRenderer extends JPanel implements TreeCellRenderer {

  private final ImageIcon SHOWN_ICON = IconList.SHOWN.getTinyIcon();
  private final ImageIcon HIDDEN_ICON = IconList.HIDDEN.getTinyIcon();
  private final ImageIcon TREE_CLOSE_ICON = IconList.TREE_CLOSE.getTinyIcon();
  private final ImageIcon TREE_OPEN_ICON = IconList.TREE_OPEN.getTinyIcon();
  private final ImageIcon EMPTY_ICON = IconList.EMPTY.getTinyIcon();

  EdtController _ctrl;

  private final JLabel _iconButton;
  private final JLabel _titleLabel;

  public BodyNodeRenderer(EdtController ctrl) {
    super(new MigLayout(new LC().insetsAll("1").fillX().hideMode(3)));
    _ctrl = ctrl;
    _iconButton = new JLabel();
    _titleLabel = new JLabel();

    add(_iconButton, new CC().height("14!"));
    add(_titleLabel, new CC().height("14!"));

    _iconButton.setOpaque(false);
    _titleLabel.setOpaque(false);
    setOpaque(true);
    setForeground(AppColor.BLACK.color());
  }

  private void setText(String text) {
    _titleLabel.setText(text);
  }

  private void setIcon(Icon icon) {
    _iconButton.setIcon(icon);
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Icon icon;
    boolean isFocused = false;
    if (value instanceof BodyNode) {
      try {
        i_Body bd = _ctrl.getBody(((BodyNode)value).getBodyID());
        isFocused = bd.getState().isChosen();
        setText(tree.convertValueToText(value, sel, expanded, leaf, row, isFocused));
        icon = _ctrl.isBodyVisible(bd.id()) ? SHOWN_ICON : HIDDEN_ICON;
      } catch (ExNoBody ex) {
        icon = EMPTY_ICON;
      }
    } else {
      setText(tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus));
      icon = expanded ? TREE_CLOSE_ICON : TREE_OPEN_ICON;
    }
    setBackground(isFocused ? AppColor.BLUE.color() : AppColor.WHITE.color());
    setIcon(icon);
    return this;
  }
}

/**
 * CellEditor для листьев. Нужен, например, для того чтобы изменять видимость тела кликом по иконке.
 * При фокусировке на теле панель листа дерева заменяется другой, неотличимой по виду.
 *
 * @author alexeev
 */
class BodyCellEditor extends AbstractCellEditor implements TreeCellEditor {

  private EdtController _ctrl;
  private BodyTree _tree;
  private String _bodyID;

  public BodyCellEditor(EdtController ctrl, BodyTree tree) {
    _ctrl = ctrl;
    _tree = tree;
  }

  @Override
  public boolean isCellEditable(EventObject event) {
    boolean returnValue = false;

    if (event instanceof MouseEvent) {
      MouseEvent mouseEvent = (MouseEvent)event;
      TreePath path = _tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
      if (path != null) {
        Object node = path.getLastPathComponent();
        if ((node != null) && (node instanceof BodyNode)) {
          returnValue = true;
        }
      }
    }
    return returnValue;
  }

  @Override
  public Object getCellEditorValue() {
    if (_bodyID == null) {
      return "";
    }

    try {
      return _ctrl.getBodyTitle(_bodyID);
    } catch (ExNoBody ex) {
      return "";
    }
  }

  @Override
  public Component getTreeCellEditorComponent(JTree tree, Object value,
          boolean isSelected, boolean expanded, boolean leaf, int row) {
    String bodyInfo = "";
    String bodyTitle = "";
    if (value instanceof BodyNode) {
      try {
        _bodyID = ((BodyNode)value).getBodyID();
        i_Body bd = _ctrl.getBody(_bodyID);

        bodyInfo = tree.convertValueToText(value, isSelected, expanded, leaf, row, true);
        bodyTitle = _ctrl.getBodyTitle(bd.id());
      } catch (ExNoBody ex) {
      }
    }
    return buildEditorPanel(bodyInfo, bodyTitle);
  }

  private JPanel buildEditorPanel(String bodyInfo, String bodyTitle) {
    final JPanel wrapper = new JPanel(new MigLayout(new LC().insetsAll("1").fillX().hideMode(3)));
    wrapper.setBackground(AppColor.BLUE.color());

    JLabel visButton = new JLabel();
    Icon icon = _ctrl.isBodyVisible(_bodyID)
            ? IconList.SHOWN.getTinyIcon()
            : IconList.HIDDEN.getTinyIcon();
    visButton.setIcon(icon);

    visButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (_bodyID == null || !SwingUtilities.isLeftMouseButton(e)) {
          return;
        }

        try {
          i_Body bd = _ctrl.getBody(_bodyID);
          BodyType type = bd.type();
          if (_ctrl.isBodyVisible(bd.id())) {
            _ctrl.setBodyVisible(bd.id(), false, type == BodyType.POINT, true, true, true);
            _ctrl.setUndo("Скрыть " + bd.alias());
          } else {
            _ctrl.setBodyVisible(bd.id(), true, true, true, true, true);
            _ctrl.setUndo("Показать " + bd.alias());
          }
          _ctrl.notifyBodyStateChange(bd.id());
          _ctrl.redraw();
        } catch (ExNoBody ex) {
        }
        stopCellEditing();
      }
    });

    wrapper.add(visButton);

    final NameTextField titleField = new NameTextField(bodyTitle);
    final JLabel titleLabel = new JLabel(bodyInfo);
    titleLabel.setOpaque(false);

    titleLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // Dispatching RMB events to be processed in tree (context menu show).
        if (SwingUtilities.isRightMouseButton(e)) {
          Component source = (Component)e.getSource();
          final MouseEvent parentEvent = SwingUtilities.convertMouseEvent(source, e, _tree);
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              // We cannot just _tree.dispatchEvent(e)
              // because we need convert coordinates X and Y of MouseEvent.
              _tree.dispatchEvent(parentEvent);
            }
          });
        } else if (!_tree.isCellEditing() && SwingUtilities.isLeftMouseButton(e)) {
          try {
            i_Body bd = _ctrl.getBody(_bodyID);
            if ((e.getClickCount() % 2 == 0) && bd.isRenamable()) {
              titleLabel.setVisible(false);
              titleField.setVisible(true);
              titleField.requestFocusInWindow();
              e.consume();

              // We're shut down main canvas key dispatcher when editing body name.
              // Setting it up located in @BodyTree.stopEditing() method.
              _ctrl.getMainCanvasCtrl().disableKeyDispatcher();
            }
          } catch (ExNoBody ex) {
          }
        }
      }
    });

    titleField.addKeyListener(new KeyAdapter() {
      /**
       * Invoked when a key has been pressed.
       */
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          String newBodyTitle = titleField.getText();
          try {
            if (!_ctrl.getBodyTitle(_bodyID).equals(newBodyTitle)) {
              // Новое имя должно отличаться от предыдущего.
              try {
                _ctrl.validateBodyTitle(newBodyTitle);
                _ctrl.renameBody(_bodyID, newBodyTitle);
                _ctrl.rebuild();
                _ctrl.setUndo("переименовать тело");
                _ctrl.redraw();
              } catch (InvalidBodyNameException ex) {
                _ctrl.status().error(ex.getMessage());
              }
            } else {
              _tree.stopEditing();
            }
          } catch (ExNoBody ex) {
          }
        }
      }
    });

    wrapper.add(titleLabel, new CC().height("14!"));
    wrapper.add(titleField, new CC().height("14!").growX().pushX());

    titleField.setVisible(false);

    return wrapper;
  }
}
