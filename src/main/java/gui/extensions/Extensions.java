package gui.extensions;

/**
 * Расширения приложения.
 * @author alexeev
 */
public enum Extensions {
  SECOND_ORDER_CURVES("Кривые второго порядка", "second_order_curves",
          "<html><strong>Модуль построения кривых второго порядка</strong><br>"
                  + "Эллипс, парабола, гипербола, коника по пяти точкам", true, true, true),
  SECOND_ORDER_SURFACES("Поверхности второго порядка", "second_order_surfaces",
          "<html><strong>Модуль построения поверхностей второго порядка</strong><br>"
                  + "Эллипсоид, гиперболоиды и параболоиды", false, true, true),
  PARALLELOHEDRA("Параллелоэдры", "parallelohedra",
          "<html><strong>Параллелоэдры</strong> &ndash; "
                  + "многогранники, которыми можно замостить пространство "
                  + "без &laquo;дыр&raquo; и перекрытий", false, true, false),
  PLATONIC_BODIES("Правильные многогранники", "platonic_bodies",
          "<html><strong>Правильный многогранник</strong> &ndash; это выпуклый многогранник, <br>"
                  + "все грани которого являются равными правильными многоугольниками,<br>"
                  + "и из вершин которого выходит равное количество рёбер.<br>"
                  + "Правильных многогранников пять: тетраэдр, куб, октаэдр, додекаэдр и икосаэдр",
          false, true, true),
  SPACE_TRANSFORM("Преобразования пространства", "space_transform",
          "<html><strong>Преобразования пространства <sup>&beta;</sup></strong><br>"
                  + "Поворот, сдвиг, симметричное отражение и гомотетия.", true, true, false);
  
  Extensions(String name, String machineName, String description,
          boolean is2d, boolean is3d, boolean onByDefault){
    _name = name;
    _machineName = machineName;
    _description = description;
    _is2d = is2d;
    _is3d = is3d;
    _onByDefault = onByDefault;
  }
  
  public boolean isOnByDefault() {
    return _onByDefault;
  }
  
  public String getName() {
    return _name;
  }
  
  public String getMachineName() {
    return _machineName;
  }
  
  public String getDescription() {
    return _description;
  }
  
  private String _name;
  private String _machineName;
  private String _description;
  private boolean _is2d;
  private boolean _is3d;
  private boolean _onByDefault;
}
