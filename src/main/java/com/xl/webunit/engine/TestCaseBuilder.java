package com.xl.webunit.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.xl.webunit.framework.Action;
import com.xl.webunit.framework.Step;
import com.xl.webunit.framework.TestCase;
import com.xl.webunit.framework.Verification;
import com.xl.webunit.util.BundleReader;
import com.xl.webunit.util.Constants;
import com.xl.webunit.util.Logger;
import com.xl.webunit.util.Util;

/**
 *  parse test case xml file to produce an test case object, test data and test object map are cached in
 *  the test object. 
 *  
 *  @author jiangxl
 */
public class TestCaseBuilder {

	/**
	 * nodes in script file need to be processed
	 */
	interface InterestedNode {
		String TestCase = "testcase";
		String Header = "header";
		String Steps = "steps";
		String Step = "step";
		String Prolog = "prolog";
		String Epilog = "epilog";
		String Action = "action";
		String Verify = "verify";
		String MappingSet = "mapping-set";
		String DataSet = "data-set";
		String Mapping = "mapping";
		String Data = "data";
		String Param = "param";

		String DData = "data";
		String DItem = "item";
		String DValue = "value";
	}

	/**
	 * attributes in script file need to be processed
	 * 
	 */
	interface InterestedAttrs {
		String Name = "name";
		String Index = "index";
		String Comment = "comment";
		String Proto = "proto";
		String Ext = "extension";
		String Type = "type";
		String Object = "object";
		String Value = "value";
		String Retry = "retry";
		String Interval = "interval";
		String Domain = "domain";
		String Param = "param";
		String Context = "context";
		String DI = "dataindex";
		String SRC = "src";

		String DIndex = "index";
		String DKey = "key";
		String DName = "name";

		String MName = "name";
	}

	public class CaseFileHandler extends DefaultHandler2 {
		private TestCase testcase;
		/**
		 * 1: prolog, 2: epilog, 3: steps, 4: mapping-set, 5: data-set
		 */
		private int scriptcontext;

		public CaseFileHandler() {
			testcase = new TestCase();
		}

		public void startElement(String nsURI, String lname, String qname,
				Attributes attrs) {
			if (lname.equals(InterestedNode.TestCase)) {
				// get into a test case file
				String name = attrs.getValue(InterestedAttrs.Name);
				testcase.setName(name);
			} else if (lname.equalsIgnoreCase(InterestedNode.MappingSet)) {
				scriptcontext = 4;
			} else if (lname.equalsIgnoreCase(InterestedNode.Mapping)) {
				String fMapName = attrs.getValue(InterestedAttrs.SRC);
				String fMapPath = Util.getBasePath() + "map"
						+ Util.Separator + fMapName;
				buildMapCache(new File(fMapPath), testcase);

			} else if (lname.equalsIgnoreCase(InterestedNode.DataSet)) {
				scriptcontext = 5;
			} else if (lname.equalsIgnoreCase(InterestedNode.Data)) {
				String fDataName = attrs.getValue(InterestedAttrs.SRC);
				String fDataPath = Util.getBasePath() + "data"
						+ Util.Separator + fDataName;
				buildDataCache(new File(fDataPath), testcase);
			} else if (lname.equalsIgnoreCase(InterestedNode.Prolog)) {
				Step prolog = new Step();
				prolog.setComment("prolog");
				scriptcontext = 1;
				testcase.setProlog(prolog);
			} else if (lname.equalsIgnoreCase(InterestedNode.Epilog)) {
				Step epilog = new Step();
				scriptcontext = 2;
				epilog.setComment("epilog");
				testcase.setEpilog(epilog);
			} else if (lname.equalsIgnoreCase(InterestedNode.Steps)) {
				scriptcontext = 3;
			} else if (lname.equalsIgnoreCase(InterestedNode.Step)) {
				Step step = new Step();
				for (int i = 0; i < attrs.getLength(); ++i) {
					if (attrs.getLocalName(i).equalsIgnoreCase(
							InterestedAttrs.Index)) {
						step.setIndex(Integer.parseInt(attrs.getValue(i)));
					} else if (attrs.getLocalName(i).equalsIgnoreCase(
							InterestedAttrs.Comment)) {
						step.setComment(attrs.getValue(i));
					} else if (attrs.getLocalName(i).equalsIgnoreCase(
							InterestedAttrs.Ext)) {
						step.setExtenstion(attrs.getValue(i));
					}
				}
				testcase.addStep(step);
			} else if (lname.equalsIgnoreCase(InterestedNode.Action)) {
				String proto = attrs.getValue(InterestedAttrs.Proto);
				if (proto != null) {
					Action ac = new Action();
					ac.setActionType(Action.Type.valueOf(proto.toUpperCase()));
					switch (scriptcontext) {
					case 1:
						testcase.getProlog().addAction(ac);
						break;
					case 2:
						testcase.getEpilog().addAction(ac);
						break;
					case 3:
						testcase.getLastStep().addAction(ac);
						break;
					}
				} else {
					Logger.warning(Constants.InvalidAction);
				}
			} else if (lname.equalsIgnoreCase(InterestedNode.Verify)) {

				String type, object, value, comment, domain, param, context;
				int retry = 0, interval = 0, dataindex = 0;

				type = attrs.getValue(InterestedAttrs.Type);
				object = attrs.getValue(InterestedAttrs.Object);
				value = attrs.getValue(InterestedAttrs.Value);
				comment = attrs.getValue(InterestedAttrs.Comment);
				domain = attrs.getValue(InterestedAttrs.Domain);
				param = attrs.getValue(InterestedAttrs.Param);
				context = attrs.getValue(InterestedAttrs.Context);

				if (attrs.getValue(InterestedAttrs.Retry) != null) {
					retry = Integer.parseInt(attrs
							.getValue(InterestedAttrs.Retry));
				}
				if (attrs.getValue(InterestedAttrs.Interval) != null) {
					retry = Integer.parseInt(attrs
							.getValue(InterestedAttrs.Interval));
				}
				if (attrs.getValue(InterestedAttrs.DI) != null) {
					retry = Integer
							.parseInt(attrs.getValue(InterestedAttrs.DI));
				}
				// maybe there is null value for these properties, but that
				// won't has bad side effect when
				// using this Verification object.
				Verification verif = new Verification(type, object, value, param, retry, interval, dataindex);

				switch (scriptcontext) {
				case 1:
					testcase.getProlog().addVerif(verif);
					break;
				case 2:
					testcase.getEpilog().addVerif(verif);
					break;
				case 3:
					testcase.getLastStep().addVerif(verif);
					break;
				}
			} else if (lname.equalsIgnoreCase(InterestedNode.Param)) {
				Action ac = null;
				switch (scriptcontext) {
				case 1:
					ac = testcase.getProlog().getLastAction();
					break;
				case 2:
					ac = testcase.getEpilog().getLastAction();
					break;
				case 3:
					ac = testcase.getLastStep().getLastAction();
					break;
				}
				String value = attrs.getValue(InterestedAttrs.Value);
				if (ac != null && value != null)
					ac.addParam(value.trim());
			}
		}

