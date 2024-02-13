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
package com.ge.research.rack.do178c.structures;

import com.ge.research.rack.analysis.structures.PlanOutput;

import java.util.ArrayList;
import java.util.List;

public class Output extends PlanOutput {

    private List<Requirement> requirements = new ArrayList<Requirement>();

    private List<DataItem> dataItems = new ArrayList<DataItem>();

    private List<Analysis> analyses = new ArrayList<Analysis>();

    private List<Test> tests = new ArrayList<Test>();

    private List<ReviewLog> logs = new ArrayList<ReviewLog>();

    private List<Hazard> hazards = new ArrayList<Hazard>();

    private List<SwComponent> swComponents = new ArrayList<SwComponent>();

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

    /**
     * @return the swComponents
     */
    public List<SwComponent> getSwComponents() {
        return swComponents;
    }

    /**
     * @param swComponents the swComponents to set
     */
    public void setSwComponents(List<SwComponent> swComponents) {
        this.swComponents = swComponents;
    }
}
