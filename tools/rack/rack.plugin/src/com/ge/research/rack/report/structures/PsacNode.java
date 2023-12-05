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
package com.ge.research.rack.report.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>A collection of hierarchical data structures to store a PSAC and associated RACK data
 */
public class PsacNode {

    /**
     * objective outputs
     *
     * @author Saswata Paul
     */
    public class Output {

        private List<Requirement> requirements = new ArrayList<Requirement>();

        private List<DataItem> dataItems = new ArrayList<DataItem>();

        private List<Analysis> analyses = new ArrayList<Analysis>();

        private List<Test> tests = new ArrayList<Test>();

        private List<ReviewLog> logs = new ArrayList<ReviewLog>();

        private List<Hazard> hazards = new ArrayList<Hazard>();

        /**
         * @return the hazards
         */
        public List<Hazard> getHazards() {
            return hazards;
        }

        /**
         * @param hazards the hazards to set
         */
        public void setHazards(List<Hazard> hazards) {
            this.hazards = hazards;
        }

        /**
         * @return the dataItems
         */
        public List<DataItem> getDataItems() {
            return dataItems;
        }

        /**
         * @param dataItems the dataItems to set
         */
        public void setDataItems(List<DataItem> dataItems) {
            this.dataItems = dataItems;
        }

        /**
         * @return the logs
         */
        public List<ReviewLog> getLogs() {
            return logs;
        }

        /**
         * @param logs the logs to set
         */
        public void setLogs(List<ReviewLog> logs) {
            this.logs = logs;
        }

        /**
         * @return the tests
         */
        public List<Test> getTests() {
            return tests;
        }

        /**
         * @param tests the tests to set
         */
        public void setTests(List<Test> tests) {
            this.tests = tests;
        }

        /**
         * @return the requirements
         */
        public List<Requirement> getRequirements() {
            return requirements;
        }

        /**
         * @param requirements the requirements to set
         */
        public void setRequirements(List<Requirement> requirements) {
            this.requirements = requirements;
        }

        /**
         * @return the documents
         */
        public List<DataItem> getDocuments() {
            return dataItems;
        }

        /**
         * @param dataItems the documents to set
         */
        public void setDocuments(List<DataItem> dataItems) {
            this.dataItems = dataItems;
        }

        /**
         * @return the analyses
         */
        public List<Analysis> getAnalyses() {
            return analyses;
        }

        /**
         * @param analyses the analyses to set
         */
        public void setAnalyses(List<Analysis> analyses) {
            this.analyses = analyses;
        }
    }

    /**
     * To store activity info
     *
     * @author Saswata Paul
     */
    public class Activity {

        private String id;

        private String description;

        private Boolean passed = false; // by default

        private Boolean noData = true; // by default

        private List<Activity> performs = new ArrayList<Activity>();

        /**
         * @return the performs
         */
        public List<Activity> getPerforms() {
            return performs;
        }

        /**
         * @param performs the performs to set
         */
        public void setPerforms(List<Activity> performs) {
            this.performs = performs;
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
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the passed
         */
        public Boolean getPassed() {
            return passed;
        }

        /**
         * @param passed the passed to set
         */
        public void setPassed(Boolean passed) {
            this.passed = passed;
        }

        /**
         * @return the noData
         */
        public Boolean getNoData() {
            return noData;
        }

        /**
         * @param noData the noData to set
         */
        public void setNoData(Boolean noData) {
            this.noData = noData;
        }
    }

    /**
     * To store objective info
     *
     * @author Saswata Paul
     */
    public class Objective {

        private String id;

        private String description;

        private List<Activity> objActivities = new ArrayList<Activity>();

        private PsacNode.Output objOutputs = new PsacNode().new Output();

        private Boolean passed;

        private Boolean noData;

        private Boolean partialData;

        private String metrics = "TBD"; // the metric to be printed in the list beside name

        private int numActPassed;
        private int numActFailed;
        private int numActNoData;

        /**
         * @return the metrics
         */
        public String getMetrics() {
            return metrics;
        }

        /**
         * @param metrics the metrics to set
         */
        public void setMetrics(String metrics) {
            this.metrics = metrics;
        }

        /**
         * @return the numActPassed
         */
        public int getNumActPassed() {
            return numActPassed;
        }

        /**
         * @param numActPassed the numActPassed to set
         */
        public void setNumActPassed(int numActPassed) {
            this.numActPassed = numActPassed;
        }

        /**
         * @return the numActFailed
         */
        public int getNumActFailed() {
            return numActFailed;
        }

        /**
         * @param numActFailed the numActFailed to set
         */
        public void setNumActFailed(int numActFailed) {
            this.numActFailed = numActFailed;
        }

        /**
         * @return the numActNoData
         */
        public int getNumActNoData() {
            return numActNoData;
        }

        /**
         * @param numActNoData the numActNoData to set
         */
        public void setNumActNoData(int numActNoData) {
            this.numActNoData = numActNoData;
        }

        /**
         * @return the partialData
         */
        public Boolean getPartialData() {
            return partialData;
        }

