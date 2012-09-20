package com.xl.webunit.framework;

import com.xl.webunit.engine.WebBrowser;

/**
 * 
 */
public abstract class TestElement {
	
	enum Type {
		TEST, STEP, ACTION, VERIF
	}
	protected Type elmType;
	protected TestElement parent;
	/**execution time of each test element (in seconds)*/
	protected long execTime; 
	
	/**execution of this test element is successful or not, true by default*/
	protected boolean success=true;
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getName() {
		return parent.getName();
	}
	public TestElement getParent() {
		return parent;
	}

	public void setParent(TestElement parent) {
		this.parent = parent;
	}
	
	protected void publish(TestResult.Type type, TestElement element) {
		getTestResult().accept(type, element, null, null);
	}
	
//	protected void publish(TestResult.Type type, TestElement element, String msg) {
//		getTestResult().accept(type, element, null, msg);
//	}
//	
//	protected void publish(TestResult.Type type, TestElement element, Exception ex) {
//		getTestResult().accept(type, element, ex, null);
//	}
	
	public Type getElementType() {
		return elmType;
	}
	
	public void setElementType(Type cmdType) {
		this.elmType = cmdType;
	}
	
	/**
	 * look for a test result object on the tree structure of test case object.
	 * @return
	 */
	public TestResult getTestResult() {
		return parent.getTestResult();
	}
	
	public WebBrowser getWebBrowser() {
		return parent.getWebBrowser();
	}
	
	/**
	 * return the unique index of the test element, if it is not set explicitly, 
	 * it uses its parent's index.  
	 * @return unique index
	 */
	public int getIndex() {
		return parent.getIndex();
	}
	
	/**
	 * 
	 * @return execution time in seconds
	 */
	public long getExecTime() {
		return execTime/1000;
	}
	/**
	 * <p>
	 * all test elements should implement this method, as every of them is executable. 
	 * during executing, test element may deliver message to test result to tell what
	 * happened in it, generally some error messages </p>
	 * <p>
	 *  the value returned can be used to 
	 * determine the execution status of its parent object. e.g.,  the return value of step's 
	 * execute method can be decided by applying AND on all return value of verification's execute 
	 * method </p>
	 * @return true if execution succeed, else false. 
	 */
	abstract public boolean execute();
	/**
	 * tells test result how many sub elements it has
	 */
	abstract public void count();

}
