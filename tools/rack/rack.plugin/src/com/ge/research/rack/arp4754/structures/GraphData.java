/**
 * 
 */
package com.ge.research.rack.arp4754.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 * 
 * To store data to draw graphs on javafx gui
 *
 */
public class GraphData {
	
	
	private String graphTitle = "";

	private List<Category> buckets = new ArrayList<Category>();
	
	/**
	 * @return the buckets
	 */
	public List<Category> getBuckets() {
		return buckets;
	}

	/**
	 * @param buckets the buckets to set
	 */
	public void setBuckets(List<Category> buckets) {
		this.buckets = buckets;
	}

	/**
	 * @return the graphTitle
	 */
	public String getGraphTitle() {
		return graphTitle;
	}

	/**
	 * @param graphTitle the graphTitle to set
	 */
	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
	}

	/**
	 * @param graphTitle
	 */
	public GraphData(String graphTitle) {
		super();
		this.graphTitle = graphTitle;
	}
	
	


}
