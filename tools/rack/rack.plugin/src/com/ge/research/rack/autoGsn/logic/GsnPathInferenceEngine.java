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
package com.ge.research.rack.autoGsn.logic;

import com.ge.research.rack.autoGsn.structures.PatternInfo;
import com.ge.research.rack.autoGsn.structures.PatternInfo.*;
import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.autoGsn.utils.OntologyJsonObjUtils;
import com.ge.research.rack.autoGsn.utils.QueryGenerationUtils;
import com.ge.research.rack.report.structures.SparqlConnectionInfo;
import com.ge.research.rack.report.utils.RackQueryUtils;
import com.ge.research.semtk.edc.client.OntologyInfoClient;
import com.ge.research.semtk.edc.client.OntologyInfoClientConfig;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>This class has the following functionalities: 1. Execute a set of existing queries on RACK
 *     to get the pattern data 2. Infer path information from he pattern data 3. Generate necessary
 *     queries to get data for the paths, upload them to RACK store, and execute them 4. Pack the
 *     path info, and the list of generated queries in a suitable structure and return for use by a
 *     data processor
 */
public class GsnPathInferenceEngine {

    // -- Class variables

    // to store the pattern path and query information for returning
    private PatternInfo patInfo = new PatternInfo();

    // Rack and outDir info
    private SparqlConnectionInfo connPars;
    private String outDir;
    private String serviceProtocol;
    private String serviceServer;
    private Integer servicePort;
    private JSONObject ontInfo;

    /**
     * @return the patInfo
     */
    public PatternInfo getPatInfo() {
        return patInfo;
    }

    /**
     * @param patInfo the patInfo to set
     */
    public void setPatInfo(PatternInfo patInfo) {
        this.patInfo = patInfo;
    }

    /**
     * Executes the prefixed queries to get pattern data
     *
     * <p>TODO: Figure out a way to bundle prefixed queries in jar
     */
    private void executePrefixePatternQuries() {
        System.out.println("Executing pre-fixed pattern queries already uploaded in RACK store");

        // create the patern queries
        String goalPatternQueryPath =
                QueryGenerationUtils.patternQuery("gsn_get_goal_patterns", outDir, connPars);
        String strategyPatternQueryPath =
                QueryGenerationUtils.patternQuery("gsn_get_strategy_patterns", outDir, connPars);
        String evidencePatternQueryPath =
                QueryGenerationUtils.patternQuery("gsn_get_evidence_patterns", outDir, connPars);

        // load the queries to store
        RackQueryUtils.uploadQueryNodegroupToRackStore(
                "gsn_get_goal_patterns", goalPatternQueryPath);
        RackQueryUtils.uploadQueryNodegroupToRackStore(
                "gsn_get_strategy_patterns", strategyPatternQueryPath);
        RackQueryUtils.uploadQueryNodegroupToRackStore(
                "gsn_get_evidence_patterns", evidencePatternQueryPath);

        // execute the queries from store
        RackQueryUtils.executeSingleQueryFromStore("gsn_get_goal_patterns", outDir, connPars);
        RackQueryUtils.executeSingleQueryFromStore("gsn_get_strategy_patterns", outDir, connPars);
        RackQueryUtils.executeSingleQueryFromStore("gsn_get_evidence_patterns", outDir, connPars);
    }

