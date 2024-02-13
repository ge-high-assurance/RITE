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
 * To store objective info
 *
 * @author Saswata Paul
 */
public class PlanObjective {

    protected String id = "";
    protected String description = "";

    private String applicability = "";
    private List<String> queries = new ArrayList<String>();

    protected PlanOutput outputs = null;
    protected PlanGraph graphs = null;

    protected String metrics = "TBD"; // the metric to be printed in the list beside name
    protected double complianceStatus = 0.0;

    protected boolean passed = false;
    protected boolean noData = true;
    protected boolean partialData = false;

    protected int numActPassed;
    protected int numActFailed;
    protected int numActNoData;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param desc the desc to set
     */
    public void setDescription(String desc) {
        this.description = desc;
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
    public PlanOutput getOutputs() {
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     */
    public void setOutputs(PlanOutput outputs) {
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

    /**
     * @return the graphs
     */
    public PlanGraph getGraphs() {
        return graphs;
    }

    /**
     * @param graphs the graphs to set
     */
    public void setGraphs(PlanGraph graphs) {
        this.graphs = graphs;
    }

    /**
     * @return the numActPassed
     */
    public int getNumPassed() {
        return numActPassed;
    }

    /**
     * @param numActPassed the numActPassed to set
     */
    public void setNumPassed(int numActPassed) {
        this.numActPassed = numActPassed;
    }

    /**
     * @return the numActFailed
     */
    public int getNumFailed() {
        return numActFailed;
    }

    /**
     * @param numActFailed the numActFailed to set
     */
    public void setNumFailed(int numActFailed) {
        this.numActFailed = numActFailed;
    }

    /**
     * @return the numActNoData
     */
    public int getNumNoData() {
        return numActNoData;
    }

    /**
     * @param numActNoData the numActNoData to set
     */
    public void setNumNoData(int numActNoData) {
        this.numActNoData = numActNoData;
    }
}
