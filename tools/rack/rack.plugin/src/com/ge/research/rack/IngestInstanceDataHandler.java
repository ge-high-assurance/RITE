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
package com.ge.research.rack;

import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.rack.views.ViewUtils;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import com.ge.research.semtk.sparqlX.client.SparqlQueryClient;
import com.opencsv.CSVReader;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
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

public class IngestInstanceDataHandler extends AbstractHandler {
	private static String MANIFEST_SUCCESS = "Manifest Ingestion Completed Successfully";
	private static String MANIFEST_CANCELED = "Manifest Ingestion Stopped";
	private static String MANIFEST_FAILED = "Manifest Ingestion Failed";
	private static String manifestPath = "";

	private static enum IngestionStatus {
		FAILED, CANCELED, DONE
	};

	private IngestionStatus uploadModelFromYAML(String yamlPath, IProgressMonitor monitor) throws Exception {
		if (monitor.isCanceled()) {
			return IngestionStatus.CANCELED;
		}
		File file = new File(yamlPath);
		if (!file.exists()) {
			return IngestionStatus.FAILED;
		}
		String dir = file.getParent();
		Object oYaml = null;

		try {
			oYaml = ProjectUtils.readYaml(yamlPath);
		} catch (Exception e) {
			RackConsole.getConsole().error("Unable to read " + dir + "/import.yaml");
		}
		if (oYaml == null || !(oYaml instanceof Map)) {
			RackConsole.getConsole().error("Ill formed " + dir + "/import.yaml, please check");
			return IngestionStatus.FAILED;
		}

		HashMap<String, Object> yamlMap = (HashMap) oYaml;

		if (!yamlMap.containsKey("files")) {
			RackConsole.getConsole().warning(dir + "/import.yaml contains no owl files to upload, done");
			return IngestionStatus.FAILED;
		}
		Object oList = yamlMap.get("files");

		if (!(oList instanceof List)) {
			RackConsole.getConsole().error("owl files in" + dir + "/import.yaml is ill formed, please check");
			return IngestionStatus.FAILED;
		}

		ArrayList<String> steps = (ArrayList<String>) oList;

		for (String owl : steps) {
			if (monitor.isCanceled()) {
				return IngestionStatus.CANCELED;
			}
			String owlPath = Paths.get(dir + "/" + owl).normalize().toString();
			File owlFile = new File(owlPath);
			if (!owlFile.exists()) {
				RackConsole.getConsole().error("Cannot find owl file: " + owlPath);
				return IngestionStatus.FAILED;
			}
			try {
				RackConsole.getConsole().print("Uploading owl file " + owlFile.getName() + "... ");
				SparqlQueryClient qAuthClient = ConnectionUtil.getOntologyUploadClient();
				qAuthClient.uploadOwl(owlFile);
				RackConsole.getConsole().printOK();
			} catch (Exception e) {
				RackConsole.getConsole().printFAIL();
				RackConsole.getConsole().error(
						"Ontology processing/upload failed, make sure you are connected to Sparql and have configured the correct Core and Overlay Projects");
				return IngestionStatus.FAILED;
			}
		}
		return IngestionStatus.DONE;
	}

