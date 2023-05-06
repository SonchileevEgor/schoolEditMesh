package gui.ui;

import gui.laf.AppColor;
import java.awt.Cursor;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Wrapper for JTextField.
 *
 * @author alexeev
 */
public class EdtTextField extends JTextField {
  public static final String TEXT_PROPERTY = "text";
  protected PlainDocument document;

  public EdtTextField() {
    super();
    setHorizontalAlignment(JTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    document = new EdtNameDocument();
    setDocument(document);
  }

  public EdtTextField(String text) {
    super(text);
    setHorizontalAlignment(JTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    document = new EdtNameDocument();
    setDocument(document);
  }

  public EdtTextField(int columns) {
    super(columns);
    setHorizontalAlignment(JTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    document = new EdtNameDocument();
    setDocument(document);
  }

  public EdtTextField(String text, int columns) {
    super(text, columns);
    setHorizontalAlignment(JTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    document = new EdtNameDocument();
    setDocument(document);
  }

  public EdtTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    setHorizontalAlignment(JTextField.RIGHT);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    document = new EdtNameDocument();
    setDocument(document);
  }

  /**
   * Set background for error case.
   * @param errorBg
   */
  public void setErrorBackground(boolean errorBg) {
    if( errorBg ) {
      setBackground(AppColor.ORANGE_ULTRA_LIGHT.color());
    } else {
      setBackground(isEnabled() ?
              UIManager.getColor("TextField.background") :
              UIManager.getColor("TextField.disabledBackground"));
    }
//    revalidate();
//    repaint();
  }

  class EdtNameDocument extends PlainDocument {
    private boolean ignoreEvents = false;

    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
      String oldValue = EdtTextField.this.getText();
      this.ignoreEvents = true;
      super.replace(offset, length, text, attrs);
      this.ignoreEvents = false;
      String newValue = EdtTextField.this.getText();
      if (!oldValue.equals(newValue))
        EdtTextField.this.firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
      String oldValue = EdtTextField.this.getText();
      super.remove(offs, len);
      String newValue = EdtTextField.this.getText();
      if (!ignoreEvents && !oldValue.equals(newValue))
        EdtTextField.this.firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
    }
  }
}
