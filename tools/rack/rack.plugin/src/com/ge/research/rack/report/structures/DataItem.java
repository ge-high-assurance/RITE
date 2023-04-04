/** */
package com.ge.research.rack.report.structures;

/**
 * @author Saswata Paul
 */
public class DataItem {

    private String format;
    private String id;
    private String description;
    private String content;
    private String objectiveId; // the DO178C objective the dataitem is an output of

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
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
     * @return the content
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * @return the objectiveId
     */
    public String getObjectiveId() {
        return objectiveId;
    }
    /**
     * @param objectiveId the objectiveId to set
     */
    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }
}
