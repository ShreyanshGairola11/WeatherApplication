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

import automation.utils.ResourceLoader;

/**
 * This is the utility class for data Read/Write
 *
 * @author QAIT
 *
 */
public class DataPropertFileUtil {

    private static String defaultDataFile;
    
   

    /**
     *
     * This method will get the properties pulled from a file located relative to the base dir
     * @param Property property name for which value has to be fetched
     * @return String value of the property
     * 
     */
    public static String getProperty(String Property) {
        try {
            Properties prop = ResourceLoader.loadProperties(defaultDataFile);
            return prop.getProperty(Property);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
    *
    * This method will write the property value against a key into a file located relative to the base dir
    * @param Property property name for which value has to be written
    * @param Value property value to be written
    * 
    */
       
    public static void setProperty(String Property, String Value) {
    	Properties props ;
		try {
			props = new Properties();
			InputStream inPropFile = new FileInputStream(defaultDataFile);
			props.load(inPropFile);
			inPropFile.close();
			OutputStream outPropFile = new FileOutputStream(defaultDataFile);
			props.setProperty(Property, Value);
			props.store(outPropFile, null);
			outPropFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
   
}
