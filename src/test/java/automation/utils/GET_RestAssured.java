package automation.utils;

import org.json.simple.parser.ParseException;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class GET_RestAssured {

	public static JsonPath verifyLeadIsSubmittedInDB(String URL,String param, String key, String value,String first_name,String last_name) throws ParseException {
	
	//	RestAssured.baseURI="https://ebng5r4uy1.execute-api.us-west-2.amazonaws.com";
		Response res=RestAssured.given().relaxedHTTPSValidation("TLSv1.2").header(key,value).when().get(URL,param);

		res.then().assertThat().statusCode(200);
	//	System.out.println("response:"+res.asString());
		
		JsonPath obj1=res.jsonPath();
		
		return obj1;
	}
	
}
