package license.license;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import license.encryption.Encryption;
import license.encryption.WrongEncryptedKeyException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Class for containing license information
 */
public class LicenseInfo {
    private static String licenseToFileEncryptionKey = "License to file encryption password";

    /**
     * Version number. Old versions have less number.
     */
    private int version = 0;
    /**
     * License identifier.
     * Has format
     */

    /**
     * License key. Has special format {@link SecureKey}
     */
    private String key = null;
    /**
     * Date when license starts
     */
    private Date startDate = null;
    /**
     * Date when license finish his life.
     */
    private Date finishDate = null;
    /**
     * MAC address for DRM protection. Should have format like 3D-F2-C9-A6-B3-4F
     */
    private String macAddress = null;

    public LicenseInfo() {
    }

    public LicenseInfo(String key, Date startDate, Date finishDate) {
        this.key = key;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public LicenseInfo(String key, Date startDate, Date finishDate, String macAddress) {
        this(key, startDate, finishDate);
        this.macAddress = macAddress;
    }

    public static void main(String[] args) {
        // 31536000000 ms = 365 days
        LicenseInfo licenseInfo = new LicenseInfo("KEY", new Date(), new Date(new Date().getTime() + 31536000000L), "MAC-address");
        System.out.println(licenseInfo);
    }

    /**
     * Get gson class for serialization license info.
     *
     * @return gson class for serialization license info.
     */
    private Gson getGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Check that license info is valid for device
     * by current date and MAC-addresses list.
     *
     * @param macAddresses list of mac-addresses of given device
     * @param currentDate  current date
     */
    public void validate(ArrayList<String> macAddresses, Date currentDate)
            throws LicenseValidationException {
        if (!macAddresses.contains(macAddress)) {
            throw new LicenseValidationException("<html>Лицензия не распространяется на данное устройство!<br>Введите новый лицензионный ключ.");
        } else if (currentDate.after(finishDate)) {
            throw new LicenseValidationException("<html>Срок лицензии истёк.<br>Продлите срок лицензии или введите новый лицензионный ключ.");
        } else if (currentDate.before(startDate)) {
            throw new LicenseValidationException("<html>Срок лицензии ещё не начался!<br>Дождитесь начала срока лицензии или введите новый лицензионный ключ.");
        }
    }

    @Override
    public String toString() {
        Gson gson = getGson();
        return gson.toJson(this);
    }

    /**
     * Saves license to file with encryption.
     *
     * @param fileName The name of the file to use as the destination
     * @throws FileNotFoundException
     */
    public void saveToFile(String fileName) throws FileNotFoundException {
      Gson gson = getGson();
      String jsonString = gson.toJson(this);
      String encryptedLicense = Encryption.encryptTextMode(jsonString, licenseToFileEncryptionKey.getBytes());
      try (PrintWriter out = new PrintWriter(fileName)) {
        out.println(encryptedLicense);
      }
    }

    /**
     * Loads license from file with decryption.
     *
     * @param fileName The name of the file to use as the destination
     * @throws FileNotFoundException
     */
    public void loadFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String encryptedLicense = reader.readLine();
            String json = Encryption.decryptTextMode(encryptedLicense, licenseToFileEncryptionKey.getBytes());
            Gson gson = getGson();
            copyData(gson.fromJson(json, this.getClass()));
        } catch (JsonSyntaxException | WrongEncryptedKeyException e) {
            throw new IOException("Error: Broken license file. Can't write license info");
        }
    }

    /**
     * Copy all data from other license
     *
     * @param licenseInfo other license for coping
     */
    public void copyData(LicenseInfo licenseInfo) {
        this.version = licenseInfo.version;
        this.key = licenseInfo.key;
        this.startDate = licenseInfo.startDate;
        this.finishDate = licenseInfo.finishDate;
        this.macAddress = licenseInfo.macAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LicenseInfo) {
            LicenseInfo li = (LicenseInfo) obj;
            return li.version == this.version &&
                    li.key.equals(this.key) &&
                    li.startDate.equals(this.startDate) &&
                    li.finishDate.equals(this.finishDate) &&
                    li.macAddress.equals(this.macAddress);
        }
        return super.equals(obj);
    }

  @Override
  public int hashCode() {
    // auto-generated method.
    int hash = 3;
    hash = 11 * hash + this.version;
    hash = 11 * hash + Objects.hashCode(this.key);
    hash = 11 * hash + Objects.hashCode(this.startDate);
    hash = 11 * hash + Objects.hashCode(this.finishDate);
    hash = 11 * hash + Objects.hashCode(this.macAddress);
    return hash;
  }
}
