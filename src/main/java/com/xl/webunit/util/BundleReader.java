package com.xl.webunit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 *
 */
public class BundleReader {

	private Properties cache = new Properties();
	private static BundleReader r;

	private BundleReader() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(
					Constants.ResourceFile);
			cache.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BundleReader getInstance() {
		if (r == null)
			r = new BundleReader();
		return r;
	}

	public String getMessage(String key, String ... params) {
		if (cache.containsKey(key)) {
			
			String value = cache.getProperty(key);
			
			if (params != null && params.length > 0) {
				//replace {*} with actual value
				for (int i=0;i<params.length;++i) {
					value=value.replace("{"+(i+1)+"}", params[i]);
				}
			}
			return value;
		} else
			return "";
	}
}
