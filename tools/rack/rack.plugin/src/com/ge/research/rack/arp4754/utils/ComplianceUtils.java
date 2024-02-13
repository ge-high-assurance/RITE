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
package com.ge.research.rack.arp4754.utils;

import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;

/**
 * @author Saswata Paul
 */
public class ComplianceUtils {

    /**
     * Computes the compliance status of a process
     *
     * @param numPassed
     * @param numPartial
     * @param numNoData
     * @param totalObjectives
     * @return
     */
    public static Double processComplianceValue(
            int numPassed, int numPartial, int numNoData, int totalObjectives) {
        Double stats = 0.0;

        if (numPassed > 0.0) {
            stats = ((double) numPassed / totalObjectives * 100.00);
        }

        return stats;
    }

    /**
     * Sets the process stat boolean flags
     *
     * @param procObj
     * @return
     */
    public static PlanTable<PlanObjective> getProcessStatus(PlanTable<PlanObjective> procObj) {
        System.out.println(
                procObj.getId()
                        + " ob no: "
                        + procObj.getNumObjNoData()
                        + " ob partial:"
                        + procObj.getNumObjPartial()
                        + " ob pass:"
                        + procObj.getNumObjPassed()
                        + " numobjs:"
                        + procObj.getTabObjectives().size());

        // set the data flags
        if (procObj.getNumObjNoData() == procObj.getTabObjectives().size()) {
            procObj.setNoData(true);
            procObj.setPartialData(false);
            procObj.setPassed(false);
        } else {
            procObj.setNoData(false);
            if (procObj.getNumObjPassed() == procObj.getTabObjectives().size()) {
                procObj.setPartialData(false);
                procObj.setPassed(true);
            } else {
                procObj.setPartialData(true);
                procObj.setPassed(false);
            }
        }

        System.out.println(
                procObj.getId()
                        + " no: "
                        + procObj.isNoData()
                        + " partial:"
                        + procObj.isPartialData()
                        + " pass:"
                        + procObj.isPassed());

        // compute process compliance
        procObj.setComplianceStatus(
                ComplianceUtils.processComplianceValue(
                        procObj.getNumObjPassed(),
                        procObj.getNumObjPartial(),
                        procObj.getNumObjNoData(),
                        procObj.getTabObjectives().size()));

        return procObj;
    }
}
