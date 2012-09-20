package com.xl.webunit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xl.webunit.util.BundleReader;
import com.xl.webunit.util.Constants;

public class BundleReaderTest {

	@Test
	public void read() {
		BundleReader reader = BundleReader.getInstance();
		assertEquals("no object specified",reader.getMessage(Constants.NO_OBJECT_SPECIFIED));
		assertEquals("invalid action",reader.getMessage(Constants.InvalidAction));
	}
	@Test
	public void readWithParam() {
		BundleReader reader = BundleReader.getInstance();
		assertEquals("test a b",reader.getMessage("test", "a", "b"));
	}
	
}