		public TestCase getTestCase() {
			return testcase;
		}
	}

	/**
	 * 
	 * extracts test data from data file, although data items in data file are
	 * organized in tree structure, this method replaces this tree structure
	 * with hash map
	 * 
	 * @param testcase
	 * 
	 */
	public void buildDataCache(File fData, TestCase testcase) {
		// use DOM API to read test data in xml file
		Document doc = getDocument(fData);
		if (doc != null) {
			Element root = doc.getDocumentElement();
			String rootname = root.getAttribute(InterestedAttrs.DName);
			// parse node named as "data"
			if (rootname == null) {
				return;
			}
			// parse nodes named as "item"
			NodeList dataitems = root.getChildNodes();
			for (int i = 0; i < dataitems.getLength(); ++i) {
				if (dataitems.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element keyNode = (Element) dataitems.item(i);
					String keyName = keyNode.getAttribute(InterestedAttrs.DKey);
					// ---parse nodes named as "value" start---

					// first create a list to store value of data item
					ArrayList<String> values = new ArrayList<String>();
					NodeList valueitems = keyNode.getChildNodes();

					for (int j = 0; j < valueitems.getLength(); ++j) {

						if (valueitems.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element valueNode = (Element) valueitems.item(j);
							// String index = valueNode
							// .getAttribute(InterestedAttrs.DIndex);
							// int iIdx = Integer.parseInt(index);
							String value = valueNode.getTextContent();
							if (value != null) {
								values.add(value);
							}
						}
					}
					// put it into data cache
					TestCase.addToDataCache(Constants.Dollar + rootname.trim()
							+ Constants.Separator + keyName.trim(), values);
					// ---parse nodes named as "value" end---
				}
			}
		} else {
			testcase.addException(new Exception(BundleReader.getInstance()
					.getMessage(Constants.InvalidDataFile)));
		}
	}

	/**
	 * 
	 * @param testcase
	 * @param mapCache
	 * @return
	 */
	public void buildMapCache(File fMap, TestCase testcase) {
		Document doc = getDocument(fMap);
		if (doc != null) {
			Element root = doc.getDocumentElement();

			String rootName = root.getAttribute(InterestedAttrs.MName);

			if (rootName == null) {
				return;
			}

			// parse second level "object" node
			NodeList testObjects = root.getChildNodes();
			for (int i = 0; i < testObjects.getLength(); ++i) {
				if (testObjects.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element to = (Element) testObjects.item(i);
					String toName = to.getAttribute(InterestedAttrs.MName);
					toName = toName.trim();
					// ---parse nodes named as "prop" start---

					HashMap<String, String> pair = new HashMap<String, String>();
					NodeList props = to.getChildNodes();

					for (int j = 0; j < props.getLength(); ++j) {

						if (props.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element propNode = (Element) props.item(j);
							String propName = propNode
									.getAttribute(InterestedAttrs.MName);
							// String index = valueNode
							// .getAttribute(InterestedAttrs.DIndex);
							// int iIdx = Integer.parseInt(index);
							String propValue = propNode.getTextContent();
							if (propName != null) {
								pair.put(propName, propValue);
							}
						}
					}
					// put it into map cache
					TestCase.addToMapCache(rootName + Constants.Separator
							+ toName, pair);
					// ---parse nodes named as "prop" end---
				}
			}
		} else {
			testcase.addException(new Exception(BundleReader.getInstance()
					.getMessage(Constants.InvalidMapFile)));
		}
	}

	private Document getDocument(File xmlFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			return doc;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * parse abstract test script that is in xml format, build a TestObject as
	 * parsing is going on. get count of all test elements. use SAX to parse
	 * 
	 * @param filePath
	 * @return
	 */
	public TestCase build(File scriptPath) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			CaseFileHandler cfh = new CaseFileHandler();
			saxParser.parse(scriptPath, cfh);
			return cfh.getTestCase();
		} catch (SAXException e) {
			return generateErrorTestCase(e);
		} catch (IOException e) {
			return generateErrorTestCase(e);
		} catch (ParserConfigurationException e) {
			return generateErrorTestCase(e);
		}
	}

	private TestCase generateErrorTestCase(Exception e) {
		TestCase tc = new TestCase();
		tc.addException(e);
		return tc;
	}
}
