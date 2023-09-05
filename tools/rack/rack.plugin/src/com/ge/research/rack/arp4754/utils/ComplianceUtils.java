/**
 * 
 */
package com.ge.research.rack.arp4754.utils;

/**
 * @author Saswata Paul
 *
 */
public class ComplianceUtils {
	
	
	
	/**
	 * Computes the compliance status of a process 
	 * 
	 * @param numPassed
	 * @param numPartial
	 * @param numNoData
	 * @param totalObjectives
	 * @return
	 */
	public static Double processComplianceValue(int numPassed, int numPartial, int numNoData, int totalObjectives) {
		Double stats = 0.0;
		
		stats = ((double)  numPassed /totalObjectives);
		
		return stats;
	}

}
