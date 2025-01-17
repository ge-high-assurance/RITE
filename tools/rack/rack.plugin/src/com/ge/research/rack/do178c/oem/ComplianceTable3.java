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
package com.ge.research.rack.do178c.oem;

import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.utils.LogicUtils;

/**
 * @author Saswata Paul
 */
public class ComplianceTable3 {

    /**
     * A rudimentary logic for all table a3 objectives. separate later
     *
     * <p>TODO: replace with specific logic
     *
     * @param objective
     * @return
     */
    public static PsacNode.Objective processObjectiveA3_Common(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SRS_Req 2. all the associated
         * SRS_Req in the output have review log
         */
        //        Boolean srsPresenceFlag = false;
        //        Boolean srsNoLogFlag = false;
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("SRS_Req")) {
        //                srsPresenceFlag = true;
        //                if (req.getLogs().size() < 1) { // no log info
        //                    srsNoLogFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        if (srsPresenceFlag && !srsNoLogFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (srsPresenceFlag && srsNoLogFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (!srsPresenceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(true);
        //        }

        int numSRS = objective.getObjOutputs().getRequirements().size();
        int numSRSWithLogs =
                LogicUtils.getNumReqsWithLogs(objective.getObjOutputs().getRequirements());
        ;

        // TODO: decide no data case
        if (numSRS < 1) { // no SRS
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("0.0% requirements have review logs");
        } else { // Some SRS
            if (numSRSWithLogs == numSRS) { // all have tests
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% requirements have review logs");
            } else if ((0 < numSRSWithLogs) && (numSRSWithLogs < numSRS)) { // some have no tests
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) numSRSWithLogs / numSRS) * 100.00;
                objective.setMetrics(
                        String.format("%.2f", percent) + "% requirements have review logs");
            } else if (numSRSWithLogs == 0) { // none have any test
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("0.0% requirements have logs");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }
}
