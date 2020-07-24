package automation.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * @author mr13
 */

public class CustomExpectedConditions {

	static String lastURL = null;
	static String currentURL = null;
	static long lastComplete = 0L;
	static long redirectTime = 0L;

	public static ExpectedCondition<Boolean> pageSettled(final long timeout) {
		return new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				currentURL = driver.getCurrentUrl();
				String readyState = String
						.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"));
				boolean complete = readyState.equalsIgnoreCase("complete");

				if (!complete) {
					lastComplete = 0L;
					return null;
				}

				if (lastURL != null && !lastURL.equals(currentURL)) {
					lastComplete = 0L;
				}

				lastURL = currentURL;

				if (lastComplete == 0L) {
					lastComplete = System.currentTimeMillis();
					return null;
				}

				redirectTime = System.currentTimeMillis() - lastComplete;

				if (redirectTime < timeout) {
					return null;
				}

				return lastURL.equals(currentURL);
			}

			@Override
			public String toString() {
				return String.format("Redirection completed after %d", redirectTime);
			}
		};
	}

	public static ExpectedCondition<Boolean> readyStateIsComplete() {
		return new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				String readyState = String
						.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"));
				return readyState.equalsIgnoreCase("complete");

			}

			@Override
			public String toString() {
				return String.format("Ready State: Complete");
			}
		};
	}

	public static ExpectedCondition<Boolean> isAttributeUpdated(final WebElement element, final String attributeName,
			final String attributeValue) {
		return new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				return element.getAttribute(attributeName).contains(attributeValue);
			}
		};
	}

	public static ExpectedCondition<Boolean> isAttributeUpdatedUsingJS(final String xPath, final String attributeName,
			final String attributeValue) {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				final String script = "function getElementByXpath(path){   "
						+ "return document.evaluate(path, document, null, "
						+ "XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; } "
						+ "return getElementByXpath(\"" + xPath + "\").getAttribute('" + attributeName + "');";
				String actualValue = (String) ((JavascriptExecutor) driver).executeScript(script);
				return actualValue.contains(attributeValue);
			}
		};
	}

}
