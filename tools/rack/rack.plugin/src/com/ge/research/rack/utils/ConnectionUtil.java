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
package com.ge.research.rack.utils;

import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClientConfig;
import com.ge.research.semtk.edc.client.OntologyInfoClientConfig;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreConfig;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.resultSet.SimpleResultSet;
import com.ge.research.semtk.resultSet.TableResultSet;
import com.ge.research.semtk.services.client.RestClient;
import com.ge.research.semtk.services.client.RestClientConfig;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import com.ge.research.semtk.sparqlX.SparqlEndpointInterface;
import com.ge.research.semtk.sparqlX.client.SparqlQueryAuthClientConfig;
import com.ge.research.semtk.sparqlX.client.SparqlQueryClient;
import java.util.List;

public class ConnectionUtil {

    private static final String SERVICE_INFO_ENDPOINT = "/serviceInfo/ping";

    public static boolean ping() {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int ngePort = Integer.parseInt(RackPreferencePage.getNGEPort());
        RestClientConfig config = null;
        try {
            config = new RestClientConfig(protocol, server, ngePort, SERVICE_INFO_ENDPOINT);
            config.setMethod(RestClientConfig.Methods.POST);
            RestClient client = new RestClient(config);
            final SimpleResultSet response = client.executeWithSimpleResultReturn();
            if (response.getSuccess() && response.getResult("available").equals("yes")) {
                return true;
            }

            RackConsole.getConsole()
                    .error("RACK Services down, please check your Rack-Box instance");
            return false;
        } catch (Exception e) {
            RackConsole.getConsole()
                    .error("Unable to configure connection to check RACK service availability");
            return false;
        }
    }

    public static String getServiceInfoEndpoint() {
        return SERVICE_INFO_ENDPOINT;
    }

    public static String getServer() {
        return RackPreferencePage.getServer();
    }

    public static String getProtocol() {
        return RackPreferencePage.getProtocol();
    }

    public static int getNGEPort() {
        try {
            return Integer.parseInt(RackPreferencePage.getNGEPort());
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getStorePort() {
        try {
            return Integer.parseInt(RackPreferencePage.getStorePort());
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getUtilityPort() {
        try {
            return Integer.parseInt(RackPreferencePage.getUtilityPort());
        } catch (Exception e) {
            return -1;
        }
    }

    public static SparqlEndpointInterface getSparqlEndpointInterface() throws Exception {
        String connType = RackPreferencePage.getConnType();
        String connUrl = RackPreferencePage.getConnURL();
        String defaultModelGraph = RackPreferencePage.getDefaultModelGraph();
        return SparqlEndpointInterface.getInstance(connType, connUrl, defaultModelGraph);
    }

    public static NodeGroupExecutionClient getNGEClient() throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getNGEPort());
        NodeGroupExecutionClientConfig config =
                new NodeGroupExecutionClientConfig(protocol, server, port);
        NodeGroupExecutionClient client = new NodeGroupExecutionClient(config);
        return client;
    }

    public static NodeGroupStoreRestClient getNGSClient() throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getStorePort());

        NodeGroupStoreConfig ngConfig = new NodeGroupStoreConfig(protocol, server, port);
        return new NodeGroupStoreRestClient(ngConfig);
    }

    public static SparqlConnection getSparqlConnection(
            String modelGraph, String dataGraph, List<String> dataGraphs) {

        String connType = RackPreferencePage.getConnType();
        String connURL = RackPreferencePage.getConnURL();
        String connDataGraph =
                (dataGraph == null || dataGraph.isEmpty())
                        ? RackPreferencePage.getDefaultDataGraph()
                        : dataGraph;
        SparqlConnection conn = null;
        try {
            SparqlEndpointInterface modelSei =
                    SparqlEndpointInterface.getInstance(
                            connType,
                            connURL,
                            modelGraph,
                            RackPreferencePage.getUser(),
                            RackPreferencePage.getPassword()); // Connection
            SparqlEndpointInterface dataSei =
                    SparqlEndpointInterface.getInstance(
                            connType,
                            connURL,
                            connDataGraph,
                            RackPreferencePage.getUser(),
                            RackPreferencePage.getPassword());

            conn = new SparqlConnection("RACK", modelSei, dataSei);

            // add extra data graphs here

        } catch (Exception e) {
            RackConsole.getConsole()
                    .error(
                            "Unable to connect to Sparql, please check configuration in RACK preference page");
        }

        for (int i = 0; i < dataGraphs.size(); i++) {

            try {
                SparqlEndpointInterface extraDataSei =
                        SparqlEndpointInterface.getInstance(
                                connType,
                                connURL,
                                dataGraphs.get(i),
                                RackPreferencePage.getUser(),
                                RackPreferencePage.getPassword());
                conn.addDataInterface(extraDataSei);

            } catch (Exception e) {
                RackConsole.getConsole().error("Cannot add extra data graph: " + dataGraphs.get(i));
            }
        }

        return conn;
    }

