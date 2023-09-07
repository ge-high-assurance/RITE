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
package com.ge.research.rack.arp4754.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>A class representing Development Assurance Plans for ARP4754
 */
public class DAPlan {

    /**
     * A package for different types pf outputs that can be associated with an ARP4754 Objective
     *
     * @author Saswata Paul
     */
    public class Output {

        // ARP4754 Output Objects
        private List<Evidence> derItemReqObjs = new ArrayList<Evidence>();

        private List<Evidence> derSysReqObjs = new ArrayList<Evidence>();

        private List<Evidence> interfaceObjs = new ArrayList<Evidence>();

        private List<Evidence> interfaceInputObjs = new ArrayList<Evidence>();

        private List<Evidence> interfaceOutputObjs = new ArrayList<Evidence>();

        private List<Evidence> itemObjs = new ArrayList<Evidence>();

        private List<Evidence> itemReqObjs = new ArrayList<Evidence>();

        private List<Evidence> sysReqObjs = new ArrayList<Evidence>();

        private List<Evidence> systemObjs = new ArrayList<Evidence>();

        /**
         * @return the derItemReqObjs
         */
        public List<Evidence> getDerItemReqObjs() {
            return derItemReqObjs;
        }

        /**
         * @param derItemReqObjs the derItemReqObjs to set
         */
        public void setDerItemReqObjs(List<Evidence> derItemReqObjs) {
            this.derItemReqObjs = derItemReqObjs;
        }

        /**
         * @return the derSysReqObjs
         */
        public List<Evidence> getDerSysReqObjs() {
            return derSysReqObjs;
        }

        /**
         * @param derSysReqObjs the derSysReqObjs to set
         */
        public void setDerSysReqObjs(List<Evidence> derSysReqObjs) {
            this.derSysReqObjs = derSysReqObjs;
        }

        /**
         * @return the interfaceObjs
         */
        public List<Evidence> getInterfaceObjs() {
            return interfaceObjs;
        }

        /**
         * @param interfaceObjs the interfaceObjs to set
         */
        public void setInterfaceObjs(List<Evidence> interfaceObjs) {
            this.interfaceObjs = interfaceObjs;
        }

        /**
         * @return the interfaceInputObjs
         */
        public List<Evidence> getInterfaceInputObjs() {
            return interfaceInputObjs;
        }

        /**
         * @param interfaceInputObjs the interfaceInputObjs to set
         */
        public void setInterfaceInputObjs(List<Evidence> interfaceInputObjs) {
            this.interfaceInputObjs = interfaceInputObjs;
        }

        /**
         * @return the interfaceOutputObjs
         */
        public List<Evidence> getInterfaceOutputObjs() {
            return interfaceOutputObjs;
        }

        /**
         * @param interfaceOutputObjs the interfaceOutputObjs to set
         */
        public void setInterfaceOutputObjs(List<Evidence> interfaceOutputObjs) {
            this.interfaceOutputObjs = interfaceOutputObjs;
        }

        /**
         * @return the itemObjs
         */
        public List<Evidence> getItemObjs() {
            return itemObjs;
        }

        /**
         * @param itemObjs the itemObjs to set
         */
        public void setItemObjs(List<Evidence> itemObjs) {
            this.itemObjs = itemObjs;
        }

        /**
         * @return the itemReqObjs
         */
        public List<Evidence> getItemReqObjs() {
            return itemReqObjs;
        }

        /**
         * @param itemReqObjs the itemReqObjs to set
         */
        public void setItemReqObjs(List<Evidence> itemReqObjs) {
            this.itemReqObjs = itemReqObjs;
        }

        /**
         * @return the sysReqObjs
         */
        public List<Evidence> getSysReqObjs() {
            return sysReqObjs;
        }

        /**
         * @param sysReqObjs the sysReqObjs to set
         */
        public void setSysReqObjs(List<Evidence> sysReqObjs) {
            this.sysReqObjs = sysReqObjs;
        }

        /**
         * @return the systemObjs
         */
        public List<Evidence> getSystemObjs() {
            return systemObjs;
        }

        /**
         * @param systemObjs the systemObjs to set
         */
        public void setSystemObjs(List<Evidence> systemObjs) {
            this.systemObjs = systemObjs;
        }
    }

    /**
     * Processes in a DAP
     *
     * @author Saswata Paul
     */
    public class Process {

        private String id = "";
        private String desc = "";
        private List<Objective> objectives = new ArrayList<Objective>();
        private String metrics = "TBD"; // the metric to be printed in the list beside name
        private double complianceStatus = 0.0;
        private boolean passed = false;
        private boolean noData = true;
        private boolean partialData = false;
        private int numObjectivesPassed = 0;
        private int numObjectivesNoData = 0;
        private int numObjectivesPartialData = 0;

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
         * @return the objectives
         */
        public List<Objective> getObjectives() {
            return objectives;
        }

        /**
         * @param objectives the objectives to set
         */
        public void setObjectives(List<Objective> objectives) {
            this.objectives = objectives;
        }

        /**
         * @return the complianceStatus
         */
        public double getComplianceStatus() {
            return complianceStatus;
        }

