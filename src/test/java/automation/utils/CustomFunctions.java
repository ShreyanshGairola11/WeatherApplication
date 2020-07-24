package automation.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.openqa.selenium.Alert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;

public class CustomFunctions {

	static WebDriver driver;
	static String productName;

	public CustomFunctions(WebDriver driver) {
		CustomFunctions.driver = driver;
	}

	public static void setProduct(String product) {
		productName = product;
	}
	
	public static String getRandomNumber() {
		String randomNum;
		int num;
		int m = (int) Math.pow(14, 9 - 1);
		num = m + new Random().nextInt(9 * m);
		randomNum = String.valueOf(num);
		return randomNum;
	}

	protected Alert switchToAlert() {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		return wait.until(ExpectedConditions.alertIsPresent());
	}

	public void handleAlert() {
		try {
			switchToAlert().accept();
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			System.out.println("No Alert window appeared...");
		}
	}

	/*
	 * Hard wait for number of seconds provided
	 * 
	 */
	public static void hardWait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public String getCurrentDayDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getDateTimeString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_a");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getDateTimeStringForReport() {
		DateFormat dateFormat = new SimpleDateFormat("h a z,MMMM dd,yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	/*
	 * 
	 * Takes a String and returns a String appended with current timestamp
	 */
	public static String getStringWithTimestamp(String name) {
		Long timeStamp = System.currentTimeMillis();
		return name + "_" + timeStamp;
	}

	/**
	 * If current month is January: returns 'Jan' if monthOffset is 0 returns
	 * 'Feb' if monthOffset is 1 returns 'Dec' if monthOffset 0s -1
	 */
	public String getMonth(int monthOffset) {
		DateFormat dateFormat = new SimpleDateFormat("MMM");
		Date newDate = DateUtils.addMonths(new Date(), monthOffset);
		String date_time = dateFormat.format(newDate);
		return date_time;
	}

	public String getDateInFormat(String format) {
		Date dNow = new Date();
		@SuppressWarnings("unused")
		String date = "";
		SimpleDateFormat ft = new SimpleDateFormat(format);
		// if(ft.format(dNow).startsWith("0")) date =
		// ft.format(dNow).substring(1, ft.format(dNow).length());
		// return date;
		return ft.format(dNow);
	}

	/**
	 * If current month is January: returns 'January' if monthOffset is 0
	 * returns 'February' if monthOffset is 1 returns 'December' if monthOffset
	 * 0s -1
	 */
	public String getMonthFullName(int monthOffset) {
		DateFormat dateFormat = new SimpleDateFormat("MMMM");
		Date newDate = DateUtils.addMonths(new Date(), monthOffset);
		String date_time = dateFormat.format(newDate);
		return date_time;
	}

	/**
	 * If current month is January, 2015: returns '2015' if monthOffset is 0
	 * returns '2016' if monthOffset is 12 returns '2014' if monthOffset 0s -1
	 */
	public String getYear(int monthOffset) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		Date newDate = DateUtils.addMonths(new Date(), monthOffset);
		String date_time = dateFormat.format(newDate);
		return date_time;
	}

