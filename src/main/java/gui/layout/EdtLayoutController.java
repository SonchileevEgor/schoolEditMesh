package gui.layout;

import gui.EdtFrame;
import gui.inspector.InspectorPanel;
import gui.statusbar.StatusStrip;
import gui.toolbox.ToolPanel;
import java.util.ArrayList;
import javax.swing.JSplitPane;

/**
 * Layout controller of the main frame.
 * @author alexeev
 */
public class EdtLayoutController {
  private EdtCanvasPanel _canvas;
  private InspectorPanel _inspector;
  private JSplitPane _centerPanel;
  private StatusStrip _status;
  private ToolPanel _tools;
  private EdtFrame _frame;

  private int _lastDividerLocation;

  private ArrayList<i_LayoutChangeListener> _layoutListeners;

  public EdtLayoutController( EdtCanvasPanel canvas, InspectorPanel inspector,
          JSplitPane centerPanel, StatusStrip status, ToolPanel tools, EdtFrame frame ){
    _layoutListeners = new ArrayList<i_LayoutChangeListener>();
    _canvas = canvas;
    _inspector = inspector;
    _centerPanel = centerPanel;
    _status = status;
    _tools = tools;
    _frame = frame;
    _lastDividerLocation = _centerPanel.getDividerLocation();
  }

  /**
   * Set visibility of the console window.
   */
  public void setConsoleVisible( boolean visible ){
    _canvas.console.setVisible(visible);
  }

  public boolean isConsoleVisible() {
    return _canvas.console.isVisible();
  }

  /**
   * Set visibility of the inspector panel.
   */
  public void toggleInspector() {
    int dividerLocation = _centerPanel.getDividerLocation();
    if( dividerLocation == 1 ){
      _centerPanel.setDividerLocation(_lastDividerLocation);
    } else {
      _centerPanel.setDividerLocation(0);
    }
    _lastDividerLocation = dividerLocation;
//    _frame.revalidate();
//    _frame.repaint();
  }

  public StatusStrip getStatusStrip() {
    return _status;
  }

  public EdtFrame getFrame() {
    return _frame;
  }

  public void lockFrame() {
    _frame.setEnabled(false);
  }

  public void unlockFrame() {
    _frame.setEnabled(true);
  }

  public void setRotationWidgetVisible( boolean visible ){
    _canvas.rotationWidget.setVisible(visible);
//    _canvas.recomposeLayout();
  }

  public boolean isRotationWidgetVisible() {
    return _canvas.rotationWidget.isVisible();
  }

  public void setCanvasToolPanelExpanded( boolean expanded ){
    _canvas.toolPanel.setExpanded(expanded);
//    _canvas.recomposeLayout();
  }

  public boolean isCanvasToolPanelExpanded() {
    return _canvas.toolPanel.isExpanded();
  }

  // observer section

  public void addLayoutListener(i_LayoutChangeListener listener) {
    _layoutListeners.add(listener);
  }

  public void removeLayoutListener(i_LayoutChangeListener listener) {
    _layoutListeners.remove(listener);
  }

  public void notifyLayoutChanged() {
    for( i_LayoutChangeListener l : _layoutListeners ){
      l.layoutChanged();
    }
  }
}