    public static SparqlConnection getSparqlConnection(
            List<String> modelGraphs, String dataGraph, List<String> dataGraphs) {

        String connType = RackPreferencePage.getConnType();
        String connURL = RackPreferencePage.getConnURL();
        String connDataGraph =
                (dataGraph == null || dataGraph.isEmpty())
                        ? RackPreferencePage.getDefaultDataGraph()
                        : dataGraph;
        SparqlConnection conn = null;
        String modelGraph = modelGraphs.get(0);
        List<String> additionalModelGraphs = modelGraphs.subList(1, modelGraphs.size());
        try {
            SparqlEndpointInterface modelSei =
                    SparqlEndpointInterface.getInstance(
                            connType,
                            connURL,
                            modelGraph,
                            RackPreferencePage.getUser(),
                            RackPreferencePage.getPassword()); // Connection
            SparqlEndpointInterface dataSei =
                    SparqlEndpointInterface.getInstance(
                            connType,
                            connURL,
                            connDataGraph,
                            RackPreferencePage.getUser(),
                            RackPreferencePage.getPassword());

            conn = new SparqlConnection("RACK", modelSei, dataSei);

            for (String mGraph : additionalModelGraphs) {
                SparqlEndpointInterface additionalModelSei =
                        SparqlEndpointInterface.getInstance(
                                connType,
                                connURL,
                                mGraph,
                                RackPreferencePage.getUser(),
                                RackPreferencePage.getPassword());
                conn.addModelInterface(additionalModelSei);
            }

            // add extra data graphs here

        } catch (Exception e) {
            RackConsole.getConsole()
                    .error(
                            "Unable to connect to Sparql, please check configuration in RACK preference page");
        }

        for (int i = 0; i < dataGraphs.size(); i++) {

            try {
                SparqlEndpointInterface extraDataSei =
                        SparqlEndpointInterface.getInstance(
                                connType,
                                connURL,
                                dataGraphs.get(i),
                                RackPreferencePage.getUser(),
                                RackPreferencePage.getPassword());
                conn.addDataInterface(extraDataSei);

            } catch (Exception e) {
                RackConsole.getConsole().error("Cannot add extra data graph: " + dataGraphs.get(i));
            }
        }

        return conn;
    }

    public static SparqlConnection getSparqlConnection() {

        String connType = RackPreferencePage.getConnType();
        String connURL = RackPreferencePage.getConnURL();
        String defaultModelGraph = RackPreferencePage.getDefaultModelGraph();
        String defaultDataGraph = RackPreferencePage.getDefaultDataGraph();
        SparqlConnection conn = null;
        try {
            SparqlEndpointInterface modelSei =
                    SparqlEndpointInterface.getInstance(
                            connType,
                            connURL,
                            defaultModelGraph,
                            RackPreferencePage.getUser(),
                            RackPreferencePage.getPassword()); // Connection
            SparqlEndpointInterface dataSei =
                    SparqlEndpointInterface.getInstance(
                            connType,
                            connURL,
                            defaultDataGraph,
                            RackPreferencePage.getUser(),
                            RackPreferencePage.getPassword());
            conn = new SparqlConnection("RACK", modelSei, dataSei);
        } catch (Exception e) {
            RackConsole.getConsole()
                    .error(
                            "Unable to connect to Sparql, please check configuration in RACK preference page");
        }
        return conn;
    }

