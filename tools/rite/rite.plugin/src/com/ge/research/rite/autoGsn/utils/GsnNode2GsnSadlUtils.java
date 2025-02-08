/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, GE Aerospace and Galois, Inc.
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
import com.ge.research.rite.autoGsn.structures.GsnSadl;
import com.ge.research.rite.autoGsn.structures.InstanceData;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains functions to print a GSsnNode as a SADL GSN instance file
 *
 * @author Saswata Paul
 */
public class GsnNode2GsnSadlUtils {

    // NOTE:
    // Because I did not anticipate the GsnNode class will be needed when I started, I had created a
    // GsnSadl class
    // to store the GSNs. However, this is an issue because it requires the additional overhead to
    // translate GsnNode to GsnSadl
    // Before printing. I cannot store different types of GsnSadl subclasses in a single list
    // Therefore, for now, I am just converting to string immediately after creating a GsnSadl so
    // that I can
    // Store everything as List<String>.
    // Therefore:
    // TODO:
    //       Create a GsnNode --> SADL printer directly

    /**
     * creates a string containing the uriblock
     *
     * @param instanceUri
     * @param imports
     * @return
     */
    public static String createUriBlock(String instanceUri, String alias, List<String> imports) {
        // Create a GsnSadl and populate it
        GsnSadl.UriAndImportBlock gsnSadl = new GsnSadl().new UriAndImportBlock();

        gsnSadl.setUri(instanceUri);
        gsnSadl.setAlias(alias);
        gsnSadl.setImports(imports);

        // return converted to string
        return GsnSadlPrintUtils.createUriAndImportsBlockString(gsnSadl);
    }

    /**
     * Creates a string for the GSN root declaration
     *
     * @param instanceUri
     * @param alias
     * @param imports
     * @return
     */
    public static String createRootDeclaration(String graphId, String rootId) {
        // Create a GsnSadl and populate it
        GsnSadl.RootSentence gsnSadl = new GsnSadl().new RootSentence();

        gsnSadl.setGsnId(graphId);
        gsnSadl.setGsnRootGoal(rootId);
        // return converted to string
        return GsnSadlPrintUtils.createRootSentenceString(gsnSadl);
    }

    /**
     * creates SadlBlock for a goal
     *
     * @param goal
     * @return
     */
    public static String getSadlDeclareSentence(GsnNode.Goal goal) {

        // Create a GsnSadl and populate it
        GsnSadl.ElementDeclareBlock gsnSadl = new GsnSadl().new ElementDeclareBlock();

        List<GsnSadl.RelationPair> propList = new ArrayList<GsnSadl.RelationPair>();

        propList.add(new GsnSadl().new RelationPair("identifier", goal.getId()));
        propList.add(new GsnSadl().new RelationPair("description", goal.getDescription()));
        propList.add(new GsnSadl().new RelationPair("developed", goal.getDeveloped().toString()));

        gsnSadl.setSubjectId(goal.getId());
        gsnSadl.setSubjectClass("Goal");
        gsnSadl.setSubjectProperties(propList);

        // return converted to string

        return GsnSadlPrintUtils.createElementDeclareBlockString(gsnSadl);
    }

    /**
     * Creates sadl block for a strategy
     *
     * @param strat
     * @return
     */
    public static String getSadlDeclareSentence(GsnNode.Strategy strat) {
        // Create a GsnSadl and populate it
        GsnSadl.ElementDeclareBlock gsnSadl = new GsnSadl().new ElementDeclareBlock();

        List<GsnSadl.RelationPair> propList = new ArrayList<GsnSadl.RelationPair>();

        propList.add(new GsnSadl().new RelationPair("identifier", strat.getId()));
        propList.add(new GsnSadl().new RelationPair("description", strat.getDescription()));
        propList.add(new GsnSadl().new RelationPair("developed", strat.getDeveloped().toString()));

        gsnSadl.setSubjectId(strat.getId());
        gsnSadl.setSubjectClass("Strategy");
        gsnSadl.setSubjectProperties(propList);

        // return converted to string

        return GsnSadlPrintUtils.createElementDeclareBlockString(gsnSadl);
    }

    /**
     * creates sadlblock for a context
     *
     * @param cont
     * @return
     */
    public static String getSadlDeclareSentence(GsnNode.Context cont) {
        // Create a GsnSadl and populate it
        GsnSadl.ElementDeclareBlock gsnSadl = new GsnSadl().new ElementDeclareBlock();

        List<GsnSadl.RelationPair> propList = new ArrayList<GsnSadl.RelationPair>();

        propList.add(new GsnSadl().new RelationPair("identifier", cont.getId()));
        propList.add(new GsnSadl().new RelationPair("context", cont.getContext()));

        gsnSadl.setSubjectId(cont.getId());
        gsnSadl.setSubjectClass("Context");
        gsnSadl.setSubjectProperties(propList);

        // return converted to string

        return GsnSadlPrintUtils.createElementDeclareBlockString(gsnSadl);
    }

    /**
     * create sadlblock for a solution
     *
     * @param sol
     * @return
     */
    public static String getSadlDeclareSentence(GsnNode.Solution sol) {
        // Create a GsnSadl and populate it
        GsnSadl.ElementDeclareBlock gsnSadl = new GsnSadl().new ElementDeclareBlock();

        List<GsnSadl.RelationPair> propList = new ArrayList<GsnSadl.RelationPair>();

        propList.add(new GsnSadl().new RelationPair("identifier", sol.getId()));
        propList.add(new GsnSadl().new RelationPair("solution", sol.getSolTest())); // The GE
        propList.add(new GsnSadl().new RelationPair("supportive", sol.getSupportive().toString()));

        gsnSadl.setSubjectId(sol.getId());
        gsnSadl.setSubjectClass("Solution");
        gsnSadl.setSubjectProperties(propList);

        // return converted to string

        return GsnSadlPrintUtils.createElementDeclareBlockString(gsnSadl);
    }

