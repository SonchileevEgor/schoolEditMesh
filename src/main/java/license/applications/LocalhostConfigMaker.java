package license.applications;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Network and server configurations using localhost.
 */
public class LocalhostConfigMaker implements KeyServerConfig, NetworkConfig {
    public final int PORT = 14080;
    private final int REQUESTS = 1000;

    private String defaultAdminPasswordHash = "60996AB46B4C842DBDA76093835E4A76";
    private String defaultEncryptionKey = "This is very security encryption key";
    private String defaultMasterSeed = "School 3d editor seed";
    private InetAddress inetAddress;

    private String defaultAdminPassword = "It_is_the_very_security_password";

    public LocalhostConfigMaker() {
        try {
            inetAddress = InetAddress.getByName(null);
        } catch (UnknownHostException e) {
            System.out.println("Can't find localhost. Houston, we've had a problem");
            return;
        }

    }

    public String getDefaultAdminPassword() {
        return defaultAdminPassword;
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public int getNumRequests() {
        return REQUESTS;
    }

    @Override
    public int getPort() {
        return PORT;
    }

    @Override
    public String getAdminPasswordHash() {
        return defaultAdminPasswordHash;
    }

    @Override
    public String getEncryptionKey() {
        return defaultEncryptionKey;
    }

    @Override
    public String getMasterSeed() {
        return defaultMasterSeed;
    }

    @Override
    public String getLicenseDirectory() {
        return null;
    }
}
