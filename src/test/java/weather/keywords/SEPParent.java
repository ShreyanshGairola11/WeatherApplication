package weather.keywords;

import java.net.URL;

import org.openqa.selenium.WebDriver;

import automation.getpageobjects.GetPage;

public class SEPParent extends GetPage {

	String pageName;
	String downloadPath = "D:\\seleniumdownloads";
	String fileName = "Unofficial College Transcript.pdf";
	public static final URL scriptUrl = SEPParent.class.getResource("/axe.min.js");

	public SEPParent(WebDriver driver, String pageName) {
		super(driver, pageName);
		this.pageName = pageName;
	}
}
