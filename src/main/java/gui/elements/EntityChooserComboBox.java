package gui.elements;

import bodies.BodyType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_Body;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 * Элемент ввода якоря или тела.
 *
 * @author alexeev
 */
public class EntityChooserComboBox extends JComboBox {
  public EntityChooserComboBox(Editor edt, boolean points, boolean ribs, boolean polys, BodyType ... bodies) {
    _anchorQty = 0;
    _ids = new ArrayList<>();
    if (points) {
      for (i_Anchor a : edt.anchors().getPointAnchors()) {
        try {
          addItem("Точка " + edt.getAnchorTitle(a.id()));
          _ids.add(a.id());
          _anchorQty++;
        } catch (ExNoAnchor ex) {}
      }
    }
    if (ribs) {
      for (i_Anchor a: edt.anchors().getRibAnchors()) {
        try {
          addItem("Отрезок " + edt.getAnchorTitle(a.id()));
          _ids.add(a.id());
          _anchorQty++;
        } catch (ExNoAnchor ex) {}
      }
    }
    if (polys) {
      for (i_Anchor a: edt.anchors().getPolyAnchors()) {
        try {
          addItem("Грань " + edt.getAnchorTitle(a.id()));
          _ids.add(a.id());
          _anchorQty++;
        } catch (ExNoAnchor ex) {}
      }
    }
    for (BodyType type : bodies) {
      for (i_Body b : edt.bd().getBodiesByType(type)) {
        addItem(b.alias() + " " + b.getTitle());
        _ids.add(b.id());
      }
    }
  }

  /**
   * Показывает, выбрано ли тело (TRUE) или якорь (FALSE)
   * @return
   */
  public boolean isBody() {
    return getSelectedIndex() >= _anchorQty;
  }

  public String getSelectedID() {
    return _ids.get(getSelectedIndex());
  }

  private int _anchorQty;
  private ArrayList<String> _ids;
}
