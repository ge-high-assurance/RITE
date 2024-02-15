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

import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.do178c.structures.DataItem;
import com.ge.research.rack.do178c.structures.Output;

/**
 * @author Saswata Paul
 */
public class ComplianceTable1 {

    public static PlanObjective processObjectiveA1_1and2and3and4(PlanObjective objective) {
        /**
         * This objective will pass if 1. The outputs contain avatars of PSAC, SDP, SVP, SCM, and
         * SQA
         */
        Boolean psacPresenceFlag = false;
        Boolean sdpPresenceFlag = false;
        Boolean svpPresenceFlag = false;
        Boolean sqaPresenceFlag = false;
        Boolean scmPresenceFlag = false;

        for (DataItem doc : ((Output) objective.getOutputs()).getDocuments()) {
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
                "Objective " + objective.getId() + " has passed = " + objective.isPassed());

        return objective;
    }
}
