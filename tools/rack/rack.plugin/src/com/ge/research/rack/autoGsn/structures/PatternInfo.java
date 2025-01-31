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
package com.ge.research.rack.autoGsn.structures;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>A data structure to store the path info and a list of queries for a GSN pattern fetched
 *     from RACK
 */
public class PatternInfo {

    /** Class for evidence patterns */
    public class EvidencePat {
        private String evidenceClass;
        private String statusProperty;
        private String passVal;

        /**
         * @param evidenceClass
         * @param statusProperty
         * @param passVal
         */
        public EvidencePat(String evidenceClass, String statusProperty, String passVal) {
            super();
            this.evidenceClass = evidenceClass;
            this.statusProperty = statusProperty;
            this.passVal = passVal;
        }
        /**
         * @return the evidenceClass
         */
        public String getEvidenceClass() {
            return evidenceClass;
        }
        /**
         * @param evidenceClass the evidenceClass to set
         */
        public void setEvidenceClass(String evidenceClass) {
            this.evidenceClass = evidenceClass;
        }
        /**
         * @return the statusProperty
         */
        public String getStatusProperty() {
            return statusProperty;
        }
        /**
         * @param statusProperty the statusProperty to set
         */
        public void setStatusProperty(String statusProperty) {
            this.statusProperty = statusProperty;
        }
        /**
         * @return the passVal
         */
        public String getPassVal() {
            return passVal;
        }
        /**
         * @param passVal the passVal to set
         */
        public void setPassVal(String passVal) {
            this.passVal = passVal;
        }
    }

    /**
     * Class for storing goal patterns
     *
     * @author Saswata Paul
     */
    public class GoalPat {
        private String id;
        private String desc;
        private String goalClass;

        /**
         * @param id
         * @param desc
         * @param goalClass
         */
        public GoalPat(String id, String desc, String goalClass) {
            super();
            this.id = id;
            this.desc = desc;
            this.goalClass = goalClass;
        }
        /**
         * @return the id
         */
        public String getId() {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }
        /**
         * @return the desc
         */
        public String getDesc() {
            return desc;
        }
        /**
         * @param desc the desc to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }
        /**
         * @return the goalClass
         */
        public String getGoalClass() {
            return goalClass;
        }
        /**
         * @param goalClass the goalClass to set
         */
        public void setGoalClass(String goalClass) {
            this.goalClass = goalClass;
        }
    }

    /**
     * class for storing strategy patterns
     *
     * @author Saswata Paul
     */
    public class StratPat {
        private String id;
        private String desc;
        private String goalClass;
        private String subGoalClass;
        private String property;
        private Boolean
                propIsForward; // true if the property goes from goal to subgoal and false if the
        // opposite

        /**
         * @param id
         * @param desc
         * @param goalClass
         * @param subGoalClass
         * @param property
         */
        public StratPat(
                String id, String desc, String goalClass, String subGoalClass, String property) {
            super();
            this.id = id;
            this.desc = desc;
            this.goalClass = goalClass;
            this.subGoalClass = subGoalClass;
            this.property = property;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }
        /**
         * @return the desc
         */
        public String getDesc() {
            return desc;
        }
        /**
         * @param desc the desc to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }
        /**
         * @return the goalClass
         */
        public String getGoalClass() {
            return goalClass;
        }
        /**
         * @param goalClass the goalClass to set
         */
        public void setGoalClass(String goalClass) {
            this.goalClass = goalClass;
        }
        /**
         * @return the subGoalClass
         */
        public String getSubGoalClass() {
            return subGoalClass;
        }
        /**
         * @param subGoalClass the subGoalClass to set
         */
        public void setSubGoalClass(String subGoalClass) {
            this.subGoalClass = subGoalClass;
        }
        /**
         * @return the property
         */
        public String getProperty() {
            return property;
        }
        /**
         * @param property the property to set
         */
        public void setProperty(String property) {
            this.property = property;
        }

