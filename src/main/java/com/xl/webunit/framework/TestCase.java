package com.xl.webunit.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xl.webunit.engine.WebBrowser;
import com.xl.webunit.util.Constants;
import com.xl.webunit.util.Util;

/***
 * 
 * a test case has structure like below, it is an intermediate form and can be
 * considered as an interface of this toolkit to other test script language.
 * this structure is in memory, that is, you can look into a TestCase object for
 * it.
 * 
 */
public class TestCase extends TestElement {

	// both map cache and data cache are static, so they are shared among all
	// test cases.
	public static HashMap<String, HashMap<String, String>> mapCache = new HashMap<String, HashMap<String, String>>();
	public static HashMap<String, List<String>> dataCache = new HashMap<String, List<String>>();

	public static synchronized void addToMapCache(String key,
			HashMap<String, String> pair) {
		// first check if "key" existed
		if (!mapCache.containsKey(key)) {
			// copy-value
			mapCache.put(key, Util.copy(pair));
		}
	}

	public static synchronized void addToDataCache(String key,
			ArrayList<String> values) {
		// copy-value
		if (!dataCache.containsKey(key))
			dataCache.put(key, Util.copy((ArrayList<String>) values));
	}

	public static String getData(String key) {
		// if using data index
		String di = key.substring(key.lastIndexOf(Constants.Separator) + 1);
		boolean hasDI = true;
		for (int i = 0; i < di.length(); ++i) {
			if (!Character.isDigit(i)) {
				hasDI = false;
			}
		}
		int index = hasDI ? Integer.parseInt(di) : 0;

		List<String> v = dataCache.get(key);
		if (v != null)
			return v.get(index);
		else
			return null;
	}

	public static HashMap<String, String> getMap(String key) {
		return mapCache.get(key);
	}

	private String name;
	private List<Step> steps = new ArrayList<Step>();
	private Step prolog;
	private Step epilog;
	
	private int index;

	private List<Exception> buildExep;

	/*
	 * run time properties
	 */
	private TestResult result;
	private WebBrowser browser;

	public TestCase() {
		browser = new WebBrowser();
	}

	@Override
	public void count() {
		assert (result != null);
		result.countTest(1);
		if (prolog != null)
			prolog.count();
		if (epilog != null)
			epilog.count();
		if (steps != null)
			for (Step step : steps) {
				step.count();
			}
	}

	@Override
	public TestResult getTestResult() {
		return result;
	}

	@Override
	public WebBrowser getWebBrowser() {
		return browser;
	}

	@Override
	public boolean execute() {
		
		if (hasBuildError()) {
			publish(TestResult.Type.TEST_END, this);
			return false;
		}
		publish(TestResult.Type.TEST_START, this);

		long start = System.currentTimeMillis();
		
		if (success&&prolog != null) {
			success = prolog.execute();
		}
		
		if (steps != null) {
			for (int i=0;success && i<steps.size();++i) {
				success = steps.get(i).execute();
			}
		}
		
		if (success&&epilog != null) {
			success = epilog.execute();
		}
		
		execTime = System.currentTimeMillis() - start;
		publish(TestResult.Type.TEST_END, this);
		return success;
	}
	
	public void addStep(Step step) {
		step.setParent(TestCase.this);
		if (steps == null)
			steps = new ArrayList<Step>();
		steps.add(step);
	}

	public void setProlog(Step prolog) {
		this.prolog = prolog;
		prolog.setParent(TestCase.this);
	}

	public void setEpilog(Step epilog) {
		this.epilog = epilog;
		epilog.setParent(TestCase.this);
	}

	public Step getEpilog() {
		return epilog;
	}

	public Step getProlog() {
		return prolog;
	}

	public void connectTo(TestResult result) {
		this.result = result;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Step getLastStep() {
		return steps.get(steps.size() - 1);
	}

	public int getStepCount() {
		return steps.size();
	}

	public boolean hasBuildError() {
		return buildExep != null && buildExep.size() > 0;
	}

	public List<Exception> getBuildExep() {
		return buildExep;
	}

	public void addException(Exception e) {
		if (buildExep == null)
			buildExep = new ArrayList<Exception>();
		buildExep.add(e);
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
