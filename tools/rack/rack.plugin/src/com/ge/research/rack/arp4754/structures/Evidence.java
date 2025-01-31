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
 *     <p>This is a class that will be used to represent any type of evidence inside RACK
 *     <p>NOTE:Using a single class called EVIDENCE instead of REQUIreMENTS, ANALYSIS, etc will
 *     allow us to have a smaller kernel and more generic code/ algorithms
 */
public class Evidence {

    // Fundamental single valued properties of any evidence
    private String id = "";
    private String description = "";
    private String type = "";
    private String URL = "";

    /**
     * All possible properties that link one evidence to another must be added here
     *
     * <p>NOTE: (Add more as required) 1. These properties are not the exact properties from the
     * RACK ontology, but properties that are relevant to ARP4754 verbiage to keep the kernel simple
     * 2. There are few main properties: allocatedTo, mitigates, tracesUp, tracesDown, hasReviews,
     * hasTests, hasVerifications, hasValidations, result,
     *
     * <p>Add more as required
     */
    private List<Evidence> allocatedTo = new ArrayList<Evidence>();

    private List<Evidence> mitigates = new ArrayList<Evidence>();

    private List<Evidence> tracesUp = new ArrayList<Evidence>();

    private List<Evidence> tracesDown = new ArrayList<Evidence>();

    private List<Evidence> hasInterfaces = new ArrayList<Evidence>();

    private List<Evidence> hasReviews = new ArrayList<Evidence>();

    private List<Evidence> hasTests = new ArrayList<Evidence>();

    private List<Evidence> hasVerifications = new ArrayList<Evidence>();

    private List<Evidence> hasValidations = new ArrayList<Evidence>();

    private List<Evidence> hasResult =
            new ArrayList<Evidence>(); // for tests, verifications, and review logs

    private List<Evidence> hasInputs = new ArrayList<Evidence>();

    private List<Evidence> hasOutputs = new ArrayList<Evidence>();

    // The source document can be annotated here (can be an URL or a document Name)
    private List<String> sourceDocument = new ArrayList<String>();

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
     * @return the allocatedTo
     */
    public List<Evidence> getAllocatedTo() {
        return allocatedTo;
    }

    /**
     * @param allocatedTo the allocatedTo to set
     */
    public void setAllocatedTo(List<Evidence> allocatedTo) {
        this.allocatedTo = allocatedTo;
    }

    /**
     * @return the mitigates
     */
    public List<Evidence> getMitigates() {
        return mitigates;
    }

    /**
     * @param mitigates the mitigates to set
     */
    public void setMitigates(List<Evidence> mitigates) {
        this.mitigates = mitigates;
    }

    /**
     * @return the tracesUp
     */
    public List<Evidence> getTracesUp() {
        return tracesUp;
    }

    /**
     * @param tracesUp the tracesUp to set
     */
    public void setTracesUp(List<Evidence> tracesUp) {
        this.tracesUp = tracesUp;
    }

    /**
     * @return the tracesDown
     */
    public List<Evidence> getTracesDown() {
        return tracesDown;
    }

    /**
     * @param tracesDown the tracesDown to set
     */
    public void setTracesDown(List<Evidence> tracesDown) {
        this.tracesDown = tracesDown;
    }

    /**
     * @return the hasInterfaces
     */
    public List<Evidence> getHasInterfaces() {
        return hasInterfaces;
    }

    /**
     * @param hasInterfaces the hasInterfaces to set
     */
    public void setHasInterfaces(List<Evidence> hasInterfaces) {
        this.hasInterfaces = hasInterfaces;
    }

    /**
     * @return the hasReviews
     */
    public List<Evidence> getHasReviews() {
        return hasReviews;
    }

    /**
     * @param hasReviews the hasReviews to set
     */
    public void setHasReviews(List<Evidence> hasReviews) {
        this.hasReviews = hasReviews;
    }

    /**
     * @return the hasTests
     */
    public List<Evidence> getHasTests() {
        return hasTests;
    }

    /**
     * @param hasTests the hasTests to set
     */
    public void setHasTests(List<Evidence> hasTests) {
        this.hasTests = hasTests;
    }

    /**
     * @return the hasVerifications
     */
    public List<Evidence> getHasVerifications() {
        return hasVerifications;
    }

    /**
     * @param hasVerifications the hasVerifications to set
     */
    public void setHasVerifications(List<Evidence> hasVerifications) {
        this.hasVerifications = hasVerifications;
    }

    /**
     * @return the hasValidations
     */
    public List<Evidence> getHasValidations() {
        return hasValidations;
    }

    /**
     * @param hasValidations the hasValidations to set
     */
    public void setHasValidations(List<Evidence> hasValidations) {
        this.hasValidations = hasValidations;
    }

    /**
     * @return the hasResult
     */
    public List<Evidence> getHasResult() {
        return hasResult;
    }

    /**
     * @param hasResult the hasResult to set
     */
    public void setHasResult(List<Evidence> hasResult) {
        this.hasResult = hasResult;
    }

    /**
     * @return the hasInputs
     */
    public List<Evidence> getHasInputs() {
        return hasInputs;
    }

    /**
     * @param hasInputs the hasInputs to set
     */
    public void setHasInputs(List<Evidence> hasInputs) {
        this.hasInputs = hasInputs;
    }

    /**
     * @return the hasOutputs
     */
    public List<Evidence> getHasOutputs() {
        return hasOutputs;
    }

    /**
     * @param hasOutputs the hasOutputs to set
     */
    public void setHasOutputs(List<Evidence> hasOutputs) {
        this.hasOutputs = hasOutputs;
    }

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
     * @return the uRL
     */
    public String getURL() {
        return URL;
    }

    /**
     * @param uRL the uRL to set
     */
    public void setURL(String uRL) {
        URL = uRL;
    }
}
