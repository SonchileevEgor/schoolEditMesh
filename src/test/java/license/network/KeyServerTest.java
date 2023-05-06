package license.network;

import license.applications.CustomKeyServerConfig;
import license.applications.KeyServerConfig;
import license.applications.LocalhostConfigMaker;
import license.applications.NetworkConfig;
import license.encryption.WrongEncryptedKeyException;
import license.license.LicenseInfo;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

//import static org.junit.

/**
 * Created by MAXbrainRUS on 18-Nov-15.
 */
@Disabled
public class KeyServerTest {
    private static final int numTryClientsLight = 1;
    private static final int numTryClientsHard = 500;
    private static final int millsPerClient = 10;
    private static final int millsStart = 1500;
    
    @TempDir
    Path folder = FileSystems.getDefault().getPath(config.Config.TMP_DIR);

    private void testServer(int numConnections, KeyServerConfig keyServerConfig) throws Exception {
        KeyServer keyServer = new KeyServer(keyServerConfig, true);
        keyServer.startServer();
        LinkedBlockingQueue<ClientTryConnect> queue = new LinkedBlockingQueue<>();
        final Executor executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < numConnections; i++) {
            ClientTryConnect clientTryConnect = new ClientTryConnect(keyServerConfig);
            queue.put(clientTryConnect);
            executor.execute(clientTryConnect);
        }
        while (!queue.isEmpty()) {
            ClientTryConnect clientTryConnect = queue.peek();
            if (clientTryConnect == null)
                break;
            if (clientTryConnect.isWorkDoneOk()) {
                queue.poll();
            } else {
                Thread.sleep(millsPerClient);
            }
        }

    }

    @Test
    @Timeout(value = numTryClientsLight * millsPerClient + millsStart)
    public void testStressServerLight() throws Exception {
        CustomKeyServerConfig customKeyServerConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
        customKeyServerConfig.setPORT(14081);
        testServer(numTryClientsLight, customKeyServerConfig);
    }


