package license.license;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.*;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;

enum LicenseOperation {
    GENERATE,
    MARK
}

/**
 * Класс для хранения и аудита использованных ключей, получения лицензий.
 * <p>
 * Каждую критическую операцию (генерация нового ключа, пометка лицензии как использованной) пишет в файл
 * для сохранения состояния. Если в этот момент он не сможет доступиться к файлу, то бросает RuntimeException.
 */
public class LicenseContainer {
    /**
     * Filename for file, where LicenseContainer saves his state.
     */
    private static final String defaultLicensesFileName = "Licenses.csv";
    final private static String defaultMasterSeed = "Some default seed";
    /**
     * Seed for key generations. The same seed generates the same chain of keys.
     */
    final private String masterSeed;
    /**
     * File where LicenseContainer saves his state.
     */
    private File licensesFile;
    /**
     * Container for unused licenses
     */
    private HashMap<String, LicenseInfo> licenseContainer;
    /**
     * Container for used licenses
     */
    private HashMap<String, LicenseInfo> usedLicenseContainer;
    /**
     * Last generated key (using for next key generation)
     */
    private String lastKey;

    public LicenseContainer() {
        this(false, defaultMasterSeed);
    }

    public LicenseContainer(boolean deleteHistory) {
        this(deleteHistory, new File(defaultLicensesFileName), defaultMasterSeed);
    }

    public LicenseContainer(boolean deleteHistory, String licensesFileDirectory, String masterSeed) {
        this(deleteHistory, new File(licensesFileDirectory, defaultLicensesFileName), masterSeed);
    }

    public LicenseContainer(boolean deleteHistory, String licensesFileDirectory) {
        this(deleteHistory, new File(licensesFileDirectory, defaultLicensesFileName), defaultMasterSeed);
    }

    public LicenseContainer(boolean deleteHistory, File licensesFile) {
        this(deleteHistory, licensesFile, defaultMasterSeed);
    }

    /**
     * Creates a new LicenseContainer. If file exists trying to load state form it, otherwise creates new file.
     *
     * @param deleteHistory if true forced delete file if it exists, if false trying to load state from file.
     * @param licensesFile  path for searching file with saved state.
     * @param masterSeed Seed for key generations. The same seed generates the same chain of keys.
     */
    public LicenseContainer(boolean deleteHistory, File licensesFile, String masterSeed) {
        this.masterSeed = masterSeed;
        this.licensesFile = licensesFile;
        licenseContainer = new HashMap<>();
        usedLicenseContainer = new HashMap<>();
        // Restore state from file if it exist.
        try {
            if (deleteHistory) {
                if (licensesFile.exists()) {
                    if (!licensesFile.delete()) {
                        panic(new IOException("File permission problems are caught here"));
                    }
                }
                throw new FileNotFoundException();
            } else {
                try (CSVReader csvReader = new CSVReader(new FileReader(licensesFile))){
                    String[] nextLine;
                    while ((nextLine = csvReader.readNext()) != null) {
                        if (nextLine.length != 5) {
                            throw new IOException("Error: Can't restore form file. Wrong number of arguments in csv file");
                        }
                        String key = nextLine[0];
                        Date startDate = new Date(Long.parseLong(nextLine[1]));
                        Date finishDate = new Date(Long.parseLong(nextLine[2]));
                        String mac = nextLine[3];
                        LicenseOperation licenseOperation = LicenseOperation.valueOf(nextLine[4]);
                        LicenseInfo licenseInfo = new LicenseInfo(key, startDate, finishDate, mac);
                        switch (licenseOperation) {
                            case GENERATE:
                                licenseContainer.put(licenseInfo.getKey(), licenseInfo);
                                lastKey = key;
                                break;
                            case MARK:
                                if (getLicense(key, mac, true) == null) {
                                    throw new IOException("Error: Can't restore form file. File has wrong transactions");
                                }
                        }
                    }
                } catch (IOException e) {
                    panic(e);
                }
            }
        } catch (FileNotFoundException e) { // Create new state file
            lastKey = SecureKey.getFirstKey(masterSeed.getBytes());
            try {
                if (!licensesFile.createNewFile()) {
                    panic(new IOException("Can't create file for saving history"));
                }
            } catch (IOException fe) {
                panic(fe);
            }
        }
    }

    /**
     * What to do, if can't read\write state from state file
     *
     * @param e exception for panic
     */
    private void panic(Exception e) {
        throw new RuntimeException(e);
    }

    /**
     * Gets last generated key.
     *
     * @return last generated key.
     */
    public String getLastGeneratedKey() {
        return lastKey;
    }

