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
package com.ge.research.rack.views;

import com.ge.research.rack.utils.RackConsole;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class RackPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {
    // preference keys
    private static final String PROTOCOL = "protocol";
    private static final String SERVER = "server";
    private static final String UTILITY_PORT = "utility_port";
    private static final String NGE_PORT = "nge_port";
    private static final String STORE_PORT = "store_port";
    private static final String QUERY_PORT = "query_port";
    private static final String ONTOLOGY_PORT = "ontology_port";
    private static final String CONN_TYPE = "connection_type";
    private static final String CONN_URL = "connection_url";
    private static final String DEFAULT_MODEL_GRAPH = "default_model_graph";
    private static final String DEFAULT_DATA_GRAPH = "default_data_graph";
    private static final String RACK_USER = "rack_user";
    private static final String RACK_PASSWORD = "rack_password";
    private static final String INSTANCE_DATA_FOLDER = "instance_data_folder";
    private static final String GSN_PROJECT_OVERLAY_SADL =
            "gsn_project_overlay_sadl"; // initial value must be ""
    private static final String GSN_PROJECT_PATTERN_SADL =
            "gsn_project_pattern_sadl"; // initial value must be ""

    private static final String JAVAFX_WINDOW = "rack_javafx";
    private static final String SHOW_CONSOLE = "rack_console";
    private static final String BLOCKING_CANCEL = "rack_blocking_cancel";

    // singleton preference store
    private static ScopedPreferenceStore preferenceStore =
            new ScopedPreferenceStore(InstanceScope.INSTANCE, "rack.plugin");
    // child field editors
    private List<FieldEditor> fields = new ArrayList<>();

    public RackPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        preferenceStore.setDefault(SHOW_CONSOLE, false);
        preferenceStore.setDefault(BLOCKING_CANCEL, false);
        setDescription("SemTK Preference");
        preferenceStore.setDefault(PROTOCOL, "http");
        preferenceStore.setDefault(SERVER, "localhost");
        preferenceStore.setDefault(UTILITY_PORT, "12060");
        preferenceStore.setDefault(NGE_PORT, "12058");
        preferenceStore.setDefault(STORE_PORT, "12056");
        preferenceStore.setDefault(QUERY_PORT, "12050");
        preferenceStore.setDefault(ONTOLOGY_PORT, "12057");
        preferenceStore.setDefault(CONN_TYPE, "fuseki");
        preferenceStore.setDefault(CONN_URL, "http://localhost:3030/RACK");
        preferenceStore.setDefault(DEFAULT_MODEL_GRAPH, "http://rack001/model");
        preferenceStore.setDefault(DEFAULT_DATA_GRAPH, "http://rack001/data");
        preferenceStore.setDefault(RACK_USER, "rack");
        preferenceStore.setDefault(RACK_PASSWORD, "rack");
        preferenceStore.setDefault(INSTANCE_DATA_FOLDER, "");
        preferenceStore.setDefault(GSN_PROJECT_OVERLAY_SADL, "");
        preferenceStore.setDefault(GSN_PROJECT_PATTERN_SADL, "");

        setPreferenceStore(preferenceStore);
    }

    public static boolean getShowConsole() {
        return preferenceStore.getBoolean(SHOW_CONSOLE);
    }

    public static boolean getUseBlockingCancel() {
        return preferenceStore.getBoolean(BLOCKING_CANCEL);
    }

    public static String getProtocol() {
        return preferenceStore.getString(PROTOCOL);
    }

    public static String getServer() {
        return preferenceStore.getString(SERVER);
    }

    public static String getUtilityPort() {
        return preferenceStore.getString(UTILITY_PORT);
    }

    public static String getNGEPort() {
        return preferenceStore.getString(NGE_PORT);
    }

    public static String getStorePort() {
        return preferenceStore.getString(STORE_PORT);
    }

    public static String getQueryPort() {
        return preferenceStore.getString(QUERY_PORT);
    }

    public static String getOntologyPort() {
        return preferenceStore.getString(ONTOLOGY_PORT);
    }

    public static String getConnType() {
        return preferenceStore.getString(CONN_TYPE);
    }

    public static String getConnURL() {
        return preferenceStore.getString(CONN_URL);
    }

    public static String getDefaultModelGraph() {
        return preferenceStore.getString(DEFAULT_MODEL_GRAPH);
    }

    public static String getDefaultDataGraph() {
        return preferenceStore.getString(DEFAULT_DATA_GRAPH);
    }

    public static String getUser() {
        return preferenceStore.getString(RACK_USER);
    }

    public static String getPassword() {
        return preferenceStore.getString(RACK_PASSWORD);
    }

    public static String getInstanceDataFolder() {
        return preferenceStore.getString(INSTANCE_DATA_FOLDER);
    }

    public static void setInstanceDataFolder(String path) {
        preferenceStore.setValue(INSTANCE_DATA_FOLDER, path);
    }

    /**
     * @return the gsnProjectOverlaySadl
     */
    public static String getGsnProjectOverlaySadl() {
        return preferenceStore.getString(GSN_PROJECT_OVERLAY_SADL);
    }

    public static void setGsnProjectOverlaySadl(String overlay) {
        preferenceStore.setValue(GSN_PROJECT_OVERLAY_SADL, overlay);
    }

    /**
     * @return the gsnProjectPatternSadl
     */
    public static String getGsnProjectPatternSadl() {
        return preferenceStore.getString(GSN_PROJECT_PATTERN_SADL);
    }

    public static void setGsnProjectPatternSadl(String pattern) {
        preferenceStore.setValue(GSN_PROJECT_PATTERN_SADL, pattern);
    }

    /**
     * Checks if all GSN preferences are there
     *
     * @return
     */
    public static Boolean areGSNPreferencesComplete() {

        if (getGsnProjectOverlaySadl() == null
                || getGsnProjectOverlaySadl().equalsIgnoreCase("")
                || getGsnProjectPatternSadl() == null
                || getGsnProjectPatternSadl().equalsIgnoreCase("")) {
            return false;
        } else {
            //            System.out.println(
            //                    "The GSN preferences: "
            //                            + getGsnProjectPatternSadl()
            //                            + " (project pattern), "
            //                            + getGsnProjectOverlaySadl()
            //                            + " (project overlay)");
        }

        return true;
    }

    @Override
    protected void createFieldEditors() {

        StringFieldEditor stemDir =
                new StringFieldEditor(PROTOCOL, "Protocol:", getFieldEditorParent());
        addField(stemDir);

        StringFieldEditor server = new StringFieldEditor(SERVER, "Server:", getFieldEditorParent());
        addField(server);

        StringFieldEditor utilityPort =
                new StringFieldEditor(UTILITY_PORT, "Utility Port:", getFieldEditorParent());
        addField(utilityPort);

        StringFieldEditor ngePort =
                new StringFieldEditor(NGE_PORT, "NGE Port:", getFieldEditorParent());
        addField(ngePort);

        StringFieldEditor storePort =
                new StringFieldEditor(STORE_PORT, "Store Port:", getFieldEditorParent());
        addField(storePort);

        StringFieldEditor queryPort =
                new StringFieldEditor(QUERY_PORT, "Query Port:", getFieldEditorParent());
        addField(queryPort);

        StringFieldEditor ontologyPort =
                new StringFieldEditor(ONTOLOGY_PORT, "Ontology Port:", getFieldEditorParent());
        addField(ontologyPort);

        StringFieldEditor connType =
                new StringFieldEditor(CONN_TYPE, "Connection Type:", getFieldEditorParent());
        addField(connType);

        StringFieldEditor connURL =
                new StringFieldEditor(CONN_URL, "Connection URL:", getFieldEditorParent());
        addField(connURL);

        StringFieldEditor defaultModelGraph =
                new StringFieldEditor(
                        DEFAULT_MODEL_GRAPH, "Default Model Graph:", getFieldEditorParent());
        addField(defaultModelGraph);

        StringFieldEditor defaultDataGraph =
                new StringFieldEditor(
                        DEFAULT_DATA_GRAPH, "Default data graph:", getFieldEditorParent());

        addField(defaultDataGraph);

        LabelFieldEditor separator =
                new LabelFieldEditor("   --- RACK Project Setup ---   ", getFieldEditorParent());
        addField(separator);

        DirectoryFieldEditor instanceDataFolder =
                new DirectoryFieldEditor(
                        INSTANCE_DATA_FOLDER, "RACK Project: ", getFieldEditorParent());

        addField(instanceDataFolder);
        LabelFieldEditor separatorCredentials =
                new LabelFieldEditor(
                        "   --- RACK Database Credentials ---   ", getFieldEditorParent());
        addField(separatorCredentials);

        StringFieldEditor rackUser =
                new StringFieldEditor(RACK_USER, "RACK User:", getFieldEditorParent());
        addField(rackUser);

        StringFieldEditor rackPassword =
                new StringFieldEditor(RACK_PASSWORD, "Password:", getFieldEditorParent());

        addField(rackPassword);

        LabelFieldEditor separatorCredentialsGSN =
                new LabelFieldEditor(
                        "   --- Automatic GSN Inference Settings ---   ", getFieldEditorParent());
        addField(separatorCredentialsGSN);
        FileFieldEditor gsnProjectOverlay =
                new FileFieldEditor(
                        GSN_PROJECT_OVERLAY_SADL,
                        "GSN Project Overlay .sadl Path:",
                        getFieldEditorParent());
        addField(gsnProjectOverlay);

        FileFieldEditor gsnProjectPattern =
                new FileFieldEditor(
                        GSN_PROJECT_PATTERN_SADL,
                        "GSN Project Pattern .sadl Path:",
                        getFieldEditorParent());
        addField(gsnProjectPattern);

        var consolePref =
                new BooleanFieldEditor(
                        SHOW_CONSOLE,
                        "Show console when output is written to it",
                        getFieldEditorParent());
        addField(consolePref);

        var blockingPref =
                new BooleanFieldEditor(
                        BLOCKING_CANCEL,
                        "Use a blocking cancel dialog for workflow interaction",
                        getFieldEditorParent());
        addField(blockingPref);
    }

    @Override
    protected void addField(FieldEditor editor) {
        fields.add(editor);
        super.addField(editor);
    }

    @Override
    public boolean performOk() {
        super.performOk();
        IProjectDescription description;
        File projectDir = new File(RackPreferencePage.getInstanceDataFolder());
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            RackConsole.getConsole().error("Please set a valid directory for RACK project");
            return true;
        }

        File projectFile = new File(RackPreferencePage.getInstanceDataFolder() + "/.project");
        if (!projectFile.exists()) {
            // create a .project file
            String text =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<projectDescription><name>"
                            + projectDir.getName()
                            + "</name></projectDescription>";
            try {
                projectFile.createNewFile();
                FileUtils.write(projectFile, text, Charset.defaultCharset());

            } catch (IOException e) {
                RackConsole.getConsole()
                        .error(
                                "Unable to setup RACK project using the folder at : "
                                        + RackPreferencePage.getInstanceDataFolder());
                return true;
            }
        }
        try {

            String absolutePath =
                    new File(RackPreferencePage.getInstanceDataFolder()).getAbsolutePath();
            String absoluteProjectFilePath =
                    Paths.get(Paths.get(absolutePath) + "/.project").normalize().toString();

            Path path = new Path(absoluteProjectFilePath);
            description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
            IProject project =
                    ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
            if (!project.exists()) {
                project.create(description, null);
            }

            project.open(null);
            RackConsole.getConsole().println("RACK project set to: " + absolutePath);
        } catch (Exception e) {
            RackConsole.getConsole().error("Unable to import RACK project into RITE");
        }

        return true;
    }
}
