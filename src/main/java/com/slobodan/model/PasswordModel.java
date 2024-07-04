package com.slobodan.model;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordModel {
  // TODO: this should be assigned by a user when user run first time application
  private static final String SECRET_KEY = "DropItLikeItsHot!!!";
  private static final String SALT = "EYt1kBk6I6W6sgcITXkRmA==";

  private String fileName;

  public PasswordModel() {
    this.fileName = getFileNameFromConfig();
    if (fileName == null || fileName.isEmpty()) {
      createConfig();
    }
  }

  private String getFileNameFromConfig() {
    Properties properties = new Properties();
    try (FileReader reader = new FileReader("config.properties")) {
      properties.load(reader);
      return properties.getProperty("filename");
    } catch (IOException e) {
      return null;
    }
  }

  private void createConfig() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("No filename found. Please enter a filename:");
    fileName = scanner.nextLine();

    try (FileWriter writer = new FileWriter("config.properties")) {
      Properties properties = new Properties();
      properties.setProperty("filename", fileName);
      properties.store(writer, null);
    } catch (IOException e) {
      e.printStackTrace();
    }

    File file = new File(fileName);
    try {
      if (file.createNewFile()) {
        System.out.println("File created: " + fileName);
      } else {
        System.out.println("File already exists: " + fileName);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      scanner.close();
    }
  }

  public void addPassword(String name, String password) {
    try {
      String encryptedPassword = encrypt(password);
      // TODO:
      try (FileWriter writer = new FileWriter(fileName, true);
          BufferedWriter bw = new BufferedWriter(writer);
          PrintWriter out = new PrintWriter(bw)) {
        if (!passwordExists(name, encryptedPassword)) {
          out.println(name + ":" + encryptedPassword);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<String> getPasswords() {
    List<String> passwords = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = br.readLine()) != null) {
        passwords.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return passwords;
  }

  private boolean passwordExists(String name, String encryptedPassword) {
    List<String> passwords = getPasswords();

    for (String passwordEntry : passwords) {
      String[] parts = passwordEntry.split(":");
      if (parts.length != 2) {
        System.out.println("Invalid password entry format: " + passwordEntry);
        continue;
      }

      String serviceName = parts[0];
      String password = parts[1];
      if (serviceName.equals(name) && password.equals(encryptedPassword)) {
        return true;
      }
    }

    return false;
  }

  // we can't simply make automatic copy to clipboard as we don't have access
  // but we can use external tools something like pbcopy
  public void copyPassword(String name) {
    List<String> passwords = getPasswords();
    for (String passwordEntry : passwords) {
      String[] parts = passwordEntry.split(":");
      if (parts.length != 2) {
        System.out.println("Invalid password entry format: " + passwordEntry);
        continue;
      }
      String service = parts[0];
      if (service.equals(name)) {
        String encryptedPassword = parts[1];
        String decryptedPassword = decrypt(encryptedPassword);
        if (decryptedPassword != null) {
          decryptedPassword = decryptedPassword.trim();
          copyToClipboard(decryptedPassword);
          System.out.println("Password for " + name + " copied to clipboard!");
        } else {
          System.out.println("Error: Failed to decrypt password.");
        }
        return;
      }
    }
    System.out.println("Password for " + name + " not found.");
  }

  public String generatePassword(int length) {
    StringBuilder randomPassword = new StringBuilder(length);

    for (int j = 0; j < length; j++) {
      randomPassword.append(randomCharacter());
    }

    String generatedPassword = randomPassword.toString();
    copyToClipboard(generatedPassword);
    System.out.println("Generated password copied to clipboard!");
    return generatedPassword;
  }

  private void copyToClipboard(String text) {
    try {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit()
            .getSystemClipboard();
        clipboard.setContents(stringSelection, null);
      } else if (os.contains("mac")) {
        String[] cmd = {
            "bash",
            "-c",
            "printf '%s' '" + text + "' | pbcopy",
        };
        Runtime.getRuntime().exec(cmd);
      } else {
        String[] cmd = {
            "bash",
            "-c",
            "printf '%s' '" + text + "' | xclip -selection clipboard",
        };
        Runtime.getRuntime().exec(cmd);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error copying text to clipboard.");
    }
  }

  public char randomCharacter() {
    int rand = ThreadLocalRandom.current().nextInt(62);

    if (rand < 10) {
      return (char) ('0' + rand); // 0-9
    } else if (rand < 36) {
      return (char) ('A' + rand - 10); // 10-35 mapped to A-Z
    } else {
      return (char) ('a' + rand - 36); // 36-61 mapped to a-z
    }
  }

  /*
   * TODO: CHECK THIS!
   * because i'am stupid and can't learn this
   * https://stackoverflow.com/questions/10303767/encrypt-and-decrypt-in-java
   * https://www.baeldung.com/java-aes-encryption-decryption
   * https://jenkov.com/tutorials/java-cryptography/cipher.html
   */

  // TODO: NEXT TIME DON'T USE BCRYPT OR ANY OTHER ONE-WAY HASH ALGO PLEASE!!!
  private String encrypt(String strToEncrypt) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
      SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private String decrypt(String strToDecrypt) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
      SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
      return new String(decryptedBytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}