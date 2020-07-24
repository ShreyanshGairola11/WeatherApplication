package automation;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class MultipleExecutionOfXML {

	public static void main(String[] args) {
		runmultiple(1, "TESTNG_sep_core.xml");
		runmultiple(1, "TESTNG_sep_military.xml");
		runmultiple(1, "TESTNG_sep_international.xml");
	}

	public static void runmultiple(int nooftimes, String filename) {
		for (int i = 0; i < nooftimes; i++) {
			List<String> suites = new ArrayList<String>();
			suites.add("src/test/resources/testngxmls/sepnew/" + filename); 
																			

			TestNG tng = new TestNG();
			tng.setTestSuites(suites);

			tng.run();
		}
	}
}