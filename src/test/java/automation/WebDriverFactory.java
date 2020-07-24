package automation;

import java.util.HashMap;
import java.util.logging.Level;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

public class WebDriverFactory {

	private static String chromeDriverPath = "src/test/resources/drivers/chromedriver";

	public WebDriver getDriver() {
		return getChromeDriver(chromeDriverPath + ".exe");
	}
	
	private static WebDriver getChromeDriver(String driverpath) {
		System.setProperty("webdriver.chrome.driver", driverpath);
		ChromeOptions options = getChromeOption();
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("download.default_directory", "src/test/resources/downloads");
		options.setExperimentalOption("prefs", chromePrefs);
		return new ChromeDriver(options);
	}
	
	private static ChromeOptions getChromeOption() {
		ChromeOptions options = new ChromeOptions();

		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		options.setCapability("goog:loggingPrefs", logPrefs);

		options.addArguments("--always-authorize-plugins=true");
		options.addArguments("start-maximized");
		options.addArguments("--disable--extensions");
		options.addArguments("disable--extensions");
		options.addArguments("--disable-popup-blocking");
		options.setPageLoadStrategy(PageLoadStrategy.NONE);
		options.setAcceptInsecureCerts(false);
		options.is(CapabilityType.SUPPORTS_JAVASCRIPT);
		return options;
	}



}