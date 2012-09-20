package com.xl.webunit.engine;

import java.util.ArrayList;
import java.util.List;

import com.xl.webunit.util.Constants;

public class HTMLElement {

	private String name;
	private List<HTMLElement> children=new ArrayList<HTMLElement> ();

	private String value;
	private String propertyName;
	private String classID;

	public HTMLElement(String name) {
		this.name = name;
	}
	
	public HTMLElement(String name,
						String propertyName,
						String value,
						String classID) {
		this.name = name;
		this.value = value;
		this.propertyName = propertyName;
		this.classID = classID;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<HTMLElement> getChildren() {
		return children;
	}

	public void addChild(HTMLElement he) {
		children.add(he);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getClassID() {
		return classID;
	}

	public void setClassID(String classID) {
		this.classID = classID;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
	/**
	 * recursively search on the tree structure of all HTML elements that will be used 
	 * during test
	 * @param node
	 * @param qualifiedName
	 * @return target HTML element, or return null if no element matches qualified name 
	 */
	public static HTMLElement search(HTMLElement node, String qualifiedName) {
		assert node!=null;
		assert qualifiedName!=null;
		
		int sepaIdx = qualifiedName.indexOf(Constants.Separator);
		//search quit condition
		if (sepaIdx < 0) {
			if (node.getName().equals(qualifiedName))
				return node;
			else return null;
		} else {
			//search goes into deep level
			String currentSegment = qualifiedName.substring(0, sepaIdx);
			if (node.getName().equals(currentSegment) && node.hasChildren()) {
				HTMLElement target = null;
				for (HTMLElement ele : node.getChildren()) {
					target = search(ele, qualifiedName.substring(sepaIdx + 1));
					if (target != null) 
						break;
				}
				return target;
			}
			else return null;
		}
	}
}
