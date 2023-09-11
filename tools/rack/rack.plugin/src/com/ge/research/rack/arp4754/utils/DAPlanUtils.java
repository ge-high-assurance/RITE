/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2023, General Electric Company and Galois, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ge.research.rack.arp4754.utils;

import com.ge.research.rack.arp4754.structures.DAPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
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

        for (int i = 0; i < objectives.size(); i++) {
            DAPlan.Objective objective = objectives.get(i);
            if (objective.getId().equalsIgnoreCase(id)) {
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

        for (int i = 0; i < processes.size(); i++) {
            DAPlan.Process process = processes.get(i);
            if (process.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return null;
    }
    
    
    /**
     * Sorts a given list of DAPLan Processes by id
     * @return
     */
    public static List<DAPlan.Process> sortProcessList(List<DAPlan.Process> list){
    	List<DAPlan.Process> newList = new ArrayList<DAPlan.Process>();
    	
        if(list.size() > 0) {
            for(int i=0; i < list.size(); i++) {
            	for(DAPlan.Process process : list) {
                	if((Integer.parseInt(process.getId().replace("Process-", "")) - 1) == i) {
                		newList.add(process);
                		
                		System.out.println(process.getId() + " -> "+ i );
                	}            		
            	}
            }
        }
        
//      for(int i = 0; i<labelList.size();i++) {
//    	// find the appropriate label for this index
//    	for(Label label : labelList) {
//    		if(i == (Integer.parseInt(getProcessIdFromLabelText(label.getText()))-1)){
//    			sortedLabelList.add(label);
//    		}
//    	}
//    }
    	
    	
    	return newList;
    }
}