    /**
     * Creates sadl block for a gsn instance dependency
     *
     * @param strat
     * @return
     */
    public static String getSadlDeclareSentence(InstanceData instance) {
        // Create a GsnSadl and populate it
        GsnSadl.ElementDeclareBlock gsnSadl = new GsnSadl().new ElementDeclareBlock();

        List<GsnSadl.RelationPair> propList = new ArrayList<GsnSadl.RelationPair>();

        propList.add(new GsnSadl().new RelationPair("identifier", instance.getId()));
        if (instance.getDescription() != null) {
            propList.add(new GsnSadl().new RelationPair("description", instance.getDescription()));
        }

        gsnSadl.setSubjectId(instance.getId());
        gsnSadl.setSubjectClass(instance.getRackClass().toString());
        gsnSadl.setSubjectProperties(propList);

        // return converted to string

        return GsnSadlPrintUtils.createElementDeclareBlockString(gsnSadl);
    }

    /**
     * Creates a incontextOf sentence
     *
     * @param subject
     * @param inContextOf
     * @return
     */
    public static String getSadlInContextOfSentence(String subject, String inContextOf) {
        // Create a GsnSadl and populate it
        GsnSadl.InContextOfSentence gsnSadl = new GsnSadl().new InContextOfSentence();

        gsnSadl.setSubjectId(subject);
        gsnSadl.setSubjectContext(inContextOf);
        // return converted to string

        return GsnSadlPrintUtils.createInContextOfString(gsnSadl);
    }

    /**
     * Creates a supportedBy sentence
     *
     * @param subject
     * @param SupportedBy
     * @return
     */
    public static String getSadlSupportedBySentence(String subject, String supportedBy) {
        // Create a GsnSadl and populate it
        GsnSadl.SupportedBySentence gsnSadl = new GsnSadl().new SupportedBySentence();

        gsnSadl.setSubjectId(subject);
        gsnSadl.setSubjectSupport(supportedBy);
        // return converted to string

        return GsnSadlPrintUtils.createSupportedByString(gsnSadl);
    }

    /**
     * Takes a node and recursively creates all sadls strings
     *
     * @param node
     * @return
     */
    public static List<String> createAllSadlStringsForGSN(GsnNode node) {
        // To store all the Gsn Sadl Sentences as strings
        List<String> gsnNodeSadlSentences = new ArrayList<String>();

        // Create a Sadl declare sentences for the node
        System.out.println("Creating sentence for ---" + node.getNodeId());
        if (node.getNodeType() == GsnCoreElements.Class.Goal) {
            gsnNodeSadlSentences.add(getSadlDeclareSentence(node.getGoal()));
        }
        if (node.getNodeType() == GsnCoreElements.Class.Strategy) {
            gsnNodeSadlSentences.add(getSadlDeclareSentence(node.getStrategy()));
        }
        if (node.getNodeType() == GsnCoreElements.Class.Context) {
            gsnNodeSadlSentences.add(getSadlDeclareSentence(node.getContext()));
        }
        if (node.getNodeType() == GsnCoreElements.Class.Solution) {
            gsnNodeSadlSentences.add(getSadlDeclareSentence(node.getSolution()));
        }

        // create a sadl sentence for each supportedBy node
        if (node.getSupportedBy() != null) {
            for (int i = 0; i < node.getSupportedBy().size(); i++) {
                // recursive call to create the declare block
                gsnNodeSadlSentences.addAll(
                        createAllSadlStringsForGSN(node.getSupportedBy().get(i)));

                // create and add the supportedBy sentence
                gsnNodeSadlSentences.add(
                        getSadlSupportedBySentence(
                                node.getNodeId(), node.getSupportedBy().get(i).getNodeId()));
            }
        }

        // create a sadl sentence for each contextOf Node
        if (node.getInContextOf() != null) {
            for (int i = 0; i < node.getSupportedBy().size(); i++) {
                // recursive call to create the declare block
                gsnNodeSadlSentences.addAll(
                        createAllSadlStringsForGSN(node.getInContextOf().get(i)));

                // create and add the inContextOf sentence
                gsnNodeSadlSentences.add(
                        getSadlInContextOfSentence(
                                node.getNodeId(), node.getInContextOf().get(i).getNodeId()));
            }
        }

        return gsnNodeSadlSentences;
    }

    /**
     * Takes a GsnNode and creates a corresponding Gsn instance Sadl file
     *
     * @param gsn
     * @param fileName
     */
    public static List<String> createGsnSadlInstance(
            String GsnId, GsnNode gsn, String instanceUri, String alias, List<String> imports) {

        // To store all the Gsn Sadl Sentences
        List<String> gsnSadlSentences = new ArrayList<String>();

        // Create the uriBlock

        gsnSadlSentences.add(createUriBlock(instanceUri, alias, imports));

        // For each GsnNode
        // Create the declaration
        // Create the contextofBlocks (if present)
        // Create the supportedBy blocks (if present)
        gsnSadlSentences.addAll(createAllSadlStringsForGSN(gsn));

        // Create the root block

        gsnSadlSentences.add(
                createRootDeclaration(GsnId, gsn.getNodeId())); // To Do: automate the gsn name

        return gsnSadlSentences;
    }
}