	private IngestionStatus uploadDataFromYAML(String yamlPath, IProgressMonitor monitor) throws Exception {

		if (monitor.isCanceled()) {
			return IngestionStatus.CANCELED;
		}
		File file = new File(yamlPath);
		if (!file.exists()) {
			return IngestionStatus.FAILED;
		}
		String dir = file.getParent();
		Object oYaml = null;
		try {
			oYaml = ProjectUtils.readYaml(yamlPath);
		} catch (Exception e) {
			RackConsole.getConsole().error("Unable to read " + dir + "/import.yaml");
			return IngestionStatus.FAILED;
		}
		if (oYaml == null || !(oYaml instanceof Map)) {
			RackConsole.getConsole().error("Ill formed " + dir + "/import.yaml, please check");
			return IngestionStatus.FAILED;
		}

		HashMap<String, Object> yamlMap = (HashMap) oYaml;

		if (!yamlMap.containsKey("ingestion-steps")) {
			RackConsole.getConsole().warning(dir + "/import.yaml contains no ingestion step, done");
			return IngestionStatus.FAILED;
		}

		Object oList = yamlMap.get("ingestion-steps");
		if (!(oList instanceof List)) {
			RackConsole.getConsole().error("ingestion-steps in" + dir + "/import.yaml is ill formed, please check");
			return IngestionStatus.FAILED;
		}

		String dataGraph = "";
		List<String> dataGraphs = new ArrayList<>();
		if (yamlMap.containsKey("data-graph")) {
			Object oDataGraph = yamlMap.get("data-graph");
			if (oDataGraph instanceof String && !((String) oDataGraph).isEmpty()) {
				dataGraph = (String) oDataGraph;
			} else {
				dataGraph = RackPreferencePage.getDefaultDataGraph();
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
					RackConsole.getConsole()
							.error("Cannot ingest data for nodegroup: " + ingestionId + ", csv file missing");
					return IngestionStatus.FAILED;
					// continue;
				}

				ArrayList<String> colsList = new ArrayList<>();
				ArrayList<ArrayList<String>> tabData = new ArrayList<>();

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
							tabData.add(itemsList);
						}
					}
					csvReader.close();
					reader.close();

				} catch (Exception e) {
					String message = "Problem occured when reading lines for :" + dir + "/" + csvFileName;
					RackConsole.getConsole().error(message);
					return IngestionStatus.FAILED;
				}

				if (colsList.size() == 0 || tabData.size() == 0) {
					return IngestionStatus.DONE;
				}
				IngestionStatus status = uploadInstanceDataCSV(ingestionId, colsList, tabData, bUriIngestion,
						csvFile.getAbsolutePath(), dataGraph, dataGraphs);
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
					RackConsole.getConsole().error("Cannot find owl file: " + owlPath);
					continue;
				}
				try {
					SparqlQueryClient qAuthClient = ConnectionUtil.getOntologyUploadClient();
					qAuthClient.uploadOwl(owl);
				} catch (Exception e) {
					RackConsole.getConsole().error(
							"Ontology processing/upload failed, make sure you are connected to Sparql and have configured the correct Core and Overlay Projects");
					return IngestionStatus.FAILED;
				}
			}
		}

		if (monitor.isCanceled()) {
			RackConsole.getConsole().print(MANIFEST_CANCELED);
		}
		return IngestionStatus.DONE;
	}

	private IngestionStatus uploadNodegroupsFromYAML(String ngPath, IProgressMonitor monitor) throws Exception {
		if (monitor.isCanceled()) {
			return IngestionStatus.CANCELED;
		}
		File dir = new File(ngPath);
		if (!dir.exists()) {
			return IngestionStatus.FAILED;
		}
		File csvNgStore = new File(ngPath + "/store_data.csv");
		if (!csvNgStore.exists()) {
			RackConsole.getConsole()
					.error("Nodegroup csv store is missing, cannot ingest nodegroups specified in folder:" + ngPath);
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
			String message = "Problem occured when reading lines for :" + dir + "/" + csvNgStore.getName();
			RackConsole.getConsole().error(message);
			return IngestionStatus.FAILED;
		}

		if (colsList.size() == 0 || tabData.size() == 0) {
			return IngestionStatus.DONE;
		}

		IngestionStatus status = uploadNodegroupsFromCSV(ngPath, tabData, monitor);

		return status;
	}

	private IngestionStatus uploadNodegroupsFromCSV(String ngPath, ArrayList<ArrayList<String>> tabData,
			IProgressMonitor monitor) throws Exception {
		for (ArrayList<String> entry : tabData) {
			if (monitor.isCanceled()) {
				return IngestionStatus.CANCELED;
			}
			String nodegroupId = entry.get(0);
			String jsonFile = entry.get(3);
			File ngJson = new File(ngPath + "/" + jsonFile);
			if (!ngJson.exists()) {
				RackConsole.getConsole().error("json file for " + jsonFile + "missing in folder: " + ngPath);
				continue;
			}
			String jsonstr = FileUtils.readFileToString(ngJson, Charset.defaultCharset());
			SparqlGraphJson json = new SparqlGraphJson(jsonstr);
			NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
			SparqlConnection conn = ConnectionUtil.getSparqlConnection();
			json.setSparqlConn(conn);
			try {
				RackConsole.getConsole().print("Uploading nodegroup: " + nodegroupId + ".json ... ");

				ngClient.executeStoreNodeGroup(nodegroupId, entry.get(1), entry.get(2), json.getJson());
				RackConsole.getConsole().printOK();

			} catch (Exception e) {
				RackConsole.getConsole().printFAIL();
				RackConsole.getConsole().error("Upload of nodegroup: " + nodegroupId + ".json " + "failed");
				return IngestionStatus.FAILED;
			}
		}
		return IngestionStatus.DONE;
	}

	private IngestionStatus uploadDataFromManifestYAML(String yamlPath, IProgressMonitor monitor) throws Exception {

		if (monitor.isCanceled()) {
			return IngestionStatus.CANCELED;
		}
		File file = new File(yamlPath);
		if (!file.exists()) {
			return IngestionStatus.FAILED;
		}
		String dir = file.getParent();
		Object oYaml = null;
		try {
			oYaml = ProjectUtils.readYaml(yamlPath);
		} catch (Exception e) {
			RackConsole.getConsole().error("Unable to read " + dir + "/" + file.getName());
		}
		if (oYaml == null || !(oYaml instanceof Map)) {
			RackConsole.getConsole().error("Ill formed manifest at " + dir + "/" + file.getName() + ", please check");
			return IngestionStatus.FAILED;
		}

		HashMap<String, Object> yamlMap = (HashMap) oYaml;

		if (!yamlMap.containsKey("steps")) {
			RackConsole.getConsole().warning(dir + "/" + file.getName() + " contains no ingestion step, done");
			return IngestionStatus.FAILED;
		}

		Object oList = yamlMap.get("steps");
		if (!(oList instanceof List)) {
			RackConsole.getConsole().error("steps in" + dir + "/" + file.getName() + " is ill formed, please check");
			return IngestionStatus.FAILED;
		}
		ArrayList<Map<String, Object>> steps = (ArrayList<Map<String, Object>>) oList;

		for (Map<String, Object> step : steps) {
			if (step.containsKey("data")) {
				String importDataYaml = (String) step.get("data");
				if (importDataYaml == null) {
					continue;
				}
				IngestionStatus status = uploadDataFromYAML(
						Paths.get(Paths.get(dir) + "/" + importDataYaml).normalize().toString(), monitor);
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

				IngestionStatus status = uploadModelFromYAML(
						Paths.get(Paths.get(dir) + "/" + owlModel).normalize().toString(), monitor);
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

				IngestionStatus status = uploadDataFromManifestYAML(
						Paths.get(Paths.get(dir) + "/" + subManifest).normalize().toString(), monitor);
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

				IngestionStatus status = uploadNodegroupsFromYAML(
						Paths.get(Paths.get(dir) + "/" + ngEntry).normalize().toString(), monitor);
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
		File ingestionYaml = new File(manifestPath);
		if (!ingestionYaml.exists()) {
			RackConsole.getConsole().warning("No manifest.yaml found, nothing to ingest");
			return Status.CANCEL_STATUS;
		}
		try {
			IngestionStatus value = uploadDataFromManifestYAML(manifestPath, monitor);
			monitor.worked(100);

			switch (value) {
			case DONE:
				RackConsole.getConsole().print(MANIFEST_SUCCESS);
				break;
			case FAILED:
				RackConsole.getConsole().error(MANIFEST_FAILED);
				break;
			case CANCELED:
				RackConsole.getConsole().print(MANIFEST_CANCELED);

			}

		} catch (Exception e) {
			RackConsole.getConsole().error("Ingestion failed using manifest yaml");
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

	private IngestionStatus uploadInstanceDataCSV(String ingestionId, ArrayList<String> header,
			ArrayList<ArrayList<String>> body, boolean bUriIngestion, String path, String dataGraph,
			List<String> dataGraphs) {

		try {
			NodeGroupExecutionClient client = ConnectionUtil.getNGEClient();
			com.ge.research.semtk.resultSet.Table tab = new com.ge.research.semtk.resultSet.Table(header);
			for (ArrayList<String> row : body) {
				if (row.size() > 0 && !isEmpty(row)) {
					tab.addRow(row);
				}
			}
			String sCSV = tab.toCSVString();
			RackConsole.getConsole().print("Uploading " + ingestionId + "... ");

			if (bUriIngestion == false) {
				client.dispatchIngestFromCsvStringsByIdSync(ingestionId, sCSV,
						ConnectionUtil.getSparqlConnection(dataGraph, dataGraphs));

			} else {
				client.dispatchIngestFromCsvStringsByClassTemplateSync(ingestionId, "identifier", sCSV,
						ConnectionUtil.getSparqlConnection(dataGraph, dataGraphs));
			}
			RackConsole.getConsole().printOK();

		} catch (Exception e) {
			RackConsole.getConsole().printFAIL();
			RackConsole.getConsole().error("Upload of " + ingestionId + " failed, " + "CSV: " + path);
			return IngestionStatus.FAILED;
		}
		return IngestionStatus.DONE;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		TreePath[] paths = ((TreeSelection) selection).getPaths();
		if (paths.length == 1) {
			org.eclipse.core.internal.resources.File file = (org.eclipse.core.internal.resources.File) paths[0]
					.getLastSegment();
			if (file.getFileExtension().equals("yaml")) {
				manifestPath = file.getRawLocation().toFile().getAbsolutePath();
			}
		}
		Job job = new Job("Ingesting data into RACK") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ViewUtils.showProgressView();
				return ingestInstanceData(monitor);
			}
		};
		job.addJobChangeListener(new IJobChangeListener() {
			@Override
			public void done(IJobChangeEvent event) {
				job.cancel();
			}

			@Override
			public void awake(IJobChangeEvent event) {
			}

			@Override
			public void aboutToRun(IJobChangeEvent event) {
			}

			@Override
			public void sleeping(IJobChangeEvent event) {
			}

			@Override
			public void running(IJobChangeEvent event) {
				job.getState();
			}

			@Override
			public void scheduled(IJobChangeEvent event) {
			}
		});
		job.schedule();
		return null;
	}
}
