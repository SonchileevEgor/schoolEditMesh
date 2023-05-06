package license.pcidentifier;

/**
 * Thrown to indicate that there is an error getting MAC address.
 */
public class NoMACAddressException extends Exception {
  public NoMACAddressException() {
    super("Ошибка: не удалось идентифицировать устройство!");
  }
}
