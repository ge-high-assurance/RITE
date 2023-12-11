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
package com.ge.research.rack.do178c.utils;

import java.util.List;

import com.ge.research.rack.do178c.structures.PsacNode;

/**
 * @author Saswata Paul
 */
public class PsacNodeUtils {

    /**
     * Checks if a table id exists in a list of table objects
     *
     * @param tableList
     * @param tableId
     * @return
     */
    public static Integer alreadyCreatedTable(List<PsacNode.Table> tableList, String tableId) {

        if (tableList.size() > 0) {
            for (int i = 0; i < tableList.size(); i++) {
                if (tableList.get(i).getId().equalsIgnoreCase(tableId)) {
                    //                    System.out.println("--- Table already exists: " +
                    // tableId);
                    return i;
                }
            }
        }
        return null;
    }

    /**
     * Checks if an objective id exists in a list of objective objects
     *
     * @param objectiveList
     * @param objectiveId
     * @return
     */
    public static Integer alreadyCreatedObjective(
            List<PsacNode.Objective> objectiveList, String objectiveId) {

        if (objectiveList.size() > 0) {
            for (int i = 0; i < objectiveList.size(); i++) {
                if (objectiveList.get(i).getId().equalsIgnoreCase(objectiveId)) {
                    //                    System.out.println("--- Objective already exists: " +
                    // objectiveId);
                    return i;
                }
            }
        }
        return null;
    }

    /**
     * Checks if an activity id exists in a list of activity objects
     *
     * @param activityList
     * @param activityId
     * @return
     */
    public static Integer alreadyCreatedActivity(
            List<PsacNode.Activity> activityList, String activityId) {

        if (activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                if (activityList.get(i).getId().equalsIgnoreCase(activityId)) {
                    //                    System.out.println("--- Activity already exists: " +
                    // activityId);
                    return i;
                }
            }
        }
        return null;
    }

    /**
     * Print a PsacNode as string for testing
     *
     * @param node
     */
    public static void printPsacNode(PsacNode node) {

        if (node.getReportTables().size() > 0) {
            for (PsacNode.Table table : node.getReportTables()) {

                System.out.println("- " + table.getId());

                if (table.getTabObjectives().size() > 0) {
                    for (PsacNode.Objective objective : table.getTabObjectives()) {

                        System.out.println("-- " + objective.getId());

                        if (objective.getObjActivities().size() > 0) {
                            for (PsacNode.Activity activity : objective.getObjActivities()) {

                                System.out.println("--- " + activity.getId());

                                if (activity.getPerforms().size() > 0) {
                                    for (PsacNode.Activity performs : activity.getPerforms()) {
                                        System.out.println("---- " + performs.getId());
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
     * Takes a PsacNode object and a table Id and returns the corresponding Table object
     *
     * @param reportdata
     * @param tableId
     * @return
     */
    public static PsacNode.Table getTableById(PsacNode reportData, String tableId) {

        for (PsacNode.Table tabObj : reportData.getReportTables()) {
            if (tabObj.getId().equalsIgnoreCase(tableId)) {
                return tabObj;
            }
        }
        return null;
    }

    /**
     * Takes a PsacNode object a table Id, and an objective id and returns the corresponding
     * Objective object
     *
     * @param reportdata
     * @param tableId
     * @param objId
     * @return
     */
    public static PsacNode.Objective getObjectiveById(
            PsacNode reportData, String tableId, String objId) {
        if (reportData.getReportTables() != null) {
            for (PsacNode.Table tabObj : reportData.getReportTables()) {
                if (tabObj.getId().equalsIgnoreCase(tableId)) {
                    if (tabObj.getTabObjectives() != null) {
                        for (PsacNode.Objective objObj : tabObj.getTabObjectives()) {
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
     * Takes a PsacNode object a table Id, an objective id, and an activity id and returns the
     * corresponding Activity object
     *
     * @param reportdata
     * @param tableId
     * @param objId
     * @param actId
     * @return
     */
    public static PsacNode.Activity getActivityById(
            PsacNode reportData, String tableId, String objId, String actId) {
        if (reportData.getReportTables() != null) {
            for (PsacNode.Table tabObj : reportData.getReportTables()) {
                if (tabObj.getId().equalsIgnoreCase(tableId)) {
                    if (tabObj.getTabObjectives() != null) {
                        for (PsacNode.Objective objObj : tabObj.getTabObjectives()) {
                            if (objObj.getId().equalsIgnoreCase(objId)) {
                                if (objObj.getObjActivities() != null) {
                                    for (PsacNode.Activity actObj : objObj.getObjActivities()) {
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
     * Takes a objective object a table Id, an objective id, and an activity id and returns the
     * corresponding Activity object
     *
     * @param objectiveObj
     * @param actId
     * @return
     */
    public static PsacNode.Activity getActivityById(PsacNode.Objective objectiveObj, String actId) {

        if (objectiveObj.getObjActivities().size() > 0) {
            for (PsacNode.Activity actObj : objectiveObj.getObjActivities()) {
                if (actObj.getId().equalsIgnoreCase(actId)) {
                    return actObj;
                }
            }
        }

        return null;
    }
}
