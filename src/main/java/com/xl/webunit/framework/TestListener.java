package com.xl.webunit.framework;

/**
 * 
 *
 */
public interface TestListener {
	
	public void startTest(TestCase test);
	public void endTest(TestCase test);
	
	public void startStep(Step step);
	public void endStep(Step step);
	
	public void startAction(Action action);
	public void endAction(Action action);
	
	public void startVerif(Verification verif);
	public void endVerif(Verification verif);
	
	/**
	 * clean every thing in memory when test case is ended or distroyed 
	 */
	public void cleanup() ;
	
}
