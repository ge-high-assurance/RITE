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
package com.ge.research.rack.arp4754.logic;

import com.ge.research.rack.arp4754.structures.DAPlan;

/**
 * @author 212807042
 */
public class ComplianceDAP {	
	
	
	
	/**
	 * Computes the compliance status of a DAPlan object 
	 * 
	 * @param plan
	 * @return
	 */
	public static DAPlan compute(DAPlan plan) {
		
		
		int passedProcessCounter = 0;
		
		// for every process in the plan, send the process to the appropriate function and replace the result in the appropriate position of the plan
		for(int i=0; i<=plan.getProcesses().size(); i++  ) {
			DAPlan.Process process = plan.getProcesses().get(i);
			
			// Compute the process compliance
			DAPlan.Process updatedProcess = new DAPlan().new Process();
			switch(process.getId()) {
            case "Process-1":
            	updatedProcess = ComplianceProcess1.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
                break;
            case "Process-2":
            	updatedProcess = ComplianceProcess2.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;    
            case "Process-3":
            	updatedProcess = ComplianceProcess3.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
            case "Process-4":
            	updatedProcess = ComplianceProcess4.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
            case "Process-5":
            	updatedProcess = ComplianceProcess5.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
            case "Process-6":
            	updatedProcess = ComplianceProcess6.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
            case "Process-7":
            	updatedProcess = ComplianceProcess7.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
            case "Process-8":
            	updatedProcess = ComplianceProcess8.computeProcess(process);
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
            default:
            	if(updatedProcess.isPassed()) {
            		passedProcessCounter++;
            	}
            	break;
			}
			
			// replace old process object with updated object
			plan.getProcesses().set(i, updatedProcess);
						
		}

		// Compute the compliance status of plan
		plan.setComplianceStatus((double) passedProcessCounter/plan.getProcesses().size());
		
		return plan;
	}
	
	
	
}
