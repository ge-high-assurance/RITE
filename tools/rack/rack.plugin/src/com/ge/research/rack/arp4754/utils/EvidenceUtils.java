/**
 * 
 */
package com.ge.research.rack.arp4754.utils;


import java.util.List;

import com.ge.research.rack.arp4754.structures.Evidence;

/**
 * @author Saswata Paul
 *
 */
public class EvidenceUtils {

	
	/**
	 * Given a list of evidence and an id, returns the appropriate evidence object
	 * @param list
	 * @param id
	 * @return
	 */
	public static Evidence getEvidenceObjById(List<Evidence> list, String id) {
		for(Evidence evidence : list) {
			if(evidence.getId().equals(id)) {
				return evidence;
			}
		}
		return null;
	}
	
	
	/**
	 * Given a list of evidence and an id, returns the index of the appropriate evidence object
	 * @param list
	 * @param id
	 * @return
	 */
	public static Integer getEvidenceObjIndxById(List<Evidence> list, String id) {
		for(int i = 0; i < list.size(); i++) {
			Evidence evidence = list.get(i);
			if(evidence.getId().equals(id)) {
				return i;
			}
		}
		return null;
	}
}
