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
package com.ge.research.rack.arp4754.logic;

import com.ge.research.rack.analysis.structures.Plan;
import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.utils.CustomFileUtils;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.analysis.utils.RackQueryUtils;
import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.arp4754.structures.Output;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.DataProcessorUtils;
import com.ge.research.rack.arp4754.utils.EvidenceUtils;
import com.ge.research.rack.utils.CSVUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 212807042
 */
public class DataProcessor extends com.ge.research.rack.analysis.structures.DataProcessor {

    public static String PLAN_DATA = "getDAP";
    public static String PLAN_DOCUMENT = "DOCUMENT";

    /**
     * Class variables to store the raw data fetched from CSV files
     *
     * <p>NOTE: There should be an associated query for each
     */
    // --- RAW DATA AS LIST OF STRINGS
    // The plan
    private List<String[]> planData;

    // All the independent element id data (TODO subset, add for more supporting objectives)
    private List<String[]> allDerivedItemRequirement;

    private List<String[]> allDerivedSystemRequirement;

    private List<String[]> allInterface;

    private List<String[]> allInterfaceInput;

    private List<String[]> allInterfaceOutput;

    private List<String[]> allItem;

    private List<String[]> allItemRequirement;

    private List<String[]> allSystem;

    private List<String[]> allSystemRequirement;

    private List<String[]> allSystemDesignDescription;

    private List<String[]> allDOCUMENT;

    private List<String[]> allRequirementCompleteCorrectReview;

    private List<String[]> allRequirementTraceableReview;

    // The connections between the elements (TODO subset, add for more supporting objectives)
    // NOTE: The connections should be just one-level to enable automatic query synthesis in future
    // NOTE: The ordering here is a guide for how they should be created when creating objects
    private List<String[]> allInterfaceWithInputOutput;

    private List<String[]> allSystemWIthInterface;

    private List<String[]> allSystemRequirementWIthSystem;

    private List<String[]> allItemRequirementWIthItem;

    private List<String[]> allItemRequirementWIthSystemRequirement;

    private List<String[]> allRequirementWithCompleteCorrectReview;

    private List<String[]> allRequirementWithTraceableReview;

    // --- PROCESSED DATA OBJECTS
    // The configuration
    private Configuration config = new Configuration();

    // The Plan
    private DAPlan planNode = new DAPlan();

    // ARP4754 Element Objects
    private Output Artifacts = new Output();

    private QueryList queries = new QueryList();

    /** Passes the Plan Object to the appropriate functions to get the compliance status */
    private void getPlanCompliance() {
        DAPlan newPlanNode = new DAPlan();
        newPlanNode = ComplianceDAP.compute(planNode);
        planNode = newPlanNode;
    }

    /**
     * Creates the DAP object with all plans, processes, systems, and objectives
     *
     * <p>Algo: 1. create the objective objects and connect the evidence artifacts to them 2. Create
     * the process objects and attach the objectives 3. Create the plan object and attach the
     * processes and the system, DAL
     *
     * @param rackDir
     */
    private void createPlanObject(String rackDir) {

        // get all headers for the csv file
        String[] allCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(PLAN_DATA, rackDir));

