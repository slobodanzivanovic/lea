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

  public void displayMessage(String message) {
    System.out.println(message);
  }

  public void displayOptions() {
    System.out.println("Select an option:");
    System.out.println("1. Add Password");
    System.out.println("2. Get Passwords");
    System.out.println("3. Copy Password");
    System.out.println("4. Generate Password");
    System.out.println("5. Exit");
  }

  public void initialScreen() {
    System.out.println();
    System.out.println(" _      _____  ___  ");
    System.out.println("| |    |  ___|/ _ \\ ");
    System.out.println("| |    | |__ / /_\\ \\");
    System.out.println("| |    |  __||  _  |");
    System.out.println("| |____| |___| | | |");
    System.out.println("\\_____/\\____/\\_| |_/");
    System.out.println("                    ");
    System.out.println();
  }
}