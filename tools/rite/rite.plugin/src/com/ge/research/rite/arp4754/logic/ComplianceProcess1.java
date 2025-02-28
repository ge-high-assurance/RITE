/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite.arp4754.logic;

import com.ge.research.rite.arp4754.structures.DAPlan;
import com.ge.research.rite.arp4754.structures.DAPlan.Objective;
import com.ge.research.rite.arp4754.structures.Evidence;
import com.ge.research.rite.arp4754.utils.ComplianceUtils;

public class ComplianceProcess1 {

    private static DAPlan.Objective computeObjective1(DAPlan.Objective objective) {

        if (objective.getOutputs().getDocumentObjs() != null
                && objective.getOutputs().getDocumentObjs().size() > 0) {

            int numRequiredDocs = 0;

            for (Evidence document : objective.getOutputs().getDocumentObjs()) {
                System.out.println(document.getId());
                if (document.getId().equalsIgnoreCase("CertificationPlan")
                        || document.getId().equalsIgnoreCase("SafetyProgramPlan")
                        || document.getId().equalsIgnoreCase("DevelopmentPlan")
                        || document.getId().equalsIgnoreCase("ValidationPlan")
                        || document.getId().equalsIgnoreCase("VerificationPlan")
                        || document.getId().equalsIgnoreCase("ConfigurationManagementPlan")
                        || document.getId().equalsIgnoreCase("ProcessAssurancePlan"))
                    numRequiredDocs++;
            }
            if (numRequiredDocs == 7) {
                objective.setNoData(false);
                objective.setPartialData(false);
                objective.setPassed(true);
                objective.setComplianceStatus(100.00);
            } else if (numRequiredDocs > 0) {
                objective.setNoData(false);
                objective.setPartialData(true);
                objective.setPassed(false);
                objective.setComplianceStatus((double) numRequiredDocs / 7 * 100.0);
            } else if (numRequiredDocs == 0) {
                objective.setNoData(true);
                objective.setPartialData(false);
                objective.setPassed(false);
            }
        } else {
            objective.setNoData(true);
            objective.setPartialData(false);
            objective.setPassed(false);
        }

        objective.setMetrics("");
        return objective;
    }

    private static DAPlan.Objective computeObjective2(DAPlan.Objective objective) {

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
                case "Objective-1-1":
                    updatedObjective = computeObjective1(objective);
                    break;
                case "Objective-1-2":
                    updatedObjective = computeObjective2(objective);
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
                    if (updatedObjective.isNoData()) {
                        numNoData++;
                    }
                }
            }

            System.out.println(
                    "Objective "
                            + updatedObjective.getId()
                            + " compliance status: "
                            + updatedObjective.getComplianceStatus());

            System.out.println(
                    updatedObjective.getId()
                            + " no: "
                            + updatedObjective.isNoData()
                            + " partial:"
                            + updatedObjective.isPartialData()
                            + " pass:"
                            + updatedObjective.isPassed());

            // replace old objective node with new node
            process.getObjectives().set(i, updatedObjective);
        }

        // add metrics to process
        process.setNumObjectivesNoData(numNoData);
        process.setNumObjectivesPartialData(numPartialData);
        process.setNumObjectivesPassed(numPassed);

        // set process status metrics
        process = ComplianceUtils.getProcessStatus(process);

        process.setMetrics("");
        return process;
    }
}
