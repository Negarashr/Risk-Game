package risk.game.grp.twenty.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Utility class for the methods needed to complete the game needed tasks
 *
 * @author Team 20
 */
public class InternalTools {

  /**
   * generate Random Number In a given Range
   *
   * @param min minimum number for random generation
   * @param max maximum number for random generation
   * @return a random integer
   */
  public static int getRandomNumberInRange(int min, int max) {

    if (min > max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  /**
   * read a file
   *
   * @param filePath path of file to read
   * @return file content as string
   * @throws IOException if java throws Exception
   */
  public static String readFile(String filePath) throws IOException {

    final StringBuilder content = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String sCurrentLine;
      while ((sCurrentLine = br.readLine()) != null) {
        content.append(sCurrentLine);
        content.append("\n");
      }

    }

    return content.toString();
  }

  /**
   * write a file
   *
   * @param fileContent content to write in file
   * @param filePath to create a file
   * @return path of created file
   * @throws IOException if java throws Exception
   */
  public static String writeFile(String fileContent, String filePath) throws IOException {

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
      bw.write(fileContent);
    }
    return filePath;

  }

  /**
   * write Object In File
   *
   * @param file file to be created
   * @param objToWrite object to write
   * @throws Exception if java throws Exception
   */
  public static void writeObjectInFile(File file, Object objToWrite) throws Exception {
    FileOutputStream fileOutputStream = null;
    ObjectOutputStream objectOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(file);
      objectOutputStream = new ObjectOutputStream(fileOutputStream);

      // Write objects to file
      objectOutputStream.writeObject(objToWrite);
    } finally {
      objectOutputStream.close();
      fileOutputStream.close();
    }
  }

  /**
   * deserialize the object
   *
   * @param file file to read object from
   * @return object from file
   * @throws Exception if java throws Exception
   */
  public static Object readObjectsFromFile(File file) throws Exception {
    FileInputStream fi = new FileInputStream(file);
    ObjectInputStream oi = new ObjectInputStream(fi);

    // Read objects
    return oi.readObject();

  }

  /**
   * get Date Format For File Name
   *
   * @return a custom date format
   */
  public static String getDateFormatForFileName() {
    final DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    return sdf.format(new Date());
  }

  /**
   * checks list Equals Ignore Order
   *
   * @param list1 list number one
   * @param list2 list number two
   * @param <T> generic option
   * @return boolean value
   */
  public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
    return new HashSet<>(list1).equals(new HashSet<>(list2));
  }
}
