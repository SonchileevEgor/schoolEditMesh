package license.applications;

import license.network.KeyServer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Server application with command line interface.
 */
public class KeyServerExecutable implements Runnable {
    LocalhostConfigMaker localhostConfigMaker = new LocalhostConfigMaker();
    String[] args;
    @Option(name = "--help", usage = "print this help", help = true)
    private boolean showHelp = false;
    @Option(name = "-d", usage = "delete saved licenses information (by default disabled)")
    private boolean deleteLicenses = false;
    @Option(name = "-p", metaVar = "<port>", usage = "use specific port for listening")
    private int port = localhostConfigMaker.getPort();
    @Option(name = "-r", metaVar = "<requests>", usage = "maximum length of the queue of incoming connections")
    private int requests = localhostConfigMaker.getNumRequests();
    @Option(name = "-a", metaVar = "<adminPasswordHash>", usage = "MD5 hash of admin password")
    private String adminPasswordHash = localhostConfigMaker.getAdminPasswordHash();
    @Option(name = "-e", metaVar = "<encryptionKey>", usage = "Key for encrypt\\decrypt messages")
    private String encryptionKey = localhostConfigMaker.getEncryptionKey();
    @Option(name = "-h", metaVar = "<hostName>", usage = "specific using host for listening")
    private String specificHostName = null;
    @Option(name = "-s", metaVar = "<seed>", usage = "master seed for generation of keys")
    private String masterSeed = localhostConfigMaker.getMasterSeed();
    @Option(name = "-l", metaVar = "<license directory>", usage = "directory which be used for saving license information ")
    private String licenseDirectory = localhostConfigMaker.getLicenseDirectory();

    private InetAddress inetAddress = localhostConfigMaker.getInetAddress();

    public KeyServerExecutable(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) {
        try {
            KeyServerExecutable keyServerExecutable = new KeyServerExecutable(args);
            keyServerExecutable.run();
        } catch (RuntimeException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        parseArgs();

        KeyServer keyServer = new KeyServer(new CustomKeyServerConfig(port, requests, adminPasswordHash, encryptionKey, masterSeed, inetAddress, licenseDirectory), deleteLicenses);
        keyServer.startServer();
    }

    private void parseArgs() {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println("Error: can't parse command line arguments");
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        try {
            if (specificHostName != null) {
                inetAddress = InetAddress.getByName(specificHostName);
            }
        } catch (UnknownHostException e) {
            System.out.println("Warning: specific host is not allowed.");
            System.exit(1);
        }
        if (showHelp) {
            showHelp = false;
            parser.printUsage(System.out);
            System.exit(0);
        }
        System.out.println("Directory for writing license is " + licenseDirectory);

    }
}
