/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite.autoGsn.utils;

import com.ge.research.rite.autoGsn.constants.GsnCoreElements;
import com.ge.research.rite.autoGsn.structures.GsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GsnNodeUtils {

    /**
     * Print a Gsn as string for testing
     *
     * @param node
     */
    public static void printGsnNode(GsnNode node) {

        for (int i = 0; i < node.getNodeLevel(); i++) {
            System.out.print("*");
        }
        System.out.print(node.getNodeId() + "| Contexts: ");
        if (node.getInContextOf() != null) {
            for (int i = 0; i < node.getInContextOf().size(); i++) {
                System.out.print(node.getInContextOf().get(i).getNodeId() + ',');
            }
        }

        System.out.println();

        if (node.getSupportedBy() != null) {
            for (int i = 0; i < node.getSupportedBy().size(); i++) {
                printGsnNode(node.getSupportedBy().get(i));
            }
        }
    }

    /**
     * Retruns the goal object for a given requirement or hazard Id from a list of goal objects
     *
     * @param reqId
     * @param allGsnNodeGoalObjects
     * @return
     */
    public static GsnNode.Goal findGsnNodeGoalObject(
            String reqOrHzrdId, List<GsnNode.Goal> allGsnNodeGoalObjects) {
        GsnNode.Goal defaultGoalObject = new GsnNode().new Goal();
        // System.out.println("findGsnNodeGoalObject -- 1 " + " : "+ allGsnNodeGoalObjects.size());
        for (int i = 0; i < allGsnNodeGoalObjects.size(); i++) {
            if (allGsnNodeGoalObjects.get(i).getElementId().equalsIgnoreCase(reqOrHzrdId)) {
                return allGsnNodeGoalObjects.get(i);
            }
        }

        return defaultGoalObject;
    }

    /**
     * Given a hazard or requirement Id, returns the corresponding solution node from a list of
     * solution nodes
     *
     * <p>Retruns null if it cannot find a solution
     *
     * @param reqOrHzrdId
     * @param allGsnNodeSolutionObjects
     * @return
     */
    public static GsnNode.Solution findGsnNodeSolutionObject(
            String reqOrHzrdId, List<GsnNode.Solution> allGsnNodeSolutionObjects) {
        for (int i = 0; i < allGsnNodeSolutionObjects.size(); i++) {
            if (allGsnNodeSolutionObjects.get(i).getSolReq().equalsIgnoreCase(reqOrHzrdId)) {
                return allGsnNodeSolutionObjects.get(i);
            }
        }

        return null;
    }

    /**
     * Returns a list of orphan goals from a list of goals
     *
     * @param allGoals
     * @return
     */
    public static List<GsnNode.Goal> findOrphanGoal(List<GsnNode.Goal> allGoals) {
        List<GsnNode.Goal> orphanGoals = new ArrayList<GsnNode.Goal>();

        // check each goal individually and add the orphan goal
        for (int i = 0; i < allGoals.size(); i++) {
            GsnNode.Goal goalCheck = allGoals.get(i);
            Boolean hasParent = false;

            for (int j = 0; j < allGoals.size(); j++) {
                GsnNode.Goal goal = allGoals.get(j);
                List<String> children = goal.getSubGoals();
                if (children.contains(goalCheck.getElementId())) {
                    System.out.println(goalCheck.getElementId() + " has a parent");
                    hasParent = true;
                    break;
                }
            }

            if (!hasParent) {
                orphanGoals.add(goalCheck);
                System.out.println(goalCheck.getElementId() + " has NO parent");
            }
        }

        return orphanGoals;
    }

    /**
     * Finds all sub hazards and sub requirements for a goal
     *
     * @param all_hazards
     * @param all_reqs
     * @param goalID
     * @return
     */
    public static List<String> findSubHzrdsOrReqs(
            List<String[]> all_hazards, List<String[]> all_reqs, String elementID) {

        List<String> subHzrdOrReqs = new ArrayList<String>();

        // From Hazards
        for (int i = 0; i < all_hazards.size(); i++) {
            String[] hazard = all_hazards.get(i);

            // Format: description, identifier, source, wasDerivedFrom

            // making sure that the hazard has source and wasderivedfrom
            if (hazard.length > 3) {
                // Check if sub hazard
                if (hazard[3].equalsIgnoreCase(elementID)) {
                    subHzrdOrReqs.add(hazard[1]);
                }
            }
        }

        // From requirements
        for (int i = 0; i < all_reqs.size(); i++) {
            String[] req = all_reqs.get(i);

            // Format: description, identifier, mitigates, satisfies

            // making sure that the req has mitigates
            if (req.length > 2) {
                // Check if sub requirement
                if (req[2].equalsIgnoreCase(elementID)) {
                    subHzrdOrReqs.add(req[1]);
                }
            }

            // making sure that the req has satisfies
            if (req.length > 3) {
                // Check if sub requirement
                if (req[3].equalsIgnoreCase(elementID)) {
                    subHzrdOrReqs.add(req[1]);
                }
            }
        }

        // for testing
        System.out.print(elementID + " has sub goals :");
        for (int i = 0; i < subHzrdOrReqs.size(); i++) {
            System.out.print(subHzrdOrReqs.get(i) + ", ");
        }
        System.out.println();
        return subHzrdOrReqs;
    }

    /**
     * Takes a list of gsns and a classname returns the number of rootgoals that belong to that
     * class
     *
     * @param allGsn
     * @param classKey
     * @return
     */
    public static int getClassCount(List<GsnNode> allGsn, String classKey) {
        int count = 0;

        for (GsnNode gsn : allGsn) {
            if (gsn.getGoal().getTypeInOverlay().equalsIgnoreCase(classKey)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Takes a list of gsns and returns the number of rootgoals that have passed
     *
     * @param allGsn
     * @param classKey
     * @return
     */
    public static int getPassCount(List<GsnNode> allGsn) {
        int count = 0;

        for (GsnNode gsn : allGsn) {
            if (gsn.getIsGreen()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets "ID:: Description" of all rootgoals from a list of GSNs
     *
     * @param allGsn
     * @return
     */
    public static List<String> getAllRootGoalIdsWithDescription(
            List<GsnNode> allGsn, String classKey) {
        List<String> allRoots = new ArrayList<String>();
        // add the root ids to the list depending on key
        if (classKey.equals("All")) {
            for (GsnNode gsn : allGsn) {
                allRoots.add(gsn.getGoal().getId() + ":: " + gsn.getGoal().getElementDescription());
            }
        } else {
            for (GsnNode gsn : allGsn) {
                if (gsn.getGoal().getTypeInOverlay().equalsIgnoreCase(classKey)) {
                    allRoots.add(
                            gsn.getGoal().getId() + ":: " + gsn.getGoal().getElementDescription());
                }
            }
        }

        return allRoots;
    }

    /**
     * Gets GSN rootgoal "ID:: Description"
     *
     * @param gsn
     * @return
     */
    public static String getRootGoalIdWithDescription(GsnNode gsn) {
        String idWithDesc = gsn.getGoal().getId() + ":: " + gsn.getGoal().getElementDescription();
        return idWithDesc;
    }

    /**
     * Takes a GsnNode and creates a name, alias, and uri for the Gsn returns a list of strings such
     * that list[0] = name list[1] = alias list[2] = uri
     *
     * @param gsn
     * @return
     */
    public static List<String> synthesizeGsnNodeNameAliasUri(GsnNode gsn) {

        // find rootGoal ID
        String rootId = gsn.getGoal().getId();

        // Create Gsn Name
        String gsnName = "GSN-" + rootId;

        // Create Gsn Alias
        String gsnAlias = gsnName.toLowerCase();

        // Create Gsn Uri
        String gsnUri = "http://sadl.org/" + gsnName;

        List<String> gsnInfo = new ArrayList<String>();
        gsnInfo.add(gsnName);
        gsnInfo.add(gsnAlias);
        gsnInfo.add(gsnUri);

        return gsnInfo;
    }

    /**
     * Given a list of GsnNodes and an id, returns the corresponding GsnNode
     *
     * @param gsnNodes
     * @param id
     * @return
     */
    public static GsnNode getGsnNode(List<GsnNode> gsnNodes, String id) {
        for (GsnNode gsn : gsnNodes) {
            if (gsn.getGoal().getId().equals(id)) return gsn;
        }
        return null; // should not reach here if used correctly
    }

    /**
     * Given a GsnNode and an id, returns the corresponding sub GsnNode
     *
     * @param gsnNodes
     * @param id
     * @return
     */
    public static GsnNode getSubGsnNode(GsnNode gsnNode, String id) {
        //    	System.out.println("node: "+ gsnNode.getNodeId()+ " need id: "+id);
        if (gsnNode.getNodeId().equals(id)) {
            return gsnNode;
        } else {
            if (gsnNode.getSupportedBy() != null) {
                List<GsnNode> childReturned = new ArrayList<GsnNode>();
                for (GsnNode childNode : gsnNode.getSupportedBy()) {
                    childReturned.add(getSubGsnNode(childNode, id));
                }
                // if everyone returned null, return null, else, return the first non-null value
                // from childReturned
                if (childReturned.size() > 0) {
                    for (GsnNode returnedNode : childReturned) {
                        if (returnedNode != null) {
                            return returnedNode;
                        }
                    }
                    return null; // if all ifs fail
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Gets all possible classes of goals from a set of GSNs
     *
     * @param gsnNodes
     * @return
     */
    public static List<String> getAllGoalClasses(List<GsnNode> gsnNodes) {
        List<String> allClasses = new ArrayList<String>();
        for (GsnNode gsn : gsnNodes) {
            allClasses.add(gsn.getGoal().getTypeInOverlay());
        }

        // remove duplicates
        List<String> uniqueClasses = allClasses.stream().distinct().collect(Collectors.toList());

        return uniqueClasses;
    }

    /**
     * Adjusts the level of nodes after fragmentation
     *
     * @param node
     * @param level
     * @return
     */
    public static GsnNode adjustLevels(GsnNode node, int level) {
        GsnNode newNode = new GsnNode();

        if (node.getGoal() != null) {
            newNode.setGoal(node.getGoal());
        } else if (node.getStrategy() != null) {
            newNode.setStrategy(node.getStrategy());
        } else if (node.getContext() != null) {
            newNode.setContext(node.getContext());
        } else if (node.getSolution() != null) {
            newNode.setSolution(node.getSolution());
        }

        newNode.setNodeId(node.getNodeId());
        newNode.setNodeType(node.getNodeType());
        newNode.setIsGreen(node.getIsGreen());
        if (node.getParentNodeId() != null) {
            newNode.setParentNodeId(node.getParentNodeId());
        }

        newNode.setNodeLevel(level);

        List<GsnNode> contextNodes = new ArrayList<GsnNode>();
        if (node.getInContextOf() != null) {
            for (GsnNode context : node.getInContextOf()) {
                GsnNode childNode = adjustLevels(context, level + 1);
                contextNodes.add(childNode);
            }
        }

        List<GsnNode> supportNodes = new ArrayList<GsnNode>();
        if (node.getSupportedBy() != null) {
            for (GsnNode support : node.getSupportedBy()) {
                GsnNode childNode = adjustLevels(support, level + 1);
                supportNodes.add(childNode);
            }
        }

        newNode.setInContextOf(contextNodes);
        newNode.setSupportedBy(supportNodes);

        return newNode;
    }

    /**
     * Given a GsnNode, extracts all possible sub fragments and returns a list of fragments
     *
     * <p>Note: The fragments will have messed up levels that will need adjusting
     *
     * @param rootNode
     * @return
     */
    public static List<GsnNode> fragmentizeNode(GsnNode rootNode) {
        List<GsnNode> allFragments = new ArrayList<GsnNode>();

        if (rootNode.getNodeType() != GsnCoreElements.Class.Strategy) {
            allFragments.add(rootNode);
        }

        if (rootNode.getSupportedBy() != null) {
            for (GsnNode childNode : rootNode.getSupportedBy()) {
                List<GsnNode> childReturned = getAllFragments(childNode);
                allFragments.addAll(childReturned);
            }
        }

        return allFragments;
    }

    /**
     * Given a GsnNode, extracts all possible sub fragments and returns a list of fragments
     *
     * @param rootNode
     * @return
     */
    public static List<GsnNode> getAllFragments(GsnNode rootNode) {
        List<GsnNode> allFragments = fragmentizeNode(rootNode);

        List<GsnNode> allAdjustedFragments = new ArrayList<GsnNode>();

        for (GsnNode fragment : allFragments) {
            allAdjustedFragments.add(adjustLevels(fragment, 0));
        }

        return allAdjustedFragments;
    }
}
