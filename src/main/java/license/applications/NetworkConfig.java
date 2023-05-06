package license.applications;

import java.net.InetAddress;

/**
 * Network configuration.
 */
public interface NetworkConfig {
    /**
     * Get internet address for client socket opening
     *
     * @return
     */
    InetAddress getInetAddress();

    /**
     * Get port for socket opening
     *
     * @return
     */
    int getPort();

    /**
     * Get encryption key for decrypt messages. (All network messages are encrypted.)
     *
     * @return
     */
    String getEncryptionKey();
}