        /**
         * @param partialData the partialData to set
         */
        public void setPartialData(Boolean partialData) {
            this.partialData = partialData;
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
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the objActivities
         */
        public List<Activity> getObjActivities() {
            return this.objActivities;
        }

        /**
         * @param objActivities the objActivities to set
         */
        public void setObjActivities(List<Activity> objActivities) {
            this.objActivities = objActivities;
        }

        /**
         * @return the passed
         */
        public Boolean getPassed() {
            return passed;
        }

        /**
         * @param passed the passed to set
         */
        public void setPassed(Boolean passed) {
            this.passed = passed;
        }

        /**
         * @return the noData
         */
        public Boolean getNoData() {
            return noData;
        }

        /**
         * @param noData the noData to set
         */
        public void setNoData(Boolean noData) {
            this.noData = noData;
        }

        /**
         * @return the objOutputs
         */
        public PsacNode.Output getObjOutputs() {
            return objOutputs;
        }

        /**
         * @param objOutputs the objOutputs to set
         */
        public void setObjOutputs(PsacNode.Output objOutputs) {
            this.objOutputs = objOutputs;
        }
    }

    /**
     * To store table info
     *
     * @author Saswata Paul
     */
    public class Table {

        private String id;

        private String description;

        private List<Objective> tabObjectives = new ArrayList<Objective>();

        private Boolean passed;

        private Boolean noData;

        private Boolean partialData;

        private String metrics = "TBD"; // the metric to be printed in the list beside name

        private int numObjComplete;
        private int numObjPartial;
        private int numObjNodata;
        private int numObjPassed;
        private int numObjFailed;

        /**
         * @return the metrics
         */
        public String getMetrics() {
            return metrics;
        }

        /**
         * @param metrics the metrics to set
         */
        public void setMetrics(String metrics) {
            this.metrics = metrics;
        }

        /**
         * @return the numObjFailed
         */
        public int getNumObjFailed() {
            return numObjFailed;
        }

        /**
         * @param numObjFailed the numObjFailed to set
         */
        public void setNumObjFailed(int numObjFailed) {
            this.numObjFailed = numObjFailed;
        }

        /**
         * @return the numObjPassed
         */
        public int getNumObjPassed() {
            return numObjPassed;
        }

        /**
         * @param numObjPassed the numObjPassed to set
         */
        public void setNumObjPassed(int numObjPassed) {
            this.numObjPassed = numObjPassed;
        }

        /**
         * @return the numObjComplete
         */
        public int getNumObjComplete() {
            return numObjComplete;
        }

        /**
         * @param numObjComplete the numObjComplete to set
         */
        public void setNumObjComplete(int numObjComplete) {
            this.numObjComplete = numObjComplete;
        }

        /**
         * @return the numObjPartial
         */
        public int getNumObjPartial() {
            return numObjPartial;
        }

        /**
         * @param numObjPartial the numObjPartial to set
         */
        public void setNumObjPartial(int numObjPartial) {
            this.numObjPartial = numObjPartial;
        }

        /**
         * @return the numObjNodata
         */
        public int getNumObjNodata() {
            return numObjNodata;
        }

        /**
         * @param numObjNodata the numObjNodata to set
         */
        public void setNumObjNodata(int numObjNodata) {
            this.numObjNodata = numObjNodata;
        }

        /**
         * @return the partialData
         */
        public Boolean getPartialData() {
            return partialData;
        }

        /**
         * @param partialData the partialData to set
         */
        public void setPartialData(Boolean partialData) {
            this.partialData = partialData;
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
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the tabObjectives
         */
        public List<Objective> getTabObjectives() {
            return tabObjectives;
        }

        /**
         * @param tabObjectives the tabObjectives to set
         */
        public void setTabObjectives(List<Objective> tabObjectives) {
            this.tabObjectives = tabObjectives;
        }

        /**
         * @return the passed
         */
        public Boolean getPassed() {
            return passed;
        }

        /**
         * @param passed the passed to set
         */
        public void setPassed(Boolean passed) {
            this.passed = passed;
        }

        /**
         * @return the noData
         */
        public Boolean getNoData() {
            return noData;
        }

        /**
         * @param noData the noData to set
         */
        public void setNoData(Boolean noData) {
            this.noData = noData;
        }
    }

    // The ID and level of the main software
    private String mainOFP = "";

    // The Id of the psac
    private String psacId = "";

    // List of tables in psac
    private List<Table> reportTables = new ArrayList<Table>();

    // report status
    private Boolean passed;

    /**
     * @return the reportTables
     */
    public List<Table> getReportTables() {
        return this.reportTables;
    }

    /**
     * @param reportTables the reportTables to set
     */
    public void setReportTables(List<Table> reportTables) {
        this.reportTables = reportTables;
    }

    /**
     * @return the passed
     */
    public Boolean getPassed() {
        return passed;
    }

    /**
     * @param passed the passed to set
     */
    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    /**
     * @return the mainOFP
     */
    public String getMainOFP() {
        return mainOFP;
    }

    /**
     * @param mainOFP the mainOFP to set
     */
    public void setMainOFP(String mainOFP) {
        this.mainOFP = mainOFP;
    }

    /**
     * @return the psacId
     */
    public String getPsacId() {
        return psacId;
    }

    /**
     * @param psacId the psacId to set
     */
    public void setPsacId(String psacId) {
        this.psacId = psacId;
    }
}
