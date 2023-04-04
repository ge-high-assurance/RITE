/** */
package com.ge.research.rack.report.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class Test {

    private String id;
    private String result;
    private String type;

    private List<String> verifies = new ArrayList<String>();
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
     * @param id
     * @param result
     * @param aVerifies
     */
    public Test(String id, String result, String aVerifies, String type) {
        super();
        this.id = id;
        this.result = result;
        this.verifies.add(aVerifies);
        this.type = type;
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

    /**
     * @return the verifies
     */
    public List<String> getVerifies() {
        return verifies;
    }
    /**
     * @param verifies the verifies to set
     */
    public void setVerifies(List<String> verifies) {
        this.verifies = verifies;
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
     * Creates a string with all verifies names
     *
     * @return
     */
    public String getVerifiesAsString() {
        String list = "";
        if (this.verifies != null) {
            if (this.verifies.size() > 0) {
                for (String req : this.verifies) {
                    list = list + req + ", ";
                }
            }
        }
        return list;
    }
}
