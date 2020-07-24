
package automation.utils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

public class SeleniumWait {

	WebDriver driver;
	WebDriverWait wait;
	WebDriverWait waitFluently;
	static int count = 0;

	public int timeout;
	public int timeoutDynamic;
	public int configTimeOut;

	public SeleniumWait(WebDriver driver, int timeout) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, timeout);
		this.timeoutDynamic = Integer.parseInt(ConfigPropertyReader.getProperty("timeoutWait"));
		this.waitFluently = new WebDriverWait(driver, this.timeoutDynamic);
		this.timeout = timeout;
		this.configTimeOut = timeout;
	}

	/**
	 * Returns webElement found by the locator if element is visible
	 *
	 * @param locator
	 * @return
	 */
	public WebElement getWhenVisible(By locator) {
		WebElement element;
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return element;
	}

	public WebElement getWhenClickable(By locator) {
		WebElement element;
		element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		return element;
	}

	public boolean waitForPageTitleToBeExact(String expectedPagetitle) {
		return wait.until(ExpectedConditions.titleIs(expectedPagetitle));
	}

	public boolean waitForPageTitleToContain(String expectedPagetitle) {
		return wait.until(ExpectedConditions.titleContains(expectedPagetitle));
	}

	public WebElement waitForElementToBeVisible(WebElement element) {
		try {
			return wait.until(ExpectedConditions.visibilityOf(element));
		} catch (StaleElementReferenceException e) {
			return wait.until(ExpectedConditions.visibilityOf(element));
		}
	}

	public WebElement waitForLocatorToBeVisible(By locator) {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch (StaleElementReferenceException e) {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		}
	}

	public void waitForFrameToBeAvailableAndSwitchToIt(By locator) {
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
	}

	public List<WebElement> waitForElementsToBeVisible(List<WebElement> elements) {
		return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
	}

	public boolean waitForElementToBeInVisible(By locator) {
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public WebElement waitForElementToBeClickable(WebElement element) {
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitForLocatorToBeClickable(By locator) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.elementToBeClickable(locator));
	}

	public boolean waitUntilNewWindowIsOpen(int numOfWindows) {
		return wait.until(ExpectedConditions.numberOfWindowsToBe(numOfWindows));
	}

	public void clickWhenReady(By locator) {
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		element.click();
	}

	public void verifyMsgToast(String msgSubstring) {
		String toastMsg = driver.findElement(By.className("toast-message")).getText().trim();
		if (!toastMsg.contains(msgSubstring)) {
			org.testng.Assert.assertTrue(false,
					"Assertion Failed : Toast Message does not contain '" + msgSubstring + "'");
		}
	}

	public void waitForMsgToastToAppear(String msgSubstring) {
		int i = 0;
		resetImplicitTimeout(1);
		while (true) {
			try {
				if (driver.findElement(By.className("toast-message")).isDisplayed()) {
					resetImplicitTimeout(timeout);
					verifyMsgToast(msgSubstring);
					return;
				}
			} catch (Exception e) {
				hardWait(1);
				i++;
				if (i > timeout) {
					break;
				}
				continue;
			}
		}
		resetImplicitTimeout(timeout);
	}

	public void waitForMsgToastToDisappear() {
		int i = 0;
		resetImplicitTimeout(1);
		try {
			while (driver.findElement(By.className("toast-message")).isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}

	public void waitForElementToDisappear(WebElement element) {
		int i = 0;
		resetImplicitTimeout(3);
		try {
			while (element.isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}

	public void resetImplicitTimeout(int newTimeOut) {
		try {
			driver.manage().timeouts().implicitlyWait(newTimeOut, TimeUnit.SECONDS);
		} catch (Exception e) {
		}
	}
	
	public void resetExplicitTimeout(int newTimeOut) {
		timeoutDynamic = newTimeOut;
	}

	public void setTimeout(int newTimeout) {
		timeout = newTimeout;
	}

	public void waitForPageToLoadCompletely() {
		waitForLocatorToBeVisibleDynamically(By.xpath("//*"));
		waitForPageToLoadCompletelyDynamically();
		Msg.logMessage("Page loaded completely");
	}

	public void hardWait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void hardWaitMillis(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@FindBy(css = ".loader-spinner.position-fixed")
	WebElement loader;

	public void waitForLoaderToDisappear() {
		int i = 0;
		hardWait(2);
		resetImplicitTimeout(5);
		try {
			while (loader.isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}

	@FindBy(xpath = "//div[@class='loader-overlay position-fixed']")
	WebElement overlay;

	public void waitForLoadingOverlayToDisappear() {
		waitForLoaderToDisappear();
		int i = 0;
		hardWait(2);
		resetImplicitTimeout(5);
		try {
			while (overlay.isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}

	@FindBy(className = "load-block-container")
	WebElement loaderMessage;

	public void waitForLoadingMessageToDisappear() {
		int i = 0;
		hardWait(2);
		resetImplicitTimeout(5);
		try {
			while (loaderMessage.isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}

	@FindBy(xpath = "//div[@class='full-loading-overlay']")
	WebElement progressBar;

	public void waitForProgressBarToAppearAndDisappear() {
		waitForProgressBarToAppear();
		waitForProgressBarToDisappear();
	}

	public void waitForProgressBarToAppear() {
		int i = 0;
		resetImplicitTimeout(1);
		while (true) {
			try {
				if (progressBar.isDisplayed()) {
					resetImplicitTimeout(timeout);
					return;
				}
			} catch (Exception e) {
				hardWait(1);
				i++;
				if (i > timeout) {
					break;
				}
				continue;
			}
		}
		resetImplicitTimeout(timeout);
	}

	public void waitForProgressBarToDisappear() {
		int i = 0;
		resetImplicitTimeout(1);
		try {
			while (progressBar.isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}

	public int getTimeout() {
		return configTimeOut;
	}

	@FindBy(xpath = "//*[@class='full-lp-logo']")
	WebElement fullLoader;

	public void waitForFullLoaderToDisappear() {
		int i = 0;
		resetImplicitTimeout(5);
		try {
			while (fullLoader.isDisplayed() && i <= timeout) {
				hardWait(1);
				i++;
			}
		} catch (Exception e) {
		}
		resetImplicitTimeout(timeout);
	}
	

	public void waitForLoaderToAppearAndDisappearVAMerge() {
		resetImplicitTimeout(1);
		try {
			hardWait(1);
			driver.findElements(By.xpath("//div[contains(@class,\"overlay-bg\")]")).get(0).isDisplayed();
		} catch (Exception e) {
			resetImplicitTimeout(timeout);
			return;
		}

		int k = 200, j = 0;
		int size = 10;

		try {
			for (j = 1; j < k; j++) {
				size = driver.findElements(By.xpath("//div[contains(@class,\"overlay-bg\")]")).size();
				if (size == 0) {
					break;
				} else {
					hardWait(1);
					continue;
				}
			}
		} catch (Exception e) {
		}
		Assert.assertTrue(j < 200, "Loader does not disappear in 200 secs");
		Reporter.log("Loader disappeared in " + j + " secs", true);
		resetImplicitTimeout(timeout);
		waitForFullLoaderToDisappear();
		waitForLoaderToDisappear();
	}
	
	
	public void waitForLoaderToAppearAndDisappear() {
		resetImplicitTimeout(1);
		try {
			hardWait(1);
			driver.findElements(By.xpath("//div[@class='blockUI blockMsg blockPage']")).get(0).isDisplayed();
		} catch (Exception e) {
			resetImplicitTimeout(timeout);
			return;
		}

		int k = 200, j = 0;
		int size = 10;

		try {
			for (j = 1; j < k; j++) {
				size = driver.findElements(By.xpath("//div[@class='blockUI blockMsg blockPage']")).size();
				if (size == 0) {
					break;
				} else {
					hardWait(1);
					continue;
				}
			}
		} catch (Exception e) {
		}
		Assert.assertTrue(j < 200, "Loader does not disappear in 200 secs");
		Reporter.log("Loader disappeared in " + j + " secs", true);
		resetImplicitTimeout(timeout);
		waitForFullLoaderToDisappear();
		waitForLoaderToDisappear();
	}

	/**
	 * @author mr13
	 */
	public void waitForRedirectionChainToComplete() {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofSeconds(2))
				.ignoring(WebDriverException.class).until(CustomExpectedConditions.pageSettled(timeoutDynamic));
//	Msg.logMessage("Page loaded after redirection chain");
	}

	/**
	 * @author mr13
	 */
	public void waitForElementToDisappearDynamically(By locator) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofMillis(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.invisibilityOfElementLocated(locator));
//	Msg.logMessage(locator + " Disappeared");
	}

	public void waitForSsoToLoad() {
		waitForPageToLoadCompletely();
		waitForRedirectionChainToComplete();
		waitForRedirectionChainToComplete();
		waitForPageToLoadCompletely();
	}

	public void waitForPageToLoadCompletelyDynamically() {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(WebDriverException.class).until(CustomExpectedConditions.readyStateIsComplete());
	}

	public void waitForLocatorToBeVisibleDynamically(By locator) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForLocatorToBePresentDynamically(By locator) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	public void waitForLocatorsToBePresentDynamically(By locator) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofSeconds(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
	}

	public void waitForAttributeValueToChange(WebElement element, String attributeName, String attributeValue) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofMillis(1))
				.ignoring(WebDriverException.class)
				.until(CustomExpectedConditions.isAttributeUpdated(element, attributeName, attributeValue));
	}

	public void waitForAttributeValueToChangeUsingJs(String xPath, String attributeName, String attributeValue) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofMillis(1))
				.ignoring(WebDriverException.class)
				.until(CustomExpectedConditions.isAttributeUpdatedUsingJS(xPath, attributeName, attributeValue));
	}

	public void waitForUrlToContain(String urlFraction) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofMillis(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.urlContains(urlFraction));
	}

	/**
	 * 
	 * @param status 0 for invisibility and 1 for visibility
	 */
	public void waitForLocatorStateChange(int status, By locator) {
		switch (status) {
		case 0:
			waitForElementToDisappearDynamically(locator);
			break;
		case 1:
			waitForLocatorToBeVisibleDynamically(locator);
			break;
		}
	}

	public void waitForElementToBeClickableDynamic(WebElement element) {
		waitFluently.withTimeout(Duration.ofSeconds(timeoutDynamic)).pollingEvery(Duration.ofMillis(1))
				.ignoring(WebDriverException.class).until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitForAlertToBePresent() {
		wait.until(ExpectedConditions.alertIsPresent());
	}

}
