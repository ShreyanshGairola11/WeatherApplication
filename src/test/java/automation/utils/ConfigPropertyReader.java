package automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;



/**
 * This is the utility class for data read write
 *
 * @author QAIT
 *
 */
public class ConfigPropertyReader {

    private static String defaultConfigFile = "./Config.properties";
    
    /**
     * construtor of this class
     */
    public ConfigPropertyReader() {
    }

    /**
     *
     * This method will get the properties pulled from a file located relative to the base dir
     *
     * @param propFile complete or relative (to base dir) file location of the properties file
     * @param Property property name for which value has to be fetched
     * @return String value of the property
     */
    public static String getPropertyConfig(String propFile, String Property) {
        try {
            Properties prop = ResourceLoader.loadProperties(propFile);
            return prop.getProperty(Property);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
//    public static String readFromXml(String xmlFile, String property){
//    	try{
//    		Properties prop= ResourceLoader.loadProperties(xmlFile);
//    		InputStream inXml=new FileInputStream(xmlFile);
//    		//return prop.loadFromXML(inXml);
//    	}catch(Exception e){
//    		e.printStackTrace();
//    		return null;
//    	}
//    }
    
    
    public static void setProperty(String propFile, String key,String Value) {
      Properties props ;
     try {
      props = new Properties();
      InputStream inPropFile = new FileInputStream(propFile);
      props.load(inPropFile);
      inPropFile.close();
      OutputStream outPropFile = new FileOutputStream(propFile);
      props.setProperty(key, Value);
      props.store(outPropFile, null);
      outPropFile.close();
     } catch (IOException e) {
      e.printStackTrace();
     }
    }

    
    public static void writeIntoXML(String xmlFIle, String key, String value){
    	Properties prop;
    	try{
    		prop= new Properties();
    		InputStream inXml=new FileInputStream(xmlFIle);
    		prop.load(inXml);
    		inXml.close();
    		OutputStream outXml = new FileOutputStream(xmlFIle);
    		prop.setProperty(key, value);
    		prop.storeToXML(outXml, key);
    		outXml.close();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
    
    
    
    public static String getPropertyFromFile(String fileName,String property){
        return getPropertyConfig(fileName, property);
    }
    
    
    
    
    public static String getProperty(String property){
        return getPropertyConfig(defaultConfigFile, property);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
