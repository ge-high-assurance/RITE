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

import com.ge.research.rack.autoGsn.constants.GsnCoreElements;
import com.ge.research.rack.autoGsn.constants.RackCoreElements;
import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.structures.InstanceData;
import com.ge.research.rack.autoGsn.structures.PatternInfo;
import com.ge.research.rack.autoGsn.utils.ListStratPatUtils;
import com.ge.research.rack.autoGsn.utils.QueryResultUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>This class processes the data fetched by path-specific queries synthesized and executed by
 *     GsnPathInferenceEngine
 *     <p>Specific functionalities: 1. Call the GsnPathInferenceEngine to generate Path object and
 *     queries and execute the queries 2. Read the data fetched by the queries 3. Create GSN tree
 *     instances from the path tree patterns using the data
 *     <p>Limitations: 1. There can by only one linear path from any class
 */
public class DataProcessor {

    // pattern info
    private PatternInfo patternInfo;

    // To store all individual root to leaf linear paths as a list of StratPats
    private List<List<PatternInfo.StratPat>> rootToLeafPaths =
            new ArrayList<List<PatternInfo.StratPat>>();

    // all GSN nodes
    private List<GsnNode> allGsnNodes = new ArrayList<GsnNode>();

    // outDir
    private String outDir;

    // stores all instance data information required for the GSNs
    private List<InstanceData> instanceDataDependencies = new ArrayList<InstanceData>();

    /**
     * @return the patternInfo
     */
    public PatternInfo getPatternInfo() {
        return patternInfo;
    }

    /**
     * @return the rootToLeafPaths
     */
    public List<List<PatternInfo.StratPat>> getRootToLeafPaths() {
        return rootToLeafPaths;
    }

    /**
     * @return the allGsnNodes
     */
    public List<GsnNode> getAllGsnNodes() {
        return allGsnNodes;
    }

    /**
     * @return the outDir
     */
    public String getOutDir() {
        return outDir;
    }

    /**
     * @return the instanceDataDependencies
     */
    public List<InstanceData> getInstanceDataDependencies() {
        return instanceDataDependencies;
    }

    /**
     * @param instanceDataDependencies the instanceDataDependencies to set
     */
    public void setInstanceDataDependencies(List<InstanceData> instanceDataDependencies) {
        this.instanceDataDependencies = instanceDataDependencies;
    }

    /**
     * Finds and returns appropriate goal pattern for a given class
     *
     * @param goalClass
     * @return
     */
    private String getGoalDescription(String goalClass) {

        for (PatternInfo.GoalPat goalPat : patternInfo.getGoalPats()) {
            if (goalPat.getGoalClass().equalsIgnoreCase(goalClass)) {
                return goalPat.getDesc();
            }
        }
        return "No goal pattern found for this goal class.";
    }

