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
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.arp4754.utils.ComplianceUtils;

public class ComplianceProcess2 {

    private static DAPlan.Objective computeObjective1(DAPlan.Objective objective) {

        return objective;
    }

    /**
     * 2.2: Aircraft functions are allocated to systems, Output: System Requirements with Interfaces
     * Check that there are a set of system requirements and each system requirement is allocated to
     * some system Check that each system linked to a system requirement has a set of associated
     * interfaces
     *
     * <p>Main output - System requirements, so if a system requirement meets all these criteria,
     * make it pass. Otherwise make it fail.
     *
     * <p>Decide the compliance status of the objective as: passed SysReqs/total SysReqs
     *
     * @param objective
     * @return
     */
    private static DAPlan.Objective computeObjective2(DAPlan.Objective objective) {

        int numSysReqsWithInterface = 0;

        if (objective.getOutputs().getSysReqObjs() != null) {
            for (Evidence sysReq : objective.getOutputs().getSysReqObjs()) {
                if (sysReq.getAllocatedTo() != null) {
                    for (Evidence system : sysReq.getAllocatedTo()) {
                        if (system.getHasInterfaces() != null) {
                            numSysReqsWithInterface++;
                        } else {
                            objective.setNoData(false);
                            objective.setPartialData(true);
                            objective.setPassed(false);
                        }
                    }
                } else {
                    objective.setNoData(false);
                    objective.setPartialData(false);
                    objective.setPassed(false);
                }
            }
            objective.setComplianceStatus(
                    (double) numSysReqsWithInterface
                            / objective.getOutputs().getSysReqObjs().size()
                            * 100.00);
            if (numSysReqsWithInterface == objective.getOutputs().getSysReqObjs().size()) {
                objective.setNoData(false);
                objective.setPartialData(false);
                objective.setPassed(true);
            }
        } else {
            objective.setNoData(true);
            objective.setPartialData(false);
            objective.setPassed(false);
        }

        return objective;
    }

    private static DAPlan.Objective computeObjective3(DAPlan.Objective objective) {

        return objective;
    }

    /**
     * 2.4: System derived requirements including assumptions and system interfaces are defined,
     * Output: System Requirements Check that there are a set of derived system requirements
     *
     * <p>Main output - Derived System Requirements
     *
     * @param objective
     * @return
     */
    private static DAPlan.Objective computeObjective4(DAPlan.Objective objective) {

        if (objective.getOutputs().getDerSysReqObjs() != null) {
        	if(objective.getOutputs().getDerSysReqObjs().size() > 0) {
                objective.setComplianceStatus(100.0);
            	objective.setNoData(false);	
                objective.setPartialData(false);
                objective.setPassed(true);        		
        	}
        } else {
            objective.setNoData(true);
            objective.setPartialData(false);
            objective.setPassed(false);
        }

        return objective;
    }

    private static DAPlan.Objective computeObjective5(DAPlan.Objective objective) {

        return objective;
    }

    /**
     * 
     * 2.6 System requirements are allocated to items, Output: Item requirements
		Check that there is a set of item requirements
		Each item requirement has a trace to a system requirement
		Each item requirement has been allocated to an Item

		Main output - Item Reqs
     * @param objective
     * @return
     */
    private static DAPlan.Objective computeObjective6(DAPlan.Objective objective) {

    	int numItemReqsWithTrace=0;
    	int numItemReqsWithAllocation=0;
    	int numItemreqsWithTraceAndAllocation=0;
    	
    	if(objective.getOutputs().getItemReqObjs()!=null) {
    		for(Evidence itemReq : objective.getOutputs().getItemReqObjs()) {
    			boolean trace = false;
    			boolean allocation = false;
    			if(itemReq.getTracesUp()!=null) {
    				numItemReqsWithTrace++;
    				trace=true;
    			}
    			if(itemReq.getAllocatedTo()!=null) {
    				numItemReqsWithAllocation++;
    				allocation=true;
    			}	
    			if(trace && allocation) {
    				numItemreqsWithTraceAndAllocation++;
    			}
    		}
    		if(numItemreqsWithTraceAndAllocation == objective.getOutputs().getItemReqObjs().size()) {
            	objective.setNoData(false);	
                objective.setPartialData(false);
                objective.setPassed(true);
    		}
    		else {
            	objective.setNoData(false);	
                objective.setPartialData(true);
                objective.setPassed(false);    			
    		}
    		objective.setComplianceStatus((double) numItemreqsWithTraceAndAllocation/objective.getOutputs().getItemReqObjs().size() * 100.00);
    	}
    	else {
        	objective.setNoData(true);	
            objective.setPartialData(false);
            objective.setPassed(false);
    	}
    	
        return objective;
    }

    private static DAPlan.Objective computeObjective7(DAPlan.Objective objective) {

        return objective;
    }

    /**
     * Computes the compliance status of the DAPlan.process object
     *
     * @param process
     * @return
     */
    public static DAPlan.Process computeProcess(DAPlan.Process process) {

        int numPassed = 0;
        int numNoData = 0;
        int numPartialData = 0;

        for (int i = 0; i < process.getObjectives().size(); i++) {

            DAPlan.Objective objective = process.getObjectives().get(i);

            DAPlan.Objective updatedObjective = new DAPlan().new Objective();

            switch (objective.getId()) {
                case "Objective-2-1":
                    updatedObjective = computeObjective1(objective);
                    break;
                case "Objective-2-2":
                    updatedObjective = computeObjective2(objective);
                    break;
                case "Objective-2-3":
                    updatedObjective = computeObjective3(objective);
                    break;
                case "Objective-2-4":
                    updatedObjective = computeObjective4(objective);
                    break;
                case "Objective-2-5":
                    updatedObjective = computeObjective5(objective);
                    break;
                case "Objective-2-6":
                    updatedObjective = computeObjective6(objective);
                    break;
                case "Objective-2-7":
                    updatedObjective = computeObjective7(objective);
                    break;
                default:
                    break;
            }

            // get metrics
            if (updatedObjective.isPassed()) {
                numPassed++;
            } else {
                if (updatedObjective.isPartialData()) {
                    numPartialData++;
                } else {
                	if(updatedObjective.isNoData()) {
                        numNoData++;                		
                	}
                }
            }

            System.out.println(
                    "Objective "
                            + updatedObjective.getId()
                            + " compliance status: "
                            + updatedObjective.getComplianceStatus());
            
            System.out.println(updatedObjective.getId() + " no: " + updatedObjective.isNoData() + " partial:" + updatedObjective.isPartialData() + " pass:" + updatedObjective.isPassed() );


            // replace old objective node with new node
            process.getObjectives().set(i, updatedObjective);
        }
        

        // add metrics to process
        process.setNumObjectivesNoData(numNoData);
        process.setNumObjectivesPartialData(numPartialData);
        process.setNumObjectivesPassed(numPassed);
        
        // set process status metrics
        process = ComplianceUtils.getProcessStatus(process);


        return process;
    }
}
