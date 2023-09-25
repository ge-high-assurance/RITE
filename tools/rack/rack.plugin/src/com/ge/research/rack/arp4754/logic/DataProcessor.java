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

import com.ge.research.rack.arp4754.structures.Configuration;
import com.ge.research.rack.arp4754.structures.DAPlan;
import com.ge.research.rack.arp4754.structures.Evidence;
import com.ge.research.rack.arp4754.utils.DAPlanUtils;
import com.ge.research.rack.arp4754.utils.DataProcessorUtils;
import com.ge.research.rack.arp4754.utils.EvidenceUtils;
import com.ge.research.rack.autoGsn.utils.CustomFileUtils;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.do178c.utils.RackQueryUtils;
import com.ge.research.rack.utils.CSVUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 212807042
 */
public class DataProcessor {

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
    private DAPlan.Output Artifacts = new DAPlan().new Output();

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
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("planData", config), rackDir));

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
        List<DAPlan.Objective> objectives = new ArrayList<DAPlan.Objective>();

        for (String[] row : planData) {
            if ((row[objIdCol] != null)) {
                // if objective does not already exist then create a new objective
                if (DAPlanUtils.getObjectivePositionFromList(objectives, row[objIdCol]) == null) {
                    DAPlan.Objective newObjective = new DAPlan().new Objective();

                    System.out.println("Created Objective object for " + row[objIdCol]);

                    newObjective.setId(row[objIdCol]);
                    if (row[objDescCol] != null) {
                        newObjective.setDesc(row[objDescCol]);
                    }
                    // connect the artifacts to objectives
                    newObjective.setOutputs(Artifacts);

                    objectives.add(newObjective);
                }
            }
        }

        // Create a list of Process objects
        List<DAPlan.Process> processes = new ArrayList<DAPlan.Process>();

        for (String[] row : planData) {
            if ((row[procIdCol] != null)) {
                // if process does not already exist then create a new process
                if (DAPlanUtils.getProcessPositionFromList(processes, row[procIdCol]) == null) {
                    DAPlan.Process newProcess = new DAPlan().new Process();

                    System.out.println("Created Process object for " + row[procIdCol]);

                    newProcess.setId(row[procIdCol]);
                    if (row[procDescCol] != null) {
                        newProcess.setDesc(row[procDescCol]);
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
                                    processes.get(existingProcessIndx).getObjectives(),
                                    row[objIdCol])
                            == null) {
                        int existingObjectiveIndx =
                                DAPlanUtils.getObjectivePositionFromList(objectives, row[objIdCol]);
                        processes
                                .get(existingProcessIndx)
                                .getObjectives()
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
        planNode.setProcesses(processes);
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
                                DataProcessorUtils.getVarCSVID(
                                        "allSystemDesignDescription", config),
                                rackDir));
        int systemDesignDescriptionIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        systemDesignDescriptionCols, config.getSystemDesignDescription() + "_id");

        int systemDesignDescriptionURLCol =
                CustomStringUtils.getCSVColumnIndex(
                        systemDesignDescriptionCols, config.getSystemDesignDescription() + "_url");

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
                                DataProcessorUtils.getVarCSVID("allDerivedItemRequirement", config),
                                rackDir));
        int derivedItemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedItemReqCols, config.getDerivedItemReq() + "_id");

        int derivedItemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedItemReqCols, config.getDerivedItemReq() + "_desc");

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
                                DataProcessorUtils.getVarCSVID(
                                        "allDerivedSystemRequirement", config),
                                rackDir));
        int derivedSystemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedSystemReqCols, config.getDerivedSysReq() + "_id");

        int derivedSystemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        derivedSystemReqCols, config.getDerivedSysReq() + "_desc");

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
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allItemRequirement", config),
                                rackDir));
        int itemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(itemReqCols, config.getItemReq() + "_id");

        int itemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(itemReqCols, config.getItemReq() + "_desc");

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
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allSystemRequirement", config),
                                rackDir));
        int systemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(systemReqCols, config.getSysReq() + "_id");

        int systemReqDescCol =
                CustomStringUtils.getCSVColumnIndex(systemReqCols, config.getSysReq() + "_desc");

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
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allInterfaceWithInputOutput", config),
                                rackDir));
        int interfaceIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allInterfaceWithInputOutputCols, config.getIntrface() + "_id");
        int inputIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allInterfaceWithInputOutputCols, config.getIntrfaceInput() + "_id");
        int outputIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allInterfaceWithInputOutputCols, config.getIntrfaceOutput() + "_id");

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
                                DataProcessorUtils.getVarCSVID("allSystemWIthInterface", config),
                                rackDir));
        int systemIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemWIthInterfaceCols, config.getSystem() + "_id");
        int interfaceIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemWIthInterfaceCols, config.getIntrface() + "_id");

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
                                DataProcessorUtils.getVarCSVID(
                                        "allSystemRequirementWIthSystem", config),
                                rackDir));
        int sysReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemRequirementWIthSystemCols, config.getSysReq() + "_id");
        int systemIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allSystemRequirementWIthSystemCols, config.getSystem() + "_id");

        for (String[] row : allSystemRequirementWIthSystem) {
            if ((row[sysReqIdCol] != null)) {
                System.out.println(row[sysReqIdCol]);
                // find index of the object in the appropriate evidence list
                int indx =
                        EvidenceUtils.getEvidenceObjIndxById(
                                Artifacts.getSysReqObjs(), row[sysReqIdCol]);
                // add the data to the object
                if ((row[systemIdCol2] != null)) {
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
                                DataProcessorUtils.getVarCSVID(
                                        "allItemRequirementWIthItem", config),
                                rackDir));
        int itemReqIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthItemCols, config.getItemReq() + "_id");
        int itemIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthItemCols, config.getItem() + "_id");

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
                                DataProcessorUtils.getVarCSVID(
                                        "allItemRequirementWIthSystemRequirement", config),
                                rackDir));
        int sysReqIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthSystemRequirementCols, config.getSysReq() + "_id");
        int itemReqIdCol3 =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthSystemRequirementCols, config.getItemReq() + "_id");

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
                                DataProcessorUtils.getVarCSVID(
                                        "allRequirementCompleteCorrectReview", config),
                                rackDir));
        int reqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementCompleteCorrectReviewCols,
                        config.getItemReq() + "_id"); // TODO: change to all requirements
        int completeCorrectReviewIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementCompleteCorrectReviewCols,
                        config.getRequirementCompleteCorrectReview() + "_id");

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
                                DataProcessorUtils.getVarCSVID(
                                        "allRequirementTraceableReview", config),
                                rackDir));
        int reqIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementTraceableReviewCols,
                        config.getItemReq() + "_id"); // TODO: change to all requirements
        int traceableReviewIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allRequirementTraceableReviewCols,
                        config.getRequirementCompleteCorrectReview() + "_id");

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
                                DataProcessorUtils.getVarCSVID("allDerivedItemRequirement", config),
                                rackDir));

        allDerivedSystemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allDerivedSystemRequirement", config),
                                rackDir));

        allInterface =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allInterface", config), rackDir));

        allInterfaceInput =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allInterfaceInput", config),
                                rackDir));

        allInterfaceOutput =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allInterfaceOutput", config),
                                rackDir));

        allItem =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allItem", config), rackDir));

        allSystem =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allSystem", config), rackDir));

        allItemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allItemRequirement", config),
                                rackDir));

        allSystemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allSystemRequirement", config),
                                rackDir));

        allSystemDesignDescription =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allSystemDesignDescription", config),
                                rackDir));

        allInterfaceWithInputOutput =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allInterfaceWithInputOutput", config),
                                rackDir));

        allItemRequirementWIthItem =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allItemRequirementWIthItem", config),
                                rackDir));

        allSystemRequirementWIthSystem =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allSystemRequirementWIthSystem", config),
                                rackDir));

        allSystemWIthInterface =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("allSystemWIthInterface", config),
                                rackDir));

        allItemRequirementWIthSystemRequirement =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allItemRequirementWIthSystemRequirement", config),
                                rackDir));

        allRequirementCompleteCorrectReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allRequirementCompleteCorrectReview", config),
                                rackDir));

        allRequirementTraceableReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allRequirementTraceableReview", config),
                                rackDir));

        allRequirementWithCompleteCorrectReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allRequirementWithCompleteCorrectReview", config),
                                rackDir));

        allRequirementWithTraceableReview =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allRequirementWithTraceableReview", config),
                                rackDir));
        allDOCUMENT = CSVUtil.getRows(RackQueryUtils.createCsvFilePath("DOCUMENT", rackDir));
        planData =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID("planData", config), rackDir));
    }

    /**
     * Function to get the config
     *
     * @param rackDir
     * @param configPath
     */
    private void getConfig(String rackDir) {
        // Get configuration
        config = ConfigReader.getConfigFromRACK(rackDir);
    }

    // Entry point that does sequence of operations
    public void getPlanDataUsingFileConfig(String rackDir, String configFileName) {
        String configPath = rackDir + "/" + configFileName;

        // clear the rack directory
        CustomFileUtils.clearDirectory(rackDir);

        // read project config from RACK
        //        getConfig(rackDir, configPath);

        // fetch the evidence from RACK as csv files by executing queries
        DataProcessorUtils.createAndExecuteDataQueries(config, rackDir);

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
    public DAPlan getPlanData(String rackDir) {

        // clear the rack directory
        CustomFileUtils.clearDirectory(rackDir);

        // read project config from RACK
        getConfig(rackDir);

        // fetch the evidence from RACK as csv files by executing queries
        DataProcessorUtils.createAndExecuteDataQueries(config, rackDir);

        // load csv data into class variables
        readEvidenceCSVs(rackDir);

        // create arp4754 element objects using the evidence data in class variables
        createEvidenceObjects(rackDir);

        // create Plan Object
        createPlanObject(rackDir);

        // get compliance status for the plan object
        getPlanCompliance();

        return planNode;
    }
}