    /**
     * Checks if a given class is an evidence class
     *
     * @param subGoalClass
     * @return
     */
    private Boolean isEvidenceClass(String subGoalClass) {

        for (PatternInfo.EvidencePat evidencePat : patternInfo.getEvdPats()) {
            if (evidencePat.getEvidenceClass().equalsIgnoreCase(subGoalClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if an evidence is supportive
     *
     * @param evidenceClass
     * @param value
     * @return
     */
    public Boolean isSupportive(String evidenceClass, String value) {

        for (PatternInfo.EvidencePat evPat : patternInfo.getEvdPats()) {
            if (evPat.getEvidenceClass().equals(evidenceClass)) {
                if (evPat.getPassVal().equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Creates a context GsnNode
     *
     * @param goal
     * @param level
     * @return
     */
    private GsnNode createContextGsnNode(GsnNode.Goal goal, int level) {
        GsnNode.Context context = new GsnNode().new Context();

        String contextId = "C-" + goal.getElementId().replaceAll("\\s", "");

        context.setId(contextId);
        context.setContext(goal.getElementId().replaceAll("\\s", ""));

        // add the context element to the list of dependencies
        instanceDataDependencies.add(
                new InstanceData(
                        goal.getElementId().replaceAll("\\s", ""),
                        goal.getDescription().replaceAll("^\"|\"$", ""),
                        goal.getTypeInOverlay()));

        GsnNode conGsnLeaf = new GsnNode();

        conGsnLeaf.setNodeLevel(level);

        conGsnLeaf.setNodeId(context.getId());

        conGsnLeaf.setNodeType(GsnCoreElements.Class.Context);

        conGsnLeaf.setContext(context);

        return conGsnLeaf;
    }

    /**
     * Creates a strategy GsnNode
     *
     * @param parentInstanceId
     * @param stratPat
     * @param path
     * @param level
     * @return
     */
    private GsnNode createStrategyGsnNode(
            String parentInstanceId,
            PatternInfo.StratPat stratPat,
            List<PatternInfo.StratPat> path,
            int level) {

        // Create GsnNode.Strategy
        GsnNode.Strategy strategy = new GsnNode().new Strategy();

        strategy.setId("S-" + parentInstanceId.replaceAll("\\s", ""));

        //    	System.out.println(StringUtils.repeatChar(level,'*')+"Creating strategy " +
        // strategy.getId());

        strategy.setHzrdOrReqId(stratPat.getGoalClass());

        strategy.setType(RackCoreElements.RackClass.THING);

        strategy.setTypeInOverlay(stratPat.getGoalClass());

        strategy.setDescription(stratPat.getDesc());

        // create the strategy GsnNode

        GsnNode stratGsnTree = new GsnNode();

        stratGsnTree.setNodeLevel(level);

        stratGsnTree.setNodeId(strategy.getId());

        stratGsnTree.setNodeType(GsnCoreElements.Class.Strategy);

        stratGsnTree.setStrategy(strategy);

        // get all the subgoal instances and create Goal GsnNodes for them and attach as children
        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();

        if (!(isEvidenceClass(stratPat.getSubGoalClass()))) { // if subgoal is not evidence class
            // find all the instances of the subgoal class relevant to the parent instance
            List<String> subGoalClassInstanceIds =
                    QueryResultUtils.getRelevantInstancesOfSubGoalClass(
                            stratPat, parentInstanceId, outDir);

            // create a goal GsnNode for each instance of the subGoal and add to supported by
            for (String subGoalInstanceId : subGoalClassInstanceIds) {
                PatternInfo.StratPat subGoalStratPat = path.get(path.indexOf(stratPat) + 1);
                GsnNode subGoalNode =
                        createGsnNodeForGoalInstance(
                                subGoalInstanceId, subGoalStratPat, path, level + 1);
                allSupportedByNodes.add(subGoalNode);
            }

        } else { // if subgoal is evidence class
            // create a solution GsnNode for the subGoal and add to supported by
            // find all the instances of the subgoal class relevant to the parent instance
            // NOTE: If the data is correct, only ONE instance should be found below
            //       Also, the instanceId will actually be a value instead of id
            List<String> subGoalClassInstanceIds =
                    QueryResultUtils.getRelevantInstancesOfSubGoalClass(
                            stratPat, parentInstanceId, outDir);

            for (String subGoalInstanceId : subGoalClassInstanceIds) {

                // create a GsnNode.Solution

                GsnNode.Solution sol = new GsnNode().new Solution();

                String solId = "SOL-" + parentInstanceId.replaceAll("\\s", "");

                sol.setId(solId);

                sol.setSolTest(parentInstanceId); // TODO: rename the SolTest property to SolValue ?

                sol.setSupportive(isSupportive(stratPat.getSubGoalClass(), subGoalInstanceId));

                // Add the solution test as Instance Data for the Gsn reference dependencies
                instanceDataDependencies.add(
                        new InstanceData(
                                parentInstanceId.replaceAll("\\s", ""),
                                null,
                                stratPat.getSubGoalClass()));

                // create a solution GSNNode
                GsnNode solGsnLeaf = new GsnNode();

                solGsnLeaf.setNodeLevel(level);

                solGsnLeaf.setNodeId(sol.getId());

                solGsnLeaf.setNodeType(GsnCoreElements.Class.Solution);

                solGsnLeaf.setSolution(sol);

                // set color of solution GsnNode
                if (sol.getSupportive()) {
                    solGsnLeaf.setIsGreen(true);
                } else {
                    solGsnLeaf.setIsGreen(false);
                }

                // add to supportive
                allSupportedByNodes.add(solGsnLeaf);
            }
        }

        stratGsnTree.setSupportedBy(allSupportedByNodes);

        // Set node.color and strategy.developed
        Boolean colorFlag = true;
        if (allSupportedByNodes.size() < 1) { // no supporting node
            colorFlag = false;
        } else { // exist supporting nodes
            for (GsnNode supportNode : allSupportedByNodes) {
                if (!supportNode.getIsGreen()) {
                    colorFlag = false;
                }
            }
        }
        stratGsnTree.setIsGreen(colorFlag);
        stratGsnTree.getStrategy().setDeveloped(colorFlag);

        return stratGsnTree;
    }

    /**
     * Creates goal GsnNode
     *
     * @param instanceId
     * @param stratPat
     * @param path
     * @param level
     * @return
     */
    private GsnNode createGsnNodeForGoalInstance(
            String instanceId,
            PatternInfo.StratPat stratPat,
            List<PatternInfo.StratPat> path,
            int level) {

        // create GsnNode.goal
        GsnNode.Goal goal = new GsnNode().new Goal();

        goal.setId("G-" + instanceId.replaceAll("\\s", ""));

        //    	System.out.println(StringUtils.repeatChar(level,'*')+ "Creating goal " +
        // goal.getId());

        goal.setElementId(instanceId);

        goal.setElementDescription(
                QueryResultUtils.getGoalInstanceDesc(instanceId, stratPat, outDir));

        goal.setType(RackCoreElements.RackClass.THING);

        goal.setTypeInOverlay(stratPat.getGoalClass());

        goal.setDescription(getGoalDescription(stratPat.getGoalClass()));

        // create GsnNode and set as goal
        GsnNode goalGsnTree = new GsnNode();

        goalGsnTree.setNodeLevel(level);

        goalGsnTree.setNodeId(goal.getId());

        goalGsnTree.setNodeType(GsnCoreElements.Class.Goal);

        goalGsnTree.setGoal(goal);

        // create the appropriate context and add as an incontex of node
        List<GsnNode> contextNodes = new ArrayList<GsnNode>();
        GsnNode contextNode = createContextGsnNode(goal, level + 1);
        contextNodes.add(contextNode);
        goalGsnTree.setInContextOf(contextNodes);

        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();
        // create the appropriate strategy and add as a supporting node

        GsnNode strategyNode = createStrategyGsnNode(instanceId, stratPat, path, level + 1);
        allSupportedByNodes.add(strategyNode);

        // add the list of supported bys
        goalGsnTree.setSupportedBy(allSupportedByNodes);

        // Set node.color and goal.developed
        goalGsnTree.setIsGreen(strategyNode.getIsGreen());
        goalGsnTree.getGoal().setDeveloped(strategyNode.getIsGreen());

        return goalGsnTree;
    }

    /**
     * Creates a GSNnode for every instance of the stratPat goal class as a rootgoal
     *
     * @param stratPat
     */
    private void createAllGsnNodesForClass(
            PatternInfo.StratPat stratPat, List<PatternInfo.StratPat> path) {

        // get all instances of the goalclass
        List<String> goalClassInstanceIds =
                QueryResultUtils.getAllInstancesOfGoalClass(stratPat, outDir);

        // for each instance of the goal class, create a GSN for that instance and add to list
        for (String id : goalClassInstanceIds) {
            GsnNode instanceGsnNode = createGsnNodeForGoalInstance(id, stratPat, path, 0);
            // add to list
            allGsnNodes.add(instanceGsnNode);
        }
    }

    /**
     * Takes a linear path and creates all possible GSNs and SubGSNs from the data for that path
     *
     * @param path
     * @return
     */
    private void createGsnForGivenPath(List<PatternInfo.StratPat> path) {

        /**
         * Algo:
         *
         * <p>For every class in Path Send to a function that takes a class and creates all possible
         * GSN nodes starting from that class
         */
        System.out.print("Creating all GSN fragments for ");
        ListStratPatUtils.printStratPatListPath(path);
        for (PatternInfo.StratPat goalClass : path) {
            System.out.println(
                    "Finding all GSN Fragments with a rootgoal of type "
                            + goalClass.getGoalClass());
            createAllGsnNodesForClass(goalClass, path);
            System.out.println("All " + goalClass.getGoalClass() + "s completed");
        }
    }

    /**
     * Recursively creates future paths given a pathTree and list of predecessors and stores in the
     * class variable
     *
     * @param pathTree
     * @param predecessorInfo
     */
    private void populateRootToLeafPaths(
            PatternInfo.PathTree pathTree, List<PatternInfo.StratPat> predecessorInfo) {
        // send current predecessor info to lower level
        // if no lower level, add to list of paths

        // add self goal lass to predecessorInfo
        predecessorInfo.add(pathTree.getCurrentNode());

        if (pathTree.getSubPaths().size() > 0) {
            for (PatternInfo.PathTree subPath : pathTree.getSubPaths()) {
                // send to getRootToLeafPaths
                populateRootToLeafPaths(subPath, predecessorInfo);
            }
        } else {
            // add to list of paths
            rootToLeafPaths.add(predecessorInfo);
        }
    }

    /**
     * Main interface function for the class that creates ALL GsnNodes for all possible goals in all
     * possible paths
     *
     * @param patInfo
     */
    public void createAllGsnNodes(PatternInfo patInfo, String outputDir) {

        outDir = outputDir;
        patternInfo = patInfo;

        // get all linear Path info from Path tree
        List<PatternInfo.StratPat> emptyList = new ArrayList<PatternInfo.StratPat>();
        populateRootToLeafPaths(patInfo.getPathTrees().get(0), emptyList);

        // create all GSN for all Paths
        for (List<PatternInfo.StratPat> path : rootToLeafPaths) {
            createGsnForGivenPath(path);
        }
    }

    /**
     * Main interface function for the class that creates the GsnNode for a specific instance of a
     * given class
     *
     * <p>This is because creating all GSNs can be time consuming
     *
     * <p>Note: Path does not need to be specified because currently this approach only allows one
     * linear path from every class
     *
     * @param instanceId
     * @param classId
     * @param patInfo
     * @param outputDir
     */
    public void createSpecificGsnNode(
            String instanceId, String classId, PatternInfo patInfo, String outputDir) {

        outDir = outputDir;
        patternInfo = patInfo;

        System.out.println("Creating GsnNode for " + instanceId);

        // get all linear Path info from Path tree
        List<PatternInfo.StratPat> emptyList = new ArrayList<PatternInfo.StratPat>();
        populateRootToLeafPaths(patInfo.getPathTrees().get(0), emptyList);

        // find the subpath corresponding to the goalclass
        List<PatternInfo.StratPat> classSubPath =
                ListStratPatUtils.getClassSubPath(classId, rootToLeafPaths);

        if (classSubPath == null) {
            System.out.println("ERROR: No path was found for the class provided");
        } else {
            ListStratPatUtils.printStratPatListPath(classSubPath);

            // create the GsnNode for the instance
            GsnNode instanceGsn =
                    createGsnNodeForGoalInstance(instanceId, classSubPath.get(0), classSubPath, 0);
            allGsnNodes.add(instanceGsn);
            System.out.println("Created GsnNode G-" + instanceId);
        }
    }
}