        int planIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "Plan_id");
        int sysIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "System_id");
        int dalCol = CustomStringUtils.getCSVColumnIndex(allCols, "DevelopmentAssuranceLevel");
        int procIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "Process_id");
        int objIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "Objective_id");
        int planDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "Plan_desc");
        int sysDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "System_desc");
        int procDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "Process_desc");
        int objDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "Objective_desc");

        System.out.println(
                planIdCol + " " + sysIdCol + " " + dalCol + " " + procIdCol + " " + objIdCol);

        // Create a list of Objective objects
        List<PlanObjective> objectives = new ArrayList<PlanObjective>();

        for (String[] row : planData) {
            if ((row[objIdCol] != null)) {
                // if objective does not already exist then create a new objective
                if (DAPlanUtils.getObjectivePositionFromList(objectives, row[objIdCol]) == null) {
                    PlanObjective newObjective = new PlanObjective();

                    System.out.println("Created Objective object for " + row[objIdCol]);

                    newObjective.setId(row[objIdCol]);
                    if (row[objDescCol] != null) {
                        newObjective.setDescription(row[objDescCol]);
                    }

                    // connect the artifacts to objectives
                    newObjective.setOutputs(Artifacts);
                    objectives.add(newObjective);
                }
            }
        }

        // Create a list of Process objects
        List<PlanTable> processes = new ArrayList<PlanTable>();

        for (String[] row : planData) {
            if ((row[procIdCol] != null)) {
                // if process does not already exist then create a new process
                if (DAPlanUtils.getProcessPositionFromList(processes, row[procIdCol]) == null) {
                    PlanTable newProcess = new PlanTable();

                    System.out.println("Created Process object for " + row[procIdCol]);

                    newProcess.setId(row[procIdCol]);
                    if (row[procDescCol] != null) {
                        newProcess.setDescription(row[procDescCol]);
                    }
                    processes.add(newProcess);
                }

                // if the row contains an objective, find the process object from the list and add
                // the objective (if not already added)
                if (row[objIdCol] != null) {
                    // if the process does not contain the objective then add the objective to the
                    // process
                    int existingProcessIndx =
                            DAPlanUtils.getProcessPositionFromList(processes, row[procIdCol]);
                    if (DAPlanUtils.getObjectivePositionFromList(
                                    processes.get(existingProcessIndx).getTabObjectives(),
                                    row[objIdCol])
                            == null) {
                        int existingObjectiveIndx =
                                DAPlanUtils.getObjectivePositionFromList(objectives, row[objIdCol]);
                        processes
                                .get(existingProcessIndx)
                                .getTabObjectives()
                                .add(objectives.get(existingObjectiveIndx));

                        System.out.println(
                                "Added objective "
                                        + objectives.get(existingObjectiveIndx).getId()
                                        + " to process "
                                        + row[procIdCol]);
                    }
                }
            }
        }

        // Add things to the plan
        if (planData.get(0)[planIdCol] != null) {
            planNode.setId(planData.get(0)[planIdCol]);
        }
        if (planData.get(0)[planDescCol] != null) {
            planNode.setDesc(planData.get(0)[planDescCol]);
        }
        if (planData.get(0)[sysIdCol] != null) {
            planNode.setSystem(planData.get(0)[sysIdCol]);
        }
        planNode.setTables(processes);
    }

    /**
     * Uses the config and the data to create objects for each type of evidence
     *
     * <p>NOTE: The order in which the connections are created is important: Depends on which stores
     * which
     */
    private void createEvidenceObjects(String rackDir) {

        // ---- Create the element objects
        System.out.println("---- Creating Objects for SystemDesignDescription ----");

        String[] systemDesignDescriptionCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("systemDesignDescription"), rackDir));
        int systemDesignDescriptionIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        systemDesignDescriptionCols, config.get("systemDesignDescription") + "_id");

        int systemDesignDescriptionURLCol =
                CustomStringUtils.getCSVColumnIndex(
                        systemDesignDescriptionCols,
                        config.get("systemDesignDescription") + "_url");

        for (String[] row : allSystemDesignDescription) {

            if ((systemDesignDescriptionIdCol >= 0) && row[systemDesignDescriptionIdCol] != null) {
                Evidence newEvidenceObj = new Evidence();
                newEvidenceObj.setType("Document");
                newEvidenceObj.setId(row[systemDesignDescriptionIdCol]);
                if ((systemDesignDescriptionURLCol >= 0)
                        && row[systemDesignDescriptionURLCol] != null) {
                    newEvidenceObj.setURL(row[systemDesignDescriptionURLCol]);
                }
                Artifacts.getSystemDesignDescriptionObjs().add(newEvidenceObj);
                System.out.println("Created Object for " + newEvidenceObj.getId());
            }
        }

        System.out.println("---- Creating Objects for DerivedItemRequirement ----");

        String[] derivedItemReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("derivedItemRequirement"), rackDir));
        int derivedItemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedItemReqCols, config.get("derivedItemRequirement") + "_id");

        int derivedItemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedItemReqCols, config.get("derivedItemRequirement") + "_desc");

        for (String[] row : allDerivedItemRequirement) {
            if ((derivedItemReqIdCol >= 0) && row[derivedItemReqIdCol] != null) {
                Evidence newEvidenceObj = new Evidence();
                newEvidenceObj.setType("Requirement");
                newEvidenceObj.setId(row[derivedItemReqIdCol]);
                if ((derivedItemReqDescCol >= 0) && row[derivedItemReqDescCol] != null) {
                    newEvidenceObj.setDescription(row[derivedItemReqDescCol]);
                }
                Artifacts.getDerItemReqObjs().add(newEvidenceObj);
                System.out.println("Created Object for " + newEvidenceObj.getId());
            }
        }

        System.out.println("---- Creating Objects for DerivedSystemRequirement ----");
        String[] derivedSystemReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("derivedSystemRequirement"), rackDir));
        int derivedSystemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedSystemReqCols, config.get("derivedSystemRequirement") + "_id");

        int derivedSystemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedSystemReqCols, config.get("derivedSystemRequirement") + "_desc");

        for (String[] row : allDerivedSystemRequirement) {
            if ((derivedSystemReqIdCol >= 0) && row[derivedSystemReqIdCol] != null) {
                Evidence newEvidenceObj = new Evidence();
                newEvidenceObj.setId(row[derivedSystemReqIdCol]);
                newEvidenceObj.setType("Requirement");
                if ((derivedSystemReqDescCol >= 0) && row[derivedSystemReqDescCol] != null) {
                    newEvidenceObj.setDescription(row[derivedSystemReqDescCol]);
                }
                Artifacts.getDerSysReqObjs().add(newEvidenceObj);
                System.out.println("Created Object for " + newEvidenceObj.getId());
            }
        }

        System.out.println(
                "---- Creating Objects for Interface ----"); // TODO: Add description field
        for (String[] row : allInterface) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Interface");
            Artifacts.getInterfaceObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println(
                "---- Creating Objects for InterfaceInput ----"); // TODO: Add description field
        for (String[] row : allInterfaceInput) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("InterfaceInput");
            Artifacts.getInterfaceInputObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println(
                "---- Creating Objects for InterfaceOutput ----"); // TODO: Add description field
        for (String[] row : allInterfaceOutput) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("InterfaceOutput");
            Artifacts.getInterfaceOutputObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println("---- Creating Objects for Item ----"); // TODO: Add description field
        for (String[] row : allItem) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Item");
            Artifacts.getItemObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println("---- Creating Objects for ItemRequirement ----");
        String[] itemReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(config.get("itemRequirement"), rackDir));
        int itemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        itemReqCols, config.get("itemRequirement") + "_id");

        int itemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        itemReqCols, config.get("itemRequirement") + "_desc");

        System.out.println(itemReqIdCol + " , " + itemReqDescCol);

        for (String[] row : allItemRequirement) {
            if ((itemReqIdCol >= 0) && row[itemReqIdCol] != null) {
                Evidence newEvidenceObj = new Evidence();
                newEvidenceObj.setType("Requirement");
                newEvidenceObj.setId(row[itemReqIdCol]);
                if ((itemReqDescCol >= 0) && row[itemReqDescCol] != null) {
                    newEvidenceObj.setDescription(row[itemReqDescCol]);
                    System.out.println("Added desc:" + row[itemReqDescCol]);
                }
                Artifacts.getItemReqObjs().add(newEvidenceObj);
                System.out.println("Created Object for " + newEvidenceObj.getId());
            }
        }

        //        System.out.println("---- Creating Objects for System ----");  // TODO: Add
        // description field
        //        for (String[] row : allSystem) {
        //            Evidence newEvidenceObj = new Evidence();
        //            newEvidenceObj.setId(row[0]);
        //            newEvidenceObj.setType("System");
        //            Artifacts.getItemObjs().add(newEvidenceObj);
        //            System.out.println("Created Object for " + newEvidenceObj.getId());
        //        }

        System.out.println("---- Creating Objects for SystemRequirement ----");
        String[] systemReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(config.get("systemRequirement"), rackDir));
        int systemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        systemReqCols, config.get("systemRequirement") + "_id");

        int systemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        systemReqCols, config.get("systemRequirement") + "_desc");

        System.out.println(systemReqIdCol + " , " + systemReqDescCol);

        for (String[] row : allSystemRequirement) {
            if ((systemReqIdCol >= 0) && row[systemReqIdCol] != null) {
                Evidence newEvidenceObj = new Evidence();
                newEvidenceObj.setId(row[systemReqIdCol]);
                newEvidenceObj.setType("Requirement");
                if ((systemReqDescCol >= 0) && row[systemReqDescCol] != null) {
                    newEvidenceObj.setDescription(row[systemReqDescCol]);
                }
                Artifacts.getSysReqObjs().add(newEvidenceObj);
                System.out.println("Created Object for " + newEvidenceObj.getId());
            }
        }

        System.out.println("---- Creating Objects for System ----"); // TODO: Add description field
        for (String[] row : allSystem) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("System");
            Artifacts.getSystemObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println(
                "---- Creating Objects for DOCUMENT ----"); // TODO: Add description field
        for (String[] row : allDOCUMENT) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("DOCUMENT");
            Artifacts.getDocumentObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println(
                "---- Creating Objects for RequirementCompleteCorrectReview ----"); // TODO: Add
        // description
        // field
        for (String[] row : allRequirementCompleteCorrectReview) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Review");
            Artifacts.getRequirementCompleteCorrectReviewObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        System.out.println(
                "---- Creating Objects for RequirementTraceableReview ----"); // TODO: Add
        // description field
        for (String[] row : allRequirementTraceableReview) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Review");
            Artifacts.getRequirementTraceableReviewObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + newEvidenceObj.getId());
        }

        // ---- create the connections

        // get the header line for allInterfaceWithInputOutput csv file
        String[] allInterfaceWithInputOutputCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(config.getWithIO("interface"), rackDir));
        int interfaceIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allInterfaceWithInputOutputCols, config.get("interface") + "_id");
        int inputIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allInterfaceWithInputOutputCols, config.get("interfaceInput") + "_id");
        int outputIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allInterfaceWithInputOutputCols, config.get("interfaceOutput") + "_id");

        for (String[] row : allInterfaceWithInputOutput) {
            if ((row[interfaceIdCol] != null)) {
                //                System.out.println(row[interfaceIdCol]);
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getInterfaceObjs(), row[interfaceIdCol]);
                // add the data to the object
                if ((row[inputIdCol] != null)) {
                    Artifacts.getInterfaceObjs()
                            .get(indx)
                            .getHasInputs()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getInterfaceInputObjs(), row[inputIdCol]));
                }
                if ((row[outputIdCol] != null)) {
                    Artifacts.getInterfaceObjs()
                            .get(indx)
                            .getHasOutputs()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getInterfaceOutputObjs(), row[outputIdCol]));
                }
            }
        }

        System.out.println("created allInterfaceWithInputOutput");

        // get the header line for allSystemWIthInterface csv file
        //        System.out.println(
        // RackQueryUtils.createCsvFilePath(DataProcessorUtils.getVarCSVID("allSystemWIthInterface",
        // config), rackDir));

        String[] allSystemWIthInterfaceCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("system", "interface"), rackDir));
        int systemIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemWIthInterfaceCols, config.get("system") + "_id");
        int interfaceIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemWIthInterfaceCols, config.get("interface") + "_id");

        System.out.println(systemIdCol + " , " + interfaceIdCol2);

        for (String[] row : allSystemWIthInterface) {
            if ((row[systemIdCol] != null)) {
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getSystemObjs(), row[systemIdCol]);
                // add the data to the object
                if ((row[interfaceIdCol2] != null)) {
                    Artifacts.getSystemObjs()
                            .get(indx)
                            .getHasInterfaces()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getInterfaceObjs(), row[interfaceIdCol2]));
                }
            }
        }

        System.out.println("created allSystemWIthInterface");

        // get the header line for allSystemRequirementWIthSystem csv file
        String[] allSystemRequirementWIthSystemCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("systemRequirement", "system"), rackDir));
        int sysReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemRequirementWIthSystemCols,
                        config.get("systemRequirement") + "_id");
        int systemIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemRequirementWIthSystemCols, config.get("system") + "_id");

        System.out.println(config.get("system"));
        for (String[] row : allSystemRequirementWIthSystem) {
            if ((row[sysReqIdCol] != null)) {
                System.out.println(row[sysReqIdCol]);
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getSysReqObjs(), row[sysReqIdCol]);
                // add the data to the object
                if ((row[systemIdCol2] != null)) {
                    System.out.println(row[systemIdCol2]);
                    Artifacts.getSysReqObjs()
                            .get(indx)
                            .getAllocatedTo()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getSystemObjs(), row[systemIdCol2]));
                }
            }
        }

        System.out.println("created allSystemRequirementWithSystem");

        // get the header line for allItemRequirementWIthItem csv file
        String[] allItemRequirementWIthItemCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("itemRequirement", "item"), rackDir));
        int itemReqIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthItemCols, config.get("itemRequirement") + "_id");
        int itemIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthItemCols, config.get("item") + "_id");

        for (String[] row : allItemRequirementWIthItem) {
            if ((row[itemReqIdCol2] != null)) {
                // find index of the object in the appropriate evidence list
                System.out.println(row[itemReqIdCol2]);
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getItemReqObjs(), row[itemReqIdCol2]);
                // add the data to the object
                if ((row[itemIdCol] != null)) {
                    Artifacts.getItemReqObjs()
                            .get(indx)
                            .getAllocatedTo()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getItemObjs(), row[itemIdCol]));
                }
            }
        }

        System.out.println("created allItemRequirementWithItem");

        // get the header line for allItemRequirementWIthSystemRequirement csv file
        String[] allItemRequirementWIthSystemRequirementCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("itemRequirement", "systemRequirement"), rackDir));
        int sysReqIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthSystemRequirementCols,
                        config.get("systemRequirement") + "_id");
        int itemReqIdCol3 =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthSystemRequirementCols,
                        config.get("itemRequirement") + "_id");

        for (String[] row : allItemRequirementWIthSystemRequirement) {
            if ((row[itemReqIdCol3] != null)) {
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getItemReqObjs(), row[itemReqIdCol3]);
                // add the data to the object
                if ((row[sysReqIdCol2] != null)) {
                    Artifacts.getItemReqObjs()
                            .get(indx)
                            .getTracesUp()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getSysReqObjs(), row[sysReqIdCol2]));
                }
            }
        }

        System.out.println("created allItemRequirementwithSystemRequreiment");

        // get the header line for allRequirementCompleteCorrectReview csv file
        String[] allRequirementCompleteCorrectReviewCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("requirementCompleteCorrectReview"), rackDir));
        int reqIdCol =
                // TODO: change to all requirements
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementCompleteCorrectReviewCols,
                        config.get("itemRequirement") + "_id");
        int completeCorrectReviewIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementCompleteCorrectReviewCols,
                        config.get("requirementCompleteCorrectReview") + "_id");

        for (String[] row : allRequirementCompleteCorrectReview) {
            if ((row[reqIdCol] != null)) {
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getItemReqObjs(), row[reqIdCol]);
                // add the data to the object
                if ((row[completeCorrectReviewIdCol] != null)) {
                    Artifacts.getItemReqObjs()
                            .get(indx)
                            .getHasReviews()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getSysReqObjs(),
                                            row[completeCorrectReviewIdCol]));
                }
            }
        }

        System.out.println("created allRequirementCompleteCorrectReview");

        // get the header line for allRequirementCompleteCorrectReview csv file
        String[] allRequirementTraceableReviewCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                config.get("requirementTraceableReview"), rackDir));
        int reqIdCol2 =
                // TODO: change to all requirements
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementTraceableReviewCols, config.get("itemRequirement") + "_id");
        int traceableReviewIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementTraceableReviewCols,
                        config.get("requirementCompleteCorrectReview") + "_id");

        for (String[] row : allRequirementTraceableReview) {
            if ((row[reqIdCol2] != null)) {
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getItemReqObjs(), row[reqIdCol2]);
                // add the data to the object
                if ((row[traceableReviewIdCol] != null)) {
                    Artifacts.getItemReqObjs()
                            .get(indx)
                            .getHasReviews()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                            Artifacts.getSysReqObjs(), row[traceableReviewIdCol]));
                }
            }
        }

        System.out.println("created allRequirementTraceableReview");
    }

    /**
     * function to use the configReader to read the config and the read the data from the .csv files
     * (TODO subset, add for more supporting objectives)
     *
     * @param rackDir
     * @param configPath
     */
    private void readEvidenceCSVs(String rackDir) {

        // TODO: Future: Generate queries and query ids dynamically

        // Use the string from configurations to create the Query/CSV IDS and use that to read the
        // files
        allDerivedItemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("derivedItemRequirement"), rackDir));

        allDerivedSystemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("derivedSystemRequirement"), rackDir));

        allInterface =
                CSVUtil.getRows(RackQueryUtils.createCsvFilePath(config.get("interface"), rackDir));

        allInterfaceInput =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(config.get("interfaceInput"), rackDir));

        allInterfaceOutput =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(config.get("interfaceOutput"), rackDir));

        allItem = CSVUtil.getRows(RackQueryUtils.createCsvFilePath(config.get("item"), rackDir));

        allSystem =
                CSVUtil.getRows(RackQueryUtils.createCsvFilePath(config.get("system"), rackDir));

        allItemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(config.get("itemRequirement"), rackDir));

        allSystemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(config.get("systemRequirement"), rackDir));

        allSystemDesignDescription =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("systemDesignDescription"), rackDir));

        allInterfaceWithInputOutput =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(config.getWithIO("interface"), rackDir));

        allItemRequirementWIthItem =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("itemRequirement", "item"), rackDir));

        allSystemRequirementWIthSystem =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("systemRequirement", "system"), rackDir));

        allSystemWIthInterface =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("system", "interface"), rackDir));

        allItemRequirementWIthSystemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("itemRequirement", "systemRequirement"), rackDir));

        allRequirementCompleteCorrectReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("requirementCompleteCorrectReview"), rackDir));

        allRequirementTraceableReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("requirementTraceableReview"), rackDir));

        allRequirementWithCompleteCorrectReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("itemRequirement", "requirementCompleteCorrectReview"),
                                rackDir));

        allRequirementWithTraceableReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                config.get("itemRequirement", "requirementTraceableReview"),
                                rackDir));

        allDOCUMENT = 
        		CSVUtil.getRows(RackQueryUtils.createCsvFilePath(PLAN_DOCUMENT, rackDir));

        planData =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(PLAN_DATA, rackDir));
    }

    /**
     * Function to get the config
     *
     * @param rackDir
     * @param configPath
     */
    private void getConfig(String rackDir) {
        // Get configuration
        config = ConfigReader.getConfigFromRACK(queries, rackDir);
    }

    private void addDefaultQueries() {
        // Populate the default queries; Must be called before getConfig
        queries.addDirect(PLAN_DATA);
        queries.addConfigLookup("derivedItemRequirement");
        queries.addConfigLookup("derivedSystemRequirement");
        queries.addConfigLookup("interface");
        queries.addConfigLookup("interfaceInput");
        queries.addConfigLookup("interfaceOutput");
        queries.addConfigLookup("item");
        queries.addConfigLookup("itemRequirement");
        queries.addConfigLookup("system");
        queries.addConfigLookup("systemRequirement");
        queries.addConfigLookup("systemDesignDescription");
        queries.addConfigLookupWithIO("interface");
        queries.addConfigLookup("itemRequirement", "item");
        queries.addConfigLookup("systemRequirement", "system");
        queries.addConfigLookup("system", "interface");
        queries.addConfigLookup("itemRequirement", "systemRequirement");
        queries.addConfigLookup("requirementCompleteCorrectReview");
        queries.addConfigLookup("requirementTraceableReview");
        queries.addConfigLookup("itemRequirement", "requirementCompleteCorrectReview");
        queries.addConfigLookup("itemRequirement", "requirementTraceableReview");
        queries.addDirect(PLAN_DOCUMENT);
    }

    // Entry point that does sequence of operations
    public void getPlanDataUsingFileConfig(String rackDir, String configFileName) {
        // String configPath = rackDir + "/" + configFileName;

        // clear the rack directory
        CustomFileUtils.clearDirectory(rackDir);

        // read project config from RACK
        addDefaultQueries();
        // getConfig(rackDir, configPath);

        // fetch the evidence from RACK as csv files by executing queries
        DataProcessorUtils.createAndExecuteDataQueries(config, queries, rackDir);

        // load csv data into class variables
        readEvidenceCSVs(rackDir);

        // create arp4754 element objects using the evidence data in class variables
        createEvidenceObjects(rackDir);

        // create Plan Object
        createPlanObject(rackDir);

        // get compliance status for the plan object
        getPlanCompliance();
    }

    // Entry point that does sequence of operations
    @Override
    public Plan getData(String outDir) {

        // clear the rack directory
        CustomFileUtils.clearDirectory(outDir);

        // read project config from RACK
        addDefaultQueries();
        getConfig(outDir);

        // fetch the evidence from RACK as csv files by executing queries
        DataProcessorUtils.createAndExecuteDataQueries(config, queries, outDir);

        // load csv data into class variables
        readEvidenceCSVs(outDir);

        // create arp4754 element objects using the evidence data in class variables
        createEvidenceObjects(outDir);

        // create Plan Object
        createPlanObject(outDir);

        // get compliance status for the plan object
        getPlanCompliance();

        return planNode;
    }
}
