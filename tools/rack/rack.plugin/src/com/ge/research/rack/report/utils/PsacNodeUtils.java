/** */
package com.ge.research.rack.report.utils;

import com.ge.research.rack.report.structures.PsacNode;
import java.util.List;

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
