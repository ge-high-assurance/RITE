package com.ge.research.rack.report.structures;

import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;

/**
 * @author Saswata Paul A store to contain the SpqrQl connection parameters for repeated use
 */
public class SparqlConnectionInfo {

    // To store the connection type
    private String connectionType;

    // to store the model url
    private String modelUrl;

    // to store model graph
    private String modelGraph;

    // to store the data url
    private String dataUrl;

    // to store the data graph
    private String dataGraph;

    // To store the sparql client
    private NodeGroupExecutionClient clientVar;

    // To store the sparql connection for executing queries
    private SparqlConnection overrideVar;

    // To store the NodeGroupStoreRestClient for uploading queries

    private NodeGroupStoreRestClient ngsrClient;

    public NodeGroupExecutionClient getClientVar() {
        return clientVar;
    }

    public void setClientVar(NodeGroupExecutionClient clientVar) {
        this.clientVar = clientVar;
    }

    public SparqlConnection getOverrideVar() {
        return overrideVar;
    }

    public void setOverrideVar(SparqlConnection overrideVar) {
        this.overrideVar = overrideVar;
    }

    /**
     * @return the ngsrClient
     */
    public NodeGroupStoreRestClient getNgsrClient() {
        return ngsrClient;
    }

    /**
     * @param ngsrClient the ngsrClient to set
     */
    public void setNgsrClient(NodeGroupStoreRestClient ngsrClient) {
        this.ngsrClient = ngsrClient;
    }

    /**
     * @return the connectionType
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * @param connectionType the connectionType to set
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    /**
     * @return the modelUrl
     */
    public String getModelUrl() {
        return modelUrl;
    }

    /**
     * @param modelUrl the modelUrl to set
     */
    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    /**
     * @return the modelGraph
     */
    public String getModelGraph() {
        return modelGraph;
    }

    /**
     * @param modelGraph the modelGraph to set
     */
    public void setModelGraph(String modelGraph) {
        this.modelGraph = modelGraph;
    }

    /**
     * @return the dataUrl
     */
    public String getDataUrl() {
        return dataUrl;
    }

    /**
     * @param dataUrl the dataUrl to set
     */
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    /**
     * @return the dataGraph
     */
    public String getDataGraph() {
        return dataGraph;
    }

    /**
     * @param dataGraph the dataGraph to set
     */
    public void setDataGraph(String dataGraph) {
        this.dataGraph = dataGraph;
    }
}
