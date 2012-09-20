package com.xl.webunit.framework;

import java.util.ArrayList;
import java.util.List;

import com.xl.webunit.util.Constants;

/**
 * collect messages such as status, failures, errors test cases emit, meanwhile,
 * notify test listener registered in it that they can take actions according to
 * the messages
 * 
 */
public class TestResult {

	enum Type {
		BUILD_ERROR, TEST_START, TEST_END, STEP_START, STEP_END, ACTION_START, ACTION_END, VERIF_START, VERIF_END
	}

	private static TestResult tr;

	private List<TestListener> ls;

	private List<TestElement> elements;

	public static TestResult getInstance() {
		if (tr == null)
			tr = new TestResult();
		return tr;
	}
	
	/**
	 * set qualifier to public for test
	 * @param string 
	 */
	public TestResult() {
		ls=new ArrayList<TestListener>();
	}
//	private TestResult() {
//		// messages = new ArrayList<TestElement>();
//		ls = new ArrayList<TestListener>();
//	}

	public static int MAX = 30;

	private int testCount = 0;
	private int stepCount = 0;
	private int actionCount = 0;
	private int verifCount = 0;

	public int getTestCount() {
		return testCount;
	}

	public int getStepCount() {
		return stepCount;
	}

	public int getActionCount() {
		return actionCount;
	}

	public int getVerifCount() {
		return verifCount;
	}

	private void saveTestElement(TestElement ele) {
		if (elements == null) elements = new ArrayList<TestElement>();
		elements.add(ele);
	}

	public List<TestElement> getByIndex(int index) {
		List<TestElement> ele = new ArrayList<TestElement>();
		for (TestElement te : elements) {
			if (te.getIndex() == index) {
				ele.add(te);
			}
		}
		return ele;
	}

	public void countTest(int n) {
		testCount += n;
	}

	public void countStep(int n) {
		stepCount += n;
	}

	public void countAction(int n) {
		actionCount += n;
	}

	public void countVerif(int n) {
		verifCount += n;
	}

	public void addListener(TestListener listener) {
		ls.add(listener);
	}

	public List<TestListener> getListeners() {
		return ls;
	}

	public synchronized void accept(Type type, TestElement element,
			Exception errEx, String msg) {
		saveTestElement(element);
		switch (type) {
		case TEST_START:
			if (element instanceof TestCase)
				fireTestStarted((TestCase) element);
			break;
		case TEST_END:
			if (element instanceof TestCase)
				fireTestEnded((TestCase) element);
			break;
		
		case STEP_START:
			if (element instanceof Step)
				fireIntoStep((Step) element);
			break;
		case STEP_END:
			if (element instanceof Step)
				fireStepEnded((Step) element);
		case ACTION_START:
			if (element instanceof Action)
				fireActionRun((Action) element);
			break;
		case ACTION_END:
			if (element instanceof Action)
				fireActionEnded((Action) element);
			break;
		case VERIF_START:
			if (element instanceof Verification)
				fireVerifRun((Verification) element);
			break;
		case VERIF_END:
			if (element instanceof Verification)
				fireVerifEnded((Verification) element);
			break;
		default:
			throw new RuntimeException(Constants.NotImplemented);
		}
	}

	private void fireVerifEnded(Verification verif) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).endVerif(verif);
		}
	}

	private void fireActionEnded(Action act) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).endAction(act);
		}
	}

	private void fireStepEnded(Step step) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).endStep(step);
		}
	}

	private void fireTestStarted(TestCase test) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).startTest(test);
		}
	}

	private void fireTestEnded(TestCase test) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).endTest(test);
		}
	}

	private void fireIntoStep(Step step) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).startStep(step);
		}
	}

	private void fireActionRun(Action action) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).startAction(action);
		}
	}

	private void fireVerifRun(Verification verif) {
		for (int i = 0; i < ls.size(); ++i) {
			ls.get(i).startVerif(verif);
		}
	}
	
	/***
	 * TODO experimental api, will be removed in the future
	 * @return
	 */
	public TestListener getTR() {
		if (ls.size() >0) {
			return ls.get(0);
		} else 
			return null;
	}
}
