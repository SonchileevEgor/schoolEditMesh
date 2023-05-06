package gui.elements;

import gui.laf.AppColor;
import gui.ui.EdtTextField;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Input text field with hint.
 * @author alexeev
 */
public class HintTextField extends EdtTextField implements FocusListener {
  protected String _hint;
  protected boolean _emptyModeOn;
  
  /**
   * Input text field with hint on background.
   * @param hint
   *    hint text
   */
  public HintTextField( String hint ){
    super();
    _hint = hint;
    setEmptyMode();
    addFocusListener(this);
  }
  
  public void clear() {
    setEmptyMode();
  }
  
  private void setEmptyMode(){
    _emptyModeOn = true;
    setForeground(AppColor.DARK_GRAY.color());
    setText(_hint);
    revalidate();
    repaint();
  }
  
  private void setTextMode(){
    _emptyModeOn = false;
    setForeground(Color.BLACK);
    setText(null);
    revalidate();
    repaint();
  }

  @Override
  public void focusGained(FocusEvent e) {
    if( _emptyModeOn ){
      setTextMode();
    }
  }

  @Override
  public void focusLost(FocusEvent e) {
    if( getText().isEmpty() || getText().equals(_hint) ){
      setEmptyMode();
    }
  }
}
