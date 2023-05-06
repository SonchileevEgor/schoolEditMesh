package editor;

public class ExNoAnchor extends ExBadRef {
  private static final long serialVersionUID = 1L;

  public ExNoAnchor( String message ){
    super("не могу найти <" + message + ">");
  }
};
