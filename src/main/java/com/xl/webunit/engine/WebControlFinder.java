package com.xl.webunit.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.xl.webunit.util.Util;

/**
 *
 */
public class WebControlFinder {
	
	/**
	 * find web controls with defined properties in specific page,
	 * 
	 * @param page
	 * @param properties
	 * @return
	 */
	public static HtmlElement findControl(HtmlPage page,
			HashMap<String, String> properties) {
		Util.debug(page.asXml());
		String property = properties.get("propertyName");
		String value = properties.get("value");
		String type = properties.get("type");
		// first find all HTML elements whose type is in "type"
		List<HtmlElement> possibleHE = findByType(page, type);
		boolean isLink = false;
		if (type.equalsIgnoreCase(".link") || type.equalsIgnoreCase("link")) {
			isLink=true;
		}
		// then compare their properties with those in hash map
		List<HtmlElement> elements = findByProperties(possibleHE, property, value, isLink);
		//if index out of bound, throw exception
		return elements.get(0);
	}
	
	public static List<HtmlElement> findControls(HtmlPage page, 
			HashMap<String, String> properties) {
		//first by type
		String property = properties.get("propertyName");
		String value = properties.get("value");
		String type = properties.get("type");
		// first find all HTML elements whose type is in "type"
		List<HtmlElement> possibleHE = findByType(page, type);
		
		boolean isLink = false;
		if (type.equalsIgnoreCase(".link") || type.equalsIgnoreCase("link")) {
			isLink=true;
		}
		return findByProperties(possibleHE, property, value, isLink);
	}

	private static boolean isInputControl(String type) {
		final String[] types = {"submit", "button", "checkbox", 
				"radiobutton", "text", "password", "textfield" };
		for (int i = 0; i < types.length; ++i) {
			if (types[i].equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	private static String getTagNameByType(String type) {
		if (isInputControl(type)) 
			return "input";
		else if (type.equalsIgnoreCase("link")) 
			return "a";
		else if (type.equalsIgnoreCase("select")) 
			return "select";
		else  if (type.equalsIgnoreCase("table")) 
			return "table";
		else return "";
	}
	
	private static List<HtmlElement> findByType(HtmlPage page, String type) {
//		if (Constants.Debug) {
//			System.out.println("-------------------page content---------------------");
//			System.out.println(page.asXml());
//		}
		String tagName = getTagNameByType(type);
		List<HtmlElement> targets = new ArrayList<HtmlElement>();
		if (isInputControl(type)) {
			DomNodeList<HtmlElement> hes = page.getElementsByTagName(tagName);
			Iterator<HtmlElement> itForHES = hes.iterator();
			while (itForHES.hasNext()) {
				HtmlElement he = itForHES.next();
				String actualtype = he.getAttribute("type");
				
//				if (actualtype.equalsIgnoreCase(type) || (actualtype.equals("text")
//						&& type.equals("textfield"))) {
//					targets.add(he);
//				} esle if () {
//					
//				}
				targets.add(he);
			}
		} else {
			DomNodeList<HtmlElement> hes = page.getElementsByTagName(tagName);
			Iterator<HtmlElement> it4HES = hes.iterator();
			while (it4HES.hasNext()) {
				targets.add(it4HES.next());
			}

		}
		return targets;
	}
	
	/**
	 * 
	 * @param possibleHE
	 * @param property
	 * @param value regular expression and ignore case enabled
	 * @return
	 */
	private static List<HtmlElement> findByProperties(List<HtmlElement> possibleHE,
			String property, String value, boolean isLink) {
		Pattern p = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
		
		if (property.startsWith(".")) 
			property = property.substring(1);
		
		List<HtmlElement> all = new ArrayList<HtmlElement>();
		
		//special treatment for link
		
		for (int i = 0; i < possibleHE.size(); ++i) {
			String attribute = possibleHE.get(i).getAttribute(property);
			//CQ0001
			if (isLink) {
				if (property.equalsIgnoreCase("text")) {
					String ctt = possibleHE.get(i).getTextContent().trim();
					//first try plain compare
					if (value.equalsIgnoreCase(ctt)) {
						all.add(possibleHE.get(i));
					}
					//they use regular expression to search
					else if (p.matcher(ctt).matches()) {
						all.add(possibleHE.get(i));
					}
				}
			}
			if (attribute != null && p.matcher(attribute).matches()) {
				all.add(possibleHE.get(i));
			}
		}
		return all;
	}

	protected static HtmlElement createObject(HtmlElement obj, String type) {
		if (obj == null)
			return null;
		if (type.equalsIgnoreCase("button"))
			return (HtmlButtonInput) obj;
		if (type.equalsIgnoreCase("password"))
			return (HtmlPasswordInput) obj;
		if (type.equalsIgnoreCase("submit"))
			return (HtmlSubmitInput) (obj);
		if (type.equalsIgnoreCase("radiobutton"))
			return (HtmlRadioButtonInput) (obj);
		if (type.equalsIgnoreCase("checkbox"))
			return (HtmlCheckBoxInput) obj;
		if (type.equalsIgnoreCase("textfield"))
			return (HtmlTextInput) (obj);
		if (type.equalsIgnoreCase("text"))
			return (HtmlTextArea) obj;
		if (type.equalsIgnoreCase("select"))
			return (HtmlSelect) (obj);
		if (type.equalsIgnoreCase("image"))
			return (HtmlImage) obj;
		if (type.equalsIgnoreCase("link"))
			return (HtmlLink) obj;
		if (type.equalsIgnoreCase("table"))
			return (HtmlTable) (obj);
		return obj;
	}
	
}
