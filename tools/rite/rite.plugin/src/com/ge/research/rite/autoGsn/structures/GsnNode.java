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
package com.ge.research.rite.autoGsn.structures;

import com.ge.research.rite.autoGsn.constants.GsnCoreElements;
import com.ge.research.rite.autoGsn.constants.RackCoreElements;

import java.util.List;

/**
 * @author Saswata Paul
 */
public class GsnNode {

    // Types of nodes

    public class Goal {

        private String id;

        private String description;

        private Boolean developed;

        // Stores the hzard/requirement/any other class Id that this goal is concerned with
        private String elementId;

        // Stores the hzard/requirement/any other class description that this goal is concerned with
        private String elementDescription;

        // The sub goals that will appear under a given goal in the final GSN
        // NOTE: This does not represent the supportedBy relationship. That is a property of the
        // GsnNode parent class
        // This information will be used to create the gsn hierarchy
        private List<String> subGoals;

        // The type of the goal: hazard or requirement
        private RackCoreElements.RackClass type;

        // The class of the goal element as per project overlay
        private String typeInOverlay;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getDeveloped() {
            return developed;
        }

        public void setDeveloped(Boolean developed) {
            this.developed = developed;
        }

        public String getElementId() {
            return elementId;
        }

        public void setElementId(String hzrdOrReqId) {
            this.elementId = hzrdOrReqId;
        }

        public List<String> getSubGoals() {
            return subGoals;
        }

        public void setSubGoals(List<String> subGoals) {
            this.subGoals = subGoals;
        }

        public RackCoreElements.RackClass getType() {
            return type;
        }

        public void setType(RackCoreElements.RackClass goalType) {
            this.type = goalType;
        }

        /**
         * @return the typeInOverlay
         */
        public String getTypeInOverlay() {
            return typeInOverlay;
        }

        /**
         * @param typeInOverlay the typeInOverlay to set
         */
        public void setTypeInOverlay(String typeInOverlay) {
            this.typeInOverlay = typeInOverlay;
        }

        /**
         * @return the elementDescription
         */
        public String getElementDescription() {
            return elementDescription;
        }

        /**
         * @param elementDescription the elementDescription to set
         */
        public void setElementDescription(String elementDescription) {
            this.elementDescription = elementDescription;
        }
    }

    public class Strategy {

        private String id;

        private String description;

        private Boolean developed;

        // Stores the hzard or requirement Id that this goal is concerned with
        private String hzrdOrReqId;

        // The sub hazards or requirements that will appear under a given goal in the final GSN
        // NOTE: This does not represent the supportedBy relationship. That is a property of the
        // GsnNode parent class
        // This information will be used to create the gsn hierarchy
        private List<String> subGoals;

        // The type of the strategy: for a hazard or requirement
        private RackCoreElements.RackClass type;

        // The class of the srategy element as per project overlay
        private String typeInOverlay;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getDeveloped() {
            return developed;
        }

        public void setDeveloped(Boolean developed) {
            this.developed = developed;
        }

        public String getHzrdOrReqId() {
            return hzrdOrReqId;
        }

        public void setHzrdOrReqId(String hzrdOrReqId) {
            this.hzrdOrReqId = hzrdOrReqId;
        }

        public List<String> getSubGoals() {
            return subGoals;
        }

        public void setSubGoals(List<String> subGoals) {
            this.subGoals = subGoals;
        }

        public RackCoreElements.RackClass getType() {
            return type;
        }

        public void setType(RackCoreElements.RackClass type) {
            this.type = type;
        }

        /**
         * @return the typeInOverlay
         */
        public String getTypeInOverlay() {
            return typeInOverlay;
        }

        /**
         * @param typeInOverlay the typeInOverlay to set
         */
        public void setTypeInOverlay(String typeInOverlay) {
            this.typeInOverlay = typeInOverlay;
        }
    }

    public class Context {

        private String id;

        private String context;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }
    }

    public class Solution {

        private String id;

        // The test that the result confirms
        private String solTest;

        // The requirements that the test corresponding to the result verifies
        private String solReq;

        private Boolean supportive;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSolTest() {
            return solTest;
        }

        public void setSolTest(String solTest) {
            this.solTest = solTest;
        }

        public Boolean getSupportive() {
            return supportive;
        }

        public void setSupportive(Boolean supportive) {
            this.supportive = supportive;
        }

        public String getSolReq() {
            return solReq;
        }

        public void setSolReq(String solReq) {
            this.solReq = solReq;
        }
    }

    // GsnNode vaiables and functions

    /** An integer describing the level of a node */
    private int nodeLevel;

    /** A unique Id for each node */
    private String nodeId;

    /** The id of the parent GsnNode */
    // for fast parent find
    private String parentNodeId;

    /** Can be one of: ["Goal", "Strategy", "Solution", "Context"] */
    private GsnCoreElements.Class nodeType;

    /** To store relevant information based on nodeType */
    private Goal goal;

    private Strategy strategy;
    private Context context;
    private Solution solution;

    /** To decide color while displaying */
    private Boolean isGreen = false; // false by default

    /** List of nodes which support a node */
    private List<GsnNode> supportedBy;

    /** List of context nodes of a node */
    private List<GsnNode> inContextOf;

    public int getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(int nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public GsnCoreElements.Class getNodeType() {
        return nodeType;
    }

    public void setNodeType(GsnCoreElements.Class nodeType) {
        this.nodeType = nodeType;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public List<GsnNode> getSupportedBy() {
        return supportedBy;
    }

    public void setSupportedBy(List<GsnNode> supportedBy) {
        this.supportedBy = supportedBy;
    }

    public List<GsnNode> getInContextOf() {
        return inContextOf;
    }

    public void setInContextOf(List<GsnNode> inContextOf) {
        this.inContextOf = inContextOf;
    }

    public Boolean getIsGreen() {
        return isGreen;
    }

    public void setIsGreen(Boolean isGreen) {
        this.isGreen = isGreen;
    }

    /**
     * @return the parentNodeId
     */
    public String getParentNodeId() {
        return parentNodeId;
    }

    /**
     * @param parentNodeId the parentNodeId to set
     */
    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }
}
