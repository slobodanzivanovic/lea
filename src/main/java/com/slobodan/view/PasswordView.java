package com.slobodan.view;

import java.util.List;

public class PasswordView {
  public void displayPasswords(List<String> passwords) {
    System.out.println("Existing Passwords:");
    for (String password : passwords) {
      String[] parts = password.split(":");
      String serviceName = parts[0];
      String encryptedPassword = parts[1];
      System.out.println("- " + serviceName + ": " + encryptedPassword);
    }
  }

  public void displayDecryptedPassword(String serviceName, String decryptedPassword) {
    System.out.println("Decrypted Password for " + serviceName + ": " + decryptedPassword);
  }

  public void displayMessage(String message) {
    System.out.println(message);
  }
}