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
package com.ge.research.rack.do178c.structures;

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
