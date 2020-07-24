/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automation.getpageobjects;

import static automation.utils.DataReadWrite.getProperty;
import static org.testng.Assert.assertEquals;
import static org.testng.Reporter.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import automation.utils.CustomAssert;
import automation.utils.ExcelReader;
import automation.utils.Msg;
import automation.utils.SeleniumWait;
import automation.utils.YamlReader;

public class BaseUi {

	WebDriver driver, driverToUploadImage;
	public SeleniumWait wait;
	private String pageName;
	private CustomAssert customAssert;
	public Msg msg;
	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int RANDOM_STRING_LENGTH = 5;
	protected String browser;
	List<String> temp;
	String str;
	String[] words;
	WebElement element;
	Scanner sc = new Scanner(System.in);

	JavascriptExecutor js = (JavascriptExecutor) driver;

	protected BaseUi() {

	}

	protected BaseUi(WebDriver driver, String pageName) {
		PageFactory.initElements(driver, this);
		this.driver = driver;
		this.pageName = pageName;
		browser = (String) executeJavascript("return navigator.userAgent;");
		int timeout;
		if (System.getProperty("timeout") != null) {
			timeout = Integer.parseInt(System.getProperty("timeout"));
		} else {
			timeout = Integer.parseInt(getProperty("Config.properties", "timeout"));
		}
		this.wait = new SeleniumWait(driver, timeout);
		customAssert = new CustomAssert(driver);
		browser = (String) executeJavascript("return navigator.userAgent;");
		this.msg = new Msg();
	}

	protected BaseUi(WebDriver driver, WebDriver driverToUploadImage, String pageName) {
		PageFactory.initElements(driver, this);
		this.driverToUploadImage = driverToUploadImage;
		this.driver = driver;
		this.pageName = pageName;
		int timeout;
		if (System.getProperty("timeout") != null) {
			timeout = Integer.parseInt(System.getProperty("timeout"));
		} else {
			timeout = Integer.parseInt(getProperty("Config.properties", "timeout"));
		}
		this.wait = new SeleniumWait(driver, timeout);
		customAssert = new CustomAssert(driver);
	}

	protected String getPageTitle() {
		return driver.getTitle().trim();
	}

	public void logMessage(String string) {
		// Reporter.setEscapeHtml(false);
		// Reporter.log(string, true);
		msg.log(string);
	}

	public String getCurrentURL() {
		return driver.getCurrentUrl();
	}


	/**
	 * this method will get page title of current window and match it partially
	 * with the param provided
	 *
	 * @param expectedPagetitle
	 *            partial page title text
	 */
	protected void verifyPageTitleContains(String expectedPagetitle) {
		String actualPageTitle = getPageTitle().trim();
		customAssert.customAssertTrue(actualPageTitle.contains(expectedPagetitle), "Verifying Actual Page Title: '"
				+ actualPageTitle + "' contains expected Page Title : '" + expectedPagetitle + "'.");
		logMessage("Assertion Passed: PageTitle for " + actualPageTitle + " contains: '" + expectedPagetitle + "'.");
	}

	protected WebElement getElementByIndex(List<WebElement> elementlist, int index) {
		return elementlist.get(index);
	}

	protected WebElement getElementByExactText(List<WebElement> elementlist, String elementtext) {
		WebElement element = null;
		for (WebElement elem : elementlist) {
			if (elem.getText().equalsIgnoreCase(elementtext.trim())) {
				element = elem;
			}
		}
		// FIXME: handle if no element with the text is found in list
		if (element == null) {
		}
		return element;
	}

	protected WebElement getElementByContainsText(List<WebElement> elementlist, String elementtext) {
		WebElement element = null;
		for (WebElement elem : elementlist) {
			if (elem.getText().contains(elementtext.trim())) {
				element = elem;
			}
		}
		// FIXME: handle if no element with the text is found in list
		if (element == null) {
		}
		return element;
	}

	protected void switchToFrame(String id) {
		driver.switchTo().frame(id);
	}

