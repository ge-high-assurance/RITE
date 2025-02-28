/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2025, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite;

import com.ge.research.rite.utils.ConnectionUtil;
import com.ge.research.rite.utils.ErrorMessageUtil;
import com.ge.research.rite.utils.ProjectUtils;
import com.ge.research.rite.views.RackPreferencePage;
import com.ge.research.rite.views.ViewUtils;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.services.nodegroupStore.NgStore.StoredItemTypes;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import com.ge.research.semtk.sparqlX.client.SparqlQueryClient;
import com.opencsv.CSVReader;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngestInstanceDataHandler extends RiteHandler {
    private static volatile boolean isRunning = false;
    private static String MANIFEST_SUCCESS = "Manifest Ingestion Completed Successfully";
    private static String MANIFEST_CANCELED = "Manifest Ingestion Stopped";
    private static String MANIFEST_FAILED = "Manifest Ingestion Failed";
    private static String MANIFEST_IN_PROGRESS = "Another Manifest Import is in progress";
    private static String manifestPath = "";
    private static String NODEGROUP_LABEL = "Nodegroup";
    private static String REPORT_LABEL = "Report";
    private static List<String> dGraphs = new ArrayList<>();
    private static List<String> mGraphs = new ArrayList<>();
    private static List<String> dedupSteps = new ArrayList<>();

    private static enum IngestionStatus {
        FAILED,
        CANCELED,
        DONE
    };

    private IngestionStatus uploadModelFromYAML(String yamlPath, IProgressMonitor monitor)
            throws Exception {

        if (monitor.isCanceled()) {
            return IngestionStatus.CANCELED;
        }

        if (dedupSteps.contains(yamlPath)) {
            ErrorMessageUtil.warning("Skipping previously executed step at: " + yamlPath);
            return IngestionStatus.DONE;
        }

        dedupSteps.add(yamlPath);

        File file = new File(yamlPath);
        if (!file.exists()) {
            return IngestionStatus.FAILED;
        }
        String dir = file.getParent();
        Object oYaml = null;

        try {
            oYaml = ProjectUtils.readYaml(yamlPath);
        } catch (Exception e) {
            ErrorMessageUtil.error("Unable to read " + dir + "/import.yaml");
        }
        if (oYaml == null || !(oYaml instanceof Map)) {
            ErrorMessageUtil.error("Ill formed " + dir + "/import.yaml, please check");
            return IngestionStatus.FAILED;
        }

        HashMap<String, Object> yamlMap = (HashMap<String, Object>) oYaml;

        if (!yamlMap.containsKey("files")) {
            ErrorMessageUtil.warning(dir + "/import.yaml contains no owl files to upload, done");
            return IngestionStatus.FAILED;
        }
        Object oList = yamlMap.get("files");

        if (!(oList instanceof List)) {
            ErrorMessageUtil.error(
                    "owl files in" + dir + "/import.yaml is ill formed, please check");
            return IngestionStatus.FAILED;
        }

        ArrayList<String> steps = (ArrayList<String>) oList;
        String modelGraph = mGraphs.get(0);
        if (yamlMap.containsKey("model-graphs")) {
            Object oDataGraph = yamlMap.get("model-graphs");
            if (oDataGraph instanceof List) {
                if (((List) oDataGraph).size() > 1) {
                    ErrorMessageUtil.warning(
                            "We currently support ingesting only using a single model-graph");
                    // return IngestionStatus.FAILED;
                }
                modelGraph = ((List<String>) oDataGraph).get(0);
                if (modelGraph.isEmpty()) {
                    modelGraph = mGraphs.get(0);
                }
                // validate target graph against footprint
                if (!mGraphs.contains(modelGraph)) {
                    ErrorMessageUtil.error(
                            "Specified target graph " + modelGraph + " not declared in footprint");
                    ErrorMessageUtil.error("YAML file: " + yamlPath);
                    return IngestionStatus.FAILED;
                }
            }
        }

        for (String owl : steps) {
            if (monitor.isCanceled()) {
                return IngestionStatus.CANCELED;
            }
            String owlPath = Paths.get(dir + "/" + owl).normalize().toString();
            File owlFile = new File(owlPath);
            if (!owlFile.exists()) {
                ErrorMessageUtil.error("Cannot find owl file: " + owlPath);
                return IngestionStatus.FAILED;
            }

            try {

                ErrorMessageUtil.print(
                        "Uploading owl file "
                                + owlFile.getAbsolutePath()
                                + " to "
                                + modelGraph
                                + " ... ");
                SparqlQueryClient qAuthClient = ConnectionUtil.getOntologyUploadClient(modelGraph);
                qAuthClient.uploadOwl(owlFile);
                ErrorMessageUtil.printOK();
            } catch (Exception e) {
                ErrorMessageUtil.printFAIL();
                ErrorMessageUtil.error(e.getLocalizedMessage());
                ErrorMessageUtil.error(
                        "Ontology processing/upload failed, make sure you are connected to RACK or RACK-BOX instance");
                ErrorMessageUtil.error("Upload of owl failed, OWL: " + owlFile.getAbsolutePath());
                return IngestionStatus.FAILED;
            }
        }
        return IngestionStatus.DONE;
    }

    private IngestionStatus uploadDataFromYAML(String yamlPath, IProgressMonitor monitor)
            throws Exception {

        if (monitor.isCanceled()) {
            return IngestionStatus.CANCELED;
        }

        if (dedupSteps.contains(yamlPath)) {
            ErrorMessageUtil.warning("Skipping previously executed step at: " + yamlPath);
            return IngestionStatus.DONE;
        }

        dedupSteps.add(yamlPath);

        File file = new File(yamlPath);
        if (!file.exists()) {
            return IngestionStatus.FAILED;
        }
        String dir = file.getParent();
        Object oYaml = null;
        try {
            oYaml = ProjectUtils.readYaml(yamlPath);
        } catch (Exception e) {
            ErrorMessageUtil.error("Unable to read " + dir + "/import.yaml");
            return IngestionStatus.FAILED;
        }
        if (oYaml == null || !(oYaml instanceof Map)) {
            ErrorMessageUtil.error("Ill formed " + dir + "/import.yaml, please check");
            return IngestionStatus.FAILED;
        }

        HashMap<String, Object> yamlMap = (HashMap<String, Object>) oYaml;

        if (!yamlMap.containsKey("ingestion-steps")) {
            ErrorMessageUtil.warning(dir + "/import.yaml contains no ingestion step, done");
            return IngestionStatus.FAILED;
        }

        Object oList = yamlMap.get("ingestion-steps");
        if (!(oList instanceof List)) {
            ErrorMessageUtil.error(
                    "ingestion-steps in" + dir + "/import.yaml is ill formed, please check");
            return IngestionStatus.FAILED;
        }

        String dataGraph = dGraphs.get(0);
        String modelGraph = mGraphs.get(0);
        List<String> dataGraphs = new ArrayList<>();
        if (yamlMap.containsKey("data-graph")) {
            Object oDataGraph = yamlMap.get("data-graph");
            if (oDataGraph instanceof String && !((String) oDataGraph).isEmpty()) {
                dataGraph = (String) oDataGraph;
                // validate target graph against footprint
                if (!dGraphs.contains(dataGraph)) {
                    ErrorMessageUtil.error(
                            "Specified target graph " + dataGraph + " not declared in footprint");
                    ErrorMessageUtil.error("YAML file: " + yamlPath);
                    return IngestionStatus.FAILED;
                }
            }
        }

        if (yamlMap.containsKey("model-graphs")) {
            Object oDataGraph = yamlMap.get("model-graphs");
            if (oDataGraph instanceof List) {
                if (((List) oDataGraph).size() > 1) {
                    ErrorMessageUtil.warning(
                            "We currently support ingesting only using a single model-graph");
                    // return IngestionStatus.FAILED;
                }
                modelGraph = ((List<String>) oDataGraph).get(0);
                if (modelGraph.isEmpty()) {
                    modelGraph = mGraphs.get(0);
                }
                // validate target graph against footprint
                if (!mGraphs.contains(modelGraph)) {
                    ErrorMessageUtil.error(
                            "Specified target graph " + modelGraph + " not declared in footprint");
                    ErrorMessageUtil.error("YAML file: " + yamlPath);
                    return IngestionStatus.FAILED;
                }
            }
        }

        if (yamlMap.containsKey("extra-data-graphs")) {
            Object oGraphs = yamlMap.get("extra-data-graphs");
            if (oGraphs != null && oGraphs instanceof List) {
                dataGraphs = ((List) oGraphs);
            }
        }

        ArrayList<Map<String, Object>> steps = (ArrayList<Map<String, Object>>) oList;

        for (Map<String, Object> step : steps) {
            if (monitor.isCanceled()) {
                return IngestionStatus.CANCELED;
            }
            boolean bUriIngestion = false;
            String ingestionId = "";
            if (step.containsKey("csv")) {
                if (step.containsKey("class")) {
                    bUriIngestion = true;
                    ingestionId = (String) step.get("class");
                }
                if (step.containsKey("nodegroup")) {
                    bUriIngestion = false;
                    ingestionId = (String) step.get("nodegroup");
                }
                String csvFileName = (String) step.get("csv");
                if (ingestionId == null || csvFileName == null) {
                    continue;
                }
                File csvFile = new File(dir + "/" + csvFileName);
                if (!csvFile.exists()) {
                    ErrorMessageUtil.error(
                            "Cannot ingest data for nodegroup: "
                                    + ingestionId
                                    + ", csv file missing");
                    return IngestionStatus.FAILED;
                    // continue;

                }

                ArrayList<String> colsList = new ArrayList<>();
                ArrayList<ArrayList<String>> tabData = new ArrayList<>();

                if (dedupSteps.contains(csvFile.getAbsolutePath())) {
                    ErrorMessageUtil.warning(
                            "Skipping already processed file: " + csvFile.getAbsolutePath());
                    continue;
                } else {
                    dedupSteps.add(csvFile.getAbsolutePath());
                }

                try {

                    FileReader reader = new FileReader(csvFile.getAbsolutePath());
                    CSVReader csvReader = new CSVReader(reader);
                    String[] line = csvReader.readNext();

                    if (line != null) {
                        colsList = new ArrayList<>(Arrays.asList(line));
                    }
                    while (line != null) {
                        line = csvReader.readNext();
                        if (tabData == null) {
                            tabData = new ArrayList<>();
                        }
                        if (line != null) {
                            ArrayList<String> itemsList = new ArrayList<>(Arrays.asList(line));
                            ArrayList<String> itemsListTrimmed = new ArrayList<>();
                            for (String entry : itemsList) {
                                itemsListTrimmed.add(entry.trim());
                            }
                            tabData.add(itemsListTrimmed);
                        }
                    }
                    csvReader.close();
                    reader.close();

                } catch (Exception e) {
                    String message =
                            "Problem occured when reading lines for :" + dir + "/" + csvFileName;
                    ErrorMessageUtil.error(message);
                    return IngestionStatus.FAILED;
                }

                if (colsList.size() == 0 || tabData.size() == 0) {
                    return IngestionStatus.DONE;
                }
                IngestionStatus status =
                        uploadInstanceDataCSV(
                                ingestionId,
                                colsList,
                                tabData,
                                bUriIngestion,
                                csvFile.getAbsolutePath(),
                                modelGraph,
                                dataGraph,
                                dataGraphs);
                if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
                    return status;
                }
            }

            if (step.containsKey("owl")) {
                String owlFile = (String) step.get("owl");
                if (owlFile == null) {
                    continue;
                }

                String owlPath = dir + "/" + owlFile;
                File owl = new File(owlPath);
                if (!owl.exists()) {
                    ErrorMessageUtil.error("Cannot find owl file: " + owlPath);
                    continue;
                }
                final String dGraph = dataGraph;

                if (dedupSteps.contains(owl.getAbsolutePath())) {
                    ErrorMessageUtil.warning(
                            "Skipping already processed file: " + owl.getAbsolutePath());
                    continue;
                } else {
                    dedupSteps.add(owl.getAbsolutePath());
                }

                try {
                    ErrorMessageUtil.print(
                            "Uploading owl file "
                                    + owl.getAbsolutePath()
                                    + " to "
                                    + dGraph
                                    + " ... ");
                    SparqlQueryClient qAuthClient = ConnectionUtil.getOntologyUploadClient(dGraph);
                    qAuthClient.uploadOwl(owl);
                    ErrorMessageUtil.printOK();
                } catch (Exception e) {
                    ErrorMessageUtil.printFAIL();
                    ErrorMessageUtil.error("Upload of owl filed, OWL: " + owl.getAbsolutePath());
                    return IngestionStatus.FAILED;
                }
            }
        }

        if (monitor.isCanceled()) {
            ErrorMessageUtil.print(MANIFEST_CANCELED);
        }
        return IngestionStatus.DONE;
    }

    private IngestionStatus uploadNodegroupsFromYAML(String ngPath, IProgressMonitor monitor)
            throws Exception {
        if (monitor.isCanceled()) {
            return IngestionStatus.CANCELED;
        }

        if (dedupSteps.contains(ngPath)) {
            ErrorMessageUtil.warning("Skipping previously executed step at: " + ngPath);
            return IngestionStatus.DONE;
        }

        dedupSteps.add(ngPath);

        File dir = new File(ngPath);
        if (!dir.exists()) {
            return IngestionStatus.FAILED;
        }
        File csvNgStore = new File(ngPath + File.separator + "store_data.csv");
        if (!csvNgStore.exists()) {
            ErrorMessageUtil.error(
                    "Nodegroup csv store is missing, cannot ingest nodegroups specified in folder:"
                            + ngPath);
            ErrorMessageUtil.error("Store data CSV: " + csvNgStore.getAbsolutePath());
        }
        ArrayList<String> colsList = new ArrayList<>();
        ArrayList<ArrayList<String>> tabData = new ArrayList<>();
        try {

            FileReader reader = new FileReader(csvNgStore.getAbsolutePath());
            CSVReader csvReader = new CSVReader(reader);
            String[] line = csvReader.readNext();

            if (line != null) {
                colsList = new ArrayList<>(Arrays.asList(line));
            }
            while (line != null) {
                line = csvReader.readNext();
                if (tabData == null) {
                    tabData = new ArrayList<>();
                }
                if (line != null) {
                    ArrayList<String> itemsList = new ArrayList<>(Arrays.asList(line));
                    tabData.add(itemsList);
                }
            }
            csvReader.close();
            reader.close();

        } catch (Exception e) {
            String message =
                    "Problem occured when reading lines for :"
                            + dir
                            + File.separator
                            + csvNgStore.getName();
            ErrorMessageUtil.error(message);
            return IngestionStatus.FAILED;
        }

        if (colsList.size() == 0 || tabData.size() == 0) {
            return IngestionStatus.DONE;
        }

        IngestionStatus status = uploadNodegroupsFromCSV(ngPath, tabData, monitor);

        return status;
    }

    private IngestionStatus uploadNodegroupsFromCSV(
            String ngPath, ArrayList<ArrayList<String>> tabData, IProgressMonitor monitor)
            throws Exception {
    	int lineNumber = 1;
        for (ArrayList<String> entry : tabData) {
        	
            if (monitor.isCanceled()) {
                return IngestionStatus.CANCELED;
            }
            if(entry.size() == 0 || entry.size() < 4) {
            	ErrorMessageUtil.error("Malformed entry at line " + (lineNumber+1) + " in nodegroup store data at" + ngPath + File.separator + "store_data.csv");
            	continue;
            }
            
            final String nodegroupId = entry.get(0);
            final String jsonFile = entry.get(3);
            
            File ngJson = new File(ngPath + File.separator + jsonFile);
            if (!ngJson.exists()) {
                ErrorMessageUtil.error(
                        "json file for " + jsonFile + "missing in folder: " + ngPath);
                continue;
            }
            final String jsonstr = FileUtils.readFileToString(ngJson, Charset.defaultCharset());
            SparqlGraphJson json = new SparqlGraphJson(jsonstr);
            NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
            SparqlConnection conn = ConnectionUtil.getSparqlConnection();
            json.setSparqlConn(conn);
            final SparqlGraphJson json2 = json;

            try {

                // Check for a Report, else fallback to PreFabNodeGroup
            	if(entry.size() < 5) {
            		ErrorMessageUtil.warning("Missing nodegroup type for nodegroup: " + nodegroupId +" at line " + (lineNumber + 1) + " in " + ngPath + File.separator + "store_data.csv, assuming PrefabNodeGroup");
            	}
                String itemType = entry.size() < 5 ? StoredItemTypes.PrefabNodeGroup.toString() : entry.get(4);
                // Defaulting to PrefabNodeGroup
                StoredItemTypes storeItemType = StoredItemTypes.PrefabNodeGroup;
                String itemLabel = NODEGROUP_LABEL;
                
                if (itemType != null && itemType.equals(StoredItemTypes.Report.toString())) {
                    storeItemType = StoredItemTypes.Report;
                    itemLabel = REPORT_LABEL;
                }
                
                
                
                ErrorMessageUtil.print(
                        "Uploading " + itemLabel + ": " + nodegroupId + ".json ... ");
                ngClient.executeStoreItem(
                        nodegroupId, entry.get(1), entry.get(2), json2.getJson(), storeItemType);
                ErrorMessageUtil.printOK();

            } catch (Exception e) {
                ErrorMessageUtil.printFAIL();
                ErrorMessageUtil.error(e.getLocalizedMessage());
                ErrorMessageUtil.error(
                        "Upload of nodegroup "
                                + nodegroupId
                                + " failed, JSON:"
                                + ngJson.getAbsolutePath());
                return IngestionStatus.FAILED;
            }
            lineNumber++;
        }
        return IngestionStatus.DONE;
    }

    private IngestionStatus uploadDataFromManifestYAML(String yamlPath, IProgressMonitor monitor)
            throws Exception {

        if (dedupSteps.contains(yamlPath)) {
            ErrorMessageUtil.warning("Skipping previously executed step at: " + yamlPath);
            return IngestionStatus.DONE;
        }

        dedupSteps.add(yamlPath);

        if (monitor.isCanceled()) {
            return IngestionStatus.CANCELED;
        }
        File file = new File(yamlPath);
        if (!file.exists()) {
            return IngestionStatus.FAILED;
        }
        String dir = file.getParent();
        HashMap<String, Object> yamlMap = null;
        try {
            yamlMap = ProjectUtils.readYaml(yamlPath);
        } catch (Exception e) {
            ErrorMessageUtil.error("Unable to read " + dir + File.separator + file.getName());
        }
        if (yamlMap == null) {
            ErrorMessageUtil.error(
                    "Ill formed manifest at "
                            + dir
                            + File.separator
                            + file.getName()
                            + ", please check");
            ErrorMessageUtil.error("Check YAML: " + file.getAbsolutePath());
            return IngestionStatus.FAILED;
        }

        if (!yamlMap.containsKey("steps")) {
            ErrorMessageUtil.error(
                    dir + "/" + file.getName() + " contains no ingestion step, done");
            ErrorMessageUtil.error("Check YAML: " + file.getAbsolutePath());
            return IngestionStatus.FAILED;
        }

        Object oList = yamlMap.get("steps");
        if (!(oList instanceof List)) {
            ErrorMessageUtil.error(
                    "steps in" + dir + "/" + file.getName() + " is ill formed, please check");
            ErrorMessageUtil.error("Check YAML: " + file.getAbsolutePath());
            return IngestionStatus.FAILED;
        }
        ArrayList<Map<String, Object>> steps = (ArrayList<Map<String, Object>>) oList;

        for (Map<String, Object> step : steps) {
            if (step.containsKey("data")) {
                String importDataYaml = (String) step.get("data");
                if (importDataYaml == null) {
                    continue;
                }
                IngestionStatus status =
                        uploadDataFromYAML(
                                Paths.get(Paths.get(dir) + File.separator + importDataYaml)
                                        .normalize()
                                        .toString(),
                                monitor);
                if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
                    return status;
                }
                continue;
            }

            if (step.containsKey("model")) {
                String owlModel = (String) step.get("model");
                if (owlModel == null) {
                    continue;
                }

                IngestionStatus status =
                        uploadModelFromYAML(
                                Paths.get(Paths.get(dir) + File.separator + owlModel)
                                        .normalize()
                                        .toString(),
                                monitor);
                if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
                    return status;
                }
                continue;
            }

            if (step.containsKey("manifest")) {
                String subManifest = (String) step.get("manifest");
                if (subManifest == null) {
                    continue;
                }

                IngestionStatus status =
                        uploadDataFromManifestYAML(
                                Paths.get(Paths.get(dir) + File.separator + subManifest)
                                        .normalize()
                                        .toString(),
                                monitor);
                if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
                    return status;
                }
                continue;
            }

            if (step.containsKey("nodegroups")) {
                if (monitor.isCanceled()) {
                    return IngestionStatus.CANCELED;
                }
                String ngEntry = (String) step.get("nodegroups");
                if (ngEntry == null) {
                    continue;
                }

                IngestionStatus status =
                        uploadNodegroupsFromYAML(
                                Paths.get(Paths.get(dir) + File.separator + ngEntry)
                                        .normalize()
                                        .toString(),
                                monitor);
                if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
                    return status;
                }
                continue;
            }
        }
        return IngestionStatus.DONE;
    }

    private IStatus ingestInstanceData(IProgressMonitor monitor) {

        // get csv files and extract nodegroup ids
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        dedupSteps.clear();
        dGraphs.clear();
        mGraphs.clear();

        File ingestionYaml = new File(manifestPath);
        if (!ingestionYaml.exists()) {
            ErrorMessageUtil.warning("No manifest.yaml found, nothing to ingest");
            return Status.CANCEL_STATUS;
        }

        String dir = ingestionYaml.getParent();
        HashMap<String, Object> yamlMap = null;
        try {
            yamlMap = ProjectUtils.readYaml(ingestionYaml.getAbsolutePath());
        } catch (Exception e) {
            ErrorMessageUtil.error(
                    "Unable to read " + dir + File.separator + ingestionYaml.getName());
            return Status.CANCEL_STATUS;
        }
        if (yamlMap == null) {
            ErrorMessageUtil.error(
                    "Ill formed manifest at "
                            + dir
                            + "/"
                            + ingestionYaml.getName()
                            + ", please check");
            ErrorMessageUtil.error("Check YAML: " + ingestionYaml.getAbsolutePath());

            return Status.CANCEL_STATUS;
        }
        // read footprint
        if (yamlMap.containsKey("footprint")) {

            Object oFootprint = yamlMap.get("footprint");
            if (oFootprint instanceof Map) {
                Map oFootprintMap = (Map) oFootprint;
                if (!oFootprintMap.containsKey("data-graphs")) {
                    dGraphs.add(RackPreferencePage.getDefaultDataGraph());
                } else {
                    dGraphs = (List<String>) oFootprintMap.get("data-graphs");
                }

                if (!oFootprintMap.containsKey("model-graphs")) {
                    mGraphs.add(RackPreferencePage.getDefaultModelGraph());
                } else {
                    mGraphs = (List<String>) oFootprintMap.get("model-graphs");
                }
            }

        } else {
            dGraphs.add(RackPreferencePage.getDefaultDataGraph());
            mGraphs.add(RackPreferencePage.getDefaultModelGraph());
        }

        try {
            setRunning(true);
            IngestionStatus value = uploadDataFromManifestYAML(manifestPath, monitor);
            monitor.worked(100);
            switch (value) {
                case DONE:
                    ErrorMessageUtil.print(MANIFEST_SUCCESS);
                    break;
                case FAILED:
                    ErrorMessageUtil.error(MANIFEST_FAILED);
                    break;
                case CANCELED:
                    ErrorMessageUtil.print(MANIFEST_CANCELED);
            }

        } catch (Exception e) {
            ErrorMessageUtil.error(
                    "Ingestion failed, check YAML: " + ingestionYaml.getAbsolutePath());
        } finally {
            setRunning(false);
        }

        return Status.OK_STATUS;
    }

    private boolean isEmpty(ArrayList<String> row) {
        for (String entry : row) {
            if (entry != null && !entry.equals("")) {
                return false;
            }
        }
        return true;
    }

    private IngestionStatus uploadInstanceDataCSV(
            String ingestionId,
            ArrayList<String> header,
            ArrayList<ArrayList<String>> body,
            boolean bUriIngestion,
            String path,
            String modelGraph,
            String dataGraph,
            List<String> dataGraphs) {
        try {

            NodeGroupExecutionClient client = ConnectionUtil.getNGEClient();
            com.ge.research.semtk.resultSet.Table tab =
                    new com.ge.research.semtk.resultSet.Table(header);
            for (ArrayList<String> row : body) {
                if (row.size() > 0 && !isEmpty(row)) {
                    tab.addRow(row);
                }
            }
            String sCSV = tab.toCSVString();
            ErrorMessageUtil.print(
                    "Uploading CSV at " + path + " as class " + ingestionId + "... ");
            if (bUriIngestion == false) {
                client.dispatchIngestFromCsvStringsByIdSync(
                        ingestionId,
                        sCSV,
                        ConnectionUtil.getSparqlConnection(mGraphs, dataGraph, dataGraphs));

            } else {

                /*
                 * String error = client.execFromCsvUsingClassTemplate( ingestionId,
                 * "identifier", sCSV, ConnectionUtil.getSparqlConnection(mGraphs, dataGraph,
                 * dataGraphs).toJson().toJSONString(), false, "override");
                 *
                 */
                String status =
                        client.dispatchIngestFromCsvStringsByClassTemplateSync(
                                ingestionId,
                                "identifier",
                                sCSV,
                                ConnectionUtil.getSparqlConnection(mGraphs, dataGraph, dataGraphs));
            }
            ErrorMessageUtil.printOK();

            List<String> warnings = client.getWarnings();
            if (warnings != null) {
                for (String warning : warnings) {
                    ErrorMessageUtil.warning(warning);
                }
            }

        } catch (Exception e) {
            ErrorMessageUtil.printFAIL();
            ErrorMessageUtil.error(e.getLocalizedMessage());
            ErrorMessageUtil.error("Upload of " + ingestionId + " failed, " + "CSV: " + path);
            return IngestionStatus.FAILED;
        }

        return IngestionStatus.DONE;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        super.execute(event);
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        TreePath[] paths = ((TreeSelection) selection).getPaths();
        if (paths.length == 1) {
            org.eclipse.core.internal.resources.File file =
                    (org.eclipse.core.internal.resources.File) paths[0].getLastSegment();
            if (file.getFileExtension().equals("yaml")) {
                manifestPath = file.getRawLocation().toFile().getAbsolutePath();
            }
        }
        Job job =
                new Job("Ingesting data into RACK") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        ViewUtils.showProgressView();

                        if (isRunning()) {
                            ErrorMessageUtil.error(MANIFEST_IN_PROGRESS);
                            return Status.CANCEL_STATUS;
                        }

                        return ingestInstanceData(monitor);
                    }
                };
        job.addJobChangeListener(
                new IJobChangeListener() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        job.cancel();
                    }

                    @Override
                    public void awake(IJobChangeEvent event) {}

                    @Override
                    public void aboutToRun(IJobChangeEvent event) {}

                    @Override
                    public void sleeping(IJobChangeEvent event) {}

                    @Override
                    public void running(IJobChangeEvent event) {
                        job.getState();
                    }

                    @Override
                    public void scheduled(IJobChangeEvent event) {}
                });
        job.schedule();
        return null;
    }

    private static synchronized void setRunning(boolean status) {
        isRunning = status;
    }

    public static synchronized boolean isRunning() {
        return isRunning;
    }
}