//    @Ignore
//    @Test(timeout = numTryClientsHard * millsPerClient + millsStart)
//    public void testStressServerHard() throws Exception {
//        CustomKeyServerConfig customKeyServerConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
//        customKeyServerConfig.setPORT(14082);
//        testServer(numTryClientsHard, customKeyServerConfig);
//    }


    @Test
    public void testChangeSeed() throws Exception {
        CustomKeyServerConfig customKeyServerConfig1 = new CustomKeyServerConfig(new LocalhostConfigMaker());
        customKeyServerConfig1.setMasterSeed("First seed");
        customKeyServerConfig1.setPORT(14083);

        CustomKeyServerConfig customKeyServerConfig2 = new CustomKeyServerConfig(new LocalhostConfigMaker());
        customKeyServerConfig2.setMasterSeed("Second seed");
        customKeyServerConfig2.setPORT(14084);

        CustomKeyServerConfig customKeyServerConfig3 = new CustomKeyServerConfig(new LocalhostConfigMaker());
        customKeyServerConfig3.setMasterSeed("First seed");
        customKeyServerConfig3.setPORT(14085);

        KeyServer keyServer1 = new KeyServer(customKeyServerConfig1, true);
        keyServer1.startServer();
        KeyServer keyServer2 = new KeyServer(customKeyServerConfig2, true);
        keyServer2.startServer();
        KeyServer keyServer3 = new KeyServer(customKeyServerConfig3, true);
        keyServer3.startServer();

        ClientAdmin clientAdmin1 = new ClientAdmin(customKeyServerConfig1, new LocalhostConfigMaker().getDefaultAdminPassword());
        ClientAdmin clientAdmin2 = new ClientAdmin(customKeyServerConfig2, new LocalhostConfigMaker().getDefaultAdminPassword());
        ClientAdmin clientAdmin3 = new ClientAdmin(customKeyServerConfig3, new LocalhostConfigMaker().getDefaultAdminPassword());

        Date fakeDate = new Date();
        assertNotEquals(clientAdmin1.getNewLicenseKey(fakeDate, fakeDate), clientAdmin2.getNewLicenseKey(fakeDate, fakeDate));
        clientAdmin3.getNewLicenseKey(fakeDate, fakeDate);
        assertEquals(clientAdmin1.getNewLicenseKey(fakeDate, fakeDate), clientAdmin3.getNewLicenseKey(fakeDate, fakeDate));

    }

    @Test
    public void testKeyServerWork1() throws Exception {
        CustomKeyServerConfig serverConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig.setPORT(14086);
        String licenseDirectory = folder.toString();
        serverConfig.setLicenseDirectory(licenseDirectory);
        KeyServer keyServer = new KeyServer(serverConfig, true);
        keyServer.startServer();
        ClientAdmin clientAdmin = new ClientAdmin(serverConfig, new LocalhostConfigMaker().getDefaultAdminPassword());
        String key1 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        String key2 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        String key3 = clientAdmin.getNewLicenseKey(new Date(), new Date());

        ClientUser clientUser = new ClientUser(serverConfig);
        LicenseInfo licenseInfo;
        for (int i = 0; i < 3; i++) {
            licenseInfo = clientUser.getLicenseFromServer(key3);
            assertNotEquals(licenseInfo, null);
            licenseInfo = clientUser.getLicenseFromServer(key1);
            assertNotEquals(licenseInfo, null);
            licenseInfo = clientUser.getLicenseFromServer(key2);
            assertNotEquals(licenseInfo, null);
        }
    }

    @Test
    public void testLoadState() throws Exception {
        CustomKeyServerConfig serverConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig.setPORT(14087);
        String licenseDirectory = folder.toString();
        serverConfig.setLicenseDirectory(licenseDirectory);
        KeyServer keyServer = new KeyServer(serverConfig, true);
        keyServer.startServer();
        ClientAdmin clientAdmin = new ClientAdmin(serverConfig, new LocalhostConfigMaker().getDefaultAdminPassword());
        String key1 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        String key2 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        String key3 = clientAdmin.getNewLicenseKey(new Date(), new Date());

        CustomKeyServerConfig serverConfig2 = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig2.setLicenseDirectory(licenseDirectory);
        serverConfig2.setPORT(14088);
        KeyServer keyServer2 = new KeyServer(serverConfig2, false);
        keyServer2.startServer();

        ClientUser clientUser = new ClientUser(serverConfig2);
        LicenseInfo licenseInfo;
        for (int i = 0; i < 3; i++) {
            licenseInfo = clientUser.getLicenseFromServer(key3);
            assertNotEquals(licenseInfo, null);
            licenseInfo = clientUser.getLicenseFromServer(key1);
            assertNotEquals(licenseInfo, null);
            licenseInfo = clientUser.getLicenseFromServer(key2);
            assertNotEquals(licenseInfo, null);
        }


    }

    @Test
    public void testLoadState2() throws Exception {
        CustomKeyServerConfig serverConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig.setPORT(14089);
        String licenseDirectory = folder.toString();
        serverConfig.setLicenseDirectory(licenseDirectory);
        KeyServer keyServer = new KeyServer(serverConfig, true);
        keyServer.startServer();
        ClientAdmin clientAdmin = new ClientAdmin(serverConfig, new LocalhostConfigMaker().getDefaultAdminPassword());
        String key1 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        String key2 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        String key3 = clientAdmin.getNewLicenseKey(new Date(), new Date());

        ClientUser clientUser = new ClientUser(serverConfig);
        LicenseInfo licenseInfo;
        for (int i = 0; i < 1; i++) {
            licenseInfo = clientUser.getLicenseFromServer(key3);
            assertNotEquals(licenseInfo, null);
            licenseInfo = clientUser.getLicenseFromServer(key1);
            assertNotEquals(licenseInfo, null);
            licenseInfo = clientUser.getLicenseFromServer(key2);
            assertNotEquals(licenseInfo, null);
        }

        CustomKeyServerConfig serverConfig2 = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig2.setLicenseDirectory(licenseDirectory);
        serverConfig2.setPORT(14090);
        KeyServer keyServer2 = new KeyServer(serverConfig2, false);
        keyServer2.startServer();

        ClientUser clientUser2 = new ClientUser(serverConfig2);
        LicenseInfo licenseInfo2;
        for (int i = 0; i < 3; i++) {
            licenseInfo2 = clientUser2.getLicenseFromServer(key3);
            assertNotEquals(licenseInfo2, null);
            licenseInfo2 = clientUser2.getLicenseFromServer(key1);
            assertNotEquals(licenseInfo2, null);
            licenseInfo2 = clientUser2.getLicenseFromServer(key2);
            assertNotEquals(licenseInfo2, null);
        }


    }

    @Test
    @Timeout(value = 3000)
    public void testBadMessage() throws Exception {
        CustomKeyServerConfig serverConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig.setPORT(14091);
        String licenseDirectory = folder.toString();
        serverConfig.setLicenseDirectory(licenseDirectory);
        KeyServer keyServer = new KeyServer(serverConfig, true);
        keyServer.startServer();
        Socket socket = new Socket(serverConfig.getInetAddress(), serverConfig.getPort());
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        printWriter.println("Some shit");
        ClientAdmin clientAdmin = new ClientAdmin(serverConfig, new LocalhostConfigMaker().getDefaultAdminPassword());
        String key1 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        ClientUser clientUser = new ClientUser(serverConfig);
        LicenseInfo licenseInfo = clientUser.getLicenseFromServer(key1);
        assertNotEquals(licenseInfo, null);
    }

    @Test
    @Timeout(value = 3000)
    public void testBadMessage2() throws Exception {
        CustomKeyServerConfig serverConfig = new CustomKeyServerConfig(new LocalhostConfigMaker());
        serverConfig.setPORT(14092);
        String licenseDirectory = folder.toString();
        serverConfig.setLicenseDirectory(licenseDirectory);
        KeyServer keyServer = new KeyServer(serverConfig, true);
        keyServer.startServer();
        Socket socket = new Socket(serverConfig.getInetAddress(), serverConfig.getPort());
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        printWriter.println("1a2b");
        ClientAdmin clientAdmin = new ClientAdmin(serverConfig, new LocalhostConfigMaker().getDefaultAdminPassword());
        String key1 = clientAdmin.getNewLicenseKey(new Date(), new Date());
        ClientUser clientUser = new ClientUser(serverConfig);
        LicenseInfo licenseInfo = clientUser.getLicenseFromServer(key1);
        assertNotEquals(licenseInfo, null);
    }


}

class ClientTryConnect implements Runnable {
    private final NetworkConfig networkConfig;
    ClientAdmin clientAdmin;
    private volatile boolean workDoneOk = false;

    public ClientTryConnect(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
        clientAdmin = new ClientAdmin(networkConfig, "It_is_the_very_security_password");
    }

    @Override
    public void run() {
        while (!workDoneOk) {
            try {
                String key = clientAdmin.getNewLicenseKey(new Date(), new Date());
                workDoneOk = true;
                return;
            } catch (WrongEncryptedKeyException | WrongAdminPasswordException e) {
                return;
            } catch (ConnectionFailedException e) {
                System.out.println("reconnect");
                continue;
            }
        }
    }

    public synchronized boolean isWorkDoneOk() {
        return workDoneOk;
    }
}