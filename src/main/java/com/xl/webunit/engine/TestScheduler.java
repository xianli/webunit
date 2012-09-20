package com.xl.webunit.engine;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.xl.webunit.framework.TestCase;
import com.xl.webunit.framework.TestResult;
import com.xl.webunit.framework.runner.CmdRunner;
import com.xl.webunit.util.Constants;
import com.xl.webunit.util.Util;

/**
 * test scheduler manage all test cases to be executed, each test case is assigned a separate thread, which
 * is obtained from a thread pool. the thread pool assures there are only limited threads launched by the system.
 * 
 * @author jiangxl
 */
public class TestScheduler {

	public static final int MAX_THREADS = 30;
	public static final int INTERVAL = 10;
	private List<File> scripts;
	private TestCaseBuilder builder;
	private int maxThreads=30;

	public TestScheduler() {
		scripts = new LinkedList<File>();
	}

	public void addScript(String fScript) {
		scripts.add(new File(Util.getBasePath() + "cases"
				+ Util.Separator + fScript));
	}

	public void removeScript(int scriptIdx) {
		scripts.remove(scriptIdx);
	}

	public void moveUp(int scriptIdx) {
		assert (scriptIdx > 0);
		File dest = scripts.remove(scriptIdx);
		scripts.add(scriptIdx - 1, dest);
	}

	public void moveDown(int scriptIdx) {
		assert (scriptIdx < scripts.size());
		File dest = scripts.remove(scriptIdx);
		scripts.add(scriptIdx + 1, dest);
	}

	public void setMaxThreads(int mt) {
		maxThreads = mt;
	}

	public boolean isAbleToRun() {
		return scripts != null && scripts.size() > 0;
	}

	private abstract class ThreadPool {

		protected List<Thread> threads;
		protected int threadCount;
		private int runningThread = 0;
		private int interval = 10;

		/**
		 * 
		 * @param threadCount
		 *            how many threads are in the pool
		 * @param interval
		 *            interval to check the thread status
		 */
		public ThreadPool(int threadCount, int interval) {
			this.threadCount = threadCount;
			this.interval = interval;
			threads = new LinkedList<Thread>();
		}

		public abstract void init();

		public Thread getAThread() {
			while (runningThread >= threadCount) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitDeadThread();
			}
			runningThread++;
			return new TCThread();
		}

		private void waitDeadThread() {
			for (int i = 0; i < threadCount; ++i) {
				Thread t = threads.get(i);
				if (!t.isAlive()) {
					threads.remove(i);
					runningThread--;
				}
			}
		}
	}

	private class TCThread extends Thread {

		private TestCase testcase;

		public void setTestCase(TestCase tc) {
			testcase = tc;
		}

		public void run() {
			if (testcase != null) {
				testcase.execute();
				testcase.getTestResult().getTR().cleanup();
//				new HtmlReport(testcase.getTestResult());
			}
			
		}
	}

	/**
	 * entry point of whole system. read test scripts on disk and then lunch
	 * each of them in separate thread.
	 */
	public void start() {
		// build test case object from input
		TestCase[] cases = new TestCase[scripts.size()];
		TestResult result;
//		TestResult.getInstance().addListener(new TestRunner());
		for (int i = 0; i < scripts.size(); ++i) {
			cases[i] = builder.build(scripts.get(i));
			cases[i].setIndex(i);
			result = new TestResult();
			result.addListener(new CmdRunner(i+cases[i].getName()));
			//TODO to be changed; each test case has a test result object
			cases[i].connectTo(result);
			cases[i].count();
		}
		// set number of threads for test case execution
		int validTCs = scripts.size();
		int j=0;
		while (j<scripts.size()) {
			if(cases[j].hasBuildError()) {
				validTCs--;
				cases[j].getBuildExep().get(0).printStackTrace();
			}
			j++;
		}
		int threadCount = maxThreads;
		if (maxThreads > validTCs) {
			// if test cases are less than predefined maximum threads, then
			// count of threads should be changed to that of test cases
			threadCount = validTCs;
		}
		// create a thread pool and fill it up with test case threads
		ThreadPool threadPool = new ThreadPool(threadCount,
				INTERVAL) {
			public void init() {
				for (int i = 0; i < threadCount; ++i) {
					threads.add(new TCThread());
				}
			}
		};
		// start each thread
		for (int i = 0; i < scripts.size(); ++i) {
			if (!cases[i].hasBuildError()) {
				TCThread tcThread = (TCThread)(threadPool.getAThread());
				tcThread.setTestCase(cases[i]);
				tcThread.start();
			} 
		}
	}

	private void setBuilder(TestCaseBuilder bd) {
		builder = bd;
	}

	public static void main(String[] args) {
		TestScheduler scheduler = new TestScheduler();
//		Util.setBasePath(Constants.BasePath);
		Util.setBasePath("C:/Documents and Settings/jxl/workspace/SQO/");
		scheduler.setBuilder(new TestCaseBuilder());
		// add scripts about to run
//		scheduler.addScript("AdditionalPart_new.xml");
		//C:\Documents and Settings\jxl\workspace\SQO\cases\Payment_System01_new.xml
		scheduler.addScript("Payment_System01_new.xml");
//		scheduler.addScript("Payment_System02_new.xml");
//		scheduler.addScript("Google3.cxml");
		if (scheduler.isAbleToRun())
			scheduler.start();
	}
}
