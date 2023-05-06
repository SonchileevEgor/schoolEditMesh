package gui.statusbar;

import static config.Config.SCREEN_KEYBOARD_ON;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import gui.laf.AppColor;
import gui.ui.EdtPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.scenegl.SceneType;

/**
 * Статусная строка.
 *
 * @author alexeev
 */
public class StatusStrip extends EdtPanel {
  private EdtController _ctrl;

  /**
   * Switch 2D/3D mode button.
   */
  private final JButton _3dmodeButton;

  /**
   * Show/hide screen keyboard button.
   */
  private final JButton _keyboardButton;

  /**
   * Switch 2D(3D) / STL-view mode button.
   */
  private final JButton _stlViewButton;
  /**
   * Message field.
   */
  private final JLabel _message;
  
  /**
   * Панель с изображениями клавиш, которые используются в режиме построения.
   */
  private final JPanel _keysPanel;

  /**
   * Value field.
   */
  private final JLabel _value;
  private final JProgressBar _progressBar;
  
  /**
   * Wait animation field
   */
  private final JLabel _waitLabel;

  /**
   * Direction of progress.
   * Increasing / decreasing.
   */
  private boolean _progressDirection;
  private Timer _progressTimer;
  private Timer _errorTimer;
  private boolean _errorMessageShown;

