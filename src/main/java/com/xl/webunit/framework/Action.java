package com.xl.webunit.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xl.webunit.engine.WebBrowser;
import com.xl.webunit.util.Constants;
import com.xl.webunit.util.Util;

/**
 * 
 *   
 */
public class Action extends TestElement {

	private Type actionType;
	private List<String> params = new ArrayList<String>();
	private Exception exp;
	
	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public void addParam(String param) {
		if (params == null)
			params = new ArrayList<String>();
		params.add(param);
	}

	public Type getActionType() {
		return actionType;
	}

	public void setActionType(Type actionType) {
		this.actionType = actionType;
	}

	public enum Type {
		BROWSER, OPEN, CLOSE, SWITCH, ACCEPTHTMLDIALOG, ACCEPT, GO, NAVIGATE, 
		WAIT, SLEEP, SETTEXT, INPUT, ENTER, CLICK, MOUSECLICK, SELECT, MULTISELECT, 
		CLOSEOPENEDRECENTLYIE, CLOSEPOPUP, CHECK, MULTICHECK, SELECTBYINDEX, SETRADIO, 
		CLICKLINKINTABLE, CHECKTABLECHECKBOX, INPUTKEYS, CAPTURESCREEN, STORE, GETNUMBER, 
		GETDIALOGSTRING, GETDATE, IF, IFNOT, ELSE, ENDIF
	}

	@Override
	/**
	 * 
	 */
	public boolean execute() {
		publish(TestResult.Type.ACTION_START, this);
		
		long start = System.currentTimeMillis();
		
		WebBrowser browser = getWebBrowser();
		HashMap<String, String> to;
		String key, value;
		
		try {
			switch (actionType) {
			case BROWSER:
			case OPEN:
					String url = params.get(0);
					assertNotNull(url, "open: url is empty");
					if (url.startsWith(Constants.Dollar)) {
						url = TestCase.getData(url);
					}
					browser.open(url);
				break;
			case CLOSE:
				//maybe close all??
					String title = params.get(0);
					assertNotNull(title, "close: title is empty");
					if (title.startsWith(Constants.Dollar)) {
						title = TestCase.getData(title);
					}
					browser.close(title);
				break;
			case SWITCH:
					String title2 = params.get(0);
					assertNotNull(title2, "switch: title is empty");
					if (title2.startsWith(Constants.Dollar)) {
						title = TestCase.getData(title2);
					}
					browser.switchByTitle(title2);
				break;
			case ACCEPTHTMLDIALOG:
			case ACCEPT: 
					String content = params.get(0);
					assertNotNull(content, "keystroke is empty");
					//won't have reference to data file??
					browser.acceptSecurityAlert(content);
				break;
			case GO:
			case NAVIGATE:
					String where = params.get(0);
					//won't have reference to data file? its value can only be "forward", "backward"
					browser.go(where);
				break;
			case WAIT:
			case SLEEP:
					String seconds = params.get(0);
					int iSeconds = Integer.parseInt(seconds);
					Thread.sleep(iSeconds);
				break;
			case SETTEXT:
			case INPUT:
			case ENTER:
				//first find the test object to operate
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				
				value = params.get(1);
				if (value.startsWith(Constants.Dollar)) {
					value = TestCase.getData(value);
				}
				assertNotNull(value, "test data not found," + value);
				browser.input(to, value);
				break;
			case CLICK:
			case MOUSECLICK:
				to = TestCase.getMap(params.get(0));
				assertNotNull(to, "test object not found," + params.get(0));
				browser.click(to);
				break;
			case SELECT:
				//regular expression and ignore case
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				value = params.get(1);
				if (value.startsWith(Constants.Dollar)) {
					value = TestCase.getData(value);
				}
				assertNotNull(value, "test data not found," + value);
				browser.select(to, value);
				break;
			case MULTISELECT:
				//This is not implemented in PACAS, I provide an implementation here
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				value = params.get(1);
				if (value.startsWith(Constants.Dollar)) {
					value = TestCase.getData(value);
				}
				assertNotNull(value, "test data not found," + value);
				//process escape character
				List<String> items = Util.asList(value);
				browser.multiSelect(to, items);
				break;
			case CLOSEOPENEDRECENTLYIE:
			case CLOSEPOPUP:
				browser.closeTop();
				break;
			case  CHECK:
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				value = params.get(1);
				assertNotNull(value, "test data not found," + value);
				boolean bv = Boolean.valueOf(value);
				browser.check(to, bv);
				break;
			case MULTICHECK:
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				value = params.get(1);
				boolean bv4Multi = Boolean.valueOf(value);
				assertNotNull(value, "test data not found," + value);
				browser.multiCheck(to, bv4Multi);
				break;
			case SELECTBYINDEX: 
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				value = params.get(1);
				assertNotNull(value, "test data not found," + value);
				//conver to integer
				int iv = Integer.parseInt(value);
				
				browser.select(to, iv);
				break; 
			case SETRADIO:
				key = params.get(0);
				to = TestCase.getMap(key);
				assertNotNull(to, "test object not found," + key);
				browser.setRadio(to);
				break;
			case CLICKLINKINTABLE:
			case CHECKTABLECHECKBOX:
			case INPUTKEYS:
			case CAPTURESCREEN:
			case STORE:
			case GETNUMBER:
			case GETDIALOGSTRING:
				//generally in html unit, a dialog is the top level window
			case GETDATE:
			case IF:
			case IFNOT:
			case ELSE:
			case ENDIF: 
				success=false;
				exp = new Exception(Constants.NotImplemented);
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			success=false;
			exp = ex;
		}
		execTime = System.currentTimeMillis() - start;
		publish(TestResult.Type.ACTION_END, this);
		return success;
	}
	
	/**
	 * get the exception which results in the action fails
	 * @return
	 */
	public Exception getException() {
		return exp;
	}
	@Override
	public void count() {
		getTestResult().countAction(1);
	}
	
	private void assertNotNull(Object obj, String message) throws Exception {
		if (obj==null) throw new Exception(message);
	}
}
