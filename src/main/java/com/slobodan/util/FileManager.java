package com.slobodan.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileManager {
  // https://stackoverflow.com/questions/22370051/how-to-write-values-in-a-properties-file-through-java-code
  // https://www.tutorialspoint.com/how-to-read-write-data-from-to-properties-file-in-java
  private static final String CONFIG_FILE = "config.properties";

  public static String getFileName() {
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
      props.load(fis);
      return props.getProperty("filename");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void setFileName(String fileName) {
    Properties props = new Properties();
    props.setProperty("filename", fileName);
    try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
      props.store(fos, "Configuration Properties");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}