package com.xl.webunit.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Util {

	public static final String Separator = "/";
	
	public static String getFileExt(File file) {
		assert (file != null);
		return file.getName().substring(file.getName().lastIndexOf(".") + 1);
	}
	
	private static String BasePath;

	public static String getBasePath() {
		return BasePath;
	}
	
	public static void setBasePath(String basePath) {
		BasePath = basePath;
	}
	
	public static HashMap<String, String> copy(HashMap<String,String> source) {
		if (source == null) return null;
		else if (!source.isEmpty()) {
			HashMap<String, String> target = new HashMap<String, String>();
			Iterator<String> it = source.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				target.put(key, source.get(key));
			}
			return target;
		}
		else return new HashMap<String, String>(0);
	}
	
	public static ArrayList<String> copy(ArrayList<String> source) {
		if (source==null) return null;
		else if (!source.isEmpty()) {
			ArrayList<String> target = new ArrayList<String>();
			target.addAll(source);
			return target;
		}
		return new ArrayList<String>(0);
	}
	
	public static List<String> asList(String text) {
		int i=0;
		int l=text.length();
		List<String> values = new ArrayList<String>();
		StringBuffer str = new StringBuffer();
		while (i<l) {
			if (text.charAt(i) == ';' ) {
				if (i==l-1) {
					values.add(str.toString());
					str.delete(0, str.length());
				}
				else if (text.charAt(i+1) != ';') {
					values.add(str.toString());
					str.delete(0, str.length());
				} else {
					str.append(';');
					++i;
				}
			} else 
				str.append(text.charAt(i));
			++i;
		}
		values.add(str.toString());
		return values;
	}

	public static void debug(String asXml) {
		final String text = asXml;
		new Thread(new Runnable() {
			public void run() {
				try {
					FileWriter writer = new FileWriter("c:/debug.xml") ;
					writer.write(text);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}).start();
	}
}