	protected void switchToFrame(WebElement element) {
		wait.waitForElementToBeVisible(element);
		driver.switchTo().frame(element);
	}

	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
		hardWait(2);
		logMessage("Switched to default content");
	}

	protected Object executeJavascript(String script) {
		return ((JavascriptExecutor) driver).executeScript(script);
	}

	protected Object executeJavascript(String method, WebElement token) {
		return ((JavascriptExecutor) driver).executeScript("arguments[0]." + method, token);
	}

	protected void hover(WebElement element) {
		Actions hoverOver = new Actions(driver);
		hoverOver.moveToElement(element).build().perform();
	}

	protected String handleAlert() {
		String alertBoxText = null;
		try {

			Alert alertBox = driver.switchTo().alert();
			alertBoxText = alertBox.getText();
			alertBox.accept();
			logMessage("Alert handled...");
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			logMessage("No Alert window appeared...");
			e.printStackTrace();
		}
		return alertBoxText;
	}

	protected String dismissAlertIfAppears() {
		String alertBoxText = null;
		try {
			Alert alertBox = driver.switchTo().alert();
			alertBoxText = alertBox.getText();
			alertBox.dismiss();
			logMessage("Alert handled...");
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			logMessage("No Alert window appeared...");
		}
		return alertBoxText;
	}

	public String handleAlert(String option) {
		switchToAlert();

		String alertBoxText = null;
		try {
			Alert alertBox = driver.switchTo().alert();
			alertBoxText = alertBox.getText();

			if (option.equalsIgnoreCase("Yes")) {
				alertBox.accept();
				logMessage("Alert was accepted");
			} else {
				alertBox.dismiss();
				logMessage("Alert was dismissed");
			}
			logMessage("Alert Box text: " + alertBoxText);
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			logMessage("No Alert window appeared...");
		}
		return alertBoxText;
	}

	protected Alert switchToAlert() {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		return wait.until(ExpectedConditions.alertIsPresent());
	}

	protected void waitForPageToLoadCompletely(String pageTitle) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.titleContains(pageTitle));
	}

	protected void waitForPageUrlToContainsQSP() {
		WebDriverWait wait = new WebDriverWait(driver, 40);
		wait.until(ExpectedConditions.urlContains("source=ku_test&ve=60080"));
	}

	public void switchWindow() {
		for (String current : driver.getWindowHandles()) {
			driver.switchTo().window(current);
		}
	}

	static String parentWindow;

	public void changeWindow(int i) {
		hardWait(1);
		parentWindow = driver.getWindowHandle();
		Set<String> windows = driver.getWindowHandles();
		if (i > 0) {
			for (int j = 0; j < 9; j++) {
				hardWait(1);
				if (windows.size() >= 2) {
					try {
						Thread.sleep(5000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					break;
				}
				windows = driver.getWindowHandles();
			}
		}
		String wins[] = windows.toArray(new String[windows.size()]);
		driver.switchTo().window(wins[i]);
		hardWait(2);
		msg.log("Title: " + driver.switchTo().window(wins[i]).getTitle());
	}

	// FIXME Remove hard Wait option
	protected void hardWait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	protected void hardWait(double seconds) {
		try {
			Thread.sleep((long) (seconds * 1000));
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void closeChildAndMoveWindow() {
		driver.close();
		switchToParentOnly();
	}

	public void closeChildAndMoveWindow(int size) {
		if (driver.getWindowHandles().size() > 1)
			driver.close();
		switchToParentOnly();
	}

	public void switchToParentOnly() {
		driver.switchTo().window(parentWindow);
	}

	public void closeWindow() {
		String orignalhandle = driver.getWindowHandle();
		System.out.println("handle is -->>" + orignalhandle);
		hardWait(1);
		driver.close();
	}

	protected void selectProvidedTextFromDropDown(WebElement el, String text) {
		wait.waitForElementToBeVisible(el);
		Select sel = new Select(el);
		sel.selectByVisibleText(text);
	}

	protected void selectByValueFromDropDown(WebElement el, String value) {
		wait.waitForElementToBeVisible(el);
		Select sel = new Select(el);
		sel.selectByValue(value);

	}

	protected String verifySelectedTextInDropdown(WebElement el) {
		wait.waitForElementToBeVisible(el);
		Select sel = new Select(el);
		String value = sel.getFirstSelectedOption().getText();
		return value;
	}

	protected void scrollToTop() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,-10000)");
	}

	protected void scrollToBottom() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,10000)");
	}

	protected void scrollThroughJavascript(String value) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0," + value + ")", "");
	}

	protected void scrollBy(String num) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0," + num + ")");
	}

	protected void scrollDown(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0, -100000)");

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		hardWait(1);
		jse.executeScript("window.scrollBy(0,-350)");

	}

	protected void scroll(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	protected void hoverClick(WebElement element) {
		Actions hoverClick = new Actions(driver);
		hoverClick.moveToElement(element).click().build().perform();
	}

	protected void fillText(WebElement element, String text) {
		element.click();
		element.clear();
		element.sendKeys(text);
		logMessage("Filled '" + text + "' in Text Field");
	}

	protected void selectText(WebElement element) {
		Actions action = new Actions(driver);
		action.clickAndHold(element).build().perform();
		action.release().perform();
		action.clickAndHold(element).build().perform();
		action.moveToElement(element, 0, 0).build().perform();
		action.release().build().perform();
	}

	protected boolean areHtmlTagsDisplayed(WebElement el) {
		String text = el.getText();
		String pattern = "<\\w+?>\\w+?<\\/\\w+?>|<[\\w]+?\\/>";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(text);
		if (m.find()) {
			return true;
		}
		return false;
	}

	protected void isStringMatching(String actual, String expected) {
		Assert.assertEquals(actual, expected);
		logMessage("ACTUAL STRING : " + actual);
		logMessage("EXPECTED STRING : " + expected);
		logMessage("String compare Assertion passed.");
	}

	public void refreshPage() {
		driver.navigate().refresh();
		logMessage("Page refreshed by Webdriver");
	}

	public void navigateBack() {
		driver.navigate().back();
		logMessage("Page has navigated back in the browser's history");
	}

	public void navigateForward() {
		driver.navigate().forward();
		logMessage("Page has navigated forward in the browser's history");
	}

	public void reloadPage() {
		driver.findElement(By.xpath("//body")).sendKeys(Keys.F5);
		msg.log("Page Reload");
	}

	public void reloadPageUsingJs() {
		executeJavascript("location.reload()");
		logMessage("Page reloaded using Js");
	}

	public void clearCookies() {
		driver.manage().deleteAllCookies();
	}

	public void click(WebElement element) {
		try {
			wait.waitForElementToBeVisible(element);
			if (browser.contains("Chrome")) {
				new Actions(driver).click(element).build().perform();
			} else {
				element.click();
			}
		} catch (StaleElementReferenceException ex1) {
			wait.waitForElementToBeVisible(element);
			scrollDown(element);
			element.click();
			logMessage("Clicked Element " + element + " after catching Stale Element Exception");
		} catch (UnhandledAlertException u) {
			handleAlert();
			element.click();
		} catch (JavascriptException e) {
			scroll(element);
			element.click();
			logMessage("Clicked Element " + element + " after catching Javascript Exception");
		}
	}

	public void enterText(WebElement element, String text) {
		try {
			wait.waitForElementToBeVisible(element);
			// scrollDown(element);
			element.clear();
			element.sendKeys(text);
		} catch (StaleElementReferenceException ex1) {
			scrollDown(element);
			element.clear();
			element.sendKeys(text);
			logMessage("Entered Text '" + text + "' in Element " + element + " after catching Stale Element Exception");
		} catch (UnhandledAlertException u) {
			handleAlert();
			scrollDown(element);
			element.clear();
			element.sendKeys(text);
		}
	}

	public void enterText(WebElement element, String text, String msg) {
		try {
			// wait.waitForElementToBeVisible(element);
			// scrollDown(element);
			element.clear();
			element.sendKeys(text);
			logMessage(msg);
		} catch (StaleElementReferenceException ex1) {
			scrollDown(element);
			element.clear();
			element.sendKeys(text);
			logMessage("Entered Text '" + text + "' in Element " + element + " after catching Stale Element Exception");
		} catch (UnhandledAlertException u) {
			handleAlert();
			scrollDown(element);
			element.clear();
			element.sendKeys(text);
		}
	}

	public void hoverOverElement(WebElement ele) {
		Actions hoverOver = new Actions(driver);
		hoverOver.moveToElement(ele).build().perform();
	}

	protected void scrollDownPX(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scroll(0, -100000)");
		hardWait(1);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		hardWait(1);
		jse.executeScript("window.scrollBy(0,-350)");
	}

	protected void doubleClick(WebElement element) {
		Actions act = new Actions(driver);
		act.doubleClick(element).perform();
	}

	protected boolean enterData(WebElement element, String text) {
		try {
			wait.waitForElementToBeVisible(element);
			element.click();
			element.clear();
			element.sendKeys(text);
			logMessage("Entered Text : " + text);
			return true;
		} catch (StaleElementReferenceException e) {
			wait.waitForElementToBeVisible(element);
			element.click();
			element.clear();
			element.sendKeys(text);
			logMessage("Data Entered after handling Stale Element");
			return true;
		} catch (Exception ex) {
			logMessage("In Catch Block: " + ex.getMessage());
			return false;
		}
	}
	
	protected boolean enterData(WebElement element, String text,String csvfileData) {
		if(csvfileData.contains("Yes"))
			{
			try {
				wait.waitForElementToBeVisible(element);
				element.click();
				element.clear();
				element.sendKeys(text);
				logMessage("Entered Text : " + text);
				return true;
			} catch (StaleElementReferenceException e) {
				wait.waitForElementToBeVisible(element);
				element.click();
				element.clear();
				element.sendKeys(text);
				logMessage("Data Entered after handling Stale Element");
				return true;
			} catch (Exception ex) {
				logMessage("In Catch Block: " + ex.getMessage());
				return false;
			}
		}
		return false;
	}

	public String getUniqueName(String name) {
		int i = YamlReader.generateRandomNumber(1, 25000);
		return name + String.valueOf(i);
	}

	protected String[] getLastNameFirstNameOfUser(String userName) {
		String[] user = userName.split(" ");
		return user;
	}

	/**
	 * This method generates random string
	 *
	 * @return
	 */

	// Blocked Words List.xls
	public String generateRandomString() {
		int flag = 0;
		StringBuffer randStr = new StringBuffer();
		for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
			int number = getRandomNumber();
			char ch = CHAR_LIST.charAt(number);
			randStr.append(ch);
		}
		msg.log("Random string: " + randStr);
		try {
			Sheet testData = ExcelReader.readExcel("Sheet1", "Blocked_Words_List.xls");
			for (int i = 1; i < testData.getPhysicalNumberOfRows(); i++) {
				temp = new ArrayList<String>();
				for (int j = 0; j < testData.getRow(i).getPhysicalNumberOfCells(); j++) {
					if (randStr.toString().toLowerCase()
							.contains(testData.getRow(i).getCell(j).getStringCellValue().toLowerCase())) {
						flag++;
						msg.log("Blocked words: " + testData.getRow(i).getCell(j).getStringCellValue());
						while (flag > 0) {
							randStr.setLength(0);
							for (i = 0; i < RANDOM_STRING_LENGTH; i++) {
								int number = getRandomNumber();
								char ch = CHAR_LIST.charAt(number);
								randStr.append(ch);
							}
							if (randStr.toString().toLowerCase()
									.contains(testData.getRow(i).getCell(j).getStringCellValue().toLowerCase()))
								flag++;
							else
								flag = 0;
						}
						msg.log("New Random string: " + randStr.toString());
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return randStr.toString();
	}

	/**
	 * This method generates random numbers
	 *
	 * @return int
	 */

	public void verifyThatSourceAndVendorIDIsAppended(String URL) {
		String parameters = "source=ku_test&ve=60080";

		if (URL.toLowerCase().contains(parameters)) {
			msg.log("URL containing source and vendor id:" + URL);
		} else {
			msg.log("URL doesn't contains source and vendor id: " + URL);
			msg.log("Browser closed");
			driver.close();
		}
	}

	public int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(CHAR_LIST.length());
		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}

	public void dragAndDropElement(WebElement fromElem, WebElement toElem) {
		Actions builder = new Actions(driver);
		Action dragAndDrop = builder.clickAndHold(fromElem).moveToElement(toElem).release(toElem).build();

		dragAndDrop.perform();
	}

	/**
	 * This method will select by provided index number
	 *
	 * @param el
	 * @param index
	 */
	protected void selectByIndex(WebElement el, int index) {
		wait.waitForElementToBeVisible(el);
		Select sel = new Select(el);
		sel.selectByIndex(index);
	}

	public String getValueUsingJS(WebElement element) {
		String value = (String) ((JavascriptExecutor) driver).executeScript("arguments[0].value;", element);
		return value;
	}

	ArrayList<String> tabs;

	public void switchTabs(int i) {
		tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(i));

	}

	public Set<String> getAllWindowHandles() {
		return driver.getWindowHandles();
	}

	public String getCurrentWindowHandle() {
		return driver.getWindowHandle();
	}

	public void closeCurrentWindow() {
		driver.close();
	}

	public void SwitchToGivenWindow(String windowHandle) {
		driver.switchTo().window(windowHandle);
	}

	public void closeChildTabAndMoveToParent(String parentHandle) {
		driver.close();
		driver.switchTo().window(parentHandle);
	}

	public void closeCurrentTabAndSwitchToGivenTab(int i) {
		tabs = new ArrayList<String>(driver.getWindowHandles());
		msg.log("Switching tabs");
		System.out.println("size:" + driver.getWindowHandles().size());
		if (driver.getWindowHandles().size() > 1) {

			driver.close();
			driver.switchTo().window(tabs.get(i));
			msg.actions("Tab switched");
		}

	}

	public void closeCurrentTabAndSwitchToGivenTab(int i, String title) {
		tabs = new ArrayList<String>(driver.getWindowHandles());
		msg.log("Switching tabs");
		System.out.println("size:" + driver.getWindowHandles().size());
		if (driver.getWindowHandles().size() > 1) {
			driver.switchTo().window(tabs.get(i));
			wait.waitForPageTitleToContain(title);
			driver.switchTo().window(tabs.get(0));
			driver.close();
			driver.switchTo().window(tabs.get(i));
			msg.actions("Tab switched");
		}

	}

	public int getTotalNoOfWindows() {
		return driver.getWindowHandles().size();
	}

	// public void clearCache(){
	// driver.get("chrome://settings/clearBrowserData");
	// wait.waitForElementToBeInVisible(driver.findElement(By.cssSelector("*
	// /deep/ #clearBrowsingDataConfirm")));
	// driver.findElement(By.cssSelector("* /deep/
	// #clearBrowsingDataConfirm")).click();
	// wait.waitForElementToBeInVisible(driver.findElement(By.cssSelector("*
	// /deep/ #clearBrowsingDataConfirm")));
	// System.out.println("Cache cleared");
	// //int timeout;
	//// if (System.getProperty("timeout") != null) {
	//// timeout = Integer.parseInt(System.getProperty("timeout"));
	//// } else {
	//// timeout = Integer.parseInt(_getSessionConfig().get("timeout"));
	//// }
	//// SeleniumWait wait = WebDriverWait(driver,timeout);
	//
	// }
	public String switchToGivenWindowByTitle(String givenTitle) {
		boolean found = false;
		String parent = driver.getWindowHandle();
		Set<String> windows = driver.getWindowHandles();
		Msg.logMessage("Size Of Windows :: " + windows.size());
		int count = 0;
		outer: while (count <= 5) {
			for (String win : windows) {
				driver.switchTo().window(win);
				wait.hardWait(2);
				msg.log("Title: " + driver.getTitle());
				if (driver.getTitle().equalsIgnoreCase(givenTitle)) {
					found = true;
					break outer;

				}
			}
			count++;
		}
		Assert.assertTrue(found, "No Window Found with Page Title Containing :: '" + givenTitle + "'");
		msg.pass("Window Found With Page Title Containing :: '" + givenTitle + "'");

		return parent;
	}

	public String switchToGivenWindowifTitleContainsGivenWord(String givenTitle) {
		boolean found = false;
		String parent = driver.getWindowHandle();
		Set<String> windows = driver.getWindowHandles();
		Msg.logMessage("Size Of Windows :: " + windows.size());
		int count = 0;
		outer: while (count <= 5) {
			for (String win : windows) {
				driver.switchTo().window(win);
				wait.hardWait(2);
				msg.log("Title: " + driver.getTitle());
				if (driver.getTitle().toLowerCase().contains(givenTitle.toLowerCase())) {
					found = true;
					break outer;

				}
			}
			count++;
		}
		Assert.assertTrue(found, "No Window Found with Page Title Containing :: '" + givenTitle + "'");
		msg.pass("Window Found With Page Title Containing :: '" + givenTitle + "'");

		return parent;
	}

	public void launchGivenUrlInNewTab(String URL) {
		logMessage(" URL >> " + URL);
		switchToDefaultContent();
		executeJavascript("window.open('" + URL + "','_blank');");
	}

	public String launchGivenURLInNewTabAndSwitchToGivenWindow(String URL, String windowTitle) {
		launchGivenUrlInNewTab(URL);
		return switchToGivenWindowByTitle(windowTitle);
	}

	public String launchGivenURLInNewTabAndSwitchToGivenWindow(String URL, int window) {
		String parent = getCurrentWindowHandle();
		logMessage(" URL >> " + URL);
		switchToDefaultContent();
		executeJavascript("window.open('" + URL + "','_blank');");
		changeWindow(window);
		return parent;
	}

	public void VerifyTextColor(WebElement e, String textColor) {
		String color = e.getCssValue("color");
		System.out.println("%%%%%%%" + color);
		Assert.assertEquals(color.trim(), textColor, "Assertion failed: Text/link color not verified");
		msg.pass("Color " + color + " for text/link- " + e.getText() + " matched");
	}

	public void VerifyBackgroundColor(WebElement e, String backgroundColor) {
		String color = e.getCssValue("background-color");
		Assert.assertEquals(color.trim(), backgroundColor, "Assertion failed: background color not verified");
		msg.pass("Background Color " + color + " for field name= " + e.getText() + " matched");
	}

	public void VerifyTileColor(WebElement e, String backgroundColor) {

		String color = Color.fromString(e.getCssValue("border-top-color")).asHex();
		msg.log(color);
		msg.log(backgroundColor);
		Assert.assertEquals(color.trim(), backgroundColor, "Assertion failed: background color not verified");
		msg.pass("Background Color " + color + " for field name= " + e.getText() + " matched");
	}

	public void VerifyTextFont(WebElement e, String textFont) {
		String font = e.getCssValue("font-family");
		Assert.assertTrue(font.split(",")[0].contains(textFont.split(",")[0]),
				"Assertion failed: Text/link font not verified");
		msg.pass("Font " + font + " for text/link- " + e.getText() + " matched");
	}

	public boolean VerifyFontSize(WebElement e, String fontSize) {
		String fontFromUI = e.getCssValue("font-size");
		return fontFromUI.equals(fontSize);
	}

	public void VerifyFontSizeOfElement(WebElement e, String fontSize) {
		String font_family = e.getCssValue("font-size");
		Assert.assertTrue(font_family.contains(fontSize),
				"[ASSERT FAILED]: Font does not matches Expected:" + fontSize + " Actual:" + font_family);
		msg.pass("Desired Font is present");
	}

	public void VerifyLineHeight(WebElement e, String lineheight) {
		String font_family = e.getCssValue("line-height");
		Assert.assertTrue(font_family.contains(lineheight),
				"[ASSERT FAILED]: Font does not matches Expected:" + lineheight + " Actual:" + font_family);
		msg.pass("Desired Font is present");
	}

	protected int getElement_IndexBasedUponText(List<WebElement> el, String expectedText) {
		for (int i = 0; i < el.size(); i++) {
			if (el.get(i).getText().trim().contains(expectedText)) {
				msg.log("=> Element with text :: '" + el.get(i).getText() + "' has been found at index :: " + (i + 1));
				return i + 1;
			}
		}
		return -1;
	}

	public List<WebElement> getElementsInsideElement(WebElement parentElement, By locatorOfChildElement) {
		try {
			List<WebElement> elemList = parentElement.findElements(locatorOfChildElement);
			return elemList;
		} catch (NoSuchElementException | NullPointerException ex) {
			msg.log("Elements with given locator :: '" + locatorOfChildElement.toString()
					+ "' was not found in the parent element.");
			return null;
		}
	}

	/**
	 * @author Mr.13
	 * @param element:
	 *            an object of WebElement type
	 */
	protected void scrollToElement(WebElement element) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(" + element.getLocation().x + "," + element.getLocation().y + ")");
		logMessage("scrolled to the element: " + element.toString());
	}
}
