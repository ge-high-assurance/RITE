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
package com.ge.research.rack.arp4754.structures;

import com.ge.research.rack.analysis.structures.PlanOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * A package for different types pf outputs that can be associated with an ARP4754 Objective
 *
 * @author Saswata Paul
 */
public class Output extends PlanOutput {

    // ARP4754 Output Objects
    private List<Evidence> documentObjs = new ArrayList<Evidence>();

    private List<Evidence> derItemReqObjs = new ArrayList<Evidence>();

    private List<Evidence> derSysReqObjs = new ArrayList<Evidence>();

    private List<Evidence> interfaceObjs = new ArrayList<Evidence>();

    private List<Evidence> interfaceInputObjs = new ArrayList<Evidence>();

    private List<Evidence> interfaceOutputObjs = new ArrayList<Evidence>();

    private List<Evidence> itemObjs = new ArrayList<Evidence>();

    private List<Evidence> itemReqObjs = new ArrayList<Evidence>();

    private List<Evidence> sysReqObjs = new ArrayList<Evidence>();

    private List<Evidence> systemObjs = new ArrayList<Evidence>();

    private List<Evidence> systemDesignDescriptionObjs = new ArrayList<Evidence>();

    private List<Evidence> requirementCompleteCorrectReviewObjs = new ArrayList<Evidence>();

    private List<Evidence> requirementTraceableReviewObjs = new ArrayList<Evidence>();

    // ---
    private List<Evidence> verificationObjs = new ArrayList<Evidence>();

    private List<Evidence> reviewObjs = new ArrayList<Evidence>();

    private List<Evidence> testObjs = new ArrayList<Evidence>();

    private List<Evidence> analysisObjs = new ArrayList<Evidence>();

    /**
     * @return the requirementCompleteCorrectReviewObjs
     */
    public List<Evidence> getRequirementCompleteCorrectReviewObjs() {
        return requirementCompleteCorrectReviewObjs;
    }

    /**
     * @param requirementCompleteCorrectReviewObjs the requirementCompleteCorrectReviewObjs to set
     */
    public void setRequirementCompleteCorrectReviewObjs(
            List<Evidence> requirementCompleteCorrectReviewObjs) {
        this.requirementCompleteCorrectReviewObjs = requirementCompleteCorrectReviewObjs;
    }

    /**
     * @return the requirementTraceableReviewObjs
     */
    public List<Evidence> getRequirementTraceableReviewObjs() {
        return requirementTraceableReviewObjs;
    }

    /**
     * @param requirementTraceableReviewObjs the requirementTraceableReviewObjs to set
     */
    public void setRequirementTraceableReviewObjs(List<Evidence> requirementTraceableReviewObjs) {
        this.requirementTraceableReviewObjs = requirementTraceableReviewObjs;
    }

    /**
     * @return the documentObjs
     */
    public List<Evidence> getDocumentObjs() {
        return documentObjs;
    }

    /**
     * @param documentObjs the documentObjs to set
     */
    public void setDocumentObjs(List<Evidence> documentObjs) {
        this.documentObjs = documentObjs;
    }

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

    /**
     * @return the verificationObjs
     */
    public List<Evidence> getVerificationObjs() {
        return verificationObjs;
    }

    /**
     * @param verificationObjs the verificationObjs to set
     */
    public void setVerificationObjs(List<Evidence> verificationObjs) {
        this.verificationObjs = verificationObjs;
    }

    /**
     * @return the reviewObjs
     */
    public List<Evidence> getReviewObjs() {
        return reviewObjs;
    }

    /**
     * @param reviewObjs the reviewObjs to set
     */
    public void setReviewObjs(List<Evidence> reviewObjs) {
        this.reviewObjs = reviewObjs;
    }

    /**
     * @return the testObjs
     */
    public List<Evidence> getTestObjs() {
        return testObjs;
    }

    /**
     * @param testObjs the testObjs to set
     */
    public void setTestObjs(List<Evidence> testObjs) {
        this.testObjs = testObjs;
    }

    /**
     * @return the analysisObjs
     */
    public List<Evidence> getAnalysisObjs() {
        return analysisObjs;
    }

    /**
     * @param analysisObjs the analysisObjs to set
     */
    public void setAnalysisObjs(List<Evidence> analysisObjs) {
        this.analysisObjs = analysisObjs;
    }

    /**
     * @return the systemDesignDescriptionObjs
     */
    public List<Evidence> getSystemDesignDescriptionObjs() {
        return systemDesignDescriptionObjs;
    }

    /**
     * @param systemDesignDescriptionObjs the systemDesignDescriptionObjs to set
     */
    public void setSystemDesignDescriptionObjs(List<Evidence> systemDesignDescriptionObjs) {
        this.systemDesignDescriptionObjs = systemDesignDescriptionObjs;
    }
}
