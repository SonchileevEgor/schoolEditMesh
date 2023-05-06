package license.encryption;

/**
 * Internal error in encryption algorithm
 */
public class EncryptionInternalError extends RuntimeException {
  public EncryptionInternalError() {
    super("Произошла внутренняя ошибка шифрования.");
  }
}
