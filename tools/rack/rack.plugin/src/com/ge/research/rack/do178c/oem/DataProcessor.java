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

import com.ge.research.rack.analysis.structures.AnalysisNode;
import com.ge.research.rack.analysis.structures.PlanObjective;
import com.ge.research.rack.analysis.structures.PlanTable;
import com.ge.research.rack.analysis.structures.SparqlConnectionInfo;
import com.ge.research.rack.analysis.utils.CustomStringUtils;
import com.ge.research.rack.analysis.utils.RackQueryUtils;
import com.ge.research.rack.do178c.constants.DO178CQueries;
import com.ge.research.rack.do178c.structures.Activity;
import com.ge.research.rack.do178c.structures.DataItem;
import com.ge.research.rack.do178c.structures.Objective;
import com.ge.research.rack.do178c.structures.Output;
import com.ge.research.rack.do178c.structures.PsacNode;
import com.ge.research.rack.do178c.structures.Requirement;
import com.ge.research.rack.do178c.structures.ReviewLog;
import com.ge.research.rack.do178c.structures.SwComponent;
import com.ge.research.rack.do178c.structures.Test;
import com.ge.research.rack.do178c.utils.LogicUtils;
import com.ge.research.rack.do178c.utils.PsacNodeUtils;
import com.ge.research.rack.utils.CSVUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Saswata Paul
 */
public class DataProcessor extends com.ge.research.rack.analysis.structures.DataProcessor {

    // class variables to store required parameters
    String rackDir;

    // ----------------------------------------------------------------------

    // Class Variables to store the raw data fetched from RACK

    // Connection from PSAC to activities
    private List<String[]> psacToActivityData;

    // Documents
    private List<String[]> allDocs;

    // SRS
    private List<String[]> allSRS;

    // PIDS
    private List<String[]> allPIDS;

    // CSID
    private List<String[]> allCSID;

    // SRS to CSID
    private List<String[]> srsToPIDSorCSID;

    // derived SRS
    private List<String[]> allDerSRS;

    // derived SRS trace
    private List<String[]> derSRSTrace;

    // Subdd
    private List<String[]> allSUBDD;

    // Subdd to SRS
    private List<String[]> subddToSRS;

    // derived SubDD
    private List<String[]> allDerSUBDD;

    // derived SUBDD trace
    private List<String[]> derSUBDDTrace;

    // derived SBVT
    private List<String[]> allSBVT;

    // all review logs SBVT
    private List<String[]> allREVLOGS;

    // all swcomponents
    private List<String[]> allSWCOMPS;

    // all sources (DOCUMENT) traced to entities
    private List<String[]> allSOURCETRACE;

    // ----------------------------------------------------------------------

    // class variables to store data as Objects

    private List<DataItem> allDataItemObjs = new ArrayList<DataItem>();

    private List<Requirement> allCSIDObjs = new ArrayList<Requirement>();

    private List<Requirement> allPIDSObjs = new ArrayList<Requirement>();

    private List<Requirement> allSRSObjs = new ArrayList<Requirement>();

    private List<Requirement> allDerSRSObjs = new ArrayList<Requirement>();

    private List<Requirement> allSUBDDObjs = new ArrayList<Requirement>();

    private List<Requirement> allDerSUBDDObjs = new ArrayList<Requirement>();

    private List<Test> allSBVTObjs = new ArrayList<Test>();

    private List<ReviewLog> allRevLogObjs = new ArrayList<ReviewLog>();

    private List<SwComponent> allSwCompObjs = new ArrayList<SwComponent>();

    // ----------------------------------------------------------------------

    private PlanTable<Objective> processTableDummy(PlanTable<Objective> tabObj, int numNoData) {
        // TODO : dummy for now
        tabObj.setNoData(true);
        tabObj.setNumObjComplete(0);
        tabObj.setNumObjNoData(numNoData);
        tabObj.setNumObjPartial(0);
        tabObj.setNumObjPassed(0);
        tabObj.setNumObjFailed(0);

        return tabObj;
    }

    private Objective processObjectiveDummy(Objective objObj) {

        objObj.setNoData(true);
        objObj.setPartialData(false);
        objObj.setPassed(false);

        return objObj;
    }

    private List<String> findSource(String entityId) {

        List<String> sources = new ArrayList<String>();

        for (String[] row : allSOURCETRACE) {
            if (row.length > 1) {
                if ((row[0] != null) && (row[1] != null)) {
                    if (row[1].equals(entityId)) {
                        System.out.println("Found source for " + entityId);
                        sources.add(row[0]);
                    }
                }
            }
        }

        if (sources.size() > 0) {
            // remove duplicates
            Set<String> set = new HashSet<>(sources);
            sources.clear();
            sources.addAll(set);
            return sources;
        }

        return null;
    }

    /**
     * Takes a table object with pre-populated objectives list and decides status
     *
     * @param tabObj
     * @return
     */
    public static PlanTable<Objective> fillTableStatus(PlanTable<Objective> tabObj) {
        boolean failFlag = false;
        int partialCounter = 0; // count the number of objectives that have reported partial data
        int nullCounter = 0; // count the number of objectives that have reported no data
        int passCounter = 0; // count the number of passed objectives
        int failCounter = 0; // count the number of failed objectives

        //        System.out.println("Table analysis: " + tabObj.getId());

        for (PlanObjective objective : tabObj.getTabObjectives()) {
            if (objective.isNoData()) {
                //                System.out.println("Table object nodata" + tabObj.getId() +
                // objective.getId());
                nullCounter = nullCounter + 1;
            } else if (objective.isPartialData()) {
                //                System.out.println("Table object partialdata" + tabObj.getId() +
                // objective.getId());
                partialCounter = partialCounter + 1;
            } else if (!objective.isPassed()) {
                //                System.out.println("Table object failed" + tabObj.getId() +
                // objective.getId());
                failFlag = true;
                failCounter = failCounter + 1;
            } else if (objective.isPassed()) {
                //                System.out.println("Table object passed" + tabObj.getId() +
                // objective.getId());
                passCounter = passCounter + 1;
            }
        }

        tabObj.setNumObjComplete(tabObj.getTabObjectives().size() - nullCounter - partialCounter);
        tabObj.setNumObjNoData(nullCounter);
        tabObj.setNumObjPartial(partialCounter);
        tabObj.setNumObjPassed(passCounter);
        tabObj.setNumObjFailed(failCounter);

        System.out.println("Table nullcounter" + tabObj.getNumObjNoData());
        System.out.println("Table partialCOunter" + tabObj.getNumObjPartial());
        System.out.println("Table failcounter" + tabObj.getNumObjFailed());
        System.out.println("Table passcounter" + tabObj.getNumObjPassed());
        System.out.println("Table total" + tabObj.getTabObjectives().size());

        // if all objectives have reported no data, then set no data = true, partial = false
        // otherwise , if only some have reported no data, then set partial = true, no data = false
        // if no objective has reported no data, ie, data is complete, set partial = false, no data
        // = false
        if (nullCounter == tabObj.getTabObjectives().size()) { // nodata
            System.out.println("Table " + tabObj.getId() + " reported no data");
            tabObj.setNoData(true);
            tabObj.setPartialData(false);
        } else if ((partialCounter < tabObj.getTabObjectives().size())
                && (partialCounter > 0)) { // partial data case 1: some objectives are partial
            tabObj.setNoData(false);
            tabObj.setPartialData(true);
            System.out.println("Table " + tabObj.getId() + " reported only partial data");
        } else if ((nullCounter + partialCounter)
                < tabObj.getTabObjectives()
                        .size()) { // partial data case 2: no objective is partial, but some are no
            // data
            tabObj.setNoData(false);
            tabObj.setPartialData(true);
            System.out.println("Table " + tabObj.getId() + " reported only partial data");

        } else if ((nullCounter == 0) && (partialCounter == 0)) { // complete data
            tabObj.setNoData(false);
            tabObj.setPartialData(false);
            if (failFlag) {
                tabObj.setPassed(false);
                System.out.println("Table " + tabObj.getId() + " failed");
            } else if (!failFlag) {
                tabObj.setPassed(true);
                System.out.println("Table " + tabObj.getId() + " passed");
            }
        }

        return tabObj;
    }

