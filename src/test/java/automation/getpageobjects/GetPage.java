package automation.getpageobjects;

import java.io.BufferedReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.testng.Assert;


public class GetPage extends BaseUi{
	public WebDriver driver;
	public String pageName;

	public GetPage()
	{
		super();
	}
	
	public GetPage(WebDriver driver, String pageName) {
		super(driver, pageName);
		this.driver = driver;
		this.pageName = pageName;
	}
	
	protected WebElement element(String elementToken) {
		return _element(elementToken, "", "");
	}

	protected WebElement element(String elementToken, String replacement) {
		return _element(elementToken, replacement, "");
	}

	protected WebElement element(String elementToken, String replacement1, String replacement2) {
		return _element(elementToken, replacement1, replacement2);
	}

	protected WebElement element(String elementToken, String replacement1, String replacement2, String replacement3) {
		return driver.findElement(getLocator(elementToken, replacement1, replacement2, replacement3));
	}
	
	private WebElement _element(String elementToken, String replacement1, String replacement2) {
		if (replacement1.isEmpty() && replacement2.isEmpty()) {
			return driver.findElement(getLocator(elementToken));
		} else if (replacement2.isEmpty() && !replacement1.isEmpty()) {
			return driver.findElement(getLocator(elementToken, replacement1));
		} else if (!replacement1.isEmpty() && !replacement2.isEmpty()) {
			return driver.findElement(getLocator(elementToken, replacement1, replacement2));
		}
		return driver.findElement(getLocator(elementToken));
	}
	
	protected By getLocator(String elementToken) {
		String[] locator = getELementFromFile(this.pageName, elementToken);
		return getLocators(locator[1].trim(), locator[2].trim());
	}

	protected By getLocator(String elementToken, String replacement) {
		String[] locator = getELementFromFile(this.pageName, elementToken);
		locator[2] = locator[2].replaceAll("\\$\\{.+?\\}", replacement);
		return getLocators(locator[1].trim(), locator[2].trim());
	}

	protected By getLocator(String elementToken, String replacement1, String replacement2) {
		String[] locator = getELementFromFile(this.pageName, elementToken);
		locator[2] = locator[2].replaceFirst("\\$\\{.+?\\}", replacement1);
		locator[2] = locator[2].replaceFirst("\\$\\{.+?\\}", replacement2);
		return getLocators(locator[1].trim(), locator[2].trim());
	}

	protected By getLocator(String elementToken, String replacement1, String replacement2, String replacement3) {
		String[] locator = getELementFromFile(this.pageName, elementToken);
		locator[2] = locator[2].replaceFirst("\\$\\{.+?\\}", replacement1);
		locator[2] = locator[2].replaceFirst("\\$\\{.+?\\}", replacement2);
		locator[2] = locator[2].replaceFirst("\\$\\{.+?\\}", replacement3);
		return getLocators(locator[1].trim(), locator[2].trim());
	}
	
	private By getLocators(String locatorType, String locatorValue) {
		switch (Locators.valueOf(locatorType)) {
		case id:
			return By.id(locatorValue);
		case xpath:
			return By.xpath(locatorValue);
		case name:
			return By.name(locatorValue);
		case classname:
			return By.className(locatorValue);
		case css:
			return By.cssSelector(locatorValue);
		case linktext:
			return By.linkText(locatorValue);
		default:
			return By.id(locatorValue);
		}
	}
	
	
	public static String[] getELementFromFile(String pageName, String elementName) {

		BufferedReader br = null;
		String matchingLine = "";
		try {
			String filePathString = "src/test/resources/PageObjectRepository/weather/NDTVLocators.txt";
			File f = new File(filePathString);
			if (!f.exists()) {
				// System.out.println("WARNING : '" + tier + "/" + pageName +
				// ".txt' file not found");
			} else {
				br = new BufferedReader(new FileReader(filePathString));
				String line = br.readLine();
				while (line != null) {
					if (line.split(":", 3)[0].equalsIgnoreCase(elementName)) {
						matchingLine = line;
						break;
					}
					line = br.readLine();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return matchingLine.split(":", 3);
	}




}