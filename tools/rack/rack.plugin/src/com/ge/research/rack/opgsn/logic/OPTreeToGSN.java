/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2024, General Electric Company and Galois, Inc.
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
package com.ge.research.rack.opgsn.logic;

import com.ge.research.rack.autoGsn.constants.GsnCoreElements;
import com.ge.research.rack.autoGsn.structures.GsnNode;
import com.ge.research.rack.autoGsn.structures.GsnNode.Goal;
import com.ge.research.rack.autoGsn.structures.GsnNode.Solution;
import com.ge.research.rack.autoGsn.structures.GsnNode.Strategy;
import com.ge.research.rack.opgsn.structures.OPTree;
import java.util.ArrayList;
import java.util.List;

public class OPTreeToGSN {

    /*
     * Rough Algorithm to create GsnNode from OPTree
     *
     * 1. Find the root argument of the Tree and keep going down the tree
     * 2. for each argument, create a goal
     * 3. For each premise create a goal
     * 4. When a goal is identified, create a goal node and call function to create strategy. The  attach strategy that is returned
     * 5. For each Strategy, create a strategy node and call function for each possible subgoal. Attach the subgoals that are returned
     *
     * will need to create three types of goal creation functions: one for OPTree, one for argument goals, one for premise goals
     * Will need to create two types of strategy creation functions: one where children are premises (goals) and the where children are evidence (solution)
     *
     */

    private GsnNode createSolutionNode(OPTree.Evidence evidenceObj, int level) {
        // create a GsnNode.Solution

        GsnNode.Solution sol = new GsnNode().new Solution();

        sol.setId("SOL-" + evidenceObj.getId().replaceAll("\\s", ""));

        // -- Cobine all details
        String evidenceDetail = "";
        for (String d : evidenceObj.getDetail()) {
            evidenceDetail = evidenceDetail + d + "\n";
        }
        sol.setSolTest(
                evidenceDetail); // This stores the details of the evidence generation activity

        sol.setSupportive(evidenceObj.getIsSupportive());

        // create a solution GSNNode
        GsnNode solGsnLeaf = new GsnNode();

        solGsnLeaf.setNodeLevel(level);

        solGsnLeaf.setNodeId(sol.getId());

        solGsnLeaf.setNodeType(GsnCoreElements.Class.Solution);

        solGsnLeaf.setSolution(sol);

        solGsnLeaf.setDescription(evidenceDetail);
        // set color of solution GsnNode
        if (sol.getSupportive()) {
            solGsnLeaf.setIsGreen(true);
        } else {
            solGsnLeaf.setIsGreen(false);
        }

        return solGsnLeaf;
    }

