package ingest;

import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import com.ge.research.semtk.sparqlX.client.SparqlQueryClient;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public class RackIngest {
	public static void main(String args[]) {

		manifestPath = args[0];
		version = (args[1] == null) ? "" : args[1];
		ingestInstanceData();
		System.out.println("Done");
		
	}

	public static String getDefaultModelGraph() {
		return "http://rack001/model" + version;
	}

	public static String getDefaultDataGraph() {
		return "http://rack001/data" + version;

	}

	public static HashMap<String, Object> readYaml(String path) throws Exception {
		FileInputStream inputStream = new FileInputStream(new File(path));
		Yaml yaml = new Yaml();
		return yaml.<HashMap<String, Object>>load(inputStream);
	}
	private static String version = "";
	private static volatile boolean isRunning = false;
	private static String MANIFEST_SUCCESS = "Manifest Ingestion Completed Successfully";
	private static String MANIFEST_CANCELED = "Manifest Ingestion Stopped";
	private static String MANIFEST_FAILED = "Manifest Ingestion Failed";
	private static String MANIFEST_IN_PROGRESS = "Another Manifest Import is in progress";
	private static String manifestPath = "";
	private static List<String> dGraphs = new ArrayList<>();
	private static List<String> mGraphs = new ArrayList<>();
	private static List<String> dedupSteps = new ArrayList<>();

	private static enum IngestionStatus {
		FAILED, CANCELED, DONE
	};

	private static IngestionStatus uploadModelFromYAML(String yamlPath) throws Exception {

		if (dedupSteps.contains(yamlPath)) {
			System.out.println("Skipping previously executed step at: " + yamlPath);
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
			oYaml = readYaml(yamlPath);
		} catch (Exception e) {
			System.out.println("Unable to read " + dir + "/import.yaml");
		}
		if (oYaml == null || !(oYaml instanceof Map)) {
			System.out.println("Ill formed " + dir + "/import.yaml, please check");
			return IngestionStatus.FAILED;
		}

		HashMap<String, Object> yamlMap = (HashMap<String, Object>) oYaml;

		if (!yamlMap.containsKey("files")) {
			System.out.println(dir + "/import.yaml contains no owl files to upload, done");
			return IngestionStatus.FAILED;
		}
		Object oList = yamlMap.get("files");

		if (!(oList instanceof List)) {
			System.out.println("owl files in" + dir + "/import.yaml is ill formed, please check");
			return IngestionStatus.FAILED;
		}

		ArrayList<String> steps = (ArrayList<String>) oList;
		String modelGraph = mGraphs.get(0);
		if (yamlMap.containsKey("model-graphs")) {
			Object oDataGraph = yamlMap.get("model-graphs");
			if (oDataGraph instanceof List) {
				if (((List) oDataGraph).size() > 1) {
					System.out.println("We currently support ingesting only using a single model-graph");
					// return IngestionStatus.FAILED;
				}
				modelGraph = ((List<String>) oDataGraph).get(0);
				if (modelGraph.isEmpty()) {
					modelGraph = mGraphs.get(0);
				}
				// validate target graph against footprint
				if (!mGraphs.contains(modelGraph)) {
					System.out.println("Specified target graph " + modelGraph + " not declared in footprint");
					System.out.println("YAML file: " + yamlPath);
					return IngestionStatus.FAILED;
				}
			}
		}

		for (String owl : steps) {
			String owlPath = Paths.get(dir + "/" + owl).normalize().toString();
			File owlFile = new File(owlPath);
			if (!owlFile.exists()) {
				System.out.println("Cannot find owl file: " + owlPath);
				return IngestionStatus.FAILED;
			}

			try {

				System.out.println("Uploading owl file " + owlFile.getAbsolutePath() + " to " + modelGraph + " ... ");
				SparqlQueryClient qAuthClient = ConnectionUtil.getOntologyUploadClient(modelGraph);
				qAuthClient.uploadOwl(owlFile);
				System.out.println("OK");
			} catch (Exception e) {
				System.out.println("FAIL");
				System.out.println(
						"Ontology processing/upload failed, make sure you are connected to RACK or RACK-BOX instance");
				System.out.println("Upload of owl failed, OWL: " + owlFile.getAbsolutePath());
				return IngestionStatus.FAILED;
			}
		}
		return IngestionStatus.DONE;
	}

	private static IngestionStatus uploadDataFromYAML(String yamlPath) throws Exception {

		if (dedupSteps.contains(yamlPath)) {
			System.out.println("Skipping previously executed step at: " + yamlPath);
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
			oYaml = readYaml(yamlPath);
		} catch (Exception e) {
			System.out.println("Unable to read " + dir + "/import.yaml");
			return IngestionStatus.FAILED;
		}
		if (oYaml == null || !(oYaml instanceof Map)) {
			System.out.println("Ill formed " + dir + "/import.yaml, please check");
			return IngestionStatus.FAILED;
		}

		HashMap<String, Object> yamlMap = (HashMap<String, Object>) oYaml;

		if (!yamlMap.containsKey("ingestion-steps")) {
			System.out.println(dir + "/import.yaml contains no ingestion step, done");
			return IngestionStatus.FAILED;
		}

		Object oList = yamlMap.get("ingestion-steps");
		if (!(oList instanceof List)) {
			System.out.println("ingestion-steps in" + dir + "/import.yaml is ill formed, please check");
			return IngestionStatus.FAILED;
		}

		String dataGraph = dGraphs.get(0);
		String modelGraph = mGraphs.get(0);
		List<String> dataGraphs = new ArrayList<>();
		if (yamlMap.containsKey("data-graph")) {
			Object oDataGraph = yamlMap.get("data-graph");
			if (oDataGraph instanceof String && !((String) oDataGraph).isEmpty()) {
				dataGraph = (String) oDataGraph + version;
				// validate target graph against footprint
				if (!dGraphs.contains(dataGraph)) {
					System.out.println("Specified target graph " + dataGraph + " not declared in footprint");
					System.out.println("YAML file: " + yamlPath);
					return IngestionStatus.FAILED;
				}
			}
		}

		if (yamlMap.containsKey("model-graphs")) {
			Object oDataGraph = yamlMap.get("model-graphs");
			if (oDataGraph instanceof List) {
				if (((List) oDataGraph).size() > 1) {
					System.out.println("We currently support ingesting only using a single model-graph");
					// return IngestionStatus.FAILED;
				}
				modelGraph = ((List<String>) oDataGraph).get(0) + version;
				if (modelGraph.isEmpty()) {
					modelGraph = mGraphs.get(0);
				}
				// validate target graph against footprint
				if (!mGraphs.contains(modelGraph)) {
					System.out.println("Specified target graph " + modelGraph + " not declared in footprint");
					System.out.println("YAML file: " + yamlPath);
					return IngestionStatus.FAILED;
				}
			}
		}

		if (yamlMap.containsKey("extra-data-graphs")) {
			Object oGraphs = yamlMap.get("extra-data-graphs");
			if (oGraphs != null && oGraphs instanceof List) {
				dataGraphs = ((List) oGraphs);
				List<String> dataGraphs2 = new ArrayList<String>();
				for(String graph : dataGraphs) {
					dataGraphs2.add(graph + version);
				}
				dataGraphs = dataGraphs2;
			}
		}

		ArrayList<Map<String, Object>> steps = (ArrayList<Map<String, Object>>) oList;

		for (Map<String, Object> step : steps) {
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
					System.out.println("Cannot ingest data for nodegroup: " + ingestionId + ", csv file missing");
					return IngestionStatus.FAILED;
					// continue;

				}

				ArrayList<String> colsList = new ArrayList<>();
				ArrayList<ArrayList<String>> tabData = new ArrayList<>();

				if (dedupSteps.contains(csvFile.getAbsolutePath())) {
					System.out.println("Skipping already processed file: " + csvFile.getAbsolutePath());
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
							tabData.add(itemsList);
						}
					}
					csvReader.close();
					reader.close();

				} catch (Exception e) {
					String message = "Problem occured when reading lines for :" + dir + "/" + csvFileName;
					System.out.println(message);
					return IngestionStatus.FAILED;
				}

				if (colsList.size() == 0 || tabData.size() == 0) {
					return IngestionStatus.DONE;
				}
				IngestionStatus status = uploadInstanceDataCSV(ingestionId, colsList, tabData, bUriIngestion,
						csvFile.getAbsolutePath(), modelGraph, dataGraph, dataGraphs);
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
					System.out.println("Cannot find owl file: " + owlPath);
					continue;
				}
				final String dGraph = dataGraph;

				if (dedupSteps.contains(owl.getAbsolutePath())) {
					System.out.println("Skipping already processed file: " + owl.getAbsolutePath());
					continue;
				} else {
					dedupSteps.add(owl.getAbsolutePath());
				}

				try {
					System.out.println("Uploading owl file " + owl.getAbsolutePath() + " to " + dGraph + " ... ");
					SparqlQueryClient qAuthClient = ConnectionUtil.getOntologyUploadClient(dGraph);
					qAuthClient.uploadOwl(owl);
					System.out.println("OK");
				} catch (Exception e) {
					System.out.println("FAIL");
					System.out.println("Upload of owl filed, OWL: " + owl.getAbsolutePath());
					return IngestionStatus.FAILED;
				}
			}
		}

		return IngestionStatus.DONE;
	}

	private static IngestionStatus uploadNodegroupsFromYAML(String ngPath) throws Exception {

		if (dedupSteps.contains(ngPath)) {
			System.out.println("Skipping previously executed step at: " + ngPath);
			return IngestionStatus.DONE;
		}

		dedupSteps.add(ngPath);

		File dir = new File(ngPath);
		if (!dir.exists()) {
			return IngestionStatus.FAILED;
		}
		File csvNgStore = new File(ngPath + "/store_data.csv");
		if (!csvNgStore.exists()) {
			System.out
					.println("Nodegroup csv store is missing, cannot ingest nodegroups specified in folder:" + ngPath);
			System.out.println("Store data CSV: " + csvNgStore.getAbsolutePath());
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
			System.out.println(message);
			return IngestionStatus.FAILED;
		}

		if (colsList.size() == 0 || tabData.size() == 0) {
			return IngestionStatus.DONE;
		}

		IngestionStatus status = uploadNodegroupsFromCSV(ngPath, tabData);

		return status;
	}

	private static IngestionStatus uploadNodegroupsFromCSV(String ngPath, ArrayList<ArrayList<String>> tabData)
			throws Exception {
		for (ArrayList<String> entry : tabData) {
			final String nodegroupId = entry.get(0);
			final String jsonFile = entry.get(3);
			File ngJson = new File(ngPath + "/" + jsonFile);
			if (!ngJson.exists()) {
				System.out.println("json file for " + jsonFile + "missing in folder: " + ngPath);
				continue;
			}
			final String jsonstr = FileUtils.readFileToString(ngJson, Charset.defaultCharset());
			SparqlGraphJson json = new SparqlGraphJson(jsonstr);
			NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
			SparqlConnection conn = ConnectionUtil.getSparqlConnection();
			json.setSparqlConn(conn);
			final SparqlGraphJson json2 = json;

			try {
				System.out.println("Uploading nodegroup: " + nodegroupId + ".json ... ");
				ngClient.executeStoreNodeGroup(nodegroupId, entry.get(1), entry.get(2), json2.getJson());
				System.out.println("OK");

			} catch (Exception e) {
				System.out.println("FAIL");
				System.out.println(e.getLocalizedMessage());
				System.out.println("Upload of nodegroup" + nodegroupId + " failed, JSON:" + ngJson.getAbsolutePath());
				return IngestionStatus.FAILED;
			}
		}
		return IngestionStatus.DONE;
	}

	private static IngestionStatus uploadDataFromManifestYAML(String yamlPath) throws Exception {

		if (dedupSteps.contains(yamlPath)) {
			System.out.println("Skipping previously executed step at: " + yamlPath);
			return IngestionStatus.DONE;
		}

		dedupSteps.add(yamlPath);

		File file = new File(yamlPath);
		if (!file.exists()) {
			return IngestionStatus.FAILED;
		}
		String dir = file.getParent();
		HashMap<String, Object> yamlMap = null;
		try {
			yamlMap = readYaml(yamlPath);
		} catch (Exception e) {
			System.out.println("Unable to read " + dir + "/" + file.getName());
		}
		if (yamlMap == null) {
			System.out.println("Ill formed manifest at " + dir + "/" + file.getName() + ", please check");
			System.out.println("Check YAML: " + file.getAbsolutePath());
			return IngestionStatus.FAILED;
		}

		if (!yamlMap.containsKey("steps")) {
			System.out.println(dir + "/" + file.getName() + " contains no ingestion step, done");
			System.out.println("Check YAML: " + file.getAbsolutePath());
			return IngestionStatus.FAILED;
		}

		Object oList = yamlMap.get("steps");
		if (!(oList instanceof List)) {
			System.out.println("steps in" + dir + "/" + file.getName() + " is ill formed, please check");
			System.out.println("Check YAML: " + file.getAbsolutePath());
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
						Paths.get(Paths.get(dir) + "/" + importDataYaml).normalize().toString());
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
						Paths.get(Paths.get(dir) + "/" + owlModel).normalize().toString());
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
						Paths.get(Paths.get(dir) + "/" + subManifest).normalize().toString());
				if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
					return status;
				}
				continue;
			}

			if (step.containsKey("nodegroups")) {
				String ngEntry = (String) step.get("nodegroups");
				if (ngEntry == null) {
					continue;
				}

				IngestionStatus status = uploadNodegroupsFromYAML(
						Paths.get(Paths.get(dir) + "/" + ngEntry).normalize().toString());
				if (status == IngestionStatus.FAILED || status == IngestionStatus.CANCELED) {
					return status;
				}
				continue;
			}
		}
		return IngestionStatus.DONE;
	}

	private static void ingestInstanceData() {

		// get csv files and extract nodegroup ids

		dedupSteps.clear();
		dGraphs.clear();
		mGraphs.clear();

		File ingestionYaml = new File(manifestPath);
		if (!ingestionYaml.exists()) {
			System.out.println("No manifest.yaml found, nothing to ingest");
		}

		String dir = ingestionYaml.getParent();
		HashMap<String, Object> yamlMap = null;
		try {
			yamlMap = readYaml(ingestionYaml.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Unable to read " + dir + "/" + ingestionYaml.getName());
		}
		if (yamlMap == null) {
			System.out.println("Ill formed manifest at " + dir + "/" + ingestionYaml.getName() + ", please check");
			System.out.println("Check YAML: " + ingestionYaml.getAbsolutePath());

		}
		// read footprint
		if (yamlMap.containsKey("footprint")) {

			Object oFootprint = yamlMap.get("footprint");
			if (oFootprint instanceof Map) {
				Map oFootprintMap = (Map) oFootprint;
				if (!oFootprintMap.containsKey("data-graphs")) {
					dGraphs.add(getDefaultDataGraph());
				} else {
					dGraphs = (List<String>) oFootprintMap.get("data-graphs");
					List<String> dGraphs2 = new ArrayList<String>();
					for(String graph : dGraphs) {
						dGraphs2.add(graph + version);
					}
					dGraphs = dGraphs2;
				}

				if (!oFootprintMap.containsKey("model-graphs")) {
					mGraphs.add(getDefaultModelGraph());
				} else {
					mGraphs = (List<String>) oFootprintMap.get("model-graphs");
					List<String> mGraphs2 = new ArrayList<String>();
					for(String graph : mGraphs) {
						mGraphs2.add(graph + version);
					}
					mGraphs = mGraphs2;
				}
			}

		} else {
			dGraphs.add(getDefaultDataGraph());
			mGraphs.add(getDefaultModelGraph());
		}

		try {
			IngestionStatus value = uploadDataFromManifestYAML(manifestPath);
			switch (value) {
			case DONE:
				System.out.println(MANIFEST_SUCCESS);
				break;
			case FAILED:
				System.out.println(MANIFEST_FAILED);
				break;
			case CANCELED:
				System.out.println(MANIFEST_CANCELED);
			}

		} catch (Exception e) {
			System.out.println("Ingestion failed, check YAML: " + ingestionYaml.getAbsolutePath());
		}

	}

	private static boolean isEmpty(ArrayList<String> row) {
		for (String entry : row) {
			if (entry != null && !entry.equals("")) {
				return false;
			}
		}
		return true;
	}

	private static IngestionStatus uploadInstanceDataCSV(String ingestionId, ArrayList<String> header,
			ArrayList<ArrayList<String>> body, boolean bUriIngestion, String path, String modelGraph, String dataGraph,
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
			System.out.println("Uploading CSV at " + path + " as class " + ingestionId + "... ");
			if (bUriIngestion == false) {
				client.dispatchIngestFromCsvStringsByIdSync(ingestionId, sCSV,
						ConnectionUtil.getSparqlConnection(mGraphs, dataGraph, dataGraphs));

			} else {

				/*
				 * String error = client.execFromCsvUsingClassTemplate( ingestionId,
				 * "identifier", sCSV, ConnectionUtil.getSparqlConnection(mGraphs, dataGraph,
				 * dataGraphs).toJson().toJSONString(), false, "override");
				 *
				 */
				String status = client.dispatchIngestFromCsvStringsByClassTemplateSync(ingestionId, "identifier", sCSV,
						ConnectionUtil.getSparqlConnection(mGraphs, dataGraph, dataGraphs));
			}
			System.out.println("OK");

			List<String> warnings = client.getWarnings();
			if (warnings != null) {
				for (String warning : warnings) {
					System.out.println(warning);
				}
			}

		} catch (Exception e) {
			System.out.println("FAIL");
			System.out.println(e.getLocalizedMessage());
			System.out.println("Upload of " + ingestionId + " failed, " + "CSV: " + path);
			return IngestionStatus.FAILED;
		}

		return IngestionStatus.DONE;
	}

}
