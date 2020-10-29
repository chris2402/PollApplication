package no.hvl.dat250.h2020.group5;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;

/**
 * Handles public and private keys
 * Credits: https://github.com/Severinzz/Sjakk-Arena-backend/blob/develop/src/main/java/no/ntnu/sjakkarena/utils/KeyHelper.java
 */
public class KeyHelper {

  private static SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  private static String secretKeyFileName = "secret.txt";

  /**
   * Returns a secret key
   *
   * @return a secret key
   */
  public static Key getKey() {
    return readKeyFromFile();
  }

  /** Writes a secret key to a file */
  public static void writeKeyToFile() {
    try {
      Path path = Paths.get(secretKeyFileName);
      if (!Files.exists(path)) {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(secretKeyFileName));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(key);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads a key from a file
   *
   * @return a key from a file
   */
  public static Key readKeyFromFile() {
    Key readKey = null;
    try {
      FileInputStream fileInputStream = new FileInputStream(new File(secretKeyFileName));
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      readKey = (Key) objectInputStream.readObject();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return readKey;
  }
}
