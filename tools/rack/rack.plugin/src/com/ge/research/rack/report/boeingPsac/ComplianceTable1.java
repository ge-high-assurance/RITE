/** */
package com.ge.research.rack.report.boeingPsac;

import com.ge.research.rack.report.structures.DataItem;
import com.ge.research.rack.report.structures.PsacNode;

/**
 * @author Saswata Paul
 */
public class ComplianceTable1 {

    public static PsacNode.Objective processObjectiveA1_1and2and3and4(
            PsacNode.Objective objective) {
        /**
         * This objective will pass if 1. The outputs contain avatars of PSAC, SDP, SVP, SCM, and
         * SQA
         */
        Boolean psacPresenceFlag = false;
        Boolean sdpPresenceFlag = false;
        Boolean svpPresenceFlag = false;
        Boolean sqaPresenceFlag = false;
        Boolean scmPresenceFlag = false;

        for (DataItem doc : objective.getObjOutputs().getDocuments()) {
            if (doc.getId().equals("BoeingTextPSAQ")) {
                psacPresenceFlag = true;
            }
            if (doc.getId().equals("BoeingTextSDP")) {
                sdpPresenceFlag = true;
            }
            if (doc.getId().equals("BoeingTextSVP")) {
                svpPresenceFlag = true;
            }
            if (doc.getId().equals("BoeingTextSCM-Plan")) {
                scmPresenceFlag = true;
            }
            if (doc.getId().equals("BoeingTextSQA-Plan")) {
                sqaPresenceFlag = true;
            }
        }

        if (psacPresenceFlag
                && sdpPresenceFlag
                && svpPresenceFlag
                && sqaPresenceFlag
                && scmPresenceFlag) {
            objective.setPassed(true);
            objective.setNoData(false);
            objective.setPartialData(false);
        } else if (!psacPresenceFlag
                && !sdpPresenceFlag
                && !svpPresenceFlag
                && !sqaPresenceFlag
                && !scmPresenceFlag) {
            objective.setPassed(false);
            objective.setNoData(true);
            objective.setPartialData(false);

        } else {
            objective.setPassed(false);
            objective.setNoData(false);
            objective.setPartialData(true);
        }

        // create metric statement
        int psacInt = psacPresenceFlag ? 1 : 0;
        int sdpInt = sdpPresenceFlag ? 1 : 0;
        int svpInt = svpPresenceFlag ? 1 : 0;
        int sqaInt = sqaPresenceFlag ? 1 : 0;
        int scmInt = scmPresenceFlag ? 1 : 0;

        double percent = ((double) (psacInt + sdpInt + svpInt + sqaInt + scmInt) / 5) * 100;

        objective.setMetrics(String.format("%.2f", percent) + "% documents available");

        System.out.println(
                "Objective " + objective.getId() + " has passed = " + objective.getPassed());

        return objective;
    }
}
