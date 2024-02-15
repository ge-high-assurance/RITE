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

import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.arp4754.structures.Category;
import com.ge.research.rack.arp4754.structures.Graph;
import com.ge.research.rack.arp4754.structures.Output;
import com.ge.research.rack.arp4754.utils.ComplianceUtils;

/**
 * @author Saswata Paul
 */
public class ComplianceProcess4 {

    private static PlanObjective computeObjective1(PlanObjective objective) {

        // TODO: write logic

        objective.setNoData(false);
        objective.setPartialData(true);
        objective.setPassed(false);

        // create and add appropriate graphdata (//TODO: add actual code, hardcoded now)
        Category reqWithNoReviews =
                new Category(
                        "No Review", ((Output) objective.getOutputs()).getItemReqObjs().size());
        Category reqWithPassedReviews = new Category("Passed Review", 0);
        Category reqWithFailedReviews = new Category("Failed Review", 0);

        if (objective.getGraphs() == null) {
            objective.setGraphs(new Graph());
        }

        ((Graph) objective.getGraphs()).getItemReqGraphData().getBuckets().add(reqWithNoReviews);
        ((Graph) objective.getGraphs())
                .getItemReqGraphData()
                .getBuckets()
                .add(reqWithPassedReviews);
        ((Graph) objective.getGraphs())
                .getItemReqGraphData()
                .getBuckets()
                .add(reqWithFailedReviews);

        objective.setMetrics("");
        return objective;
    }

    private static PlanObjective computeObjective2(PlanObjective objective) {
        return objective;
    }

    private static PlanObjective computeObjective3(PlanObjective objective) {
        return objective;
    }

    private static PlanObjective computeObjective4(PlanObjective objective) {

        // TODO: write logic

        objective.setNoData(false);
        objective.setPartialData(true);
        objective.setPassed(false);

        // create and add appropriate graphdata (//TODO: add actual code, hardcoded now)
        Category reqWithNoReviews =
                new Category(
                        "No Review", ((Output) objective.getOutputs()).getItemReqObjs().size());
        Category reqWithPassedReviews = new Category("Passed Review", 0);
        Category reqWithFailedReviews = new Category("Failed Review", 0);

        if (objective.getGraphs() == null) {
            objective.setGraphs(new Graph());
        }

        ((Graph) objective.getGraphs()).getItemReqGraphData().getBuckets().add(reqWithNoReviews);
        ((Graph) objective.getGraphs())
                .getItemReqGraphData()
                .getBuckets()
                .add(reqWithPassedReviews);
        ((Graph) objective.getGraphs())
                .getItemReqGraphData()
                .getBuckets()
                .add(reqWithFailedReviews);

        objective.setMetrics("");
        return objective;
    }

    private static PlanObjective computeObjective6(PlanObjective objective) {
        return objective;
    }

    /**
     * Computes the compliance status of the DAPlan.process object
     *
     * @param process
     * @return
     */
    public static PlanTable computeProcess(PlanTable process) {

        int numPassed = 0;
        int numNoData = 0;
        int numPartialData = 0;

        for (int i = 0; i < process.getTabObjectives().size(); i++) {
            PlanObjective objective = process.getTabObjectives().get(i);
            PlanObjective updatedObjective = new PlanObjective();

            switch (objective.getId()) {
                case "Objective-4-1":
                    updatedObjective = computeObjective1(objective);
                    break;
                case "Objective-4-2":
                    updatedObjective = computeObjective2(objective);
                    break;
                case "Objective-4-3":
                    updatedObjective = computeObjective3(objective);
                    break;
                case "Objective-4-4":
                    updatedObjective = computeObjective4(objective);
                    break;
                case "Objective-4-6":
                    updatedObjective = computeObjective6(objective);
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
            process.getTabObjectives().set(i, updatedObjective);
        }

        // add metrics to process
        process.setNumObjNoData(numNoData);
        process.setNumObjPartial(numPartialData);
        process.setNumObjPassed(numPassed);

        // set process status metrics
        process = ComplianceUtils.getProcessStatus(process);

        process.setMetrics("");
        return process;
    }
}
