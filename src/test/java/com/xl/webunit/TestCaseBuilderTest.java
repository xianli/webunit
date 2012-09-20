package com.xl.webunit;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.xl.webunit.engine.TestCaseBuilder;
import com.xl.webunit.framework.Action;
import com.xl.webunit.framework.Step;
import com.xl.webunit.framework.TestCase;
import com.xl.webunit.framework.TestResult;
import com.xl.webunit.util.Constants;
import com.xl.webunit.util.Util;

import static com.xl.webunit.framework.TestCase.*;

public class TestCaseBuilderTest {

	private static TestCaseBuilder builder;
	private static TestCase tc;
	@BeforeClass
	public static void createTestCaseBuilder() {
		builder = new TestCaseBuilder();
		
		Util.setBasePath("G:/testtoolkit/testgo/doc/scripts/");
		String fCasePath = Util.getBasePath() + "cases"
		+ Util.Separator + "SQO01_new.xml";
		tc = builder.build(new File(fCasePath));
//		builder.buildDataCache(dataCache, fData);
//		builder.buildMapCache(mapCache, new File(
//				"SQO_LoginPage.xml"));
//		builder.buildMapCache(mapCache, new File(
//				"SQO_DSWOnline.xml"));
//		builder
//				.buildMapCache(
//						mapCache,
//						new File(
//								"SQO_SoftwareQuoteAndOrder.xml"));
	}

	@Test
	public void buildTestCase() {
		
		assertEquals("SQO01_new", tc.getName());
		assertEquals(25, tc.getStepCount());

		// assert last step

		Step step2 = tc.getLastStep();
		assertEquals(120, step2.getIndex());
		assertEquals(
				"Click Download quote as rich text file button and save the file,Check this file.",
				step2.getComment());
		List<Action> actions = step2.getActions();
		assertEquals(5, actions.size());
		Action firstAction = actions.get(0);
		assertEquals(Action.Type.CLICK, firstAction.getActionType());
		List<String> params = firstAction.getParams();
		assertEquals(1, params.size());
		assertEquals("MyCurrentQuote:DownloadQuote_button", params.get(0));
	}

	@Test
	public void testResult() {
		TestResult r = TestResult.getInstance();
		tc.connectTo(r);
		tc.count();
		assertEquals(1, r.getTestCount());
		assertEquals(27, r.getStepCount());
		System.out.println(r.getActionCount());
		System.out.println(r.getVerifCount());
	}
	@Test
	public void buildDataCache_OneValue() {
		List<String> expected = new ArrayList<String>();
		expected.add("password");
		List<String> actual = dataCache.get("$web:password");
//		assertArrayEquals(expected.toArray(), actual.toArray());
	}

	@Test
	public void buildDataCache_TwoValues() {

		List<String> expected = new ArrayList<String>();
		expected
				.add("https://w3-117uat.etl.ibm.com/software/sales/passportadvantage/telesales");
		expected
				.add("https://w3-117fvt.etl.ibm.com/software/sales/passportadvantage/telesales");
		List<String> actual = dataCache.get("$web:DSWOnlineURL");
//		assertArrayEquals(expected.toArray(), actual.toArray());
		// assertEquals("", builder.buildDataCache(dataCache, fData))
	}

	@Test
	public void buildMapCache() {
		assert (mapCache.get("LoginPage:Username_input") != null);
		assertEquals("logonID", mapCache.get("LoginPage:Username_input").get(
				"value"));
		assertEquals("Html.INPUT.submit", mapCache.get(
				"LoginPage:Submit_button").get("classID"));
		assertEquals(".text", mapCache.get("DSWOnline:SoftQuoteOrder_Link")
				.get("propertyName"));
		assertEquals("link", mapCache.get(
				"SoftQuoteAndOrd:CreateSalesQuote_Link").get("type"));
		assertEquals("Quote Status", mapCache
				.get("SoftQuoteAndOrd:Status_Link").get("value"));
	}

//	public void search() {
//		HTMLElement he = createElement();
//
//		assertEquals("id", HTMLElement.search(he,
//				"OnTARGET:SignInTable:btnSubmit").getPropertyName());
//		assertEquals("username", HTMLElement
//				.search(he, "OnTARGET:textusername").getValue());
//		HTMLElement.search(null, "OnTARGET:textusername");
//		HTMLElement.search(he, null);
//	}
//
//	private HTMLElement createElement() {
//		HTMLElement root = new HTMLElement("OnTARGET");
//		HTMLElement ch1 = new HTMLElement("SignInTable", "name", "signin",
//				"table");
//		HTMLElement ch2 = new HTMLElement("textusername", "id", "username",
//				"text");
//		HTMLElement ch1_1 = new HTMLElement("btnSubmit", "id", "signin",
//				"button");
//		ch1.addChild(ch1_1);
//		root.addChild(ch2);
//		root.addChild(ch1);
//		return root;
//	}
}