        /**
         * @param complianceStatus the complianceStatus to set
         */
        public void setComplianceStatus(double complianceStatus) {
            this.complianceStatus = complianceStatus;
        }

        /**
         * @return the passed
         */
        public boolean isPassed() {
            return passed;
        }

        /**
         * @param passed the passed to set
         */
        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        /**
         * @return the noData
         */
        public boolean isNoData() {
            return noData;
        }

        /**
         * @param noData the noData to set
         */
        public void setNoData(boolean noData) {
            this.noData = noData;
        }

        /**
         * @return the partialData
         */
        public boolean isPartialData() {
            return partialData;
        }

        /**
         * @param partialData the partialData to set
         */
        public void setPartialData(boolean partialData) {
            this.partialData = partialData;
        }

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
         * @return the numObjectivesPassed
         */
        public int getNumObjectivesPassed() {
            return numObjectivesPassed;
        }

        /**
         * @param numObjectivesPassed the numObjectivesPassed to set
         */
        public void setNumObjectivesPassed(int numObjectivesPassed) {
            this.numObjectivesPassed = numObjectivesPassed;
        }

        /**
         * @return the numObjectivesNoData
         */
        public int getNumObjectivesNoData() {
            return numObjectivesNoData;
        }

        /**
         * @param numObjectivesNoData the numObjectivesNoData to set
         */
        public void setNumObjectivesNoData(int numObjectivesNoData) {
            this.numObjectivesNoData = numObjectivesNoData;
        }

        /**
         * @return the numObjectivesParyialData
         */
        public int getNumObjectivesPartialData() {
            return numObjectivesPartialData;
        }

        /**
         * @param numObjectivesPartialData the numObjectivesParyialData to set
         */
        public void setNumObjectivesPartialData(int numObjectivesPartialData) {
            this.numObjectivesPartialData = numObjectivesPartialData;
        }
    }

    /**
     * Objectives in a DAP
     *
     * @author Saswata Paul
     */
    public class Objective {

        private String id = "";
        private String desc = "";
        private String applicability = "";
        private List<String> queries = new ArrayList<String>();
        private Output outputs = new Output();
        private String metrics = "TBD"; // the metric to be printed in the list beside name
        private double complianceStatus = 0.0;
        private boolean passed = false;
        private boolean noData = true;
        private boolean partialData = false;

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
         * @return the applicability
         */
        public String getApplicability() {
            return applicability;
        }
        /**
         * @param applicability the applicability to set
         */
        public void setApplicability(String applicability) {
            this.applicability = applicability;
        }
        /**
         * @return the queries
         */
        public List<String> getQueries() {
            return queries;
        }
        /**
         * @param queries the queries to set
         */
        public void setQueries(List<String> queries) {
            this.queries = queries;
        }
        /**
         * @return the outputs
         */
        public Output getOutputs() {
            return outputs;
        }
        /**
         * @param outputs the outputs to set
         */
        public void setOutputs(Output outputs) {
            this.outputs = outputs;
        }
        /**
         * @return the complianceStatus
         */
        public double getComplianceStatus() {
            return complianceStatus;
        }
        /**
         * @param complianceStatus the complianceStatus to set
         */
        public void setComplianceStatus(double complianceStatus) {
            this.complianceStatus = complianceStatus;
        }
        /**
         * @return the passed
         */
        public boolean isPassed() {
            return passed;
        }
        /**
         * @param passed the passed to set
         */
        public void setPassed(boolean passed) {
            this.passed = passed;
        }
        /**
         * @return the noData
         */
        public boolean isNoData() {
            return noData;
        }
        /**
         * @param noData the noData to set
         */
        public void setNoData(boolean noData) {
            this.noData = noData;
        }
        /**
         * @return the partialData
         */
        public boolean isPartialData() {
            return partialData;
        }
        /**
         * @param partialData the partialData to set
         */
        public void setPartialData(boolean partialData) {
            this.partialData = partialData;
        }
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
    }

    private String id = "";
    private String desc = "";
    private String system = "";
    private String sysDAL = "";
    private List<Process> processes = new ArrayList<Process>();
    private double complianceStatus = 0.0;

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
     * @return the system
     */
    public String getSystem() {
        return system;
    }
    /**
     * @param system the system to set
     */
    public void setSystem(String system) {
        this.system = system;
    }
    /**
     * @return the sysDAL
     */
    public String getSysDAL() {
        return sysDAL;
    }
    /**
     * @param sysDAL the sysDAL to set
     */
    public void setSysDAL(String sysDAL) {
        this.sysDAL = sysDAL;
    }
    /**
     * @return the process
     */
    public List<Process> getProcesses() {
        return processes;
    }
    /**
     * @param process the process to set
     */
    public void setProcesses(List<Process> process) {
        processes = process;
    }
    /**
     * @return the complianceStatus
     */
    public double getComplianceStatus() {
        return complianceStatus;
    }
    /**
     * @param complianceStatus the complianceStatus to set
     */
    public void setComplianceStatus(double complianceStatus) {
        this.complianceStatus = complianceStatus;
    }
}
