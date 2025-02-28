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
package com.ge.research.rite.do178c.utils;

import com.ge.research.rite.do178c.structures.Analysis;
import com.ge.research.rite.do178c.structures.ReportNode;
import com.ge.research.rite.do178c.structures.Review;
import com.ge.research.rite.do178c.structures.ReviewLog;

/**
 * @author Saswata Paul
 */
public class ReportNodeUtils {

    /**
     * Print a reportNode as string for testing
     *
     * @param node
     */
    public static void printReportNode(ReportNode node) {

        if (node.getReportTables().size() > 0) {
            for (ReportNode.Table table : node.getReportTables()) {

                System.out.println("- " + table.getId());

                if (table.getTabObjectives().size() > 0) {
                    for (ReportNode.Objective objective : table.getTabObjectives()) {

                        System.out.println("-- " + objective.getId());

                        if (objective.getObjActivities().size() > 0) {
                            for (ReportNode.Activity activity : objective.getObjActivities()) {

                                System.out.println("--- " + activity.getId());

                                if (activity.getActReviews().size() > 0) {
                                    for (Review review : activity.getActReviews()) {

                                        System.out.println("--- " + review.getId());

                                        if (review.getLogs().size() > 0) {
                                            for (ReviewLog revLog : review.getLogs()) {

                                                System.out.println("---- " + revLog.getId());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Takes a reportnode object and a table Id and returns the corresponding Table object
     *
     * @param reportdata
     * @param tableId
     * @return
     */
    public static ReportNode.Table getTableById(ReportNode reportData, String tableId) {

        for (ReportNode.Table tabObj : reportData.getReportTables()) {
            if (tabObj.getId().equalsIgnoreCase(tableId)) {
                return tabObj;
            }
        }
        return null;
    }

    /**
     * Takes a reportnode object a table Id, and an objective id and returns the corresponding
     * Objective object
     *
     * @param reportdata
     * @param tableId
     * @param objId
     * @return
     */
    public static ReportNode.Objective getObjectiveById(
            ReportNode reportData, String tableId, String objId) {
        if (reportData.getReportTables() != null) {
            for (ReportNode.Table tabObj : reportData.getReportTables()) {
                if (tabObj.getId().equalsIgnoreCase(tableId)) {
                    if (tabObj.getTabObjectives() != null) {
                        for (ReportNode.Objective objObj : tabObj.getTabObjectives()) {
                            if (objObj.getId().equalsIgnoreCase(objId)) {
                                return objObj;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Takes a reportnode object a table Id, an objective id, and an activity id and returns the
     * corresponding Activity object
     *
     * @param reportdata
     * @param tableId
     * @param objId
     * @param actId
     * @return
     */
    public static ReportNode.Activity getActivityById(
            ReportNode reportData, String tableId, String objId, String actId) {
        if (reportData.getReportTables() != null) {
            for (ReportNode.Table tabObj : reportData.getReportTables()) {
                if (tabObj.getId().equalsIgnoreCase(tableId)) {
                    if (tabObj.getTabObjectives() != null) {
                        for (ReportNode.Objective objObj : tabObj.getTabObjectives()) {
                            if (objObj.getId().equalsIgnoreCase(objId)) {
                                if (objObj.getObjActivities() != null) {
                                    for (ReportNode.Activity actObj : objObj.getObjActivities()) {
                                        if (actObj.getId().equalsIgnoreCase(actId)) {
                                            return actObj;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Takes a reportnode object a table Id, an objective id, an activity id, and Analysis runBy
     * info and returns the corresponding Analysis object
     *
     * @param reportdata
     * @param tableId
     * @param objId
     * @param actId
     * @return
     */
    public static Analysis getAnalysisByRunBy(
            ReportNode reportData, String tableId, String objId, String actId, String runBy) {
        if (reportData.getReportTables() != null) {
            for (ReportNode.Table tabObj : reportData.getReportTables()) {
                if (tabObj.getId().equalsIgnoreCase(tableId)) {
                    if (tabObj.getTabObjectives() != null) {
                        for (ReportNode.Objective objObj : tabObj.getTabObjectives()) {
                            if (objObj.getId().equalsIgnoreCase(objId)) {
                                if (objObj.getObjActivities() != null) {
                                    for (ReportNode.Activity actObj : objObj.getObjActivities()) {
                                        if (actObj.getId().equalsIgnoreCase(actId)) {
                                            if (actObj.getActAnalyses() != null) {
                                                for (Analysis anObj : actObj.getActAnalyses()) {
                                                    if (anObj.getRunBy().equalsIgnoreCase(runBy)) {
                                                        return anObj;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Takes a objective object a table Id, an objective id, and an activity id and returns the
     * corresponding Activity object
     *
     * @param objectiveObj
     * @param actId
     * @return
     */
    public static ReportNode.Activity getActivityById(
            ReportNode.Objective objectiveObj, String actId) {

        if (objectiveObj.getObjActivities().size() > 0) {
            for (ReportNode.Activity actObj : objectiveObj.getObjActivities()) {
                if (actObj.getId().equalsIgnoreCase(actId)) {
                    return actObj;
                }
            }
        }

        return null;
    }
}
