package license.network;

import com.google.gson.Gson;
import license.applications.KeyServerConfig;
import license.applications.LocalhostConfigMaker;
import license.encryption.Encryption;
import license.encryption.WrongEncryptedKeyException;
import license.license.LicenseContainer;
import license.license.LicenseInfo;
import license.pcidentifier.PcIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for run license key server.
 * <p>
 * Класс для создания сервера по раздаче лицензий и создания лицензионных ключей.
 * Основа функционала создания лицензионных ключей заключена в LicenseContainer.
 * Пользование сервером: создаем объект класса KeyServer с нужной сетевой конфигурацией и вызываем метод {@link #startServer()}.
 * <p>
 * <p>
 * Все сообщения передаются в текстовом режиме.
 * <p>
 * Шифрование:
 * Все исходящие сообщения шифруются. От входящих сообщений ожидается, что они зашифрованы.
 * Ожидается, что клиент знает ключ и алгоритм для шифровки и расшифровки сообщений
 * (используется алгоритм симметричного шифрования из {@link Encryption} )
 * <p>
 * Протокол сообщений:
 * <p>
 * Если сообщения от клиента не могут быть расшифрованы, то посылается "Wrong encryption key".
 * Если запрос от клиента составлен неверно, то посылается "Wrong request".
 * Информация о лицензии передается в json формате (Gson).
 * <p>
 * Для пользователя: пользователь посылает запрос "get license LICENSE_KEY MAC-address", где
 * LICENSE_KEY - его лицензионный ключ, MAC-address - его MAC адрес.
 * <p>
 * Если лицензионный ключ верен и не использовался ранее, то для него запоминается MAC-адрес и
 * сервер посылает "Key is valid" и следующим ответом шлет информацию о лицензии.
 * <p>
 * Если лицензионный ключ верен, но использовался ранее, идет проверка MAC-адреса.
 * Если MAC-адрес совпадает с запомненным, то сервер посылает "Key is valid" и следующим ответом шлет информацию о лицензии.
 * <p>
 * Если ключ неверен или верен, но уже используется и не совпадает MAC-адрес, то посылается "Wrong key".
 * <p>
 * Для админа: админ посылает запрос "generate key START_DATE FINISH_DATE ADMIN_PASSWORD", где
 * START_DATE - дата начала лицензии в формате UNIX-время в миллисекундах,
 * FINISH_DATE - дата конца лицензии в формате UNIX-время в миллисекундах,
 * ADMIN_PASSWORD - пароль администратора
 * <p>
 * Сервер в ответ на запрос посылает новый сгенерированный лицензионный ключ.
 * Если пароль админа не является верным, то посылается "Wrong password".
 */
public class KeyServer {
    /**
     * Print or not debug output.
     */
    private final boolean DEBUG = false;
    /**
     * Queue of client connections.
     */
    private final LinkedBlockingQueue<Socket> connectionsQueue;
    private final LicenseContainer licenseContainer;
    /**
     * Object for calc md5 hash.
     */
    final private MessageDigest md5Calculator;
    /**
     * Timeout for force checking of connection queue.
     */
    private int connectionsQueueForceCheckTimeout = 10000;
    /**
     * Timeout for one client session.
     */
    private int answerTimeout = 30000;
    /**
     * Thread for listening clients and putting them to {@link #connectionsQueue}
     */
    private Thread connectionListenerThread;
    /**
     * Thread pool for getting connections and answering.
     */
    private ExecutorService threadPool;
    /**
     * Configurations for server.
     */
    private KeyServerConfig keyServerConfig;

    public KeyServer() {
        this(false);
    }

    public KeyServer(boolean deleteHistory) {
        this(new LocalhostConfigMaker(), deleteHistory);
    }

    /**
     * Create KeyServer object.
     *
     * @param keyServerConfig server configurations
     * @param deleteHistory   if true deletes key history file and starts as new clear server,
     *                        if false trying load from key history file
     */
    public KeyServer(KeyServerConfig keyServerConfig, boolean deleteHistory) {
        this.keyServerConfig = keyServerConfig;
        connectionsQueue = new LinkedBlockingQueue(keyServerConfig.getNumRequests());
        licenseContainer = new LicenseContainer(deleteHistory, keyServerConfig.getLicenseDirectory(), keyServerConfig.getMasterSeed());
        try {
            md5Calculator = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Starts server work.
     */
    public void startServer() {
        connectionListenerThread = new Thread(new ConnectionListener());
        connectionListenerThread.start();
        threadPool = Executors.newFixedThreadPool(10);
        System.out.println("Starting server:");
        System.out.println("Host name: " + keyServerConfig.getInetAddress().getHostName());
        System.out.println("Host address: " + keyServerConfig.getInetAddress().getHostAddress());
        System.out.println("Canonical host name: " + keyServerConfig.getInetAddress().getCanonicalHostName());
        System.out.println("Host port: " + keyServerConfig.getPort());
    }

    /**
     * Get number of client connections in queue.
     *
     * @return
     */
    public synchronized int getNumConnections() {
        synchronized (connectionsQueue) {
            return connectionsQueue.size();
        }
    }

    /**
     * Class for answering for client. Closes socket after performing.
     */
    class Answerer implements Runnable {
        Socket clientSocket;

        public Answerer(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void sendLicense(PrintWriter printWriter, LicenseInfo licenseInfo) {
            printWriter.println(Encryption.encryptTextMode("Key is valid", keyServerConfig.getEncryptionKey().getBytes()));
            Gson gson = new Gson();
            String licenseInfoJson = gson.toJson(licenseInfo);
            String encryptedLicenseInfoString = Encryption.encryptTextMode(licenseInfoJson, keyServerConfig.getEncryptionKey().getBytes());
            printWriter.println(encryptedLicenseInfoString);
        }

        @Override
        public void run() {
            synchronized (licenseContainer) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String inputLine;
                    try {
                        String line = bufferedReader.readLine();
                        inputLine = Encryption.decryptTextMode(line, keyServerConfig.getEncryptionKey().getBytes());
                    } catch (WrongEncryptedKeyException e) {
                        System.out.println("Wrong encryption key");
                        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        printWriter.println(Encryption.encryptTextMode("Wrong encryption key", keyServerConfig.getEncryptionKey().getBytes()));
                        return;
                    }
                    System.out.println("Received " + inputLine);
                    PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                    if (inputLine.startsWith("get license")) {
                        String[] words = inputLine.split(" ");
                        if (words.length != 4) {
                            printWriter.println(Encryption.encryptTextMode("Wrong request", keyServerConfig.getEncryptionKey().getBytes()));
                            return;
                        }
                        String key = words[2];
                        String mac = words[3];
                        if (!PcIdentifier.checkMACAddressFormat(mac)) {
                            printWriter.println(Encryption.encryptTextMode("Wrong request", keyServerConfig.getEncryptionKey().getBytes()));
                            return;
                        }
                        LicenseInfo license = licenseContainer.getLicense(key, mac);
                        if (license == null) {
                            LicenseInfo usedLicense = licenseContainer.getUsedLicense(key, mac);
                            if (usedLicense == null) {
                                printWriter.println(Encryption.encryptTextMode("Wrong key", keyServerConfig.getEncryptionKey().getBytes()));
                            } else {
                                sendLicense(printWriter, usedLicense);
                            }
                        } else {
                            sendLicense(printWriter, license);
                        }
                        return;
                    }
                    if (inputLine.startsWith("generate key")) {
                        String[] words = inputLine.split(" ");
                        if (words.length != 5) {
                            printWriter.println(Encryption.encryptTextMode("Wrong request", keyServerConfig.getEncryptionKey().getBytes()));
                            return;
                        }
                        Date dateStart = new Date(Long.parseLong(words[2]));
                        Date dateFinish = new Date(Long.parseLong(words[3]));
                        String password = words[4];
                        byte[] hash;
                        synchronized (md5Calculator) {
                            hash = md5Calculator.digest(password.getBytes());
                        }
                        String hashHexString = new BigInteger(1, hash).toString(16);
                        String hashString = hashHexString.toUpperCase();
                        if (!hashString.equals(keyServerConfig.getAdminPasswordHash())) {
                            printWriter.println(Encryption.encryptTextMode("Wrong password", keyServerConfig.getEncryptionKey().getBytes()));
                        } else {
                            String key = licenseContainer.generateNewKey(dateStart, dateFinish);
                            printWriter.println(Encryption.encryptTextMode(key, keyServerConfig.getEncryptionKey().getBytes()));
                        }
                        return;
                    }
                    printWriter.println(Encryption.encryptTextMode("Wrong request", keyServerConfig.getEncryptionKey().getBytes()));
                } catch (SocketTimeoutException e) {
                    // May be I should make some magic there, but not. I just doing nothing.
                } catch (IOException e) {
                    // Socket input/output error.
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        // Nothing. Socket already closed.
                    }
                }
            }
        }
    }

    /**
     * Thread for listening new client connections. Adds new client sockets to {@link #connectionsQueue}
     */
    class ConnectionListener implements Runnable {
        @Override
        public void run() {
            InetAddress inetAddress;
            inetAddress = keyServerConfig.getInetAddress();
            if (DEBUG) {
                System.out.println(MessageFormat.format("Try to create server on {0} (address is {1})",
                        inetAddress.getHostName(), inetAddress.getHostAddress()));
            }

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(keyServerConfig.getPort(), keyServerConfig.getNumRequests(), inetAddress);
                while (true) {
                    try {
                        if (DEBUG) {
                            System.out.println("Wait for client");
                        }
                        Socket clientSocket = serverSocket.accept();
                        if (DEBUG) {
                            System.out.println("Client found");
                        }
                        clientSocket.setSoTimeout(answerTimeout);
                        threadPool.execute(new Answerer(clientSocket));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            } catch (IOException e) {
                System.out.println(MessageFormat.format("Could not listen on port: {0}", keyServerConfig.getPort()));
                e.printStackTrace();
                System.exit(1);
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        // Socket is already closed.
                    }
                }
            }

        }
    }
}

