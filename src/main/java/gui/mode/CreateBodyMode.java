package gui.mode;

import editor.InvalidBodyNameException;
import editor.i_Anchor;
import geom.Vect3d;
import gui.EdtController;
import gui.mode.param.CreateModeParamType;
import gui.mode.param.Message;
import gui.mode.param.ModeParamManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import opengl.Render;
import opengl.drawing.DrawingAction;
import opengl.drawing.DrawingQueue;

/**
 * @author rita
 */
public abstract class CreateBodyMode extends ScreenMode {

  protected static ModeParamManager _param;
  protected Message _msg;
  protected String _name;
  protected ArrayList<String> _anchorID;
  protected ArrayList<i_Anchor> _anchor;
  protected int _numOfChosenAnchors;
  protected boolean is3d; // флаг, указывающий, в каком 2D или 3D режиме запущен сценарий

  public CreateBodyMode(EdtController ctrl) {
    super(ctrl);

    if (_param == null) {
      _param = new ModeParamManager(ctrl);
    }
  }

  @Override
  public void run() {
    super.run();
    _anchorID = new ArrayList<>();
    _anchor = new ArrayList<>();
    _numOfChosenAnchors = 0;
    _param.reset();
    _msg = new Message();
    is3d = canvas().is3d();
    setCreationMessengers();
    setName();
    _ctrl.status().showMessage(_msg.current()); // start message
    DrawingQueue.add(new DrawingAction(0) {
      @Override
      public void draw(Render ren) {
        nativeDraw0(ren);
      }
    });
    DrawingQueue.add(new DrawingAction(1) {
      @Override
      public void draw(Render ren) {
        nativeDraw1(ren);
      }
    });
  }

  /**
   * Set messages for interactive building of object.
   */
  abstract protected void setCreationMessengers();

  /**
   * Set default name of the new body.
   */
  abstract protected void setName();

  /**
   * Set title of the new body.
   *
   * @param newName
   */
  public void setName(String newName) {
    _name = newName;
  }

  /**
   * Get title of the new body.
   *
   * @return
   */
  public String getName() {
    return _name;
  }

  /**
   * Validate title of the new body.
   *
   * @throws editor.InvalidBodyNameException
   */
  protected void validateBodyTitle() throws InvalidBodyNameException {
    _ctrl.validateBodyTitle(_name);
  }

  /**
   * Validate title of the new body (with suffix).
   *
   * @param suffix
   * @throws editor.InvalidBodyNameException
   */
  protected void validateName(String suffix) throws InvalidBodyNameException {
    _ctrl.validateBodyTitle(_name + suffix);
  }

  /**
   * Set drawings specifically for this mode. Draw objects of 1st drawing queue
   *
   * @param ren
   */
  abstract protected void nativeDraw1(Render ren);

  /**
   * Set drawings specifically for this mode. Draw objects of 2nd drawing queue
   *
   * @param ren
   */
  abstract protected void nativeDraw0(Render ren);

  /**
   * Create new body. You need provide a check that new title is valid.
   */
  abstract protected void create();

  /**
   * Give possibility to user to select name of body.
   *
   * @return
   */
  abstract public boolean isNameSelectable();

  /**
   * Provide name validation before body creation.
   */
  protected final void createWithNameValidation() {
    try {
      validateBodyTitle();
      create();
    } catch (InvalidBodyNameException ex) {
      _ctrl.status().showMessage(ex.getMessage());
    }
  }

  protected void changeValue(CreateModeParamType type, KeyEvent e) {
    if (press.up(e)) {
      _param.inc(type);
      _param.showValue(type);
      _ctrl.redraw();
    } else if (press.down(e)) {
      _param.dec(type);
      _param.showValue(type);
      _ctrl.redraw();
    } else if (press.enter(e)) {
      _param.setChosen(type);
      _ctrl.status().showMessage(_msg.current());
      _param.showValue(type);
      _ctrl.redraw();
    }
    canvas().notifyModeParamChange(type);
  }

  protected boolean isChosen(CreateModeParamType type) {
    return _param.isChosen(type);
  }

  protected void showValue(CreateModeParamType type) {
    _param.showValue(type);
  }

  protected void changeValue(CreateModeParamType type, int wheelRotation) {
    _param.change(type, -wheelRotation); // При прокрутке вниз значение wheelRotation положительно
    _param.showValue(type);
    canvas().notifyModeParamChange(type);
    _ctrl.redraw();
  }

  protected double valueAsDouble(CreateModeParamType type) {
    return _param.getValueAsDouble(type);
  }

  protected int valueAsInt(CreateModeParamType type) {
    return _param.getValueAsInt(type);
  }

  protected String valueAsString(CreateModeParamType type) {
    return _param.getValueAsString(type);
  }

  protected i_Anchor anchor(int i) {
    return _anchor.get(i);
  }

  protected String id(int i) {
    return _anchorID.get(i);
  }

  protected Vect3d point(int i) {
    return _anchor.get(i).getPoint();
  }

  protected i_Anchor lastAnchor() {
    return _anchor.get(_anchor.size() - 1);
  }

  protected void chooseAnchor(i_Anchor a) {
    if (a == null) {
      return;
    }
    _anchor.add(a);
    _anchorID.add(a.id());
    _numOfChosenAnchors++;
    _ctrl.status().showMessage(_msg.current());
    _ctrl.redraw();
  }

  public ModeParamManager getParam() {
    return _param;
  }

  public void setValue(CreateModeParamType type, Object value) {
    _param.setValue(type, value);
    canvas().notifyModeParamChange(type);
  }
}
