package license.applications;

import java.net.InetAddress;

/**
 * NetworkConfig realization with customization of all parameters.
 */
public class CustomNetworkConfig implements NetworkConfig {
    private int port;
    private InetAddress inetAddress;
    private String encryptionKey;

    public CustomNetworkConfig(int port, InetAddress inetAddress, String encryptionKey) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.encryptionKey = encryptionKey;
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
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}
