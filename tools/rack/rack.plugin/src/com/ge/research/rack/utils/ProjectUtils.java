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
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.yaml.snakeyaml.*;

public class ProjectUtils {
    public static boolean isOverlaySelected = false;

    public static void createDirectory(String path) throws Exception {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static File createFile(String path) throws Exception {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            file.createNewFile();
        }
        return file;
    }

    public static void setupInstanceDataFolder() throws Exception {
        String dir = RackPreferencePage.getInstanceDataFolder();
        String dataPath = dir + "/InstanceData/";
        File f = new File(dataPath);
        f.mkdirs();
        String yamlPath = dataPath + "/import.yaml";
        File yaml = new File(yamlPath);
        yaml.createNewFile();
    }

    public static void setupNodegroupsFolder() throws Exception {

        String ingestionNgDir = RackPreferencePage.getInstanceDataFolder() + "/nodegroups";

        if (ingestionNgDir == null || ingestionNgDir == "") {
            return;
        }

        createDirectory(ingestionNgDir);
        createFile(ingestionNgDir + "/" + Core.nodegroupMetadataFile);

        refreshProjects();
    }

    public static void loadYamls() throws Exception {

        File metadataYaml =
                createFile(
                        RackPreferencePage.getInstanceDataFolder()
                                + "/nodegroups/"
                                + Core.nodegroupMetadataFile);
        NodegroupUtil.ingestionNodegroupMapping = readYaml(metadataYaml.getAbsolutePath());
        if (NodegroupUtil.ingestionNodegroupMapping == null) {
            NodegroupUtil.ingestionNodegroupMapping = new HashMap<String, ArrayList<String>>();
            Map<String, ArrayList<String>> yamlMap =
                    (Map<String, ArrayList<String>>) NodegroupUtil.ingestionNodegroupMapping;
            yamlMap.put("nodegroups", new ArrayList<>());
        }
    }

    public static HashMap<String, Object> readYaml(String path) throws Exception {
        FileInputStream inputStream = new FileInputStream(new File(path));
        Yaml yaml = new Yaml();
        return yaml.<HashMap<String, Object>>load(inputStream);
    }

    public static DumperOptions getYamlDumperOptions() {
        final DumperOptions options = new DumperOptions();
        options.setIndent(1);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

    public static void writeYaml(Object object, String path) throws Exception {
        FileUtils.write(new File(path), writeYaml(object), Charset.defaultCharset());
    }

    public static String writeYaml(Object object) throws Exception {
        DumperOptions options = new DumperOptions();
        options.setIndent(1);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        StringWriter writer = new StringWriter();
        yaml.dump(object, writer);
        return writer.toString();
    }

    public static void refreshProjects() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject project : projects) {
            try {
                project.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                RackConsole.getConsole().error(" Unable to refresh projects");
            }
        }
    }

    public static boolean buildProjects(
            IWorkspace iWorkspace, IProgressMonitor monitor, Set<String> allBuildErrors) {
        boolean buildSuccessful = true;

        if (iWorkspace instanceof Workspace) {
            Workspace workspace = (Workspace) iWorkspace;
            IBuildConfiguration[] buildOrder = workspace.getBuildOrder();
            int trigger = IncrementalProjectBuilder.FULL_BUILD;

            try {
                IBuildConfiguration[] configs = ICoreConstants.EMPTY_BUILD_CONFIG_ARRAY;
                IStatus result =
                        workspace.getBuildManager().build(buildOrder, configs, trigger, monitor);
                if (!result.isOK()) {
                    buildSuccessful = false;
                }
            } finally {
                // Always send POST_BUILD if there was a PRE_BUILD
                workspace.broadcastBuildEvent(workspace, IResourceChangeEvent.POST_BUILD, trigger);
            }

            buildSuccessful = accumulateErrorMessages(buildOrder, buildSuccessful, allBuildErrors);
        }

        return buildSuccessful;
    }

    /** Accumulates error messages after building imported projects */
    private static boolean accumulateErrorMessages(
            IBuildConfiguration[] buildOrder, boolean buildSuccessful, Set<String> allBuildErrors) {
        // Get any error markers
        try {
            for (IBuildConfiguration bc : buildOrder) {
                IProject project = bc.getProject();
                IMarker[] findMarkers = project.findMarkers(null, true, IResource.DEPTH_INFINITE);
                for (IMarker marker : findMarkers) {
                    int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                    if (severity >= IMarker.SEVERITY_ERROR) {
                        buildSuccessful = false;
                        allBuildErrors.add(marker.toString());
                    }
                }
            }
        } catch (CoreException e) {
            System.err.println("Error getting error markers: " + e);
        }

        return buildSuccessful;
    }

    public static boolean validateInstanceDataFolder() {
        String path = RackPreferencePage.getInstanceDataFolder();
        File dir = new File(path);
        if (!dir.exists()) {
            ErrorMessageUtil.error(
                    "RACK Project does not exist, please set a valid directory as RACK project");
            return false;
        }
        if (!dir.isDirectory()) {
            ErrorMessageUtil.error(
                    "RACK Project is not a directory, please set a valid directory as RACK project");
            return false;
        }
        return true;
    }

    //    private HashMap<String, String> nodegroupMapping = new HashMap<>();

    //    private String selectedProject = "";

    public static void createInstanceDataFolder(String projectPath) {}

    public static void createNodegroupFolder(String projectPath) {
        File ngDir = new File(projectPath + Core.NODEGROUP_DATA_FOLDER);
        ngDir.mkdirs();
    }

    public static void mapNodegroups() {}

    public static void createOwlModelsFolder() {}

    public void createNodegroupsFolder() {}

    public static boolean containsNodegroup(String nodegroupId, String sgJSON) {

        return false;
    }

    public static void buildStoreData() {}

    public static void buildYAML() {}
}
