package automation.utils;

import org.json.JSONObject;

public class JSONReader {
	public static String getValue(String response,String value)
	{
		JSONObject myResponse = new JSONObject(response);
		if(value.contains("."))
		{
			String Sub = value.substring(0, value.indexOf("."));
			value = value.replace(Sub+"." , "");
			return getValue(myResponse.get(Sub).toString(),value);
		}
		else
		{
			return myResponse.get(value).toString();
		}
	}
}