    /** Gets the patterninfo from the raw data and stores as objects */
    private void getPatternInfo() {

        System.out.println("\n--Creating goal pattern objects");
        // -- GOAL PATTERNS
        // get the header line for pattern
        String[] goalPatCols =
                RackQueryUtils.readCSVHeader(
                        RackQueryUtils.createCsvFilePath("gsn_get_goal_patterns", outDir));
        // get the raw data fir patterns
        List<String[]> goalPatData =
                RackQueryUtils.readCSVFile2(
                        RackQueryUtils.createCsvFilePath("gsn_get_goal_patterns", outDir));
        int goalIdCol = CustomStringUtils.getCSVColumnIndex(goalPatCols, "identifier");
        int goalDescCol = CustomStringUtils.getCSVColumnIndex(goalPatCols, "description");
        int goalClassCol = CustomStringUtils.getCSVColumnIndex(goalPatCols, "pGoal");
        int goalIsPatCol = CustomStringUtils.getCSVColumnIndex(goalPatCols, "Pat");

        for (String[] row : goalPatData) {
            if (row[goalIsPatCol].equalsIgnoreCase("true")) { // if a pattern
                String goalId = row[goalIdCol];
                String goalDesc = row[goalDescCol];
                String goalClass = row[goalClassCol];
                System.out.println(goalId + ", " + goalDesc + ", " + goalClass);
                // create a new pattern and add to list
                PatternInfo.GoalPat goalPatObj =
                        new PatternInfo().new GoalPat(goalId, goalDesc, goalClass);
                patInfo.getGoalPats().add(goalPatObj);
            }
        }

        System.out.println("\n--Creating strategy pattern objects");
        // -- STRATEGY PATTERNS
        // get the header line for pattern
        String[] stratPatCols =
                RackQueryUtils.readCSVHeader(
                        RackQueryUtils.createCsvFilePath("gsn_get_strategy_patterns", outDir));
        // get the raw data fir patterns
        List<String[]> stratPatData =
                RackQueryUtils.readCSVFile2(
                        RackQueryUtils.createCsvFilePath("gsn_get_strategy_patterns", outDir));
        int stratIdCol = CustomStringUtils.getCSVColumnIndex(stratPatCols, "identifier");
        int stratDescCol = CustomStringUtils.getCSVColumnIndex(stratPatCols, "description");
        int stratGoalClassCol = CustomStringUtils.getCSVColumnIndex(stratPatCols, "pGoal");
        int stratSubGoalClassCol = CustomStringUtils.getCSVColumnIndex(stratPatCols, "pSubGoal");
        int stratPropertyCol =
                CustomStringUtils.getCSVColumnIndex(stratPatCols, "pGoalSubGoalConnector");
        int stratIsPatCol = CustomStringUtils.getCSVColumnIndex(stratPatCols, "Pat");

        for (String[] row : stratPatData) {
            if (row[stratIsPatCol].equalsIgnoreCase("true")) { // if a pattern
                String stratId = row[stratIdCol];
                String stratDesc = row[stratDescCol];
                String stratGoalClass = row[stratGoalClassCol];
                String stratSubGoalClass = row[stratSubGoalClassCol];
                String stratProperty = row[stratPropertyCol];
                System.out.println(
                        stratId
                                + ", "
                                + stratDesc
                                + ", "
                                + stratGoalClass
                                + ", "
                                + stratSubGoalClass
                                + ", "
                                + stratProperty);
                // create a new pattern and add to list
                PatternInfo.StratPat stratPatObj =
                        new PatternInfo()
                        .new StratPat(
                                stratId,
                                stratDesc,
                                stratGoalClass,
                                stratSubGoalClass,
                                stratProperty);
                // check direction of property
                if (OntologyJsonObjUtils.isForwardProperty(
                                ontInfo, stratGoalClass, stratProperty, stratSubGoalClass)
                        == null) {
                    System.out.println(
                            "ERROR: Could not find the connection "
                                    + stratGoalClass
                                    + " <"
                                    + stratProperty
                                    + "> "
                                    + stratSubGoalClass
                                    + " in the ontology information pulled from RACK!");
                } else {
                    if (OntologyJsonObjUtils.isForwardProperty(
                            ontInfo, stratGoalClass, stratProperty, stratSubGoalClass)) {
                        stratPatObj.setPropIsForward(true);
                    } else {
                        stratPatObj.setPropIsForward(false);
                    }
                }
                patInfo.getStratPats().add(stratPatObj);
            }
        }

        System.out.println("\n--Creating evidence pattern objects");
        // -- EVIDENCE PATTERNS
        // get the header line for pattern
        String[] evdPatCols =
                RackQueryUtils.readCSVHeader(
                        RackQueryUtils.createCsvFilePath("gsn_get_evidence_patterns", outDir));
        // get the raw data fir patterns
        List<String[]> evdPatData =
                RackQueryUtils.readCSVFile2(
                        RackQueryUtils.createCsvFilePath("gsn_get_evidence_patterns", outDir));
        int evdClassIdCol = CustomStringUtils.getCSVColumnIndex(evdPatCols, "classId");
        int evdPassValCol = CustomStringUtils.getCSVColumnIndex(evdPatCols, "passValue");
        int evdStatusPropertyCol =
                CustomStringUtils.getCSVColumnIndex(evdPatCols, "statusProperty");

        for (String[] row : evdPatData) {
            if (true) { // if a pattern
                String evdClassId = row[evdClassIdCol];
                String evdPassVal = row[evdPassValCol];
                String evdStatusProperty = row[evdStatusPropertyCol];

                System.out.println(evdClassId + ", " + evdPassVal + ", " + evdStatusProperty);
                // create a new pattern and add to list
                PatternInfo.EvidencePat evdPatObj =
                        new PatternInfo()
                        .new EvidencePat(evdClassId, evdStatusProperty, evdPassVal);
                patInfo.getEvdPats().add(evdPatObj);
            }
        }
    }

