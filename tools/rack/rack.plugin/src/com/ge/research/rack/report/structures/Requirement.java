/** */
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
