package automation.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.testng.IInvokedMethod;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

import automation.TestSessionInitiator;
import automation.utils.ConfigPropertyReader;

public class CustomReportCreator implements IReporter {

	// private static final Logger L = Logger.getLogger(test123455.class);

	// ~ Instance fields ------------------------------------------------------

	private PrintWriter m_out;

	private int m_row;

	private Integer m_testIndex;

	private int m_methodIndex;

	private Scanner scanner;
	TestSessionInitiator test;

	// ~ Methods --------------------------------------------------------------

	/** Creates summary of the run */
	@Override
	public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
		String SuiteName = "EmailableReport";
		String tier = "";
		try {
			if (System.getProperty("tier") == null || System.getProperty("tier").isEmpty()
					|| System.getProperty("tier") == "")
				tier = ConfigPropertyReader.getProperty("tier");
			else
				tier = System.getProperty("tier");
		} catch (Exception e) {
			tier = ConfigPropertyReader.getProperty("tier");
		}
		String appurl = null;
		if (tier.equalsIgnoreCase("prod")) {
			tier = "Production";
			appurl = "http://pubs.acs.org";
		} else if (tier.equalsIgnoreCase("staging")) {
			tier = "Master-Pubstest";
			appurl = "http://master-pubstest.acs.org";
		} else if (tier.equalsIgnoreCase("pubstest")) {
			tier = "Pubstest";
			appurl = "http://pubstest.acs.org";
		}
		for (ISuite suite : suites)
			SuiteName = suite.getName();

		try {
			m_out = createWriter(outdir, SuiteName);
		} catch (IOException e) {
			System.out.println("Sorry, can't create the custome output file as file creation has thrown exception");
			return;
		}