  public StatusStrip(EdtController ctrl) {
    super(new MigLayout(new LC().fillX().insetsAll("0")));
    _ctrl = ctrl;
    
    _3dmodeButton = new JButton(_ctrl.getScene().is3d() ?
        IconList.MODE_3D_ON.getMediumIcon() : IconList.MODE_3D_OFF.getMediumIcon());
    _3dmodeButton.setToolTipText("Режим 2D / 3D");
    _3dmodeButton.setFocusable(false);
    
    _keyboardButton = new JButton(SCREEN_KEYBOARD_ON.value() ?
        IconList.KEYBOARD_ON.getMediumIcon() : IconList.KEYBOARD_OFF.getMediumIcon());
    _keyboardButton.setToolTipText("Экранная клавиатура");
    _keyboardButton.setFocusable(false);
    
    _stlViewButton = new JButton(_ctrl.getScene().getSceneType() == SceneType.SceneSTL ? 
        IconList.MODE_STL_ON.getMediumIcon() : IconList.MODE_STL_OFF.getMediumIcon());
    _stlViewButton.setToolTipText("Режим 2D(3D) / STL-view");
    _stlViewButton.setFocusable(false);
    
    _message = new JLabel();
    _value = new JLabel();
    _keysPanel = new JPanel(new MigLayout(new LC().alignX("right")));
    _keysPanel.setOpaque(false);
    _progressBar = new JProgressBar(0, 100);
    _progressBar.setVisible(false);
    _waitLabel = new JLabel(IconList.LOAD_ANIMATED.getMediumIcon());
    _waitLabel.setVisible(false);
    _errorMessageShown = false;
    
    _keyboardButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _ctrl.getKeyboard().toggleVisible();
      }
    });
    _3dmodeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _ctrl.getLayoutController().lockFrame();
        ActionFactory.SWITCH_MODE_2D_3D.getAction(_ctrl).actionPerformed(e);
        if (_ctrl.getScene().is3d()) {
          _3dmodeButton.setIcon(IconList.MODE_3D_ON.getMediumIcon());
          _stlViewButton.setIcon(IconList.MODE_STL_OFF.getMediumIcon());
        } else {
          _3dmodeButton.setIcon(IconList.MODE_3D_OFF.getMediumIcon());
          _stlViewButton.setIcon(IconList.MODE_STL_OFF.getMediumIcon());
        }
        _ctrl.getLayoutController().unlockFrame();
        _ctrl.getFrame().requestFocus();
      }
    });
    _stlViewButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            _ctrl.getLayoutController().lockFrame();
        ActionFactory.TOGGLE_STL_VIEW.getAction(_ctrl).actionPerformed(e);
        if (_ctrl.getScene().is3d()) 
          _3dmodeButton.setIcon(IconList.MODE_3D_OFF.getMediumIcon());
        if (_ctrl.getScene().getSceneType() == SceneType.SceneSTL)
          _stlViewButton.setIcon(IconList.MODE_STL_ON.getMediumIcon());
        _ctrl.getLayoutController().unlockFrame();
        _ctrl.getFrame().requestFocus();
        }
    });
    JPanel waitPanel = new JPanel(new MigLayout(new LC().insetsAll("0")));
    waitPanel.add(_waitLabel);
    waitPanel.setOpaque(false);
    setBackground(AppColor.LIGHT_GRAY.color());
    setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColor.DARK_GRAY.color()));
    JPanel leftWrapper = new JPanel(new MigLayout(
            new LC().insets("0", "2", "0", "0").gridGap("2", "0").fillY()));
    JPanel rightWrapper = new JPanel(new MigLayout(
            new LC().leftToRight(false).hideMode(0).
                    insets("0", "0", "0", "2").gridGap("2", "0").fillY()));
    leftWrapper.setOpaque(false);
    rightWrapper.setOpaque(false);
    
    
    rightWrapper.add(waitPanel, new CC().width("30!"));
    rightWrapper.add(_progressBar, new CC().width("150!"));
    rightWrapper.add(_value, new CC().width("150!"));
    rightWrapper.add(_message, new CC().width("400!"));
    rightWrapper.add(_keysPanel, new CC().width("120!").height("34!"));
    
    leftWrapper.add(_3dmodeButton, new CC().width("30!"));
    leftWrapper.add(_keyboardButton, new CC().width("30!"));
    leftWrapper.add(_stlViewButton, new CC().width("30!"));

    add(leftWrapper, new CC().dockWest().height("34!"));
    add(rightWrapper, new CC().dockEast().height("34!"));
  }

  /**
   * Показать текстовое сообщение.
   * Вставки в виде {...} интерпретируются как значки с клавишами или кнопками мыши.
   * @param message 
   */
  public void showMessage(String message) {
    _errorMessageShown = false;
    _message.setForeground(Color.BLACK);
    
    Pattern p = Pattern.compile("\\{([A-Za-z0-9]*)\\}");
    Matcher m = p.matcher(message);
    ArrayList<String> keys = new ArrayList<>();
    while ( m.find() ){
      keys.add(m.group(1));
    }
    showKeys(keys);
    
    _message.setText(p.split(message)[0]);
  }
  
  public void showValue(String value) {
    _value.setText(value);
  }
  
  public void showKeys(List<String> keys) {
    _keysPanel.removeAll();
    for( String key : keys ){
      _keysPanel.add(new JLabel(IconList.getKeyImage(key)));
    }
    _keysPanel.revalidate();
    _keysPanel.repaint();
  }

  public void setKeyboardIcon(boolean visible) {
    _keyboardButton.setIcon(visible ?
            IconList.KEYBOARD_ON.getMediumIcon() :
            IconList.KEYBOARD_OFF.getMediumIcon());
  }

  /**
   * Set visibility of progress bar.
   * @param visible
   */
  public void setProgressBarVisible(boolean visible) {
    _progressBar.setVisible(visible);
  }

  /**
   * Set value of progress.
   * Available values 0..100.
   * @param value
   */
  public void setProgressValue(int value) {
    _progressBar.setValue(value);
  }

  /**
   * Симулировать занятость приложения при помощи полосы прогрузки.
   */
  public void simulateProgress() {
    _progressDirection = true;
    _progressTimer = new Timer(300, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int value = _progressBar.getValue();
        value = (_progressDirection) ? value + 1 : value - 1;
        if (value == 100 || value == 0) {
          // bound of progress achieved
          _progressDirection = !_progressDirection;
        }
        _progressBar.setValue(value);
      }
    });
    _progressTimer.start();
  }
  
  /**
   * Симулировать занятость приложения при помощи анимации.
   */
  public void simulateAction() {
    _waitLabel.setVisible(true);
  }

  public void error(String message) {
    if (!_errorMessageShown) {
      final String oldMessage = _message.getText();
      _message.setForeground(Color.RED);
      _message.setText(message);
      _errorMessageShown = true;
      _errorTimer = new Timer(1500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          _message.setForeground(Color.BLACK);
          _message.setText(oldMessage);
          _errorTimer.stop();
          _errorMessageShown = false;
        }
      });
    }
    _errorTimer.start();
  }

  public void haltProgress() {
    if (_progressTimer != null)
      _progressTimer.stop();
  }

  public void clear() {
    _errorMessageShown = false;
    _value.setText("");
    _message.setText("");
    _message.setForeground(Color.BLACK);
    _keysPanel.removeAll();
    _progressBar.setVisible(false);
    _waitLabel.setVisible(false);
    revalidate();
    repaint();
  }
}
