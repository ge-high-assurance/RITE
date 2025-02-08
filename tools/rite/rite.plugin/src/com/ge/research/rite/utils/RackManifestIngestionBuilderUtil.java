/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, GE Aerospace and Galois, Inc.
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
package com.ge.research.rite.utils;

import com.opencsv.CSVReader;

import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Flattens a rack project into an ingestion package .zip
 *
 * <p>PORT of the RACK CLI (python) ingestion package creator
 */
/*
 * TODO: POJO for Rack Manifest & leveraging Yaml's deserialization to avoid
 * unchecked casting
 */
@SuppressWarnings("unchecked")
public class RackManifestIngestionBuilderUtil {

    private static String FILES = "files";
    private static String INGESTION_STEPS = "ingestion-steps";

    private static String NODEGROUPS_STORE_DATA_FILENAME = "store_data.csv";
    private static String MANIFEST_YAML_FILENAME = "manifest.yaml";

    private static String INGESTION_STEP_TYPE_OWL = "owl";
    private static String INGESTION_STEP_TYPE_CSV = "csv";
    private static Set<String> STEP_TYPES =
            Set.of(INGESTION_STEP_TYPE_OWL, INGESTION_STEP_TYPE_CSV);

    private static String JSON_FILE = "jsonFile";

    private static String MANIFEST_NAME_KEY = "name";
    private static String MANIFEST_STEPS_KEY = "steps";
    private static String MANIFEST_MANIFEST_KEY = "manifest";
    private static String MANIFEST_MODEL_KEY = "model";
    private static String MANIFEST_DATA_KEY = "data";
    private static String MANIFEST_NODEGROUPS_KEY = "nodegroups";

    private static String ZIP_ENTRY_ERROR =
            "Unable to generate ingestion zip file; error creating %s";

    public static class IngestionBuilderException extends Exception {

        private static final long serialVersionUID = 1L;
        private static String YAML_PARSE_ERROR = "Unable to parse the file at %s to YAML";

        private IngestionBuilderException(String msg, Exception e) {
            super(msg, e);
        }

        private static IngestionBuilderException yamlParseException(
                String yamlFilepath, Exception e) {

            return new IngestionBuilderException(String.format(YAML_PARSE_ERROR, yamlFilepath), e);
        }
    }

    private static Object readYaml(String yamlFilePath) throws IngestionBuilderException {

        try {

            return ProjectUtils.readYaml(yamlFilePath);

        } catch (Exception e) {

            throw IngestionBuilderException.yamlParseException(yamlFilePath, e);
        }
    }

    private static void writeYaml(Path fileName, Object yamlContent, ZipOutputStream zipStream)
            throws IngestionBuilderException, IOException {

        // set filename readable

        try (StringWriter writer = new StringWriter()) {

            new Yaml(ProjectUtils.getYamlDumperOptions()).dump(yamlContent, writer);
            byte[] yamlAsString = writer.toString().getBytes();

            zipStream.putNextEntry(
                    new ZipEntry(FilenameUtils.separatorsToUnix(fileName.toString())));
            zipStream.write(yamlAsString, 0, yamlAsString.length);
            zipStream.closeEntry();

        } catch (IOException e) {

            throw new IngestionBuilderException(String.format(ZIP_ENTRY_ERROR, fileName), e);
        }
    }

    private int fresh;
    private List<Object> steps = new ArrayList<Object>();
    private Set<String> manifests = new TreeSet<String>();

    private int nextFresh() {
        int result = this.fresh;
        this.fresh = result + 1;
        return result;
    }

    private Path newDirectory(String name, String kind) {
        String dirIdx = String.format("%02d", this.nextFresh());
        Path path = Path.of(name, dirIdx + '_' + kind);
        path.toFile().mkdir();
        path.toFile().setWritable(true, false);
        path.toFile().setReadable(true, false);
        return path;
    }

    private void zipModelYamlResources(
            Path modelYamlFilepath, Path modelYamlGeneratedPath, ZipOutputStream zipStream)
            throws IngestionBuilderException, IOException {

        modelYamlFilepath.getParent();

        Map<String, Object> oModelYamlMap =
                (Map<String, Object>) readYaml(modelYamlFilepath.toString());

        List<String> loFiles = (List<String>) oModelYamlMap.get(FILES);

        for (int i = 0; i < loFiles.size(); i++) {

            /*
             * Writes the file to the zipStream and modifies the yaml file to use generated
             * paths
             */
            Path filepath = Path.of(loFiles.get(i).toString());
            Path fromPath = modelYamlFilepath.getParent().resolve(filepath);
            Path filename = filepath.getFileName();
            Path toPath = modelYamlGeneratedPath.getParent().resolve(filename);
            zipStream.putNextEntry(new ZipEntry(FilenameUtils.separatorsToUnix(toPath.toString())));
            Files.copy(fromPath, zipStream);
            zipStream.closeEntry();
            loFiles.set(i, filename.toString());
        }

        writeYaml(modelYamlGeneratedPath, oModelYamlMap, zipStream);
    }

    private void zipDataYamlResources(
            Path dataYamlFilepath, Path dataYamlGeneratedPath, ZipOutputStream zipStream)
            throws IngestionBuilderException, IOException {

        Map<String, Object> oDataYamlMap =
                (Map<String, Object>) readYaml(dataYamlFilepath.toString());

        List<Map<String, String>> loIngestionSteps =
                (List<Map<String, String>>) oDataYamlMap.get(INGESTION_STEPS);

        for (Map<String, String> ingestionStep : loIngestionSteps) {

            String key =
                    ingestionStep.keySet().stream()
                            .filter(k -> STEP_TYPES.contains(k))
                            .findFirst()
                            .get();

            Path filepath = Path.of(ingestionStep.get(key));
            Path fromPath = dataYamlFilepath.getParent().resolve(filepath);
            Path filename = filepath.getFileName();

            Path toPath = dataYamlGeneratedPath.getParent().resolve(filename);
            zipStream.putNextEntry(new ZipEntry(FilenameUtils.separatorsToUnix(toPath.toString())));
            Files.copy(fromPath, zipStream);
            zipStream.closeEntry();
            ingestionStep.put(key, filename.toString());
        }

        writeYaml(dataYamlGeneratedPath, oDataYamlMap, zipStream);
    }