		startHtml(m_out, tier, appurl);
		generateSuiteSummaryReport(suites);
		generateMethodSummaryReport(suites);
		generateMethodDetailReport(suites);
		endHtml(m_out);
		m_out.flush();
		m_out.close();
	}

	protected PrintWriter createWriter(String outdir, String SuiteName) throws IOException {
		File f = new File(outdir);
		for (File file : f.listFiles())
			if (file.getName().contains("_Report"))
				file.delete();
		new File(outdir).mkdirs();
		if (SuiteName.contains("."))
			SuiteName = SuiteName.substring(SuiteName.lastIndexOf(".") + 1, SuiteName.length());
		return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, SuiteName + "_Report" + ".html"))));
	}

	/**
	 * Creates a table showing the highlights of each test method with links to
	 * the method details
	 */
	protected void generateMethodSummaryReport(List<ISuite> suites) {
		int errorSize;
		m_methodIndex = 0;
		errorSize = startResultSummaryTable("methodOverview", suites);
		int testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() > 1) {
				titleRow(suite.getName(), 5);
			}
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				String testName = testContext.getName();
				m_testIndex = testIndex;
				resultSummary(suite, testContext.getFailedConfigurations(), testName, "failed",
						" (configuration methods)", errorSize);
				resultSummaryForFailure(suite, testContext.getFailedTests(), testName, "failed", "");
				resultSummary(suite, testContext.getSkippedConfigurations(), testName, "skipped",
						" (configuration methods)", errorSize);
				resultSummary(suite, testContext.getSkippedTests(), testName, "skipped", "", errorSize);
				resultSummary(suite, testContext.getPassedTests(), testName, "passed", "", errorSize);
				testIndex++;
			}
		}
		m_out.println("</table>");
	}

	private void resultSummaryForFailure(ISuite suite, IResultMap tests, String testname, String style,
			String details) {

		if (tests.getAllResults().size() > 0) {

			StringBuffer buff = new StringBuffer();
			String lastClassName = "";
			int mq = 0;
			int cq = 0;
			int i = 1;
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				m_row += 1;
				m_methodIndex += 1;
				ITestClass testClass = method.getTestClass();
				String className = testClass.getName();
				className = className.substring(className.lastIndexOf(".") + 1, className.length());
				if (mq == 0) {
					String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
					titleRow(testname + " &#8212; " + style + details, 5, id);
					m_testIndex = null;
				}
				if (!className.equalsIgnoreCase(lastClassName)) {

					if (mq > 0) {
						cq += 1;
						m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
						if (mq > 1) {
							m_out.print(" rowspan=\"" + mq + "\"");
						}
						m_out.println(">" + lastClassName + "</td>" + buff);
					}
					mq = 0;
					buff.setLength(0);
					lastClassName = className;
				}
				Set<ITestResult> resultSet = tests.getResults(method);
				long end = Long.MIN_VALUE;
				long start = Long.MAX_VALUE;
				String URL = "";
				String firstLine = "";
				String Parameters = "";
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end) {
						end = testResult.getEndMillis();
					}
					if (testResult.getStartMillis() < start) {
						start = testResult.getStartMillis();
					}

					Throwable exception = testResult.getThrowable();
					List<String> msgs = Reporter.getOutput(testResult);
					boolean hasReporterOutput = msgs.size() > 0;
					boolean hasThrowable = exception != null;
					if (hasThrowable) {
						// if (hasReporterOutput){
						URL = DataPropertFileUtil.getProperty("URLAtFailureOfTest" + testResult.getName() + i);
						if (URL == null) {
							URL = "Unable to capture failure URL : unable to load page";
						}
						// }

						// Getting first line of the stack trace
						String str = Utils.stackTrace(exception, true)[0];
						scanner = new Scanner(str);
						firstLine = scanner.nextLine();
					}

					Object[] parameters = testResult.getParameters();
					boolean hasParameters = parameters != null && parameters.length > 0;
					if (hasParameters) {

						for (Object p : parameters) {
							Parameters = (Utils.escapeHtml(Utils.toString(p)) + " | ");

						}
					}

				}

				mq += 1;
				if (mq > 1) {
					buff.append("<tr class=\"" + style + (cq % 2 == 0 ? "odd" : "even") + "\">");
				}
				String description = method.getDescription();
				String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();

				Date date = new Date(start);
				DateFormat formatter = new SimpleDateFormat("HH:mm:ss z");
				DateFormat formatter1 = new SimpleDateFormat("MMM d yyyy");
				buff.append("<td><a href=\"#m" + m_methodIndex + "\">" + qualifiedName(method, tests) + " "
						+ (description != null && description.length() > 0 ? "(\"" + description + "\")" : "") + "</a>"
						+ (null == testInstanceName ? "" : "<br>(" + testInstanceName + ")") + "</td>"
						+ "<td><b>@URL: <a target=\"_blank\" href=\"" + URL + "\">" + URL + "</a></b><br><br>"
						+ firstLine + "</td>" + "<td style=\"max-width:90px\">" + formatter1.format(date) + "</br>"
						+ formatter.format(date) + "</td>" + "<td class=\"numi\">" + (end - start) / 1000 + "</td>"
						+ "</tr>");
				// increment URL Counter
				i++;
			}
			if (mq > 0) {
				cq += 1;
				m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
				if (mq > 1) {
					m_out.print(" rowspan=\"" + mq + "\"");
				}
				m_out.println(">" + lastClassName + "</td>" + buff);
			}
		}

	}

	/** Creates a section showing known results for each method */
	protected void generateMethodDetailReport(List<ISuite> suites) {
		m_methodIndex = 0;
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				if (r.values().size() > 0) {
					// m_out.println("<h1>" + testContext.getName() + "</h1>");
				}
				resultDetail(testContext.getFailedConfigurations());
				resultDetail(testContext.getFailedTests());
				resultDetail(testContext.getSkippedConfigurations());
				resultDetail(testContext.getSkippedTests());
				resultDetail(testContext.getPassedTests());
			}
		}
	}

	/**
	 * @param tests
	 */
	private void resultSummary(ISuite suite, IResultMap tests, String testname, String style, String details,
			int errorSize) {
		if (tests.getAllResults().size() > 0) {
			StringBuffer buff = new StringBuffer();
			String lastClassName = "", data = "";
			int mq = 0, column = 0;
			int cq = 0;
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				m_row += 1;
				m_methodIndex += 1;
				ITestClass testClass = method.getTestClass();
				String className = testClass.getName();
				className = className.substring(className.lastIndexOf(".") + 1, className.length());

				if (errorSize > 0) { // for removing error message column
					data = "<td></td>";
					column = 5;
				} else
					column = 4;
				if (mq == 0) {
					String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
					titleRow(testname + " &#8212; " + style + details, column, id);
					m_testIndex = null;
				}
				if (!className.equalsIgnoreCase(lastClassName)) {
					if (mq > 0) {
						cq += 1;
						m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
						if (mq > 1) {
							m_out.print(" rowspan=\"" + mq + "\"");
						}
						m_out.println(">" + lastClassName + "</td>" + buff);
					}
					mq = 0;
					buff.setLength(0);
					lastClassName = className;
				}
				Set<ITestResult> resultSet = tests.getResults(method);
				long end = Long.MIN_VALUE;
				long start = Long.MAX_VALUE;
				String Parameters = "";
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end) {
						end = testResult.getEndMillis();
					}
					if (testResult.getStartMillis() < start) {
						start = testResult.getStartMillis();
					}
					Object[] parameters = testResult.getParameters();
					boolean hasParameters = parameters != null && parameters.length > 0;
					if (hasParameters) {

						for (Object p : parameters) {
							Parameters = (Utils.escapeHtml(Utils.toString(p)) + " | ");
						}
					}

				}
				mq += 1;
				if (mq > 1) {
					buff.append("<tr class=\"" + style + (cq % 2 == 0 ? "odd" : "even") + "\">");
				}
				String description = method.getDescription();
				String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();

				Date date = new Date(start);
				DateFormat formatter = new SimpleDateFormat("HH:mm:ss z");
				DateFormat formatter1 = new SimpleDateFormat("MMM d yyyy");
				buff.append("<td><a href=\"#m" + m_methodIndex + "\">" + qualifiedName(method, tests) + " "
						+ (description != null && description.length() > 0 ? "(\"" + description + "\")" : "") + "</a>"
						+ (null == testInstanceName ? "" : "<br>(" + testInstanceName + ")") + "</td>" + data
						+ "<td style=\"max-width:90px\">" + formatter1.format(date) + "</br>" + formatter.format(date)
						+ "</td>" + "<td class=\"numi\">" + (end - start) / 1000 + "</td>" + "</tr>");

			}
			if (mq > 0) {
				cq += 1;
				m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
				if (mq > 1) {
					m_out.print(" rowspan=\"" + mq + "\"");
				}
				m_out.println(">" + lastClassName + "</td>" + buff);
			}
		}
	}

	/** Starts and defines columns result summary table */
	private int startResultSummaryTable(String style, List<ISuite> suites) {
		String errorTab = "";
		int errorSize = 0;
		boolean errorFlag = false;
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				if (testContext.getFailedTests().size() > 0) {
					errorSize = testContext.getFailedTests().size();
					errorFlag = true;
					break;
				}

			}
		}

		if (errorFlag)
			errorTab = "<th>Error Message</th>";
		tableStart(style, "summary");
		m_out.println("<tr><th>Class</th>" + "<th>Method</th>" + errorTab
				+ "<th style=\"max-width:90px\">Start Time</th><th>Total Time<br/>(sec)</th></tr>");
		m_row = 0;
		return errorSize;
	}

	private String qualifiedName(ITestNGMethod method, IResultMap tests) {
		StringBuilder addon = new StringBuilder();
		String[] groups = method.getGroups();

		int length = groups.length;
		if (length > 0 && !"basic".equalsIgnoreCase(groups[0])) {
			addon.append("(");
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					addon.append(", ");
				}
				addon.append(groups[i]);
			}
			addon.append(")");
		}

		System.out.println("LENGTH IS : " + ((ITestResult) tests.getAllResults().toArray()[0]).getParameters().length);
		return "<b>" + method.getMethodName() + "[" + ((ITestResult) tests.getAllResults().toArray()[0]).getParameters()
				+ "]" + "</b> " + addon;
	}

	private void resultDetail(IResultMap tests) {
		List<ITestResult> testResultsList = new ArrayList<ITestResult>(tests.getAllResults());
		Collections.sort(testResultsList, new TestResultsSorter());
		for (ITestResult result : testResultsList) {
			ITestNGMethod method = result.getMethod();
			m_methodIndex++;
			String cname = method.getTestClass().getName();
			cname = cname.substring(cname.lastIndexOf(".") + 1, cname.length());
			m_out.println("<h2 id=\"m" + m_methodIndex + "\">" + cname + ":" + method.getMethodName() + "</h2>");
			Set<ITestResult> resultSet = tests.getResults(method);
			generateForResult(result, method, resultSet.size());
			m_out.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");

		}
	}

	private class TestResultsSorter implements Comparator<ITestResult> {
		@Override
		public int compare(ITestResult obj1, ITestResult obj2) {
			int result = obj1.getTestClass().getName().compareTo(obj2.getTestClass().getName());
			if (result == 0) {
				result = obj1.getMethod().getMethodName().compareTo(obj2.getMethod().getMethodName());
			}
			return result;
		}
	}

	/**
	 * Write the first line of the stack trace
	 * 
	 * @param tests
	 */
	private void getShortException(IResultMap tests) {
		int i = 1;
		for (ITestResult result : tests.getAllResults()) {
			m_methodIndex++;
			Throwable exception = result.getThrowable();
			List<String> msgs = Reporter.getOutput(result);

			boolean hasReporterOutput = msgs.size() > 0;
			boolean hasThrowable = exception != null;
			if (hasThrowable) {
				boolean wantsMinimalOutput = result.getStatus() == ITestResult.SUCCESS;
				if (hasReporterOutput) {
					m_out.print("<h3>"
							+ (wantsMinimalOutput ? "Expected Exception"
									: "Failure" + "</br>" + "@URL: <a target=\"_blank\" href=\""
											+ DataPropertFileUtil
													.getProperty("URLAtFailureOfTest" + result.getName() + i)
											+ "  \">"
											+ DataPropertFileUtil
													.getProperty("URLAtFailureOfTest" + result.getName() + i))
							+ "</a></h3>");
				}

				// Getting first line of the stack trace
				String str = Utils.stackTrace(exception, true)[0];
				scanner = new Scanner(str);
				String firstLine = scanner.nextLine();
				m_out.println(firstLine);
			}
			i++;
		}
	}

	/**
	 * Write all parameters
	 * 
	 * @param tests
	 */
	private void getParameters(IResultMap tests) {

		for (ITestResult result : tests.getAllResults()) {
			m_methodIndex++;
			Object[] parameters = result.getParameters();
			boolean hasParameters = parameters != null && parameters.length > 0;
			if (hasParameters) {

				for (Object p : parameters) {
					m_out.println(Utils.escapeHtml(Utils.toString(p)) + " | ");
				}
			}

		}
	}

	private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
		Object[] parameters = ans.getParameters();
		boolean hasParameters = parameters != null && parameters.length > 0;
		if (hasParameters) {
			tableStart("result", null);
			m_out.print("<tr class=\"param\">");
			for (int x = 1; x <= parameters.length; x++) {
				m_out.print("<th>Param." + x + "</th>");
			}
			m_out.println("</tr>");
			m_out.print("<tr class=\"param stripe\">");
			for (Object p : parameters) {
				m_out.println("<td>" + Utils.escapeHtml(Utils.toString(p)) + "</td>");
			}
			m_out.println("</tr>");
		}
		List<String> msgs = Reporter.getOutput(ans);
		boolean hasReporterOutput = msgs.size() > 0;
		Throwable exception = ans.getThrowable();
		boolean hasThrowable = exception != null;
		if (hasReporterOutput || hasThrowable) {
			if (hasParameters) {
				m_out.print("<tr><td");
				if (parameters.length > 1) {
					m_out.print(" colspan=\"" + parameters.length + "\"");
				}
				m_out.println(">");
			} else {
				m_out.println("<div>");
			}
			if (hasReporterOutput) {
				if (hasThrowable) {
					m_out.println("<h3>Test Messages</h3>");
				}
				for (String line : msgs) {
					m_out.println(line + "<br/>");
				}
			}
			if (hasThrowable) {
				boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;
				if (hasReporterOutput) {
					m_out.println("<h3>" + (wantsMinimalOutput ? "Expected Exception" : "Failure") + "</h3>");
				}
				generateExceptionReport(exception, method);
			}
			if (hasParameters) {
				m_out.println("</td></tr>");
			} else {
				m_out.println("</div>");
			}
		}
		if (hasParameters) {
			m_out.println("</table>");
		}
	}

	protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
		m_out.print("<div class=\"stacktrace\">");
		m_out.print(Utils.stackTrace(exception, true)[0]);
		m_out.println("</div>");
	}

	/**
	 * Since the methods will be sorted chronologically, we want to return the
	 * ITestNGMethod from the invoked methods.
	 */
	private Collection<ITestNGMethod> getMethodSet(IResultMap tests, ISuite suite) {
		List<IInvokedMethod> r = Lists.newArrayList();
		List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();
		for (IInvokedMethod im : invokedMethods) {
			if (tests.getAllMethods().contains(im.getTestMethod())) {
				r.add(im);
			}
		}

		// Arrays.sort(r.toArray(new IInvokedMethod[r.size()]), new
		// TestSorter());
		List<ITestNGMethod> result = Lists.newArrayList();

		// Add all the invoked methods
		for (IInvokedMethod m : r) {
			result.add(m.getTestMethod());
		}

		// Add all the methods that weren't invoked (e.g. skipped) that we
		// haven't added yet
		for (ITestNGMethod m : tests.getAllMethods()) {
			if (!result.contains(m)) {
				result.add(m);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public void generateSuiteSummaryReport(List<ISuite> suites) {
		tableStart("testOverview", null);
		m_out.print("<tr>");
		tableColumnStart("Test");
		tableColumnStart("Tests Passed");
		tableColumnStart("Tests Skipped");
		tableColumnStart("Tests Failed");
		// tableColumnStart("Error messages");
		// tableColumnStart("Parameters");
		// tableColumnStart("Start<br/>Time");
		tableColumnStart("Total<br/>Tests");
		tableColumnStart("Total<br/>Time");

		m_out.println("</tr>");
		NumberFormat formatter = new DecimalFormat("#,##0.0");
		int qty_tests = 0;
		int qty_pass_m = 0;
		int qty_pass_s = 0;
		int qty_skip = 0;
		int qty_fail = 0;
		int qty_tests_suite = 0;
		int total_tests;
		long time_start = Long.MAX_VALUE;
		long time_end = Long.MIN_VALUE;
		m_testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() > 1) {
				titleRow(suite.getName(), 8);
			}
			Map<String, ISuiteResult> tests = suite.getResults();
			for (ISuiteResult r : tests.values()) {
				qty_tests += 1;
				total_tests = 0;
				ITestContext overview = r.getTestContext();
				startSummaryRow(overview.getName());
				int q = getMethodSet(overview.getPassedTests(), suite).size();
				qty_pass_m += q;
				qty_tests_suite += q;
				total_tests += q;
				m_out.print("<td style=\"background-color:#0A0\">" + q + "</td>");
				q = getMethodSet(overview.getSkippedTests(), suite).size();
				qty_skip += q;
				qty_tests_suite += q;
				total_tests += q;
				m_out.print("<td style=\"background-color:#CCC\">" + q + "</td>");
				// summaryCell(q, 0);
				q = getMethodSet(overview.getFailedTests(), suite).size();
				qty_fail += q;
				qty_tests_suite += q;
				total_tests += q;
				summaryCell(q, 0);
				summaryCell(total_tests, Integer.MAX_VALUE);

				// // NEW
				// // Insert error found
				// m_out.print("<td class=\"numi" + (true ? "" : "_attn") +
				// "\">");
				// getShortException(overview.getFailedTests());
				// getShortException(overview.getSkippedTests());
				// m_out.println("</td>");
				//
				// // NEW
				// // Add parameters for each test case (failed or passed)
				// m_out.print("<td class=\"numi" + (true ? "" : "_attn") +
				// "\">");
				//
				// // Write OS and Browser
				// // m_out.println(suite.getParameter("os").substring(0, 3) +
				// // " | "
				// // + suite.getParameter("browser").substring(0, 3) + " | ");
				//
				// getParameters(overview.getFailedTests());
				// getParameters(overview.getPassedTests());
				// getParameters(overview.getSkippedTests());
				// m_out.println("</td>");
				//
				// // NEW
				// summaryCell(
				// overview.getStartDate().toString(),
				// true);
				// m_out.println("</td>");
				// summaryCell(
				// overview.getEndDate().toString(),
				// true);
				// m_out.println("</td>");

				time_start = Math.min(overview.getStartDate().getTime(), time_start);
				time_end = Math.max(overview.getEndDate().getTime(), time_end);
				summaryCell(
						formatter.format((overview.getEndDate().getTime() - overview.getStartDate().getTime()) / 1000.)
								+ " seconds",
						true);
				m_out.println("</tr>");
				m_testIndex++;
			}
		}
		if (qty_tests > 1) {
			m_out.println("<tr class=\"total\"><td>Total</td>");
			m_out.print("<td style=\"background-color:#0A0\">" + qty_pass_m + "</td>");
			// summaryCell(qty_pass_m, Integer.MAX_VALUE);
			m_out.print("<td style=\"background-color:#CCC\">" + qty_skip + "</td>");
			// summaryCell(qty_skip, 0);
			summaryCell(qty_fail, 0);
			summaryCell(qty_tests_suite, Integer.MAX_VALUE);
			summaryCell(formatter.format(((time_end - time_start) / 1000.) / 60.) + " minutes", true);
			m_out.println("</tr>");
		}
		m_out.println("</table>");
	}

	private void summaryCell(String[] val) {
		StringBuffer b = new StringBuffer();
		for (String v : val) {
			b.append(v + " ");
		}
		summaryCell(b.toString(), true);
	}

	private void summaryCell(String v, boolean isgood) {
		m_out.print("<td class=\"numi" + (isgood ? "" : "_attn") + "\">" + v + "</td>");
	}

	private void startSummaryRow(String label) {
		m_row += 1;
		m_out.print("<tr" + (m_row % 2 == 0 ? " class=\"stripe\"" : "")
				+ "><td style=\"text-align:left;padding-right:2em\"><a href=\"#t" + m_testIndex + "\">" + label + "</a>"
				+ "</td>");
	}

	private void summaryCell(int v, int maxexpected) {
		summaryCell(String.valueOf(v), v <= maxexpected);
	}

	private void tableStart(String cssclass, String id) {
		m_out.println("<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\""
				+ (cssclass != null ? " class=\"" + cssclass + "\"" : " style=\"padding-bottom:2em\"")
				+ (id != null ? " id=\"" + id + "\"" : "") + ">");
		m_row = 0;
	}

	private void tableColumnStart(String label) {
		m_out.print("<th>" + label + "</th>");
	}

	private void titleRow(String label, int cq) {
		titleRow(label, cq, null);
	}

	private void titleRow(String label, int cq, String id) {
		m_out.print("<tr");
		if (id != null) {
			m_out.print(" id=\"" + id + "\"");
		}
		m_out.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
		m_row = 0;
	}

	/**
	 * Starts HTML stream
	 * 
	 * @param tier
	 */
	protected void startHtml(PrintWriter out, String tier, String appurl) {
		out.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<title>ACS Literatum/PageBuilder - TestNG Report</title>");
		out.println("<style type=\"text/css\">");
		out.println("table {table-layout: fixed; margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
		out.println("td,th {border:1px solid #009;padding:.25em .5em}");
		out.println(".result th {vertical-align:bottom}");
		out.println(".param th {padding-left:1em;padding-right:1em}");
		out.println(".param td {padding-left:.5em;padding-right:2em}");
		out.println(".stripe td,.stripe th {background-color: #E6EBF9}");
		out.println(".numi,.numi_attn {text-align:right}");
		out.println(".total td {font-weight:bold}");
		out.println(".passedodd td {background-color: #0A0}");
		out.println(".passedeven td {background-color: #3F3}");
		out.println(".skippedodd td {background-color: #CCC}");
		out.println(".skippedodd td {background-color: #DDD}");
		out.println(".failedodd td,.numi_attn {background-color: #F33}");
		out.println(".failedeven td,.stripe .numi_attn {background-color: #D00}");
		out.println(".stacktrace {white-space:pre;font-family:monospace}");
		out.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
		out.println("</style>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1><center>ACS Literatum/PageBuilder - Automation Report On " + "<a target=\"_blank\" href=\""
				+ appurl + "\">" + tier + "</a>" + " Environment" + "</center></h1>");
	}

	/** Finishes HTML stream */
	protected void endHtml(PrintWriter out) {
		out.println("<center> Report customized by QAIT Automation team </center>");
		out.println("</body></html>");
	}

	// ~ Inner Classes --------------------------------------------------------
	/** Arranges methods by classname and method name */
	private class TestSorter implements Comparator<IInvokedMethod> {
		// ~ Methods
		// -------------------------------------------------------------

		/** Arranges methods by classname and method name */
		@Override
		public int compare(IInvokedMethod o1, IInvokedMethod o2) {
			// System.out.println("Comparing " + o1.getMethodName() + " " +
			// o1.getDate()
			// + " and " + o2.getMethodName() + " " + o2.getDate());
			return (int) (o1.getDate() - o2.getDate());
			// int r = ((T) o1).getTestClass().getName().compareTo(((T)
			// o2).getTestClass().getName());
			// if (r == 0) {
			// r = ((T) o1).getMethodName().compareTo(((T) o2).getMethodName());
			// }
			// return r;
		}
	}
}