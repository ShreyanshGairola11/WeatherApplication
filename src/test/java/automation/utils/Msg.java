/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automation.utils;

import org.testng.Reporter;

/**
 *
 * @author QAI
 */
public class Msg {

	protected static final String fail = "[ASSERT FAIL]: ";
	protected static final String info = "[INFO]: ";
	protected static final String pass = "[ASSERT PASS]: ";
	protected static final String scripterror = "[SCRIPTING ERROR]: ";
	protected static final String actions = "[ACTION]: ";
	protected static final String verified = "[Verified]: ";

	public String failForAssert(String message) {
		return reportMsgForAssert(fail, message, true);
	}

	public String fail(String message) {
		return reportMsg(fail, message);
	}
	
	public String verified(String message) {
		return reportMsg(verified, message);
	}

	public String pass(String message) {
		return reportMsg(pass, message);
	}

	public String scripterror(String message) {
		return reportMsg(scripterror, message);
	}

	public String log(String message) {
		return reportMsg(info, message);
	}

	public String logAssertion(String message) {
		return reportMsg("", message);
	}

	public String actions(String message) {
		return reportMsg(actions, message);
	}

	private String reportMsg(String prefix, String message) {
		Reporter.log(prefix + message, true);
		return prefix + message;
	}
	public static void reportInfo(String message) {
		Reporter.log(info + message, true);
	}
	public static void reportAction(String message) {
		Reporter.log(actions + message, true);
	}
	public static void reportPass(String message) {
		Reporter.log(pass + message, true);
	}
	public static String reportFailForAssert(String message) {
		 return fail + message;
	}


	private String reportMsgForAssert(String prefix, String message, boolean flag) {
		return prefix + message;
	}
	
	// bBelow methods are static so that can be used across the classes
	
	private static String reportMessage(String prefix, String message) {
		Reporter.log(prefix + message, true);
		return prefix + message;
	}
	
	
	public static  String logMessage(String message) {
		return reportMessage(info, message);
	}

}
