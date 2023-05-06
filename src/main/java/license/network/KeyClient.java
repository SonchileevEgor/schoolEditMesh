package license.network;


import license.applications.LocalhostConfigMaker;
import license.encryption.Encryption;
import license.encryption.WrongEncryptedKeyException;
import license.license.LicenseInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Stress test for server.
 */
@Deprecated
public class KeyClient {
    public static void main(String[] args) {
        try {
            new ClientAdminRunnableTest().run();
            for (int i = 0; i < 200; i++) {
//                Thread thread = new Thread(new ClientUserRunnableTest());
                Thread thread = new Thread(new ClientAdminRunnableTest());
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createClient() {

    }
}

class ClientAdminRunnableTest implements Runnable {
    LocalhostConfigMaker localhostConfigMaker = new LocalhostConfigMaker();
    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;

    private String encriptionKey = localhostConfigMaker.getEncryptionKey();

    @Override
    public void run() {
        try {
            socket = new Socket(localhostConfigMaker.getInetAddress(), localhostConfigMaker.getPort());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            Date startDate = new Date();
            Date finishDate = new Date();
            finishDate.setTime(finishDate.getTime() + 31536000000L);
            String sendMessage = MessageFormat.format("generate key {0,number,#} {1,number,#} {2}", startDate.getTime(), finishDate.getTime(), "It_is_the_very_security_password");
            printWriter.println(Encryption.encryptTextMode(sendMessage, encriptionKey.getBytes()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String answer;
            try {
                answer = Encryption.decryptTextMode(bufferedReader.readLine(), encriptionKey.getBytes());
            } catch (WrongEncryptedKeyException e) {
                System.out.println("Wrong encryption key");
                return;
            }
            System.out.println("I have a key:" + answer);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientUserRunnableTest implements Runnable {
    ClientUser clientUser;
    LocalhostConfigMaker localhostConfigMaker = new LocalhostConfigMaker();

    public ClientUserRunnableTest() {
        clientUser = new ClientUser(localhostConfigMaker);
    }

    @Override
    public void run() {
        String key = "E5RL9-VJW3-MN36-HKH3C";
        try {
            LicenseInfo licenseInfo = clientUser.getLicenseFromServer(key);
            System.out.println(licenseInfo);
        } catch (WrongLicenseKeyException e) {
            System.out.println("Wrong key");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}