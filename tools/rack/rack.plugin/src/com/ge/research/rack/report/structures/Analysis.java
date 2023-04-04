/** */
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
