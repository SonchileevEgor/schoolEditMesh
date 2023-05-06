package gui;

import static config.Config.PRECISION;
import gui.elements.NameTextField;
import gui.mode.CreateBodyMode;
import gui.mode.ScreenMode;
import gui.mode.i_ModeChangeListener;
import gui.mode.i_ModeParamChangeListener;
import gui.mode.param.CreateModeParamType;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import parser.MathParser;
import util.Util;

/**
 * Window for input screen mode parameters.
 *
 * @author alexeev
 */
public class EnterModeParamsPanel extends JPanel implements i_ModeChangeListener, i_ModeParamChangeListener {
  private final MathParser _parser;
  // Поле ввода имени тела
  private NameTextField _nameField;
  private CreateBodyMode _currentMode;
//  private EnumMap _currentModeParams;
  private boolean _isRunning;
//  private EnumMap<CreateModeParamType, NameTextField> _map;
  private TreeMap<Integer, CreateModeParamType> _currentParams;
  private TreeMap<CreateModeParamType, NameTextField> _nameFieldsByParam;
  private TreeMap<CreateModeParamType, JLabel> _nameOfPanelsForParams;
  private Integer _numOfUsedParam;

  public EnterModeParamsPanel() {
    setLayout(new MigLayout(new LC().hideMode(3).wrapAfter(2)));

    _nameFieldsByParam =  new TreeMap<>();
    _nameOfPanelsForParams = new TreeMap<CreateModeParamType, JLabel>();
    _nameField = new NameTextField();
    _numOfUsedParam = 0;
    _parser = new MathParser();
    _nameField.addPropertyChangeListener("text", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if( _isRunning ){
          _currentMode.setName((String)evt.getNewValue());
          _currentMode.repaint();
        }
      }
    });
    /**
     * When text field is focused,
     * we shut down key event transferring to the canvas,
     * except enter key press event.
     */
    _nameField.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        _currentMode.disableKeyDispatcher();
      }
      @Override
      public void focusLost(FocusEvent e) {
        _currentMode.enableKeyDispatcher();
      }
    });
    _nameField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == KeyEvent.VK_ENTER ||
            e.getKeyCode() == KeyEvent.VK_ESCAPE ){
          _currentMode.fireKeyPressEvent(e);
        }
      }
    });

    for (CreateModeParamType type : CreateModeParamType.values()) {
      _nameFieldsByParam.put(type, new NameTextField());
      _nameOfPanelsForParams.put(type, new JLabel(type.getTitle()));
    }

    setTextFields();
  }


  private void setTextFields () {
    for (final Map.Entry<CreateModeParamType, NameTextField> e : _nameFieldsByParam.entrySet()) {
      e.getValue().addPropertyChangeListener("text", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          if( _isRunning ){
              try {
                // _currentMode.setName((String)evt.getNewValue());
                String s = (String)evt.getNewValue();
                if (s.length() > 0){
                  if (_numOfUsedParam > 0) {
                    _currentMode.getParam().setValue(e.getKey(), _parser.Parse(s));
                    _currentMode.getParam().showValue(e.getKey());
                  }
                }
              } catch (Exception ex) {}
            _currentMode.repaint();
          }
        }
      });
      /**
       * When text field is focused,
       * we shut down key event transferring to the canvas,
       * except enter key press event.
       */
      e.getValue().addFocusListener(new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
          _currentMode.disableKeyDispatcher();
        }
        @Override
        public void focusLost(FocusEvent e) {
          _currentMode.enableKeyDispatcher();
        }
      });
      e.getValue().addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if( e.getKeyCode() == KeyEvent.VK_ENTER ){
            _currentMode.fireKeyPressEvent(e);
            _numOfUsedParam++;  // Переход к следующему параметру
          }
        }
      });
    }

    CC fieldCC = new CC().width("150!");

    add(new JLabel("Имя объекта:"));
    add(_nameField, fieldCC);
    for (Map.Entry<CreateModeParamType, NameTextField> e : _nameFieldsByParam.entrySet()) {
      add(_nameOfPanelsForParams.get(e.getKey()));
      add(e.getValue(), fieldCC);
    }
  }

  private void run() {
    if( _currentMode.isNameSelectable() ) {
      _isRunning = true;
      _nameField.setText(_currentMode.getName());
      setVisible(true);
    }
    else {
      setVisible(false);
    }
    if (_numOfUsedParam > 0){
      _isRunning = true;
      for (Map.Entry<CreateModeParamType, NameTextField> e : _nameFieldsByParam.entrySet()) {
        if (_currentMode.getParam().getUsedParams().get(e.getKey()) != 0)
            e.getValue().setText(
                    Util.valueOf(_currentMode.getParam().getValueAsDouble(e.getKey()), PRECISION.value()));
        }

      setVisible(true);
    } else {
      //setVisible(false);
    }
  }

  private void stop() {
    _isRunning = false;
    setVisible(false);
  }

  @Override
  public void updateMode(ScreenMode mode) {
    if( mode instanceof CreateBodyMode ){
      _numOfUsedParam = 0;
      _currentMode = (CreateBodyMode)mode;
      Set<CreateModeParamType> _arr = _currentMode.getParam().getUsedParams().keySet();
      _currentParams = new TreeMap<>();
      for (CreateModeParamType type : _arr) {
        if (_currentMode.getParam().getUsedParams().get(type) > 0) {
          _currentParams.put(_currentMode.getParam().getUsedParams().get(type), type);
        }
      }

      if (_currentParams.size() > 0) {
        _numOfUsedParam = 1;
      }

      for (Map.Entry<CreateModeParamType, NameTextField> e : _nameFieldsByParam.entrySet()) {
        if (_currentMode.getParam().getUsedParams().get(e.getKey()) == 0) {
          e.getValue().setVisible(false);
          _nameOfPanelsForParams.get(e.getKey()).setVisible(false);
        } else {
          e.getValue().setVisible(true);
          _nameOfPanelsForParams.get(e.getKey()).setVisible(true);
        }
      }
      run();
    } else {
      stop();
    }
  }

  @Override
  public void updateParams(CreateModeParamType type) {
    if ( _isRunning ) {
      if (_numOfUsedParam > 0){
        _nameFieldsByParam.get(type).setText(
                Util.valueOf(_currentMode.getParam().getValueAsDouble(type), PRECISION.value()));
//        setVisible(true);
      }
    }
  }
}
