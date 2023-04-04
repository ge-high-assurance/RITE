/** */
package com.ge.research.rack.report.boeingPsac;

import com.ge.research.rack.report.structures.PsacNode;
import com.ge.research.rack.report.structures.Requirement;
import com.ge.research.rack.report.structures.SwComponent;

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
