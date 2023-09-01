/**
 * 
 */
package com.ge.research.rack.arp4754.utils;

import java.util.List;

import com.ge.research.rack.arp4754.structures.DAPlan;

/**
 * @author Saswata Paul
 *
 */
public class DAPlanUtils {

	/**
	 * Takes a list of objective objects and an id and returns the object with the id
	 * 
	 * @param objectives
	 * @param id
	 * @return
	 */
	public static Integer getObjectiveObjectFromList(List<DAPlan.Objective> objectives, String id) {
		
		for(int i =0; i < objectives.size(); i++) {
			DAPlan.Objective objective = objectives.get(i);
			if(objective.getId().equalsIgnoreCase(id)) {
				return i;
			}
		}
		return null;
	}


	/**
	 * Takes a list of process objects and an id and returns index of the object with the id
	 * 
	 * @param processes
	 * @param id
	 * @return
	 */
	public static Integer getProcessObjectFromList(List<DAPlan.Process> processes, String id) {
		
		for(int i =0; i < processes.size(); i++) {
			DAPlan.Process process = processes.get(i);
			if(process.getId().equalsIgnoreCase(id)) {
				return i;
			}
		}
		return null;
	}

}