    /**
     * Creates paths starting from a rootnode as a tree (There can be multiple paths from a rootnode
     * --> different evidence nodes)
     *
     * <p>TODO: need to connect to evidence?
     *
     * @param strat
     * @param myLevel
     * @return
     */
    public PathTree createPaths(StratPat strat, int myLevel) {

        PathTree crntPath = new PatternInfo().new PathTree();
        crntPath.setCurrentNode(strat);
        crntPath.setCurrentLevel(myLevel);

        System.out.println(strat.getGoalClass() + "-->" + strat.getSubGoalClass());

        // find all stratPats X such that the goalClass of X is the subGoalClass of prevStratPat
        List<StratPat> childStratPats = new ArrayList<StratPat>();
        for (StratPat strtPt : patInfo.getStratPats()) {
            if (strtPt.getGoalClass().equalsIgnoreCase(strat.getSubGoalClass())) {
                childStratPats.add(strtPt);
            }
        }

        if (childStratPats.size() > 0) {
            for (StratPat childStratPat : childStratPats) {
                crntPath.getSubPaths().add(createPaths(childStratPat, myLevel + 1));
            }
        } else {
            //			return  crntPath;
        }

        return crntPath;
    }

    /** Infers the pathinfo from the pattern data and stores them in the class variable */
    private void inferPathInfo() {

        // Find the list of strategy objects that are concerned with a rootgoal
        List<PatternInfo.StratPat> rootGoalStrats = new ArrayList<PatternInfo.StratPat>();
        for (PatternInfo.StratPat stratPat : patInfo.getStratPats()) {
            boolean hasParent = false;
            for (PatternInfo.StratPat checkStratPat : patInfo.getStratPats()) {
                if (checkStratPat.getSubGoalClass().equalsIgnoreCase(stratPat.getGoalClass())) {
                    hasParent = true;
                    break;
                }
            }
            if (!hasParent) {
                System.out.println(stratPat.getGoalClass() + " is a root goal class");
                rootGoalStrats.add(stratPat);
            }
        }

        /**
         * Now, for each root goal strategy object, create a path object and connect to sub-paths
         * When a path is complete, add it to the list of patInfo paths
         *
         * <p>How:
         *
         * <p>For each rootgoal strategy object, send to function createPath
         */
        for (StratPat stratPat : rootGoalStrats) {
            PathTree rootPath = createPaths(stratPat, 0);
            patInfo.getPathTrees().add(rootPath);
            System.out.println("NEW PATH TREE:");
            rootPath.printPath(rootPath);
        }
    }

