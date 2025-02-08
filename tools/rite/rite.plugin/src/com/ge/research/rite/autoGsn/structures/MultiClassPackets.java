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
package com.ge.research.rite.autoGsn.structures;

import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * @author Saswata Paul
 *     <p>A class of random structures used to store multiple class values throughout the project
 */
public class MultiClassPackets {

    /**
     * Packs three integers
     *
     * @author Saswata Paul
     */
    public class ThreeInt {
        int first;
        int second;
        int third;

        /**
         * @param first
         * @param second
         * @param third
         */
        public ThreeInt(int first, int second, int third) {
            super();
            this.first = first;
            this.second = second;
            this.third = third;
        }
        /**
         * @return the first
         */
        public int getFirst() {
            return first;
        }
        /**
         * @param first the first to set
         */
        public void setFirst(int first) {
            this.first = first;
        }
        /**
         * @return the second
         */
        public int getSecond() {
            return second;
        }
        /**
         * @param second the second to set
         */
        public void setSecond(int second) {
            this.second = second;
        }
        /**
         * @return the third
         */
        public int getThird() {
            return third;
        }
        /**
         * @param third the third to set
         */
        public void setThird(int third) {
            this.third = third;
        }
    }

    /**
     * Packs goalIds and goalClasses
     *
     * @author Saswata Paul
     */
    public class GoalIdAndClass {

        String goalId;
        String goalClass;

        /**
         * @param goalId
         * @param goalClass
         */
        public GoalIdAndClass(String goalId, String goalClass) {
            super();
            this.goalId = goalId;
            this.goalClass = goalClass;
        }
        /**
         * @return the goalId
         */
        public String getGoalId() {
            return goalId;
        }
        /**
         * @param goalId the goalId to set
         */
        public void setGoalId(String goalId) {
            this.goalId = goalId;
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
     * A pair containing a list of GSNs and a list of instance dependencies
     *
     * @author Saswata Paul
     */
    public class GsnListAndDependenyList {

        List<GsnNode> gsnList;
        List<InstanceData> dependencyList;

        /**
         * @return the gsnList
         */
        public List<GsnNode> getGsnList() {
            return gsnList;
        }
        /**
         * @param gsnList the gsnList to set
         */
        public void setGsnList(List<GsnNode> gsnList) {
            this.gsnList = gsnList;
        }
        /**
         * @return the dependencyList
         */
        public List<InstanceData> getDependencyList() {
            return dependencyList;
        }
        /**
         * @param dependencyList the dependencyList to set
         */
        public void setDependencyList(List<InstanceData> dependencyList) {
            this.dependencyList = dependencyList;
        }
    }

    /**
     * Packs a tree item and a boolean
     *
     * @author Saswata Paul
     */
    public class TreeItemAndBoolean<T> {

        TreeItem<T> treeItem;
        Boolean expandFlag;

        public TreeItemAndBoolean(TreeItem<T> treeItem, Boolean expandFlag) {
            this.treeItem = treeItem;
            this.expandFlag = expandFlag;
        }

        /**
         * @return the treeItem
         */
        public TreeItem<T> getTreeItem() {
            return treeItem;
        }
        /**
         * @param treeItem the treeItem to set
         */
        public void setTreeItem(TreeItem<T> treeItem) {
            this.treeItem = treeItem;
        }
        /**
         * @return the expandFlag
         */
        public Boolean getExpandFlag() {
            return expandFlag;
        }
        /**
         * @param expandFlag the expandFlag to set
         */
        public void setExpandFlag(Boolean expandFlag) {
            this.expandFlag = expandFlag;
        }
    }
}
