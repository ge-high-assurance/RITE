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
    //--- RAW DATA AS LIST OF STRINGS
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

    // The connections between the elements (TODO subset, add for more supporting objectives)
    // NOTE: The connections should be just one-level to enable automatic query synthesis in future
    // NOTE: The ordering here is a guide for how they should be created when creating objects
    private List<String[]> allInterfaceWithInputOutput;

    private List<String[]> allSystemWIthInterface;

    private List<String[]> allSystemRequirementWIthSystem;

    private List<String[]> allItemRequirementWIthItem;

    private List<String[]> allItemRequirementWIthSystemRequirement;

	
	//--- PROCESSED DATA OBJECTS
    // The configuration
    private Configuration config = new Configuration();

    // The Plan 
    private DAPlan planNode = new DAPlan();
    
    // ARP4754 Element Objects
    private DAPlan.Output Artifacts = new DAPlan().new Output();


    
    /**
     * Passes the Plan Object to the appropriate functions to get the compliance status
     */
    private void getPlanCompliance() {
    	DAPlan newPlanNode = new DAPlan();
    	newPlanNode = ComplianceDAP.compute(planNode);
    	planNode = newPlanNode;
    }
    
    
    /**
     * Creates the DAP object with all plans, processes, systems, and objectives
     * 
     * Algo:
     *      1. create the objective objects and connect the evidence artifacts to them
     *      2. Create the process objects and attach the objectives
     *      3. Create the plan object and attach the processes and the system, DAL
     * 
     * @param rackDir
     */
    private void createPlanObject(String rackDir) {
    
    	// get all headers for the csv file
        String[] allCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(DataProcessorUtils.getVarCSVID("planData", config),rackDir));
    	
        int planIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "Plan_id");
        int sysIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "System_id");
        int dalCol = CustomStringUtils.getCSVColumnIndex(allCols, "DevelopmentAssuranceLevel");
        int procIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "Process_id");
        int objIdCol = CustomStringUtils.getCSVColumnIndex(allCols, "Objective_id");
        int planDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "Plan_desc");
        int sysDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "System_desc");
        int procDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "Process_desc");
        int objDescCol = CustomStringUtils.getCSVColumnIndex(allCols, "Objective_desc");

        System.out.println(planIdCol + " " + sysIdCol + " " + dalCol + " " + procIdCol + " " + objIdCol);

        // Create a list of Objective objects
        List<DAPlan.Objective> objectives = new ArrayList<DAPlan.Objective>();

        for (String[] row : planData) {
            if ((row[objIdCol] != null)) {
            	// if objective does not already exist then create a new objective
            	if(DAPlanUtils.getObjectiveObjectFromList(objectives, row[objIdCol])==null) {
            		DAPlan.Objective newObjective = new DAPlan().new Objective();
            		
            		System.out.println("Created Objective object for " + row[objIdCol]);
            		
            		newObjective.setId(row[objIdCol]);
            		if(row[objDescCol]!=null) {
                		newObjective.setDesc(row[objDescCol]);            			
            		}
            		//connect the artifacts to objectives
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
            	if(DAPlanUtils.getProcessObjectFromList(processes, row[procIdCol])==null) {
            		DAPlan.Process newProcess = new DAPlan().new Process();

            		System.out.println("Created Process object for " + row[procIdCol]);

            		newProcess.setId(row[procIdCol]);
            		if(row[procDescCol]!=null) {
                		newProcess.setDesc(row[procDescCol]);            			
            		}
            		processes.add(newProcess);            		
            	}
            	// if the row contains an objective, find the process object from the list and add the objective (if not already added)
            	if(row[objIdCol]!=null) {
            		// if the process does not contain the objective then add the objective to the process
            		int existingProcessIndx = DAPlanUtils.getProcessObjectFromList(processes, row[procIdCol]);
            		if(DAPlanUtils.getObjectiveObjectFromList(processes.get(existingProcessIndx).getObjectives(),row[objIdCol]) == null) {
            			int existingObjectiveIndx = DAPlanUtils.getObjectiveObjectFromList(objectives, row[objIdCol]);
            			processes.get(existingProcessIndx).getObjectives().add(objectives.get(existingObjectiveIndx));
            		}
            	}
            }
        }
        
        // Add things to the plan
       if(planData.get(0)[planIdCol]!=null) {
    	   planNode.setId(planData.get(0)[planIdCol]);
       }
       if(planData.get(0)[planDescCol]!=null) {
    	   planNode.setDesc(planData.get(0)[planDescCol]);
       }
       if(planData.get(0)[sysIdCol]!=null) {
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
    	System.out.println("---- Creating Objects for DerivedItemRequirement ----");
        for (String[] row : allDerivedItemRequirement) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Requirement");
            Artifacts.getDerItemReqObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for DerivedSystemRequirement ----");
        for (String[] row : allDerivedSystemRequirement) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Requirement");
            Artifacts.getDerSysReqObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for Interface ----");
        for (String[] row : allInterface) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Interface");
            Artifacts.getInterfaceObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for InterfaceInput ----");
        for (String[] row : allInterfaceInput) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("InterfaceInput");
            Artifacts.getInterfaceInputObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for InterfaceOutput ----");
        for (String[] row : allInterfaceOutput) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("InterfaceOutput");
            Artifacts.getInterfaceOutputObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for Item ----");
        for (String[] row : allItem) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("Item");
            Artifacts.getItemObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for ItemRequirement ----");
        for (String[] row : allItemRequirement) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("ItemRequirement");
            Artifacts.getItemReqObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for System ----");
        for (String[] row : allSystem) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("System");
            Artifacts.getItemObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for SystemRequirement ----");
        for (String[] row : allSystemRequirement) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("SystemRequirement");
            Artifacts.getSysReqObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
        }

    	System.out.println("---- Creating Objects for System ----");
        for (String[] row : allSystem) {
            Evidence newEvidenceObj = new Evidence();
            newEvidenceObj.setId(row[0]);
            newEvidenceObj.setType("System");
            Artifacts.getSystemObjs().add(newEvidenceObj);
            System.out.println("Created Object for " + row[0]);
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
                int indx = EvidenceUtils.getEvidenceObjIndxById(Artifacts.getInterfaceObjs(), row[interfaceIdCol]);
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
                            .getHasInputs()
                            .add(
                                    EvidenceUtils.getEvidenceObjById(
                                    		Artifacts.getInterfaceOutputObjs(), row[outputIdCol]));
                }
            }
        }

        // get the header line for allSystemWIthInterface csv file
