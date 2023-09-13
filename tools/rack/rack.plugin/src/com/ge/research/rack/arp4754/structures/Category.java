/**
 * 
 */
package com.ge.research.rack.arp4754.structures;

/**
 * a category name and value pair for GraphData
 * @author Saswata Paul
 *
 */
public class Category{
	
	private String name;
	private int value;
	
	
	
	/**
	 * @param name
	 * @param value
	 */
	public Category(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	

}