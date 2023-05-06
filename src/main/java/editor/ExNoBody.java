package editor;

public class ExNoBody extends ExBadRef {
  private static final long serialVersionUID = 1L;

  public ExNoBody( String message ){
    super("не могу найти тело <" + message + ">");
  }
};
