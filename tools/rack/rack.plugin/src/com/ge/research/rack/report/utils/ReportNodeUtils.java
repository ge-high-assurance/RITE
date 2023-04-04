/** */
package com.ge.research.rack.report.utils;

import com.ge.research.rack.report.structures.Analysis;
import com.ge.research.rack.report.structures.ReportNode;
import com.ge.research.rack.report.structures.Review;
import com.ge.research.rack.report.structures.ReviewLog;

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
