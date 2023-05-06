package updater;

/**
 * Ошибка в наименовании версии программы.
 * @author alexeev
 */
public class WrongVersionNumberException extends Exception {
  public WrongVersionNumberException(String msg){
    super(msg);
  }
}
