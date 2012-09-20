package com.xl.webunit.framework;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.xl.webunit.engine.WebBrowser;
import com.xl.webunit.util.Constants;

/**
 * 
 * verif if actual value is equal to expected value. there are couple of verifications, which 
 * are derived from PACAS' verification definition
 * 
 *
 */
public class Verification extends TestElement {

	private String type;
	private String object;
	private String value;
	private String param;

	//reserved for future
	private int retry;
	private int di;
	private int interval;
	
	
	private Exception exp;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	enum Type {
		STRING_IN_PAGE, OBJECT_EXISTENCE, ITEM_IN_SELECT, CHECKBOX_STATUS, RADIO_STATUS,
		DEFAULT_VALUE, STRING_IN_TEXTFIELD, STRING_IN_TABLE,STRING_IN_DIALOG,
		CONTROL_STATUS_DISABLE,	STRING_IN_SELECT
	}
	
	public Verification(String type, String object, String value, String param, int retry, int interval, int dataindex) {
		this.type = type;
		this.object = object;
		this.value = value;
		this.param = param;
		
		this.retry = retry;
		this.interval = interval;
		this.di = dataindex;
	}

	public boolean execute() {
		publish(TestResult.Type.VERIF_START, this);
		
		Long start = System.currentTimeMillis();
		WebBrowser browser = getWebBrowser();
		boolean nocase=false, regular=false;
		
		if (param!=null) {
			nocase = param.contains("i");
			regular = param.contains("r");
		}
		//check if contains "!" which represents logic 'NOT'
		boolean neg = type.startsWith(Constants.Exclaimation);
		if (neg) 
			type = type.substring(1);
		Type eType = Type.valueOf(type.toUpperCase());
		
		try {
			switch(eType) {
				case STRING_IN_PAGE:
					String str = object;
					
					if (str==null) {
						success=false;
						throw new Exception("test data " + object + "not found in cache.");
					}
					if (str.startsWith(Constants.Dollar)) {
						str = TestCase.getData(str);
					}
					Page page = getWebBrowser().getCurrentWindow().getEnclosedPage();
					String pageContext =((HtmlPage)page).asText();
	//				if (Constants.Debug) System.out.println(pageContext);
					success = compare(pageContext, str , regular, nocase, false);
					if (!success) throw new Exception(" \"" + str + "\"  not found in the page");
					break;
				case OBJECT_EXISTENCE: 
					HashMap<String, String> objToTest = TestCase.getMap(object);
					HtmlElement target = browser.findTestObject(objToTest);
					if (target==null) {
						success=false;
						throw new Exception(objToTest.toString() + "not found in the page");
					}
					break;
				case ITEM_IN_SELECT: 
					HashMap<String, String> selectObj = TestCase.getMap(object);
					//get HtmlSelect object
					HtmlElement element = browser.findTestObject(selectObj);
					HtmlSelect realSelect = (HtmlSelect)element;
					//get all options
					List<HtmlOption> options = realSelect.getOptions();
					
					success=false;
					//check every option
					for (HtmlOption option : options) {
						if (option.getValueAttribute().equals(value)) {
							success=true;
							break;
						}
					}
					break;
				case CHECKBOX_STATUS:
					HtmlCheckBoxInput cb = (HtmlCheckBoxInput)browser.
									findTestObject(TestCase.getMap(object));
					if (!cb.getCheckedAttribute().equalsIgnoreCase(value))
						success=false;
					break;
				case RADIO_STATUS: 
					HtmlRadioButtonInput rb = (HtmlRadioButtonInput)browser.
								findTestObject(TestCase.getMap(object));
					
					String checkAttr = rb.getCheckedAttribute();
					success=false;
					if ((checkAttr.equalsIgnoreCase("checked")&&
							value.equalsIgnoreCase("selected")) ||
							checkAttr.equalsIgnoreCase("unchecked") &&
							value.equalsIgnoreCase("notselected")) {
						success=true;
					}
					break;
				case DEFAULT_VALUE: 
					//not implemented in PACAS
					break;
				case STRING_IN_TEXTFIELD: 
					HtmlTextInput textInput = (HtmlTextInput)browser.findTestObject(TestCase.getMap(object));
					String actualValue = textInput.getValueAttribute();
					
					success = compare(value, actualValue, regular, nocase, true);
	//				if (regular) {
	//					Pattern p;
	//					if (nocase)
	//						p = Pattern.compile(actualValue, Pattern.CASE_INSENSITIVE);
	//					else 
	//						p = Pattern.compile(actualValue);
	//					success = p.matcher(value).matches();
	//				} else {
	//					//exact match; case insensitive
	//					if (nocase) {
	//						success=actualValue.equalsIgnoreCase(value);
	//					} else {
	//						success=actualValue.equals(value);
	//					}
	//				}
					break;
				case STRING_IN_TABLE: 
					HtmlTable targetTbl = (HtmlTable)browser.findTestObject(TestCase.getMap(object));
					String text = targetTbl.asXml();
					success = text.indexOf(value)>-1;
					break;
				case STRING_IN_DIALOG: 
					//TODO string_in_dialog to be implemented later
					break;
				case CONTROL_STATUS_DISABLE:
					HtmlElement element2 = browser.
							findTestObject(TestCase.getMap(object));
					//TODO pseudo code here, need more test
					String disabled = element2.getAttribute("disabled");
					success = Boolean.valueOf(disabled);
					break;
				case STRING_IN_SELECT: 
					HtmlSelect realSelect2 = (HtmlSelect)browser.findTestObject(TestCase.getMap(object));
					//get all options
					List<HtmlOption> options2 = realSelect2.getOptions();
					success=false;
					
					Pattern pattern=null;
					if (regular && nocase) {
						pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
					} else if (regular) {
						pattern = Pattern.compile(value);
					}
					//check every option
					for (HtmlOption option : options2) {
						if (option.isSelected()) {
							if (regular ) {
								success = pattern.matcher(option.getValueAttribute()).matches();
								break;
							} else {
								//exact match; case insensitive
								if (nocase) {
									success=option.getValueAttribute().equalsIgnoreCase(value);
								} else {
									success=option.getValueAttribute().equals(value);
								}
								break;
							}
						}
					}
					break;
				default: 
					break;
			}
		}catch (Exception ex) {
			exp = ex;
			success=false;
		}
		execTime = System.currentTimeMillis() - start;
		publish(TestResult.Type.VERIF_END, this);
		return success;
	}
	
	
	private boolean compare(String content, String acualvalue, boolean regular, boolean nocase, boolean equal) {
		boolean success=false;
		if (regular) {
			Pattern p;
			if (nocase)
				p = Pattern.compile(acualvalue, Pattern.CASE_INSENSITIVE);
			else 
				p = Pattern.compile(acualvalue);
			success = p.matcher(value).matches();
		} else {
			//exact match; case insensitive
			if (equal) {
				if (nocase) {
					success=acualvalue.equalsIgnoreCase(value);
				} else {
					success=acualvalue.equals(value);
				}
			} else {
				success = content.contains(acualvalue);
			}
		}
		return success;
	}
	
	public Exception getException() {
		return exp;
	}
	
	@Override
	public void count() {
		getTestResult().countVerif(1);
	}
}
