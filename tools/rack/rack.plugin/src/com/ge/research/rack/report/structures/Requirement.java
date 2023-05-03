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
 */
public class Requirement {

    private String id;

    private String description;

    private String type;

    private SwComponent allocatedTo;

    private SwComponent governs;

    private List<Hazard> mitigates =
            new ArrayList<Hazard>(); // initializing so that the list does not need to be created

    private List<Requirement> satisfies =
            new ArrayList<
                    Requirement>(); // initializing so that the list does not need to be created

    private List<Test> tests = new ArrayList<Test>();

    private List<ReviewLog> logs = new ArrayList<ReviewLog>();

    private List<String> sourceDocument = new ArrayList<String>();

    /**
     * @return the sourceDocument
     */
    public List<String> getSourceDocument() {
        return sourceDocument;
    }

    /**
     * @param sourceDocument the sourceDocument to set
     */
    public void setSourceDocument(List<String> sourceDocument) {
        this.sourceDocument = sourceDocument;
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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the mitigates
     */
    public List<Hazard> getMitigates() {
        return mitigates;
    }

    /**
     * @param mitigates the mitigates to set
     */
    public void setMitigates(List<Hazard> mitigates) {
        this.mitigates = mitigates;
    }

    /** Adds a new object to the existing mitigates list */
    public void addAMitigates(Hazard hzrd) {
        this.mitigates.add(hzrd);
    }

    /**
     * @return the allocatedTo
     */
    public SwComponent getAllocatedTo() {
        return allocatedTo;
    }

    /**
     * @param allocatedTo the allocatedTo to set
     */
    public void setAllocatedTo(SwComponent allocatedTo) {
        this.allocatedTo = allocatedTo;
    }

    /**
     * @return the governs
     */
    public SwComponent getGoverns() {
        return governs;
    }

    /**
     * @param governs the governs to set
     */
    public void setGoverns(SwComponent governs) {
        this.governs = governs;
    }

    /**
     * @return the satisfies
     */
    public List<Requirement> getSatisfies() {
        return satisfies;
    }

    /**
     * @param satisfies the satisfies to set
     */
    public void setSatisfies(List<Requirement> satisfies) {
        this.satisfies = satisfies;
    }

    /** Adds a new object to the existing satisfies list */
    public void addASatisfies(Requirement req) {
        this.satisfies.add(req);
    }
}