	/*
	 * Takes screenshot. Creates Screenshot in path/Screenshots/<Date> with name
	 * <DateTime>_<Testname>.png
	 * 
	 */
	public String takeScreenshot(String folderName, String testName, String ftpUrl, String userid, String password,
			String uploadImage) {
		// handleAlert();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_a");
		Date date = new Date();
		String date_time = dateFormat.format(date);
		testName = testName.substring(testName.lastIndexOf(".") + 1);

		File file = new File(System.getProperty("user.dir") + File.separator + folderName + File.separator
				+ "Screenshots" + File.separator + date_time);
		boolean exists = file.exists();
		if (!exists) {
			new File(System.getProperty("user.dir") + File.separator + folderName + File.separator + "Screenshots"
					+ File.separator + testName + File.separator + date_time).mkdir();
		}

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			String saveImgFile = System.getProperty("user.dir") + File.separator + folderName + File.separator
					+ "Screenshots" + File.separator + testName + File.separator + date_time + File.separator
					+ "screenshot.png";
			System.out.println("Save Image File Path : " + saveImgFile);
			FileUtils.copyFile(scrFile, new File(saveImgFile));
			saveImgFile = uploadScreenshotToServer(ftpUrl, userid, password, testName, saveImgFile);
			return saveImgFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void takeScreenshot(String path, String testname) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_a");
		Date date = new Date();
		String date_time = dateFormat.format(date);
		File file = new File(
				path + File.separator + "Screenshots" + File.separator + testname + File.separator + date_time);
		boolean exists = file.exists();
		if (!exists) {
			new File(path + File.separator + "Screenshots" + File.separator + testname + File.separator + date_time)
					.mkdir();
		}

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			String saveImgFile = path + File.separator + "Screenshots" + File.separator + testname + File.separator
					+ date_time + File.separator + "screenshot.png";
			// System.out.println("Save Image File Path : " + saveImgFile);
			FileUtils.copyFile(scrFile, new File(saveImgFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// On Test Failure calls function which takes screenshot
	public String takeScreenShotOnException(String folderName, String testname, ITestResult result,
			String takeScreenshot, String ftpUrl, String userid, String password, String uploadImage) {
		if (result.getStatus() == ITestResult.FAILURE) {
			if (takeScreenshot.equalsIgnoreCase("true")) {
				try {
					if (driver != null) {
						return takeScreenshot(folderName, testname, ftpUrl, userid, password, uploadImage);
					}
				} catch (Exception ex) {
					Reporter.log("Driver is null while taking screen shot", true);
				}
			}
		}
		return "";
	}

	/*
	 * Uploads screenshot to
	 * <ftpurl>/selenium/test/resourcepagetest/<datetime>/screenshot.png and
	 * prints the image Url
	 */
	public String uploadScreenshotToServer(String serverUrl, String userid, String password, String testname,
			String imageFilePath) {
		System.out.println("Uploading screenshot to server...");
		FTPClient client = new FTPClient();
		FileInputStream fis = null;
		String imageFileUrl = "ftp://geek:password@" + serverUrl + ":54218";
		try {
			client.connect(serverUrl, 54218);
			showServerReply(client);
			client.enterLocalPassiveMode();
			client.login(userid, password);
			showServerReply(client);
			String workingDirectory = client.printWorkingDirectory();
			// System.out.println("Current working directory is: " +
			// workingDirectory);
			List<String> dirNames = Arrays.asList("ftp", "selenium", "test", productName);
			for (int i = 0; i < dirNames.size(); i++) {
				client.makeDirectory(workingDirectory + dirNames.get(i));
				workingDirectory = workingDirectory + dirNames.get(i) + "/";
				imageFileUrl = imageFileUrl + "/" + dirNames.get(i);
			}
			client.setFileType(FTPClient.BINARY_FILE_TYPE);
			client.changeWorkingDirectory(workingDirectory);
			File srcFile = new File(imageFilePath);
			System.out.println(imageFilePath);
			fis = new FileInputStream(srcFile);
			String fileName = getDateTimeString() + "_screenshot.png";
			client.storeFile(fileName, fis);
			showServerReply(client);
			client.logout();
			showServerReply(client);
			imageFileUrl += "/" + fileName;
			System.out.println("************************************************************************************");
			System.out.println("Screenshot URL : " + imageFileUrl);
			System.out.println("************************************************************************************");
			return imageFileUrl;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	private static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				System.out.println("SERVER: " + aReply);
			}
		}
	}

	private List<String> getListOfSubdirectory(String path) {
		File pageObjDir = new File(path);
		File[] listOfFiles = pageObjDir.listFiles();
		List<String> subdirList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				subdirList.add(listOfFiles[i].getName());
			}
		}
		return subdirList;
	}