    public static SparqlQueryClient getOntologyUploadClient() throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getQueryPort());
        String sparqlQueryEndPoint = "/sparqlQueryService/uploadOwl";
        String connURL = RackPreferencePage.getConnURL();
        String connType = RackPreferencePage.getConnType();
        String defaultModelGraph = RackPreferencePage.getDefaultModelGraph();
        String user = RackPreferencePage.getUser();
        String password = RackPreferencePage.getPassword();
        SparqlQueryAuthClientConfig qAuthConfig =
                new SparqlQueryAuthClientConfig(
                        protocol,
                        server,
                        port,
                        sparqlQueryEndPoint,
                        connURL,
                        connType,
                        defaultModelGraph,
                        user,
                        password);

        return new SparqlQueryClient(qAuthConfig);
    }

    public static SparqlQueryClient getOntologyUploadClient(String graph) throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getQueryPort());
        String sparqlQueryEndPoint = "/sparqlQueryService/uploadOwl";
        String connURL = RackPreferencePage.getConnURL();
        String connType = RackPreferencePage.getConnType();
        String user = RackPreferencePage.getUser();
        String password = RackPreferencePage.getPassword();
        SparqlQueryAuthClientConfig qAuthConfig =
                new SparqlQueryAuthClientConfig(
                        protocol,
                        server,
                        port,
                        sparqlQueryEndPoint,
                        connURL,
                        connType,
                        graph,
                        user,
                        password);

        return new SparqlQueryClient(qAuthConfig);
    }

    public static SparqlQueryClient getDataGraphClient() throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getQueryPort());
        String sparqlQueryEndPoint = "/sparqlQueryService/uploadOwl";
        String connURL = RackPreferencePage.getConnURL();
        String connType = RackPreferencePage.getConnType();
        String defaultDataGraph = RackPreferencePage.getDefaultDataGraph();
        String user = RackPreferencePage.getUser();
        String password = RackPreferencePage.getPassword();
        SparqlQueryAuthClientConfig qAuthConfig =
                new SparqlQueryAuthClientConfig(
                        protocol,
                        server,
                        port,
                        sparqlQueryEndPoint,
                        connURL,
                        connType,
                        defaultDataGraph,
                        user,
                        password);
        return new SparqlQueryClient(qAuthConfig);
    }

    public static SparqlQueryClient getDataGraphClient(String dataGraph) throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getQueryPort());
        String sparqlQueryEndPoint = "/sparqlQueryService/uploadOwl";
        String connURL = RackPreferencePage.getConnURL();
        String connType = RackPreferencePage.getConnType();
        String user = RackPreferencePage.getUser();
        String password = RackPreferencePage.getPassword();
        SparqlQueryAuthClientConfig qAuthConfig =
                new SparqlQueryAuthClientConfig(
                        protocol,
                        server,
                        port,
                        sparqlQueryEndPoint,
                        connURL,
                        connType,
                        dataGraph,
                        user,
                        password);
        return new SparqlQueryClient(qAuthConfig);
    }

    public static OntologyInfoClientConfig getOntologyClientConfig() throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getOntologyPort());
        return new OntologyInfoClientConfig(protocol, server, port);
    }

    public static TableResultSet getGraphInfo() throws Exception {
        String protocol = RackPreferencePage.getProtocol();
        String server = RackPreferencePage.getServer();
        int port = Integer.parseInt(RackPreferencePage.getQueryPort());
        String sparqlQueryEndPoint = "/sparqlQueryService/selectGraphNames";
        String connURL = RackPreferencePage.getConnURL();
        String connType = RackPreferencePage.getConnType();
        String defaultDataGraph = RackPreferencePage.getDefaultDataGraph();
        String user = RackPreferencePage.getUser();
        String password = RackPreferencePage.getPassword();
        SparqlQueryAuthClientConfig qAuthConfig =
                new SparqlQueryAuthClientConfig(
                        protocol,
                        server,
                        port,
                        sparqlQueryEndPoint,
                        connURL,
                        connType,
                        defaultDataGraph,
                        user,
                        password);
        SparqlQueryClient qClient = new SparqlQueryClient(qAuthConfig);
        qClient.addParameter("skipSemtkGraphs", "true");
        qClient.addParameter("graphNamesOnly", "false");
        return qClient.executeWithTableResultReturn();
    }
}