    private GsnNode createStrategyForPremiseGoalNode(
            String parentId, OPTree.Premise premiseObj, int level) {
        // Step 1: Create GsnNode.Strategy
        GsnNode.Strategy strategy = new GsnNode().new Strategy();

        strategy.setId("S-" + parentId.replaceAll("\\s", ""));

        //        strategy.setHzrdOrReqId(stratPat.getGoalClass());

        //        strategy.setType(RackCoreElements.RackClass.THING);

        //        strategy.setTypeInOverlay(stratPat.getGoalClass());

        if (premiseObj.getSubPremises().size() > 0) { // if not a leaf premise
            strategy.setDescription("Argument: By Checking that all Sub-Premises Hold");
        } else { // if a leaf premise
            // check the type of the first (and only) evidence and decide strategy
            if (premiseObj.getEvidence().get(0).getConfirmedBy().toLowerCase().contains("cameo")) {
                strategy.setDescription("Argument: By Cameo Review");
            } else if (premiseObj
                    .getEvidence()
                    .get(0)
                    .getConfirmedBy()
                    .toLowerCase()
                    .contains("matlab")) {
                strategy.setDescription("Argument: By Matlab Analysis");
            } else if (premiseObj
                    .getEvidence()
                    .get(0)
                    .getConfirmedBy()
                    .toLowerCase()
                    .contains("simulink")) {
                strategy.setDescription("Argument: By Simulink Analysis");
            } else if (premiseObj
                    .getEvidence()
                    .get(0)
                    .getConfirmedBy()
                    .toLowerCase()
                    .contains("python")) {
                strategy.setDescription("Argument: By Python Analysis");
            } else if (premiseObj
                    .getEvidence()
                    .get(0)
                    .getConfirmedBy()
                    .toLowerCase()
                    .contains("marabou")) {
                strategy.setDescription("Argument: By Marabou Analysis");
            } else {
                strategy.setDescription("Argument: By Unknown Approach");
            }
        }

        // Step 2: Create GsnNode for GSN tree and attach strategy node
        GsnNode stratGsnTree = new GsnNode();

        stratGsnTree.setNodeLevel(level);

        stratGsnTree.setNodeId(strategy.getId());

        stratGsnTree.setNodeType(GsnCoreElements.Class.Strategy);

        stratGsnTree.setStrategy(strategy);

        stratGsnTree.setDescription(strategy.getDescription());

        // Step 3: for each argument, create a goal tree
        // get all the subgoal instances and create Goal GsnNodes for them and attach as children
        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();

        if (premiseObj.getSubPremises().size() > 0) { // if not leaf premise
            for (OPTree.Premise premise : premiseObj.getSubPremises()) {
                GsnNode subGoalNode = createPremiseGoalNode(premise, level + 1);
                allSupportedByNodes.add(subGoalNode);
            }
        } else { // if leaf premise, create a solution node and attach
            GsnNode solutionNode = createSolutionNode(premiseObj.getEvidence().get(0), level + 1);
            allSupportedByNodes.add(solutionNode);
        }

        // Step 4: attach all returned goal trees as supporting
        stratGsnTree.setSupportedBy(allSupportedByNodes);

        // Step 5: Compute color
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

    private GsnNode createPremiseGoalNode(OPTree.Premise premiseObj, int level) {
        // Step 1: Create GsnNode.Goal
        GsnNode.Goal goal = new GsnNode().new Goal();

        goal.setId("G-" + premiseObj.getId().replaceAll("\\s", ""));

        goal.setElementId(premiseObj.getId());

        goal.setElementDescription(premiseObj.getStatement());

        goal.setDescription(premiseObj.getStatement());
        // Step 2: Create GsnNode for goal tree
        GsnNode goalGsnTree = new GsnNode();

        goalGsnTree.setNodeLevel(level);

        goalGsnTree.setNodeId(goal.getId());

        goalGsnTree.setNodeType(GsnCoreElements.Class.Goal);

        goalGsnTree.setGoal(goal);

        goalGsnTree.setDescription(goal.getDescription());

        // Step 3: Create strategyNode
        GsnNode strategyNode =
                createStrategyForPremiseGoalNode(premiseObj.getId(), premiseObj, level + 1);

        // Step 4: Attach strategynode as supporting
        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();
        allSupportedByNodes.add(strategyNode);
        goalGsnTree.setSupportedBy(allSupportedByNodes);

        // Step 5: Assign Color and developed
        // Set node.color and goal.developed
        goalGsnTree.setIsGreen(strategyNode.getIsGreen());
        goalGsnTree.getGoal().setDeveloped(strategyNode.getIsGreen());

        return goalGsnTree;
    }

    private GsnNode createStrategyForArgumentGoalNode(
            String parentId, List<OPTree.Premise> premiseObjs, int level) {
        // Step 1: Create GsnNode.Strategy
        GsnNode.Strategy strategy = new GsnNode().new Strategy();

        strategy.setId("S-" + parentId.replaceAll("\\s", ""));

        //        strategy.setHzrdOrReqId(stratPat.getGoalClass());

        //        strategy.setType(RackCoreElements.RackClass.THING);

        //        strategy.setTypeInOverlay(stratPat.getGoalClass());

        strategy.setDescription("Argument: By Checking that all Premises Hold");

        // Step 2: Create GsnNode for GSN tree and attach strategy node
        GsnNode stratGsnTree = new GsnNode();

        stratGsnTree.setNodeLevel(level);

        stratGsnTree.setNodeId(strategy.getId());

        stratGsnTree.setNodeType(GsnCoreElements.Class.Strategy);

        stratGsnTree.setStrategy(strategy);

        stratGsnTree.setDescription(strategy.getDescription());

        // Step 3: for each argument, create a goal tree
        // get all the subgoal instances and create Goal GsnNodes for them and attach as children
        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();

        for (OPTree.Premise premise : premiseObjs) {
            GsnNode subGoalNode = createPremiseGoalNode(premise, level + 1);
            allSupportedByNodes.add(subGoalNode);
        }

        // Step 4: attach all returned goal trees as supporting
        stratGsnTree.setSupportedBy(allSupportedByNodes);

        // Step 5: Compute color
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

    private GsnNode createArgumentGoalNode(OPTree.Argument argumentObj, int level) {

        // Step 1: Create GsnNode.Goal
        GsnNode.Goal goal = new GsnNode().new Goal();

        goal.setId("G-" + argumentObj.getId().replaceAll("\\s", ""));

        goal.setElementId(argumentObj.getId());

        goal.setElementDescription(argumentObj.getConclusion());

        goal.setDescription(argumentObj.getConclusion());
        // Step 2: Create GsnNode for goal tree
        GsnNode goalGsnTree = new GsnNode();

        goalGsnTree.setNodeLevel(level);

        goalGsnTree.setNodeId(goal.getId());

        goalGsnTree.setNodeType(GsnCoreElements.Class.Goal);

        goalGsnTree.setGoal(goal);

        goalGsnTree.setDescription(goal.getDescription());

        // Step 3: Create strategyNode
        GsnNode strategyNode =
                createStrategyForArgumentGoalNode(
                        argumentObj.getId(), argumentObj.getSubPremises(), level + 1);

        // Step 4: Attach strategynode as supporting
        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();
        allSupportedByNodes.add(strategyNode);
        goalGsnTree.setSupportedBy(allSupportedByNodes);

        // Step 5: Assign Color and developed
        // Set node.color and goal.developed
        goalGsnTree.setIsGreen(strategyNode.getIsGreen());
        goalGsnTree.getGoal().setDeveloped(strategyNode.getIsGreen());

        return goalGsnTree;
    }

    private GsnNode createStrategyForOPTreeGoalNode(
            String parentId, List<OPTree.Argument> argumentObjs, int level) {

        // Step 1: Create GsnNode.Strategy
        GsnNode.Strategy strategy = new GsnNode().new Strategy();

        strategy.setId("S-" + parentId.replaceAll("\\s", ""));

        //        strategy.setHzrdOrReqId(stratPat.getGoalClass());

        //        strategy.setType(RackCoreElements.RackClass.THING);

        //        strategy.setTypeInOverlay(stratPat.getGoalClass());

        strategy.setDescription("Argument: By Proving Intent, Correctness, and Innocuity.");

        // Step 2: Create GsnNode for GSN tree and attach strategy node
        GsnNode stratGsnTree = new GsnNode();

        stratGsnTree.setNodeLevel(level);

        stratGsnTree.setNodeId(strategy.getId());

        stratGsnTree.setNodeType(GsnCoreElements.Class.Strategy);

        stratGsnTree.setStrategy(strategy);

        stratGsnTree.setDescription(strategy.getDescription());

        // Step 3: for each argument, create a goal tree
        // get all the subgoal instances and create Goal GsnNodes for them and attach as children
        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();

        for (OPTree.Argument argument : argumentObjs) {
            GsnNode subGoalNode = createArgumentGoalNode(argument, level + 1);
            allSupportedByNodes.add(subGoalNode);
        }

        // Step 4: attach all returned goal trees as supporting
        stratGsnTree.setSupportedBy(allSupportedByNodes);

        // Step 5: Compute color
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

    public GsnNode createOPTreeGoalNode(OPTree opTreeObj, int level) {

        // -- create a GsnNode.Goal
        GsnNode.Goal goal = new GsnNode().new Goal();

        goal.setId("G-" + opTreeObj.getId().replaceAll("\\s", ""));

        goal.setElementId(opTreeObj.getId());

        goal.setElementDescription(opTreeObj.getStatement());

        //        goal.setType(RackCoreElements.RackClass.THING);

        //        goal.setTypeInOverlay(stratPat.getGoalClass());

        goal.setDescription(opTreeObj.getStatement());

        // create GsnNode and set as goal
        GsnNode goalGsnTree = new GsnNode();

        goalGsnTree.setNodeLevel(level);

        goalGsnTree.setNodeId(goal.getId());

        goalGsnTree.setNodeType(GsnCoreElements.Class.Goal);

        goalGsnTree.setGoal(goal);

        goalGsnTree.setDescription(goal.getDescription());

        //        // create the appropriate context and add as an incontex of node
        //        List<GsnNode> contextNodes = new ArrayList<GsnNode>();
        //        GsnNode contextNode = createContextGsnNode(goal, level + 1);
        //        contextNodes.add(contextNode);
        //        goalGsnTree.setInContextOf(contextNodes);

        List<GsnNode> allSupportedByNodes = new ArrayList<GsnNode>();
        // create the appropriate strategy and add as a supporting node

        GsnNode strategyNode =
                createStrategyForOPTreeGoalNode(
                        opTreeObj.getId(), opTreeObj.getArguments(), level + 1);
        allSupportedByNodes.add(strategyNode);

        // add the list of supported bys
        goalGsnTree.setSupportedBy(allSupportedByNodes);

        // Set node.color and goal.developed
        goalGsnTree.setIsGreen(strategyNode.getIsGreen());
        goalGsnTree.getGoal().setDeveloped(strategyNode.getIsGreen());

        return goalGsnTree;
    }
}
