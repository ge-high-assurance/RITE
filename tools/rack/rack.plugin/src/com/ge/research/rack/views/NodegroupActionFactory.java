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

import com.ge.research.rack.utils.CSVUtil;
import com.ge.research.rack.utils.ConnectionUtil;
import com.ge.research.rack.utils.Core;
import com.ge.research.rack.utils.IngestionTemplateUtil;
import com.ge.research.rack.utils.ProjectUtils;
import com.ge.research.rack.utils.RackConsole;
import com.ge.research.semtk.api.nodeGroupExecution.client.NodeGroupExecutionClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.io.*;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class NodegroupActionFactory {
    public static Action getCreateInstanceDataAction(INodegroupView view) {

        return new Action() {
            public void run() {
                String selectedNodegroup = view.getSelectedNodegroups().get(0);
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                File dir =
                        new File(
                                RackPreferencePage /*.getOverlayOntProj()*/
                                        .getInstanceDataFolder());
                try {
                    ProjectUtils.setupInstanceDataFolder();
                } catch (Exception e) {
                    RackConsole.getConsole()
                            .error(
                                    "Unable to use InstanceData folder, it is either missing or unable to create a new one");
                    return;
                }

                String dataPath = dir.getAbsolutePath();
                String filePath = dataPath + Core.INSTANCE_DATA_FOLDER + selectedNodegroup + ".csv";
                String template = IngestionTemplateUtil.csvTemplates.get(selectedNodegroup);

                try {
                    // TODO: Need to check if the overlay project has been set or not in the
                    // preference page
                    // Also, this would create all the instance data in the overlay project.
                    // I think it should be fine because overlay project is something users would
                    // work with.

                    File file = new File(filePath);
                    if (!file.exists() || !file.isFile()) {
                        if (!file.getParentFile().exists()) {
                            File pFile = file.getParentFile();
                            pFile.mkdirs();
                        }
                        if (!file.exists()) {
                            file.createNewFile();
                            if (template != null) {
                                FileUtils.write(file, template, Charset.defaultCharset());
                            } else {
                                RackConsole.getConsole()
                                        .error(
                                                "Have no template for selected nodegroup, please ingest the nodegroup again and retry");
                                return;
                            }
                        }
                    }

                    CSVUtil.addToYaml(selectedNodegroup, file.getName());

                    String[] csvCols = CSVUtil.getColumnInfo(filePath);
                    ArrayList<String> fileCols = new ArrayList<>();

                    if (csvCols != null) {
                        fileCols = new ArrayList<>(Arrays.asList(csvCols));
                    }

                    // trim the csv template
                    String templateTrimmed = template.replace("\n", "").trim();

                    ArrayList<String> templateColumns =
                            new ArrayList<>(Arrays.asList(templateTrimmed.split(",")));

                    if (!templateColumns.containsAll(fileCols) && fileCols.size() != 0) {
                        RackConsole.getConsole()
                                .error(
                                        "Selected nodegroup's columns are incompatible with its template, check its ingestion template headers");
                        return;
                    }

                    if (fileCols.size() == 0) {
                        fileCols = templateColumns;
                    }

                    ArrayList<String> diff = CSVUtil.diffColumns(templateColumns, fileCols);

                    InstanceDataEditor.init(
                            selectedNodegroup, view.isUri(), filePath, fileCols, diff);

                    try {
                        IViewPart instanceDataView =
                                window.getActivePage().findView(InstanceDataEditor.ID);
                        window.getActivePage().hideView(instanceDataView);
                        window.getActivePage().showView(InstanceDataEditor.ID);

                        /*
                         * IViewPart manageColumnView =
                         * window.getActivePage().findView(NodegroupColumnView.ID);
                         * window.getActivePage().hideView(manageColumnView);
                         * window.getActivePage().showView(NodegroupColumnView.ID);
                         */
                    } catch (Exception e) {
                        RackConsole.getConsole()
                                .error(
                                        "Cannot show selected nodegroup template, please check the ingestion template");
                    }

                } catch (Exception e) {
                    RackConsole.getConsole()
                            .error(" Cannot process csv file for selected nodegroup:" + filePath);
                }
            }
        };
    }

    public static Action getQueryNodegroupAction(INodegroupView view) {
        return new Action() {
            public void run() {
                // nodegroups
                ArrayList<String> selection = view.getSelectedNodegroups();
                if (selection.size() == 0) {
                    return;
                }

                String queryNodegroup = selection.get(0);
                try {
                    NodeGroupExecutionClient client = ConnectionUtil.getNGEClient();
                    SparqlConnection conn = ConnectionUtil.getSparqlConnection();
                    // define an endpoint graph, and build a connection that uses it for model and
                    // data
                    // run a query from the store by id
                    com.ge.research.semtk.resultSet.Table results =
                            client.execDispatchSelectByIdToTable(queryNodegroup, conn, null, null);
                    QueryResultsView.results = results;
                    // System.out.println("Cell 0 0: " + results.getCellAsString(0, 0));
                    String csv_string = results.toCSVString();
                    String dir = /*ProjectUtils.getOverlayProjectPath();*/
                            RackPreferencePage.getInstanceDataFolder();
                    ProjectUtils.validateInstanceDataFolder();
                    String queryResultsDir = dir + "/" + Core.QUERY_RESULTS_FOLDER + "/";
                    File file = new File(queryResultsDir);
                    if (!file.exists() || !file.isDirectory()) {
                        RackConsole.getConsole()
                                .println("No QueryResults folder found, creating one");
                        file.mkdirs();
                    }
                    CSVUtil.writeToCSV(
                            csv_string, queryResultsDir + queryNodegroup + "_queryresults.csv");
                    System.out.println("Results for queried nodegroup: ");
                    System.out.println(csv_string);

                    Display.getDefault()
                            .asyncExec(
                                    () -> {
                                        ViewUtils.showView(QueryResultsView.ID);
                                    });

                } catch (Exception e) {
                    RackConsole.getConsole().error("Unable to show query result view");
                }
            }
        };
    }
}
