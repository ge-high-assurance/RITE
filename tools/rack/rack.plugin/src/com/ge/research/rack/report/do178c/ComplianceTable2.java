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
package com.ge.research.rack.report.do178c;

import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.utils.LogicUtils;

/**
 * @author Saswata Paul
 */
public class ComplianceTable2 {

    public static PsacNode.Objective processObjectiveA2_1(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SRS_Req 2. all the associated
         * SRS_Req in the output have some trace
         */
        //        Boolean srsPresenceFlag = false;
        //        Boolean srsNoTraceFlag = false;
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("SRS_Req")) {
        //                srsPresenceFlag = true;
        //                if (req.getSatisfies().size() < 1) { // no trace info
        //                    srsNoTraceFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        if (srsPresenceFlag && !srsNoTraceFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (srsPresenceFlag && srsNoTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(true);
        //            objective.setNoData(false);
        //        } else if (!srsPresenceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(true);
        //        }

        int numSRS = objective.getObjOutputs().getRequirements().size();
        int numSRSWithTrace =
                LogicUtils.getNumReqsWithTrace(objective.getObjOutputs().getRequirements());
        ;

        // TODO: decide no data case
        if (numSRS < 1) { // no SRS
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("No requirements");
        } else { // Some SRS
            if (numSRSWithTrace == numSRS) { // all have
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% requirements have trace");
            } else if ((0 < numSRSWithTrace) && (numSRSWithTrace < numSRS)) { // some have no trace
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) numSRSWithTrace / numSRS) * 100.00;
                objective.setMetrics(String.format("%.2f", percent) + "% requirements have trace");
            } else if (numSRSWithTrace == 0) { // none have any test
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("0.0% requirements have trace");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }

    public static PsacNode.Objective processObjectiveA2_2(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated DerSRS_Req 2. No associated
         * DerSRS_Req in the output has a trace to some other req
         */
        //        Boolean derSrsPresenceFlag = false;
        //        Boolean derSrsTraceFlag = false;
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("DerSRS_Req")) {
        //                derSrsPresenceFlag = true;
        //                if (req.getSatisfies().size() > 0) { // some trace info
        //                    derSrsTraceFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        /**
        //         * There is no partial data or no data case
        //         */
        //        if (derSrsPresenceFlag && !derSrsTraceFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (derSrsPresenceFlag && derSrsTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (!derSrsPresenceFlag && derSrsTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        }
        //        else if (!derSrsPresenceFlag && !derSrsTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        }

        int numDerSRS = objective.getObjOutputs().getRequirements().size();
        int numDerSRSWithTrace =
                LogicUtils.getNumReqsWithTrace(objective.getObjOutputs().getRequirements());
        ;

        // TODO: decide no data case
        if (numDerSRS < 1) { // no DerSRS
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("No requirements");
        } else { // Some DerSRS
            if (numDerSRSWithTrace > 0) { // at least one has trace
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                double percent = ((double) (numDerSRS - numDerSRSWithTrace) / numDerSRS) * 100.00;
                objective.setMetrics(percent + "% requirements have trace");

            } else if (numDerSRSWithTrace == 0) { // none have any test
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% requirements have trace");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }

    public static PsacNode.Objective processObjectiveA2_4(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SubDD_Req 2. all the associated
         * SubDD_Req in the output have some trace
         */
        //        Boolean subDDPresenceFlag = false;
        //        Boolean subDDNoTraceFlag = false;
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("SubDD_Req")) {
        //                subDDPresenceFlag = true;
        //                if (req.getSatisfies().size() < 1) { // no trace info
        //                    subDDNoTraceFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        if (subDDPresenceFlag && !subDDNoTraceFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (subDDPresenceFlag && subDDNoTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(true);
        //            objective.setNoData(false);
        //        } else if (!subDDPresenceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(true);
        //        }

        int numSubDD = objective.getObjOutputs().getRequirements().size();
        int numSubDDWithTrace =
                LogicUtils.getNumReqsWithTrace(objective.getObjOutputs().getRequirements());
        ;

        // TODO: decide no data case
        if (numSubDD < 1) { // no SubDD
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("No requirements");
        } else { // Some SubDD
            if (numSubDDWithTrace == numSubDD) { // all have trace
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% requirements have trace");
            } else if ((0 < numSubDDWithTrace)
                    && (numSubDDWithTrace < numSubDD)) { // some have no trace
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) numSubDDWithTrace / numSubDD) * 100.00;
                objective.setMetrics(String.format("%.2f", percent) + "% requirements have trace");
            } else if (numSubDDWithTrace == 0) { // none have any test
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("0.0% requirements have trace");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }

    public static PsacNode.Objective processObjectiveA2_5(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated DerSubDD_Req 2. No associated
         * DerSubDD_Req in the output has a trace to some other req
         */
        //        Boolean derSubDDPresenceFlag = false;
        //        Boolean derSubDDTraceFlag = false;
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("DerSubDD_Req")) {
        //                derSubDDPresenceFlag = true;
        //                if (req.getSatisfies().size() > 0) { // some trace info
        //                    derSubDDTraceFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        /**
        //         * There is no partial data or no data case
        //         */
        //        if (derSubDDPresenceFlag && !derSubDDTraceFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (derSubDDPresenceFlag && derSubDDTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (!derSubDDPresenceFlag && derSubDDTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        }
        //        else if (!derSubDDPresenceFlag && !derSubDDTraceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        }

        int numDerSubDD = objective.getObjOutputs().getRequirements().size();
        int numDerSubDDWithTrace =
                LogicUtils.getNumReqsWithTrace(objective.getObjOutputs().getRequirements());
        ;

        // TODO: decide no data case
        if (numDerSubDD < 1) { // no DerSubDD
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("No requirements");
        } else { // Some DerSubDD
            if (numDerSubDDWithTrace > 0) { // at least one has trace
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                double percent =
                        ((double) (numDerSubDD - numDerSubDDWithTrace) / numDerSubDD) * 100.00;
                objective.setMetrics(String.format("%.2f", percent) + "% requirements have trace");

            } else if (numDerSubDDWithTrace == 0) { // none have any test
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% requirements have trace");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }
}
