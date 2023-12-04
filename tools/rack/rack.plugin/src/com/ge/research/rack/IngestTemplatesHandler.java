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

import com.ge.research.rack.utils.*;
import com.ge.research.rack.views.RackPreferencePage;
import com.ge.research.rack.views.RackSettingPanel;
import com.ge.research.semtk.load.utility.SparqlGraphJson;
import com.ge.research.semtk.nodeGroupStore.client.NodeGroupStoreRestClient;
import com.ge.research.semtk.sparqlX.SparqlConnection;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;

public class IngestTemplatesHandler extends AbstractHandler {

    public static IStatus ingestTemplate(ExecutionEvent event, IProgressMonitor monitor)
            throws Exception {

        final ArrayList<String> classList = OntologyUtil.getClassNames();

        if (classList == null) {
            RackConsole.getConsole()
                    .error("No ontology classes found, please check connection to RACK");
            return Status.CANCEL_STATUS;
        }

        final SubMonitor subMonitor = SubMonitor.convert(monitor);

        // Iterate over class names and ingest their json nodegroup template
        for (String className : classList) {
            if (subMonitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            String localClassName = OntologyUtil.getLocalClassName(className);
            String ingestionId = IngestionTemplateUtil.getIngestionNodegroupId(className);

            String comment = Core.NODEGROUP_INGEST_COMMENT;
            SparqlGraphJson json = null;
            try {

                RackConsole.getConsole()
                        .print(
                                "Ingesting template for "
                                        + localClassName
                                        + " by id: "
                                        + ingestionId
                                        + " ...");
                json = IngestionTemplateUtil.getSparqlGraphJson(className);
                if (json == null) {
                    return Status.CANCEL_STATUS;
                }
                NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
                ngClient.executeStoreNodeGroup(
                        ingestionId, comment, System.getProperty("user.name"), json.getJson());
                RackConsole.getConsole().printOK();
                monitor.worked(1);
            } catch (Exception e) {
                if (json == null) {
                    RackConsole.getConsole()
                            .error("Unable to build/upload nodegroup: " + ingestionId);
                    return Status.CANCEL_STATUS;
                }

                uploadExistingNodegroup(ingestionId, json);
            }
        }
        return Status.OK_STATUS;
    }

    public static IStatus ingestTemplateFromLocal(ExecutionEvent event, IProgressMonitor monitor)
            throws Exception {

        String dir = RackPreferencePage.getInstanceDataFolder();
        if (!ProjectUtils.validateInstanceDataFolder()) {
            return Status.CANCEL_STATUS;
        }

        String nodegroupsDir = dir + "/nodegroups";
        File ngDir = new File(nodegroupsDir);
        if (!ngDir.exists()) {
            RackConsole.getConsole()
                    .error("Nodegroups folder does not exist in instance data folder project");
            return Status.CANCEL_STATUS;
        }

        File[] jsons = ngDir.listFiles();
        for (File nodegroup : jsons) {
            if (nodegroup.getName().endsWith(".json")) {
                String nodegroupId = nodegroup.getName().replace(".json", "");
                uploadNodegroup(nodegroupId, true);
                monitor.worked(1);
            }
        }

        return Status.OK_STATUS;
    }

    public static void uploadExistingNodegroup(String ingestId, SparqlGraphJson json)
            throws Exception {

        // try overwriting nodegroup if applicable
        NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
        if (RackSettingPanel.isOverwrite) {
            RackConsole.getConsole()
                    .print("Nodegroup " + ingestId + " exists on RACK, attempting overwrite ...");
            try {
                ngClient.deleteStoredNodeGroup(ingestId);
                ngClient.executeStoreNodeGroup(
                        ingestId,
                        Core.NODEGROUP_INGEST_COMMENT,
                        System.getProperty("user.name"),
                        json.getJson());
                RackConsole.getConsole().printOK();
            } catch (Exception e2) {
                RackConsole.getConsole().printFAIL();
                RackConsole.getConsole().error("Overwrite of nodegroup: " + ingestId + " failed");
            }
        }
    }

    public static void uploadNodegroup(String ingestId, boolean isCore) throws Exception {

        String dir = RackPreferencePage.getInstanceDataFolder();
        String ngPath = dir + "/nodegroups/" + ingestId + ".json";
        String jsonstr = FileUtils.readFileToString(new File(ngPath), Charset.defaultCharset());
        SparqlGraphJson json = new SparqlGraphJson(jsonstr);
        NodeGroupStoreRestClient ngClient = ConnectionUtil.getNGSClient();
        SparqlConnection conn = ConnectionUtil.getSparqlConnection();
        json.setSparqlConn(conn);
        try {
            ngClient.executeStoreNodeGroup(
                    ingestId,
                    Core.NODEGROUP_INGEST_COMMENT,
                    System.getProperty("user.name"),
                    json.getJson());
        } catch (Exception e) {

            uploadExistingNodegroup(ingestId, json);
        }
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        try {

            String consoleViewId = IConsoleConstants.ID_CONSOLE_VIEW;
            IConsoleView consoleView =
                    (IConsoleView)
                            PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow()
                                    .getActivePage()
                                    .showView(consoleViewId);
            consoleView.display(RackConsole.getConsole());

            Job job =
                    new Job("Uploading ingestion nodegroups to RACK") {
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            // Set total number of work units
                            ArrayList<String> classList = OntologyUtil.getClassNames();
                            if (classList == null) {
                                RackConsole.getConsole()
                                        .error(
                                                "No classes found, check either connection parameters or upload ontologies to RACK");
                                return Status.CANCEL_STATUS;
                            }

                            monitor.beginTask("start task", classList.size());
                            try {
                                if (RackSettingPanel.genNodegroupsLocal) {
                                    return ingestTemplateFromLocal(event, monitor);
                                } else {
                                    return ingestTemplate(event, monitor);
                                }
                            } catch (Exception e) {
                                RackConsole.getConsole()
                                        .error(
                                                "Upload of nodegroups failed, please check RACK connection parameters and whether Rack-Box is up and running");
                                this.cancel();
                                return Status.CANCEL_STATUS;
                            }
                        }
                    };
            job.addJobChangeListener(
                    new IJobChangeListener() {
                        @Override
                        public void done(IJobChangeEvent event) {
                            if (event.getResult() == Status.CANCEL_STATUS) {
                                return;
                            }
                            HandlerUtils.loadNodegroups();
                            HandlerUtils.showNodegroupTable();
                            try {
                                IngestionTemplateUtil.buildCsvTemplates();
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void awake(IJobChangeEvent event) {}

                        @Override
                        public void aboutToRun(IJobChangeEvent event) {}

                        @Override
                        public void sleeping(IJobChangeEvent event) {}

                        @Override
                        public void running(IJobChangeEvent event) {}

                        @Override
                        public void scheduled(IJobChangeEvent event) {}
                    });

            job.schedule();

        } catch (Exception e) {
            // RackConsole.getConsole().print(e.getMessage());
            RackConsole.getConsole().error("Error during uploading nodegroups to RACK");
        }
        return null;
    }

    public static ArrayList<String> removeDuplicates(ArrayList<String> list) {
        ArrayList<String> result = new ArrayList<String>();
        for (String entry : list) {
            if (!result.contains(entry)) {
                result.add(entry);
            }
        }
        return result;
    }
}