    private PsacNode computePsacCompliance(PsacNode psacNode) {
        PsacNode psacCompNode = new PsacNode();

        psacCompNode.setMainOFP(psacNode.getMainOFP());

        /**
         * for each table in psacNode, send them to their respective dataprocessors Note: for now,
         * will only do for table A2
         */

        // ----- Table A1

        PlanTable<Objective> tableA1CompNode = new PlanTable<Objective>();
        tableA1CompNode.setId(PsacNodeUtils.getTableById(psacNode, "A1").getId());
        tableA1CompNode.setDescription(PsacNodeUtils.getTableById(psacNode, "A1").getDescription());

        tableA1CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable1.processObjectiveA1_1and2and3and4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-1")));

        tableA1CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable1.processObjectiveA1_1and2and3and4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-2")));

        tableA1CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable1.processObjectiveA1_1and2and3and4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-3")));

        tableA1CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable1.processObjectiveA1_1and2and3and4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-4")));

        tableA1CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-5")));

        tableA1CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-6")));

        tableA1CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A1", "A1-7")));

        // ----- Table A2

        PlanTable<Objective> tableA2CompNode = new PlanTable<Objective>();
        tableA2CompNode.setId(PsacNodeUtils.getTableById(psacNode, "A2").getId());
        tableA2CompNode.setDescription(PsacNodeUtils.getTableById(psacNode, "A2").getDescription());
        // adding the procesed objectives for this table
        tableA2CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable2.processObjectiveA2_1(
                                PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-1")));
        tableA2CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable2.processObjectiveA2_2(
                                PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-2")));

        tableA2CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-3")));

        tableA2CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable2.processObjectiveA2_4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-4")));
        tableA2CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable2.processObjectiveA2_5(
                                PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-5")));
        tableA2CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-6")));
        tableA2CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A2", "A2-7")));

        // ----- Table A3

        PlanTable<Objective> tableA3CompNode = new PlanTable<Objective>();
        tableA3CompNode.setId(PsacNodeUtils.getTableById(psacNode, "A3").getId());
        tableA3CompNode.setDescription(PsacNodeUtils.getTableById(psacNode, "A3").getDescription());
        tableA3CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable3.processObjectiveA3_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-1")));
        tableA3CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable3.processObjectiveA3_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-2")));

        tableA3CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable3.processObjectiveA3_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-3")));

        tableA3CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable3.processObjectiveA3_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-4")));
        tableA3CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable3.processObjectiveA3_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-5")));
        tableA3CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable3.processObjectiveA3_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-6")));
        tableA3CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A3", "A3-7")));

        // ----- Table A4

        PlanTable<Objective> tableA4CompNode = new PlanTable<Objective>();
        tableA4CompNode.setId(PsacNodeUtils.getTableById(psacNode, "A4").getId());
        tableA4CompNode.setDescription(PsacNodeUtils.getTableById(psacNode, "A4").getDescription());
        tableA4CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable4.processObjectiveA4_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-1")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable4.processObjectiveA4_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-2")));

        tableA4CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable4.processObjectiveA4_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-3")));

        tableA4CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable4.processObjectiveA4_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-4")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable4.processObjectiveA4_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-5")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable4.processObjectiveA4_Common(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-6")));
        tableA4CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-7")));
        tableA4CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-8")));
        tableA4CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-9")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        processObjectiveDummy(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-10")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        processObjectiveDummy(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-11")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        processObjectiveDummy(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-12")));
        tableA4CompNode
                .getTabObjectives()
                .add(
                        processObjectiveDummy(
                                PsacNodeUtils.getObjectiveById(psacNode, "A4", "A4-13")));

        // ----- Table A5

        PlanTable<Objective> tableA5CompNode = new PlanTable<Objective>();
        tableA5CompNode.setId(PsacNodeUtils.getTableById(psacNode, "A5").getId());
        tableA5CompNode.setDescription(PsacNodeUtils.getTableById(psacNode, "A5").getDescription());

        // adding the procesed objectives for this table
        tableA5CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable5.processObjectiveA5_1_2_3_4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-1")));
        tableA5CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable5.processObjectiveA5_1_2_3_4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-2")));

        tableA5CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable5.processObjectiveA5_1_2_3_4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-3")));

        tableA5CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable5.processObjectiveA5_1_2_3_4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-4")));

        tableA5CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable5.processObjectiveA5_5(
                                PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-5")));

        tableA5CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-6")));
        tableA5CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-7")));
        tableA5CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-8")));
        tableA5CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A5", "A5-9")));

        // ----- Table A6

        PlanTable<Objective> tableA6CompNode = new PlanTable<Objective>();
        // just addding the raw tables for this table
        tableA6CompNode = PsacNodeUtils.getTableById(psacNode, "A6");

        // ----- Table A7

        PlanTable<Objective> tableA7CompNode = new PlanTable<Objective>();
        tableA7CompNode.setId(PsacNodeUtils.getTableById(psacNode, "A7").getId());
        tableA7CompNode.setDescription(PsacNodeUtils.getTableById(psacNode, "A7").getDescription());

        // adding the procesed objectives for this table
        tableA7CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable7.processObjectiveA7_1(
                                PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-1")));
        tableA7CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-2")));

        tableA7CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable7.processObjectiveA7_3(
                                PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-3")));

        tableA7CompNode
                .getTabObjectives()
                .add(
                        ComplianceTable7.processObjectiveA7_4(
                                PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-4")));
        tableA7CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-5")));
        tableA7CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-6")));
        tableA7CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-7")));
        tableA7CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-8")));
        tableA7CompNode
                .getTabObjectives()
                .add(processObjectiveDummy(PsacNodeUtils.getObjectiveById(psacNode, "A7", "A7-9")));

        // ----- Table A8

        PlanTable<Objective> tableA8CompNode = new PlanTable<Objective>();
        // just addding the raw tables for this table
        tableA8CompNode = PsacNodeUtils.getTableById(psacNode, "A8");

        // ----- Table A9

        PlanTable<Objective> tableA9CompNode = new PlanTable<Objective>();
        // just addding the raw tables for this table
        tableA9CompNode = PsacNodeUtils.getTableById(psacNode, "A9");

        // ----- Table A10

        PlanTable<Objective> tableA10CompNode = new PlanTable<Objective>();
        // just addding the raw tables for this table
        tableA10CompNode = PsacNodeUtils.getTableById(psacNode, "A10");

        /**
         * for each table in psacNode, compute table status Note: for now, we will add dummy
         * hardcoded values for all tables except A2
         */
        tableA1CompNode = fillTableStatus(tableA1CompNode);
        tableA2CompNode = fillTableStatus(tableA2CompNode);
        tableA3CompNode = fillTableStatus(tableA3CompNode);
        tableA4CompNode = fillTableStatus(tableA4CompNode);
        tableA5CompNode = fillTableStatus(tableA5CompNode);
        tableA6CompNode = processTableDummy(tableA6CompNode, 5);
        tableA7CompNode = fillTableStatus(tableA7CompNode);
        tableA8CompNode = processTableDummy(tableA8CompNode, 6);
        tableA9CompNode = processTableDummy(tableA9CompNode, 5);
        tableA10CompNode = processTableDummy(tableA10CompNode, 3);

        // add the new tables to the new psacCompNode
        psacCompNode.getReportTables().add(tableA1CompNode);
        psacCompNode.getReportTables().add(tableA2CompNode);
        psacCompNode.getReportTables().add(tableA3CompNode);
        psacCompNode.getReportTables().add(tableA4CompNode);
        psacCompNode.getReportTables().add(tableA5CompNode);
        psacCompNode.getReportTables().add(tableA6CompNode);
        psacCompNode.getReportTables().add(tableA7CompNode);
        psacCompNode.getReportTables().add(tableA8CompNode);
        psacCompNode.getReportTables().add(tableA9CompNode);
        psacCompNode.getReportTables().add(tableA10CompNode);

        return psacCompNode;
    }

    /**
     * Attaches the outputs to the objectives as applicable
     *
     * @param psacNode
     * @return
     */
    private PsacNode attachOutputsToObjectives(PsacNode psacNode) {
        System.out.println("Connecting relevant outputs to PSAC objectives ...\n");

        if (psacNode.getReportTables().size() > 0) {
            for (PlanTable<Objective> tableObj : psacNode.getReportTables()) {

                if (tableObj.getTabObjectives().size() > 0) {
                    for (PlanObjective objectiveObj : tableObj.getTabObjectives()) {

                        // Attach Docs to A1-1, A1-2. A1-3, and A1-4
                        if (objectiveObj.getId().equalsIgnoreCase("A1-1")
                                || objectiveObj.getId().equalsIgnoreCase("A1-2")
                                || objectiveObj.getId().equalsIgnoreCase("A1-3")
                                || objectiveObj.getId().equalsIgnoreCase("A1-4")) {
                            for (DataItem doc : allDataItemObjs) {
                                if (doc.getObjectiveId().equals(objectiveObj.getId())) {
                                    ((Output) objectiveObj.getOutputs()).getDocuments().add(doc);
                                }
                            }
                        }

                        // Attach SRS to A2-1
                        if (objectiveObj.getId().equalsIgnoreCase("A2-1")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                            }
                        }

                        // Attach Derived SRS to A2-2
                        if (objectiveObj.getId().equalsIgnoreCase("A2-2")) {
                            for (Requirement req : allDerSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                            }
                        }

                        // Attach SubDD to A2-4
                        if (objectiveObj.getId().equalsIgnoreCase("A2-4")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                            }
                        }

                        // Attach Derived SubDD to A2-5
                        if (objectiveObj.getId().equalsIgnoreCase("A2-5")) {
                            for (Requirement req : allDerSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                            }
                        }
                        // Attach SRS and relevant logs to A3-1
                        if (objectiveObj.getId().equalsIgnoreCase("A3-1")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SRS and relevant logs to A3-2
                        if (objectiveObj.getId().equalsIgnoreCase("A3-2")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SRS and relevant logs to A3-3
                        if (objectiveObj.getId().equalsIgnoreCase("A3-3")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SRS and relevant logs to A3-4
                        if (objectiveObj.getId().equalsIgnoreCase("A3-4")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }

                        // Attach SRS and relevant logs to A3-5
                        if (objectiveObj.getId().equalsIgnoreCase("A3-5")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SRS and relevant logs to A3-6
                        if (objectiveObj.getId().equalsIgnoreCase("A3-6")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }

                        // Attach SUBDD and relevant logs to A4-1
                        if (objectiveObj.getId().equalsIgnoreCase("A4-1")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SUBDD and relevant logs to A4-2
                        if (objectiveObj.getId().equalsIgnoreCase("A4-2")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SUBDD and relevant logs to A4-3
                        if (objectiveObj.getId().equalsIgnoreCase("A4-3")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SUBDD and relevant logs to A4-4
                        if (objectiveObj.getId().equalsIgnoreCase("A4-4")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SUBDD and relevant logs to A4-4
                        if (objectiveObj.getId().equalsIgnoreCase("A4-5")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SUBDD and relevant logs to A4-4
                        if (objectiveObj.getId().equalsIgnoreCase("A4-6")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }

                        // Attach SWCOMPONENTS and relevant logs (none for now, TODO) to A5-1 to
                        // A5-5
                        if (objectiveObj.getId().equalsIgnoreCase("A5-1")
                                || objectiveObj.getId().equalsIgnoreCase("A5-2")
                                || objectiveObj.getId().equalsIgnoreCase("A5-3")
                                || objectiveObj.getId().equalsIgnoreCase("A5-4")
                                || objectiveObj.getId().equalsIgnoreCase("A5-5")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getLogs() != null) {
                                    for (ReviewLog log : req.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                            for (SwComponent swComp : allSwCompObjs) {
                                ((Output) objectiveObj.getOutputs()).getSwComponents().add(swComp);
                            }
                        }

                        // Attach SBVT and logs to A7-1
                        if (objectiveObj.getId().equalsIgnoreCase("A7-1")) {
                            for (Test tst : allSBVTObjs) {
                                ((Output) objectiveObj.getOutputs()).getTests().add(tst);
                                if (tst.getLogs() != null) {
                                    for (ReviewLog log : tst.getLogs()) {
                                        ((Output) objectiveObj.getOutputs()).getLogs().add(log);
                                    }
                                }
                            }
                        }
                        // Attach SRS and relevant SBVT to A7-3
                        if (objectiveObj.getId().equalsIgnoreCase("A7-3")) {
                            for (Requirement req : allSRSObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getTests() != null) {
                                    for (Test tst : req.getTests()) {
                                        // check if the test was already added from another
                                        // requirement
                                        Integer existingIndex =
                                                LogicUtils.alreadyCreatedTest(
                                                        ((Output) objectiveObj.getOutputs())
                                                                .getTests(),
                                                        tst.getId());
                                        if (existingIndex == null) {
                                            ((Output) objectiveObj.getOutputs())
                                                    .getTests()
                                                    .add(tst);
                                        }
                                    }
                                }
                            }
                        }
                        // Attach SubDD and relevant SBVT to A7-4
                        if (objectiveObj.getId().equalsIgnoreCase("A7-4")) {
                            for (Requirement req : allSUBDDObjs) {
                                ((Output) objectiveObj.getOutputs()).getRequirements().add(req);
                                if (req.getTests() != null) {
                                    for (Test tst : req.getTests()) {
                                        // check if the test was already added from another
                                        // requirement
                                        Integer existingIndex =
                                                LogicUtils.alreadyCreatedTest(
                                                        ((Output) objectiveObj.getOutputs())
                                                                .getTests(),
                                                        tst.getId());
                                        if (existingIndex == null) {
                                            ((Output) objectiveObj.getOutputs())
                                                    .getTests()
                                                    .add(tst);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Connected relevant outputs to PSAC objectives\n");

        return psacNode;
    }

    /**
     * Creates a psac node from the PSAC data inside rack
     *
     * @return
     */
    private PsacNode getPsacFromRack() {
        System.out.println("Creating PSAC object from data ...\n");

        PsacNode psacNode = new PsacNode();

        // get all column numbers
        String[] psacToActivityDataCols =
                RackQueryUtils.readCSVHeader(
                        RackQueryUtils.createCsvFilePath("psac_to_activities", rackDir));

        int psacIdCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "identifier_psac");
        int swIdCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "identifier_software");
        int swLevCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "software_level");
        int tableIdCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "identifier_table");
        int tableDescCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "description_table");
        int objectiveIdCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "identifier_objective");
        int objectiveDescCol =
                CustomStringUtils.getCSVColumnIndex(
                        psacToActivityDataCols, "description_objective");
        int activityIdCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "identifier_activity");
        int activityDescCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "description_activity");
        int performsIdCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "identifier_performs");
        int performsDescCol =
                CustomStringUtils.getCSVColumnIndex(psacToActivityDataCols, "description_performs");

        for (String[] row : psacToActivityData) {

            // these two details will be redundantly filled everytime, but it should not matter as
            // long as the data is not garbage
            psacNode.setId(row[psacIdCol]);
            psacNode.setMainOFP(
                    row[swIdCol]
                            + " (Level "
                            + row[swLevCol].charAt(row[swLevCol].length() - 1)
                            + ")");

            if (row.length > tableIdCol) {
                String tableId = row[tableIdCol].replace("Table-", "");

                //                System.out.println("Preparing to add (unless already added):" +
                // row[tableIdCol]);

                Integer indexofExistingTable =
                        PsacNodeUtils.alreadyCreatedTable(psacNode.getReportTables(), tableId);

                if (indexofExistingTable == null) { // not yet created
                    // create a new table object, add its id
                    PlanTable<Objective> newTableObj = new PlanTable<Objective>();
                    newTableObj.setId(tableId);
                    newTableObj.setDescription(row[tableDescCol]);

                    // if there is an objective, add to new table

                    if (row.length > objectiveIdCol) {
                        String objectiveId = row[objectiveIdCol].replace("Objective-", "");

                        Integer indexofExistingObjective =
                                PsacNodeUtils.alreadyCreatedObjective(
                                        newTableObj.getTabObjectives(), objectiveId);

                        if (indexofExistingObjective == null) { // not yet created
                            // create a new objective obect, add its id
                            Objective newObjectiveObj = new Objective();
                            newObjectiveObj.setId(objectiveId);
                            newObjectiveObj.setDescription(row[objectiveDescCol]);

                            // if there is an activity in this row, add it to the new objective
                            // object

                            if (row.length > activityIdCol) {
                                String activityId = row[activityIdCol].replace("Activity-", "");

                                Integer indexofExistingActivity =
                                        PsacNodeUtils.alreadyCreatedActivity(
                                                newObjectiveObj.getObjActivities(), activityId);

                                if (indexofExistingActivity == null) { // not yet created
                                    // Create a new activity object
                                    Activity newActivityObj = new Activity();
                                    newActivityObj.setId(activityId);
                                    newActivityObj.setDescription(row[activityDescCol]);

                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        newActivityObj.getPerforms(), performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            newActivityObj.getPerforms().add(newPerformsObj);
                                        }
                                    }

                                    // add the activity object to the objective objects list of
                                    // activities
                                    newObjectiveObj.getObjActivities().add(newActivityObj);

                                } else {
                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        newObjectiveObj
                                                                .getObjActivities()
                                                                .get(indexofExistingActivity)
                                                                .getPerforms(),
                                                        performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            newObjectiveObj
                                                    .getObjActivities()
                                                    .get(indexofExistingActivity)
                                                    .getPerforms()
                                                    .add(newPerformsObj);
                                        }
                                    }
                                }
                            }

                            // add new objective to new table
                            newTableObj.getTabObjectives().add(newObjectiveObj);
                        } else {
                            // if there is activity, add to exiting objective
                            if (row.length > activityIdCol) {
                                String activityId = row[activityIdCol].replace("Activity-", "");

                                Integer indexofExistingActivity =
                                        PsacNodeUtils.alreadyCreatedActivity(
                                                ((Objective)
                                                                newTableObj
                                                                        .getTabObjectives()
                                                                        .get(
                                                                                indexofExistingObjective))
                                                        .getObjActivities(),
                                                activityId);

                                if (indexofExistingActivity == null) { // not yet created
                                    // Create a new activity object
                                    Activity newActivityObj = new Activity();
                                    newActivityObj.setId(activityId);
                                    newActivityObj.setDescription(row[activityDescCol]);

                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        newActivityObj.getPerforms(), performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            newActivityObj.getPerforms().add(newPerformsObj);
                                        }
                                    }

                                    // add the activity object to the objective objects list of
                                    // activities
                                    ((Objective)
                                                    newTableObj
                                                            .getTabObjectives()
                                                            .get(indexofExistingObjective))
                                            .getObjActivities()
                                            .add(newActivityObj);
                                } else {
                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        ((Objective)
                                                                        newTableObj
                                                                                .getTabObjectives()
                                                                                .get(
                                                                                        indexofExistingObjective))
                                                                .getObjActivities()
                                                                .get(indexofExistingActivity)
                                                                .getPerforms(),
                                                        performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            ((Objective)
                                                            newTableObj
                                                                    .getTabObjectives()
                                                                    .get(indexofExistingObjective))
                                                    .getObjActivities()
                                                    .get(indexofExistingActivity)
                                                    .getPerforms()
                                                    .add(newPerformsObj);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // add the table object to the list of tables in psac
                    psacNode.getReportTables().add(newTableObj);
                } else {
                    // if there is an objective, add to existing table
                    if (row.length > objectiveIdCol) {
                        String objectiveId = row[objectiveIdCol].replace("Objective-", "");

                        Integer indexofExistingObjective =
                                PsacNodeUtils.alreadyCreatedObjective(
                                        psacNode.getReportTables()
                                                .get(indexofExistingTable)
                                                .getTabObjectives(),
                                        objectiveId);

                        if (indexofExistingObjective == null) { // not yet created
                            // create a new objective obect, add its id
                            Objective newObjectiveObj = new Objective();
                            newObjectiveObj.setId(objectiveId);
                            newObjectiveObj.setDescription(row[objectiveDescCol]);

                            // if there is an activity in this row, add it to the new objective
                            // object

                            if (row.length > activityIdCol) {
                                String activityId = row[activityIdCol].replace("Activity-", "");

                                Integer indexofExistingActivity =
                                        PsacNodeUtils.alreadyCreatedActivity(
                                                newObjectiveObj.getObjActivities(), activityId);

                                if (indexofExistingActivity == null) { // not yet created
                                    // Create a new activity object
                                    Activity newActivityObj = new Activity();
                                    newActivityObj.setId(activityId);
                                    newActivityObj.setDescription(row[activityDescCol]);

                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        newActivityObj.getPerforms(), performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            newActivityObj.getPerforms().add(newPerformsObj);
                                        }
                                    }

                                    // add the activity object to the objective objects list of
                                    // activities
                                    newObjectiveObj.getObjActivities().add(newActivityObj);
                                } else {
                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        ((Objective)
                                                                        psacNode.getReportTables()
                                                                                .get(
                                                                                        indexofExistingTable)
                                                                                .getTabObjectives()
                                                                                .get(indexofExistingObjective))
                                                                .getObjActivities()
                                                                .get(indexofExistingActivity)
                                                                .getPerforms(),
                                                        performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            ((Objective)
                                                            psacNode.getReportTables()
                                                                    .get(indexofExistingTable)
                                                                    .getTabObjectives()
                                                                    .get(indexofExistingObjective))
                                                    .getObjActivities()
                                                    .get(indexofExistingActivity)
                                                    .getPerforms()
                                                    .add(newPerformsObj);
                                        }
                                    }
                                }
                            }

                            // add new objective to new table
                            psacNode.getReportTables()
                                    .get(indexofExistingTable)
                                    .getTabObjectives()
                                    .add(newObjectiveObj);
                        } else {
                            // if there is activity, add to exiting objective
                            if (row.length > activityIdCol) {
                                String activityId = row[activityIdCol].replace("Activity-", "");

                                Integer indexofExistingActivity =
                                        PsacNodeUtils.alreadyCreatedActivity(
                                                ((Objective)
                                                                psacNode.getReportTables()
                                                                        .get(indexofExistingTable)
                                                                        .getTabObjectives()
                                                                        .get(
                                                                                indexofExistingObjective))
                                                        .getObjActivities(),
                                                activityId);

                                if (indexofExistingActivity == null) { // not yet created
                                    // Create a new activity object
                                    Activity newActivityObj = new Activity();
                                    newActivityObj.setId(activityId);
                                    newActivityObj.setDescription(row[activityDescCol]);

                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        newActivityObj.getPerforms(), performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            newActivityObj.getPerforms().add(newPerformsObj);
                                        }
                                    }

                                    // add the activity object to the objective objects list of
                                    // activities
                                    ((Objective)
                                                    psacNode.getReportTables()
                                                            .get(indexofExistingTable)
                                                            .getTabObjectives()
                                                            .get(indexofExistingObjective))
                                            .getObjActivities()
                                            .add(newActivityObj);
                                } else {
                                    // TODO: add sub-activities, if any
                                    if (row.length > performsIdCol) {
                                        String performsId =
                                                row[performsIdCol]; // no need to replace
                                        // "Activity" here

                                        Integer indexOfExistingPerforms =
                                                PsacNodeUtils.alreadyCreatedActivity(
                                                        ((Objective)
                                                                        psacNode.getReportTables()
                                                                                .get(
                                                                                        indexofExistingTable)
                                                                                .getTabObjectives()
                                                                                .get(
                                                                                        indexofExistingObjective))
                                                                .getObjActivities()
                                                                .get(indexofExistingActivity)
                                                                .getPerforms(),
                                                        performsId);

                                        if (indexOfExistingPerforms == null) { // not yet created
                                            // Create a new activity object for the performs
                                            Activity newPerformsObj = new Activity();
                                            newPerformsObj.setId(
                                                    performsId.replace("Activity-", ""));
                                            newPerformsObj.setDescription(row[performsDescCol]);

                                            // add to activity
                                            ((Objective)
                                                            psacNode.getReportTables()
                                                                    .get(indexofExistingTable)
                                                                    .getTabObjectives()
                                                                    .get(indexofExistingObjective))
                                                    .getObjActivities()
                                                    .get(indexofExistingActivity)
                                                    .getPerforms()
                                                    .add(newPerformsObj);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Created PSAC object from data.\n");

        return psacNode;
    }

    /** Creates all document data item objects */
    private void createAllDataItemObjs() {
        // NOTE: This is a little hacked because avatars of data items like PSAC, SDP, SVP, etc were
        // created by us in SADL as text files

        // get the header line for all
        // Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan csv file
        String[] dataItemCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan",
                                rackDir));

        int diIdCol = CustomStringUtils.getCSVColumnIndex(dataItemCols, "identifier_output");
        int diObjCol = CustomStringUtils.getCSVColumnIndex(dataItemCols, "identifier_objective");

        for (String[] row : allDocs) {
            if (row.length > diIdCol) {
                DataItem newDatItemObj = new DataItem();

                newDatItemObj.setId(row[diIdCol]);
                newDatItemObj.setObjectiveId(row[diObjCol].replace("Objective-", ""));

                allDataItemObjs.add(newDatItemObj);
            }
        }
    }

    /** Create all review log data objects */
    private void createAllRevLogObjs() {
        // get the header line for all
        // Objective-A1-1-query-check-output-for-PSAC-SDP-SVP-SCMPlan-SQAPlan csv file
        String[] dataItemCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-multiple-query-all-review-logs", rackDir));

        int logIdCol = CustomStringUtils.getCSVColumnIndex(dataItemCols, "identifier_log");
        int reviewsCol = CustomStringUtils.getCSVColumnIndex(dataItemCols, "identifier_reviews");
        int wasGeneratedByCol =
                CustomStringUtils.getCSVColumnIndex(dataItemCols, "identifier_wasGeneratedBy");
        int resultCol = CustomStringUtils.getCSVColumnIndex(dataItemCols, "identifier_result");

        for (String[] row : allREVLOGS) {
            if ((row.length > logIdCol)
                    && (row.length > reviewsCol)
                    && (row.length > wasGeneratedByCol)
                    && (row.length > resultCol)) {
                ReviewLog newRevLogObj = new ReviewLog();

                newRevLogObj.setId(row[logIdCol]);
                newRevLogObj.setReviews(row[reviewsCol]);
                newRevLogObj.setWasGeneratedBy(row[wasGeneratedByCol]);
                newRevLogObj.setResult(row[resultCol]);

                allRevLogObjs.add(newRevLogObj);
            }
        }
    }

    /** Creates all possible SBVT test objects */
    private void createAllSBVTTestObjs() {
        System.out.println("Creating SBVT_Test Objects");

        // get the header line for all Objective-A7-3-4-query-SBVT-Test csv file
        String[] sbvtCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A7-3-4-query-SBVT-Test", rackDir));

        int sbvtIdCol = CustomStringUtils.getCSVColumnIndex(sbvtCols, "identifier_sbvt_test");
        int resultCol =
                CustomStringUtils.getCSVColumnIndex(sbvtCols, "identifier_sbvt_test_status");
        int verifiesCol = CustomStringUtils.getCSVColumnIndex(sbvtCols, "identifier_verifies");

        for (String[] row : allSBVT) {
            if ((row.length > sbvtIdCol)
                    && (row.length > resultCol)
                    && (row.length > verifiesCol)) {
                // check if the test object already exists
                Integer indexOfExistingTest =
                        LogicUtils.alreadyCreatedTest(allSBVTObjs, row[sbvtIdCol]);
                if (indexOfExistingTest != null) { // exists
                    // add the verifies to the list of existing test
                    // NOTE: We do not add the results because for a given test, if the data is not
                    // garbage, all result rows should be same
                    allSBVTObjs.get(indexOfExistingTest).getVerifies().add(row[verifiesCol]);
                } else { // does not exist
                    // create new test object, add verifies and review logs to it, and add test to
                    // list
                    Test newSbvtTestObj =
                            new Test(row[sbvtIdCol], row[resultCol], row[verifiesCol], "SBVT_Test");

                    // find and add any relevant review logs
                    List<ReviewLog> logList =
                            LogicUtils.findRevLogObjById(allRevLogObjs, row[sbvtIdCol]);
                    if (logList.size() > 0) {
                        newSbvtTestObj.setLogs(logList);
                    }

                    // find source if exists
                    newSbvtTestObj.setSourceDocument(findSource(newSbvtTestObj.getId()));

                    allSBVTObjs.add(newSbvtTestObj);
                }
            }
        }
    }

    /** Ceates all csid req objects */
    private void createAllCSIDObjs() {
        System.out.println("Creating CSID_Req Objects");

        // get the header line for all Objective-A2-1-query-count-all-CSID-Reqs csv file
        String[] csidCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-1-query-count-all-CSID-Reqs", rackDir));

        int csidIdCol = CustomStringUtils.getCSVColumnIndex(csidCols, "identifier_csid_req");
        int csidDescCol = CustomStringUtils.getCSVColumnIndex(csidCols, "description_csid_req");

        for (String[] row : allCSID) {
            if (row.length > csidIdCol) {
                Requirement newCSIDObj = new Requirement();

                newCSIDObj.setId(row[csidIdCol]);
                newCSIDObj.setDescription(row[csidDescCol]);
                newCSIDObj.setType("CSID_Req");

                // find source if exists
                newCSIDObj.setSourceDocument(findSource(newCSIDObj.getId()));

                allCSIDObjs.add(newCSIDObj);
            }
        }
    }

    /** Creates all PIDS Objects */
    private void createAllPIDSObjs() {
        System.out.println("Creating PIDS_Req Objects");

        // get the header line for all Objective-A2-1-query-count-all-PIDS-Reqs csv file
        String[] pidsCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-1-query-count-all-PIDS-Reqs", rackDir));

        int pidsIdCol = CustomStringUtils.getCSVColumnIndex(pidsCols, "identifier_pids_req");
        int pidsDescCol = CustomStringUtils.getCSVColumnIndex(pidsCols, "description_pids_req");

        for (String[] row : allPIDS) {
            if (row.length > pidsIdCol) {
                Requirement newPIDSObj = new Requirement();

                newPIDSObj.setId(row[pidsIdCol]);
                newPIDSObj.setDescription(row[pidsDescCol]);
                newPIDSObj.setType("PIDS_Req");

                // find source if exists
                newPIDSObj.setSourceDocument(findSource(newPIDSObj.getId()));

                allPIDSObjs.add(newPIDSObj);
            }
        }
    }

    /** Creates all SRS objects with trace info */
    private void createAllSRSObjsWithTrace() {
        System.out.println("Creating SRS_Req Objects");

        // get the header line for all Objective-A2-1-query-count-all-SRS-Reqs csv file
        String[] srsCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-1-query-count-all-SRS-Reqs", rackDir));

        int srsIdCol = CustomStringUtils.getCSVColumnIndex(srsCols, "identifier_srs_req");
        int srsDescCol = CustomStringUtils.getCSVColumnIndex(srsCols, "description_srs_req");

        // get the header line for all
        // Objective-A2-1-query-count-all-SRS-Reqs-that-satisfy-CSID_Reqs csv file
        String[] srstToCsidCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-1-query-count-all-SRS-Reqs-that-satisfy-PIDS-or-CSID_Reqs",
                                rackDir));

        int srsIdCol2 = CustomStringUtils.getCSVColumnIndex(srstToCsidCols, "identifier_srs_req");
        int pidsOrCsidIdCol =
                CustomStringUtils.getCSVColumnIndex(srstToCsidCols, "identifier_pids_csid_req");

        for (String[] row : allSRS) {
            if (row[srsIdCol] != null) {
                Requirement newSRSObj = new Requirement();

                newSRSObj.setId(row[srsIdCol]);
                newSRSObj.setDescription(row[srsDescCol]);
                newSRSObj.setType("SRS_Req");

                // find and add any relevant SBVT test
                List<Test> sbvtList =
                        LogicUtils.findTestObjByVerifiesId(allSBVTObjs, row[srsIdCol]);
                if (sbvtList.size() > 0) {
                    newSRSObj.setTests(sbvtList);
                }

                // find and add any relevant review logs
                List<ReviewLog> logList =
                        LogicUtils.findRevLogObjById(allRevLogObjs, row[srsIdCol]);
                if (logList.size() > 0) {
                    newSRSObj.setLogs(logList);
                }

                // find and add trace (satisfies) info
                List<Requirement> satisfies = new ArrayList<Requirement>();

                // -- find PIDS or CSID trace

                for (String[] row2 : srsToPIDSorCSID) {
                    if ((row2.length > srsIdCol2) && (row2.length > pidsOrCsidIdCol)) {
                        if (row2[srsIdCol2].equals(row[srsIdCol])) { // if relevant to this req
                            //            				System.out.println("Finding object for " +
                            // row2[pidsOrCsidIdCol]);

                            // find the pids and add to list if not null
                            Requirement thePIDSObj =
                                    LogicUtils.findReqObjById(allPIDSObjs, row2[pidsOrCsidIdCol]);
                            if (thePIDSObj != null) {
                                satisfies.add(thePIDSObj);
                            } else { // if not pids
                                // find the csid and add to list if not null
                                Requirement theCSIDObj =
                                        LogicUtils.findReqObjById(
                                                allCSIDObjs, row2[pidsOrCsidIdCol]);
                                if (theCSIDObj != null) {
                                    satisfies.add(theCSIDObj);
                                }
                            }
                        }
                    }
                }

                newSRSObj.setSatisfies(satisfies);

                // find source if exists
                newSRSObj.setSourceDocument(findSource(newSRSObj.getId()));

                // add the SRS object to list
                allSRSObjs.add(newSRSObj);
            }
        }
    }

    /** Creates all derived SRS req objects with trace info */
    private void createAllDerSRSObjsWithTrace() {
        System.out.println("Creating DerSRS_Req Objects");

        // get the header line for all Objective-A2-2-query-count-all-Derived-SRS-Reqs csv file
        String[] derSrsCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-2-query-count-all-Derived-SRS-Reqs", rackDir));

        int derSrsIdCol = CustomStringUtils.getCSVColumnIndex(derSrsCols, "identifier_dersrs_req");
        int derSrsDescCol =
                CustomStringUtils.getCSVColumnIndex(derSrsCols, "description_dersrs_req");

        // get the header line for all
        // Objective-A2-2-query-count-all-Derived-SRS-Reqs-that-satisfy-some-req csv file
        String[] derSrsToAnyReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-2-query-count-all-Derived-SRS-Reqs-that-satisfy-some-req",
                                rackDir));

        int derSrsIdCol2 =
                CustomStringUtils.getCSVColumnIndex(derSrsToAnyReqCols, "identifier_dersrs_req");
        int satisfiesIdCol =
                CustomStringUtils.getCSVColumnIndex(derSrsToAnyReqCols, "identifier_satisfies");

        for (String[] row : allDerSRS) {
            if (row[derSrsIdCol] != null) {
                Requirement newDerSRSObj = new Requirement();

                newDerSRSObj.setId(row[derSrsIdCol]);
                newDerSRSObj.setDescription(row[derSrsDescCol]);
                newDerSRSObj.setType("DerSRS_Req");

                // find and add trace (satisfies) info
                List<Requirement> satisfies = new ArrayList<Requirement>();

                // -- find any trace

                for (String[] row2 : derSRSTrace) {
                    if ((row2.length > derSrsIdCol2) && (row2.length > satisfiesIdCol)) {
                        if (row2[derSrsIdCol2].equals(
                                row[derSrsIdCol])) { // if the row is relevant to the req
                            // create a generic requirement with id and add to satisfies
                            Requirement genericReq = new Requirement();
                            genericReq.setId(row2[satisfiesIdCol]);
                            satisfies.add(genericReq);
                        }
                    }
                }

                // add satisfies to derSrs OBJ
                newDerSRSObj.setSatisfies(satisfies);

                // find source if exists
                newDerSRSObj.setSourceDocument(findSource(newDerSRSObj.getId()));

                // add the SRS object to list
                allDerSRSObjs.add(newDerSRSObj);
            }
        }
    }

    /** Creates all subdd req objects with trace info */
    private void createAllSUBDDObjsWithTrace() {
        System.out.println("Creating SubDD_Req Objects");

        // get the header line for all Objective-A2-4-query-count-all-SubDD-Reqs csv file
        String[] subddCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-4-query-count-all-SubDD-Reqs", rackDir));

        int subddIdCol = CustomStringUtils.getCSVColumnIndex(subddCols, "identifier_subdd_req");
        int subddDescCol = CustomStringUtils.getCSVColumnIndex(subddCols, "description_subdd_req");

        // get the header line for all
        // Objective-A2-4-query-count-all-SubDD-Reqs-that-satisfy-SRS_Reqs csv file
        String[] subddtToSrdCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-4-query-count-all-SubDD-Reqs-that-satisfy-SRS_Reqs",
                                rackDir));

        int subddIdCol2 =
                CustomStringUtils.getCSVColumnIndex(subddtToSrdCols, "identifier_subdd_req");
        int srsIdCol = CustomStringUtils.getCSVColumnIndex(subddtToSrdCols, "identifier_srs_req");

        //int counter = 1;

        for (String[] row : allSUBDD) {
            if (row[subddIdCol] != null) {
                Requirement newSUBDDObj = new Requirement();

                newSUBDDObj.setId(row[subddIdCol]);
                newSUBDDObj.setDescription(row[subddDescCol]);
                newSUBDDObj.setType("SubDD_Req");

                // find and add any relevant SBVT test
                List<Test> sbvtList =
                        LogicUtils.findTestObjByVerifiesId(allSBVTObjs, row[subddIdCol]);
                if (sbvtList.size() > 0) {
                    newSUBDDObj.setTests(sbvtList);
                }

                // find and add any relevant review logs
                List<ReviewLog> logList =
                        LogicUtils.findRevLogObjById(allRevLogObjs, row[subddIdCol]);
                if (logList.size() > 0) {
                    newSUBDDObj.setLogs(logList);
                }

                // find and add trace (satisfies) info
                List<Requirement> satisfies = new ArrayList<Requirement>();

                // -- find SRS trace

                for (String[] row2 : subddToSRS) {
                    if ((row2.length > subddIdCol2) && (row2.length > srsIdCol)) {
                        //        				System.out.println("Finding object for " + row2[srsIdCol] + "
                        // iteration number: " + counter);
                        //counter++;

                        if (row2[subddIdCol2].equals(row[subddIdCol])) { // if relevant to the req

                            // find the srs and add to list if not null
                            Requirement theSRSObj =
                                    LogicUtils.findReqObjById(allSRSObjs, row2[srsIdCol]);
                            if (theSRSObj != null) {
                                satisfies.add(theSRSObj);
                            }
                        }
                    }
                }

                // add satisfies to subdd OBJ

                newSUBDDObj.setSatisfies(satisfies);

                // find source if exists
                newSUBDDObj.setSourceDocument(findSource(newSUBDDObj.getId()));

                // add the SRS object to list
                allSUBDDObjs.add(newSUBDDObj);
            }
        }
    }

    /**
     * Creates all SwComponent objects with trace info Note: This is a bit different than others
     * since I took the lazy route of using just one query
     */
    private void createAllSWCOMPObjsWithTrace() {
        System.out.println("Creating SWCOMP Objects");

        // get the header line for all Objective-A5-5-query-swcomponent-subDD-trace csv file
        String[] swcomponentToReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A5-5-query-swcomponent-subDD-trace", rackDir));

        int swcompIdCol =
                CustomStringUtils.getCSVColumnIndex(swcomponentToReqCols, "identifier_swcomponent");
        int reqCol =
                CustomStringUtils.getCSVColumnIndex(swcomponentToReqCols, "identifier_requirement");

        //int counter = 1;
        for (String[] row : allSWCOMPS) {
            if (row[swcompIdCol] != null
                    && (LogicUtils.findSwCompObjById(allSwCompObjs, row[swcompIdCol])
                            == null)) { // if not null and not already created

                SwComponent newSWCOMPObj = new SwComponent();

                newSWCOMPObj.setId(row[swcompIdCol]);

                // find and add trace (satisfies) info
                List<Requirement> wasImpactedBy = new ArrayList<Requirement>();

                // -- find Req trace

                for (String[] row2 : allSWCOMPS) {
                    if ((row2.length > swcompIdCol) && (row2.length > reqCol)) {
                        //                         System.out.println("Finding wasimpactedby for " +
                        // row2[swcompIdCol] + "iteration number: " + counter);
                        //counter++;

                        if (row2[swcompIdCol].equals(
                                row[swcompIdCol])) { // if relevant to the swComp in question

                            // create the req and add to list if not null
                            // Note : This reqs might be new, so may nit be in the list of all
                            // SubDDS, so will create fresh
                            if (row2[reqCol] != null) {
                                if (row2[reqCol].length() > 0) {

                                    // check if a subdd exists wit the id
                                    // find the srs and add to list if not null
                                    Requirement theSubDDObj =
                                            LogicUtils.findReqObjById(allSUBDDObjs, row2[reqCol]);
                                    if (theSubDDObj != null) {
                                        wasImpactedBy.add(theSubDDObj);
                                    } else {
                                        // if a subdd does not exist with the id, then create a req
                                        // obj of type "unknown"
                                        Requirement newReqObj = new Requirement();
                                        newReqObj.setId(row2[reqCol]);
                                        newReqObj.setType("unknown");
                                        wasImpactedBy.add(newReqObj);
                                    }
                                }
                            }
                        }
                    }
                }

                // add satisfies to subdd OBJ

                newSWCOMPObj.setWasImpactedBy(wasImpactedBy);

                // find source if exists
                newSWCOMPObj.setSourceDocument(findSource(newSWCOMPObj.getId()));

                // add the SRS object to list
                allSwCompObjs.add(newSWCOMPObj);
            }
        }
    }

    /**
     * Creates all derived subdd req objects with trace info (trace shouldn't be there, but if
     * exists, we attach)
     */
    private void createAllDerSUBDDObjsWithTrace() {
        System.out.println("Creating DerSubDD_Req Objects");

        // get the header line for all Objective-A2-5-query-count-all-Derived-SubDD-Reqs csv file
        String[] derSubddCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-5-query-count-all-Derived-SubDD-Reqs", rackDir));

        int derSubddIdCol =
                CustomStringUtils.getCSVColumnIndex(derSubddCols, "identifier_dersubdd_req");
        int derSubddDescCol =
                CustomStringUtils.getCSVColumnIndex(derSubddCols, "description_dersubdd_req");

        // get the header line for all
        // Objective-A2-2-query-count-all-Derived-SRS-Reqs-that-satisfy-some-req csv file
        String[] derSrsToAnyReqCols =
                CSVUtil.getColumnInfo(
                        RackQueryUtils.createCsvFilePath(
                                "Objective-A2-5-query-count-all-Derived-SubDD-Reqs-that-satisfy-some-req",
                                rackDir));

        int derSubddIdCol2 =
                CustomStringUtils.getCSVColumnIndex(derSrsToAnyReqCols, "identifier_dersubdd_req");
        int satisfiesIdCol =
                CustomStringUtils.getCSVColumnIndex(derSrsToAnyReqCols, "identifier_satisfies");

        for (String[] row : allDerSUBDD) {
            if (row[derSubddIdCol] != null) {
                Requirement newDerSUBDDObj = new Requirement();

                newDerSUBDDObj.setId(row[derSubddIdCol]);
                newDerSUBDDObj.setDescription(row[derSubddDescCol]);
                newDerSUBDDObj.setType("DerSubDD_Req");

                // find and add trace (satisfies) info
                List<Requirement> satisfies = new ArrayList<Requirement>();

                // -- find any trace

                for (String[] row2 : derSUBDDTrace) {
                    if ((row2.length > derSubddIdCol2) && (row2.length > satisfiesIdCol)) {

                        if (row2[derSubddIdCol2].equals(row[derSubddIdCol])) {
                            // create a generic requirement with id and add to satisfies
                            Requirement genericReq = new Requirement();
                            genericReq.setId(row2[satisfiesIdCol]);
                            satisfies.add(genericReq);
                        }
                    }
                }

                // add satisfies to derSrs OBJ
                newDerSUBDDObj.setSatisfies(satisfies);

                // find source if exists
                newDerSUBDDObj.setSourceDocument(findSource(newDerSUBDDObj.getId()));

                // add the SRS object to list
                allDerSUBDDObjs.add(newDerSUBDDObj);
            }
        }
    }

    /** Creates all data objects from csv files */
    private void createAllDataObjects() {
        System.out.println("Creating data objects from RACK data ...\n");
        createAllDataItemObjs();
        System.out.println("Num Documents Objects : " + allDataItemObjs.size());
        createAllSBVTTestObjs();
        System.out.println("Num SBVT Test Objects : " + allSBVTObjs.size());
        createAllRevLogObjs();
        System.out.println("Num Review Log Objects : " + allRevLogObjs.size());
        createAllCSIDObjs();
        System.out.println("Num CSID Objects : " + allCSIDObjs.size());
        createAllPIDSObjs();
        System.out.println("Num PIDS Objects : " + allPIDSObjs.size());
        createAllSRSObjsWithTrace();
        System.out.println("Num SRS Objects : " + allSRSObjs.size());
        createAllDerSRSObjsWithTrace();
        System.out.println("Num DerSRS Objects : " + allDerSRSObjs.size());
        createAllSUBDDObjsWithTrace();
        System.out.println("Num SUBDD Objects : " + allSUBDDObjs.size());
        createAllDerSUBDDObjsWithTrace();
        System.out.println("Num DerSUBDD Objects : " + allDerSUBDDObjs.size());
        createAllSWCOMPObjsWithTrace();
        System.out.println("Num SWCOMP Objects : " + allSwCompObjs.size());
        System.out.println("Data objects created from RACK data.\n");
    }

    /**
     * Sets up Rack settings and queries RACK to get data for the psac demo
     *
     * <p>TODO: Move to the data processor that calls it instead of keeping in this generic class
     *
     * @throws Exception
     */
    private void queryRackForDO178CPsac() {

        try {
            //            // ***************** DO NOT DELETE ***************** TURNED OFF FOR
            // TESTING
            //            // Connect to RACK using RACK preferences
            //            SparqlConnectionInfo newConnPars =
            // RackQueryUtils.initiateQueryConnection();

            // FOR TESTING ONLY : Connect to RACK using hardcoded preferences
            SparqlConnectionInfo newConnPars = RackQueryUtils.hardcodedQueryConnectionForTesting();

            // Execute each predefined query
            List<String> allQueryIds = DO178CQueries.All.getAllQueries();
            RackQueryUtils.executeMultiQueriesFromStore(allQueryIds, rackDir, newConnPars);
        } catch (Exception e) {
            System.out.println("ERROR: Was unable to successfuly query RACK!!\n");
            e.printStackTrace();
        }
    }

    /**
     * fetch RACk data and store in class variables
     *
     * @param rackDir
     */
    private void fetchData() {

        // Query RACK
        queryRackForDO178CPsac();

        psacToActivityData =
                RackQueryUtils.readCSVFile2(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.GET_PSAC.getQId(), rackDir));

        allDocs =
                RackQueryUtils.readCSVFile2(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A1_1_DOCS.getQId(), rackDir));

        /**
         * Notes:
         *
         * <p>TODO: Create an alternative that preserves integrity but is also fast
         */
        allSRS =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_1_SRS.getQId(), rackDir));
        allCSID =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_1_CSID.getQId(), rackDir));

        allPIDS =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_1_PIDS.getQId(), rackDir));

        srsToPIDSorCSID =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_1_SRS_TO_PIDS_CSID.getQId(), rackDir));

        allDerSRS =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_2_DERSRS.getQId(), rackDir));

        derSRSTrace =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_2_DERSRS_TRACE.getQId(), rackDir));

        allSUBDD =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_4_SUBDD.getQId(), rackDir));

        subddToSRS =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_4_SUBDD_TO_SRS.getQId(), rackDir));

        allDerSUBDD =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_5_DERSUBDD.getQId(), rackDir));

        derSUBDDTrace =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A2_5_DERSUBDD_TRACE.getQId(), rackDir));

        allSBVT =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A7_3_4_SBVT_TEST.getQId(), rackDir));

        allREVLOGS =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.ALL_REVIEW_LOGS.getQId(), rackDir));

        allSWCOMPS =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.A5_5_SWCOMP_REQ_TRACE.getQId(), rackDir));

        allSOURCETRACE =
                CSVUtil.getRows(
                        RackQueryUtils.createCsvFilePath(
                                DO178CQueries.All.ALL_SOURCES.getQId(), rackDir));
    }

    /**
     * Interface function
     *
     * @param rackDir
     * @return
     */
    @Override
    public AnalysisNode getData(String outDir) {

        rackDir = outDir;

        // fetch the necessary data from RACK
        fetchData();

        // Create all the objects
        createAllDataObjects();

        // Get the PSAC data from RACK
        PsacNode psacNode = getPsacFromRack();

        // PsacNodeUtils.printPsacNode(psacNode);

        // Attach the relevant outputs to the PSAC objectives for analysis
        PsacNode psacNodeWithOutputs = attachOutputsToObjectives(psacNode);

        // get PSAC compliance status
        PsacNode psacComplianceNode = computePsacCompliance(psacNodeWithOutputs);

        // remove non-relevant objectives based on software level and return
        return LogicUtils.filterNonRelevantObjectives(psacComplianceNode);
    }
}
