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
package com.ge.research.rite.arp4754.utils;

import com.ge.research.rite.arp4754.structures.DAPlan;
import com.ge.research.rite.arp4754.structures.Evidence;
import com.ge.research.rite.arp4754.viewManagers.Arp4754ViewsManager;

import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Saswata Paul
 */
public class ViewUtils {

    /** Returns javafx color for a process object */
    public static Color getProcessColor(final DAPlan.Process procObj) {

        if (procObj.isNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (procObj.isPartialData()) {
            return Color.ORANGE;
        }

        return procObj.isPassed() ? Color.GREEN : Color.RED;
    }

    /** Returns javafx color for an objective object */
    public static Color getObjectiveColor(final DAPlan.Objective objObj) {

        if (objObj.isNoData()) {
            return Color.GRAY; // if no data, then GRAY
        }
        if (objObj.isPartialData()) {
            return Color.ORANGE;
        }

        return objObj.isPassed() ? Color.GREEN : Color.RED;
    }

    /**
     * Computes the numbers of different types of Evidence artifacts present in a process and
     * returns list
     *
     * @param procObj
     * @return
     */
    public static List<Integer> getProcessArtifactStats(DAPlan.Process procObj) {

        // some generic types of elemnts in ARP4754
        final Set<String> docIds = new HashSet<String>();
        final Set<String> reqIds = new HashSet<String>();
        final Set<String> itemIds = new HashSet<String>();
        final Set<String> systemIds = new HashSet<String>();
        final Set<String> interfaceIds = new HashSet<String>();
        // some generic types of verification/test outputs in ARP4754
        final Set<String> verificationIds = new HashSet<String>();
        final Set<String> testIds = new HashSet<String>();
        final Set<String> reviewIds = new HashSet<String>();
        final Set<String> analysisIds = new HashSet<String>();

        procObj.getObjectives().stream()
                .map(DAPlan.Objective::getOutputs)
                .forEach(
                        objective -> {
                            for (final Evidence doc : objective.getDocumentObjs()) {
                                docIds.add(doc.getId());
                            }
                            for (final Evidence derItemReq : objective.getDerItemReqObjs()) {
                                reqIds.add(derItemReq.getId());
                            }
                            for (final Evidence derSysReq : objective.getDerSysReqObjs()) {
                                reqIds.add(derSysReq.getId());
                            }
                            for (final Evidence itemReq : objective.getItemReqObjs()) {
                                reqIds.add(itemReq.getId());
                            }
                            for (final Evidence sysReq : objective.getSysReqObjs()) {
                                reqIds.add(sysReq.getId());
                            }
                            for (final Evidence item : objective.getItemObjs()) {
                                itemIds.add(item.getId());
                            }
                            for (final Evidence interfce : objective.getInterfaceObjs()) {
                                interfaceIds.add(interfce.getId());
                            }
                            for (final Evidence system : objective.getSystemObjs()) {
                                systemIds.add(system.getId());
                            }
                            for (final Evidence verification : objective.getVerificationObjs()) {
                                verificationIds.add(verification.getId());
                            }
                            for (final Evidence test : objective.getTestObjs()) {
                                testIds.add(test.getId());
                            }
                            for (final Evidence review : objective.getReviewObjs()) {
                                reviewIds.add(review.getId());
                            }
                            for (final Evidence analysis : objective.getAnalysisObjs()) {
                                analysisIds.add(analysis.getId());
                            }
                        });

        return List.of(
                docIds.size(),
                reqIds.size(),
                itemIds.size(),
                systemIds.size(),
                interfaceIds.size(),
                verificationIds.size(),
                testIds.size(),
                reviewIds.size(),
                analysisIds.size());
    }

    /**
     * Given a file address, opens it in the default user specified app on the platform
     *
     * @param url
     */
    public static void openUrlInDefaultApp(String url) {
        System.out.println("Trying to open URL in default app!");
        try {
            Arp4754ViewsManager.hostServices.showDocument(url);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to open URL in default app!");
            e.printStackTrace();
        }
    }
}
