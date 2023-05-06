package gui.dialog;

import editor.InvalidBodyNameException;
import gui.EdtController;
import gui.elements.NameTextField;
import gui.ui.EdtInputDialog;
import gui.ui.EdtRadioButton;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import latex.ImageGenerator;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import util.Util;

/**
 * Choose names of points before polyhedron creation.
 * @author alexeev.
 */
public class ChoosePointNamesDialog extends EdtInputDialog {
  public static final int USER_CHOICE_INDEX = -1;
  private static final Dimension DEFAULT_MIN_SIZE = new Dimension(400, 150);

  private EdtController _ctrl;
  private EdtRadioButton[] _autoButtons;
  private boolean[] _fixTitles;
  private EdtRadioButton _userNamingButton;
  private final MultiTextField _userTextField;
  private int _choice;

  /**
   * Choose names of points before polyhedron creation.
   * @param ctrl
   * @param choices
   * @param defaultTitles
   * @param fixTitles
   */
  public ChoosePointNamesDialog(EdtController ctrl, String[] choices, String[] defaultTitles, boolean[] fixTitles) {
    super(ctrl.getFrame(), "Выберите имена вершин");
    _ctrl = ctrl;
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    ButtonGroup group = new ButtonGroup();

    _fixTitles = fixTitles;
    _autoButtons = new EdtRadioButton[choices.length];
    for( int i = 0; i < choices.length; i++ ){
      final int index = i;
      _autoButtons[i] = new EdtRadioButton(ImageGenerator.createImage(choices[i], 14));
      _autoButtons[i].addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if( e.getStateChange() == ItemEvent.SELECTED )
            _choice = index;
        }
      });
      group.add(_autoButtons[i]);
      addItem(_autoButtons[i], "left");
    }
    _autoButtons[0].setSelected(true);

    _userNamingButton = new EdtRadioButton("Задать вручную:", false);
    _userTextField = new MultiTextField(defaultTitles, fixTitles);
    _userTextField.setEnabled(false);

    group.add(_userNamingButton);

    addItem(_userNamingButton, "left");
    addItem(_userTextField, "left");
    addButtons();
    addFeedback();

    _userNamingButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if( e.getStateChange() == ItemEvent.SELECTED ) {
          _userTextField.setEnabled(true);
          _choice = USER_CHOICE_INDEX;
        } else {
          _userTextField.clearBackground();
          _userTextField.setEnabled(false);
        }
      }
    });

    setMinimumSize(DEFAULT_MIN_SIZE);
    pack();
    setCenterLocation();
  }

  public int getChoice() {
    return _choice;
  }

  public boolean isUserChoice() {
    return _choice == USER_CHOICE_INDEX;
  }

  /**
   * Get values for user's choice.
   * @return
   */
  public String[] getValues() {
    return _userTextField.getValues();
  }

  @Override
  public AbstractAction OKAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed( ActionEvent e ){
        if( _choice == USER_CHOICE_INDEX ){
          _userTextField.clearBackground();
          ArrayList<Integer> failIndices = new ArrayList<Integer>(); // list of failed text field indices
          String errString = "";
          String[] values = getValues();
          if( Util.hasDuplicates(values) ){
            failIndices.addAll(Util.getDuplicates(values));
            errString = "Имена точек должны быть различны!";
          } else {
            for( int i = 0; i < values.length; i++ ){
              if( values[i].isEmpty() ){
                failIndices.add(i);
                errString = "Введите имя точки!";
                break;
              } else if (!_fixTitles[i]) {
                try {
                  _ctrl.validateBodyTitle(values[i]);
                } catch( InvalidBodyNameException ex ){
                  failIndices.add(i);
                  errString = ex.getMessage();
                  break;
                }
              }
            }
          }
          if( failIndices.isEmpty() ){
            accepted = true;
            dispose();
          } else {
            _userTextField.setErrorBackground(failIndices, true);
            showMessage(errString, 2);
          }
        } else {
          accepted = true;
          dispose();
        }
      }
    };
  }
}

class MultiTextField extends JPanel {
  NameTextField[] fields;
  boolean[] fixedFields;
  int size;

  public MultiTextField(String[] values, boolean[] fixedFields) {
    setLayout(new MigLayout(new LC().fillX(), new AC().gap("5")));
    size = values.length;
    fields = new NameTextField[size];
    this.fixedFields = fixedFields;
    for( int i = 0; i < size; i++ ){
      NameTextField f = new NameTextField(values[i]);
      fields[i] = f;
      if( this.fixedFields[i] ){
        fields[i].setEnabled(false);
        fields[i].setErrorBackground(false);
      }
      add(f, new CC().width("30!"));
    }
  }

  public MultiTextField(int size) {
    setLayout(new MigLayout(new LC().fillX(), new AC().gap("5")));
    this.size = size;
    fields = new NameTextField[size];
    for( int i = 0; i < size; i++ ){
      NameTextField f = new NameTextField();
      fields[i] = f;
      add(f, new CC().width("30!"));
    }
  }
  
  public void clear() {
    for( int i = 0; i < size; i++){
      // Don't change value of fixed fields.
      if( !fixedFields[i] )
        fields[i].setText(null);
    }
  }

  /**
   * Set default background for fields.
   */
  public void clearBackground() {
    for( NameTextField f : fields ){
      f.setErrorBackground(false);
    }
  }

  /**
   * Set background for failed text fields.
   * @param i
   * @param errorBg
   */
  public void setErrorBackground(List<Integer> errInd, boolean errorBg) {
    for( int i : errInd ) {
      fields[i].setErrorBackground(errorBg);
    }
  }

  public String[] getValues() {
    String[] result = new String[size];
    for( int i = 0; i < size; i++ ) {
      result[i] = fields[i].getText();
    }
    return result;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    for( int i = 0; i < size; i++){
      // Don't change state of fixed fields.
      fields[i].setEnabled(enabled && !fixedFields[i]);
    }
  }
}