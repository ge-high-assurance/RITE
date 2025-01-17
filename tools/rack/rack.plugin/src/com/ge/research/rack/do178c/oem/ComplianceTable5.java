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
import com.ge.research.rack.do178c.structures.Requirement;
import com.ge.research.rack.do178c.structures.SwComponent;

/**
 * @author Saswata Paul
 */
public class ComplianceTable5 {

    /**
     * Harcoded logic in absence of review logs
     *
     * <p>TODO: replace with specific and detailed logic
     *
     * @param objective
     * @return
     */
    public static PsacNode.Objective processObjectiveA5_1_2_3_4(PsacNode.Objective objective) {

        objective.setPassed(false);
        objective.setPartialData(false);
        objective.setNoData(false);
        objective.setMetrics("0.0% tests have review logs");

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }

    public static PsacNode.Objective processObjectiveA5_5(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SwComponent 2. all the
         * associated SwComponents in the output have some SubDD requirement trace
         */
        int numSwComp = objective.getObjOutputs().getSwComponents().size();
        int numSwCompWithSubDDTrace = 0;
        int numSwCompWithNoSubDDTrace = 0;

        for (SwComponent swComp : objective.getObjOutputs().getSwComponents()) {
            if (swComp.getWasImpactedBy().size() < 1) { // no trace
                numSwCompWithNoSubDDTrace = numSwCompWithNoSubDDTrace + 1;
            } else { // some trace
                Boolean hasSubDDTrace = false;
                for (Requirement req : swComp.getWasImpactedBy()) {
                    if (req.getType().equalsIgnoreCase("SubDD_Req")) {
                        hasSubDDTrace = true;
                        break;
                    }
                }
                if (hasSubDDTrace) {
                    numSwCompWithSubDDTrace = numSwCompWithSubDDTrace + 1;
                } else {
                    // nothing
                }
            }
        }

        if (numSwComp > 1) { // some SwComp
            if (numSwCompWithSubDDTrace == 0) {
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                objective.setMetrics("0.0% SwComponents have SubDD trace");
            } else {
                if (numSwCompWithSubDDTrace == numSwComp) {
                    objective.setPassed(true);
                    objective.setPartialData(false);
                    objective.setNoData(false);
                    objective.setMetrics("100.0% SwComponents have SubDD trace");
                } else {
                    objective.setPassed(false);
                    objective.setPartialData(false);
                    objective.setNoData(false);
                    double percent = ((double) (numSwCompWithSubDDTrace) / numSwComp) * 100.00;
                    objective.setMetrics(
                            String.format("%.2f", percent) + "% SwComponents have SubDD trace");
                }
            }

        } else { // no SwComp
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(true);
            objective.setMetrics("No SwComponents");
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }
}
