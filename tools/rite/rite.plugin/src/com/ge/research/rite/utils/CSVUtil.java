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

import com.ge.research.rite.views.RackPreferencePage;
import com.ge.research.semtk.resultSet.TableResultSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVUtil {

    public static String[] getColumnInfo(String path) {
        // by default, assume first line is csv file to be set of columns

        String[] columns = {};

        try {

            new File(path).createNewFile();
            CSVReader reader = new CSVReader(new FileReader(path));
            columns = reader.readNext();
        } catch (Exception e) {
            RackConsole.getConsole().error("Reading CSV File: " + path);
        }

        return columns;
    }

    public static ArrayList<String> diffColumns(ArrayList<String> cols1, ArrayList<String> cols2) {
        ArrayList<String> diff = new ArrayList<>();
        for (String col : cols1) {
            if (!cols2.contains(col)) {
                diff.add(col);
            }
        }
        return diff;
    }

    public static List<String[]> getRows(String path) {
        List<String[]> rows = new ArrayList<>();

        try {
            File file = new File(path);
            file.createNewFile();
            CSVReader reader = new CSVReader(new FileReader(path));
            rows = reader.readAll();
            reader.close();
            if (rows.size() > 0) {
                rows = rows.subList(1, rows.size());
            }
        } catch (Exception e) {
            return rows;
        }
        return rows;
    }

    public static void addToYaml(String nodegroup, String filename) throws Exception {

        String dir = RackPreferencePage.getInstanceDataFolder();
        String yamlPath = dir + "/InstanceData/import.yaml";
        Object oYaml = ProjectUtils.readYaml(yamlPath);

        if (oYaml != null && !(oYaml instanceof Map)) {
            RackConsole.getConsole().error("Ill formed import.yaml");
            return;
        }

        if (oYaml == null) {
            oYaml = new HashMap<String, Object>();
        }
        Map<String, Object> yamlMap = (Map<String, Object>) oYaml;

        if (yamlMap.get("data-graph") == null) {
            String defaultDataGraph = RackPreferencePage.getDefaultDataGraph();
            yamlMap.put("data-graph", defaultDataGraph);
        }
        ArrayList<Map<String, String>> ingestionSteps =
                (ArrayList<Map<String, String>>) yamlMap.get("ingestion-steps");
        if (ingestionSteps == null) {
            ingestionSteps = new ArrayList<Map<String, String>>();
        }

        Map<String, String> csvMap = new HashMap<>();
        csvMap.put("nodegroup", nodegroup);
        csvMap.put("csv", filename);
        if (!ingestionSteps.contains(csvMap)) {
            ingestionSteps.add(csvMap);
        }
        yamlMap.put("ingestion-steps", ingestionSteps);

        ProjectUtils.writeYaml(yamlMap, yamlPath);
    }

    public static void writeToCSV(TableResultSet rs, String filepath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filepath));
            writer.writeAll((ResultSet) rs, true, true);
            writer.close();
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to write csv content to file: " + filepath);
        }
    }

    public static void writeToCSV(String CSVString, String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.write(new File(filepath), CSVString, Charset.defaultCharset());
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to write csv content to file: " + filepath);
        }
    }

    public static boolean isRowEmpty(ArrayList<String> row) {

        for (String entry : row) {
            if (!entry.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
