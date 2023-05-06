package license.applications;

import java.net.InetAddress;

/**
 * KeyServerConfig realization with customization of all parameters.
 */
public class CustomKeyServerConfig implements KeyServerConfig {
    public int PORT;
    InetAddress inetAddress;
    private int REQUESTS;
    private String adminPasswordHash;
    private String encryptionKey;
    private String masterSeed;
    private String licenseDirectory;

    public CustomKeyServerConfig(int port, int REQUESTS, String adminPasswordHash, String encryptionKey, String masterSeed, InetAddress inetAddress, String licenseDirectory) {
        this.PORT = port;
        this.REQUESTS = REQUESTS;
        this.adminPasswordHash = adminPasswordHash;
        this.encryptionKey = encryptionKey;
        this.masterSeed = masterSeed;
        this.inetAddress = inetAddress;
        this.licenseDirectory = licenseDirectory;
    }

    public CustomKeyServerConfig(KeyServerConfig keyServerConfig) {
        this(keyServerConfig.getPort(), keyServerConfig.getNumRequests(), keyServerConfig.getAdminPasswordHash(), keyServerConfig.getEncryptionKey(), keyServerConfig.getMasterSeed(), keyServerConfig.getInetAddress(), keyServerConfig.getLicenseDirectory());
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public void setREQUESTS(int REQUESTS) {
        this.REQUESTS = REQUESTS;
    }

    @Override
    public int getNumRequests() {
        return REQUESTS;
    }

    @Override
    public String getAdminPasswordHash() {
        return adminPasswordHash;
    }

    public void setAdminPasswordHash(String adminPasswordHash) {
        this.adminPasswordHash = adminPasswordHash;
    }

    @Override
    public String getMasterSeed() {
        return masterSeed;
    }

    public void setMasterSeed(String masterSeed) {
        this.masterSeed = masterSeed;
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    @Override
    public int getPort() {
        return PORT;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @Override
    public String getLicenseDirectory() {
        return licenseDirectory;
    }

    public void setLicenseDirectory(String licenseDirectory) {
        this.licenseDirectory = licenseDirectory;
    }
}
