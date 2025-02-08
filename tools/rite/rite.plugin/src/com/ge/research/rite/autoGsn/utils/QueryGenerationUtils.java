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
package com.ge.research.rite.autoGsn.utils;

import com.ge.research.rite.autoGsn.constants.PrefixedPatternQueries;
import com.ge.research.rite.autoGsn.constants.QueryTemplates;
import com.ge.research.rite.do178c.structures.SparqlConnectionInfo;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author Saswata Paul
 *     <p>Contains functions to create json files for different types of queries
 */
public class QueryGenerationUtils {

    /**
     * Creates a query for binary class connections
     *
     * @param ontInfo
     * @param sourceClassId
     * @param propertyId
     * @param targetClassId
     * @param outDir
     * @return
     */
    public static String binaryConnectionQuery(
            JSONObject ontInfo,
            String sourceClassId,
            String propertyId,
            String targetClassId,
            String outDir,
            SparqlConnectionInfo connPars) {
        /**
         * Place-holders to edit in the template
         *
         * <p>SPARQLCONN_NAME_PH
         *
         * <p>MODEL_URL_PH MODEL_GRAPH_PH
         *
         * <p>DATA_URL_PH DATA_GRAPH_PH
         *
         * <p>TARGET_CLASS_ID_PH TARGET_CLASS_URI_PH
         *
         * <p>SOURCE_CLASS_ID_PH SOURCE_CLASS_URI_PH
         *
         * <p>SOURCE_CLASS_PROPERTY_RANGE_PH : List of strings in "quotes" separated by ','
         *
         * <p>SOURCE_CLASS_PROPERTY_URI_PH
         */

        // Using default values for the below for now
        // TODO: Add function parameters for these values
        String sparqlConnName = "synthesis";
        String modelUrl = connPars.getModelUrl();
        String modelGraph = connPars.getModelGraph();
        String dataUrl = connPars.getDataUrl();
        String dataGraph = connPars.getDataGraph();

        // Obtain the other values
        String targetClassUri = OntologyJsonObjUtils.getClassURI(ontInfo, targetClassId);
        String sourceClassUri = OntologyJsonObjUtils.getClassURI(ontInfo, sourceClassId);

        List<String> propertyInfo =
                OntologyJsonObjUtils.getPropertyRangeURIs(ontInfo, sourceClassId, propertyId);

        String propertyUri =
                propertyInfo.get(0).toString(); // For some reason, throws error without toString()

        String propertyRange = "";

        // ones with commas
        for (int i = 1; i < (propertyInfo.size() - 1); i++) {
            propertyRange = propertyRange + "\"" + propertyInfo.get(i) + "\",\n";
        }
        // last one without comma
        propertyRange = propertyRange + "\"" + propertyInfo.get(propertyInfo.size() - 1) + "\"\n";

        try {

            String templateString = QueryTemplates.getBinary();
            // replace placeholders in template string
            templateString = templateString.replace("SPARQLCONN_NAME_PH", sparqlConnName);
            templateString = templateString.replace("MODEL_URL_PH", modelUrl);
            templateString = templateString.replace("MODEL_GRAPH_PH", modelGraph);
            templateString = templateString.replace("DATA_URL_PH", dataUrl);
            templateString = templateString.replace("DATA_GRAPH_PH", dataGraph);
            templateString = templateString.replace("TARGET_CLASS_ID_PH", targetClassId);
            templateString = templateString.replace("TARGET_CLASS_URI_PH", targetClassUri);
            templateString = templateString.replace("SOURCE_CLASS_ID_PH", sourceClassId);
            templateString = templateString.replace("SOURCE_CLASS_URI_PH", sourceClassUri);
            templateString = templateString.replace("SOURCE_CLASS_PROPERTY_URI_PH", propertyUri);
            templateString =
                    templateString.replace("SOURCE_CLASS_PROPERTY_RANGE_PH", propertyRange);

            //            System.out.println(templateString);

            // create new Json query file
            String newJsonFilePath =
                    outDir + sourceClassId + "_" + propertyId + "_" + targetClassId + ".json";
            CustomStringUtils.writeStrAsNewFile(templateString, newJsonFilePath);

            return newJsonFilePath;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR: Could not read binary connection query template file!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates query for binary connection with descriptions
     *
     * @param ontInfo
     * @param sourceClassId
     * @param propertyId
     * @param targetClassId
     * @param outDir
     * @return
     */
    public static String binaryConnectionQueryWithDesc(
            JSONObject ontInfo,
            String sourceClassId,
            String propertyId,
            String targetClassId,
            String outDir,
            SparqlConnectionInfo connPars) {
        /**
         * Place-holders to edit in the template
         *
         * <p>SPARQLCONN_NAME_PH
         *
         * <p>MODEL_URL_PH MODEL_GRAPH_PH
         *
         * <p>DATA_URL_PH DATA_GRAPH_PH
         *
         * <p>TARGET_CLASS_ID_PH TARGET_CLASS_URI_PH
         *
         * <p>SOURCE_CLASS_ID_PH SOURCE_CLASS_URI_PH
         *
         * <p>SOURCE_CLASS_PROPERTY_RANGE_PH : List of strings in "quotes" separated by ','
         *
         * <p>SOURCE_CLASS_PROPERTY_URI_PH
         */

        // Using default values for the below for now
        // TODO: Add function parameters for these values
        String sparqlConnName = "synthesis";
        String modelUrl = connPars.getModelUrl();
        String modelGraph = connPars.getModelGraph();
        String dataUrl = connPars.getDataUrl();
        String dataGraph = connPars.getDataGraph();

        // Obtain the other values
        String targetClassUri = OntologyJsonObjUtils.getClassURI(ontInfo, targetClassId);
        String sourceClassUri = OntologyJsonObjUtils.getClassURI(ontInfo, sourceClassId);

        List<String> propertyInfo =
                OntologyJsonObjUtils.getPropertyRangeURIs(ontInfo, sourceClassId, propertyId);

        String propertyUri =
                propertyInfo.get(0).toString(); // For some reason, throws error without toString()

        String propertyRange = "";

        // ones with commas
        for (int i = 1; i < (propertyInfo.size() - 1); i++) {
            propertyRange = propertyRange + "\"" + propertyInfo.get(i) + "\",\n";
        }
        // last one without comma
        propertyRange = propertyRange + "\"" + propertyInfo.get(propertyInfo.size() - 1) + "\"\n";

        try {

            String templateString = QueryTemplates.getBinarywithdescription();

            // replace placeholders in template string
            templateString = templateString.replace("SPARQLCONN_NAME_PH", sparqlConnName);
            templateString = templateString.replace("MODEL_URL_PH", modelUrl);
            templateString = templateString.replace("MODEL_GRAPH_PH", modelGraph);
            templateString = templateString.replace("DATA_URL_PH", dataUrl);
            templateString = templateString.replace("DATA_GRAPH_PH", dataGraph);
            templateString = templateString.replace("TARGET_CLASS_ID_PH", targetClassId);
            templateString = templateString.replace("TARGET_CLASS_URI_PH", targetClassUri);
            templateString = templateString.replace("SOURCE_CLASS_ID_PH", sourceClassId);
            templateString = templateString.replace("SOURCE_CLASS_URI_PH", sourceClassUri);
            templateString = templateString.replace("SOURCE_CLASS_PROPERTY_URI_PH", propertyUri);
            templateString =
                    templateString.replace("SOURCE_CLASS_PROPERTY_RANGE_PH", propertyRange);

            //            System.out.println(templateString);

            // create new Json query file
            String newJsonFilePath =
                    outDir + sourceClassId + "_" + propertyId + "_" + targetClassId + ".json";
            CustomStringUtils.writeStrAsNewFile(templateString, newJsonFilePath);

            return newJsonFilePath;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR: Could not read binary connection query template file!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create the queirs for getting GSN patterns data by filling in the templates with connection
     * info
     *
     * @param key
     * @param outDir
     * @param connPars
     * @return
     */
    public static String patternQuery(String key, String outDir, SparqlConnectionInfo connPars) {

        // Using default values for the below for now
        String modelUrl = connPars.getModelUrl();
        String modelGraph = connPars.getModelGraph();
        String dataUrl = connPars.getDataUrl();
        String dataGraph = connPars.getDataGraph();

        try {

            String templateString = "";

            if (key.equalsIgnoreCase("gsn_get_goal_patterns")) {
                templateString = PrefixedPatternQueries.getGsnGetGoalPatterns();
            } else if (key.equalsIgnoreCase("gsn_get_strategy_patterns")) {
                templateString = PrefixedPatternQueries.getGsnGetStrategyPatterns();
            } else if (key.equalsIgnoreCase("gsn_get_evidence_patterns")) {
                templateString = PrefixedPatternQueries.getGsnGetEvidencePatterns();
            }

            // replace placeholders in template string
            templateString = templateString.replace("MODEL_URL_PH", modelUrl);
            templateString = templateString.replace("MODEL_GRAPH_PH", modelGraph);
            templateString = templateString.replace("DATA_URL_PH", dataUrl);
            templateString = templateString.replace("DATA_GRAPH_PH", dataGraph);

            // create new Json query file
            String newJsonFilePath = outDir + key + ".json";
            CustomStringUtils.writeStrAsNewFile(templateString, newJsonFilePath);

            return newJsonFilePath;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR: Could not read prefixed pattern query template file!");
            e.printStackTrace();
            return null;
        }
    }
}
