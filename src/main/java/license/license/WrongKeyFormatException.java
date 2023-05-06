package license.license;

/**
 * Key has wrong format.
 */
public class WrongKeyFormatException extends Exception {
  public WrongKeyFormatException() {
    super("Ошибка: формат лицензионного ключа неверен!");
  }
}
