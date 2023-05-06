package license.network;

/**
 * Thrown to indicate that there is an error with internet connection.
 *
 * @author MaxBrainRus
 */
public class ConnectionFailedException extends Exception {
  public ConnectionFailedException() {
    super("Ошибка: не удалось соединиться с сервером!");
  }
}
