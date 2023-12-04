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
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreConfig;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.resultSet.TableResultSet;

import java.util.*;

public class NodegroupUtil {

    public static NodeGroupStoreConfig config;
    public static NodeGroupStoreRestClient client;
    public static TableResultSet nodegroups;
    public static Object ingestionNodegroupMapping = new HashMap<>();
    public static ArrayList<String> ingestionNodegroups = new ArrayList<>();

    public static boolean nodegroupExistsOnRack(String id) throws Exception {
        NodeGroupStoreRestClient ngsClient = ConnectionUtil.getNGSClient();
        int count = ngsClient.executeGetNodeGroupById(id).getResults().getNumRows();
        if (count >= 1) {
            return true;
        }
        return false;
    }

    public static void addToYaml(String ingestId) {
        Map<String, Object> yamlMap = null;
        String yamlPath = "";

        if (!ProjectUtils.validateInstanceDataFolder()) {
            return;
        }
        yamlMap = (Map<String, Object>) ingestionNodegroupMapping;
        yamlPath = /* ProjectUtils.getOverlayProjectPath()*/
                RackPreferencePage.getInstanceDataFolder() + "/nodegroups/metadata.yaml";

        Object oYamlNodegroups = yamlMap.get("nodegroups");
        ArrayList<String> yamlNodegroups = (ArrayList<String>) oYamlNodegroups;
        yamlNodegroups.add(ingestId);
        yamlMap.put("nodegroups", yamlNodegroups);
        try {
            ProjectUtils.writeYaml(yamlMap, yamlPath);
        } catch (Exception e) {
            String prj = RackPreferencePage.getInstanceDataFolder();
            ErrorMessageUtil.error("Cannot write back to " + prj + " project's metadata.yaml");
        }
    }

    public static void removeFromYaml(String ingestId) {

        if (!ProjectUtils.validateInstanceDataFolder()) {
            return;
        }
        String yamlPath = "";
        Map<String, Object> yamlMap = (Map<String, Object>) ingestionNodegroupMapping;
        yamlPath = /*ProjectUtils.getOverlayProjectPath()*/
                RackPreferencePage.getInstanceDataFolder() + "/nodegroups/metadata.yaml";

        Object oYamlNodegroups = yamlMap.get("nodegroups");
        ArrayList<String> yamlNodegroups = (ArrayList<String>) oYamlNodegroups;
        yamlNodegroups.remove(ingestId);
        yamlMap.put("nodegroups", yamlNodegroups);
        try {
            ProjectUtils.writeYaml(yamlMap, yamlPath);
        } catch (Exception e) {
            String prj = "Instance Data folder: " + RackPreferencePage.getInstanceDataFolder();
            RackConsole.getConsole()
                    .error("Cannot write back to " + prj + " project's metadata.yaml");
        }
    }

    public static ArrayList<String> getLocalNodegroupsCore() {
        Map<String, Object> yamlMap = (Map<String, Object>) ingestionNodegroupMapping;
        Object oYamlNodegroups = yamlMap.get("nodegroups");
        ArrayList<String> yamlNodegroups = (ArrayList<String>) oYamlNodegroups;
        return yamlNodegroups;
    }

    public static ArrayList<String> getLocalNodegroupsOverlay() {
        Map<String, Object> yamlMap = (Map<String, Object>) ingestionNodegroupMapping;
        Object oYamlNodegroups = yamlMap.get("nodegroups");
        ArrayList<String> yamlNodegroups = (ArrayList<String>) oYamlNodegroups;
        return yamlNodegroups;
    }

    public static void init() throws Exception {
        config =
                new NodeGroupStoreConfig(
                        RackPreferencePage.getProtocol(),
                        RackPreferencePage.getServer(),
                        Integer.parseInt(RackPreferencePage.getStorePort()));
        client = new NodeGroupStoreRestClient(config);
    }

    public static void getAllNodegroups() throws Exception {
        NodegroupUtil.ingestionNodegroups.clear();
        nodegroups = client.executeGetNodeGroupMetadata();
        com.ge.research.semtk.resultSet.Table results = nodegroups.getResults();
        ArrayList<ArrayList<String>> rows = results.getRows();

        rows.sort(
                new Comparator<ArrayList<String>>() {
                    @Override
                    public int compare(ArrayList<String> row1, ArrayList<String> row2) {
                        return row1.get(0).compareTo(row2.get(0));
                    }
                });

        for (ArrayList<String> row : rows) {
            NodegroupUtil.ingestionNodegroups.add(row.get(0));
        }
    }
}
