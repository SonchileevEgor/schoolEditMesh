package license.encryption;

/**
 * If you try to decrypt message with wrong key or message is not encrypted message, you will get this exception
 */
public class WrongEncryptedKeyException extends Exception {
  public WrongEncryptedKeyException() {
    super("Ошибка: ключ дешифрования неверен!");
  }
}
