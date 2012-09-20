package com.xl.webunit.util;

import java.util.ResourceBundle;

/**
 * a customized logger for whole system, 
 * 
 */
public class Logger {
	private static final boolean DEBUG=true;
	private static ResourceBundle bundle;
	
	static {
		bundle = ResourceBundle.getBundle("com.ibm.testgo.ApplicationResources");
	}
	
	public static void debug(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
	public static void info(String msg) {
		System.out.println(bundle.getString(msg));
	}
	
	public static void warning(String msg) {
		System.out.println(bundle.getString(msg));
	}

	public static void error(String msg) {
		System.err.println(bundle.getString(msg));
	}
}
