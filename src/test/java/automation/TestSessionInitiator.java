package automation;

import static automation.utils.YamlReader.setYamlFilePath;

import org.openqa.selenium.WebDriver;

import weather.keywords.PageActions;

public class TestSessionInitiator {
	public PageActions NDTVPage;
	public WebDriver driver;
	private WebDriverFactory wdfactory;

	private void _initPage() {
		NDTVPage = new PageActions(driver);
	}

	public TestSessionInitiator() {
		super();
		setYamlFilePath();

		wdfactory = new WebDriverFactory();
		driver = wdfactory.getDriver();
		_initPage();
	}
	
	public void launchApplication(String appUrl) {
		driver.get(appUrl);
	}
	
	public void closeBrowserSession() {
		driver.quit();
	}


	

}
