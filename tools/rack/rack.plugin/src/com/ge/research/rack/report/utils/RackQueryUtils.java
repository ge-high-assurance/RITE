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
package com.ge.research.rack.report.utils;

import com.ge.research.rack.autoGsn.utils.CustomStringUtils;
import com.ge.research.rack.report.structures.SparqlConnectionInfo;
import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.Core;
import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClientConfig;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreConfig;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import com.ge.research.semtk.sparqlX.SparqlEndpointInterface;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Saswata Paul
 */
public class RackQueryUtils {

    // TODO: Remove redundancies with
    // /rack.plugin/src/com/ge/research/rack/utils/ConnectionUtil.java

    /**
     * Initialize RACK connection parameters using RACK preferences
     *
     * @throws Exception
     */
    public static SparqlConnectionInfo initiateQueryConnection() {
        try {
            SparqlConnectionInfo newConnPars = new SparqlConnectionInfo();

            // Set up the nodegroup execution service
            String PROTOCOL = RackPreferencePage.getProtocol();
            String SERVER = RackPreferencePage.getServer();
            int NGE_PORT = Integer.parseInt(RackPreferencePage.getNGEPort()); //

            // set up a triplestore connection
            String CONN_TYPE = RackPreferencePage.getConnType();
            String CONN_URL = RackPreferencePage.getConnURL();
            String CONN_GRAPH = RackPreferencePage.getDefaultModelGraph();
            String DATA_GRAPH = RackPreferencePage.getDefaultDataGraph();

            newConnPars.setConnectionType(CONN_TYPE);
            newConnPars.setModelUrl(CONN_URL);
            newConnPars.setDataUrl(CONN_URL);
            newConnPars.setModelGraph(CONN_GRAPH);
            newConnPars.setDataGraph(DATA_GRAPH);

            // build the nodegroup executor client
            NodeGroupExecutionClientConfig config =
                    new NodeGroupExecutionClientConfig(PROTOCOL, SERVER, NGE_PORT);
            NodeGroupExecutionClient client = new NodeGroupExecutionClient(config);

            // Build a connection that uses an endpoint graph for model and another for data
            SparqlEndpointInterface modelSei =
                    SparqlEndpointInterface.getInstance(CONN_TYPE, CONN_URL, CONN_GRAPH);

            SparqlEndpointInterface dataSei =
                    SparqlEndpointInterface.getInstance(CONN_TYPE, CONN_URL, DATA_GRAPH);

            SparqlConnection override = new SparqlConnection("RACK", modelSei, dataSei);

            // Create NodeGroupStoresRestClient
            NodeGroupStoreRestClient ngsrClient = ConnectionUtil.getNGSClient();

            // Setting values to the parameters object
            newConnPars.setClientVar(client);
            newConnPars.setOverrideVar(override);
            newConnPars.setNgsrClient(ngsrClient);

            return newConnPars;
        } catch (Exception e) {
            System.out.println(
                    "ERROR: Was unable to create SparQlGraph connection using user preferences!!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initialize RACK connection parameters using hardcoded preferences
     *
     * <p>NOTE: This is used for testing only !!
     *
     * @throws Exception
     */
    public static SparqlConnectionInfo hardcodedQueryConnectionForTesting() {
        try {
            // ------ MANUAL RACK CONNECTION FOR TESTING WITHOUT ECLIPSE GUI
            // Set up the nodegroup execution service
            String PROTOCOL = "http";
            String SERVER = "localhost";
            int NGE_PORT = 12058;
            // set up a triplestore connection
            String CONN_TYPE = "fuseki";
            String CONN_URL = "http://localhost:3030/RACK";
            String CONN_GRAPH = "http://rack001/model";
            String DATA_GRAPH = "http://rack001/data";
            // build the nodegroup executor client
            NodeGroupExecutionClientConfig config =
                    new NodeGroupExecutionClientConfig(PROTOCOL, SERVER, NGE_PORT);
            NodeGroupExecutionClient client = new NodeGroupExecutionClient(config);
            // Build a connection that uses an endpoint graph for model and another for data
            SparqlEndpointInterface modelSei =
                    SparqlEndpointInterface.getInstance(CONN_TYPE, CONN_URL, CONN_GRAPH);

            SparqlEndpointInterface dataSei =
                    SparqlEndpointInterface.getInstance(CONN_TYPE, CONN_URL, DATA_GRAPH);

            SparqlConnection override = new SparqlConnection("RACK", modelSei, dataSei);

            // Create NodeGroupStoresRestClient
            int STORE_PORT = 12056;
            NodeGroupStoreConfig ngConfig = new NodeGroupStoreConfig(PROTOCOL, SERVER, STORE_PORT);
            NodeGroupStoreRestClient ngsrClient = new NodeGroupStoreRestClient(ngConfig);

            // Connection variables
            SparqlConnectionInfo newConnPars = new SparqlConnectionInfo();
            newConnPars.setClientVar(client);
            newConnPars.setOverrideVar(override);
            newConnPars.setNgsrClient(ngsrClient);
            newConnPars.setConnectionType(CONN_TYPE);
            newConnPars.setModelUrl(CONN_URL);
            newConnPars.setDataUrl(CONN_URL);
            newConnPars.setModelGraph(CONN_GRAPH);
            newConnPars.setDataGraph(DATA_GRAPH);

            // ----------------------------------------------------------------------------

            return newConnPars;
        } catch (Exception e) {
            System.out.println("ERROR: Was unable to create hardcoded SparQlGraph connection!!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Given a json filepath, returns the qury id from the filename
     *
     * <p>Eg:, xxx/yyy/query_do_something.json --> query_do_something
     *
     * @param path
     * @return
     */
    public static String getQueryIdFromFilePath(String path) {
        // get the filename
        File f = new File(path);
        String fileName = f.getName();
        // remove .json from filename
        String id = fileName.replace(".json", "");

        return id;
    }

    /**
     * Takes a queryId and output directory and returns csv file Path
     *
     * @param QueryId
     * @param outDir
     * @return
     */
    public static String createCsvFilePath(String QueryId, String outDir) {

        String filePath = outDir + "\\" + QueryId + ".csv";

        return filePath;
    }

    /**
     * Runs a query already on rack store and saves the output as a csv file at specified file path
     *
     * @param queryId
     * @param csvFilePath
     * @throws Exception
     */
    public static void executeRackStoreQueryAndCreateCSV(
            String queryId, String csvFilePath, SparqlConnectionInfo connPars) {

        System.out.println("Sending the following query to RACK via the API:" + queryId);

        try {
            // run query from the store by id
            com.ge.research.semtk.resultSet.Table results =
                    connPars.getClientVar()
                            .execDispatchSelectByIdToTable(
                                    queryId, connPars.getOverrideVar(), null, null);

            System.out.println(
                    "Number of rows in the com.ge.research.semtk.resultSet.Table object returned by the API = "
                            + results.getNumRows()
                            + "\n");

            String csv_string = results.toCSVString();

            // System.out.println(csv_string);

            try {
                // save the result as csv file
                FileWriter myWriter = new FileWriter(csvFilePath);
                myWriter.write(csv_string);
                myWriter.close();
                System.out.println("Successfully created and wrote data to CSV files.\n");
            } catch (Exception e) {
                System.out.println("ERROR: Could not create and write data to CSV files.\n");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(
                    "ERROR: Could not execute query " + queryId + " successfully on RACK!!\n");
            e.printStackTrace();
        }
    }

    /**
     * Executes a given query already existing in RACK store by ID and stores result as csv in
     * provided output directory
     *
     * @param queryId
     * @param outDir
     * @param connPars
     */
    public static void executeSingleQueryFromStore(
            String queryId, String outDir, SparqlConnectionInfo connPars) {

        String csvFilePath = createCsvFilePath(queryId, outDir);
        // execute the query
        executeRackStoreQueryAndCreateCSV(queryId, csvFilePath, connPars);
    }

    /**
     * Given a list of queries, output directory, and spqrql connection parameters, creates a
     * filename with the queryId and executes each
     *
     * @param Queries
     * @param connPars
     * @throws Exception
     */
    public static void executeMultiQueriesFromStore(
            List<String> Queries, String outDir, SparqlConnectionInfo connPars) {

        for (int i = 0; i < Queries.size(); i++) {

            String queryId = Queries.get(i);

            executeSingleQueryFromStore(queryId, outDir, connPars);
        }
    }

    /**
     * Reads a csv file and returns data packed into a list of string arrays where each array
     * represents a line in the file
     *
     * <p>Note: 1. This SKIPS the first line of csv which are usually the headers 2. This CANNOT
     * handle newlines in between quotes
     *
     * @param flPth
     * @return
     * @throws Exception
     */
    public static List<String[]> readCSVFile(String flPth) {

        Path filePath = Paths.get(flPth);

        // to store the csv data as a list of lists
        List<String[]> fileArray = new ArrayList<String[]>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset())) {

            // read the first line from the csv file
            String csvLine = reader.readLine();

            // read the second line
            csvLine = reader.readLine();

            // loop to read all lines
            while (csvLine != null) {

                // This helps ignore commas within quotes
                String[] row = csvLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
                fileArray.add(row);
                // System.out.println(line);

                // read next line before looping
                // will be null at EOF
                csvLine = reader.readLine();
            }

        } catch (IOException ioe) {
            System.out.println(
                    "ERROR: Was unable to read the CSV files created by querying RACK!!\n");
            ioe.printStackTrace();
        }

        return fileArray;
    }

    /**
     * Reads a csv file and returns data packed into a list of string arrays where each array
     * represents a line in the file
     *
     * <p>Note: 1. This SKIPS the first line of csv which are usually the headers 2. CAN handle
     * commas and newline between quotes
     *
     * @param flPth
     * @return
     */
    public static List<String[]> readCSVFile2(String flPth) {

        // to store the csv data as a list of lists
        List<String[]> fileArray = new ArrayList<String[]>();

        // get entire file as a string object
        String fileString =
                com.ge.research.rack.autoGsn.utils.CustomFileUtils.readFile(
                        flPth, Charset.defaultCharset());

        // replace all commas and newlines inside quote
        String cleanString = CustomStringUtils.removeCommasAndNewlinesInQuotes(fileString);

        // read resultant string line by line and split and store
        try (BufferedReader reader = new BufferedReader(new StringReader(cleanString))) {

            // read the second line from the csv file
            // String columnHeadersLine = reader.readLine();

            // read the second line
            String csvLine = reader.readLine();

            // loop to read all lines
            while (csvLine != null) {

                // This helps ignore commas within quotes
                String[] row = csvLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
                fileArray.add(row);
                // System.out.println(line);

                // read next line before looping
                // will be null at EOF
                csvLine = reader.readLine();
            }

        } catch (IOException ioe) {
            System.out.println(
                    "ERROR: Was unable to read the CSV files created by querying RACK!!\n");
            ioe.printStackTrace();
        }

        return fileArray;
    }

    /**
     * Reads a csv file and returns data packed into a list of string arrays where each array
     * represents a line in the file
     *
     * <p>Note: 1. This INCLUDES the first line of csv which are usually the headers 2. CAN handle
     * commas and newline between quotes
     *
     * @param flPth
     * @return
     */
    public static List<String[]> readCSVFile3(String flPth) {

        // to store the csv data as a list of lists
        List<String[]> fileArray = new ArrayList<String[]>();

        // get entire file as a string object
        String fileString =
                com.ge.research.rack.autoGsn.utils.CustomFileUtils.readFile(
                        flPth, Charset.defaultCharset());

        // replace all commmas and newlines inside quote
        String cleanString = CustomStringUtils.removeCommasAndNewlinesInQuotes(fileString);

        // read resultant string line by line and split and store
        try (BufferedReader reader = new BufferedReader(new StringReader(cleanString))) {

            // read the first line from the csv file
            String csvLine = reader.readLine();

            // loop to read all lines
            while (csvLine != null) {

                // This helps ignore commas within quotes
                String[] row = csvLine.split(",(?=([^\"]|\"[^\"]*\")*$)");
                fileArray.add(row);
                // System.out.println(line);

                // read next line before looping
                // will be null at EOF
                csvLine = reader.readLine();
            }

        } catch (IOException ioe) {
            System.out.println(
                    "ERROR: Was unable to read the CSV files created by querying RACK!!\n");
            ioe.printStackTrace();
        }

        return fileArray;
    }

    /**
     * Reads a csv file and returns header line as a string array
     *
     * <p>Note: 1. This ONLY INCLUDES the first line of csv which are usually the headers 2. CAN
     * handle commas and newline between quotes
     *
     * @param flPth
     * @return
     */
    public static String[] readCSVHeader(String flPth) {

        // get entire file as a string object
        String fileString =
                com.ge.research.rack.autoGsn.utils.CustomFileUtils.readFile(
                        flPth, Charset.defaultCharset());

        // replace all commmas and newlines inside quote
        String cleanString = CustomStringUtils.removeCommasAndNewlinesInQuotes(fileString);

        // read resultant string line by line and split and store
        try (BufferedReader reader = new BufferedReader(new StringReader(cleanString))) {

            // read the first line from the csv file
            String csvLine = reader.readLine();

            if (csvLine == null) {
                String[] arr = {};
                return arr;
            }
            // This helps ignore commas within quotes
            String[] row = csvLine.split(",(?=([^\"]|\"[^\"]*\")*$)");

            return row;

        } catch (IOException ioe) {
            System.out.println(
                    "ERROR: Was unable to read the CSV files created by querying RACK!!\n");
            ioe.printStackTrace();
        }

        return null;
    }

    /**
     * Takes the id and filepath of an existing query json file and tries to upload the
     * corresponding nodegroup to RACK Returns true if successful and false otherwise
     *
     * <p>NOTE: It first deletes any nodegroup with same name that already exists in RACK store
     *
     * @param queryId
     * @param filePath
     * @return
     */
    public static Boolean uploadQueryNodegroupToRackStore(String queryId, String filePath) {
        // ***************** DO NOT DELETE ***************** TURNED OFF FOR TESTING
        // Connect to RACK using RACK preferences
        SparqlConnectionInfo newConnPars = initiateQueryConnection();

        //        // FOR TESTING ONLY : Connect to RACK using hardcoded preferences
        //        SparqlConnectionInfo newConnPars = hardcodedQueryConnectionForTesting();

        // Delete the query nodegroup if it already exists in the store
        try {
            System.out.println("Deleting nodegroup with same name on store (if any exists)");
            newConnPars.getNgsrClient().deleteStoredNodeGroupIfExists(queryId);
        } catch (Exception e) { // This means that the nodegroup did not already exist in the store
            // Do nothing here
        }

        try {
            String jsonString =
                    FileUtils.readFileToString(
                            new java.io.File(filePath), Charset.defaultCharset());
            SparqlGraphJson json = new SparqlGraphJson(jsonString);
            json.setSparqlConn(newConnPars.getOverrideVar());
            System.out.println("Created nodegroup from file: " + queryId + ".json");
            try {
                System.out.println("Uploading nodegroup: " + queryId);
                newConnPars
                        .getNgsrClient()
                        .executeStoreNodeGroup(
                                queryId,
                                Core.NODEGROUP_INGEST_COMMENT,
                                System.getProperty("user.name"),
                                json.getJson());
                System.out.println("Successfully uploaded nodegroup to RACK store!\n");
                return true;

            } catch (Exception e) {
                System.out.println(
                        "ERROR: Upload of nodegroup: " + queryId + ".json " + "failed!\n");
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            System.out.println(
                    "ERROR: Could not use json string from filePath to create nodegroup!\n");
            e.printStackTrace();
            return false;
        }
    }
}
