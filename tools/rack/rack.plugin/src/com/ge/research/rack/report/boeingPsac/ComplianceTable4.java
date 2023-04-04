/** */
package com.ge.research.rack.report.boeingPsac;

import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.utils.LogicUtils;

/**
 * @author Saswata Paul
 */
public class ComplianceTable4 {

    /**
     * A rudimentary logic for all table a4 objectives. separate later
     *
     * <p>TODO: replace with specific logic
     *
     * @param objective
     * @return
     */
    public static PsacNode.Objective processObjectiveA4_Common(PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. There are some associated SubDD_Req 2. all the associated
         * SubDD_Req in the output have review log
         */
        //        Boolean subddPresenceFlag = false;
        //        Boolean subddNoLogFlag = false;
        //
        //        for (Requirement req : objective.getObjOutputs().getRequirements()) {
        //            if (req.getType().equals("SubDD_Req")) {
        //                subddPresenceFlag = true;
        //                if (req.getLogs().size() < 1) { // no log info
        //                    subddNoLogFlag = true;
        //                    break;
        //                }
        //            }
        //        }
        //
        //        if (subddPresenceFlag && !subddNoLogFlag) {
        //            objective.setPassed(true);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (subddPresenceFlag && subddNoLogFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(false);
        //        } else if (!subddPresenceFlag) {
        //            objective.setPassed(false);
        //            objective.setPartialData(false);
        //            objective.setNoData(true);
        //        }

        int numSubDD = objective.getObjOutputs().getRequirements().size();
        int numSubDDWithLogs =
                LogicUtils.getNumReqsWithLogs(objective.getObjOutputs().getRequirements());
        ;

        // TODO: decide no data case
        if (numSubDD < 1) { // no SubDD
            objective.setPassed(false);
            objective.setPartialData(false);
            objective.setNoData(false);
            objective.setMetrics("0.0% requirements have logs");
        } else { // Some SubDD
            if (numSubDDWithLogs == numSubDD) { // all have tests
                objective.setPassed(true);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("100.0% requirements have review logs");
            } else if ((0 < numSubDDWithLogs)
                    && (numSubDDWithLogs < numSubDD)) { // some have no tests
                objective.setPassed(false);
                objective.setPartialData(true);
                objective.setNoData(false);
                double percent = ((double) numSubDDWithLogs / numSubDD) * 100.00;
                objective.setMetrics(
                        String.format("%.2f", percent) + "% requirements have review logs");
            } else if (numSubDDWithLogs == 0) { // none have any test
                objective.setPassed(false);
                objective.setPartialData(false);
                objective.setNoData(false);
                objective.setMetrics("0.0% requirements have review logs");
            }
        }

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }
}