	private List<String> getListOfFiles(String path) {
		File pageObjDir = new File(path);
		File[] listOfFiles = pageObjDir.listFiles();
		List<String> fileList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileList.add(listOfFiles[i].getName());
			}
		}
		return fileList;
	}

	private List<String> getListOfElementNames(String actionFilePath) throws FileNotFoundException {
		List<String> elemNames = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(actionFilePath));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				List<String> elemNamesInLine = getElementNamesFromLine(line);
				if (elemNamesInLine != null) {
					for (int i = 0; i < elemNamesInLine.size(); i++) {
						if (!elemNames.contains(elemNamesInLine.get(i))) {
							elemNames.add(elemNamesInLine.get(i));
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return elemNames;
	}

	private List<String> getElementNamesFromLine(String line) {
		List<String> matches = new ArrayList<String>();
		String pattern = "(isElementDisplayed|element|elements|verifyCheckBoxIsChecked|waitForElementToBeVisible|"
				+ "waitAndClick|waitScrollAndClick|waitAndScrollToElement|fillText|verifyTextOfElementIsCorrect|"
				+ "verifyAttributeOfElementIsCorrect|waitForElementToDisappear|getTextFirstElementOfList|"
				+ "verifyRadioButtonSelected|clickElementIfVisible|verifyElementNotDisplayed|verifyBackgroundColorHexCode)[(][\"](.*?)[\"]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		while (m.find()) {
			matches.add(m.group(2));
		}
		return matches;
	}

	public void reportPageObjectDescrepancies(String projDir, String product) {
		System.out.println("Reporting any descrepencies in Page Object Files...");
		String pageObjectDir = projDir + File.separator + "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "PageObjectRepository" + File.separator + product.toUpperCase();
		String keywordDir = projDir + File.separator + "src" + File.separator + "test" + File.separator + "java"
				+ File.separator + "com" + File.separator + "qait" + File.separator + product + File.separator
				+ "keywords";
		List<String> envList = getListOfSubdirectory(pageObjectDir);
		List<String> actionFileNames = getListOfFiles(keywordDir);
		// printList(envList);
		// printList(actionFileNames);
		String result = "";
		for (int i = 0; i < actionFileNames.size(); i++) {
			try {
				if (actionFileNames.get(i).equals("")) {
					continue;
				}
				String actionFilePath = keywordDir + File.separator + actionFileNames.get(i);
				String pageObjectFileName = getPageObjectFileName(actionFilePath) + ".txt";
				List<String> listOfElemsInActionFile = getListOfElementNames(actionFilePath);
				for (int j = 0; j < envList.size(); j++) {
					// System.out.println("Action file name : " +
					// actionFileNames.get(i));
					// System.out.println("Environment : " + envList.get(j));
					String pageObjectFilePath = pageObjectDir + File.separator + envList.get(j) + File.separator
							+ pageObjectFileName;
					String resultPageObjFile = getDetailsOfMissingDuplicateExtraElements(pageObjectFilePath,
							listOfElemsInActionFile);
					if (!resultPageObjFile.equals("")) {
						result += "*****************************************\n";
						result += "Page Object File Name : " + pageObjectFileName + "\n";
						result += "Environment : " + envList.get(j) + "\n";
						result += "*****************************************\n";
						result += resultPageObjFile + "\n";
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (result.equals("")) {
			System.out.println("All Page Object Files have correct Element Names\n");
		} else {
			System.out.println(result);
		}
	}

	public void debugPageObjects(String projDir, String debug, String product) {
		if (debug.equalsIgnoreCase("yes")) {
			reportPageObjectDescrepancies(projDir, product);
		}
	}

	private String getDetailsOfMissingDuplicateExtraElements(String pageObjectFilePath,
			List<String> listOfElemsInActionFile) throws FileNotFoundException {
		String returnStr = "";
		for (int i = 0; i < listOfElemsInActionFile.size(); i++) {
			String elem = listOfElemsInActionFile.get(i);
			int occurance = getOccuranceCountOfElement(pageObjectFilePath, elem);
			if (occurance == 0) {
				returnStr += "Element '" + elem + "' is missing\n";
			} else if (occurance > 1) {
				returnStr += "Element '" + elem + "' is present " + occurance + " times\n";
			}
		}
		return returnStr;
	}

	private int getOccuranceCountOfElement(String pageObjectFilePath, String elem) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(pageObjectFilePath));
		String line;
		int count = 0;
		try {
			while ((line = br.readLine()) != null) {
				if (line.startsWith(elem + ":")) {
					count++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	private String getPageObjectFileName(String actionFilePath) throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(actionFilePath));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String returnVal = getPageObjectFileNameFromLine(line);
				if (!returnVal.equals("")) {
					br.close();
					return returnVal;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getPageObjectFileNameFromLine(String line) {
		String pattern = "super[(]driver,\\s?[\"](.*?)[\"][)]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		while (m.find()) {
			return m.group(1);
		}
		return "";
	}

	/**
	 * Get system date in MM/dd/YY format
	 *
	 * @return
	 */
	public String getSystemDate() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy");
		return ft.format(dNow);
	}

	/**
	 * Get current date in M/d/YYYY format
	 *
	 * @return
	 */
	public String getCurrentDate() {
		Date dNow = new Date();
		@SuppressWarnings("unused")
		String date = "";
		SimpleDateFormat ft = new SimpleDateFormat("M/d/yyyy");
		// if(ft.format(dNow).startsWith("0")) date =
		// ft.format(dNow).substring(1, ft.format(dNow).length());
		// return date;
		return ft.format(dNow);
	}

	public String getPreviousDate() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("MMMM yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		dNow = cal.getTime();
		return ft.format(dNow);
	}

	public static void updateFailScriptFile(String scriptPath, ITestResult testResult, String screenshot) {
		System.out.println("script path: " + scriptPath);
		String scriptName = scriptPath.substring(scriptPath.lastIndexOf(".") + 1);
		System.out.println("script name: " + scriptName);
		String testStep = testResult.getName().trim();
		System.out.println("testStep: " + testStep);
		testStep = testStep.substring(testStep.indexOf("_") + 1);
		String failureMessage = testResult.getThrowable().getMessage();
		System.out.println("**>>Failure message printed in Custom Function :" + failureMessage + " <<**");
		if (failureMessage == null) {
			failureMessage = "null";
		}
		if (failureMessage.length() > 400) {
			failureMessage = failureMessage.substring(0, 400) + "...";
		}
		failureMessage = testStep + "|||" + failureMessage;
		failureMessage = failureMessage.replaceAll("[,]", " ");
		failureMessage = failureMessage.replaceAll("[;]", " ");
		failureMessage = failureMessage.replaceAll("(\\r|\\n)", " ");
		failureMessage = failureMessage.replaceAll("[']", "\"");

		String dirPath = "C:/Users/Administrator/Desktop/FTP/ftp";
		File f = new File(dirPath);
		if (!f.exists()) {
			dirPath = System.getProperty("user.home") + File.separator + "Desktop";
		}
		String filePath = dirPath + File.separator + productName + ".csv";
		try {
			f = new File(filePath);
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			System.out.println(
					"Exec File Line : " + scriptName + "," + testStep + "," + "" + screenshot + "," + failureMessage);
			out.println(scriptName + "," + testStep + "," + "" + screenshot + ", " + failureMessage);
			out.close();
			updateScreenshotLocationFile(productName, scriptName, screenshot);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void updateFailScriptFileWithoutScreenshot(String scriptPath, ITestResult testResult) {
		System.out.println("script path: " + scriptPath);
		String scriptName = scriptPath.substring(scriptPath.lastIndexOf(".") + 1);
		System.out.println("script name: " + scriptName);
		String testStep = testResult.getName().trim();
		System.out.println("testStep: " + testStep);
		testStep = testStep.substring(testStep.indexOf("_") + 1);
		String failureMessage = testResult.getThrowable().getMessage();
		System.out.println("**>>Failure message printed in Custom Function :" + failureMessage + " <<**");
		if (failureMessage == null) {
			failureMessage = "null";
		}
		if (failureMessage.length() > 400) {
			failureMessage = failureMessage.substring(0, 400) + "...";
		}
		failureMessage = testStep + "|||" + failureMessage;
		failureMessage = failureMessage.replaceAll("[,]", " ");
		failureMessage = failureMessage.replaceAll("[;]", " ");
		failureMessage = failureMessage.replaceAll("(\\r|\\n)", " ");
		failureMessage = failureMessage.replaceAll("[']", "\"");
	}

	public static void updateFailScriptFile(String scriptPath, ITestResult testResult, String screenshot,
			String testStepName) {
		System.out.println("script path: " + scriptPath);
		String scriptName = scriptPath.substring(scriptPath.lastIndexOf(".") + 1);
		System.out.println("script name: " + scriptName);
		String testStep = testStepName.trim();
		System.out.println("testStep: " + testStep);
		testStep = testStep.substring(testStep.indexOf("_") + 1);
		String failureMessage = testResult.getThrowable().getMessage();
		System.out.println("**>>Failure message printed in Custom Function :" + failureMessage + " <<**");
		if (failureMessage == null) {
			failureMessage = "null";
		}
		if (failureMessage.length() > 400) {
			failureMessage = failureMessage.substring(0, 400) + "...";
		}
		failureMessage = testStep + "|||" + failureMessage;
		failureMessage = failureMessage.replaceAll("[,]", " ");
		failureMessage = failureMessage.replaceAll("[;]", " ");
		failureMessage = failureMessage.replaceAll("(\\r|\\n)", " ");
		failureMessage = failureMessage.replaceAll("[']", "\"");

		String dirPath = "D:/ftpfiles";
		File f = new File(dirPath);
		if (!f.exists()) {
			dirPath = System.getProperty("user.home") + File.separator + "Desktop";
		}
		String filePath = dirPath + File.separator + productName + ".csv";
		try {
			f = new File(filePath);
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			System.out.println(
					"Exec File Line : " + scriptName + "," + testStep + "," + "" + screenshot + "," + failureMessage);
			out.println(scriptName + "," + testStep + "," + "" + screenshot + ", " + failureMessage);
			out.close();
			updateScreenshotLocationFile(productName, scriptName, screenshot);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	private static void updateScreenshotLocationFile(String prodName, String scriptName, String screenshot) {
		String ftpDirPath = "ftp://geek:password@10.78.103.55:54218/ftp/selenium/test/automation/screenshotLinks";
		String ftpUrl = "10.78.103.55";
		String username = "geek";
		String password = "password";
		String fileData = getLinesFromFTPFile(ftpDirPath + "/" + prodName + ".csv");
		String newData = fileData.trim() + "\n" + scriptName + ", " + screenshot;
		writeToFTPFile(ftpUrl, username, password, "/ftp/selenium/test/automation/screenshotLinks/" + prodName + ".csv",
				newData);
	}

	// getLinesFromFTPFile(ftpDirPath + "/file.csv");
	public static String getLinesFromFTPFile(String filePath) {
		URL url;
		String line;
		String data = "";
		try {
			url = new URL(filePath);
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = in.readLine()) != null) {
				if (line.trim().equals("")) {
					continue;
				}
				data += line.trim() + "\n";
			}
			in.close();
		} catch (IOException e) {
			System.out.println("[Warning] : Unable to read FTP file path '" + filePath + "'");
		}
		return data;
	}

	public static void writeToFTPFile(String ftpUrl, String username, String password, String path, String data) {
		FTPClient client = new FTPClient();
		try {
			client.connect(ftpUrl, 54218);
			client.login(username, password);

			OutputStream out = client.storeFileStream(path);

			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
			bufferedWriter.write(data);

			bufferedWriter.close();
			out.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readFile(String filePath) {
		String data = "";
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.trim().equals("")) {
					continue;
				}
				data += strLine.trim() + "\n";
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void deleteFTPFile(String deleteFilePath) {
		FTPClient client = new FTPClient();
		String ftpUrl = "10.78.103.55";
		String username = "geek";
		String password = "password";
		try {
			client.connect(ftpUrl, 54218);
			client.login(username, password);
			client.deleteFile(deleteFilePath);
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred while deleting FTP File!!!");
		}
	}

	// public static void main(String[] args){
	// checkFTPFileExists("/ftp/selenium/test/automation/executions/",
	// "KUPORTAL_test_SMOKE.csv");
	// }
	public static boolean checkFTPFileExists(String filePath, String fileName) {
		FTPClient client = new FTPClient();
		String ftpUrl = "10.78.103.55";
		String username = "geek";
		String password = "password";
		try {
			client.connect(ftpUrl, 54218);
			client.login(username, password);
			client.changeWorkingDirectory(filePath);
			FTPFile[] fileList = client.listFiles();
			for (FTPFile file : fileList) {
				String details = file.getName();
				if (details.equals(fileName)) {
					return true;
				}
			}
			client.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	public static String getTestRunDateTimeString() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		Date date = new Date();
		return dateFormat.format(date);
	}

	// public static void updateTestRun(String spreadSheetId, String testName,
	// String col1, String col2,
	// ITestResult result) {
	// String scriptName = testName.substring(testName.lastIndexOf(".") + 1);
	// String testStep = result.getName().toString().trim();
	// testStep = testStep.substring(testStep.indexOf("_") + 1);
	//
	// SheetsAPI sheetsObj = new SheetsAPI(spreadSheetId);
	// sheetsObj.getSheetsService();
	// int sheetIndex = sheetsObj.getSheetIndex(scriptName);
	// // System.out.println("Sheet Index : " + sheetIndex);
	// // sheetsObj.readSheetData(sheetIndex);
	// String tcids = sheetsObj.findData(sheetIndex, col1, testStep, col2);
	// System.out.println("TC ID is : " + tcids);
	//
	// String[] tcidList = tcids.split("[:]");
	//
	// if (!tcids.equals("")) {
	// try {
	// for (int i = 0; i < tcidList.length; i++) {
	// System.out.println("Updating TestRail TestRun for TestCase : " +
	// tcidList[i]);
	// if (result.getStatus() == ITestResult.FAILURE) {
	// TestRail.testRailPostMethod(5, tcidList[i].trim());
	// } else if (result.getStatus() == ITestResult.SUCCESS) {
	// TestRail.testRailPostMethod(1, tcidList[i].trim());
	// } else if (result.getStatus() == ITestResult.SKIP) {
	// TestRail.testRailPostMethod(3, tcidList[i].trim());
	// }
	// }
	// } catch (MalformedURLException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (APIException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }
	// }
	public static int getRowsFromCSV(String filename) {
		int count = 0;
		try {
			InputStream is = null;
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(System.getProperty("user.dir") + File.separator + filename));
			String input = "";
			while ((input = bufferedReader.readLine()) != null) {
				count++;
			}
		} catch (IOException ex) {
			Logger.getLogger(CustomFunctions.class.getName()).log(Level.SEVERE, null, ex);
		}
		return count;
	}

	public static int getColumnFromCSV(String filename) {
		FileReader fr;
		int count = 0;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + filename);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				String[] array1 = s.split(",");
				count = array1.length;
				break;
			}
			fr.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return count;
	}

	public static String[][] extractContentFromCSV(String[][] desiredArray, String filename) {
		String[][] data1 = desiredArray;
		FileReader fr;
		int row = 0, column = 0;
		int counter = 0;
		try {
			fr = new FileReader(System.getProperty("user.dir") + File.separator + filename);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				if (counter != 0) { // added this step so that code does not
									// pick first row of csv file
					String[] array1 = s.split(",");
					for (int i = 0; i < array1.length; i++) {
						data1[row][column] = array1[i];
						column++;
					}
					row++;
					column = 0;
				}
				counter++;
			}

			fr.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return data1;
	}

	public static String[] splitAndExtractAllStrings(String splitBy, String inputString) {

		String[] outputString = inputString.split("\\" + splitBy);

		System.out.println("Array lenght : " + outputString.length);

		for (int i = 0; i < outputString.length; i++) {
			Msg.logMessage("String " + (i + 1) + " : " + outputString[i].trim());
		}
		return outputString;

	}

	public static String RemoveGivenLinesFromFTPFile(String filePath, String Text) {
		URL url;
		String line;
		String data = "";
		try {
			url = new URL(filePath);
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = in.readLine()) != null) {
				if (line.trim().equals("") || line.trim().equals(Text)) {
					continue;
				}
				data += line.trim() + "\n";
			}
			in.close();
		} catch (IOException e) {
			System.out.println("[Warning] : Unable to read FTP file path '" + filePath + "'");
		}
		return data;
	}

	public static String getStringWithCurrentDateAndTimeStamp(String name) {
		DateFormat dateFormat = new SimpleDateFormat("dd MMM");
		String current = name + dateFormat.format(new Date());
		current = current.replaceAll("\\s", "");
		Long timeStamp = System.currentTimeMillis();
		return current + "_" + timeStamp;
	}

	public static String getCurrentDateWithDayAndMonthOnly() {
		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		return dateFormat.format(new Date());
	}
}