        /**
         * @return the propIsForward
         */
        public Boolean getPropIsForward() {
            return propIsForward;
        }

        /**
         * @param propIsForward the propIsForward to set
         */
        public void setPropIsForward(Boolean propIsForward) {
            this.propIsForward = propIsForward;
        }
    }

    /**
     * A partially ordered ordered collection of strategy patterns that have info for the binary
     * jumps in a path
     *
     * <p>Note: Stored as a recursive tree data structure
     *
     * @author Saswata Paul
     */
    public class PathTree {
        private StratPat currentNode;
        private int currentLevel;
        private List<PathTree> subPaths = new ArrayList<PathTree>();

        /**
         * @return the currentNode
         */
        public StratPat getCurrentNode() {
            return currentNode;
        }

        /**
         * @param currentNode the currentNode to set
         */
        public void setCurrentNode(StratPat currentNode) {
            this.currentNode = currentNode;
        }

        /**
         * @return the currentLevel
         */
        public int getCurrentLevel() {
            return currentLevel;
        }

        /**
         * @param currentLevel the currentLevel to set
         */
        public void setCurrentLevel(int currentLevel) {
            this.currentLevel = currentLevel;
        }

        /**
         * @return the subPaths
         */
        public List<PathTree> getSubPaths() {
            return subPaths;
        }

        /**
         * @param subPaths the subPaths to set
         */
        public void setSubPaths(List<PathTree> subPaths) {
            this.subPaths = subPaths;
        }

        public void printPath(PathTree path) {
            for (int i = 0; i < path.getCurrentLevel(); i++) {
                System.out.print("*");
            }
            System.out.println(" " + path.getCurrentNode().getGoalClass());
            if (path.getSubPaths().size() > 0) {
                for (PathTree sbPath : path.getSubPaths()) {
                    printPath(sbPath);
                }
            }
        }
    }

    // -- Main class variables
    private List<GoalPat> goalPats = new ArrayList<GoalPat>();
    private List<StratPat> stratPats = new ArrayList<StratPat>();
    private List<EvidencePat> evdPats = new ArrayList<EvidencePat>();
    private List<PathTree> pathTrees = new ArrayList<PathTree>();
    private List<String> queryJsonFilePaths = new ArrayList<String>();
    private JSONObject ontInfo;

    /**
     * @return the ontInfo
     */
    public JSONObject getOntInfo() {
        return ontInfo;
    }
    /**
     * @param ontInfo the ontInfo to set
     */
    public void setOntInfo(JSONObject ontInfo) {
        this.ontInfo = ontInfo;
    }
    /**
     * @return the goalPats
     */
    public List<GoalPat> getGoalPats() {
        return goalPats;
    }
    /**
     * @param goalPats the goalPats to set
     */
    public void setGoalPats(List<GoalPat> goalPats) {
        this.goalPats = goalPats;
    }
    /**
     * @return the stratPats
     */
    public List<StratPat> getStratPats() {
        return stratPats;
    }
    /**
     * @param stratPats the stratPats to set
     */
    public void setStratPats(List<StratPat> stratPats) {
        this.stratPats = stratPats;
    }
    /**
     * @return the evdPats
     */
    public List<EvidencePat> getEvdPats() {
        return evdPats;
    }
    /**
     * @param evdPats the evdPats to set
     */
    public void setEvdPats(List<EvidencePat> evdPats) {
        this.evdPats = evdPats;
    }
    /**
     * @return the paths
     */
    public List<PathTree> getPathTrees() {
        return pathTrees;
    }
    /**
     * @param paths the paths to set
     */
    public void setPathTrees(List<PathTree> paths) {
        this.pathTrees = paths;
    }
    /**
     * @return the queryIds
     */
    public List<String> getQueryJsonFilePaths() {
        return queryJsonFilePaths;
    }
    /**
     * @param queryIds the queryIds to set
     */
    public void setQueryJsonFilePaths(List<String> queryIds) {
        this.queryJsonFilePaths = queryIds;
    }
}
