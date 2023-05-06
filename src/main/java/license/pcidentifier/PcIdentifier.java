package license.pcidentifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import util.Log;

/**
 * Library for getting hardware ids.
 */
public class PcIdentifier {
    /**
     * Gets list of MAC addresses available on this machine. Uses simple filtering for excluding virtual MAC addresses.
     * Can return empty list.
     * <p>
     * MAC address is six groups of two hexadecimal digits(capital letters), separated by hyphens ('-').
     * For example 3D-F2-C9-A6-B3-4F.
     *
     * @return list of available MAC addresses
     * @throws NoMACAddressException if network interface isn't available.
     */
    static public ArrayList<String> getMACAddresses() throws NoMACAddressException {
        try {
            ArrayList<String> MACAddresses = new ArrayList<>();
            for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                byte[] mac = network.getHardwareAddress();
                // MAC address filtering
                if (mac != null) {
                    // Filtering by display name
                    String displayName = network.getDisplayName();
                    if (displayName.toLowerCase().contains("virtual"))
                        continue;
                    if (displayName.toLowerCase().contains("pseudo"))
                        continue;
                    if (displayName.toLowerCase().contains("bluetooth"))
                        continue;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    String macAddress = sb.toString();
                    // Filtering by MAC address string representation
                    if (!checkMACAddressFormat(macAddress)) {
                        continue;
                    }
                    MACAddresses.add(macAddress);
                }
            }
            return MACAddresses;
        } catch (SocketException e) {
            throw new NoMACAddressException();
        }
    }

    /**
     * Checks MAC address format.
     * MAC address is six groups of two hexadecimal digits(capital letters), separated by hyphens ('-').
     * For example 3D-F2-C9-A6-B3-4F.
     *
     * @param macAddress string representation of MAC address
     * @return true if MAC address fits for format. Returns false for broken MAC addresses
     */
    static public boolean checkMACAddressFormat(String macAddress) {
        return macAddress.matches("^([0-9A-F]{2}[-]){5}([0-9A-F]{2})$");
    }

    @Deprecated
    public static String getMotherboardSN() {
      String result = "";
      try {
        File file = File.createTempFile("realhowto", ".vbs");
        file.deleteOnExit();
        try (FileWriter fw = new java.io.FileWriter(file)) {
          String vbs =
                  "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                  + "Set colItems = objWMIService.ExecQuery _ \n"
                  + "   (\"Select * from Win32_BaseBoard\") \n"
                  + "For Each objItem in colItems \n"
                  + "    Wscript.Echo objItem.SerialNumber \n"
                  + "    exit for  ' do the first cpu only! \n"
                  + "Next \n";

          fw.write(vbs);
        }
        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        try (BufferedReader input = new BufferedReader
                                (new InputStreamReader(p.getInputStream()))) {
          String line;
          while ((line = input.readLine()) != null) {
            result += line;
          }
        }
      } catch (Exception e) {
        Log.out.print(e.getMessage());
      }
      return result.trim();
    }

    @Deprecated
    public static String getHardDiskSerialNumber(String drive) {
      String result = "";
      try {
        File file = File.createTempFile("realhowto", ".vbs");
        file.deleteOnExit();
        try (FileWriter fw = new java.io.FileWriter(file)) {
          String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                  + "Set colDrives = objFSO.Drives\n"
                  + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                  + "Wscript.Echo objDrive.SerialNumber";  // see note
          fw.write(vbs);
        } // see note
        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        try (BufferedReader input = new BufferedReader
                                (new InputStreamReader(p.getInputStream()))) {
          String line;
          while ((line = input.readLine()) != null) {
            result += line;
          }
        }
      } catch (Exception e) {
        Log.out.print(e.getMessage());
      }
      return result.trim();
    }

    public static void main(String[] args) {
      try {
        for (String mac : getMACAddresses()) {
          System.out.println("Found MAC address " + mac);
        }
        System.out.println("Motherboard serial number is " + getMotherboardSN());
        System.out.println("Hard disk serial number is " + getHardDiskSerialNumber("C"));
      } catch (NoMACAddressException e) {
        e.printStackTrace();
      }
    }
}
