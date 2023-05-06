package license.network;

import license.applications.NetworkConfig;
import license.encryption.Encryption;
import license.encryption.WrongEncryptedKeyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Class for sending information to license key server for admin users.
 */
public class ClientAdmin {
    private NetworkConfig networkConfig;
    private String adminPassword;

    /**
     * Create ClientAdmin  using given network configurations and admin password.
     *
     * @param networkConfig network configuration.
     * @param adminPassword password for getting admin rights.
     */
    public ClientAdmin(NetworkConfig networkConfig, String adminPassword) {
        this.networkConfig = networkConfig;
        this.adminPassword = adminPassword;
    }

    /**
     * Send command to server to generate new license key and get it.
     * @param startDate start date for new license which will use new generated key.
     * @param finishDate finish date for new license which will use new generated key.
     * @return new license key which was generated on server.
     * @throws WrongEncryptedKeyException if can't decrypt message
     * @throws ConnectionFailedException if any connection error occurs
     * @throws WrongAdminPasswordException if admin password is wrong for this server.
     */
    public String getNewLicenseKey(Date startDate, Date finishDate) throws WrongEncryptedKeyException, ConnectionFailedException, WrongAdminPasswordException {
        Socket socket = null;
        try {
            socket = new Socket(networkConfig.getInetAddress(), networkConfig.getPort());
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            String sendMessage = MessageFormat.format("generate key {0,number,#} {1,number,#} {2}", startDate.getTime(), finishDate.getTime(), adminPassword);
            printWriter.println(Encryption.encryptTextMode(sendMessage, networkConfig.getEncryptionKey().getBytes()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String answer = Encryption.decryptTextMode(bufferedReader.readLine(), networkConfig.getEncryptionKey().getBytes());
            if (answer.startsWith("Wrong request")) {
                throw new ConnectionFailedException();
            }
            if (answer.startsWith("Wrong password")) {
                throw new WrongAdminPasswordException();
            }
            if (answer.startsWith("Wrong encryption key")) {
                throw new WrongEncryptedKeyException();
            }
            return answer;
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