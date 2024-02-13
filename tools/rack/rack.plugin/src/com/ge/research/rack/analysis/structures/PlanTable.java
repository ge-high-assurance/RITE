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
package com.ge.research.rack.analysis.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * To store table info
 *
 * @author Saswata Paul
 */
public class PlanTable<T> {
    protected String id = "";
    protected String description = "";
    protected List<T> objectives = new ArrayList<T>();

    protected boolean passed = false;
    protected boolean noData = true;
    protected boolean partialData = false;

    protected String metrics = "TBD"; // the metric to be printed in the list beside name
    protected double complianceStatus = 0.0;

    protected int numObjComplete;
    protected int numObjPartial;
    protected int numObjNoData;
    protected int numObjPassed;
    protected int numObjFailed;

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
    public int getNumObjNoData() {
        return numObjNoData;
    }

    /**
     * @param numObjNodata the numObjNodata to set
     */
    public void setNumObjNoData(int numObjNodata) {
        this.numObjNoData = numObjNodata;
    }

    /**
     * @return the partialData
     */
    public Boolean isPartialData() {
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
    public List<T> getTabObjectives() {
        return objectives;
    }

    /**
     * @param tabObjectives the tabObjectives to set
     */
    public void setTabObjectives(List<T> tabObjectives) {
        this.objectives = tabObjectives;
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
    public void setPassed(Boolean passed) {
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
}
