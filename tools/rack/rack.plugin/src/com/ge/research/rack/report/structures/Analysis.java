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
 *     <p>Structure to combine analysis data with corresponding review log data obtained from RACK
 */
public class Analysis {

    /**
     * Structure to store review log data
     *
     * @author Saswata Paul
     */
    public class ReviewLog {

        private String id;

        private String reviews;

        private String dataInsertedBy;

        private String result;

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
         * @return the reviews
         */
        public String getReviews() {
            return reviews;
        }

        /**
         * @param reviews the reviews to set
         */
        public void setReviews(String reviews) {
            this.reviews = reviews;
        }

        /**
         * @return the dataInsertedBy
         */
        public String getDataInsertedBy() {
            return dataInsertedBy;
        }

        /**
         * @param dataInsertedBy the dataInsertedBy to set
         */
        public void setDataInsertedBy(String dataInsertedBy) {
            this.dataInsertedBy = dataInsertedBy;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }

        /**
         * @param result the result to set
         */
        public void setResult(String result) {
            this.result = result;
        }
    }

    private String id;

    private String description;

    private String runBy;

    private List<ReviewLog> logs;

    private Boolean passed;

    private Boolean inconclusive;

    private int numPassedLogs;
    private int numFailedLogs;

    /**
     * @return the numPassedLogs
     */
    public int getNumPassedLogs() {
        return numPassedLogs;
    }

    /**
     * @param numPassedLogs the numPassedLogs to set
     */
    public void setNumPassedLogs(int numPassedLogs) {
        this.numPassedLogs = numPassedLogs;
    }

    /**
     * @return the numFailedLogs
     */
    public int getNumFailedLogs() {
        return numFailedLogs;
    }

    /**
     * @param numFailedLogs the numFailedLogs to set
     */
    public void setNumFailedLogs(int numFailedLogs) {
        this.numFailedLogs = numFailedLogs;
    }

    /**
     * @return the inconclusive
     */
    public Boolean getInconclusive() {
        return inconclusive;
    }

    /**
     * @param inconclusive the inconclusive to set
     */
    public void setInconclusive(Boolean inconclusive) {
        this.inconclusive = inconclusive;
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
     * @return the runBy
     */
    public String getRunBy() {
        return runBy;
    }

    /**
     * @param runBy the runBy to set
     */
    public void setRunBy(String runBy) {
        this.runBy = runBy;
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
}