    /**
     * Write operation in state file. All critical(GENERATE, MARK) operations should be written in file.
     *
     * @param licenseInfo      information about operation which used in operation.
     * @param licenseOperation type of operation
     */
    private void writeLicenseOperation(LicenseInfo licenseInfo, LicenseOperation licenseOperation) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(licensesFile, true))){
            // feed in your array (or convert your data to an array)
            String[] entries = MessageFormat.format("{0}#{1,number,#}#{2,number,#}#{3}#{4}",
                    licenseInfo.getKey(),
                    licenseInfo.getStartDate().getTime(),
                    licenseInfo.getFinishDate().getTime(),
                    licenseInfo.getMacAddress(),
                    licenseOperation.toString()).split("#");
            writer.writeNext(entries);
            writer.close();
        } catch (IOException e) {
            panic(e);
        }
    }

    /**
     * Generate a new key and add information about new license in database as a new license.
     *
     * @param startDate  date of starting license
     * @param finishDate date of finishing license
     * @return new generated secure key.
     */
    public String generateNewKey(Date startDate, Date finishDate) {
        String newKey = SecureKey.generateSecureKey(true, (masterSeed + getLastGeneratedKey()).getBytes());
        lastKey = newKey;
        LicenseInfo licenseInfo = new LicenseInfo(newKey, startDate, finishDate);
        licenseContainer.put(licenseInfo.getKey(), licenseInfo);
        writeLicenseOperation(licenseInfo, LicenseOperation.GENERATE);
        return newKey;
    }


    /**
     * Check key by his internal information (format, checking symbols)
     *
     * @param key key for checking
     * @return 0 if key is ok, -1 if key has wrong format, -2 if key has wrong checking symbols
     */
    private int checkKey(String key) {
        if (!SecureKey.checkKeyFormat(key)) {
            return -1; // Wrong format
        }
        if (!SecureKey.validate(key)) {
            return -2; // Wrong key checking symbols
        }
        return 0;
    }

    /**
     * Mark license as used.
     * Key should be in licenseContainer, otherwise RuntimeException will be thrown.
     *
     * @param key         key of searching license.
     * @param restoreMode if true operation doesn't write this operation in state file. Needed for restore state from file.
     */
    private void markAsUsed(String key, boolean restoreMode) {
        if (!licenseContainer.containsKey(key)) {
            throw new RuntimeException("Error: trying to mark license witch can't be marked");
        }
        LicenseInfo licenseInfo = licenseContainer.get(key);
        usedLicenseContainer.put(licenseInfo.getKey(), licenseInfo);
        licenseContainer.remove(key);
        if (!restoreMode) {
            writeLicenseOperation(licenseInfo, LicenseOperation.MARK);
        }
    }

    /**
     * Mark license as used with saving operation in state file.
     *
     * @param key key of searching license.
     * @see #markAsUsed(String, boolean)
     */
    private void markAsUsed(String key) {
        markAsUsed(key, false);
    }

    /**
     * Get license with specified key and mark it as used (saves MAC address in this license).
     * Search provides only for new generated license which wasn't in use.
     * Returns null if can't find license with specified key.
     *
     * @param key key of license
     * @param mac MAC address
     * @param restoreMode if true operation doesn't write this operation in state file. Needed for restore state from file.
     * @return license with specified key and MAC address or null.
     */
    private LicenseInfo getLicense(String key, String mac, boolean restoreMode) {
        int err = checkKey(key);
        if (err < 0) {
            return null;
        }
        if (!licenseContainer.containsKey(key)) {
            return null;
        }
        LicenseInfo licenseInfo = licenseContainer.get(key);
        licenseInfo.setMacAddress(mac);
        markAsUsed(key, restoreMode);
        return licenseInfo;
    }

    /**
     * Get license with specified key and mark it as used (saves MAC address in this license).
     * Search provides only for new generated license which wasn't in use.
     * Returns null if can't find license with specified key.
     *
     * @param key key of license
     * @param mac MAC address
     * @return license with specified key and MAC address or null.
     */
    public LicenseInfo getLicense(String key, String mac) {
        return getLicense(key, mac, false);
    }

    /**
     * Get license with specified key and MAC address.
     * Search provides only for license which was in use.
     * Returns null if can't find license with specified key.
     * Returns null if license with specified key has other MAC address then specified.
     *
     * @param key key of license
     * @param mac MAC address
     * @return license with specified key and MAC address or null.
     */
    public LicenseInfo getUsedLicense(String key, String mac) {
        int err = checkKey(key);
        if (err < 0) {
            return null;
        }
        if (!usedLicenseContainer.containsKey(key)) {
            return null;
        }
        LicenseInfo licenseInfo = usedLicenseContainer.get(key);
        if (licenseInfo.getMacAddress().equals(mac)) {
            return licenseInfo;
        } else {
            return null;
        }
    }

}