    private static void zipNodegroups(
            Path nodegroupsFilepath, Path nodegroupsGeneratedFilepath, ZipOutputStream zipStream)
            throws FileNotFoundException, IOException {

        Path storeDataFilepath = nodegroupsFilepath.resolve(NODEGROUPS_STORE_DATA_FILENAME);

        try (FileReader reader = new FileReader(storeDataFilepath.toFile());
                CSVReader csvReader = new CSVReader(reader)) {

            csvReader.skip(0);
            Iterator<String[]> iterator = csvReader.iterator();

            List<String> headers = List.of(iterator.next());
            int jsonFileIdx = headers.indexOf(JSON_FILE);

            while (iterator.hasNext()) {
                String[] csvLine = iterator.next();
                Path filepath = Path.of(List.of(csvLine).get(jsonFileIdx));
                Path fromPath = nodegroupsFilepath.resolve(filepath);
                Path toPath = nodegroupsGeneratedFilepath.resolve(filepath);
                zipStream.putNextEntry(
                        new ZipEntry(FilenameUtils.separatorsToUnix(toPath.toString())));
                Files.copy(fromPath, zipStream);
                zipStream.closeEntry();
            }
        }

        String toStoreFilename =
                nodegroupsGeneratedFilepath.resolve(NODEGROUPS_STORE_DATA_FILENAME).toString();

        zipStream.putNextEntry(new ZipEntry(FilenameUtils.separatorsToUnix(toStoreFilename)));
        Files.copy(storeDataFilepath, zipStream);
        zipStream.closeEntry();
    }

    public void zipManifestResources(Path ingestionDir, ZipOutputStream zipStream)
            throws FileNotFoundException, IngestionBuilderException, IOException {

        zipManifestResources(ingestionDir.resolve(MANIFEST_YAML_FILENAME), true, zipStream);
    }

    public void zipManifestResources(
            Path physicalManifestFile, boolean isTopLevelManifest, ZipOutputStream zipStream)
            throws IngestionBuilderException, FileNotFoundException, IOException {

        Map<String, Object> oManifestYamlMap =
                (Map<String, Object>) readYaml(physicalManifestFile.toString());
        // Handle multiple inclusions of the same manifest to simplify
        String manifestName = oManifestYamlMap.get(MANIFEST_NAME_KEY).toString();

        if (manifests.contains(manifestName)) {
            return;
        }
        manifests.add(manifestName);

        // base used for resolving relative paths found in manifests
        Path fromBase = physicalManifestFile.getParent();

        String manifestDirectory = null;

        List<Map<String, Object>> oManifestSteps =
                (List<Map<String, Object>>) oManifestYamlMap.get(MANIFEST_STEPS_KEY);

        for (Map<String, Object> oStepMap : oManifestSteps) {

            for (Entry<?, ?> step : ((Map<?, ?>) oStepMap).entrySet()) {

                if (MANIFEST_MANIFEST_KEY.equals(step.getKey())) {

                    Path resolvedManifestFile = fromBase.resolve(step.getValue().toString());

                    zipManifestResources(resolvedManifestFile, false, zipStream);

                } else {

                    if (null == manifestDirectory) {

                        manifestDirectory = newDirectory("", manifestName).toString();
                    }

                    if (MANIFEST_MODEL_KEY.equals(step.getKey())) {

                        Path modelPath = fromBase.resolve(step.getValue().toString());

                        Path generatedDir = newDirectory(manifestDirectory, MANIFEST_MODEL_KEY);

                        Path generatedModelPath = generatedDir.resolve(modelPath.getFileName());

                        zipModelYamlResources(modelPath, generatedModelPath, zipStream);

                        this.steps.add(
                                Map.of(
                                        MANIFEST_MODEL_KEY,
                                        generatedModelPath.toString().replace("\\", "/")));

                    } else if (MANIFEST_DATA_KEY.equals(step.getKey())) {

                        Path dataPath = fromBase.resolve(step.getValue().toString());

                        Path generatedDir = newDirectory(manifestDirectory, MANIFEST_DATA_KEY);

                        Path generatedModelPath = generatedDir.resolve(dataPath.getFileName());

                        zipDataYamlResources(dataPath, generatedModelPath, zipStream);

                        this.steps.add(
                                Map.of(
                                        MANIFEST_DATA_KEY,
                                        generatedModelPath.toString().replace("\\", "/")));

                    } else if (MANIFEST_NODEGROUPS_KEY.equals(step.getKey())) {

                        Path nodegroupPath = fromBase.resolve(step.getValue().toString());

                        Path generatedDir =
                                newDirectory(manifestDirectory, MANIFEST_NODEGROUPS_KEY);

                        zipNodegroups(nodegroupPath, generatedDir, zipStream);

                        this.steps.add(
                                Map.of(
                                        MANIFEST_NODEGROUPS_KEY,
                                        generatedDir.toString().replace("\\", "/")));
                    }
                }
            }
        }

        if (isTopLevelManifest) {
            oManifestYamlMap.put(MANIFEST_STEPS_KEY, this.steps);
            writeYaml(Path.of(MANIFEST_YAML_FILENAME), oManifestYamlMap, zipStream);
        }
    }
}
