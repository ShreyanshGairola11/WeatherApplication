package automation.utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIhit {
	public static InputStream hitapi(String Apiurl,String param1) throws IOException
	{
		Apiurl = Apiurl.replace("{param1}",param1);
		
		return hit(Apiurl);
	}
	
	public static InputStream hitapi(String Apiurl,String param1,String param2) throws IOException
	{
		Apiurl = Apiurl.replace("{param1}",param1);
		Apiurl = Apiurl.replace("{param2}",param2);
		
		return hit(Apiurl);
	}
	
	
	
	public static InputStream hit(String Apiurl) throws IOException
	{
		URL url = new URL(Apiurl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("GET");
		
		System.out.println("Response Code is "+con.getResponseCode()+"\n");
		
		return con.getInputStream();
	}
}
