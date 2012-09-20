package com.xl.webunit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.xl.webunit.util.Util;

public class UtilTest {

	@Test
	public void normalEscape() {
		List<String> expected = new ArrayList<String>();
		expected.add("abc");
		expected.add("cbd");
//		assertArrayEquals(expected.toArray(), Util.asList("abc;cbd").toArray());
	}
	
	@Test
	public void testHeadingEscape() {
		List<String> expected = new ArrayList<String>();
		expected.add("");
		expected.add("abc");
		expected.add("cbd");
//		assertArrayEquals(expected.toArray(), Util.asList(";abc;cbd").toArray());
	}
	
	@Test
	public void testTailingEscape() {
		List<String> expected = new ArrayList<String>();
		expected.add("abc");
		expected.add("cbd");
		expected.add("");
//		assertArrayEquals(expected.toArray(), Util.asList("abc;cbd;").toArray());
	}
	
	@Test
	public void multiEscape() {
		List<String> expected = new ArrayList<String>();
		expected.add("abc;cbd");
		expected.add("cbd");
		expected.add("efg");
//		assertArrayEquals(expected.toArray(), Util.asList("abc;;cbd;cbd;efg").toArray());
	}
	
	@Test
	public void noEscape() {
		List<String> expected = new ArrayList<String>();
		expected.add("abcde");
//		assertArrayEquals(expected.toArray(), Util.asList("abcde").toArray());
	}
}
