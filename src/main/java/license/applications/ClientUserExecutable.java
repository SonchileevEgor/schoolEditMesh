package license.applications;

import license.license.LicenseInfo;
import license.network.ClientUser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Application for users with command line interface.
 */
public class ClientUserExecutable implements Runnable {
    LocalhostConfigMaker localhostConfigMaker = new LocalhostConfigMaker();
    ClientUser clientUser;
    @Option(name = "--help", usage = "print this help", help = true)
    private boolean showHelp = false;
    @Option(name = "-p", metaVar = "<port>", usage = "use specific port for connection")
    private int port = localhostConfigMaker.getPort();
    @Option(name = "-e", metaVar = "<encryptionKey>", usage = "Key for encrypt\\decrypt messages")
    private String encryptionKey = localhostConfigMaker.getEncryptionKey();
    @Option(name = "-h", metaVar = "<hostName>", usage = "specific using host for listening")
    private String specificHostName = null;
    @Option(name = "-k", metaVar = "<licenseKey>", usage = "license key for getting license file")
    private String licenseKey;

    public ClientUserExecutable(String[] args) {
        try {
            parseArgs(args);
            CustomNetworkConfig customNetworkConfig = new CustomNetworkConfig(port, InetAddress.getByName(specificHostName), encryptionKey);
            setParams(customNetworkConfig, licenseKey);
        } catch (UnknownHostException e) {
            System.out.println("Warning: specific host is not allowed.");
            System.exit(1);
        }
    }

    public ClientUserExecutable(NetworkConfig networkConfig, String licenseKey) {
        setParams(networkConfig, licenseKey);
    }

    public static void main(String[] args) throws UnknownHostException {
        ClientUserExecutable clientUserExecutable = new ClientUserExecutable(args);
        clientUserExecutable.run();

    }

    private void parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println("Error: can't parse command line arguments");
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        if (showHelp) {
            showHelp = false;
            parser.printUsage(System.out);
            System.exit(0);
        }
        if (licenseKey == null) {
            System.out.println("Error: Need to set license key");
            System.exit(1);
        }
    }

    private void setParams(NetworkConfig networkConfig, String licenseKey) {
        clientUser = new ClientUser(networkConfig);
        this.licenseKey = licenseKey;
    }

    @Override
    public void run() {
        try {
            LicenseInfo licenseInfo = clientUser.getLicenseFromServer(licenseKey);
            System.out.println(licenseInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
