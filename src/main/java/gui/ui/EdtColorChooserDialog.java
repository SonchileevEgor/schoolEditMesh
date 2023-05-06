package gui.ui;

import gui.dialog.EdtDialog;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Color chooser dialog.
 * @author alexeev
 */
public class EdtColorChooserDialog extends EdtDialog {
  private JColorChooser _jcc;

  /**
   * Action performed on color change.
   */
  private ChangeListener _onColorChange;

  /**
   * Is color changed in the current session.
   */
  private boolean _isColorChanged;

  public EdtColorChooserDialog(JFrame owner) {
    super(owner, "Выберите цвет");
    setLayout(new GridBagLayout());
    _jcc = new JColorChooser(Color.WHITE);
    _jcc.setPreviewPanel(new JPanel());

    // set chooser panels
    AbstractColorChooserPanel[] panels = _jcc.getChooserPanels();
    AbstractColorChooserPanel[] newPanels = new AbstractColorChooserPanel[2];
    for( AbstractColorChooserPanel p : panels ){
      switch( p.getDisplayName() )  {
        case "Палитра":
          newPanels[0] = p;
          break;
        case "RGB":
          newPanels[1] = p;
          break;
      }
    }

    _jcc.setChooserPanels(newPanels);

    _jcc.getSelectionModel().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        _isColorChanged = true;
      }
    });

    // check if color was changed in the current session.
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentShown(ComponentEvent e) {
        _isColorChanged = false;
      }

      @Override
      public void componentHidden(ComponentEvent e) {
        // удаляем слушатель изменения цвета при закрытии
        // иначе при вызове setColor будет происходить обновление цвета старого элемента
        _jcc.getSelectionModel().removeChangeListener(_onColorChange);
        _onColorChange = null;
      }
    });

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    add(_jcc, gbc);

    pack();
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    setResizable(false);
    setModal(true);
    setAlwaysOnTop(true);
  }

  /**
   * Set action which is performed on color change.
   * Previous action will be removed.
   * @param listener
   */
  public void setColorChangeListener( ChangeListener listener ){
    // для модального режима эта проверка не нужна, т. к.
    // при закрытии окна происходит удаление слушателя,
    // но пусть будет.
    if( _onColorChange != null ){
      _jcc.getSelectionModel().removeChangeListener(_onColorChange);
    }
    _jcc.getSelectionModel().addChangeListener(listener);
    _onColorChange = listener;
  }

  /**
   * Is color changed in the current session.
   */
  public boolean isColorChanged() {
    return _isColorChanged;
  }

  public void setColor(Color c) {
    _jcc.setColor(c);
  }

  public Color getColor() {
    return _jcc.getColor();
  }
}