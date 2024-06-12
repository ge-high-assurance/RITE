/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, General Electric Company and Galois, Inc.
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
package com.ge.research.rack.arp4754.constants;

public class HepsHardCoded {

    public static final String allAircraftReqsString =
            // "description,identifier\r\n"
            "The Aircraft shall have a maximum payload of 30 passengers and 1 flight attendant.,23\r\n"
                    + "The Aircraft shall be capable of taking off from a 6000 ft runway at MTOW.,24\r\n"
                    + "\"The Aircraft shall have a useable range of 1,200 NM\",25\r\n"
                    + "\"The Aircraft shall have a service ceiling of 30,000 ft\",26\r\n"
                    + "The Aircraft shall have a two member flight crew.,27\r\n"
                    + "The Aircraft shall have a cruise speed of 300 kts.,28";

    public static final String[] allAircraftReqs = allAircraftReqsString.split("\\r?\\n");

    public static final String allRequirementCompleteCorrectReviewString =
            // "REVIEW_LOG_description,RequirementCompleteCorrectReview_id,result,ENTITY_in_REVIEW_LOG\r\n"
            "Completeness Analysis Results (AutomatedControl) : Complete Requirements,RCompReview7,Passed,CAC3\r\n"
                    + "Completeness Analysis Results (AutomatedControl) : Complete Requirements,RCompReview7,Passed,CAC4\r\n"
                    + "Completeness Analysis Results (AutomatedControl) : Complete Requirements,RCompReview7,Passed,CAC5\r\n"
                    + "Completeness Analysis Results (HLRs) : Incomplete Requirements,RCompReview2,Revise With Review,31\r\n"
                    + "Completeness Analysis Results (HLRs) : Incomplete Requirements,RCompReview2,Revise With Review,32\r\n"
                    + "Completeness Analysis Results (HLRs) : Incomplete Requirements,RCompReview2,Revise With Review,33\r\n"
                    + "Completeness Analysis Results (HLRs) : Incomplete Requirements,RCompReview2,Revise With Review,34\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,92\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,93\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,94\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,95\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,96\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,84\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,86\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,87\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,88\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,89\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,90\r\n"
                    + "Completeness Analysis Results (PropulsionControlReqs) : Incomplete Requirements,RCompReview3,Revise With Review,91\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR1\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR2\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR3\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR4\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR5\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR6\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR7\r\n"
                    + "Contingent (DCACCtrReqs) : Contingent Requirements,RContReview1,Passed,DCACCtrR8\r\n"
                    + "Pair-wise Requirements Dependency Analysis (PropulsionControlReqs) : All except for the ones in the DQ column in package PropulsionControlReqs,RIndReview2,Revise With Review,94\r\n"
                    + "Pair-wise Requirements Dependency Analysis (PropulsionControlReqs) : All except for the ones in the DQ column in package PropulsionControlReqs,RIndReview2,Revise With Review,96\r\n"
                    + "Completeness Analysis Results (Aircraft) : Complete Requirements,RCompReview9,Passed,CAC1\r\n"
                    + "Completeness Analysis Results (Aircraft) : Complete Requirements,RCompReview9,Passed,CAC2\r\n"
                    + "Completeness Analysis Results (CockpitControl) : Incomplete Requirements,RCompReview4,Revise With Review,CCC5\r\n"
                    + "Completeness Analysis Results (CockpitControl) : Incomplete Requirements,RCompReview4,Revise With Review,CCC3\r\n"
                    + "Completeness Analysis Results (CockpitControl) : Incomplete Requirements,RCompReview4,Revise With Review,CCC14\r\n"
                    + "Completeness Analysis Results (CockpitControl) : Incomplete Requirements,RCompReview4,Revise With Review,CCC12\r\n"
                    + "Completeness Analysis Results (CockpitControl) : Incomplete Requirements,RCompReview4,Revise With Review,CCC13\r\n"
                    + "Completeness Analysis Results (CockpitControl) : Incomplete Requirements,RCompReview4,Revise With Review,CCC10\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,C1\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,C2\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,C3\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,97\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,98\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,99\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,100\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,101\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,102\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,C6\r\n"
                    + "Completeness Analysis Results (Control) : Incomplete Requirements,RCompReview1,Revise With Review,C7\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR1\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR2\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR3\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR4\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR5\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR6\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR7\r\n"
                    + "Completeness Analysis Results (DCACCtrReqs) : Complete Requirements,RCompReview6,Passed,DCACCtrR8\r\n"
                    + "Globally Unsatisfiable Requirements Package (HLRs),RGContReview1,Revise With Review,31\r\n"
                    + "Globally Unsatisfiable Requirements Package (HLRs),RGContReview1,Revise With Review,32\r\n"
                    + "Globally Unsatisfiable Requirements Package (HLRs),RGContReview1,Revise With Review,33\r\n"
                    + "Globally Unsatisfiable Requirements Package (HLRs),RGContReview1,Revise With Review,34\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,C1\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,97\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,98\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,99\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,100\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,101\r\n"
                    + "Contingent (Control) : Contingent Requirements,RContReview2,Passed,102\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR1\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR2\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR3\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR4\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR5\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR6\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR7\r\n"
                    + "Completeness Analysis Results (DCDCCtrReqs) : Complete Requirements,RCompReview8,Passed,DCDCCtrR8";

    public static final String[] allRequirementCompleteCorrectReview =
            allRequirementCompleteCorrectReviewString.split("\\r?\\n");
}
