package weather.test;

import static automation.utils.YamlReader.getYamlValue;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import automation.TestSessionInitiator;

public class VAMergeSmoke {
	String appUrl,city;
	TestSessionInitiator test;
	List<String> ndtvdata,openweatherdata;
	
	@BeforeClass
	public void start_test_Session() {
		test = new TestSessionInitiator();
		initVar();
	}


	public void initVar() {
		appUrl = getYamlValue("appURL");
		city="Dehra Dun";
	}

	@Test
	public void Test01_Launch_Application() {
		test.launchApplication(appUrl);
		test.NDTVPage.verifyUserIsOnHomePage();
		test.NDTVPage.openWeatherPage();
	}
	
	@Test
	public void Test02_Validate_Weather_Info() {
		ndtvdata = test.NDTVPage.getWeatherData(city);
		openweatherdata = test.NDTVPage.getWeatherDataFromApi(city);
	}
	
	@Test
	public void Test03_Validate_Weather_Info_Difference() {
		test.NDTVPage.validateDifference(ndtvdata,openweatherdata); 
	}
	

	@AfterClass
	public void stop_test_session() {
		//test.closeBrowserSession();
	}
}
