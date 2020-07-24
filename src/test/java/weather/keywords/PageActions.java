package weather.keywords;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

import automation.utils.APIhit;

public class PageActions extends SEPParent {
	String tier;
	SoftAssert softly;

	public PageActions(WebDriver driver) {
		super(driver, "PageActions");
		softly = new SoftAssert();
	}

	public void launchApplication(String Url) {
		driver.get(Url);
	}

	public void verifyUserIsOnHomePage() {
		wait.waitForPageToLoadCompletely();
		hardWait(20);
		wait.waitForElementToBeClickableDynamic(element("alert_Nothanks"));
		element("alert_Nothanks").click();
		wait.waitForElementToBeVisible(element("header"));
		assertEquals(element("header").getAttribute("src"),
				"https://drop.ndtv.com/homepage/images/ndtvlogo23march.png");
		msg.log("HomePage is Opened");
	}

	public void openWeatherPage() {
		element("navbar_extend").click();
		wait.waitForElementToBeVisible(element("nav_weather", "WEATHER"));
		element("nav_weather", "WEATHER").click();
	}

	public List<String> getWeatherData(String city) {
		wait.waitForPageToLoadCompletely();

		String cityEle = element("generic_input", city).getAttribute("onclick");

		System.out.println(cityEle);
		cityEle = cityEle.replace("javascript:dropCityMarker(this, \"{", "");
		cityEle = cityEle.replace("\\", "");

		String[] res = cityEle.split("[,]", 0);

		List<String> list2 = new ArrayList<>();

		for (String myStr : res) {
			list2.add(myStr);
		}

		
		List<String> list = new ArrayList<>();
		list.add(findValueInList(list2, "lat"));
		list.add(findValueInList(list2, "lng"));
		list.add(findValueInList(list2, "temp_c"));
		list.add(findValueInList(list2, "Humidity"));
		list.add(findValueInList(list2, "condition"));

		return list;
	}

	private String findValueInList(List<String> list, String value) {
		ListIterator<String> iterator1 = list.listIterator();
		while (iterator1.hasNext()) {
			String str1 = iterator1.next();
			if (str1.contains(value)) {
				str1 = str1.replace(value, "");
				str1 = str1.replace(":", "");
				str1 = str1.replace("\"", "");
				return str1;
			}
		}
		return "Not Found";
	}

	public List<String> getWeatherDataFromApi(String city) {
		BufferedReader in = null;
		String inputline;

		APIhit api = new APIhit();

		InputStream responsestream = null;
		try {
			responsestream = api.hitapi("http://api.openweathermap.org/data/2.5/weather?q={param1}&appid={param2}",
					city, "7fe67bf08c80ded756e598d6f8fedaea");
		} catch (IOException e) {
			e.printStackTrace();
		}

		in = new BufferedReader(new InputStreamReader(responsestream));

		StringBuffer response = new StringBuffer();
		try {
			while ((inputline = in.readLine()) != null) {
				response.append(inputline);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		automation.utils.JSONReader jsreader = new automation.utils.JSONReader();

		System.out.println(response.toString());
		List<String> list = new ArrayList<>();
		
		list.add(jsreader.getValue(response.toString(), "coord.lat"));
		list.add(jsreader.getValue(response.toString(), "coord.lon"));
		list.add(jsreader.getValue(response.toString(), "main.temp"));
		list.add(jsreader.getValue(response.toString(), "main.humidity"));

		// to read array

		String jsarray = jsreader.getValue(response.toString(), "weather");
		String name = null;
		JSONArray jsonarray = new JSONArray(jsarray);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			name = jsonobject.getString("main");
		}

		list.add(name);

		return list;
	}

	public void validateDifference(List<String> data1, List<String> data2) {
		ListIterator<String> iterator1 = data1.listIterator();
		ListIterator<String> iterator2 = data2.listIterator();

		while (iterator1.hasNext()) {
			String str1 = iterator1.next();
			String str2 = iterator2.next();
			System.out.println("Value is : " + str1 + "    " + str2);
			if (str1.contentEquals(str2)) {
				msg.pass("Value is Equal");
			} else {
				msg.fail("Value is Not Equal");
			}
		}
	}
}