//        System.out.println( RackQueryUtils.createCsvFilePath(DataProcessorUtils.getVarCSVID("allSystemWIthInterface", config), rackDir));

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

        for (String[] row : allSystemWIthInterface) {
            if ((row[systemIdCol] != null)) {
                // find index of the object in the appropriate evidence list
                int indx = EvidenceUtils.getEvidenceObjIndxById(Artifacts.getSystemObjs(), row[systemIdCol]);
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
                int indx = EvidenceUtils.getEvidenceObjIndxById(Artifacts.getSysReqObjs(), row[sysReqIdCol]);
                // add the data to the object
                if ((row[systemIdCol2] != null)) {
                	Artifacts.getSysReqObjs()
                            .get(indx)
                            .getAllocatedTo()
                            .add(EvidenceUtils.getEvidenceObjById(Artifacts.getSystemObjs(), row[systemIdCol2]));
                }
            }
        }

        // get the header line for allItemRequirementWIthItem csv file
        String[] allItemRequirementWIthItemCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "allItemRequirementWIthItem", config),
                                rackDir));
        int itemReqIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthItemCols, config.getItemReq() + "_id");
        int itemIdCol =
                CustomStringUtils.getCSVColumnIndex(
                        allItemRequirementWIthItemCols, config.getItem() + "_id");

        for (String[] row : allItemRequirementWIthItem) {
            if ((row[itemReqIdCol] != null)) {
                // find index of the object in the appropriate evidence list
                int indx = EvidenceUtils.getEvidenceObjIndxById(Artifacts.getItemReqObjs(), row[itemReqIdCol]);
                // add the data to the object
                if ((row[itemIdCol] != null)) {
                	Artifacts.getItemReqObjs()
                            .get(indx)
                            .getAllocatedTo()
                            .add(EvidenceUtils.getEvidenceObjById(Artifacts.getItemObjs(), row[itemIdCol]));
                }
            }
        }
        
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
        int itemReqIdCol2 =
                CustomStringUtils.getCSVColumnIndex(
                		allItemRequirementWIthSystemRequirementCols, config.getItemReq() + "_id");

        for (String[] row : allItemRequirementWIthSystemRequirement) {
            if ((row[itemReqIdCol2] != null)) {
                // find index of the object in the appropriate evidence list
                int indx = EvidenceUtils.getEvidenceObjIndxById(Artifacts.getItemReqObjs(), row[itemReqIdCol2]);
                // add the data to the object
                if ((row[sysReqIdCol2] != null)) {
                	Artifacts.getItemReqObjs()
                            .get(indx)
                            .getAllocatedTo()
                            .add(EvidenceUtils.getEvidenceObjById(Artifacts.getSysReqObjs(), row[sysReqIdCol2]));
                }
            }
        }
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
        
        planData =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DataProcessorUtils.getVarCSVID(
                                        "planData", config),
                                rackDir));
    }

    /**
     * Function to get the config
     *
     * @param rackDir
     * @param configPath
     */
    private void getConfig(String rackDir, String configPath) {
        // Get configuration
        config = ConfigReader.getConfigFromRACK(rackDir);
    }

    // Entry point that does sequence of operations
    public void getPlanData(String rackDir, String configFileName) {
        String configPath = rackDir + "/" + configFileName;

        // clear the rack directory
        CustomFileUtils.clearDirectory(rackDir);
        
        // read project config from RACK
        getConfig(rackDir, configPath);
        
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
}
