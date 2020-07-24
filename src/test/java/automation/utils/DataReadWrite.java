package automation.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * This is the utility class for data read write
 *
 * @author QAIT
 *
 */
public class DataReadWrite {

    /**
     * construtor of this class
     */
    public DataReadWrite() {
    }

    /**
     * writeDataToFile
     *
     * @param Property
     * @param Data
     * @return true if written else return false
     */
    public static boolean writeDataToFile(String Property, String Data) {
        PrintWriter pw = null;
        boolean result = false;
        System.out.println("Data=" + Data);
        try {
            System.out.println("Start of try block");
            URL url = ResourceLoader.getResourceUrl("courseData");
            System.out.println(url);
            //String dataFolderPath = URLDecoder.decode(url.getPath(), "UTF-8");
            String path = "./src/test/resources/testdata";
            String dataFolderPath = URLDecoder.decode(path, "UTF-8");
            System.out.println(dataFolderPath);
            String outFilePath = dataFolderPath + File.separator + "courseData.tmp";
            System.out.println(outFilePath);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath, true)));
            pw.println(Property + "=" + Data);

            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }

        }

        return result;
    }

    /**
     * readDataFromFile
     *
     * @param Property
     * @return text
     */
    public static String readDataFromFile(String Property) {
        try {
            Properties prop = ResourceLoader.loadProperties("./src/test/resources/testdata/courseData.tmp");
            return prop.getProperty(Property);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getProperty(String Property) {
        try {
            Properties prop = ResourceLoader.loadProperties("./src/test/resources/testdata/courseData.tmp");
            return prop.getProperty(Property);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * This method will get the properties pulled from a file located relative to the base dir
     * 
     * @param propFile complete or relative (to base dir) file location of the properties file 
     * @param Property property name for which value has to be fetched 
     * @return String value of the property
     */
    public static String getProperty(String propFile, String Property) {
        try {
            Properties prop = ResourceLoader.loadProperties(propFile);
            return prop.getProperty(Property);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }    

    /**
     * readXmlFromFile
     *
     * @param fileName
     * @return text
     */
    public static String readXmlFromFile(String fileName) {

        File file = new File(fileName);
        StringBuilder contents = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
                contents.append(text).append(
                        System.getProperty("line.separator"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (contents.toString());
    }
    //***************************************************
    public static boolean writeDataToFile(String Property, String Data,String filename) {
        PrintWriter pw = null;
        boolean result = false;
        System.out.println("Data=" + Data);
        try {
            System.out.println("Start of try block");
            URL url = ResourceLoader.getResourceUrl("courseData");
            System.out.println(url);
            //String dataFolderPath = URLDecoder.decode(url.getPath(), "UTF-8");
            String path = "./src/test/resources/testdata/SEP";
            String dataFolderPath = URLDecoder.decode(path, "UTF-8");
            System.out.println(dataFolderPath);
            String outFilePath = dataFolderPath + File.separator + filename;
            System.out.println(outFilePath);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath, true)));
            pw.println(Property + "=" + Data);

            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }

        }

        return result;
    }
    
    public static boolean writeDataToFile(String Property, String Data, String productName, String filename) {
        PrintWriter pw = null;
        boolean result = false;
        try {
            //System.out.println("Start of try block");
            URL url = ResourceLoader.getResourceUrl("courseData");
            //System.out.println(url);
            //String dataFolderPath = URLDecoder.decode(url.getPath(), "UTF-8");
            String path = "./src/test/resources/testdata/"+productName;
            String dataFolderPath = URLDecoder.decode(path, "UTF-8");
           // System.out.println(dataFolderPath);
            String outFilePath = dataFolderPath + File.separator + filename;
            //System.out.println(outFilePath);
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath, true)));
            pw.println(Property + "=" + Data);

            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }

        }

        return result;
    }

    /**
     * readDataFromFile
     *
     * @param Property
     * @return text
     */
    public static String readDataFromFile(String productName, String Property,String filename) {
        try {
            Properties prop = ResourceLoader.loadProperties("./src/test/resources/testdata/"+productName+File.separator+filename);
            return prop.getProperty(Property);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    
    /**
     * 
     * This method will get the properties pulled from a file located relative to the base dir
     * 
     * @param propFile complete or relative (to base dir) file location of the properties file 
     * @param Property property name for which value has to be fetched 
     * @return String value of the property
     */
        


}
