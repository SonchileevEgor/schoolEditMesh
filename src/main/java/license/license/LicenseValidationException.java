package license.license;

/**
 * Exception thrown when license info is invalid.
 * @author alexeev
 */
public class LicenseValidationException extends Exception {
  public LicenseValidationException( String msg ){
    super(msg);
  }
}
