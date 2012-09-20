package com.xl.webunit.engine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/***
 * provide API to operate web controls such as button, textfield...
 * each function may throw run time exceptions, do not catch any exceptions in 
 * these functions, leave them to Action class  
 */
public class WebBrowser {
	
	private BrowserVersion bv;
	private TopLevelWindow currentWindow;
	private WebClient client;
	public boolean changeCurrentPage() {
		return false;
	}
	
	public void setBrowser(BrowserVersion bv) {
		this.bv = bv;
	}
	
	public void open(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException, GeneralSecurityException {
		if (bv==null) bv = BrowserVersion.INTERNET_EXPLORER_7;
		client = new WebClient(bv);
		client.setUseInsecureSSL(true);
		client.setRedirectEnabled(true);
//		ProxyConfig pc = new ProxyConfig("hkce01.hk.ibm.com", 80);
//		client.setProxyConfig(pc);
		client.getPage(url);
//		catch (Exception ex) {
			//try again with proxy;
//			ProxyConfig pc = new ProxyConfig("hkce01.hk.ibm.com", 80);
//			client.setProxyConfig(pc);
//			Page p = client.getPage(url);
//			client.getCurrentWindow().setEnclosedPage(p);
//		}
	}
	
	public void close(String tilte) {
		//TODO not sure if browserTitle is the very name
		WebWindow tw = client.getWebWindowByName(tilte);
		 ((TopLevelWindow) tw).close();
	}
	
	public void closeTop() {
		WebWindow tw = client.getCurrentWindow();
		((TopLevelWindow) tw).close();
	}
	
	public void switchByTitle(String title){
		WebWindow tw = client.getWebWindowByName(title);
		client.setCurrentWindow(tw);
	}

	public void go(String oritenation) throws IOException {
		WebWindow tw = client.getCurrentWindow();
		
		History history = tw.getHistory();
		if (oritenation.equalsIgnoreCase("forward"))
			history.forward();
		else if (oritenation.equalsIgnoreCase("back")) {
			history.back();
		}
	}

	public void acceptSecurityAlert(String content) {
		//TODO impelemented later
	}
	
	public void click(HashMap<String, String> to) throws IOException  {
		HtmlElement target = (HtmlElement)findTestObject(to);
		HtmlPage page = target.click();
		currentWindow.setEnclosedPage(page);
	}

	public void input(HashMap<String, String> to, String value) {
		HtmlElement element = findTestObject(to);
		Page page;
		if (element instanceof HtmlPasswordInput) {
			page = ((HtmlPasswordInput) element).setValueAttribute(value);
			currentWindow.setEnclosedPage(page);
		} else  {
			HtmlTextInput target = (HtmlTextInput)findTestObject(to);
			page = target.setValueAttribute(value);
			currentWindow.setEnclosedPage(page);
		}
	}
	
	public void select(HashMap<String, String> to, String item) throws Exception {
		HtmlSelect target = (HtmlSelect)findTestObject(to);
//		Page page = target.setSelectedAttribute(item, true);
		
		int idx = getOptIdx(item, target);
		if (idx>=0) {
			Page page = target.getOption(idx).setSelected(true);
			currentWindow.setEnclosedPage(page);
		} else 
			throw new Exception("option " + item+ " not found in " + to.toString());
	}
	
	private int getOptIdx(String opt, HtmlSelect target) {
		
		List<HtmlOption> opts = target.getOptions();
		for (int i=0;i<opts.size();++i) {
			String ctt = opts.get(i).getTextContent().trim();
			if (ctt.equalsIgnoreCase(opt)) {
				return i;
			}
		}
		return -1;
	}
	public void select(HashMap<String, String> to, int iv) {
		HtmlSelect target = (HtmlSelect)findTestObject(to);
		List<HtmlOption> opts = target.getOptions();
		
		HtmlOption toBeSelected = opts.get(iv);
		Page page = toBeSelected.setSelected(true);
		currentWindow.setEnclosedPage(page);
	}
	
	public void multiSelect(HashMap<String, String> to, List<String> items) {
		HtmlSelect target = (HtmlSelect)findTestObject(to);
		
		List<HtmlOption> opts = target.getOptions();
		
		for (HtmlOption opt: opts) {
			if (items.contains(opt.getValueAttribute())) {
				opt.setSelected(true);
			}
		}
		//didn't change current window as 'select' does
	}

	public void setRadio(HashMap<String, String> to) {
		HtmlRadioButtonInput target = (HtmlRadioButtonInput)findTestObject(to);
		target.setChecked(true);
	}
	
	public void check(HashMap<String, String> to, boolean bv2) {
		HtmlCheckBoxInput target = (HtmlCheckBoxInput)findTestObject(to);
		target.setChecked(bv2);
	}


	public void multiCheck(HashMap<String, String> to, boolean bv4Multi) {
		Page currentPage = currentWindow.getEnclosedPage();
		List<HtmlElement> ss = WebControlFinder.findControls((HtmlPage)currentPage, to);
		
		for (HtmlElement s : ss) {
			((HtmlCheckBoxInput)s).setChecked(bv4Multi);
		}
	}
	
	public TopLevelWindow getCurrentWindow() {
		return currentWindow;
	}

	public HtmlElement findTestObject(HashMap<String, String> properties) {
		if (currentWindow==null)
			currentWindow = (TopLevelWindow)client.getCurrentWindow();
		Page currentPage = currentWindow.getEnclosedPage();
		return WebControlFinder.findControl((HtmlPage)currentPage, properties);
	}
}
