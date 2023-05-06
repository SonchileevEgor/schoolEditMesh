package license.network;

/**
 * Thrown to indicate that admin password is not valid.
 */
public class WrongAdminPasswordException extends Exception {
    public WrongAdminPasswordException() {
        super("Ошибка: неверный пароль администратора");
    }
}