    /**
     * For a given path, constructs query json files for each consecutive node pair in the path tree
     * and executes them
     *
     * @param path
     * @param jsonObj
     */
    private void createPathQueries(PathTree path, JSONObject jsonObj) {

        String currentNodeQueryJsonFilePath;

        System.out.println(
                "Generating queries for "
                        + path.getCurrentNode().getGoalClass()
                        + " --- "
                        + path.getCurrentNode().getSubGoalClass());

        // create query for currentNode
        if (path.getCurrentNode().getPropIsForward()) {
            currentNodeQueryJsonFilePath =
                    QueryGenerationUtils.binaryConnectionQueryWithDesc(
                            jsonObj,
                            path.getCurrentNode().getGoalClass(),
                            path.getCurrentNode().getProperty(),
                            path.getCurrentNode().getSubGoalClass(),
                            outDir,
                            connPars);
        } else {
            currentNodeQueryJsonFilePath =
                    QueryGenerationUtils.binaryConnectionQueryWithDesc(
                            jsonObj,
                            path.getCurrentNode().getSubGoalClass(),
                            path.getCurrentNode().getProperty(),
                            path.getCurrentNode().getGoalClass(),
                            outDir,
                            connPars);
        }

        // add the new query Id to the list of query ids in patInfo
        patInfo.getQueryJsonFilePaths().add(currentNodeQueryJsonFilePath);

        // send to each next level if exists
        if (path.getSubPaths().size() > 0) {
            for (PathTree subPath : path.getSubPaths()) {
                createPathQueries(subPath, jsonObj);
            }
        }
    }

    /**
     * Creates queries required to get path data for the patterns and stores them in the class
     * variable
     */
    private void createPatternQueries() {
        for (PathTree path : patInfo.getPathTrees()) {
            createPathQueries(path, ontInfo);
        }
    }

    /** Executes the set of queries in patInfo that were generated from the pattern */
    private void loadAndExecuteGeneratedPatternPathQueries() {

        for (String qJsonPath : patInfo.getQueryJsonFilePaths()) {
            System.out.println("Uploading and Executing Query Nodegroup:" + qJsonPath);

            String qId = RackQueryUtils.getQueryIdFromFilePath(qJsonPath);

            // upload nodegroup to store
            RackQueryUtils.uploadQueryNodegroupToRackStore(qId, qJsonPath);

            // execute the query from store
            RackQueryUtils.executeSingleQueryFromStore(qId, outDir, connPars);
        }
    }

    /**
     * Interface function for the class to perform all necessary actions in sequence and return the
     * results to caller
     *
     * @param connParsInp
     * @param outDirInp
     * @param serviceProtocolInp
     * @param serviceServerInp
     * @param servicePortInp
     * @return
     */
    public PatternInfo fetchPatternInfo(
            SparqlConnectionInfo connParsInp,
            String outDirInp,
            String serviceProtocolInp,
            String serviceServerInp,
            Integer servicePortInp) {
        // assign the variables
        connPars = connParsInp;
        outDir = outDirInp;
        serviceProtocol = serviceProtocolInp;
        serviceServer = serviceServerInp;
        servicePort = servicePortInp;

        try {
            // get ontology info
            OntologyInfoClient newClient =
                    new OntologyInfoClient(
                            new OntologyInfoClientConfig(
                                    serviceProtocol, serviceServer, servicePort));

            ontInfo = newClient.execGetOntologyInfoJson(connPars.getOverrideVar());

            // set ontInfo in patInfo
            patInfo.setOntInfo(ontInfo);

        } catch (Exception e) {
            System.out.println("ERROR: Could not fetch ontology information via SemTk API!");
            e.printStackTrace();
        }

        // query rack
        executePrefixePatternQuries();
        // get data
        getPatternInfo();
        // infer path info
        inferPathInfo();
        // create the queries
        createPatternQueries();
        // load and execute the queries
        loadAndExecuteGeneratedPatternPathQueries();

        return patInfo;
    }
}
