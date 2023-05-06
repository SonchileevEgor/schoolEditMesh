package license.applications;

/**
 * Server configuration
 */
public interface KeyServerConfig extends NetworkConfig {
    /**
     * Get maximum length of the queue of incoming connections
     * @return
     */
    int getNumRequests();

    /**
     * Get MD5 hash of admin password which allows get new license keys
     * @return
     */
    String getAdminPasswordHash();

    /**
     * Get seed for secure random algorithm which using for generation license keys.
     * The same seed generates the same chain of keys.
     * @return
     */
    String getMasterSeed();

    /**
     * Get directory path for file which used for saving license information
     * @return
     */
    String getLicenseDirectory();
}
