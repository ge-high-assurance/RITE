/** */
package com.ge.research.rack.report.boeingPsac;

import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.utils.LogicUtils;

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
