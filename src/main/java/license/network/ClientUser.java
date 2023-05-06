package license.network;

import com.google.gson.Gson;
import license.applications.NetworkConfig;
import license.encryption.Encryption;
import license.encryption.WrongEncryptedKeyException;
import license.license.LicenseInfo;
import license.pcidentifier.NoMACAddressException;
import license.pcidentifier.PcIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.MessageFormat;

/**
 * Gets license from server.
 */
public class ClientUser {
    private NetworkConfig networkConfig;

    /**
     * Create ClientUser for getting license using given network configurations.
     *
     * @param networkConfig network configuration
     */
    public ClientUser(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    /**
     * Make request to server. Sends license key (and MAC address) and trying to receive license.
     *
     * @param key license key.
     * @return license from server.
     * @throws NoMACAddressException      if can't get any MAC address
     * @throws WrongEncryptedKeyException if can't decrypt message
     * @throws WrongLicenseKeyException   if key is not valid on server
     * @throws ConnectionFailedException  if any connection error occurs
     * @see license.license.SecureKey
     */
    public LicenseInfo getLicenseFromServer(String key) throws NoMACAddressException, WrongEncryptedKeyException, WrongLicenseKeyException, ConnectionFailedException {
        Socket socket = null;
        try {
            PrintWriter printWriter;
            BufferedReader bufferedReader;
            byte[] encryptionKey = networkConfig.getEncryptionKey().getBytes();
            String mac = PcIdentifier.getMACAddresses().get(0);
            socket = new Socket(networkConfig.getInetAddress(), networkConfig.getPort());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(Encryption.encryptTextMode(MessageFormat.format("get license {0} {1}", key, mac), encryptionKey));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String answer = Encryption.decryptTextMode(bufferedReader.readLine(), encryptionKey);
            if (answer.startsWith("Wrong request")) {
                throw new ConnectionFailedException();
            }
            if (answer.startsWith("Wrong key")) {
                throw new WrongLicenseKeyException();
            }
            if (answer.startsWith("Key is valid")) {
                String licenseInfoHexEncrypted = bufferedReader.readLine();
                String licenseInfoDecryptedJson = Encryption.decryptTextMode(licenseInfoHexEncrypted, encryptionKey);
                Gson gson = new Gson();
                return gson.fromJson(licenseInfoDecryptedJson, LicenseInfo.class);
            }
            if (answer.startsWith("Wrong encryption key")) {
                throw new WrongEncryptedKeyException();
            }
            // Unsupported answer.
            return null;
        } catch (IOException e) {
            throw new ConnectionFailedException();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Socket can't be closed. Nothing to do there.
                }
            }
        }
    }
}