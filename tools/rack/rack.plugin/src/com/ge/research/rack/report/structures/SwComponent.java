/** */
package com.ge.research.rack.report.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class SwComponent {

    private String id;

    private String description;

    private String type;

    private List<Requirement> wasImpactedBy =
            new ArrayList<
                    Requirement>(); // initializing so that the list does not need to be created

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
     * @return the wasImpactedBy
     */
    public List<Requirement> getWasImpactedBy() {
        return wasImpactedBy;
    }

    /**
     * @param wasImpactedBy the wasImpactedBy to set
     */
    public void setWasImpactedBy(List<Requirement> wasImpactedBy) {
        this.wasImpactedBy = wasImpactedBy;
    }

    /**
     * Get a string with all reqiurement traces separated by ','
     *
     * @return
     */
    public String getWasImpactedByAsString() {
        String list = "";

        if ((this.getWasImpactedBy() != null)) {
            if ((this.getWasImpactedBy().size() > 0)) {
                for (Requirement req : this.getWasImpactedBy()) {
                    list = list + req.getId() + ", ";
                }
            }
        }

        return list;
    }
}
