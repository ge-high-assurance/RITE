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
package com.ge.research.rite;

import com.ge.research.rite.utils.Core;
import com.ge.research.rite.utils.IngestionTemplateUtil;
import com.ge.research.rite.utils.OntologyUtil;
import com.ge.research.rite.utils.ProjectUtils;
import com.ge.research.rite.utils.RackConsole;
import com.ge.research.rite.views.RackPreferencePage;
import com.ge.research.rite.views.ViewUtils;

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
import org.eclipse.ui.progress.WorkbenchJob;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BuildIngestionNodegroupsHandler extends RiteHandler {

    public static void bookkeepNodegroups() {
        ProjectUtils.refreshProjects();

        ArrayList<String> classList = OntologyUtil.getClassNames();
        if (classList == null) {
            RackConsole.getConsole()
                    .error(
                            "No classes found on RACK, please check connection parameters or upload ontologies to RACK");
            return;
        }
        // Iterate over class names and generate their json nodegroup template
        for (String className : classList) {
            try {

                String ingestionNodegroupId =
                        IngestionTemplateUtil.getIngestionNodegroupId(className);
                String csvTemplate = IngestionTemplateUtil.getNodegroupCSVTemplate(className);
                IngestionTemplateUtil.csvTemplates.put(ingestionNodegroupId, csvTemplate);
            } catch (Exception e) {
                RackConsole.getConsole()
                        .error(
                                "Could not build ingestion nodegroup template for nodegroup: "
                                        + OntologyUtil.getLocalClassName(className));
                return;
            }
        }
    }

    public static IStatus buildNodegroups(IProgressMonitor monitor) {

        if (!ProjectUtils.validateInstanceDataFolder()) {
            return Status.CANCEL_STATUS;
        }

        String ingestionNodegroupPath = RackPreferencePage.getInstanceDataFolder();

        ProjectUtils.createNodegroupFolder(ingestionNodegroupPath);
        ProjectUtils.refreshProjects();

        ArrayList<String> classList = OntologyUtil.getClassNames();

        if (classList == null) {
            RackConsole.getConsole()
                    .error(
                            "No classes found on RACK, please check connection parameters or upload ontologies to RACK");
            return Status.CANCEL_STATUS;
        }

        // Iterate over class names and generate their json nodegroup template
        for (String className : classList) {
            try {
                String ingestionNodegroupId =
                        IngestionTemplateUtil.getIngestionNodegroupId(className);
                RackConsole.getConsole()
                        .print("Generating nodegroup: " + ingestionNodegroupId + ".json ... ");
                String ingestionTemplate =
                        IngestionTemplateUtil.getNodegroupIngestionTemplate(className);
                if (ingestionTemplate == null) {
                    RackConsole.getConsole().printFAIL();
                    RackConsole.getConsole()
                            .error(
                                    "Ingestion template undefined for :"
                                            + OntologyUtil.getLocalClassName(className));
                    continue;
                }

                File file =
                        new File(
                                /* RackPreferencePage.getOverlayOntProj() */
                                RackPreferencePage.getInstanceDataFolder()
                                        + Core.NODEGROUP_DATA_FOLDER
                                        + ingestionNodegroupId
                                        + ".json");
                FileUtils.write(file, ingestionTemplate, Charset.defaultCharset());
                monitor.worked((int) (100 / classList.size()) + 1);
                RackConsole.getConsole().printOK();
            } catch (Exception e) {
                RackConsole.getConsole().printFAIL();
                RackConsole.getConsole()
                        .error(
                                "Could not build ingestion nodegroup template for nodegroup: "
                                        + OntologyUtil.getLocalClassName(className));
                return Status.CANCEL_STATUS;
            }
        }

        RackConsole.getConsole()
                .print(
                        "Nodegroups generated successfully and placed in nodegroups folder within "
                                + RackPreferencePage.getInstanceDataFolder());
        return Status.OK_STATUS;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        /*
         * Job job = new Job("Generating nodegroups in the project nodegroups folder") {
         *
         * @Override protected IStatus run(IProgressMonitor monitor) { // Set total
         * number of work units monitor.beginTask("start task", 100); try { return
         * buildNodegroups(monitor); } catch (Exception e) {
         * RackConsole.getConsole().error("Cannot build nodegroups"); this.cancel();
         * return Status.CANCEL_STATUS; } } };
         *
         */
    	super.execute(event);
        Job job =
                WorkbenchJob.create(
                        "Generating nodegroups in the project nodegroups folder",
                        monitor -> {
                            monitor.beginTask("start task", 100);

                            try {
                                ViewUtils.showProgressView();
                                return buildNodegroups(monitor);
                            } catch (Exception e) {
                                RackConsole.getConsole().error("Cannot build nodegroups");
                                return Status.CANCEL_STATUS;
                            }
                        });

        job.addJobChangeListener(
                new IJobChangeListener() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        if (event.getResult() == Status.CANCEL_STATUS) {
                            return;
                        }
                        HandlerUtils.loadNodegroups();
                        HandlerUtils.showNodegroupTable();
                        RackConsole.getConsole().print("Done");
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

        return null;
    }
}
