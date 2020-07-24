/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automation.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Reporter;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class YamlReader {

	private static String yamlFilePath;
	private static String tier = System.getProperty("tier");
	private static String leadtype = System.getProperty("leadtype");
	private static String tier1 = null;
	private static String product = null;

	@SuppressWarnings("resource")
	public static String setYamlFilePath() {
		yamlFilePath = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testdata"
					+ File.separator + "weather"+ File.separator + "TestData.yml";

		System.out.println("Yaml file path :: " + yamlFilePath);
		try {
			new FileReader(yamlFilePath);
		} catch (FileNotFoundException e) {
			Reporter.log("Wrong Tier!!!", true);
			System.exit(0);
		}
		
		return yamlFilePath;
	}

	public static void setTier(String env) {
		tier1 = env;
	}

	public static String getTier() {
		return tier;
	}

	public static String getYamlValue(String token) {
		if (yamlFilePath == null) {
			setYamlFilePath();
		}
		return getValue(token);
	}

	public static String getYamlValue(String yamlPath, String token) {
		if (yamlFilePath == null) {
			setYamlFilePath();
		}
		return getValue(yamlPath, token);
	}

	@Deprecated
	public static String getData(String token) {
		return getYamlValue(token);
	}

	public static Map<String, Object> getYamlValues(String token) {
		if (yamlFilePath == null) {
			setYamlFilePath();
		}
		Reader doc;
		try {
			doc = new FileReader(yamlFilePath);
		} catch (FileNotFoundException ex) {
			System.out.println("File not valid or missing!!!");
			ex.printStackTrace();
			return null;
		}
		Yaml yaml = new Yaml();
		// TODO: check the type casting of object into the Map and create
		// instance in one place
		Map<String, Object> object = (Map<String, Object>) yaml.load(doc);
		return parseMap(object, token + ".");
	}

	private static String getValue(String token) {
		Reader doc = null;
		try {
			doc = new FileReader(yamlFilePath);
		} catch (FileNotFoundException e) {
			Reporter.log("Wrong tier", true);
			return null;
		}
		Yaml yaml = new Yaml();
		Map<String, Object> object = (Map<String, Object>) yaml.load(doc);
		try {
			return getMapValue(object, token);
		} catch (Exception e) {
			return null;
		}

	}

	private static String getValue(String yamlPath, String token) {
		Reader doc = null;
		try {
			doc = new FileReader(yamlPath);
		} catch (FileNotFoundException e) {
			Reporter.log("Wrong tier", true);
			return null;
		}
		Yaml yaml = new Yaml();
		Map<String, Object> object = (Map<String, Object>) yaml.load(doc);
		try {
			return getMapValue(object, token);
		} catch (Exception e) {
			return null;
		}

	}

	private static String getMapValue(Map<String, Object> object, String token) {
		// TODO: check for proper yaml token string based on presence of '.'
		String[] st = token.split("\\.");
		return parseMap(object, token).get(st[st.length - 1]).toString();
	}

	private static Map<String, Object> parseMap(Map<String, Object> object, String token) {
		if (token.contains(".")) {
			String[] st = token.split("\\.");
			object = parseMap((Map<String, Object>) object.get(st[0]), token.replace(st[0] + ".", ""));
		}
		return object;
	}

	public static int generateRandomNumber(int MinRange, int MaxRange) {
		int randomNumber = MinRange + (int) (Math.random() * ((MaxRange - MinRange) + 1));
		return randomNumber;
	}
	/**
	 * The List returned by this method is fixed size list 
	 * @param token
	 * @return
	 */
	public static List<String> getYamlValuesAsList(String token) {
		List<String> values = new ArrayList<String>();
		for(String s: getYamlValue(token).split("[|]")) {
			System.err.println(s.trim());
			values.add(s.trim());
		}
		return values;
	}
	
	public static List<String> getYamlValuesAsListWithComas(String token) {
		List<String> values = new ArrayList<String>();
		for(String s: getYamlValue(token).split("[,]")) {
			System.err.println(s.trim());
			values.add(s.trim());
		}
		return values;
	}
}
