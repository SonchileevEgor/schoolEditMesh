package license.applications;

import license.encryption.WrongEncryptedKeyException;
import license.network.ClientAdmin;
import license.network.ConnectionFailedException;
import license.network.WrongAdminPasswordException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Application for admin users with command line interface.
 */
public class ClientAdminExecutable implements Runnable {
    LocalhostConfigMaker localhostConfigMaker = new LocalhostConfigMaker();
    ClientAdmin clientAdmin;
    InetAddress inetAddress;
    @Option(name = "--help", usage = "print this help", help = true)
    private boolean showHelp = false;
    @Option(name = "-p", metaVar = "<port>", usage = "use specific port for connection")
    private int port = localhostConfigMaker.getPort();
    @Option(name = "-e", metaVar = "<encryptionKey>", usage = "Key for encrypt\\decrypt messages")
    private String encryptionKey = localhostConfigMaker.getEncryptionKey();
    @Option(name = "-h", metaVar = "<hostName>", usage = "specific using host for listening")
    private String specificHostName = null;
    @Option(name = "-a", metaVar = "<adminPassword>", usage = "Admin password for getting new keys")
    private String adminPassword = "It_is_the_very_security_password";
    private Date startDate;
    private Date finishDate;

    public ClientAdminExecutable(String[] args) {
        startDate = new Date();
        finishDate = new Date();
        finishDate.setTime(finishDate.getTime() + 31536000000L); // Plus one year.
        parseArgs(args);
        clientAdmin = new ClientAdmin(new CustomNetworkConfig(port, inetAddress, encryptionKey), adminPassword);

    }

    public static void main(String[] args) {
        ClientAdminExecutable clientAdminExecutable = new ClientAdminExecutable(args);
        clientAdminExecutable.run();
    }

    @Option(name = "-s", metaVar = "<start date>", usage = "Start date for new license in \"dd.mm.yyyy\" format. By default current date")
    public void setStartDate(String startDate) {
        setDate(startDate, DATE_VAR.START_DATE);
    }

    @Option(name = "-f", metaVar = "<finish date>", usage = "Finish date for new license in \"dd.mm.yyyy\" format. By default current date + one year")
    public void setFinishDate(String finishDate) {
        setDate(finishDate, DATE_VAR.FINISH_DATE);
    }

    private void setDate(String stringDate, DATE_VAR dateVar) {
        try {
            if (!stringDate.matches("(\\d{2}.\\d{2}.\\d{4})")) {
                throw new IllegalArgumentException();
            }
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            format.setLenient(false);
            Date date = format.parse(stringDate);
            switch (dateVar) {
                case START_DATE:
                    this.startDate = date;
                    break;
                case FINISH_DATE:
                    this.finishDate = date;
                    break;
            }
        } catch (ParseException | IllegalArgumentException | NullPointerException e) {
            System.out.println("Error: wrong finish date");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            String newKey = clientAdmin.getNewLicenseKey(startDate, finishDate);
            System.out.println("New key: " + newKey);
            File file = new File("Log.txt");
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Log.txt", true)));
            out.println(MessageFormat.format("Key: \"{0}\" startDate: \"{1}\" finishDate: \"{2}\"", newKey, startDate, finishDate));
            out.close();
        } catch (WrongEncryptedKeyException | ConnectionFailedException | WrongAdminPasswordException | IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    private void parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            if (showHelp) {
                showHelp = false;
                parser.printUsage(System.out);
                System.exit(0);
            }
            if (specificHostName == null) {
                inetAddress = localhostConfigMaker.getInetAddress();
            } else {
                inetAddress = InetAddress.getByName(specificHostName);
            }
        } catch (CmdLineException e) {
            System.out.println("Error: can't parse command line arguments");
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        } catch (UnknownHostException e) {
            System.out.println("Warning: specific host is not allowed.");
            System.exit(1);
        }
    }

    private enum DATE_VAR {
        START_DATE,
        FINISH_DATE
    }
}
