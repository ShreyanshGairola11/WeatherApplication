package automation.utils;

import static org.testng.internal.EclipseInterface.ASSERT_LEFT;
import static org.testng.internal.EclipseInterface.ASSERT_MIDDLE;
import static org.testng.internal.EclipseInterface.ASSERT_RIGHT;
import java.lang.reflect.Array;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CustomAssert extends Assert {
	WebDriver driver;
	static CustomFunctions customFunctions;
	static String uploadScreenshot;
	
	public CustomAssert(WebDriver driver) {
		this.driver = driver;
	}
	
	public static void setUploadScreenshotFlag(String flag){
		if (flag.equalsIgnoreCase("yes")) uploadScreenshot = "true";
		else uploadScreenshot = "false";
	}
	

	public void customAssertEquals(String actual, String expected,
			String message) {
		customFunctions = new CustomFunctions(driver);
		CustomAssert.assertEquals((Object) actual, (Object) expected, message);
	}
	
	public void customAssertEquals(Boolean actual, Boolean expected,
			String message) {
		customFunctions = new CustomFunctions(driver);
		CustomAssert.assertEquals((Object) actual, (Object) expected, message);
	}

	public void customAssertEquals(int actual, int expected, String message) {
		customFunctions = new CustomFunctions(driver);
		CustomAssert.assertEquals(Integer.valueOf(actual),
				Integer.valueOf(expected), message);
	}
	
	public void customAssertNotEquals(String actual, String expected,
			String message) {
		customFunctions = new CustomFunctions(driver);
		CustomAssert.assertNotEquals((Object) actual, (Object) expected, message);
	}
	
	public void customAssertTrue(boolean condition, String message) {
		if (!condition) {
			customFunctions = new CustomFunctions(driver);
			CustomAssert.failNotEquals(Boolean.valueOf(condition),
					Boolean.TRUE, message);
		}
	}

	public void customAssertFalse(boolean condition, String message) {
		if (condition) {
			customFunctions = new CustomFunctions(driver);
			CustomAssert.failNotEquals(Boolean.valueOf(condition),
					Boolean.FALSE, message); // TESTNG-81
		}
	}

	static public void assertEquals(Object actual, Object expected,
			String message) {
		if ((expected == null) && (actual == null)) {
			return;
		}
		if (expected != null) {
			if (expected.getClass().isArray()) {
				CustomAssert.assertArrayEquals(actual, expected, message);
				return;
			} else if (expected.equals(actual)) {
				return;
			}
		}
		CustomAssert.failNotEquals(actual, expected, message);
	}

	static public void assertEquals(Object actual, Object expected) {
		CustomAssert.assertEquals(actual, expected, null);
	}

	public static void failNotEquals(Object actual, Object expected, String message) {
//		System.out.println("************************ CUSTOM ASSERT FAIL ****************************");
		System.out.println(CustomAssert.format(actual, expected, message));
		CustomAssert.fail(CustomAssert.format(actual, expected, message));
	}

	private static void assertArrayEquals(Object actual, Object expected,
			String message) {
		// is called only when expected is an array
		if (actual.getClass().isArray()) {
			int expectedLength = Array.getLength(expected);
			if (expectedLength == Array.getLength(actual)) {
				for (int i = 0; i < expectedLength; i++) {
					Object _actual = Array.get(actual, i);
					Object _expected = Array.get(expected, i);
					try {
						CustomAssert.assertEquals(_actual, _expected);
					} catch (AssertionError ae) {
						CustomAssert.failNotEquals(actual, expected,
								message == null ? "" : message
										+ " (values at index " + i
										+ " are not the same)");
					}
				}
				// array values matched
				return;
			} else {
				CustomAssert.failNotEquals(Array.getLength(actual),
						expectedLength, message == null ? "" : message
								+ " (Array lengths are not the same)");
			}
		}
		CustomAssert.failNotEquals(actual, expected, message);
	}

	static String format(Object actual, Object expected, String message) {		

		String formatted = "";
		if (null != message) {
			formatted = message + " ";
		}
		String messageString = formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT;
		return messageString;
//		try {
//			String screenshot = customFunctions.takeScreenshot("Screenshots",
//					"Result", "vspxseldev01.web.hbpub.net", "pxselenium",
//					"p8s31en7*m", uploadScreenshot);
//			if (screenshot.equals("")) return messageString;
//			else {
//				return "\nScreenshot Url : "
//					+ screenshot
//					+ "\n"
//					+ "****************************************************\n"
//					+ messageString;
//			}
//			} catch (Exception e) {
//			return messageString;
//		}

	}

}
