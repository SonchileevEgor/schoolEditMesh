package license.network;


/**
 * Thrown to indicate that license key is not valid.
 *
 * @author MaxBrainRus
 */
public class WrongLicenseKeyException extends Exception {
  public WrongLicenseKeyException() {
    super("Ошибка: лицензионный ключ неверен!");
  }
}